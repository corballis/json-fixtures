package ie.corballis.fixtures.generator;

import java.util.*;

import static com.google.common.collect.Lists.newArrayList;

public class SampleClassCollections extends SampleClassSimpleTypes {
    private int[] primitiveArrayFieldDefault;
    private boolean[] primitiveArrayFieldInitialized = new boolean[] {true, false, false};

    private Integer[] WrapperArrayFieldDefault;
    private Short[] WrapperArrayFieldSpecified = new Short[5];
    private Double[] WrapperArrayFieldInitialized = new Double[] {-0.23, 5.01, 99999.0, 35.674};

    private String[] StringArrayFieldDefault;
    private String[] StringArrayFieldSpecified = new String[6];
    private String[] StringArrayFieldInitialized = new String[] {"sjdhbh", "hdbhb"};

    private Object[] ObjectArrayFieldDefault;
    private Object[] ObjectArrayFieldSpecified = new Object[3];

    private Date[] DateArrayFieldDefault;
    private Date[] DateArrayFieldSpecified = new Date[6];
    private Date[] DateArrayFieldInitialized = new Date[] {new Date()};

    private Collection<Integer> WrapperCollectionFieldDefault;
    private Collection<String> StringCollectionFieldDefault;
    private Collection<Object> ObjectCollectionFieldDefault;
    private Collection<Date> DateCollectionFieldDefault;

    private Set<Integer> WrapperCollectionFieldInitialized = new HashSet<Integer>();
    private Collection<String> StringCollectionFieldInitialized = new ArrayList<String>();
    private List<Date> DateCollectionFieldInitialized = new ArrayList<Date>();

    private ArrayList<String> ArrayListFieldDefault;
    private ArrayList<String> ArrayListFieldInitialized = newArrayList("jdh", "gvgv", "hh");
    private HashSet<Date> HashSetFieldDefault;
    private HashSet<String> HashSetFieldInitialized = new HashSet<String>();

    private Map<Integer, String> WrapperStringMapFieldDefault;
    private Map<Object, Date> ObjectDateMapFieldDefault;

    private Map<Integer, String> WrapperStringMapFieldInizialized = new HashMap<Integer, String>();
    private Map<Object, Date> ObjectDateMapFieldInitialized = new TreeMap<Object, Date>();

    private SampleEnum sampleEnumDefault;
    private SampleEnum enumFieldInitialized = SampleEnum.value2;

    private enum SampleEnum {value1, value2, value3}

    @Override
    public String toString() {
        return super.toString() + '\n' + "SampleClassCollections{" +
               "\nprimitiveArrayFieldDefault=" + Arrays.toString(primitiveArrayFieldDefault) +
               ",\nprimitiveArrayFieldInitialized=" + Arrays.toString(primitiveArrayFieldInitialized) +
               ",\nWrapperArrayFieldDefault=" + Arrays.toString(WrapperArrayFieldDefault) +
               ",\nWrapperArrayFieldSpecified=" + Arrays.toString(WrapperArrayFieldSpecified) +
               ",\nWrapperArrayFieldInitialized=" + Arrays.toString(WrapperArrayFieldInitialized) +
               ",\nStringArrayFieldDefault=" + Arrays.toString(StringArrayFieldDefault) +
               ",\nStringArrayFieldSpecified=" + Arrays.toString(StringArrayFieldSpecified) +
               ",\nStringArrayFieldInitialized=" + Arrays.toString(StringArrayFieldInitialized) +
               ",\nObjectArrayFieldDefault=" + Arrays.toString(ObjectArrayFieldDefault) +
               ",\nObjectArrayFieldSpecified=" + Arrays.toString(ObjectArrayFieldSpecified) +
               ",\nDateArrayFieldDefault=" + Arrays.toString(DateArrayFieldDefault) +
               ",\nDateArrayFieldSpecified=" + Arrays.toString(DateArrayFieldSpecified) +
               ",\nDateArrayFieldInitialized=" + Arrays.toString(DateArrayFieldInitialized) +
               ",\nWrapperCollectionFieldDefault=" + WrapperCollectionFieldDefault +
               ",\nStringCollectionFieldDefault=" + StringCollectionFieldDefault +
               ",\nObjectCollectionFieldDefault=" + ObjectCollectionFieldDefault +
               ",\nDateCollectionFieldDefault=" + DateCollectionFieldDefault +
               ",\nWrapperCollectionFieldInitialized=" + WrapperCollectionFieldInitialized +
               ",\nStringCollectionFieldInitialized=" + StringCollectionFieldInitialized +
               ",\nDateCollectionFieldInitialized=" + DateCollectionFieldInitialized +
               ",\nArrayListFieldDefault=" + ArrayListFieldDefault +
               ",\nArrayListFieldInitialized=" + ArrayListFieldInitialized +
               ",\nHashSetFieldDefault=" + HashSetFieldDefault +
               ",\nHashSetFieldInitialized=" + HashSetFieldInitialized +
               ",\nWrapperStringMapFieldDefault=" + WrapperStringMapFieldDefault +
               ",\nObjectDateMapFieldDefault=" + ObjectDateMapFieldDefault +
               ",\nWrapperStringMapFieldInizialized=" + WrapperStringMapFieldInizialized +
               ",\nObjectDateMapFieldInitialized=" + ObjectDateMapFieldInitialized +
               ",\nsampleEnumDefault=" + sampleEnumDefault +
               ",\nenumFieldInitialized=" + enumFieldInitialized +
               '}';
    }
}