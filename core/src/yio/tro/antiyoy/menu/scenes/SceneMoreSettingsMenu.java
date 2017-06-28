package yio.tro.antiyoy.menu.scenes;

import com.badlogic.gdx.Gdx;
import yio.tro.antiyoy.GraphicsYio;
import yio.tro.antiyoy.LanguagesManager;
import yio.tro.antiyoy.menu.behaviors.ReactBehavior;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.CheckButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;

public class SceneMoreSettingsMenu extends AbstractScene{


    private double panelHeight;
    private double offset;
    private double y;
    private double checkButtonSize;
    private double hSize;
    private double chkX;
    private double chkY;


    public SceneMoreSettingsMenu(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
    }


    @Override
    public void create() {
        menuControllerYio.beginMenuCreation();

        changeBackground();
        createBackButton();
        createResetButton();

        initMetrics();
        createSkinLabel();
        createAnimStyleLabel();

        createCheckPanel();
        initCheckMetrics();
        createCheckButtons();

        createChooseLanguageButton();

        menuControllerYio.endMenuCreation();
    }


    private void createChooseLanguageButton() {
        ButtonYio chooseLanguageButton = buttonFactory.getButton(generateRectangle(0.1, 0.08, 0.8, 0.07), 315, getString("language"));
        chooseLanguageButton.setReactBehavior(ReactBehavior.rbLanguageMenu);
        chooseLanguageButton.setAnimType(ButtonYio.ANIM_DOWN);
    }


    private void createCheckButtons() {
        CheckButtonYio chkWaterTexture = CheckButtonYio.getCheckButton(menuControllerYio, generateSquare(chkX, chkY - hSize / 2, hSize), 10);
        chkWaterTexture.setTouchPosition(generateRectangle(0.1, chkY - hSize * 1.5, 0.8, hSize * 3));
        chkWaterTexture.setAnimType(ButtonYio.ANIM_DOWN);

        chkY -= 0.086;
        CheckButtonYio chkTurnLimit = CheckButtonYio.getCheckButton(menuControllerYio, generateSquare(chkX, chkY - hSize / 2, hSize), 6);
        chkTurnLimit.setTouchPosition(generateRectangle(0.1, chkY - hSize * 1.5, 0.8, hSize * 3));
        chkTurnLimit.setAnimType(ButtonYio.ANIM_DOWN);

        chkY -= 0.086;
        CheckButtonYio chkLongTapToMove = CheckButtonYio.getCheckButton(menuControllerYio, generateSquare(chkX, chkY - hSize / 2, hSize), 7);
        chkLongTapToMove.setTouchPosition(generateRectangle(0.1, chkY - hSize * 1.5, 0.8, hSize * 3));
        chkLongTapToMove.setAnimType(ButtonYio.ANIM_DOWN);
    }


    private void initCheckMetrics() {
        checkButtonSize = 0.045;
        hSize = GraphicsYio.convertToHeight(checkButtonSize);
        chkX = 0.87 - checkButtonSize;
        chkY = 0.465;
    }


    private void createCheckPanel() {
        panelHeight = 0.3;
        y -= panelHeight + offset;
        ButtonYio chkPanel = buttonFactory.getButton(generateRectangle(0.1, y, 0.8, panelHeight), 316, null);

        if (chkPanel.notRendered()) {
            chkPanel.cleatText();
            chkPanel.addTextLine(" ");
            chkPanel.addTextLine(LanguagesManager.getInstance().getString("water_texture"));
            chkPanel.addTextLine(" ");
            chkPanel.addTextLine(LanguagesManager.getInstance().getString("limited_turns"));
            chkPanel.addTextLine(" ");
            chkPanel.addTextLine(LanguagesManager.getInstance().getString("hold_to_march"));
            chkPanel.addTextLine(" ");
            menuControllerYio.getButtonRenderer().renderButton(chkPanel);
        }

        chkPanel.setTouchable(false);
        chkPanel.setAnimType(ButtonYio.ANIM_DOWN);
    }


    private void createAnimStyleLabel() {
        ButtonYio animStyleButton = buttonFactory.getButton(generateRectangle(0.1, y, 0.8, panelHeight), 313, null);
        menuControllerYio.renderTextAndSomeEmptyLines(animStyleButton, getString("anim_style"), 2);
        animStyleButton.setTouchable(false);
        animStyleButton.setAnimType(ButtonYio.ANIM_DOWN);
        menuControllerYio.getSliders().get(9).appear();
        menuControllerYio.getSliders().get(9).setPos(0.15, y + 0.3 * panelHeight, 0.7, 0);
        menuControllerYio.getSliders().get(9).setVerticalTouchOffset(0.06f * Gdx.graphics.getHeight());
    }


    private void createSkinLabel() {
        ButtonYio skinLabel = buttonFactory.getButton(generateRectangle(0.1, y, 0.8, panelHeight), 312, null);
        menuControllerYio.renderTextAndSomeEmptyLines(skinLabel, getString("skin"), 2);
        skinLabel.setTouchable(false);
        skinLabel.setAnimType(ButtonYio.ANIM_UP);
        menuControllerYio.getSliders().get(5).appear();
        menuControllerYio.getSliders().get(5).setPos(0.15, y + 0.3 * panelHeight, 0.7, 0);
        menuControllerYio.getSliders().get(5).setVerticalTouchOffset(0.06f * Gdx.graphics.getHeight());
        y -= panelHeight + offset;
    }


    private void changeBackground() {
        menuControllerYio.getYioGdxGame().beginBackgroundChange(1, false, true);
    }


    private void createBackButton() {
        menuControllerYio.spawnBackButton(310, ReactBehavior.rbSettingsMenu);
    }


    private void initMetrics() {
        panelHeight = 0.14;
        offset = 0.03;
        y = 0.9 - offset - panelHeight;
    }


    private void createResetButton() {
        ButtonYio resetButton = buttonFactory.getButton(generateRectangle(0.55, 0.9, 0.4, 0.07), 311, getString("menu_reset"));
        resetButton.setReactBehavior(ReactBehavior.rbConfirmReset);
        resetButton.setAnimType(ButtonYio.ANIM_UP);
        resetButton.disableTouchAnimation();
    }
}