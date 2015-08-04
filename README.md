JSON Test Fixture library for Java [![Build Status](https://travis-ci.org/szmeti/json-fixtures.png?branch=master)](https://travis-ci.org/corballis/json-fixtures)

#Documentation for <br/>**JSON Fixtures** library
`JSON Fixtures` is an open-source Java library that enables us, Java developers and testers, to test our applications quickly and easily, using fixtures that we have prepared in JSON format.

The library provides two main features:

- it builds Java objects from JSON fixture files for testing Java applications easily, with the use of only a few annotations;
- it eases unit testing by four handy assertion methods (that are based on converting both the expected and the actual values (the results) to JSON strings).

## How to set up the library
To set up the library in a Java project, copy the following XML node, and paste it between the `<dependencies>...</dependencies>` tags of your `pom.xml` file:

```
<dependency>
	<groupId>ie.corballis</groupId>
	<artifactId>json-fixtures-lib</artifactId>
	<version>1.0.4</version>
</dependency>
```
From this time on, you can use the classes provided by the library.

## **Main feature 1:**<br/>Building Java objects from JSON fixture files
One main feature of the library is that it can detect JSON fixture files on the project's classpath, process their content, and initialize the annotated fields of the test class with them for further testing. This process is almost totally automatic, it requires almost no extra configuration. And as JSON files can be created, edited and reproduced easily, this spares the developers or testers from having to build the object hierarchy of the test data from source code.

### How to prepare the JSON fixture files
If you want a JSON file to be recognized as a possible fixture resource, its name + extension must end in ".fixtures.json" (e.g. "cities.fixtures.json"). Locate your JSON fixture resources on the project's classpath, preferably in folder `src/main/test/resources`.

The library supports both simple bean objects and collections; beans can also contain nested fields.
Example: the below JSONs are all correct, and each one could be wrapped to the Java types following it:

 - A simple bean object (with primitive fields):

```json
{
	"person":{
		"name":"John Doe",
		"age":30
	}
}
```
```java
class Person {
	private String name;
	private int age;
}
```
- A bean object with a nested field:
```json
{
	"dog":{
		"color":"brown",
		"owner":{
			"name":"Alice",
			"age":35
		}
	}
}
```
```java
class Dog {
	private String color;
	private Person owner;
}
```
- A type with a list of strings and a list of bean objects:
```json
{
	"inputData":{
		"words":["word1","word2","word3"],
		"people":[{"name":"x","age":25},
		{"name":"y","age":40}]
	}
}
```
```java
class InputData {
	private List<String> words;
	private List<Person> people;
}
```

### How to use the `@Fixture` annotation
Open your test class. Choose the field(s) that you want to hold fixture data, and annotate them with `@Fixture`. Then specify the JSON fixture that you want to use to populate each field, by passing its JSON name to the annotation as parameter:
```java
public class MyUnitTests {
	@Fixture("person")
	private Person candidate;
	@Fixture("inputData")
	private InputData data;
}
```

> Note: the `@Fixture` annotation may be used only for fields!

An example where the annotated field is a collection itself:
```java
@Fixture("cities")
private List<String> cities;
```
```json
{
	"cities":["New York","Miskolc","Budapest"]
}
```

### **_Feature_**: default fixture name
If you specify no fixture name(s) for the `@Fixture` annotation as parameter(s), the field annotation processor takes the field's name as the "default" fixture name.
Therefore, if you are satisfied with the field name being equal to the fixture name, you needn't write anything after `@Fixture`.

**Example** - the following two solutions are equivalent (and, of course, both of them are correct):
```java
@Fixture("car")
private Car car;
```
```java
@Fixture
private Car car;
```

### **_Feature_**: `@Fixture`-annotated fields of superclasses

The field annotation processor looks for the `@Fixture`-annotated fields not only among the declared fields of the *actual* test class, but also among those of its superclasses.
So if you extend a test class (`B`) from another one (`A`), where `A` declares a `@Fixture`-d field (`AField`),
then -- as long as `B` is able to *access* `AField` -- it can also access its data content.

```java
public class A {
    @Fixture
    protected MyBean AField;
	(...)
}
public class B extends A {
    @Test
    public void testFieldOfSuperclass() {
        assertThat(AField).isNotNull();
        assertThat(AField). (...)
    }
}
```

### **_Feature_**: how to **merge** the contents of separate fixtures into one field

Let's see a type that contains three fields:
```java
public class Car {
	private int age;
	private String color;
	private int id;
}
```
If you only work with 6-year-old black cars in your tests, and it's only the id that has to vary between the instances, you don't need to copy the `age + color` part into every single JSON fixture. In this case you can use a convenient JSON Fixture feature called `merging`.

`Merging` permits that the parts of the data that finally makes up a Java object may be located in different places in the JSON resource. If you specify this right after the `@Fixture` annotation, JSON Fixtures performs the merging. Thus certain parts of the JSON fixture files may be written only once, but reused several times.

As an example, let's declare the `age + color` stem in the fixture only once, and then let's declare more cars with different IDs:
```json
{
	"stem":{
		"age":6,
		"color":"black"
	},
	"car1":{"id":1},
	"car2":{"id":2}
}
```
Now these fixtures may be used in the Java code as follows:
```java
@Fixture({"stem","car1"})
private Car car1;
@Fixture({"stem","car2"})
private Car car2;
```

### How to tell the library to process the annotations
If you want to use the fields that you have previously annotated with `@Fixture`, you have to *initialize* Fixtures. Use the library's access method for it: `FixtureAnnotations.initFixtures()`.
As it's the initialization process that sets the `@Fixture`-d fields, initialization must happen prior to their usage. Therefore -- if you are working with jUnit -- it's worth initializing in a method annotated with `org.junit.Before`, so it gets executed before every unit test, and always resets the field fixture values:

```java
@Before
public void init() throws Exception {
	FixtureAnnotations.initFixtures(this);
}
```
The parameter of the method is a not-null instance of the class that contains the `@Fixture`-d fields. If the initialization is in the same class as these fields, let the parameter value be `this`.


#### **_Additional information_**:<br/>how to set an own object mapper
The way JSON Fixtures reads up the JSON files rests on [Jackson](https://github.com/FasterXML/jackson) library. It uses Jackson's `ObjectMapper` class for this purpose. JSON Fixtures configures its object mapper with only one characteristic:
```java
objectMapper.configure(SerializationFeature.
WRITE_DATES_AS_TIMESTAMPS, false);
```
However, you might want to use your own object mapper with your pre-set custom configuration instead. -- For this, perform the following static method call:
```java
ObjectMapperProvider.setObjectMapper(ownMapper);
```
>Warning: if you choose to use the default object mapper, your bean classes (e.g. class `Car` in the example above) must declare getters, at least for those fields that the fixtures really use!

## **Main feature 2**:<br/>The library's four handy assertion methods
The library's other main feature is four assertion methods.
They are the instance methods of class `FixtureAssert`.

Their working is based on converting both the expected and the actual values (aka the method results) to JSON-formatted strings, comparing these strings and asserting that they match each other. The implementation of the methods notably relies on the aid of [hamcrest](https://github.com/hamcrest/JavaHamcrest).

The methods assert that the actual value matches the fixtures passed as parameters, with the following variations:

 1. `matches(String... fixtures)`:
allows both any array ordering and extra unexpected fields;
 2. `matchesWithStrictOrder(String... fixtures)`:
allows only strict array ordering, but allows extra unexpected fields;
 3. `matchesExactly(String... fixtures)`:
allows any array ordering, but no extra unexpected fields;
 4. `matchesExactlyWithStrictOrder(String... fixtures)`:
allows only strict array ordering and no extra unexpected fields.

#### Example of usage:
```java
import static ie.corballis.fixtures.assertion.FixtureAssert.assertThat;
(...)
public class TestClass {
	@Fixture("expected")
	private List<String> expected;

	@Test
	public void test2() throws JsonProcessingException {
		List<String> actual = myMethodResult();
		assertThat(actual).matchesExactly("expected");
	}
}
```