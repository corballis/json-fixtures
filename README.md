JSON Test Fixture library for Java [![Build Status](https://travis-ci.org/corballis/json-fixtures.svg?branch=master)](https://travis-ci.org/corballis/json-fixtures)

`JSON Fixtures` is an open-source Java library that enables us, Java developers and testers, to test our applications quickly and easily, using fixtures that we have prepared in JSON format.

>**Fixture** (according to [Wikipedia](https://en.wikipedia.org/wiki/Test_fixture#Software)):
"In software testing, a test fixture is a fixed state of the software under test used as a baseline for running tests."

>**JSON fixture**: a set of data for testing some code, given in JSON format.

The library provides three main features:

- it builds Java bean / map / list / set test-objects from JSON fixture files for testing Java applications easily, with the use of only a few annotations;
- it generates fixture files with default values based on the skeleton of a bean, for enabling us to load it as a fixture next time;
- it eases unit testing with four handy assertion methods (that are based on converting both the expected and the actual values (the results) to JSON strings).

## How to set up the library
To set up the library in a Java project, copy the following XML node, and paste it between the `<dependencies>...</dependencies>` tags of your `pom.xml` file:

```
<dependency>
	<groupId>ie.corballis</groupId>
	<artifactId>json-fixtures-lib</artifactId>
	<version>1.0.5</version>
</dependency>
```
From this time on, you can use the classes provided by the library.

## Building Java objects from JSON fixture files
One main feature of the library is that it can detect JSON fixture files on the project's classpath, process their content, and initialize the annotated fields of the test class with them for further testing. This process is almost totally automatic, it requires almost no extra configuration. And as JSON files can be created, edited and reproduced easily, this spares the developers or testers from having to build the object hierarchy of the test data from source code.

### How you should prepare the JSON fixture files
If you want a JSON file to be recognized as a possible fixture resource, its name + extension must end in ".fixtures.json" (e.g. "cities.fixtures.json"). Locate your JSON fixture resources on the project's classpath, preferably in folder `src/main/test/resources`.

The library supports both simple bean objects and collections; beans can also contain nested fields.
Example: the below JSONs are all correct, and each one can be wrapped to the Java type following it:

 - A simple bean object (with a String and a primitive field):

```json
{
	"person": {
		"name": "John Doe",
		"age": 30
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
	"dog": {
		"color": "brown",
		"owner": {
			"name": "Alice",
			"age": 35
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
- A type with a list of strings and a set of bean objects:
```json
{
	"inputData": {
		"words": ["word1", "word2", "word3"],
		"people": [{"name": "x","age": 25},
			{"name": "y","age": 40}]
	}
}
```
```java
class InputData {
	private List<String> words;
	private Set<Person> people;
}
```

### How to use the `@Fixture` annotation in a test class
Open your test class. Choose the field(s) that you want to hold fixture data, and annotate it/them with `@Fixture`.
Then specify the JSON fixture that you want to use to populate each field, by passing its fixture name to the annotation as parameter:
```java
public class MyUnitTests {
	@Fixture("person")
	private Person candidate;
	@Fixture("inputData")
	private InputData data;
}
```

>**Note**: the `@Fixture` annotation may be used only for fields!

An example where the annotated field is a collection itself:
```java
@Fixture("cities")
private List<String> cities;
```
```json
{
	"cities": ["New York", "Miskolc", "Budapest"]
}
```

### Is there a default fixture name?
If you specify no fixture name(s) for the `@Fixture` annotation as parameter(s), then the field annotation processor takes the field's name as the "default" fixture name.
Therefore, if you are satisfied with the field name being equal to the fixture name, you needn't write anything after `@Fixture`.

>**Note**: Fixture names are global on your classpath, so that you can reuse any fixture in any test class. If multiple fixtures found with the same name, the library will use the last fixture which has been scanned.
This also means that you need to take care of the proper naming convention. 
Tips: don't use too generic names like "person", "car"..etc. because that can be accidentally overridden. 

**Example** - the following two solutions are equivalent (and, of course, both of them are correct):
```java
@Fixture("car")
private Car car;
```
```java
@Fixture
private Car car;
```

### Accessing `@Fixture`-annotated fields of superclasses

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

### **Merging** the contents of multiple separate fixtures into one field

Let's see a type that declares three fields:
```java
public class Car {
	private int age;
	private String color;
	private int id;
}
```
If you only work with 6-year-old black cars in your tests, and it's only the *id* that has to vary between the instances,
then you don't need to copy the `age + color` part into every single JSON fixture.
In this case you can use a convenient feature called `merging`.

`Merging` permits that the parts of the data that finally makes up a Java object may be located in different fixtures, in a distributed manner.
If you specify the names of all of these fixtures as parameters of the `@Fixture` annotation, the library methods perform the merging.
Thus certain parts of the fixture data may be written only once and reused several times.

**Example** - let's write `age + color` only once, and then let's declare multiple cars with unique IDs:
```json
{
	"ageAndColor": {
		"age": 6,
		"color": "black"
	},
	"id1": {"id": 1},
	"id2": {"id": 2}
}
```
These fixtures may be used in the Java code as follows:
```java
@Fixture({"ageAndColor", "id1"})
private Car car1;
@Fixture({"ageAndColor", "id2"})
private Car car2;
```

If we (having previously generated `toString()` for class `Car`, and initialized *Fixtures*, see [below](https://github.com/corballis/json-fixtures#how-to-tell-the-library-to-process-the-annotations))
now print the `car1`, `car2` objects, we should receive the following output:
```java
Car{age=6, color='black', id=1}
Car{age=6, color='black', id=2}
```

>**Note**: Merging will be applied in the same order as you defined in the annotation. 
If the two (or more) objects has the same properties, the value in the last one wins. 
Currently merging lists or sets or any array like types are NOT supported. 
Feel free to contribute and send us a pull request! 

### Using references

A big advantage of our library is that it supports the usage of **references** in the fixture files.

A *reference* is a string beginning with `#` (by default), and ending in a valid fixture name.
E.g. `"#car1"` is treated as a reference if and only if there *is* a fixture with the name `car1`;
otherwise the library with fail.
If you want to use different prefix for your references specify it in the `@Fixture` annotation.

With references, you can embed fixtures into each other. This
 - makes your fixture file much more tidy, ordered and structured;
 - enables the re-use of data written in the fixture file only once.

**Example** - let's say we have 3 people with some cars in their possession. These objects -- `person1`, `person2` and `person3` --
will be the fixtures that we want to use in our test class.
Alice and Bob are family members, so we know that they own the same two cars.
Furthermore, we know that John's car is of the same model as their second one.

Using references, we can write the necessary fixture file in the following very simple, compact format:

```json
{
	"person1": {
		"name": "Alice",
		"cars": "#cars"
	},
	"person2": {
		"name": "Bob",
		"cars": "#cars"
	},
	"person3": {
		"name": "John",
		"cars": [
			"#car"
		]
	},
	"cars": [
		{"model": "Audi"},
		"#car"
	],
	"car": {"model": "Toyota"}
}
```

Note that the usage of references eases *future modification*: if tomorrow Alice and Bob change their Audi to some other car,
we have to rewrite the file only at one place, instead of having to modify it by every single person object
that connects to the changed data.

For more examples, see the tests in the library's test package `references`,
together with the fixture files they rely on; especially
[ReferencesTest](https://github.com/corballis/json-fixtures/blob/newFeatureReferences/json-fixtures-lib/src/test/java/ie/corballis/fixtures/references/ReferencesTest.java)
and
[references.fixtures.json](https://github.com/corballis/json-fixtures/blob/newFeatureReferences/json-fixtures-lib/src/test/resources/references.fixtures.json).

Circular dependencies between fixture references are NOT permitted in any depth. It will be detected by the library.

If you need to use object graph that are referring to the same object over and over again (e.g: joined hibernate entities), 
split these to separated fixtures and link them in the test code. If you want to break circular dependencies,
you can use `@JsonIdentityInfo` annotation provided by Jackson library.

#### Reference chain

Look at this JSON:

```json
{
	"car1": {"model": "#model"},
	"model": "#model2",
	"model2": "#model3",
	"model3": "BMW"
}
```

It's possible to use chained references in JSON. These will be resolved properly. In the above example the model of `"car1"` will be resolved as `BMW`

### How to tell the library to process the annotations
If you want to use the fields that you have previously annotated with `@Fixture`, you have to *initialize* the library.
Use the its access method for it: `FixtureAnnotations.initFixtures()`.
As it's the initialization process that sets the `@Fixture`-d fields, initialization must happen prior to their usage.
Therefore -- if you are working with jUnit -- it's worth initializing in a method annotated with `org.junit.Before`,
so it always gets executed before running *any* unit test, and always resets the fixture field values:

```java
@Before
public void init() throws Exception {
	FixtureAnnotations.initFixtures(this);
}
```
The parameter of the method is a not-null instance of the class that declares the `@Fixture`-d fields.
If the initialization happens in the same class where these fields are, let the parameter value be `this`.


#### **_Additional information_**:<br/>the settings of the library's default object mapper
The way JSON Fixtures reads up the JSON files rests on [Jackson](https://github.com/FasterXML/jackson) library. It uses Jackson's `ObjectMapper` class for this purpose. JSON Fixtures configures its object mapper with only two basic characteristics:
```java
objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
```

The first configuration makes the object mapper write dates as formatted strings (e.g. `2015-08-07T09:35:07.903+0000`) instead of the default long values.

The second one means that the object mapper should access every field, let its visibility modifier be anything.

This gives you two important advantages:
 - You don't definitely need to declare getters/setters for the fields you use from the fixtures -- the object mapper will still access them, even if they are private/protected.
 - Let's say you have a bean that inherits some fields from its superclass. You can write inherited fields (just as not inherited ones) into the fixture, as well, - the object mapper will fill *all existing* fields of the subclass object with the specified values properly.
 
>**Note**: for being able to map a JSON to an object,
the object mapper requires that the class of the object has a default constructor.
If your bean class does *not* declare any constructors, the default implicit constructor is available;
so in this case you don't need to declare it explicitly.
However, if you *have* defined parametrized constructor(s) for the class,
you *must* explicitly define a default constructor, as well. 

**Example**: let's say we have two beans -- `A` and `B` --, where `B` extends `A`.
Let both beans declare an own protected field (`a` and `b`).
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
Now let's prepare a fixture that describes a B instance, and specifies both the `a` and `b` values:
```json
{
	"myBInstance": {
		"a": 5,
		"b": 6
	}
}
```
Loading this fixture into a Java object, invoking its `toString()` should result the following output:
```java
a = 5, b = 6
```
So thanks to this setting, also the inherited fields can be set properly from the fixtures.

## Configure `Json-fixtures`

Every library works fine until you try it with the "Hell World" example. When your project is getting more and more complicated you often need to customize them. The same applies to `Json-fixtures`. 
That's why we introduced our `Settings.Builder` class. During the initialization, you can customize the following settings:

1. ObjectMapper: This library uses [Jackson](https://github.com/FasterXML/jackson) for json processing. If you need to customize how serialization or deserialization work, you can pass your own `ObjectMapper` instance, pimped-up with all the stuff you need to your project.
In the example below we extended our default ObjectMapper with the standard Java8+ time de/serialization:
```java
@Before
public void setUp() throws Exception {
    ObjectMapper objectMapper = Settings.Builder.defaultObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    FixtureAnnotations.initFixtures(this, new Settings.Builder().setObjectMapper(objectMapper));
}
```
2. Snapshot `FileNamingStrategy`: You can read about [snapshots](#snapshot-matching) below. If the default naming strategy is not eligible for you, you can add your custom class any time. By default [TestClassFileNamingStrategy](https://github.com/corballis/json-fixtures/blob/snapshot-matching/json-fixtures-lib/src/main/java/ie/corballis/fixtures/io/write/TestClassFileNamingStrategy.java) is applied.

3. Generator `FileNamingStrategy`: Same as above, but here you can configure the naming strategy for the generator. If the default naming strategy is not eligible for you, you can add your custom class any time. By default [TestClassFileNamingStrategy](https://github.com/corballis/json-fixtures/blob/snapshot-matching/json-fixtures-lib/src/main/java/ie/corballis/fixtures/io/write/TestClassFileNamingStrategy.java) is applied.

4. `SnapshotFixtureWriter`: If you want to change the way how the fixtures are written to the files, you can customize it by writing your custom `SnapshotFixtureWriter`. It can be useful when you are not using conventional [java project structures](#generate-snapshots-to-somewhere-else).

5. `FixtureScanner`: By default `Json-fixtures` scans your classpath and looks for `.fixture.json` files. If you need more you can write your custom scanner any time. Default class: [ClassPathFixtureScanner](https://github.com/corballis/json-fixtures/blob/snapshot-matching/json-fixtures-lib/src/main/java/ie/corballis/fixtures/io/ClassPathFixtureScanner.java)

## Generating JSON fixture files from Java bean classes
The second main feature of the library is the inverse of the first one: it helps you generate JSON fixtures based on the skeleton of a bean.
In the next step (the way documented [above](https://github.com/corballis/json-fixtures#main-feature-1building-java-objects-from-json-fixture-files))
you may reload the fixture generated by the current feature into a test field --
so, with combining these two features, you can easily produce full fixture data for your application under test.

At this time the feature is available only as the `main` method of an executable class.
But we are continuously developing it; according to our plans,
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
 	"sampleFixture1": {
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
  but the user didn't allow appending the new fixture to its end, so nothing was able to get executed!".

After having inputted the last parameter, the fixture should get created without any further user interaction.

### Modifying the data that the fixtures store

The source gives interface `generator.FixtureGenerator` for producing the fixture in the form of a `java.util.Map`.
The `Map` state is a middle phase of the fixture: it will be passed to the suitable method of another class that finally realizes the I/O operations.

Our implementation of the mentioned interface is `generator.DefaultFixtureGenerator`:
this class generates the fixtures with the bean's *default* values.

The "default" values are the following:

 1. If a field is initialized from source code (e.g. `private int a = 6;`), then the default value is the initialization value.
 2. If a field is *not* initialized from source code (e.g. `private int a;`), then the following rules are applied:
 
Type | Default value
---- | -------------------
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

>**Note**: when the fixture is generated, the fields of the bean's *superclasses* (here `Sample1`) are also regarded!

From now, we can modify the values in the file (e.g. rewrite `myString` to some other string), and
[use](https://github.com/corballis/json-fixtures#how-to-use-the-fixture-annotation) the fixture from
any test class, just like we would do any other ordinary fixture:
```java
@Fixture
private Sample2 sample2;
```

## The library's four handy assertion methods
The library's third main feature is four assertion methods.
They are the instance methods of class `FixtureAssert`.

Their working is based on converting both the expected and the actual values (aka the method results) to JSON-formatted strings, comparing these strings and asserting that they match each other. The implementation of the methods notably relies on the aid of [hamcrest](https://github.com/hamcrest/JavaHamcrest).

The methods assert that the actual value matches the fixture(s) passed as parameters.

There are the following variations:

 1. `matches(String... fixtures)`:
allows both any array ordering and extra unexpected fields;
 2. `matchesWithStrictOrder(String... fixtures)`:
allows only strict array ordering, but allows extra unexpected fields;
 3. `matchesExactly(String... fixtures)`:
allows any array ordering, but no extra unexpected fields;
 4. `matchesExactlyWithStrictOrder(String... fixtures)`:
allows only strict array ordering and no extra unexpected fields.

>**Warning**: if you use a fixture as a parameter of the previous assertion methods, you **cannot** use references in it!
During the execution of these methods no references are resolved; they all remain simple beginning-with-"#" strings.

#### Example of usage:
```java
import static ie.corballis.fixtures.assertion.FixtureAssert.assertThat;
(...)
public class TestClass {
	@Test
	public void test2() throws JsonProcessingException {
		List<String> actual = myMethodResult();
		assertThat(actual).matchesExactly("expected");
	}
}
```

## Snapshot matching

Snapshot matching is inspired by [Jest Framework](https://jestjs.io). Althought Jest is a frontend testing framework, the concept of snapshot matching can be useful in Java tests too.
When you would like to test a bean, you need to create the expected json and save it to a fixture file as it was described above. If your are such a lazy guy/girl as we are, you will probably convert the original object to json and then copy it to the proper file. 
We have good news! Snapshot matching saves this time for you. 
A snapshot is the actual state of your bean, which will be written to a file for the first time. When we detect that a snapshot is present, we compare that with the actual value.

### How snapshots work
To try it out quickly, use the `FixtureAssert.assertThat(bean).toMatchSnapshot()` method, which will generate the initial json value at the first usage. 

By default all snapshots are written to a `.fixtures.json` ending file next to your test class with the same name (`MyTests.java -> MyTests.fixtures.json`). If you need different file naming strategy or you want to generate the file elsewhere, jump to the [Generate snapshots to somewhere else](#generate-snapshots-to-somewhere-else) or [Change the name of the snapshot file](#change-the-name-of-the-snapshot-file) sections.

Conventionally the generated fixtures will have the same names as the running testcase. It's possible to call `toMatchSnapshot()` multiple times in a test. According to the default naming convention every call will be postfixed with the index of the execution.

Example:

```java
import static ie.corballis.fixtures.assertion.FixtureAssert.assertThat;
(...)
public class TestClass {
	@Test
	public void test2() throws Exception {
		List<String> actual = myMethodResult();
		assertThat(actual).toMatchSnapshot();
		
		Bean actual2 = myMethodResult2();
		assertThat(actual2).toMatchSnapshot();
	}
}
```
When you run this test for the first time, the contents of the `TestClass.fixtures.json` will be something similar:

```javascript
{
    "test2-1": [...],
    "test2-2": {...}
}
```
After the initial execution, `toMatchSnapshot` methods will work like the other matchers. They look up the fixtures based on the previous conventions and match the fixures with the actual values.

Similarly as you could have seen in the assertions section, you can use `toMatchSnapshot` with different level of strictness:

 1. `toMatchSnapshot()`:
allows both any array ordering and extra unexpected fields;
 2. `toMatchSnapshotWithStrictOrder()`:
allows only strict array ordering, but allows extra unexpected fields;
 3. `toMatchSnapshotExactly()`:
allows any array ordering, but no extra unexpected fields;
 4. `toMatchSnapshotExactlyWithStrictOrder()`:
allows only strict array ordering and no extra unexpected fields.

### Generate snapshots to somewhere else

By default snapshot files are generated next to the test class. 
**NOTE:** The `DefaultSnapshotWriter` assumes that you use conventional project structure like this:

```
├───src
    ├───main
    │   └───java
    │       └───packages
    │
    └───test
        ├───java
        	└───packages
```
If you have different structure, you can either write a custom `SnapshotFixtureWriter` or you can set an absolute path to the folder where you want your fixture files to be generated to. You can configure a custom path by using our `SettingsBuilder`:

```java
    @Before
    public void setUp() throws Exception {
        FixtureAnnotations.initFixtures(this, new Settings.Builder().setSnapshotFolderPath("/absolute/path/to/my/folder"));
    }
```

### Change the name of the snapshot file

By default all snapshots are written to a `.fixtures.json` file starting with the same name as your test class. If this behavior is not suitable for your needs, you can configure a custom `FileNamingStrategy`:

```java
    @Before
    public void setUp() throws Exception {
        FixtureAnnotations.initFixtures(this, new Settings.Builder().setSnapshotFileNamingStrategy(...));
    }
```

### Regenerate snapshots

There are situations when you need to refactor bigger chunks of your codebase. When it happens, it's often easier to regenerate some fixtures rather than patching them until they are up-to-date. 
For these cases we introduced the `regenerate` flag  in `toMatchSnapshot*` methods. 
**NOTE:** When this flag is turned on, the fixures are regenerated at every test execution and the assertions are not executed. **Make sure that you don't commit any test files to source control where the regeneration is turned on.**

### Protection against renames/moves

Sometimes you need to move your test classes to other packages or rename them to something else. `toMatchSnapshot*` methods are looking for the snapshot files in the same place where they were generated. 
It leads to problems when you forget to move/rename the fixture file along with your test class. In order to prevent these situations, every snapshot fixture file contains a special property called: `_AUTO_GENERATED_FOR_`. This property stores the fully qualified class name of test which it was generated for. Based on this property, the existence of the test file will be validated during the initialization of the fixtures.

**NOTE**: Never remove `_AUTO_GENERATED_FOR_` property from the fixture files otherwise you won't be protected against renames/moves.
