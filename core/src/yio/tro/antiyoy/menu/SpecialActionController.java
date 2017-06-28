package yio.tro.antiyoy.menu;

import yio.tro.antiyoy.GraphicsYio;
import yio.tro.antiyoy.OneTimeInfo;
import yio.tro.antiyoy.menu.scenes.Scenes;

public class SpecialActionController {

    MenuControllerYio menuControllerYio;
    boolean enabled;
    int countDown;
    private SliderYio sliderYio;


    public SpecialActionController(MenuControllerYio menuControllerYio) {
        this.menuControllerYio = menuControllerYio;

        enabled = false;
    }


    public void move() {
        if (!enabled) return;

        if (countDown > 0) {
            countDown--;

            if (countDown == 0) {
                touchDownSlider();
            }

            return;
        }

        float x = sliderYio.getViewX() + sliderYio.runnerValue * sliderYio.getViewWidth();
        x += 0.07f * GraphicsYio.width;
        float y = sliderYio.currentVerticalPos;
        sliderYio.touchDrag(x, y);

        if (sliderYio.runnerValue == 1) {
            sliderYio.touchUp(x, y);
            enabled = false;
        }
    }


    private void touchDownSlider() {
        float x = sliderYio.getViewX() + sliderYio.runnerValue * sliderYio.getViewWidth();
        float y = sliderYio.currentVerticalPos;
        sliderYio.touchDown(x, y);
    }


    public void forceEnableShroomArts() {
        Scenes.sceneSettingsMenu.create();
        Scenes.sceneMoreSettingsMenu.create();

        enabled = true;
        sliderYio = menuControllerYio.sliders.get(5);
        countDown = 40;

        OneTimeInfo instance = OneTimeInfo.getInstance();
        instance.aboutShroomArts = true;
        instance.save();
    }
}
