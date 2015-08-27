package ie.corballis.fixtures.core;

import java.util.Stack;

public class Context {
    private Stack<Object> context = new Stack<Object>();

    public Context(Object firstObject) {
        push(firstObject);
    }

    private Context(Stack<Object> context) {
        this.context = context;
    }

    public void push(Object o) {
        context.push(o);
    }

    public void pop() {
        context.pop();
    }

    public int size() {
        return context.size();
    }

    public Object firstElement() {
        return context.firstElement();
    }

    public Object get(int i) {
        return context.get(i);
    }

    @SuppressWarnings("unchecked")
    public Context copy() {
        return new Context((Stack<Object>) context.clone());
    }

    public Context withoutLastElement() {
        Stack<Object> newContext = copy().context;
        newContext.pop();
        return new Context(newContext);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Context context1 = (Context) o;
        return !(context != null ? !context.equals(context1.context) : context1.context != null);
    }

    @Override
    public int hashCode() {
        return context != null ? context.hashCode() : 0;
    }

    @Override
    public String toString() {
        return context.toString();
    }
}