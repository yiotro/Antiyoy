package yio.tro.antiyoy.menu.diplomacy_element;

import yio.tro.antiyoy.factor_yio.FactorYio;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.LanguagesManager;
import yio.tro.antiyoy.stuff.PointYio;
import yio.tro.antiyoy.stuff.RectangleYio;

public class DeLabel {

    DiplomacyElement diplomacyElement;
    public String text;
    public PointYio position;
    public float textWidth;
    public boolean visible;
    public FactorYio appearFactor;


    public DeLabel(DiplomacyElement diplomacyElement) {
        this.diplomacyElement = diplomacyElement;

        text = LanguagesManager.getInstance().getString("diplomacy");
        position = new PointYio();
        textWidth = GraphicsYio.getTextWidth(diplomacyElement.titleFont, text);
        visible = true;
        appearFactor = new FactorYio();
        appear();
    }


    void move() {
        appearFactor.move();
        updatePosition();
    }


    void appear() {
        appearFactor.setValues(1, 0);
        appearFactor.appear(1, 1);
    }


    public void setVisible(boolean visible) {
        this.visible = visible;

        if (visible) {
            appear();
        }
    }


    void updatePosition() {
        RectangleYio pos = diplomacyElement.viewPosition;
        position.x = (float) (pos.x + pos.width / 2 - textWidth / 2);
        position.y = (float) (pos.y + pos.height - 0.02f * GraphicsYio.width);
    }

}