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
    private Byte ByteField;
    private Short ShortField = 6;
    private Integer IntegerField = 7;
    private Long LongField;
    private Float FloatField;
    private Double DoubleField;
    private Character CharacterField;
    private Boolean BooleanField;

    private String StringFieldDefault; // empty string
    private String StringFieldInitialized = "xxx";
    private Object ObjectFieldDefault; // default initialization (null)
    private Date DateFieldDefault; // today
    private Date DateFieldInitialized = new GregorianCalendar().getGregorianChange();

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
               ",\nByteField=" + ByteField +
               ",\nShortField=" + ShortField +
               ",\nIntegerField=" + IntegerField +
               ",\nLongField=" + LongField +
               ",\nFloatField=" + FloatField +
               ",\nDoubleField=" + DoubleField +
               ",\nCharacterField=" + CharacterField +
               ",\nBooleanField=" + BooleanField +
               ",\nStringFieldDefault='" + StringFieldDefault + '\'' +
               ",\nStringFieldInitialized='" + StringFieldInitialized + '\'' +
               ",\nObjectFieldDefault=" + ObjectFieldDefault +
               ",\nDateFieldDefault=" + DateFieldDefault +
               ",\nDateFieldInitialized=" + DateFieldInitialized +
               '}';
    }
}