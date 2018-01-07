package beta;

import GetContentRSVPU.GetResultThread;
import com.google.gson.Gson;
import main.Start;

import javax.lang.model.element.NestingKind;
import java.io.*;
import java.security.acl.LastOwnerException;
import java.util.Calendar;
import java.util.Scanner;
import java.util.UUID;

public class SaveTimeTable {

    String filePathForTimeTable;
    String filePathForLists;

    private final String separator1 = "|+|+|+|+|+|+|+|+|+|+|+|+|+|+|+|+|+|+|+|+|+|+|+|+|+|+|+|+|+|+|+|+|+|+|+|+|+|+|+|+|+|+|+|+|+|+|";
    private final String separator2 = "+|+|+|+|+|+|+|+|+|+|+|+|+|+|+|+|+|+|+|+|+|+|+|+|+|+|+|+|+|+|+|+|+|+|+|+|+|+|+|+|+|+|+|+|+|+";

    public SaveTimeTable() {
        filePathForTimeTable = generateFilePath(true);
        filePathForLists = generateFilePath(false);
    }

    public static void main(String[] args) {
//        createPath();
        for (int i = 0; i < 10; i++)
            testWrite("test1");

//        boolean stop = false;
       testRead();

    }

    private String generateFilePath(boolean timeTable) {

        Calendar calendar = Calendar.getInstance();
        return "RSVPUServer/" + calendar.get(Calendar.YEAR) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.DAY_OF_MONTH) + "/" + calendar.getTime()+(timeTable?"TimeTable":"List")+ ".txt";

    }

    public void saveLists() {
//        Calendar calendar = Calendar.getInstance();
//        String fileName = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND) + " " + calendar.get(Calendar.DAY_OF_MONTH) + "." + calendar.get(Calendar.MONTH) + "." + calendar.get(Calendar.YEAR);
        File file = new File(this.filePathForLists);
        Log(file.exists());
        Log(file.getAbsolutePath());
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            PrintWriter writer = new PrintWriter(file);
            writer.println(new Gson().toJson(Start.list_group));
            writer.println(this.separator1);
            writer.println(new Gson().toJson(Start.list_teacher));
            writer.println(this.separator1);
            writer.println(new Gson().toJson(Start.list_classroom));
            writer.println(this.separator1);
            writer.println(new Gson().toJson(Start.list_group_z));
            writer.println();
            writer.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void saveTimeTable() {
        Log("Save Time Table");
        File file = new File(this.filePathForTimeTable);
        try {
            PrintWriter writer = new PrintWriter(file);
            writer.println(new Gson().toJson(GetResultThread.result_group));
            writer.println(this.separator2);
            writer.println(new Gson().toJson(GetResultThread.result_teacher));
            writer.println(this.separator2);
            writer.println(new Gson().toJson(GetResultThread.result_class));
            writer.println(this.separator2);
            writer.println(new Gson().toJson(GetResultThread.result_group_z));
            writer.println();
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    static void Log(Object args) {
        System.out.println("SaveTimeTable: " + String.valueOf(args));
    }

    static String separatorTest1 = "©©©©©©©©©©©©©©©©©©©©©©©©©©©©©©©©©©";
    static String separatorTest2 = "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~";

    static void testWrite(String name) {
        Calendar c = Calendar.getInstance();
        File fileTest = new File("TestRSVPU/" + c.get(Calendar.YEAR) + "/" + c.get(Calendar.MONTH) + "/" + name + ".txt");

        if (!fileTest.exists()) {
            boolean mkdir = fileTest.getParentFile().mkdirs();
            System.out.println("mkdirs: "+mkdir);
            try {
                boolean createNewFile = fileTest.createNewFile();
                System.out.println("Create new file: "+ createNewFile);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new FileWriter(fileTest, true));
//            writer = new PrintWriter(fileTest);
        } catch (IOException e) {
            e.printStackTrace();
        }

//        for (int i = 0; i < 10; i++) {
//            writer.println(System.currentTimeMillis());
//        }
//        writer.println(separatorTest1);

        for (int i1 = 0; i1 < 10; i1++) {
            writer.println(UUID.randomUUID().toString());
        }
        writer.println(separatorTest2);
        writer.close();
    }

 static void testRead(){
        File fileToRead = new File("TestRSVPU/2018/0/test1.txt");
        try {

            Scanner scanner = new Scanner(fileToRead);
            StringBuffer inFile = new StringBuffer();
            while (scanner.hasNext()){
                inFile.append(scanner.next()+"\n");
//                Log(scanner.next());
            }
            scanner.close();

            String uids[] = inFile.toString().split(separatorTest2);

            int i1 = 0, i2 = 0;
            for(String uids2: uids){

                if(uids2.equals("\n"))
                    return;

                String[] uids3 = uids2.split("\n");
                i1++;
                Log("\t\t\t\t\t\t\t\t\t\t\t\t"+i1);
                for (String uid: uids3){

                    if(uid.equals(""))
                        continue;
                    i2++;
                    Log(i2+ ".\t"+uid);
                }

            }
//            Log(inFile.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
