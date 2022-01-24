package yio.tro.antiyoy.menu.scenes.gameplay;

import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.GoalViewElement;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.stuff.Yio;

public class SceneGoalView extends AbstractModalScene{

    GoalViewElement goalViewElement;


    public SceneGoalView(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
        goalViewElement = null;
    }


    @Override
    public void create() {
        initGoalViewElement();
        goalViewElement.appear();
    }


    private void initGoalViewElement() {
        if (goalViewElement != null) return;

        goalViewElement = new GoalViewElement(menuControllerYio);
        goalViewElement.setAnimation(Animation.up);
        goalViewElement.setPosition(generateRectangle(0, 0.9, 1, 0.1));
        menuControllerYio.addElementToScene(goalViewElement);
    }


    public void sync() {
        if (goalViewElement == null) return;
        goalViewElement.loadValues();
    }


    @Override
    public void hide() {
        if (goalViewElement != null) {
            goalViewElement.destroy();
        }
    }
}
