package beta;

import java.io.IOException;
import java.net.URL;

public class TestServer {
//    private static String url1 = "http://92.248.221.177:8765/v_gru=2260&v_date=30.10.2017&type=ochnoe";
//    private static String url2 = "http://92.248.221.177:8765/getCountConnection";
//    private static String url3 = "http://92.248.221.177:8765/getAllOchnoe";

    private static String url1 = "http://192.168.1.50:8765/v_gru=2260&v_date=30.10.2017&type=ochnoe";
    private static String url2 = "http://192.168.1.50:8765/getCountConnection";
    private static String url3 = "http://192.168.1.50:8765/getAllOchnoe";

    public static void main(String[] args) {

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    new URL(url1).openStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    new URL(url2).openStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    new URL(url3).openStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }
}
