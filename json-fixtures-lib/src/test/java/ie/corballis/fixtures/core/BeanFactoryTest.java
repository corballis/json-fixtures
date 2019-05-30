package ie.corballis.fixtures.core;

import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.SimpleType;
import ie.corballis.fixtures.io.ClassPathResource;
import ie.corballis.fixtures.io.DefaultFixtureReader;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

public class BeanFactoryTest {

    private BeanFactory factory;

    private String NEW_LINE = System.lineSeparator();

    @Before
    public void setUp() throws Exception {
        factory = new BeanFactory();
        ClassPathResource resource = new ClassPathResource("test2.fixtures.json");
        DefaultFixtureReader reader = new DefaultFixtureReader();
        factory.registerAll(reader.read(resource));
    }

    @Test
    public void createsBeanFromSingleFixture() throws Exception {
        MyBean bean = factory.create(MyBean.class, "fixture1");

        assertThat(bean.getStringProperty()).isEqualTo("property");
        assertThat(bean.getIntProperty()).isEqualTo(1);

        String asString = factory.createAsString("fixture1");
        assertThat(asString).isEqualTo("{\"stringProperty\":\"property\",\"intProperty\":1}");

        String asPrettyString = factory.createAsString(true, "fixture1");
        assertThat(asPrettyString).isEqualTo("{" + NEW_LINE + "  \"stringProperty\": \"property\"," + NEW_LINE +
                                             "  \"intProperty\": 1" + NEW_LINE +
                                             "}");
    }

    @Test(expected = IllegalArgumentException.class)
    public void failsToCreateEmptyBean() throws Exception {
        factory.create(MyBean.class);
    }

    @Test
    public void laterFixturesOverwritePreviousValues() throws Exception {
        MyBean bean = factory.create(MyBean.class, "fixture1", "fixture2");

        assertThat(bean.getStringProperty()).isEqualTo("property2");
        assertThat(bean.getIntProperty()).isEqualTo(1);

        String asString = factory.createAsString("fixture1", "fixture2");
        assertThat(asString).isEqualTo("{\"stringProperty\":\"property2\",\"intProperty\":1}");

        String asPrettyString = factory.createAsString(true, "fixture1", "fixture2");
        assertThat(asPrettyString).isEqualTo("{" + NEW_LINE + "  \"stringProperty\": \"property2\"," + NEW_LINE +
                                             "  \"intProperty\": 1" + NEW_LINE +
                                             "}");
    }

    @Test(expected = IllegalArgumentException.class)
    public void doesNotAllowInvalidPropertiesByDefault() throws Exception {
        factory.create(MyBean.class, "fixture1", "fixture4");
    }

    @Test
    public void ignoresInvalidPropertiesIfAsked() throws Exception {
        factory.setAllowUnknownProperties(true);
        factory.create(MyBean.class, "fixture1", "fixture4");
    }

    @Test
    public void canReadLists() throws Exception {
        MyBean bean = factory.create(MyBean.class, "fixture1", "fixture3");

        assertThat(bean.getStringProperty()).isEqualTo("property");
        assertThat(bean.getIntProperty()).isEqualTo(1);
        assertThat(bean.getListProperty()).containsExactly("element1", "element2", "element3");

        String asString = factory.createAsString("fixture1", "fixture3");
        assertThat(asString).isEqualTo("{\"stringProperty\":\"property\",\"intProperty\":1,\"listProperty\":[\"element1\",\"element2\",\"element3\"]}");

        String asPrettyString = factory.createAsString(true, "fixture1", "fixture3");
        assertThat(asPrettyString).isEqualTo("{" + NEW_LINE + "  \"stringProperty\": \"property\"," + NEW_LINE +
                                             "  \"intProperty\": 1," + NEW_LINE + "  \"listProperty\": [" + NEW_LINE +
                                             "    \"element1\"," + NEW_LINE + "    \"element2\"," + NEW_LINE +
                                             "    \"element3\"" + NEW_LINE + "  ]" +
                                             NEW_LINE +
                                             "}");
    }

    @Test
    public void canReadByJavaType() throws Exception {
        CollectionType collectionType = CollectionType.construct(List.class, SimpleType.construct(MyBean.class));
        List<MyBean> beans = factory.create(collectionType, "fixture6");

        assertThat(beans).hasSize(3);
        assertThat(beans.get(0).getStringProperty()).isEqualTo("property1");
        assertThat(beans.get(0).getIntProperty()).isEqualTo(1);
        assertThat(beans.get(1).getStringProperty()).isEqualTo("property2");
        assertThat(beans.get(1).getIntProperty()).isEqualTo(2);
        assertThat(beans.get(2).getStringProperty()).isEqualTo("property3");
        assertThat(beans.get(2).getIntProperty()).isEqualTo(3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void doesNotAllowEmptyFixtureListIfJavaTypeIsUsed() throws Exception {
        CollectionType collectionType = CollectionType.construct(List.class, SimpleType.construct(MyBean.class));
        factory.create(collectionType);
    }

    @Test
    public void canReadNestedObjects() throws Exception {
        MyBean bean = factory.create(MyBean.class, "fixture3", "fixture5");

        assertThat(bean.getStringProperty()).isNull();
        assertThat(bean.getIntProperty()).isEqualTo(0);
        assertThat(bean.getListProperty()).containsExactly("element1", "element2", "element3");
        assertThat(bean.getNested().getProp1()).isEqualTo("value");

        String asString = factory.createAsString("fixture3", "fixture5");
        assertThat(asString).isEqualTo("{\"listProperty\":[\"element1\",\"element2\",\"element3\"],\"nested\":{\"prop1\":\"value\"}}");

        String asPrettyString = factory.createAsString(true, "fixture3", "fixture5");
        assertThat(asPrettyString).isEqualTo("{" + NEW_LINE + "  \"listProperty\": [" + NEW_LINE + "    \"element1\"," +
                                             NEW_LINE + "    \"element2\"," + NEW_LINE + "    \"element3\"" + NEW_LINE +
                                             "  ]," +
                                             NEW_LINE + "  \"nested\": {" + NEW_LINE + "    \"prop1\": \"value\"" +
                                             NEW_LINE +
                                             "  }" + NEW_LINE +
                                             "}");
    }
}