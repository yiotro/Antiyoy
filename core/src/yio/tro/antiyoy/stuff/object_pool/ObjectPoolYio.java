package yio.tro.antiyoy.stuff.object_pool;

import yio.tro.antiyoy.stuff.Yio;

import java.util.ArrayList;
import java.util.ListIterator;

public abstract class ObjectPoolYio<ObjectType extends ReusableYio> {


    private ArrayList<ObjectType> freeObjects;
    private ArrayList<ObjectType> externalList;


    public ObjectPoolYio() {
        freeObjects = new ArrayList<>();
        externalList = null;
    }


    public ObjectPoolYio(ArrayList<ObjectType> externalList) {
        freeObjects = new ArrayList<>();
        setExternalList(externalList);
    }


    public abstract ObjectType makeNewObject();


    public void add(ObjectType object) {
        Yio.addByIterator(freeObjects, object);
    }


    public void addWithCheck(ObjectType object) {
        if (object == null) return;
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


    public int getSize() {
        return freeObjects.size();
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


    public void removeFromExternalList(ObjectType object) {
        add(object);
        externalList.remove(object);
    }


    public void clearExternalList() {
        while (externalList.size() > 0) {
            removeFromExternalList(externalList.get(0));
        }
    }


    public ObjectType getFreshObject() {
        ObjectType next = getNext();
        externalList.add(next);
        return next;
    }


    public void setExternalList(ArrayList<ObjectType> externalList) {
        this.externalList = externalList;
    }
}
