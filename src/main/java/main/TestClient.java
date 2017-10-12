package main;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class TestClient {
    String url = "http://92.248.221.177:8765/?getAll";
    List<Container> list_teacher;
    List<Container> list_group;
    List<Container> list_class;

    TestClient(){

    }

    void setupConnection(){



        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
//            long startTime = System.currentTimeMillis();
            URL url = new URL(this.url);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();


            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }

            String resultJson = buffer.toString();
            String[] arr = resultJson.split("//");
            Type listOfTestObject = new TypeToken<List<Container>>(){}.getType();
            list_group = new Gson().fromJson(arr[0],listOfTestObject);
            list_teacher = new Gson().fromJson(arr[1],listOfTestObject);
            list_class = new Gson().fromJson(arr[2],listOfTestObject);

//            print("                             Teacher count: " + list_teacher.size());
//            print("                             Group count: " + list_group.size());
//            print("                             Classroom count: " + list_class.size());
//            print("                     Connection Time client: "+(System.currentTimeMillis()-startTime));


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void print(Object object) {
        System.out.println(String.valueOf(object));
    }


}
