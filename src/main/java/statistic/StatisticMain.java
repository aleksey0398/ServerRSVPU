package statistic;

import beta.RamTest;
import main.Start;

import javax.swing.*;
import java.awt.*;

public class StatisticMain extends JFrame {

    private static JFrame mainFrame;
    private static JLabel countConnection;
    private static JLabel downloadGroup;
    private static JLabel downloadGroupz;
    private static JLabel downloadTeacher;
    private static JLabel downloadClass;
    private static JLabel uptime;
    private static final String stringDownloadCount = "Count downloaded: ";
    private static JLabel usingRAM;
    private static long timeStart = 0;

    private static final String stringDownloadGroup = "Group downloaded: ";
    private static final String stringDownloadGroupz = "Group z downloaded: ";
    private static final String stringDownloadTeacher = "Teacher downloaded: ";
    private static final String stringDownloadClass = "Class downloaded: ";
    private static JLabel countDownloaded;

    private static int progressDownloadGroup = 0;
    private static int progressDownloadGroupz = 0;
    private static int progressDownloadTeacher = 0;
    private static int progressDownloadClass = 1;

    private static JButton btnClearLogConnection;
    private static JButton btnClearLogError;
    private static JButton btnFreeRAM;

    public static void initGUI(){
        uptime = new JLabel();

        usingRAM = new JLabel(RamTest.getUsingRAM());
        countDownloaded = new JLabel(stringDownloadCount);

        btnClearLogConnection = new JButton("Clear log connection");
        btnClearLogError = new JButton("Clear log error");
        btnFreeRAM = new JButton("Use Grabber Collector");

        btnListener();

        countConnection = new JLabel("Count connection: 0");
        downloadGroup = new JLabel(stringDownloadGroup);
        downloadGroupz = new JLabel(stringDownloadGroupz);
        downloadTeacher = new JLabel(stringDownloadTeacher);
        downloadClass = new JLabel(stringDownloadClass);

        mainFrame = new JFrame("Statistic");
        GridLayout gridLayout = new GridLayout(0,1);

        mainFrame.setLayout(gridLayout);

        mainFrame.add(uptime);
        mainFrame.add(countConnection);
        mainFrame.add(downloadGroup);
        mainFrame.add(downloadGroupz);
        mainFrame.add(downloadTeacher);
        mainFrame.add(downloadClass);
        mainFrame.add(countDownloaded);
        mainFrame.add(usingRAM);
        mainFrame.add(btnClearLogConnection);
        mainFrame.add(btnClearLogError);
        mainFrame.add(btnFreeRAM);

        mainFrame.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        mainFrame.setSize(420,350);
        mainFrame.setVisible(true);

        timeStart = System.currentTimeMillis();

        updateUpTime();
        usingRAM();
    }

    static void btnListener(){
        btnClearLogError.addActionListener(e -> {
            Start.clearLogError();
        });

        btnClearLogConnection.addActionListener(e->{
            Start.clearLogConnection();
        });

        btnFreeRAM.addActionListener( e-> System.gc());
    }

    private static void updateUpTime(){
        new Thread(() -> {
            while (true){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                long time = System.currentTimeMillis() - timeStart;
                uptime.setText("Up time: "+(time/(1000*60*60*24))+":"+(time/(1000*60*60)%24)+":"+(time/(1000*60)%60)+":"+(time/1000%60));
            }
        }).start();
    }

    private static void usingRAM(){
        new Thread(()->{
            while (true){
                usingRAM.setText(RamTest.getUsingRAM());
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void setCountConnection(long count){
        countConnection.setText("Count connection: "+count);
    }

    public static synchronized void setProgressDownloadGroup(){
        downloadGroup.setText(stringDownloadGroup+(++progressDownloadGroup)+" of "+ Start.list_group.size());
        checkComplete();
    }

    public static synchronized void setProgressDownloadGroupz(){
        downloadGroupz.setText(stringDownloadGroupz+(++progressDownloadGroupz)+" of "+Start.list_group_z.size());
        checkComplete();
    }

    public static synchronized void setProgressDownloadTeacher(){
        downloadTeacher.setText(stringDownloadTeacher+(++progressDownloadTeacher)+" of "+Start.list_teacher.size());
        checkComplete();
    }

    public static synchronized void setProgressDownloadClass(){
        downloadClass.setText(stringDownloadClass+(++progressDownloadClass)+" of "+Start.list_classroom.size());
        checkComplete();
    }

    private static void checkComplete(){
        int count = Start.list_group.size()+Start.list_group_z.size()+Start.list_classroom.size()+Start.list_teacher.size();
        int countDownloadedInt = progressDownloadClass+progressDownloadTeacher+progressDownloadGroup+progressDownloadGroupz;

        countDownloaded.setText(stringDownloadCount+countDownloadedInt+" of "+count);
        if(countDownloadedInt%10 == 0)
            System.gc();

        if(count == countDownloadedInt){
//            JOptionPane.showMessageDialog(null,"Download complete");
            Start.saveTimeTable.saveTimeTable();
        }

    }

    public static void clearProgress(){
        progressDownloadGroupz = 0;
        progressDownloadGroup = 0;
        progressDownloadTeacher = 0;
        progressDownloadClass = 1;
    }
}
