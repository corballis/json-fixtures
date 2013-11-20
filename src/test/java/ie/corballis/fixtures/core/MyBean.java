package ie.corballis.fixtures.core;

import java.util.List;

public class MyBean {
    private String stringProperty;
    private int intProperty;
    private List<String> listProperty;
    private OtherBean nested;

    public String getStringProperty() {
        return stringProperty;
    }

    public void setStringProperty(String stringProperty) {
        this.stringProperty = stringProperty;
    }

    public int getIntProperty() {
        return intProperty;
    }

    public void setIntProperty(int intProperty) {
        this.intProperty = intProperty;
    }

    public List<String> getListProperty() {
        return listProperty;
    }

    public void setListProperty(List<String> listProperty) {
        this.listProperty = listProperty;
    }

    public OtherBean getNested() {
        return nested;
    }

    public void setNested(OtherBean nested) {
        this.nested = nested;
    }
}