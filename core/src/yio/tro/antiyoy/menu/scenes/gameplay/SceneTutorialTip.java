package yio.tro.antiyoy.menu.scenes.gameplay;

import yio.tro.antiyoy.gameplay.campaign.CampaignProgressManager;
import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.scenes.Scenes;

import java.util.ArrayList;

public class SceneTutorialTip extends AbstractModalScene {


    private final ArrayList<String> text;


    public SceneTutorialTip(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
        text = new ArrayList<>();
    }


    public void createTutorialTip(ArrayList<String> srcText) {
        text.clear();
        text.addAll(srcText);

        menuControllerYio.getButtonById(32).setTouchable(false);

        for (int i = 0; i < 2; i++) text.add(" ");
        ButtonYio textPanel = buttonFactory.getButton(generateRectangle(0, 0, 1, 1), 50, null);
        textPanel.setPosition(generateRectangle(0, 0.1, 1, 0.05 * (double) text.size()));
        textPanel.cleatText();
        textPanel.addManyLines(text);
        menuControllerYio.getButtonRenderer().renderButton(textPanel);
        textPanel.setTouchable(false);
        textPanel.setAnimation(Animation.fixed_down);
        textPanel.enableRectangularMask();
        textPanel.appearFactor.appear(3, 1);

        ButtonYio okButton = buttonFactory.getButton(generateRectangle(0.6, 0.1, 0.4, 0.07), 53, getString("end_game_ok"));
        okButton.setShadow(false);
        okButton.setReaction(Reaction.rbCloseTutorialTip);
        okButton.setAnimation(Animation.fixed_down);
        okButton.appearFactor.appear(3, 1);
    }


    public void createTutorialTipWithFixedHeight(ArrayList<String> text, int lines) {
        menuControllerYio.getButtonById(32).setTouchable(false);

        for (int i = 0; i < 2; i++) text.add(" ");
        ButtonYio textPanel = buttonFactory.getButton(generateRectangle(0, 0, 1, 1), 50, null);
        textPanel.setPosition(generateRectangle(0, 0.1, 1, 0.3));
        textPanel.cleatText();
        textPanel.addManyLines(text);
        while (textPanel.textLines.size() < lines) {
            textPanel.addTextLine(" ");
        }
        menuControllerYio.getButtonRenderer().renderButton(textPanel);
        textPanel.setTouchable(false);
        textPanel.setAnimation(Animation.fixed_down);
        textPanel.appearFactor.appear(3, 1);

        ButtonYio okButton = buttonFactory.getButton(generateRectangle(0.6, 0.1, 0.4, 0.07), 53, getString("end_game_ok"));
        okButton.setShadow(false);
        okButton.setReaction(Reaction.rbCloseTutorialTip);
        okButton.setAnimation(Animation.fixed_down);
        okButton.appearFactor.appear(3, 1);
    }


    @Override
    public void hide() {
        menuControllerYio.getButtonById(50).destroy();
        menuControllerYio.getButtonById(53).destroy();
        menuControllerYio.getButtonById(50).appearFactor.destroy(1, 3);
        menuControllerYio.getButtonById(53).appearFactor.destroy(1, 3);

        if (menuControllerYio.getButtonById(57) != null) { // help index button
            menuControllerYio.getButtonById(57).destroy();
            menuControllerYio.getButtonById(57).appearFactor.destroy(1, 3);
        }

        if (menuControllerYio.getButtonById(55) != null) { // help index button
            menuControllerYio.getButtonById(55).destroy();
            menuControllerYio.getButtonById(55).appearFactor.destroy(1, 3);
        }

        if (menuControllerYio.getButtonById(54) != null) { // win button
            menuControllerYio.getButtonById(54).destroy();
            menuControllerYio.getButtonById(54).appearFactor.destroy(1, 3);
        }

        menuControllerYio.getButtonById(30).setTouchable(true);
        menuControllerYio.getButtonById(32).setTouchable(true);
    }


    public boolean isCurrentlyVisible() {
        ButtonYio buttonById = menuControllerYio.getButtonById(50);
        return buttonById != null && buttonById.getFactor().getGravity() >= 0;
    }


    public void addHelpButtonToTutorialTip() {
        ButtonYio helpButton = buttonFactory.getButton(generateRectangle(0, 0.1, 0.6, 0.07), 57, null);
        helpButton.setTextLine(getString("help"));
        menuControllerYio.buttonRenderer.renderButton(helpButton);
        helpButton.setShadow(false);
        helpButton.setReaction(new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                CampaignProgressManager.getInstance().markLevelAsCompleted(0);
                Scenes.sceneHelpIndex.create();
            }
        });
        helpButton.setAnimation(Animation.fixed_down);
        helpButton.appearFactor.appear(3, 1);
    }


    @Override
    public void create() {

    }
}