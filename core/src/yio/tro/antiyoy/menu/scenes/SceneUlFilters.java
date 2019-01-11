package yio.tro.antiyoy.menu.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.CheckButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.keyboard.AbstractKbReaction;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.RectangleYio;

public class SceneUlFilters extends AbstractScene {


    private Reaction rbUserLevels;
    private ButtonYio label;
    private RectangleYio labelPos;
    private ButtonYio titleButton;
    public CheckButtonYio chkCompleted;
    public CheckButtonYio chkHistorical;
    public CheckButtonYio chkSingleplayer;
    public CheckButtonYio chkMultiplayer;
    private ButtonYio nameFilterButton;
    private AbstractKbReaction kbReaction;
    private Reaction rbByName;


    public SceneUlFilters(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        double h = 0.5;
        labelPos = new RectangleYio(0.1, (0.9 - h) / 2, 0.8, h);

        initReactions();
    }


    private void initReactions() {
        rbUserLevels = new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                saveValues();
                Scenes.sceneUserLevels.create();
            }
        };

        kbReaction = new AbstractKbReaction() {
            @Override
            public void onInputFromKeyboardReceived(String input) {
                Preferences filterPrefs = getFilterPrefs();
                String searchName = filterPrefs.getString("search_name", "");
                if (searchName.equals(input)) return;

                filterPrefs.putString("search_name", input);
                filterPrefs.flush();
                updateNameFilterButton();
            }
        };

        rbByName = new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                Scenes.sceneKeyboard.create();
                Scenes.sceneKeyboard.setReaction(kbReaction);

                String searchName = getFilterPrefs().getString("search_name", "");
                if (searchName.length() > 0) {
                    Scenes.sceneKeyboard.setValue(searchName);
                }
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
        createNameFilterButton();

        loadValues();
    }


    private void createNameFilterButton() {
        float bw = 0.7f;
        float bh = 0.06f;
        RectangleYio pos = generateRectangle(labelPos.x + labelPos.width / 2 - bw / 2, labelPos.y + 0.02, bw, bh);
        nameFilterButton = buttonFactory.getButton(pos, 813, null);
        updateNameFilterButton();
        nameFilterButton.setAnimation(Animation.FROM_CENTER);
        nameFilterButton.disableTouchAnimation();
        nameFilterButton.setReaction(rbByName);
    }


    public void updateNameFilterButton() {
        nameFilterButton.cleatText();

        String searchName = getFilterPrefs().getString("search_name", "");
        if (searchName.length() == 0) {
            nameFilterButton.addTextLine(getString("by_name"));
        } else {
            nameFilterButton.addTextLine("'" + searchName + "'");
        }

        menuControllerYio.buttonRenderer.renderButton(nameFilterButton);
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
        double hTouchSize = hSize * 1;
        double chkX = 0.8 - checkButtonSize;
        double chkY = 0.575;

        chkCompleted = CheckButtonYio.getCheckButton(menuControllerYio, generateSquare(chkX, chkY - hSize / 2, hSize), 14);
        chkCompleted.setTouchPosition(generateRectangle(labelPos.x, chkY - hTouchSize, labelPos.width, 2 * hTouchSize));
        chkCompleted.setAnimation(Animation.FROM_CENTER);
        chkY -= hTouchSize * 2;

        chkHistorical = CheckButtonYio.getCheckButton(menuControllerYio, generateSquare(chkX, chkY - hSize / 2, hSize), 15);
        chkHistorical.setTouchPosition(generateRectangle(labelPos.x, chkY - hTouchSize, labelPos.width, 2 * hTouchSize));
        chkHistorical.setAnimation(Animation.FROM_CENTER);
        chkY -= hTouchSize * 2;

        chkSingleplayer = CheckButtonYio.getCheckButton(menuControllerYio, generateSquare(chkX, chkY - hSize / 2, hSize), 38122);
        chkSingleplayer.setTouchPosition(generateRectangle(labelPos.x, chkY - hTouchSize, labelPos.width, 2 * hTouchSize));
        chkSingleplayer.setAnimation(Animation.FROM_CENTER);
        chkY -= hTouchSize * 2;

        chkMultiplayer = CheckButtonYio.getCheckButton(menuControllerYio, generateSquare(chkX, chkY - hSize / 2, hSize), 38123);
        chkMultiplayer.setTouchPosition(generateRectangle(labelPos.x, chkY - hTouchSize, labelPos.width, 2 * hTouchSize));
        chkMultiplayer.setAnimation(Animation.FROM_CENTER);
        chkY -= hTouchSize * 2;
    }


    public void saveValues() {
        Preferences prefs = getFilterPrefs();

        prefs.putBoolean("completed", chkCompleted.isChecked());
        prefs.putBoolean("historical", chkHistorical.isChecked());
        prefs.putBoolean("single_player", chkSingleplayer.isChecked());
        prefs.putBoolean("multiplayer", chkMultiplayer.isChecked());

        prefs.flush();
    }


    private void loadValues() {
        Preferences prefs = getFilterPrefs();

        chkCompleted.setChecked(prefs.getBoolean("completed", true));
        chkHistorical.setChecked(prefs.getBoolean("historical", true));
        chkSingleplayer.setChecked(prefs.getBoolean("single_player", true));
        chkMultiplayer.setChecked(prefs.getBoolean("multiplayer", true));
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
            label.addTextLine(makeFirstLetterUpperCase(getString("single_player")));
            label.addTextLine(makeFirstLetterUpperCase(getString("multiplayer")));
            label.addTextLine(" ");
            label.addTextLine(" ");
            label.addTextLine(" ");
            label.addTextLine(" ");

            label.setTextOffset(0.09f * GraphicsYio.width);
            menuControllerYio.getButtonRenderer().renderButton(label);
        }
        label.setTouchable(false);
        label.setAnimation(Animation.FROM_CENTER);
    }


    private String makeFirstLetterUpperCase(String source) {
        return source.substring(0, 1).toUpperCase() + source.substring(1);
    }
}
