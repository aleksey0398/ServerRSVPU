package main;

import GetContentRSVPU.GetGroupTeacherClassroom;
import GetContentRSVPU.GetTimeTable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

public class Start extends JFrame {

    public static List<Container> list_group = new ArrayList<>();
    public static List<Container> list_teacher = new ArrayList<>();
    public static List<Container> list_classroom = new ArrayList<>();

    public static List<Container> list_group_z = new ArrayList<>();


    Start() {
        setTitle("Server");
        setLayout(new FlowLayout());
        setVisible(true);
        setSize(300, 300);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); //помещает окно на центр

    }

    public static void main(String[] args) {
        new Start();

        //start our server for listen requesr
        new Thread(new Server()).start();

        new Thread(() -> {

            //цикл для очного отделения
            while (true) {
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
                        new Thread(new GetTimeTable(c,"ochnoe")).start();
                        try {
                            sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                });

                Thread teacherThread = new Thread(() -> {
                    for (Container c : list_teacher) {
                        new Thread(new GetTimeTable(c,"ochnoe")).start();
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
                        new Thread(new GetTimeTable(c,"ochnoe")).start();
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
                    sleep(1000 * 60 * (int)(60*1.0));
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
                        new Thread(new GetTimeTable(c,"zaochnoe")).start();
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
                    sleep(1000 * 60 * (int)(60*1.0));
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
