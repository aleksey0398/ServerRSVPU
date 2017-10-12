package beta;

import GetContentRSVPU.TimeTableOneDay;
import GetContentRSVPU.TimeTableOneLesson;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created october 11.10.2017
 * version 1.0
 */
public class GetTimeTable {

    private static Document doc;
    static List<TimeTableOneDay> timeTable = new ArrayList<>();
    static final String backgroundColorToday = "#fcfbeb";

    public static void main(String[] args) {

        boolean connection;

        String URL = "http://www.rsvpu.ru/raspisanie-zanyatij-ochnoe-otdelenie/?v_gru=2260";
        try {
            doc = getConection(URL);
            connection = true;
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error connection with" + URL);
            connection = false;
        }

        if (connection) {
            for (Element day : doc.select("td[class='disciplina ']")) {
                timeTable.add(getOneDayFromElements(day));
                System.out.println(day.text());
            }
            System.out.println("\n\n\n");
            int i = 0;
            for (Element dayOfTheWeek : doc.select("table[class='tametable_ofo']").select("tr[class='day']")) {
                System.out.println(i + ":" + dayOfTheWeek.text());
                timeTable.get(i + i).dayOfTheWeek = dayOfTheWeek.text();
                timeTable.get(i + i + 1).dayOfTheWeek = dayOfTheWeek.text();
                i++;
            }

            //sort array
            timeTable = sortArray(timeTable);
            System.out.println("\n\n");

            i = 0;
            for (Element date : doc.select("td[class='left']")) {
                System.out.println(date.text());
                timeTable.get(i).date = date.text();
                timeTable.get(i).colorOfTheWeek = date.select("p").attr("style").substring(6);


                i++;
            }

            System.out.println("\n\n");

            i = 0;
            for (Element date : doc.select("td[class='right']")) {
                System.out.println(date.text());
                timeTable.get(7+i).colorOfTheWeek = date.select("p").attr("style").substring(6);
                timeTable.get(7+i).date = date.text();
                i++;
            }





            System.out.println("\n" + timeTable.size());
        }

    }

    private static TimeTableOneDay getOneDayFromElements(Element day) {
        final TimeTableOneDay timeTableDay = new TimeTableOneDay();
        System.out.println("\n\n");

        int count = 0;
        for (Element lesson : day.select("td[class='disciplina_info']")) {

            System.out.println("++++++++++++++++++++++++++++++++++ ");

            timeTableDay.lessons[count] = getOneLesson(lesson);
            count++;
        }

        count = 0;
        for (Element time : day.select("td[class='disciplina_time']")) {
            System.out.println(time.text().substring(3));
            timeTableDay.lessons[count].timeStart = time.text().substring(3);
            count++;
        }

        return timeTableDay;

    }

    private static TimeTableOneLesson getOneLesson(Element oneDay) {
        TimeTableOneLesson oneLesson = new TimeTableOneLesson();

        System.out.println("\n\t\t===|GetOneLesson|===\n");
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
                System.out.println("\t\t\t\t" + request.text() + " | " + request.select("a").attr("href"));
            }
            oneLesson.lessonsName.add(getLessons(lessons.text()));
            oneLesson.typeOfLesson.add(getLessonsType(lessons.text()));
            oneLesson.numberOfGroup.add(getNumberOfGroup(lessons.text()));
            System.out.println("\t\t\t\t--------------------------");

        }
        System.out.println("\t\t==================\n");
        return oneLesson;
    }

    private static String getNumberOfGroup(String text) {
//        System.out.println("\t\t\t\t"+text.charAt(text.length()-1));
        if (text.charAt(text.length() - 1) == ')') {

            String number = text.substring(text.length() - 6, text.length() - 1);
            System.out.println("\t\t\t\t" + number);
            return number;
        } else {
            System.out.println("\t\t\t\t" + null);
        }
        return null;
    }

    static String getLessons(String lesson) {
        String lessonClear = lesson;
//        System.out.println(lessonClear);
        int index = lesson.indexOf('(');
//        System.out.print(index+": ");
        if (index != -1) {
            lessonClear = lesson.substring(0, index);
        }
        System.out.println("\t\t\t\t" + lessonClear);
        return lessonClear;
    }

    static String getLessonsType(String lesson) {
        int start = lesson.indexOf('(');
        int end = lesson.indexOf(')');

        if (start != -1) {
            String string = lesson.substring(start + 1, end);
            System.out.println("\t\t\t\t" + string);
            return string;
        } else {
            System.out.println("\t\t\t\t" + null);
            return null;
        }
    }

    private static List<TimeTableOneDay> sortArray(List<TimeTableOneDay> ttOneDay) {
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

    private static Document getConection(String url) throws IOException {
        Document document = Jsoup.connect(url).get();

        return document;
    }

}
