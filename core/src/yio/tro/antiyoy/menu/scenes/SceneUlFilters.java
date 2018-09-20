package yio.tro.antiyoy.menu.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.CheckButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.RectangleYio;

public class SceneUlFilters extends AbstractScene{


    private final Reaction rbUserLevels;
    private ButtonYio label;
    private RectangleYio labelPos;
    private ButtonYio titleButton;
    public CheckButtonYio chkCompleted;
    public CheckButtonYio chkHistorical;


    public SceneUlFilters(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        double h = 0.3;
        labelPos = new RectangleYio(0.1, (0.9 - h) / 2, 0.8, h);

        rbUserLevels = new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                saveValues();
                Scenes.sceneUserLevels.create();
            }
        };
    }


    @Override
    public void create() {
        menuControllerYio.beginMenuCreation();

        menuControllerYio.getYioGdxGame().beginBackgroundChange(2, true, true);

        menuControllerYio.spawnBackButton(810, rbUserLevels);

        createInternals();

        menuControllerYio.endMenuCreation();
    }


    private void createInternals() {
        createLabel();
        createTitleButton();
        createCheckButtons();

        loadValues();
    }


    private void createTitleButton() {
        double h = 0.07;
        RectangleYio pos = generateRectangle(
                labelPos.x,
                labelPos.y + labelPos.height - h,
                0.4,
                h
        );
        titleButton = buttonFactory.getButton(pos, 812, null);
        if (titleButton.notRendered()) {
            titleButton.setTextLine(getString("filters"));
            titleButton.loadCustomBackground("big_button_background.png");
            menuControllerYio.buttonRenderer.renderButton(titleButton);
        }
        titleButton.setAnimation(Animation.FROM_CENTER);
        titleButton.setVisualHook(label);
        titleButton.setIgnorePauseResume(true);
        titleButton.setTouchable(false);
    }


    private void createCheckButtons() {
        double checkButtonSize = 0.04;
        double hSize = GraphicsYio.convertToHeight(checkButtonSize);
        double chkX = 0.8 - checkButtonSize;
        double chkY = 0.475;
        double hTouchSize = hSize * 1;

        chkCompleted = CheckButtonYio.getCheckButton(menuControllerYio, generateSquare(chkX, chkY - hSize / 2, hSize), 14);
        chkCompleted.setTouchPosition(generateRectangle(labelPos.x, chkY - hTouchSize, labelPos.width, 2 * hTouchSize));
        chkCompleted.setAnimation(Animation.FROM_CENTER);
        chkY -= hTouchSize * 2;

        chkHistorical = CheckButtonYio.getCheckButton(menuControllerYio, generateSquare(chkX, chkY - hSize / 2, hSize), 15);
        chkHistorical.setTouchPosition(generateRectangle(labelPos.x, chkY - hTouchSize, labelPos.width, 2 * hTouchSize));
        chkHistorical.setAnimation(Animation.FROM_CENTER);
        chkY -= hTouchSize * 2;
    }


    public void saveValues() {
        Preferences prefs = getFilterPrefs();

        prefs.putBoolean("completed", chkCompleted.isChecked());
        prefs.putBoolean("historical", chkHistorical.isChecked());

        prefs.flush();
    }


    private void loadValues() {
        Preferences prefs = getFilterPrefs();

        chkCompleted.setChecked(prefs.getBoolean("completed", true));
        chkHistorical.setChecked(prefs.getBoolean("historical", true));
    }


    public static Preferences getFilterPrefs() {
        return Gdx.app.getPreferences("ul_filters");
    }


    private void createLabel() {
        label = buttonFactory.getButton(generateRectangle(labelPos.x, labelPos.y, labelPos.width, labelPos.height), 811, null);
        if (label.notRendered()) {
            label.cleatText();
            label.addTextLine(" ");
            label.addTextLine(" ");
            label.addTextLine(getString("completed"));
            label.addTextLine(getString("historical"));
            label.addTextLine(" ");
            label.addTextLine(" ");

            label.setTextOffset(0.09f * GraphicsYio.width);
            menuControllerYio.getButtonRenderer().renderButton(label);
        }
        label.setTouchable(false);
        label.setAnimation(Animation.FROM_CENTER);
    }
}
