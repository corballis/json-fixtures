package ie.corballis.fixtures.references;

public class Car {
    private String model;

    public Car(String model) {
        this.model = model;
    }

    public Car() {
    }

    public Car(Car car) {
        this(car.model);
    }

    @Override
    public String toString() {
        return "Car: model = " + model;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Car car = (Car) o;
        return !(model != null ? !model.equals(car.model) : car.model != null);
    }

    @Override
    public int hashCode() {
        return model != null ? model.hashCode() : 0;
    }
}