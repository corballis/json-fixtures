JSON Test Fixture library for Java [![Build Status](https://travis-ci.org/szmeti/json-fixtures.png?branch=master)](https://travis-ci.org/corballis/json-fixtures)

#Documentation for <br/>**JSON Fixtures** library
`JSON Fixtures` is an open-source Java library that enables us, Java developers and testers, to test our applications quickly and easily, using fixtures that we have prepared in JSON format.

>**Fixture** (according to [Wikipedia](https://en.wikipedia.org/wiki/Test_fixture#Software)):
"In software testing, a test fixture is a fixed state of the software under test used as a baseline for running tests."

>**JSON fixture**: a set of data for testing some code, given in JSON format.

The library provides three main features:

- it builds Java bean objects from JSON fixture files for testing Java applications easily, with the use of only a few annotations;
- it generates fixture files with default values based on the skeleton of a bean, to enable us to load it as a fixture next time;
- it eases unit testing with four handy assertion methods (that are based on converting both the expected and the actual values (the results) to JSON strings).

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

### How you should prepare the JSON fixture files
If you want a JSON file to be recognized as a possible fixture resource, its name + extension must end in ".fixtures.json" (e.g. "cities.fixtures.json"). Locate your JSON fixture resources on the project's classpath, preferably in folder `src/main/test/resources`.

The library supports both simple bean objects and collections; beans can also contain nested fields.
Example: the below JSONs are all correct, and each one could be wrapped to the Java type following it:

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

If we (having previously generated `toString()` for class `Car`, and initialized *Fixtures*, see [below](https://github.com/corballis/json-fixtures#how-to-tell-the-library-to-process-the-annotations))
now print the `car1`, `car2` objects, we should receive the following output:
```java
Car{age=6, color='black', id=1}
Car{age=6, color='black', id=2}
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
The way JSON Fixtures reads up the JSON files rests on [Jackson](https://github.com/FasterXML/jackson) library. It uses Jackson's `ObjectMapper` class for this purpose. JSON Fixtures configures its object mapper with only two basic characteristics:
```java
objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
```

The first configuration makes the object mapper write dates as formatted strings (e.g. `2015-08-07T09:35:07.903+0000`) instead of the default long values.

The second one means that the object mapper should access every field, let its visibility modifier be anything.

This gives you two important advantages:
 - You don't definitely need to declare getters/setters for the fields you use from the fixtures - the object mapper will still access them, even if they are private/protected.
 - Let's say you have a bean that inherits some fields from its superclass. You can write inherited fields (just as not inherited ones) into the fixture, as well, - the object mapper will fill *all existing* fields of the subclass object with the specified values properly.
 
**Example**: let's say we have two beans - `A` and `B` -, where `B` extends `A`.
Let both beans declare a protected field (`a` and `b`).
Let B declare a `toString()`, which prints both its inherited and not inherited field.
```java
public class A {
	protected int a;
}
public class B extends A {
	protected int b;
	@Override
	public String toString(){
		return "a = " + a + ", b = " + b;
	}
}
```
Now let's prepare a JSON fixture that describes a B instance, and contains both the `a` and `b` values:
```json
{
	"myBInstance":{
		"a":5,
		"b":6
	}
}
```
Loading this fixture into a Java object, invoking its `toString()` should result the following output:
```java
a = 5, b = 6
```
So thanks to this setting, also the inherited fields can be used properly from the fixtures.

**However**, you might want to use your **own** object mapper with your pre-set custom configuration instead. - For this, perform the following static method call:
```java
ObjectMapperProvider.setObjectMapper(ownMapper);
```

## **Main feature 2**:<br/>Generating JSON Fixture files from Java bean classes
The second main feature of the library is the inverse of the first one: it helps you generate JSON fixtures based on the skeleton of a bean.
In the next step (the way documented [above](https://github.com/corballis/json-fixtures#main-feature-1building-java-objects-from-json-fixture-files))
you may reload the fixture generated by the current feature into a test field -
so, with combining these two features, you can easily produce full fixture data for your application under test.

At this time the feature is available only as the `main` method of an executable class. But we are constantly developing it; according to our plans,
the feature will soon be available in the form of an IntelliJ plugin.

### How to generate a fixture file

The executable class to run if you want to generate a fixture is `ie.corballis.fixtures.generator.Main`.
Its `main` method asks for the parameters through `System.in` (console).

You have to give it the following parameters:

 1. **The class to serialize**: the fully qualified name of the class (without quotation marks) you want to create the fixture of.
 <br/>E.g. if you want to create a fixture of class `SampleClassCollections` (our sample class in the same package as `Main`),
 type `ie.corballis.fixtures.generator.SampleClassCollections`.
 
 2. **The absolute path of the folder**: the absolute path of the folder you want the newly generated file to be located in.
 <br/>E.g. `D://workspace/myProject/src/main/test/resources`.
 <br/>It's worth locating the generated fixture file on the project's classpath, preferably in folder `resources`:
 this is how the library will [recognize](https://github.com/corballis/json-fixtures#how-you-should-prepare-the-json-fixture-files)
 it when you later want to [reload](https://github.com/corballis/json-fixtures#how-to-use-the-fixture-annotation) its content into a `@Fixture`-d field.
 
 3. **The prefix of the file name**: the string (without quotation marks) you want to see before the ".fixtures.json" ending.
 <br/>E.g. if you want the file name to be `cities.fixtures.json`, type `cities`.
 
 4. **The fixture name**: the name (without quotation marks) of the JSON object to create.
 <br/>E.g. if you type `sampleFixture1`, the generated fixture will look so:
 ```json
 	"sampleFixture1":{
 		(...)
 	}
 ```
 
 5. **Should the new fixture be appended if the file already exists?**: it's possible that the file already exists
 in the given folder and with the given file name.
  This parameter is a boolean value to set whether the new fixture should be appended to the end of the file in such cases.
  (So evidently, this parameter takes effect only if the file already exists.)
  <br/>Then, if its value is `true`, there can be two cases:
  - no fixture exists in the file with the specified fixture name - then the new fixture will be appended to the end of the file;
  - there already is a fixture in the file with the specified fixture name - then, to avoid confusion or data loss, an exception is thrown.
  
  If the parameter's value is `false`, an exception is thrown to indicate "The fixture file already exists,
  but the user didn't allow appending the new fixture to its end, so nothing could have been executed!".

After having inputted the last parameter, the fixture should get created without any further user interaction.

### Modifying the data that the fixtures store

The source gives interface `generator.FixtureGenerator` for producing the fixture in the form of a `java.util.Map`.
The `Map` state is a middle phase of the fixture: it will be passed to the suitable method of another class that finally realizes the I/O operations.

Our implementation of the mentioned interface is `generator.DefaultFixtureGenerator`:
this class generates the fixtures with the bean's *default* values.

The "default" values are the following:

 1. If a field is initialized from source code (e.g. `private int a = 6;`), then the default value is the initialization value.
 2. If a field is *not* initialized from source code (e.g. `private int a;`), then the following rules are applied:
 
 --------------- | -------------------
  primitive type | the default value of the type (e.g. `short` - `0`, `char` - `\u0000`, `boolean` - `false` etc.)
  wrapper type | the default value of the corresponding primitive type (e.g. `Integer` - `0`)
  array | a new empty array
  collection, map | empty collection if the declared type can be instantiated (e.g. `ArrayList`), `null` if it is an interface (e.g. `List`, `Collection`)
  enum | `null`
  `String` | empty string (`""`)
  `Date` | `new Date()`
  `Object` | `null`

If these default values do not suit you, make and use an own implementation of `FixtureGenerator`.

If you want to modify the data that the fixtures store, but *without* changing the implementation of how they get generated,
your way to do the modification manually is by rewriting the values in the JSON fixture file itself.

### Example of usage
Let's have the following two beans:
```java
public class Sample1 {
	private int intField = 5;
	private Short shortField;
	private String initString = "myString";
	private String emptyString;
	private String[] stringArray;
}
public class Sample2 extends Sample1 {
	private Collection<String> stringCollection;
	private ArrayList<String> stringArrayList;
	private Date newDate;
}
```

Now let's generate a new fixture of `Sample2`, the way documented [above](https://github.com/corballis/json-fixtures#how-to-generate-a-fixture-file), with fixture name `sample2`!

If no file in the folder and with the filename we gave existed before, the content of the generated file should be:
```json
{
  "sample2" : {
    "intField" : 5,
    "shortField" : 0,
    "initString" : "myString",
    "emptyString" : "",
    "stringArray" : [ ],
    "stringCollection" : null,
    "stringArrayList" : [ ],
    "newDate" : "2015-08-10T09:07:51.757+0000"
  }
}
```

>Note: when the file is generated, the fields of the bean's *superclass* (here `Sample1`) are also regarded!

From now, we can modify the values in the file (e.g. rewrite `myString` to some other string), and
[use](https://github.com/corballis/json-fixtures#how-to-use-the-fixture-annotation) the fixture from
any test class, just like we would do it with any other ordinary fixture:
```java
@Fixture
private Sample2 sample2;
```

## **Main feature 3**:<br/>The library's four handy assertion methods
The library's third main feature is four assertion methods.
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