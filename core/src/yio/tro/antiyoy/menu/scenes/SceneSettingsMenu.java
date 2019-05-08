package yio.tro.antiyoy.menu.scenes;

import com.badlogic.gdx.Gdx;
import yio.tro.antiyoy.MusicManager;
import yio.tro.antiyoy.SettingsManager;
import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.YioGdxGame;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.CheckButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.stuff.LanguagesManager;

public class SceneSettingsMenu extends AbstractScene{


    private double labelHeight;
    private double labelTopY;
    Reaction rbChkSound;
    private CheckButtonYio chkSound;
    private ButtonYio mainLabel;
    private CheckButtonYio chkAutosave;
    private CheckButtonYio chkMusic;
    private CheckButtonYio chkTurnEnd;
    private CheckButtonYio chkCityNames;
    private Reaction rbBack;
    private Reaction rbChkMusic;


    public SceneSettingsMenu(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        initReactions();
    }


    private void initReactions() {
        rbChkSound = new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                applyValues();
                MusicManager.getInstance().onMusicStatusChanged();
            }
        };

        rbChkMusic = new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                applyValues();
                MusicManager.getInstance().onMusicStatusChanged();
            }
        };

        rbBack = new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                onDestroy();
                Scenes.sceneMainMenu.create();
            }
        };
    }


    public void onDestroy() {
        applyValues();
        SettingsManager.getInstance().saveMainSettings();
    }


    @Override
    public void create() {
        menuControllerYio.beginMenuCreation();

        menuControllerYio.getYioGdxGame().beginBackgroundChange(1, false, true);

        menuControllerYio.spawnBackButton(190, rbBack);

        createInfoButton();
        createMainLabel();
        createCheckButtons();
        createMoreSettingsButton();

        loadValues();

        menuControllerYio.endMenuCreation();
    }


    private void createMoreSettingsButton() {
        ButtonYio moreSettingsButton = buttonFactory.getButton(generateRectangle(0.62, labelTopY - labelHeight, 0.3, 0.05), 199, getString("more"));
        moreSettingsButton.setReaction(Reaction.rbMoreSettings);
        moreSettingsButton.setAnimation(Animation.FROM_CENTER);
        moreSettingsButton.disableTouchAnimation();
        moreSettingsButton.setShadow(false);
        moreSettingsButton.setVisualHook(mainLabel);
        moreSettingsButton.setTouchOffset(0.05f * Gdx.graphics.getHeight());
    }


    private void createCheckButtons() {
        double checkButtonSize = 0.05;
        double hSize = GraphicsYio.convertToHeight(checkButtonSize);
        double chkX = 0.9 - checkButtonSize;
        double chkY = labelTopY - 0.07;

        chkAutosave = CheckButtonYio.getCheckButton(menuControllerYio, generateSquare(chkX, chkY - hSize / 2, hSize), 1);
        chkAutosave.setTouchPosition(generateRectangle(0.04, chkY - hSize * 1.5, 0.92, hSize * 3));

        chkY -= 0.086;
        chkMusic = CheckButtonYio.getCheckButton(menuControllerYio, generateSquare(chkX, chkY - hSize / 2, hSize), 2);
        chkMusic.setTouchPosition(generateRectangle(0.04, chkY - hSize * 1.5, 0.92, hSize * 3));
        chkMusic.setReaction(rbChkMusic);

        chkY -= 0.086;
        chkTurnEnd = CheckButtonYio.getCheckButton(menuControllerYio, generateSquare(chkX, chkY - hSize / 2, hSize), 3);
        chkTurnEnd.setTouchPosition(generateRectangle(0.04, chkY - hSize * 1.5, 0.92, hSize * 3));

        chkY -= 0.086;
        chkCityNames = CheckButtonYio.getCheckButton(menuControllerYio, generateSquare(chkX, chkY - hSize / 2, hSize), 4);
        chkCityNames.setTouchPosition(generateRectangle(0.04, chkY - hSize * 1.5, 0.92, hSize * 3));

        chkY -= 0.086;
        chkSound = CheckButtonYio.getCheckButton(menuControllerYio, generateSquare(chkX, chkY - hSize / 2, hSize), 5);
        chkSound.setTouchPosition(generateRectangle(0.04, chkY - hSize * 1.5, 0.92, hSize * 3));
        chkSound.setReaction(rbChkSound);

        for (int i = 1; i <= 5; i++) {
            menuControllerYio.getCheckButtonById(i).setAnimation(Animation.FROM_CENTER);
        }
    }


    private void createMainLabel() {
        labelHeight = 0.52;
        labelTopY = 0.7;
        mainLabel = buttonFactory.getButton(generateRectangle(0.04, labelTopY - labelHeight, 0.92, labelHeight), 192, null);
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
        ButtonYio infoButton = buttonFactory.getButton(generateSquare(0.95 - 0.07 / YioGdxGame.screenRatio, 0.9, 0.07), 191, null);
        menuControllerYio.loadButtonOnce(infoButton, "menu/info_icon.png");
        infoButton.setShadow(true);
        infoButton.setAnimation(Animation.UP);
        infoButton.setTouchOffset(0.05f * GraphicsYio.width);
        infoButton.setReaction(Reaction.RB_ABOUT_GAME);
        infoButton.disableTouchAnimation();
    }


    public void loadValues() {
        chkAutosave.setChecked(SettingsManager.autosave);
        chkMusic.setChecked(SettingsManager.musicEnabled);
        chkCityNames.setChecked(SettingsManager.cityNamesEnabled);
        chkTurnEnd.setChecked(SettingsManager.askToEndTurn);
        chkSound.setChecked(SettingsManager.soundEnabled);
    }


    public void applyValues() {
        SettingsManager.autosave = chkAutosave.isChecked();
        SettingsManager.musicEnabled = chkMusic.isChecked();
        SettingsManager.cityNamesEnabled = chkCityNames.isChecked();
        SettingsManager.askToEndTurn = chkTurnEnd.isChecked();
        SettingsManager.soundEnabled = chkSound.isChecked();
    }
}