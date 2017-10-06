package yio.tro.antiyoy.menu.scenes;

import yio.tro.antiyoy.menu.behaviors.ReactBehavior;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;

import java.util.ArrayList;

public class SceneTutorialTip extends AbstractScene{


    public SceneTutorialTip(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
    }


    public void createTutorialTip(ArrayList<String> text) {
        menuControllerYio.getButtonById(31).setTouchable(false);
        menuControllerYio.getButtonById(32).setTouchable(false);

        for (int i = 0; i < 2; i++) text.add(" ");
        ButtonYio textPanel = buttonFactory.getButton(generateRectangle(0, 0, 1, 1), 50, null);
        textPanel.setPosition(generateRectangle(0, 0.1, 1, 0.05 * (double) text.size()));
        textPanel.cleatText();
        textPanel.addManyLines(text);
        menuControllerYio.getButtonRenderer().renderButton(textPanel);
        textPanel.setTouchable(false);
        textPanel.setAnimType(ButtonYio.ANIM_COLLAPSE_DOWN);
        textPanel.appearFactor.beginSpawning(3, 1);

        ButtonYio okButton = buttonFactory.getButton(generateRectangle(0.6, 0.1, 0.4, 0.07), 53, getString("end_game_ok"));
        okButton.setShadow(false);
        okButton.setReactBehavior(ReactBehavior.rbCloseTutorialTip);
        okButton.setAnimType(ButtonYio.ANIM_COLLAPSE_DOWN);
        okButton.appearFactor.beginSpawning(3, 1);
    }


    public void createTutorialTipWithFixedHeight(ArrayList<String> text, int lines) {
        menuControllerYio.getButtonById(31).setTouchable(false);
        menuControllerYio.getButtonById(32).setTouchable(false);

        for (int i = 0; i < 2; i++) text.add(" ");
        ButtonYio textPanel = buttonFactory.getButton(generateRectangle(0, 0, 1, 1), 50, null);
        textPanel.setPosition(generateRectangle(0, 0.1, 1, 0.3));
        textPanel.cleatText();
        textPanel.addManyLines(text);
        while (textPanel.text.size() < lines) {
            textPanel.addTextLine(" ");
        }
        menuControllerYio.getButtonRenderer().renderButton(textPanel);
        textPanel.setTouchable(false);
        textPanel.setAnimType(ButtonYio.ANIM_COLLAPSE_DOWN);
        textPanel.appearFactor.beginSpawning(3, 1);

        ButtonYio okButton = buttonFactory.getButton(generateRectangle(0.6, 0.1, 0.4, 0.07), 53, getString("end_game_ok"));
        okButton.setShadow(false);
        okButton.setReactBehavior(ReactBehavior.rbCloseTutorialTip);
        okButton.setAnimType(ButtonYio.ANIM_COLLAPSE_DOWN);
        okButton.appearFactor.beginSpawning(3, 1);
    }


    public void closeTutorialTip() {
        menuControllerYio.getButtonById(50).destroy();
        menuControllerYio.getButtonById(53).destroy();
        menuControllerYio.getButtonById(50).appearFactor.beginDestroying(1, 3);
        menuControllerYio.getButtonById(53).appearFactor.beginDestroying(1, 3);

        if (menuControllerYio.getButtonById(54) != null) { // help index button
            menuControllerYio.getButtonById(54).destroy();
            menuControllerYio.getButtonById(54).appearFactor.beginDestroying(1, 3);
        }

        menuControllerYio.getButtonById(30).setTouchable(true);
        menuControllerYio.getButtonById(31).setTouchable(true);
        menuControllerYio.getButtonById(32).setTouchable(true);
    }


    @Override
    public void create() {

    }
}