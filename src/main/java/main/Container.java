package main;

public class Container {
    String value, name, attr;

    public Container(String value, String name, String attr) {
        this.value = value;
        this.name = name;
        this.attr = attr;
    }

    public Container() {
    }

    public String getAttr() {
        return attr;
    }

    public void setAttr(String attr) {
        this.attr = attr;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}