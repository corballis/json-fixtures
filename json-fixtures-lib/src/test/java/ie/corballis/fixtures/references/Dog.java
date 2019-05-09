package ie.corballis.fixtures.references;

public class Dog {
    private String name;
    private double age;
    private String breed;

    public Dog() {
    }

    public Dog(String name, double age, String breed) {
        this.name = name;
        this.age = age;
        this.breed = breed;
    }

    public Dog(Dog dog) {
        this(dog.name, dog.age, dog.breed);
    }

    @Override
    public String toString() {
        return "Dog:" +
               "\n\tname = " + name +
               ",\n\tage = " + age +
               ",\n\tbreed = " + breed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Dog dog = (Dog) o;
        if (Double.compare(dog.age, age) != 0) {
            return false;
        }
        if (name != null ? !name.equals(dog.name) : dog.name != null) {
            return false;
        }
        return !(breed != null ? !breed.equals(dog.breed) : dog.breed != null);
    }
}