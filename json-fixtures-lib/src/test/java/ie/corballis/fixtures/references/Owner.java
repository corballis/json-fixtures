package ie.corballis.fixtures.references;

import java.util.ArrayList;

import static com.google.common.collect.Lists.newArrayList;

public class Owner {
    private String name;
    private ArrayList<Thing> things = newArrayList();

    public String getName() {
        return name;
    }

    public Owner(String name) {
        this.name = name;
    }

    public Owner() {
    }

    public void add(Thing thing) {
        things.add(thing);
    }

    @Override
    public String toString() {
        return "Owner:" +
               "\n\tname = " + name +
               ",\n\tthings = " + things;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Owner owner = (Owner) o;
        if (name != null ? !name.equals(owner.name) : owner.name != null) {
            return false;
        }
        return !(things != null ? !things.equals(owner.things) : owner.things != null);
    }
}