package yio.tro.antiyoy.menu.scenes;

import com.badlogic.gdx.Gdx;
import yio.tro.antiyoy.GraphicsYio;
import yio.tro.antiyoy.YioGdxGame;
import yio.tro.antiyoy.menu.behaviors.ReactBehavior;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.CheckButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;

import java.util.ArrayList;

public class SceneSettingsMenu extends AbstractScene{


    private double labelHeight;
    private double labelTopY;


    public SceneSettingsMenu(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
    }


    @Override
    public void create() {
        menuControllerYio.beginMenuCreation();

        menuControllerYio.getYioGdxGame().beginBackgroundChange(1, false, true);

        menuControllerYio.spawnBackButton(190, ReactBehavior.rbCloseSettingsMenu);

        ButtonYio infoButton = buttonFactory.getButton(generateSquare(0.8, 0.89, 0.15 * YioGdxGame.screenRatio), 191, null);
        menuControllerYio.loadButtonOnce(infoButton, "info_icon.png");
        infoButton.setShadow(true);
        infoButton.setAnimType(ButtonYio.ANIM_UP);
        infoButton.setReactBehavior(ReactBehavior.rbInfo);
        infoButton.disableTouchAnimation();

        labelHeight = 0.52;
        labelTopY = 0.7;
        ButtonYio mainLabel = buttonFactory.getButton(generateRectangle(0.04, labelTopY - labelHeight, 0.92, labelHeight), 192, null);
        mainLabel.cleatText();
        ArrayList<String> list = menuControllerYio.getArrayListFromString(getString("main_label"));
        mainLabel.addManyLines(list);
        int addedEmptyLines = 11 - list.size();
        for (int i = 0; i < addedEmptyLines; i++) {
            mainLabel.addTextLine(" ");
        }
        menuControllerYio.getButtonRenderer().renderButton(mainLabel);
        mainLabel.setTouchable(false);
        mainLabel.setAnimType(ButtonYio.ANIM_FROM_CENTER);

        double checkButtonSize = 0.05;
        double hSize = GraphicsYio.convertToHeight(checkButtonSize);
        double chkX = 0.9 - checkButtonSize;
        double chkY = labelTopY - 0.07;

        CheckButtonYio chkAutosave = CheckButtonYio.getCheckButton(menuControllerYio, generateSquare(chkX, chkY - hSize / 2, hSize), 1);
        chkAutosave.setTouchPosition(generateRectangle(0.04, chkY - hSize * 1.5, 0.92, hSize * 3));

        chkY -= 0.086;
        CheckButtonYio chkSlots = CheckButtonYio.getCheckButton(menuControllerYio, generateSquare(chkX, chkY - hSize / 2, hSize), 2);
        chkSlots.setTouchPosition(generateRectangle(0.04, chkY - hSize * 1.5, 0.92, hSize * 3));

        chkY -= 0.086;
        CheckButtonYio chkTurnEnd = CheckButtonYio.getCheckButton(menuControllerYio, generateSquare(chkX, chkY - hSize / 2, hSize), 3);
        chkTurnEnd.setTouchPosition(generateRectangle(0.04, chkY - hSize * 1.5, 0.92, hSize * 3));

        chkY -= 0.086;
        CheckButtonYio chkCityNames = CheckButtonYio.getCheckButton(menuControllerYio, generateSquare(chkX, chkY - hSize / 2, hSize), 4);
        chkCityNames.setTouchPosition(generateRectangle(0.04, chkY - hSize * 1.5, 0.92, hSize * 3));

        chkY -= 0.086;
        CheckButtonYio chkSound = CheckButtonYio.getCheckButton(menuControllerYio, generateSquare(chkX, chkY - hSize / 2, hSize), 5);
        chkSound.setTouchPosition(generateRectangle(0.04, chkY - hSize * 1.5, 0.92, hSize * 3));

        for (int i = 1; i <= 5; i++) {
            menuControllerYio.getCheckButtonById(i).setAnimType(ButtonYio.ANIM_FROM_CENTER);
        }

        ButtonYio moreSettingsButton = buttonFactory.getButton(generateRectangle(0.62, labelTopY - labelHeight, 0.3, 0.05), 199, getString("more"));
        moreSettingsButton.setReactBehavior(ReactBehavior.rbMoreSettings);
        moreSettingsButton.setAnimType(ButtonYio.ANIM_FROM_CENTER);
        moreSettingsButton.disableTouchAnimation();
        moreSettingsButton.setTouchOffset(0.05f * Gdx.graphics.getHeight());

        menuControllerYio.endMenuCreation();
    }
}