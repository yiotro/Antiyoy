package yio.tro.antiyoy.menu.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import yio.tro.antiyoy.KeyboardManager;
import yio.tro.antiyoy.menu.*;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.keyboard.AbstractKbReaction;
import yio.tro.antiyoy.stuff.RectangleYio;

public class SceneUlFilters extends AbstractScene {


    private Reaction rbUserLevels;
    private ButtonYio label;
    private RectangleYio labelPos;
    public CheckButtonYio chkCompleted;
    public CheckButtonYio chkHistorical;
    public CheckButtonYio chkSingleplayer;
    public CheckButtonYio chkMultiplayer;
    private ButtonYio nameFilterButton;
    private AbstractKbReaction kbReaction;
    private Reaction rbByName;
    private CheckButtonYio chkDiplomacy;
    private CheckButtonYio chkFogOfWar;
    private CheckButtonYio chkHidden;


    public SceneUlFilters(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        double h = 0.7;
        labelPos = new RectangleYio(0.1, (0.9 - h) / 2, 0.8, h);
        chkCompleted = null;

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
                String searchName = getFilterPrefs().getString("search_name", "");
                KeyboardManager.getInstance().apply(searchName, kbReaction);
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
        nameFilterButton.setAnimation(Animation.from_center);
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


    private void createCheckButtons() {
        initCheckButtons();
        chkCompleted.appear();
        chkHistorical.appear();
        chkSingleplayer.appear();
        chkMultiplayer.appear();
        chkDiplomacy.appear();
        chkFogOfWar.appear();
        chkHidden.appear();
    }


    private void initCheckButtons() {
        if (chkCompleted != null) return;

        chkCompleted = CheckButtonYio.getFreshCheckButton(menuControllerYio);
        chkCompleted.setParent(label);
        chkCompleted.setAlternativeVisualMode(true);
        chkCompleted.alignTop(0.08);
        chkCompleted.setTitle(getString("completed"));

        chkHistorical = CheckButtonYio.getFreshCheckButton(menuControllerYio);
        chkHistorical.setParent(label);
        chkHistorical.setAlternativeVisualMode(true);
        chkHistorical.alignUnderPreviousElement();
        chkHistorical.setTitle(getString("historical"));

        chkSingleplayer = CheckButtonYio.getFreshCheckButton(menuControllerYio);
        chkSingleplayer.setParent(label);
        chkSingleplayer.setAlternativeVisualMode(true);
        chkSingleplayer.alignUnderPreviousElement();
        chkSingleplayer.setTitle(makeFirstLetterUpperCase(getString("single_player")));

        chkMultiplayer = CheckButtonYio.getFreshCheckButton(menuControllerYio);
        chkMultiplayer.setParent(label);
        chkMultiplayer.setAlternativeVisualMode(true);
        chkMultiplayer.alignUnderPreviousElement();
        chkMultiplayer.setTitle(makeFirstLetterUpperCase(getString("multiplayer")));

        chkDiplomacy = CheckButtonYio.getFreshCheckButton(menuControllerYio);
        chkDiplomacy.setParent(label);
        chkDiplomacy.setAlternativeVisualMode(true);
        chkDiplomacy.alignUnderPreviousElement();
        chkDiplomacy.setTitle(getString("diplomacy"));

        chkFogOfWar = CheckButtonYio.getFreshCheckButton(menuControllerYio);
        chkFogOfWar.setParent(label);
        chkFogOfWar.setAlternativeVisualMode(true);
        chkFogOfWar.alignUnderPreviousElement();
        chkFogOfWar.setTitle(getString("fog_of_war"));

        chkHidden = CheckButtonYio.getFreshCheckButton(menuControllerYio);
        chkHidden.setParent(label);
        chkHidden.setAlternativeVisualMode(true);
        chkHidden.alignUnderPreviousElement();
        chkHidden.setTitle(getString("hidden"));
    }


    public void saveValues() {
        Preferences prefs = getFilterPrefs();

        prefs.putBoolean("completed", chkCompleted.isChecked());
        prefs.putBoolean("historical", chkHistorical.isChecked());
        prefs.putBoolean("single_player", chkSingleplayer.isChecked());
        prefs.putBoolean("multiplayer", chkMultiplayer.isChecked());
        prefs.putBoolean("diplomacy", chkDiplomacy.isChecked());
        prefs.putBoolean("fog_of_war", chkFogOfWar.isChecked());
        prefs.putBoolean("hidden", chkHidden.isChecked());

        prefs.flush();
    }


    private void loadValues() {
        Preferences prefs = getFilterPrefs();

        chkCompleted.setChecked(prefs.getBoolean("completed", true));
        chkHistorical.setChecked(prefs.getBoolean("historical", true));
        chkSingleplayer.setChecked(prefs.getBoolean("single_player", true));
        chkMultiplayer.setChecked(prefs.getBoolean("multiplayer", true));
        chkDiplomacy.setChecked(prefs.getBoolean("diplomacy", true));
        chkFogOfWar.setChecked(prefs.getBoolean("fog_of_war", true));
        chkHidden.setChecked(prefs.getBoolean("hidden", false));
    }


    public static Preferences getFilterPrefs() {
        return Gdx.app.getPreferences("ul_filters");
    }


    private void createLabel() {
        label = buttonFactory.getButton(generateRectangle(labelPos.x, labelPos.y, labelPos.width, labelPos.height), 811, null);
        if (label.notRendered()) {
            label.setTextLine(getString("filters"));
            label.applyNumberOfLines(12);
            menuControllerYio.getButtonRenderer().renderButton(label);
        }
        label.setTouchable(false);
        label.setAnimation(Animation.from_center);
    }


    private String makeFirstLetterUpperCase(String source) {
        return source.substring(0, 1).toUpperCase() + source.substring(1);
    }
}
