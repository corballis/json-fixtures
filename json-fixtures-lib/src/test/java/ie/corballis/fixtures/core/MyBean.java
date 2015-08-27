package ie.corballis.fixtures.core;

import java.util.List;

public class MyBean {
    private String stringProperty;
    private int intProperty;
    private List<String> listProperty;
    private OtherBean nested;

    public MyBean(String stringProperty, int intProperty) {
        this.stringProperty = stringProperty;
        this.intProperty = intProperty;
    }

    public MyBean() {
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MyBean myBean = (MyBean) o;
        if (intProperty != myBean.intProperty) {
            return false;
        }
        return !(stringProperty != null ? !stringProperty.equals(myBean.stringProperty) :
                 myBean.stringProperty != null);
    }

    @Override
    public int hashCode() {
        int result = stringProperty != null ? stringProperty.hashCode() : 0;
        result = 31 * result + intProperty;
        return result;
    }
}