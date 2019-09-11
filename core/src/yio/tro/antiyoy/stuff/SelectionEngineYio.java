package yio.tro.antiyoy.stuff;

import yio.tro.antiyoy.factor_yio.FactorYio;

public class SelectionEngineYio {

    public boolean selected;
    public FactorYio factorYio;


    public SelectionEngineYio() {
        selected = false;
        factorYio = new FactorYio();
    }


    public void move() {
        if (!selected) return;

        factorYio.move();

        if (factorYio.get() == 0) {
            selected = false;
        }
    }


    public float getAlpha() {
        return 0.25f * factorYio.get();
    }


    public boolean isSelected() {
        return selected;
    }


    public void select() {
        selected = true;
        factorYio.setValues(1, 0);
        factorYio.destroy(1, 2);
    }

}
