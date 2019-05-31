package ie.corballis.fixtures.references;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class Person {
    private int age;
    private Dog dog;
    private List<Car> cars;

    public Person() {
    }

    public Person(Person person1r) {
        this(person1r.age, person1r.dog, person1r.cars);
    }

    public Person(int age, Dog dog, List<Car> cars) {
        this.age = age;
        this.dog = new Dog(dog);
        this.cars = cars != null ? cars.stream().map(Car::new).collect(toList()) : null;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Dog getDog() {
        return dog;
    }

    public void setDog(Dog dog) {
        this.dog = dog;
    }

    public List<Car> getCars() {
        return cars;
    }

    public void setCars(List<Car> cars) {
        this.cars = cars;
    }

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