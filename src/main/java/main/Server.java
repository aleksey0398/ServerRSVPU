package main;

import GetContentRSVPU.GetResultThread;
import GetContentRSVPU.GetTimeTable;
import com.google.gson.Gson;
import com.sun.net.httpserver.*;
import statistic.StatisticMain;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;


public class Server implements Runnable {
    public static long userConnection = 0;

    public Server() {

    }

    @Override
    public void run() {
        print("main.Server is running...");
        try {
            HttpServer server = HttpServer.create();
            server.bind(new InetSocketAddress(var.releasePort), 10000000);

            HttpContext context = server.createContext("/", new EchoHandler());
            context.setAuthenticator(new Auth());

            server.setExecutor(null);
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
            Start.addTextError("Server\nrun()\n" + e.getMessage());
        }
    }

    class EchoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) {
            if (exchange.getRequestURI().toString().equals("/favicon.ico")) {
                return;
            }

            new Thread(() -> {
                if (!exchange.getRequestURI().toString().equals("/checkURL"))
                    userConnection++;
                StatisticMain.setCountConnection(userConnection);
//                Start.setTextCountConnection(userConnection);
                String textForGUI = "User connection!\n" + exchange.getRequestURI() + "\n";


                print("===============User connection!==============");
                StringBuilder builder = new StringBuilder();

                switch (String.valueOf(exchange.getRequestURI())) {

                    case "/getAllOchnoe":
                        builder.append(new Gson().toJson(Start.list_group)).append("//").
                                append(new Gson().toJson(Start.list_teacher)).append("//")
                                .append(new Gson().toJson(Start.list_classroom));
                        break;

                    case "/getAllZaochnoe":
                        builder.append(new Gson().toJson(Start.list_group_z)).append("//").
                                append(new Gson().toJson(Start.list_teacher)).append("//")
                                .append(new Gson().toJson(Start.list_classroom));
                        break;

                    case "/getCountConnection":
                        builder.append("Count: " + Server.userConnection + "\n");
                        break;

                    case "/checkURL":
                        break;

                    default:
                        if (String.valueOf(exchange.getRequestURI()).equals("") || String.valueOf(exchange.getRequestURI()).equals("/favicon.ico")) {
                            break;
                        }
                /*
                * принимаем запрос типа v_gru=2300&v_date=24.09.2017&type=ochnoe||zaochone
                *                       v_prep=1000&v_date=24.09.2017&type=ochnoe||zaochoe
                *                       v_aud=5000&v_date=24.09.2017&type=ochnoe||zaochnoe
                * */
                        String[] req = String.valueOf(exchange.getRequestURI()).split("&");
                        String type, type_value, date, jsonForSend = null, typeTimeTable;
/* print(req.length); */
                        type = req[0].split("=")[0].substring(1);
                        type_value = req[0].split("=")[1];
                        date = req[1].split("=")[1];
                        typeTimeTable = req[2].split("=")[1];

                        print(type + ":" + type_value + "\n" + date + "\n" + typeTimeTable);
                        switch (type) {
                            case "v_gru":
                                if (typeTimeTable.equals("ochnoe")) {
                                    jsonForSend = GetResultThread.result_group.get(type_value);
                                } else {
                                    jsonForSend = GetResultThread.result_group_z.get(type_value);
                                }
                                break;
                            case "v_prep":
                                jsonForSend = GetResultThread.result_teacher.get(type_value);
                                break;
                            case "v_aud":
                                jsonForSend = GetResultThread.result_class.get(type_value);
                                break;
                        }
                        textForGUI += type_value + "\n";
                        print(type_value + ": " + jsonForSend);
                        if (jsonForSend == null) {
                            jsonForSend = new GetTimeTable().getTimeTable("?" + type + "=" + type_value + "&" + "v_date=" + date, typeTimeTable);
                        }
                        builder.append(jsonForSend);
                        break;


                }
//            builder.append("Count: " + main.Server.userConnection + "\n");
//            builder.append("<h1>URI: ").append(exchange.getRequestURI()).append("</h1>");

//            Headers headers = exchange.getRequestHeaders();
//            for (String header : headers.keySet()) {
//                builder.append("<p>").append(header).append("=")
//                        .append(headers.getFirst(header)).append("</p>");
//            }

                byte[] bytes = new byte[0];
                try {
                    bytes = builder.toString().getBytes("UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                try {
                    exchange.sendResponseHeaders(200, bytes.length);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                OutputStream os = exchange.getResponseBody();

                try {
                    os.write(bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (!exchange.getRequestURI().toString().equals("/checkURL"))
                    Start.addTextServer(textForGUI);
                print("================================");

            }).start();


        }
    }

    static class Auth extends Authenticator {
        @Override
        public Result authenticate(HttpExchange httpExchange) {
            if ("/forbidden".equals(httpExchange.getRequestURI().toString()))
                return new Failure(403);
            else
                return new Success(new HttpPrincipal("c0nst", "realm"));
        }
    }

    private void print(Object object) {
        System.out.println(String.valueOf(object));
    }
}
