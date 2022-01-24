package yio.tro.antiyoy.gameplay.editor;

import yio.tro.antiyoy.stuff.object_pool.ReusableYio;

import java.util.ArrayList;

public class Coalition implements ReusableYio {

    public ArrayList<Integer> fractions;


    public Coalition() {
        fractions = new ArrayList<>();
    }


    @Override
    public void reset() {
        fractions.clear();
    }


    public boolean contains(int fraction) {
        for (int f : fractions) {
            if (f == fraction) return true;
        }
        return false;
    }


    public void add(int fraction) {
        if (contains(fraction)) return;
        fractions.add(fraction);
    }


    public void remove(int fraction) {
        fractions.remove(fraction);
    }
}
