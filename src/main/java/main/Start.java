package main;

import GetContentRSVPU.GetGroupTeacherClassroom;
import GetContentRSVPU.GetTimeTable;
import beta.RamTest;
import statistic.SaveTimeTable;
import statistic.StatisticMain;

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

    private static JTextArea textLogGroupz = new JTextArea();

    private static JTextArea textLogGroup = new JTextArea();
    private static JTextArea textLogTeacher = new JTextArea();
    private static JTextArea textLogClass = new JTextArea();

    private static JTextArea textLogServer = new JTextArea();
    private static JTextArea textLogError = new JTextArea();


    private static JFrame frame;

    private static String lastConnectionTime = "";

    public static SaveTimeTable saveTimeTable;

    static {
        textLogGroupz.setEditable(false);
        textLogServer.setEditable(false);
        textLogError.setEditable(false);

        textLogGroup.setEditable(false);
        textLogTeacher.setEditable(false);
        textLogClass.setEditable(false);

        textLogGroupz.setFont(textLogGroupz.getFont().deriveFont(15f));
        textLogServer.setFont(textLogGroupz.getFont());
        textLogError.setFont(textLogGroupz.getFont());

        textLogGroup.setFont(textLogGroupz.getFont());
        textLogClass.setFont(textLogGroupz.getFont());
        textLogTeacher.setFont(textLogGroupz.getFont());
    }

    private static void createGIU() {
        frame = new JFrame("Server");
        frame.setSize(1200, 700);
        JPanel mainPanel = new JPanel(new GridLayout(2, 3));

        JScrollPane scrollAll = new JScrollPane(textLogGroupz, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        JScrollPane scrollServer = new JScrollPane(textLogServer, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        JScrollPane scrollError = new JScrollPane(textLogError, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        JScrollPane scrollGroup = new JScrollPane(textLogGroup, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        JScrollPane scrollTeacher = new JScrollPane(textLogTeacher, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        JScrollPane scrollClass = new JScrollPane(textLogClass, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);


        mainPanel.add(scrollError);
        mainPanel.add(scrollServer);
        mainPanel.add(scrollAll);

        mainPanel.add(scrollGroup);
        mainPanel.add(scrollTeacher);
        mainPanel.add(scrollClass);

        frame.add(mainPanel);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); //помещает окно на центр
    }




    static void addTextServer(String args) {
        textLogServer.append("=====" + getTime() + "=====" + "\n" + args + "\n\n");
        textLogServer.setCaretPosition(textLogServer.getDocument().getLength());

    }

    public static void addTextError(String args) {

        textLogError.append(getTime() + "\n" + args + "\n\n");
        textLogError.setCaretPosition(textLogError.getDocument().getLength());

    }

    private static void setTextLastConnection() {
        lastConnectionTime = getTime();
        frame.setTitle("Server (last connection = " + lastConnectionTime + " )" );
    }

    public static void addTextGroupz(String args) {

        textLogGroupz.append(args + "\n\n");
        textLogGroupz.setCaretPosition(textLogGroupz.getDocument().getLength());
    }

    public synchronized static void addTextGroup(String args){
        textLogGroup.append(args+"\n\n");
        textLogGroup.setCaretPosition(textLogGroup.getDocument().getLength());
    }

    public synchronized static void addTextClass(String args){
        textLogClass.append(args+"\n\n");
        textLogClass.setCaretPosition(textLogClass.getDocument().getLength());
    }

    public synchronized static void addTextTeacher(String args){
        textLogTeacher.append(args+"\n\n");
        textLogTeacher.setCaretPosition(textLogTeacher.getDocument().getLength());
    }

    public static void clearLogConnection(){
        textLogServer.setText("\n===== Connection: "+Server.userConnection+" "+ getTime()+" =====\n\n");
    }

    public static void clearLogError(){
        textLogError.setText("\n===== Error "+getTime()+" =====\n\n");
    }

    public static String getTime() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(cal.getTime()) + " " + cal.get(Calendar.DAY_OF_MONTH) + "." + (cal.get(Calendar.MONTH) + 1) + "." + cal.get(Calendar.YEAR);
    }

    public static void main(String[] args) {
        RamTest.printUsingRAM();

        createGIU();

        //start our server for listen requesr
        new Thread(new Server()).start();

        StatisticMain.initGUI();

        new Thread(() -> {

            //цикл для очного отделения
            while (true) {
                setTextLastConnection();
                StatisticMain.clearProgress();
                saveTimeTable = new SaveTimeTable();

                textLogGroupz.setText("===== Group z " + getTime() + "=====\n\n");
                textLogGroup.setText("===== Group "+getTime()+" =====\n\n");
                textLogTeacher.setText("===== Teacher "+getTime()+"=====\n\n");
                textLogClass.setText("===== Classes "+getTime()+" =====\n\n");

                textLogError.append("=====" + getTime() + "=====\n\n");
                textLogServer.append("===== Connection: "+Server.userConnection+" "+ getTime() + "=====\n\n");

                long startConnection = System.currentTimeMillis();

                GetGroupTeacherClassroom get = new GetGroupTeacherClassroom("ochnoe");
                GetGroupTeacherClassroom getz = new GetGroupTeacherClassroom("zaocnoe");

                list_group = get.getList(GetGroupTeacherClassroom.GROUP);
                list_teacher = get.getList(GetGroupTeacherClassroom.TEACHER);
                list_classroom = get.getList(GetGroupTeacherClassroom.CLASSROOM);
                list_group_z = getz.getList(GetGroupTeacherClassroom.GROUP);

                print("---------------------------------------------------");
                print("Teacher count: " + list_teacher.size());
                print("Group och count: " + list_group.size());
                print("Group zaoch count: " + list_group_z.size());
                print("Classroom count: " + list_classroom.size());
                print("connectionTime = " + (System.currentTimeMillis() - startConnection));
                print("---------------------------------------------------\n");

                //сохраянем списки групп после всех коннектов
                saveTimeTable.saveLists();

                //инициализируем потоки
                Thread classThread = new Thread(() -> {
                    GetTimeTable thread;
                    for (Container c : list_classroom) {
                        thread = new GetTimeTable(c, "ochnoe");
                        thread.start();
                    }

                });


                Thread teacherThread = new Thread(() -> {
                    GetTimeTable getTeacher;

                    for (Container c : list_teacher) {
                        getTeacher = new GetTimeTable(c, "ochnoe");
                        getTeacher.start();
                    }
                });

                Thread groupThread = new Thread(() -> {
                    GetTimeTable getGroupThread;
                    for (Container c : list_group) {
                        getGroupThread = new GetTimeTable(c, "ochnoe");
                        getGroupThread.start();
                    }

                });



                Thread groupThreadZ = new Thread(() -> {
                    GetTimeTable getGroupZ;
                    for (Container c : list_group_z) {
                       getGroupZ = new GetTimeTable(c, "zaochnoe");
                        getGroupZ.start();
                    }

                });

                //запускаем потоки
                    groupThread.start();

                    groupThreadZ.start();

                    teacherThread.start();

                    classThread.start();

                //ставим поток на паузу в 60 минут
                try {
                    sleep(1000 * 60 * (int) (60 * 1.0)); // 1 hours
//                    sleep(1000 * 60 * 20); //20 minutes
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    private static void print(Object object) {
        System.out.println(String.valueOf(object));
    }
}
