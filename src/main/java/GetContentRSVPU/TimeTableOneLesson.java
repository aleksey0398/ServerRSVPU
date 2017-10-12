package GetContentRSVPU;

import java.util.ArrayList;
import java.util.List;

public class TimeTableOneLesson {

    public TimeTableOneLesson(){}

    public List<String> lessonsName = new ArrayList<>();
    public List<String> typeOfLesson = new ArrayList<>();
    public List<String> numberOfGroup = new ArrayList<>();
    public String timeStart;
    public List<Tuple> teachers = new ArrayList<>();
    public List<Tuple> classrooms = new ArrayList<>();

    public static class Tuple{
        String name,request;
        public Tuple(String name, String request){
            this.name = name;
            this.request = request;
        }
    }
}
