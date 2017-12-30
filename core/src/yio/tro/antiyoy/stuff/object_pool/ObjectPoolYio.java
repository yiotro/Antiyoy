package yio.tro.antiyoy.stuff.object_pool;

import yio.tro.antiyoy.stuff.Yio;

import java.util.ArrayList;
import java.util.ListIterator;

public abstract class ObjectPoolYio<ObjectType extends ReusableYio> {


    private ArrayList<ObjectType> freeObjects;


    public ObjectPoolYio() {
        freeObjects = new ArrayList<>();
    }


    public abstract ObjectType makeNewObject();


    public void add(ObjectType object) {
        Yio.addByIterator(freeObjects, object);
    }


    public void addWithCheck(ObjectType object) {
        if (contains(object)) return;

        add(object);
    }


    public boolean contains(ObjectType object) {
        return freeObjects.contains(object);
    }


    public void clear() {
        freeObjects.clear();
    }


    public ObjectType getNext() {
        if (freeObjects.size() > 0) {
            ListIterator<ObjectType> iterator = freeObjects.listIterator();
            ObjectType next = iterator.next();
            iterator.remove();
            next.reset();
            return next;
        }

        ObjectType object = makeNewObject();
        object.reset();
        return object;
    }


    public void showInConsole() {
        System.out.println();
        if (freeObjects.size() == 0) {
            System.out.println("Empty pool");
        } else {
            String simpleName = freeObjects.get(0).getClass().getSimpleName();
            System.out.println("Pool" +
                    "(" + freeObjects.size() + ")" +
                    ": " + simpleName);
        }
    }


    public boolean hasDuplicates() {
        for (int i = 0; i < freeObjects.size(); i++) {
            for (int j = i + 1; j < freeObjects.size(); j++) {
                if (freeObjects.get(i) == freeObjects.get(j)) {
                    System.out.println("Found duplicate in pool");
                    return true;
                }
            }
        }

        return false;
    }
}
