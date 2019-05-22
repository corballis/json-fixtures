package ie.corballis.fixtures.references;

import java.util.Objects;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

public class Owner {
    private String name;
    private Set<Thing> things = newHashSet();

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

    public Set<Thing> getThings() {
        return things;
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
        return Objects.equals(name, owner.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}