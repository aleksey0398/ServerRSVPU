package GetContentRSVPU;

import com.google.gson.Gson;
import main.Container;
import main.var;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GetTimeTable implements Runnable {

    //    Map<String, TimeTableObject> mapTime = new HashMap<>();
    private String value;
    private String name;
    private String valueForMap;
    private String attr;
    private String url;
    private String type;

    public GetTimeTable() {

    }

    public GetTimeTable(Container container, String type) {
        switch (container.getAttr()) {
            case "gr":
                this.value = "?v_gru=" + container.getValue();
                break;
            case "prep":
                this.value = "?v_prep=" + container.getValue();
                break;
            case "aud":
                this.value = "?v_aud=" + container.getValue();
                break;
        }
        attr = container.getAttr();
        name = container.getName();
        url = type.equals("ochnoe") ? var.URLTimeTableOchnoe : var.URLTimeTableZaochnoe;
        this.type = type;
        this.valueForMap = container.getValue();
//        print(valueForMap);
    }

    @Override
    public void run() {
        try {
//            print("Connection URL: " + this.url + this.value);
            Document doc = Jsoup.connect(this.url + this.value).timeout(10 * 6000).ignoreHttpErrors(true).get();
            String jsonTimeTable = new Gson().toJson(parseHTML(doc));

//            GetResultThread.result.put(this.valueForMap, new Gson().toJson(timeTableList));


            switch (attr) {

                case "gr":
//                    print("case v_gru");
                    if (type.equals("ochnoe")) {
                        GetResultThread.result_group.put(this.valueForMap, jsonTimeTable);
                    } else {
                        GetResultThread.result_group_z.put(this.valueForMap, jsonTimeTable);
                    }
                    break;
                case "prep":
//                    print("case v_prep");
                    GetResultThread.result_teacher.put(this.valueForMap, jsonTimeTable);

                    break;
                case "aud":
//                    print("case v_aud");
                    GetResultThread.result_class.put(this.valueForMap, jsonTimeTable);

                    break;
            }
            print(this.name + ": complete");

            Thread.currentThread().interrupt();

        } catch (
                IOException e)

        {
            e.printStackTrace();
            System.err.println(this.name + ": error");
            Thread.currentThread().interrupt();
        }

    }

    public String getTimeTable(String args, String type) {
        String jsonForResult = null;
        try {
            print("getTimeTable URL = " + (type.equals("ochnoe") ? var.URLTimeTableOchnoe : var.URLTimeTableZaochnoe) + args);
            Document doc = Jsoup.connect((type.equals("ochnoe") ? var.URLTimeTableOchnoe : var.URLTimeTableZaochnoe) + args).timeout(1000 * 60 * 3).ignoreHttpErrors(true).get();
            jsonForResult = new Gson().toJson(parseHTML(doc));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return jsonForResult;
    }

    List<TimeTableOneDay> parseHTML(Document doc) {

        List<TimeTableOneDay> timeTable = new ArrayList<>();

        for (Element day : doc.select("td[class='disciplina ']")) {
            timeTable.add(getOneDayFromElements(day));
//            System.out.println(day.text());
        }
//        System.out.println("\n\n\n");
        int i = 0;
        for (Element dayOfTheWeek : doc.select("table[class='tametable_ofo']").select("tr[class='day']")) {
//            System.out.println(i + ":" + dayOfTheWeek.text());
            timeTable.get(i + i).dayOfTheWeek = dayOfTheWeek.text();
            timeTable.get(i + i + 1).dayOfTheWeek = dayOfTheWeek.text();
            i++;
        }

        //sort array
        timeTable = sortArray(timeTable);
//        System.out.println("\n\n");

        i = 0;
        for (Element date : doc.select("td[class='left']")) {
//            System.out.println(date.text());
            timeTable.get(i).date = date.text();
            timeTable.get(i).colorOfTheWeek = date.select("p").attr("style").substring(6);


            i++;
        }

//        System.out.println("\n\n");

        i = 0;
        for (Element date : doc.select("td[class='right']")) {
//            System.out.println(date.text());
            timeTable.get(7+i).colorOfTheWeek = date.select("p").attr("style").substring(6);
            timeTable.get(7+i).date = date.text();
            i++;
        }


        return timeTable;
    }

    private TimeTableOneDay getOneDayFromElements(Element day) {
        final TimeTableOneDay timeTableDay = new TimeTableOneDay();
//        System.out.println("\n\n");

        int count = 0;
        for (Element lesson : day.select("td[class='disciplina_info']")) {

//            System.out.println("++++++++++++++++++++++++++++++++++ ");

            timeTableDay.lessons[count] = getOneLesson(lesson);
            count++;
        }

        count = 0;
        for (Element time : day.select("td[class='disciplina_time']")) {
//            System.out.println(time.text().substring(3));
            timeTableDay.lessons[count].timeStart = time.text().substring(3);
            count++;
        }

        return timeTableDay;

    }

    private TimeTableOneLesson getOneLesson(Element oneDay) {
        TimeTableOneLesson oneLesson = new TimeTableOneLesson();

//        System.out.println("\n\t\t===|GetOneLesson|===\n");
        for (Element lessons : oneDay.select("p")) {

            int count = 0;
            for (Element request : lessons.select("nobr")) {
                if (count == 2) {
                    break;
                }

                switch (count) {
                    case 0:
                        oneLesson.classrooms.add(new TimeTableOneLesson.Tuple(request.text(), request.select("a").attr("href")));
                        break;
                    case 1:
                        oneLesson.teachers.add(new TimeTableOneLesson.Tuple(request.text(), request.select("a").attr("href")));
                        break;
                }

                count++;
//                System.out.println("\t\t\t\t" + request.text() + " | " + request.select("a").attr("href"));
            }
            oneLesson.lessonsName.add(getLessons(lessons.text()));
            oneLesson.typeOfLesson.add(getLessonsType(lessons.text()));
            oneLesson.numberOfGroup.add(getNumberOfGroup(lessons.text()));
//            System.out.println("\t\t\t\t--------------------------");

        }
//        System.out.println("\t\t==================\n");
        return oneLesson;
    }

    private String getNumberOfGroup(String text) {
//        System.out.println("\t\t\t\t"+text.charAt(text.length()-1));
        if (text.charAt(text.length() - 1) == ')') {

            String number = text.substring(text.length() - 6, text.length() - 1);
//            System.out.println("\t\t\t\t" + number);
            return number;
        } else {
//            System.out.println("\t\t\t\t" + null);
        }
        return null;
    }

     String getLessons(String lesson) {
        String lessonClear = lesson;
//        System.out.println(lessonClear);
        int index = lesson.indexOf('(');
//        System.out.print(index+": ");
        if (index != -1) {
            lessonClear = lesson.substring(0, index);
        }
//        System.out.println("\t\t\t\t" + lessonClear);
        return lessonClear;
    }

     String getLessonsType(String lesson) {
        int start = lesson.indexOf('(');
        int end = lesson.indexOf(')');

        if (start != -1) {
            String string = lesson.substring(start + 1, end);
//            System.out.println("\t\t\t\t" + string);
            return string;
        } else {
//            System.out.println("\t\t\t\t" + null);
            return null;
        }
    }

    private List<TimeTableOneDay> sortArray(List<TimeTableOneDay> ttOneDay) {
        List<TimeTableOneDay> timeTableOneDays = new ArrayList<>();
        for (int i = 0; i < ttOneDay.size(); i++) {
            if (i % 2 == 0) {
                timeTableOneDays.add(ttOneDay.get(i));
            }
        }
        for (int i = 0; i < ttOneDay.size(); i++) {
            if (i % 2 != 0) {
                timeTableOneDays.add(ttOneDay.get(i));
            }
        }

        return timeTableOneDays;
    }

    void print(Object object) {
        System.out.println(String.valueOf(object));
    }
}
