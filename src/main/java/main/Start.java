package main;

import GetContentRSVPU.GetGroupTeacherClassroom;
import GetContentRSVPU.GetTimeTable;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static java.lang.Thread.sleep;

public class Start extends JFrame {

    public static List<Container> list_group = new ArrayList<>();
    public static List<Container> list_teacher = new ArrayList<>();
    public static List<Container> list_classroom = new ArrayList<>();

    public static List<Container> list_group_z = new ArrayList<>();

//    private static JList listGroup = new JList();
//    private static JList listPrep = new JList();
//    private static JList listClass = new JList();

    private static JTextArea textLogGroup = new JTextArea();
    private static JTextArea textLogPrep = new JTextArea();
    private static JTextArea textLogClass = new JTextArea();

    private static JTextArea textLogServer = new JTextArea();
    private static JTextArea textLogError = new JTextArea();
//    private static JLabel textLastConnection= new JLabel();
//    private static JLabel textCountConnection= new JLabel();

    private static JFrame frame;

    private static String lastConnectionTime = "";

    static {
        textLogGroup.setEditable(false);
        textLogClass.setEditable(false);
        textLogServer.setEditable(false);
        textLogError.setEditable(false);
        textLogPrep.setEditable(false);

        textLogGroup.setFont(textLogGroup.getFont().deriveFont(10f));
        textLogServer.setFont(textLogGroup.getFont());
        textLogPrep.setFont(textLogGroup.getFont());
        textLogClass.setFont(textLogGroup.getFont());
        textLogError.setFont(textLogGroup.getFont());
    }

    Start() {
//        setTitle("Server");
//        setLayout(new FlowLayout());
//        setVisible(true);
//        setSize(300, 300);
//        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//        setLocationRelativeTo(null); //помещает окно на центр

    }

    private static void createGIU() {
        frame = new JFrame("Server");
        frame.setSize(1200, 400);
        JPanel mainPanel = new JPanel(new GridLayout(0,5));
//        mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));
//        mainPanel.add(listGroup);
//        mainPanel.add(listClass);
//        mainPanel.add(listPrep);

        JScrollPane scrollGroup = new JScrollPane(textLogGroup, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        JScrollPane scrollPrep = new JScrollPane(textLogPrep, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        JScrollPane scrollClass = new JScrollPane(textLogClass, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        JScrollPane scrollServer = new JScrollPane(textLogServer, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        JScrollPane scrollError = new JScrollPane(textLogError, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);


        mainPanel.add(scrollError);
        mainPanel.add(scrollServer);
        mainPanel.add(scrollClass);
        mainPanel.add(scrollPrep);
        mainPanel.add(scrollGroup);

//        mainPanel.add(textLastConnection);
//        mainPanel.add(textCountConnection);

        frame.add(mainPanel);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); //помещает окно на центр
    }

    public static void addTextGroup(String args){

        textLogGroup.append(args+"\n\n");
        textLogGroup.setCaretPosition(textLogGroup.getDocument().getLength());
    }
    public static void addTextPrep(String args){
        textLogPrep.append(args+"\n\n");
        textLogPrep.setCaretPosition(textLogPrep.getDocument().getLength());

    }
    public static void addTextClass(String args){
        textLogClass.append(args+"\n\n");
        textLogClass.setCaretPosition(textLogClass.getDocument().getLength());

    }

    static void addTextServer(String args){
        textLogServer.append(args+"\n\n");
        textLogServer.setCaretPosition(textLogServer.getDocument().getLength());

    }
    public static void addTextError(String args){

        textLogError.append(getTime()+"\n"+args+"\n\n");
        textLogError.setCaretPosition(textLogError.getDocument().getLength());

    }

    static void setTextLastConnection(){
        lastConnectionTime = getTime();
        frame.setTitle("Server (last connection = "+lastConnectionTime+" ) (count connection = "+Server.userConnection+")");
    }

    static void setTextCountConnection(long count){

     frame.setTitle("Server (last connection = "+ lastConnectionTime +" ) count connection("+count+")");
//        textCountConnection.setText(String.valueOf(count));
    }

    static String getTime(){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(cal.getTime()) +" "+ cal.get(Calendar.DAY_OF_MONTH)+"."+ (cal.get(Calendar.MONTH)+1)+"."+cal.get(Calendar.YEAR);
    }

    public static void main(String[] args) {
        createGIU();
        //start our server for listen requesr
        new Thread(new Server()).start();

        new Thread(() -> {

            //цикл для очного отделения
            while (true) {
                setTextLastConnection();

                textLogPrep.setText("==="+getTime()+"===\n");
                textLogClass.setText("==="+getTime()+"===\n");
                textLogGroup.setText("==="+getTime()+"===\n");
                textLogError.append("==="+getTime()+"===\n");
                textLogServer.setText("==="+getTime()+"====\n");

                long startConnection = System.currentTimeMillis();
                GetGroupTeacherClassroom get = new GetGroupTeacherClassroom("ochnoe");

                list_group = get.getList(GetGroupTeacherClassroom.GROUP);
                list_teacher = get.getList(GetGroupTeacherClassroom.TEACHER);
                list_classroom = get.getList(GetGroupTeacherClassroom.CLASSROOM);
                print("---------------------------------------------------");
                print("Teacher count: " + list_teacher.size());
                print("Group count: " + list_group.size());
                print("Classroom count: " + list_classroom.size());
                print("connectionTime = " + (System.currentTimeMillis() - startConnection));
                print("---------------------------------------------------\n");


                Thread classThread = new Thread(() -> {
                    for (Container c : list_classroom) {
                        new Thread(new GetTimeTable(c, "ochnoe")).start();
                        try {
                            sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                });

                Thread teacherThread = new Thread(() -> {
                    for (Container c : list_teacher) {
                        new Thread(new GetTimeTable(c, "ochnoe")).start();
                        try {
                            sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    classThread.start();
                });

                Thread groupThread = new Thread(() -> {
                    for (Container c : list_group) {
                        new Thread(new GetTimeTable(c, "ochnoe")).start();
                        try {
                            sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    teacherThread.start();
                });


                groupThread.start();

//                    new Thread(new GetTimeTable(list_group.get(1))).start();

                try {
                    sleep(1000 * 60 * (int) (60 * 1.0));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        //цикл для заочного отделения

        new Thread(() -> {

            while (true) {
                long startConnection = System.currentTimeMillis();
                GetGroupTeacherClassroom get = new GetGroupTeacherClassroom("zaocnoe");

                list_group_z = get.getList(GetGroupTeacherClassroom.GROUP);

                print("---------------------------------------------------");
                print("Group count: " + list_group_z.size());
                print("connectionTime zaocnoe = " + (System.currentTimeMillis() - startConnection));
                print("---------------------------------------------------\n");


                Thread groupThreadZ = new Thread(() -> {
                    for (Container c : list_group_z) {
                        new Thread(new GetTimeTable(c, "zaochnoe")).start();
                        try {
                            sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });


                groupThreadZ.start();

//                    new Thread(new GetTimeTable(list_group.get(1))).start();

                try {
                    sleep(1000 * 60 * (int) (60 * 1.0));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();


//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    while (true){
//                    sleep(500);
//                    new TestClient().setupConnection();
//                    }
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//
//        }).start();


    }

    private static void print(Object object) {
        System.out.println(String.valueOf(object));
    }
}
