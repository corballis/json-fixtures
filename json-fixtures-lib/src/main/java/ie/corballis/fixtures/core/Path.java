package ie.corballis.fixtures.core;

import java.util.Stack;

public class Path {
    private Stack<Object> path = new Stack<Object>();

    public Path(Object firstObject) {
        push(firstObject);
    }

    private Path(Stack<Object> path) {
        this.path = path;
    }

    public void push(Object o) {
        path.push(o);
    }

    public void pop() {
        path.pop();
    }

    public int size() {
        return path.size();
    }

    public Object firstElement() {
        return path.firstElement();
    }

    public Object get(int i) {
        return path.get(i);
    }

    @SuppressWarnings("unchecked")
    public Path copy() {
        return new Path((Stack<Object>) path.clone());
    }

    public Path withoutLastElement() {
        Stack<Object> newPath = copy().path;
        newPath.pop();
        return new Path(newPath);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Path path1 = (Path) o;
        return !(path != null ? !path.equals(path1.path) : path1.path != null);
    }

    @Override
    public int hashCode() {
        return path != null ? path.hashCode() : 0;
    }

    @Override
    public String toString() {
        return path.toString();
    }
}