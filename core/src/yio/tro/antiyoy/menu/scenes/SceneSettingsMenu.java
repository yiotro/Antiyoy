package yio.tro.antiyoy.menu.scenes;

import com.badlogic.gdx.Gdx;
import yio.tro.antiyoy.Settings;
import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.YioGdxGame;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.CheckButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.stuff.LanguagesManager;

import java.util.ArrayList;

public class SceneSettingsMenu extends AbstractScene{


    private double labelHeight;
    private double labelTopY;
    Reaction soundChkReaction;
    private CheckButtonYio chkSound;


    public SceneSettingsMenu(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        soundChkReaction = new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                Settings.soundEnabled = chkSound.isChecked();
            }
        };
    }


    @Override
    public void create() {
        menuControllerYio.beginMenuCreation();

        menuControllerYio.getYioGdxGame().beginBackgroundChange(1, false, true);

        menuControllerYio.spawnBackButton(190, Reaction.rbCloseSettingsMenu);

        createInfoButton();
        createMainLabel();
        createCheckButtons();
        createMoreSettingsButton();

        Settings.getInstance().loadSettings();

        menuControllerYio.endMenuCreation();
    }


    private void createMoreSettingsButton() {
        ButtonYio moreSettingsButton = buttonFactory.getButton(generateRectangle(0.62, labelTopY - labelHeight, 0.3, 0.05), 199, getString("more"));
        moreSettingsButton.setReaction(Reaction.rbMoreSettings);
        moreSettingsButton.setAnimation(Animation.FROM_CENTER);
        moreSettingsButton.disableTouchAnimation();
        moreSettingsButton.setTouchOffset(0.05f * Gdx.graphics.getHeight());
    }


    private void createCheckButtons() {
        double checkButtonSize = 0.05;
        double hSize = GraphicsYio.convertToHeight(checkButtonSize);
        double chkX = 0.9 - checkButtonSize;
        double chkY = labelTopY - 0.07;

        CheckButtonYio chkAutosave = CheckButtonYio.getCheckButton(menuControllerYio, generateSquare(chkX, chkY - hSize / 2, hSize), 1);
        chkAutosave.setTouchPosition(generateRectangle(0.04, chkY - hSize * 1.5, 0.92, hSize * 3));

        chkY -= 0.086;
        CheckButtonYio chkMusic = CheckButtonYio.getCheckButton(menuControllerYio, generateSquare(chkX, chkY - hSize / 2, hSize), 2);
        chkMusic.setTouchPosition(generateRectangle(0.04, chkY - hSize * 1.5, 0.92, hSize * 3));

        chkY -= 0.086;
        CheckButtonYio chkTurnEnd = CheckButtonYio.getCheckButton(menuControllerYio, generateSquare(chkX, chkY - hSize / 2, hSize), 3);
        chkTurnEnd.setTouchPosition(generateRectangle(0.04, chkY - hSize * 1.5, 0.92, hSize * 3));

        chkY -= 0.086;
        CheckButtonYio chkCityNames = CheckButtonYio.getCheckButton(menuControllerYio, generateSquare(chkX, chkY - hSize / 2, hSize), 4);
        chkCityNames.setTouchPosition(generateRectangle(0.04, chkY - hSize * 1.5, 0.92, hSize * 3));

        chkY -= 0.086;
        chkSound = CheckButtonYio.getCheckButton(menuControllerYio, generateSquare(chkX, chkY - hSize / 2, hSize), 5);
        chkSound.setReaction(soundChkReaction);
        chkSound.setTouchPosition(generateRectangle(0.04, chkY - hSize * 1.5, 0.92, hSize * 3));

        for (int i = 1; i <= 5; i++) {
            menuControllerYio.getCheckButtonById(i).setAnimation(Animation.FROM_CENTER);
        }
    }


    private void createMainLabel() {
        labelHeight = 0.52;
        labelTopY = 0.7;
        ButtonYio mainLabel = buttonFactory.getButton(generateRectangle(0.04, labelTopY - labelHeight, 0.92, labelHeight), 192, null);
        if (mainLabel.notRendered()) {
            mainLabel.cleatText();

            mainLabel.addTextLine(LanguagesManager.getInstance().getString(" "));
            mainLabel.addTextLine(LanguagesManager.getInstance().getString("autosave"));
            mainLabel.addTextLine(LanguagesManager.getInstance().getString(" "));

            mainLabel.addTextLine(LanguagesManager.getInstance().getString("music"));
            mainLabel.addTextLine(LanguagesManager.getInstance().getString(" "));

            mainLabel.addTextLine(LanguagesManager.getInstance().getString("ask_to_end_turn"));
            mainLabel.addTextLine(LanguagesManager.getInstance().getString(" "));

            mainLabel.addTextLine(LanguagesManager.getInstance().getString("city_names"));
            mainLabel.addTextLine(LanguagesManager.getInstance().getString(" "));

            mainLabel.addTextLine(LanguagesManager.getInstance().getString("sound"));
            mainLabel.addTextLine(LanguagesManager.getInstance().getString(" "));
            mainLabel.addTextLine(LanguagesManager.getInstance().getString(" "));

            menuControllerYio.getButtonRenderer().renderButton(mainLabel);
        }
        mainLabel.setTouchable(false);
        mainLabel.setAnimation(Animation.FROM_CENTER);
    }


    private void createInfoButton() {
        ButtonYio infoButton = buttonFactory.getButton(generateSquare(0.8, 0.89, 0.15 * YioGdxGame.screenRatio), 191, null);
        menuControllerYio.loadButtonOnce(infoButton, "info_icon.png");
        infoButton.setShadow(true);
        infoButton.setAnimation(Animation.UP);
        infoButton.setReaction(Reaction.rbInfo);
        infoButton.disableTouchAnimation();
    }
}