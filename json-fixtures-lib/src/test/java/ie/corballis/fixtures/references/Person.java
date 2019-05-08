package ie.corballis.fixtures.references;

import java.util.ArrayList;

public class Person {
    private int age;
    private Dog dog;
    private ArrayList<Car> cars;

    @Override
    public String toString() {
        return "Person:" +
               "\nage = " + age +
               ",\ndog = " + dog +
               ",\ncars = " + cars;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Person person = (Person) o;
        if (age != person.age) {
            return false;
        }
        if (dog != null ? !dog.equals(person.dog) : person.dog != null) {
            return false;
        }
        return !(cars != null ? !cars.equals(person.cars) : person.cars != null);
    }
}