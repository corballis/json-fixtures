package ie.corballis.fixtures.generator;

import java.util.Date;
import java.util.GregorianCalendar;

public class SampleClassSimpleTypes {
    // primitive types -> default initialization
    private byte byteField;
    private short shortField = 5;
    private int intField;
    private long longField;
    private float floatField;
    private double doubleField = -3.12;
    private char charField = 'd';
    private boolean boolField;

    // wrapper types -> the default initialization of the suitable primitive type
    private Byte byteFieldWrapper;
    private Short shortFieldWrapper = 6;
    private Integer integerFieldWrapper = 7;
    private Long longFieldWrapper;
    private Float floatFieldWrapper;
    private Double doubleFieldWrapper;
    private Character characterFieldWrapper;
    private Boolean booleanFieldWrapper;

    private String stringFieldDefault; // empty string
    private String stringFieldInitialized = "xxx";
    private Object objectFieldDefault; // default initialization (null)
    private Date dateFieldDefault; // today
    private Date dateFieldInitialized = new GregorianCalendar().getGregorianChange();

    @Override
    public String toString() {
        return "SampleClassSimpleTypes{" +
               "\nbyteField=" + byteField +
               ",\nshortField=" + shortField +
               ",\nintField=" + intField +
               ",\nlongField=" + longField +
               ",\nfloatField=" + floatField +
               ",\ndoubleField=" + doubleField +
               ",\ncharField=" + charField +
               ",\nboolField=" + boolField +
               ",\nbyteFieldWrapper=" + byteFieldWrapper +
               ",\nshortFieldWrapper=" + shortFieldWrapper +
               ",\nintegerFieldWrapper=" + integerFieldWrapper +
               ",\nlongFieldWrapper=" + longFieldWrapper +
               ",\nfloatFieldWrapper=" + floatFieldWrapper +
               ",\ndoubleFieldWrapper=" + doubleFieldWrapper +
               ",\ncharacterFieldWrapper=" + characterFieldWrapper +
               ",\nbooleanFieldWrapper=" + booleanFieldWrapper +
               ",\nstringFieldDefault='" + stringFieldDefault + '\'' +
               ",\nstringFieldInitialized='" + stringFieldInitialized + '\'' +
               ",\nobjectFieldDefault=" + objectFieldDefault +
               ",\ndateFieldDefault=" + dateFieldDefault +
               ",\ndateFieldInitialized=" + dateFieldInitialized +
               '}';
    }
}