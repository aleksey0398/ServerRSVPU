package GetContentRSVPU;

import com.google.gson.Gson;
import main.Container;
import main.Start;
import main.var;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import statistic.StatisticMain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GetTimeTable extends Thread {

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
            if (name.equals("Спортивный зал"))
                return;
//            print("Connection URL: " + this.url + this.value);
            Document doc = Jsoup.connect(this.url + this.value).timeout(1000 * 60 * 30).ignoreHttpErrors(true).get();
            List<TimeTableOneDay> ttOneDay = parseHTML(doc);
            String jsonTimeTable = new Gson().toJson(ttOneDay);

//            GetResultThread.result.put(this.valueForMap, new Gson().toJson(timeTableList));


            switch (attr) {

                case "gr":
//                    print("case v_gru");
                    if (type.equals("ochnoe")) {
                        GetResultThread.result_group.put(this.valueForMap, jsonTimeTable);
                        Start.addTextGroup(this.name + ": complete");
                        StatisticMain.setProgressDownloadGroup();
                    } else {
                        GetResultThread.result_group_z.put(this.valueForMap, jsonTimeTable);
                        Start.addTextGroupz(this.name + ": complete");
                        StatisticMain.setProgressDownloadGroupz();
                    }


                    break;
                case "prep":
//                    print("case v_prep");
                    GetResultThread.result_teacher.put(this.valueForMap, jsonTimeTable);
//                    Start.addTextGroupz(this.name + ": complete");
                    Start.addTextTeacher(this.name + ": complete");
                    StatisticMain.setProgressDownloadTeacher();
                    break;
                case "aud":
//                    print("case v_aud");
                    GetResultThread.result_class.put(this.valueForMap, jsonTimeTable);
//                    Start.addTextGroupz(this.name + ": complete");
                    Start.addTextClass(this.name + ": complete");
                    StatisticMain.setProgressDownloadClass();
                    break;
            }
            print(this.name + ": complete"+"\n\t\t\t\t"+ttOneDay.size());

//            Thread.currentThread().interrupt();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println(this.name + ": error");
//            Start.addTextGroupz(this.name + ": ERROR");
            Start.addTextError(this.name + ": " + e.getMessage());
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            Start.addTextError("GetTimeTableBeta\nrun()\n" + name + "\n" + e.toString());
            Thread.currentThread().interrupt();
            e.printStackTrace();
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
        } catch (Exception e) {
            e.printStackTrace();
            Start.addTextError("GetTimeTableBeta\n" + "getTimeTable()\n" + e.getMessage());
        }

        return jsonForResult;
    }

    private List<TimeTableOneDay> parseHTML(Document doc) {

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
            timeTable.get(7 + i).colorOfTheWeek = date.select("p").attr("style").substring(6);
            timeTable.get(7 + i).date = date.text();
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

            //            System.out.println("\t\t\t\t" + number);
            return text.substring(text.length() - 6, text.length() - 1);
        }
//        else {
//            System.out.println("\t\t\t\t" + null);
//        }
        return null;
    }

    private String getLessons(String lesson) {
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

    private String getLessonsType(String lesson) {
        int start = lesson.indexOf('(');
        int end = lesson.indexOf(')');

        if (start != -1) {
            //            System.out.println("\t\t\t\t" + string);
            return lesson.substring(start + 1, end);
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

    private void print(Object object) {
        System.out.println(String.valueOf(object));
    }
}
