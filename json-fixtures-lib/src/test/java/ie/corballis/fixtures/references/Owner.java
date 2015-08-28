package ie.corballis.fixtures.references;

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

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (things != null ? things.hashCode() : 0);
        return result;
    }
}