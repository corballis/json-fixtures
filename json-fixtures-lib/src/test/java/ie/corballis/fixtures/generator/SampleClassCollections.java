package ie.corballis.fixtures.generator;

import java.util.*;

import static com.google.common.collect.Lists.newArrayList;

public class SampleClassCollections extends SampleClassSimpleTypes {
    private int[] primitiveArrayFieldDefault;
    private boolean[] primitiveArrayFieldInitialized = new boolean[] {true, false, false};

    private Integer[] wrapperArrayFieldDefault;
    private Short[] wrapperArrayFieldSpecified = new Short[5];
    private Double[] wrapperArrayFieldInitialized = new Double[] {-0.23, 5.01, 99999.0, 35.674};

    private String[] stringArrayFieldDefault;
    private String[] stringArrayFieldSpecified = new String[6];
    private String[] stringArrayFieldInitialized = new String[] {"sjdhbh", "hdbhb"};

    private Object[] objectArrayFieldDefault;
    private Object[] objectArrayFieldSpecified = new Object[3];

    private Date[] dateArrayFieldDefault;
    private Date[] dateArrayFieldSpecified = new Date[6];
    private Date[] dateArrayFieldInitialized = new Date[] {new Date(2)};

    private Collection<Integer> wrapperCollectionFieldDefault;
    private Collection<String> stringCollectionFieldDefault;
    private Collection<Object> objectCollectionFieldDefault;
    private Collection<Date> dateCollectionFieldDefault;

    private Set<Integer> wrapperCollectionFieldInitialized = new HashSet<Integer>();
    private Collection<String> stringCollectionFieldInitialized = new ArrayList<String>();
    private List<Date> dateCollectionFieldInitialized = new ArrayList<Date>();

    private ArrayList<String> arrayListFieldDefault;
    private ArrayList<String> arrayListFieldInitialized = newArrayList("jdh", "gvgv", "hh");
    private HashSet<Date> hashSetFieldDefault;
    private HashSet<String> hashSetFieldInitialized = new HashSet<String>();

    private Map<Integer, String> wrapperStringMapFieldDefault;
    private Map<Object, Date> objectDateMapFieldDefault;

    private Map<Integer, String> wrapperStringMapFieldInizialized = new HashMap<Integer, String>();
    private Map<Object, Date> objectDateMapFieldInitialized = new TreeMap<Object, Date>();

    private SampleEnum sampleEnumDefault;
    private SampleEnum enumFieldInitialized = SampleEnum.value2;

    private enum SampleEnum {value1, value2, value3}

    @Override
    public String toString() {
        return super.toString() + '\n' + "SampleClassCollections{" +
               "\nprimitiveArrayFieldDefault=" + Arrays.toString(primitiveArrayFieldDefault) +
               ",\nprimitiveArrayFieldInitialized=" + Arrays.toString(primitiveArrayFieldInitialized) +
               ",\nwrapperArrayFieldDefault=" + Arrays.toString(wrapperArrayFieldDefault) +
               ",\nwrapperArrayFieldSpecified=" + Arrays.toString(wrapperArrayFieldSpecified) +
               ",\nwrapperArrayFieldInitialized=" + Arrays.toString(wrapperArrayFieldInitialized) +
               ",\nstringArrayFieldDefault=" + Arrays.toString(stringArrayFieldDefault) +
               ",\nstringArrayFieldSpecified=" + Arrays.toString(stringArrayFieldSpecified) +
               ",\nstringArrayFieldInitialized=" + Arrays.toString(stringArrayFieldInitialized) +
               ",\nobjectArrayFieldDefault=" + Arrays.toString(objectArrayFieldDefault) +
               ",\nobjectArrayFieldSpecified=" + Arrays.toString(objectArrayFieldSpecified) +
               ",\ndateArrayFieldDefault=" + Arrays.toString(dateArrayFieldDefault) +
               ",\ndateArrayFieldSpecified=" + Arrays.toString(dateArrayFieldSpecified) +
               ",\ndateArrayFieldInitialized=" + Arrays.toString(dateArrayFieldInitialized) +
               ",\nwrapperCollectionFieldDefault=" + wrapperCollectionFieldDefault +
               ",\nstringCollectionFieldDefault=" + stringCollectionFieldDefault +
               ",\nobjectCollectionFieldDefault=" + objectCollectionFieldDefault +
               ",\ndateCollectionFieldDefault=" + dateCollectionFieldDefault +
               ",\nwrapperCollectionFieldInitialized=" + wrapperCollectionFieldInitialized +
               ",\nstringCollectionFieldInitialized=" + stringCollectionFieldInitialized +
               ",\ndateCollectionFieldInitialized=" + dateCollectionFieldInitialized +
               ",\narrayListFieldDefault=" + arrayListFieldDefault +
               ",\narrayListFieldInitialized=" + arrayListFieldInitialized +
               ",\nhashSetFieldDefault=" + hashSetFieldDefault +
               ",\nhashSetFieldInitialized=" + hashSetFieldInitialized +
               ",\nwrapperStringMapFieldDefault=" + wrapperStringMapFieldDefault +
               ",\nobjectDateMapFieldDefault=" + objectDateMapFieldDefault +
               ",\nwrapperStringMapFieldInizialized=" + wrapperStringMapFieldInizialized +
               ",\nobjectDateMapFieldInitialized=" + objectDateMapFieldInitialized +
               ",\nsampleEnumDefault=" + sampleEnumDefault +
               ",\nenumFieldInitialized=" + enumFieldInitialized +
               '}';
    }
}