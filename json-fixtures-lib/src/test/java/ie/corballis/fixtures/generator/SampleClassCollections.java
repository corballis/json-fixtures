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

    public int[] getPrimitiveArrayFieldDefault() {
        return primitiveArrayFieldDefault;
    }

    public void setPrimitiveArrayFieldDefault(int[] primitiveArrayFieldDefault) {
        this.primitiveArrayFieldDefault = primitiveArrayFieldDefault;
    }

    public boolean[] getPrimitiveArrayFieldInitialized() {
        return primitiveArrayFieldInitialized;
    }

    public void setPrimitiveArrayFieldInitialized(boolean[] primitiveArrayFieldInitialized) {
        this.primitiveArrayFieldInitialized = primitiveArrayFieldInitialized;
    }

    public Integer[] getWrapperArrayFieldDefault() {
        return wrapperArrayFieldDefault;
    }

    public void setWrapperArrayFieldDefault(Integer[] wrapperArrayFieldDefault) {
        this.wrapperArrayFieldDefault = wrapperArrayFieldDefault;
    }

    public Short[] getWrapperArrayFieldSpecified() {
        return wrapperArrayFieldSpecified;
    }

    public void setWrapperArrayFieldSpecified(Short[] wrapperArrayFieldSpecified) {
        this.wrapperArrayFieldSpecified = wrapperArrayFieldSpecified;
    }

    public Double[] getWrapperArrayFieldInitialized() {
        return wrapperArrayFieldInitialized;
    }

    public void setWrapperArrayFieldInitialized(Double[] wrapperArrayFieldInitialized) {
        this.wrapperArrayFieldInitialized = wrapperArrayFieldInitialized;
    }

    public String[] getStringArrayFieldDefault() {
        return stringArrayFieldDefault;
    }

    public void setStringArrayFieldDefault(String[] stringArrayFieldDefault) {
        this.stringArrayFieldDefault = stringArrayFieldDefault;
    }

    public String[] getStringArrayFieldSpecified() {
        return stringArrayFieldSpecified;
    }

    public void setStringArrayFieldSpecified(String[] stringArrayFieldSpecified) {
        this.stringArrayFieldSpecified = stringArrayFieldSpecified;
    }

    public String[] getStringArrayFieldInitialized() {
        return stringArrayFieldInitialized;
    }

    public void setStringArrayFieldInitialized(String[] stringArrayFieldInitialized) {
        this.stringArrayFieldInitialized = stringArrayFieldInitialized;
    }

    public Object[] getObjectArrayFieldDefault() {
        return objectArrayFieldDefault;
    }

    public void setObjectArrayFieldDefault(Object[] objectArrayFieldDefault) {
        this.objectArrayFieldDefault = objectArrayFieldDefault;
    }

    public Object[] getObjectArrayFieldSpecified() {
        return objectArrayFieldSpecified;
    }

    public void setObjectArrayFieldSpecified(Object[] objectArrayFieldSpecified) {
        this.objectArrayFieldSpecified = objectArrayFieldSpecified;
    }

    public Date[] getDateArrayFieldDefault() {
        return dateArrayFieldDefault;
    }

    public void setDateArrayFieldDefault(Date[] dateArrayFieldDefault) {
        this.dateArrayFieldDefault = dateArrayFieldDefault;
    }

    public Date[] getDateArrayFieldSpecified() {
        return dateArrayFieldSpecified;
    }

    public void setDateArrayFieldSpecified(Date[] dateArrayFieldSpecified) {
        this.dateArrayFieldSpecified = dateArrayFieldSpecified;
    }

    public Date[] getDateArrayFieldInitialized() {
        return dateArrayFieldInitialized;
    }

    public void setDateArrayFieldInitialized(Date[] dateArrayFieldInitialized) {
        this.dateArrayFieldInitialized = dateArrayFieldInitialized;
    }

    public Collection<Integer> getWrapperCollectionFieldDefault() {
        return wrapperCollectionFieldDefault;
    }

    public void setWrapperCollectionFieldDefault(Collection<Integer> wrapperCollectionFieldDefault) {
        this.wrapperCollectionFieldDefault = wrapperCollectionFieldDefault;
    }

    public Collection<String> getStringCollectionFieldDefault() {
        return stringCollectionFieldDefault;
    }

    public void setStringCollectionFieldDefault(Collection<String> stringCollectionFieldDefault) {
        this.stringCollectionFieldDefault = stringCollectionFieldDefault;
    }

    public Collection<Object> getObjectCollectionFieldDefault() {
        return objectCollectionFieldDefault;
    }

    public void setObjectCollectionFieldDefault(Collection<Object> objectCollectionFieldDefault) {
        this.objectCollectionFieldDefault = objectCollectionFieldDefault;
    }

    public Collection<Date> getDateCollectionFieldDefault() {
        return dateCollectionFieldDefault;
    }

    public void setDateCollectionFieldDefault(Collection<Date> dateCollectionFieldDefault) {
        this.dateCollectionFieldDefault = dateCollectionFieldDefault;
    }

    public Set<Integer> getWrapperCollectionFieldInitialized() {
        return wrapperCollectionFieldInitialized;
    }

    public void setWrapperCollectionFieldInitialized(Set<Integer> wrapperCollectionFieldInitialized) {
        this.wrapperCollectionFieldInitialized = wrapperCollectionFieldInitialized;
    }

    public Collection<String> getStringCollectionFieldInitialized() {
        return stringCollectionFieldInitialized;
    }

    public void setStringCollectionFieldInitialized(Collection<String> stringCollectionFieldInitialized) {
        this.stringCollectionFieldInitialized = stringCollectionFieldInitialized;
    }

    public List<Date> getDateCollectionFieldInitialized() {
        return dateCollectionFieldInitialized;
    }

    public void setDateCollectionFieldInitialized(List<Date> dateCollectionFieldInitialized) {
        this.dateCollectionFieldInitialized = dateCollectionFieldInitialized;
    }

    public ArrayList<String> getArrayListFieldDefault() {
        return arrayListFieldDefault;
    }

    public void setArrayListFieldDefault(ArrayList<String> arrayListFieldDefault) {
        this.arrayListFieldDefault = arrayListFieldDefault;
    }

    public ArrayList<String> getArrayListFieldInitialized() {
        return arrayListFieldInitialized;
    }

    public void setArrayListFieldInitialized(ArrayList<String> arrayListFieldInitialized) {
        this.arrayListFieldInitialized = arrayListFieldInitialized;
    }

    public HashSet<Date> getHashSetFieldDefault() {
        return hashSetFieldDefault;
    }

    public void setHashSetFieldDefault(HashSet<Date> hashSetFieldDefault) {
        this.hashSetFieldDefault = hashSetFieldDefault;
    }

    public HashSet<String> getHashSetFieldInitialized() {
        return hashSetFieldInitialized;
    }

    public void setHashSetFieldInitialized(HashSet<String> hashSetFieldInitialized) {
        this.hashSetFieldInitialized = hashSetFieldInitialized;
    }

    public Map<Integer, String> getWrapperStringMapFieldDefault() {
        return wrapperStringMapFieldDefault;
    }

    public void setWrapperStringMapFieldDefault(Map<Integer, String> wrapperStringMapFieldDefault) {
        this.wrapperStringMapFieldDefault = wrapperStringMapFieldDefault;
    }

    public Map<Object, Date> getObjectDateMapFieldDefault() {
        return objectDateMapFieldDefault;
    }

    public void setObjectDateMapFieldDefault(Map<Object, Date> objectDateMapFieldDefault) {
        this.objectDateMapFieldDefault = objectDateMapFieldDefault;
    }

    public Map<Integer, String> getWrapperStringMapFieldInizialized() {
        return wrapperStringMapFieldInizialized;
    }

    public void setWrapperStringMapFieldInizialized(Map<Integer, String> wrapperStringMapFieldInizialized) {
        this.wrapperStringMapFieldInizialized = wrapperStringMapFieldInizialized;
    }

    public Map<Object, Date> getObjectDateMapFieldInitialized() {
        return objectDateMapFieldInitialized;
    }

    public void setObjectDateMapFieldInitialized(Map<Object, Date> objectDateMapFieldInitialized) {
        this.objectDateMapFieldInitialized = objectDateMapFieldInitialized;
    }

    public SampleEnum getSampleEnumDefault() {
        return sampleEnumDefault;
    }

    public void setSampleEnumDefault(SampleEnum sampleEnumDefault) {
        this.sampleEnumDefault = sampleEnumDefault;
    }

    public SampleEnum getEnumFieldInitialized() {
        return enumFieldInitialized;
    }

    public void setEnumFieldInitialized(SampleEnum enumFieldInitialized) {
        this.enumFieldInitialized = enumFieldInitialized;
    }

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