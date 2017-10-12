package GetContentRSVPU;

import main.Container;
import main.Start;
import main.var;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GetGroupTeacherClassroom {
    Document doc;
    public final static int CLASSROOM = 0, GROUP = 1, TEACHER = 2;
    private String type = null;

    public GetGroupTeacherClassroom(String type) {
        try {
//            long startConnection = System.currentTimeMillis();
            this.type = type;
            doc = Jsoup.connect(type.equals("ochnoe") ? var.URLTimeTableOchnoe : var.URLTimeTableZaochnoe).get();
//            print("connectionTime = "+ (System.currentTimeMillis() - startConnection));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Container> getList(int args) {
        Elements nameGroup;
        List<Container> containerList = new ArrayList<>();

        if (this.doc != null) {
            nameGroup = this.doc.select("select").select("option");
            String currentArgs = null;

            switch (args) {
                case GROUP:
                    currentArgs = "gr";
                    break;
                case TEACHER:
                    currentArgs = "prep";
                    break;
                case CLASSROOM:
                    currentArgs = "aud";
            }

//            print("|GetGTC| count element: " + nameGroup.size());
            for (Element element : nameGroup) {

                String value = element.attr("value");
                String attr = element.attr("name");
                String name = element.text();
                if (!value.equals("") && !name.equals("")) {
                    if (attr.equals(currentArgs)) {
                        containerList.add(new Container(value, name, attr));
//                    print(attr + "    " + value + "   " + name);
                    }
                }

            }


        } else {

            switch (args) {
                case GROUP:
                    containerList = type.equals("ochnoe")?Start.list_group:Start.list_group_z;
                    break;
                case TEACHER:
                    containerList = Start.list_teacher;
                    break;
                case CLASSROOM:
                    containerList = Start.list_classroom;
                    break;
            }
        }

        return containerList;
    }

    private void print(Object object) {
        System.out.println(String.valueOf(object));
    }


}
