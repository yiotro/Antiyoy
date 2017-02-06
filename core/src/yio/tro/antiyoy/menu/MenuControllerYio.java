package yio.tro.antiyoy.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import yio.tro.antiyoy.*;
import yio.tro.antiyoy.behaviors.ReactBehavior;
import yio.tro.antiyoy.factor_yio.FactorYio;
import yio.tro.antiyoy.gameplay.*;
import yio.tro.antiyoy.gameplay.rules.GameRules;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.StringTokenizer;

/**
 * Created by ivan on 22.07.14.
 */
public class MenuControllerYio {

    public static int anim_style;
    public static int SPAWN_ANIM = 2, DESTROY_ANIM = 2;
    public static double SPAWN_SPEED = 1.5, DESTROY_SPEED = 1.5;
    public final YioGdxGame yioGdxGame;
    public final ArrayList<ButtonYio> buttons;
    private final ButtonFactory buttonFactory;
    public ButtonRenderer buttonRenderer;
    public static LanguagesManager languagesManager = LanguagesManager.getInstance();
    TextureRegion unlockedLevelIcon, lockedLevelIcon, openedLevelIcon;
    //    public ScrollerYio scrollerYio;
    public LevelSelector levelSelector;
    public FactorYio infoPanelFactor;
    public ArrayList<SliderYio> sliders;
    public ArrayList<CheckButtonYio> checkButtons;
    private NotificationHolder notificationHolder;
    public SpecialActionController specialActionController;


    public MenuControllerYio(YioGdxGame yioGdxGame) {
        this.yioGdxGame = yioGdxGame;
        buttonFactory = new ButtonFactory(this);
        buttons = new ArrayList<ButtonYio>();
        buttonRenderer = new ButtonRenderer();
        infoPanelFactor = new FactorYio();
        specialActionController = new SpecialActionController(this);
        unlockedLevelIcon = GameView.loadTextureRegionByName("unlocked_level_icon.png", true);
        lockedLevelIcon = GameView.loadTextureRegionByName("locked_level_icon.png", true);
        openedLevelIcon = GameView.loadTextureRegionByName("opened_level_icon.png", true);
        initCheckButtons();
//        initScroller();
        initLevelSelector();
        initSliders();
        notificationHolder = new NotificationHolder();
        applyAnimStyle();

        createMainMenu();
        checkToCreateSingleMessage();
    }


    private void checkToCreateSingleMessage() {
        SingleMessages.load();

//        if (SingleMessages.achikapsRelease) {
//            SingleMessages.achikapsRelease = false;
//            SingleMessages.save();
//            createSingleMessageMenu("achikaps_release");
//            return;
//        }
    }


    private void initCheckButtons() {
        checkButtons = new ArrayList<>();

        for (int i = 0; i < 30; i++) {
            CheckButtonYio.getCheckButton(this, generateRectangle(0, 0, 0, 0), i + 1);
            getCheckButtonById(i + 1).destroy();
        }
    }


    private void initLevelSelector() {
        levelSelector = new LevelSelector(this, 175);
    }


    private void initSliders() {
        sliders = new ArrayList<SliderYio>();
        for (int i = 0; i < 11; i++) {
            SliderYio sliderYio = new SliderYio(this);
            sliderYio.index = i;
            sliders.add(sliderYio);
        }
        sliders.get(2).addListener(sliders.get(1));
        sliders.get(0).addListener(sliders.get(2));
        sliders.get(2).addListener(sliders.get(4));

        sliders.get(0).setValues(0.5f, 1, 3, true, SliderYio.CONFIGURE_SIZE); // map size
        sliders.get(1).setValues(0.2f, 0, 5, false, SliderYio.CONFIGURE_HUMANS); // humans
        sliders.get(2).setValues(0.6, 2, 6, false, SliderYio.CONFIGURE_COLORS); // colors
        sliders.get(3).setValues(0.33, 1, 5, true, SliderYio.CONFIGURE_DIFFICULTY); // difficulty
        sliders.get(4).setValues(0, 0, 6, true, SliderYio.CONFIGURE_COLOR_OFFSET_SKIRMISH); // color offset
        sliders.get(5).setValues(0, 0, 3, true, SliderYio.CONFIGURE_SKIN); // skin
        sliders.get(6).setValues(0.5f, 0, 4, true, SliderYio.CONFIGURE_CAMERA_OFFSET); // camera offset
//        sliders.get(7).setValues(0, 0, 1, false, SliderYio.CONFIGURE_COLOR_OFFSET_SKIRMISH); // autosave
//        sliders.get(8).setValues(0, 0, 1, true, SliderYio.CONFIGURE_ASK_END_TURN); // ask to end turn
        sliders.get(9).setValues(0.5, 0, 6, false, SliderYio.CONFIGURE_SENSITIVITY); // sensitivity
//        sliders.get(10).setValues(0, 0, 1, false, SliderYio.CONFIGURE_COLOR_OFFSET_SKIRMISH); // city names
    }


    private void initScroller() {
        long timeStart = System.currentTimeMillis();

//        scrollerYio = new ScrollerYio(yioGdxGame, generateRectangle(0.05, 0.05, 0.9, 0.8), 0.09f * Gdx.graphics.getHeight(), yioGdxGame.batch);
//        scrollerYio.addLine(openedLevelIcon, languagesManager.getString("how_to_play"));
//        TextureRegion textureRegion;
//        for (int i = 1; i <= YioGdxGame.INDEX_OF_LAST_LEVEL; i++) {
//            if (yioGdxGame.isLevelLocked(i)) textureRegion = lockedLevelIcon;
//            else if (yioGdxGame.isLevelComplete(i)) textureRegion = openedLevelIcon;
//            else textureRegion = unlockedLevelIcon;
//
//            scrollerYio.addLine(textureRegion, scrollerYio.getLevelStringByIndex(languagesManager, i));
//        }
//        if (scrollerYio.selectionIndex > 6) {
//            scrollerYio.pos = (scrollerYio.selectionIndex - 1) * scrollerYio.lineHeight - 0.5f * scrollerYio.lineHeight;
//            scrollerYio.limit();
//        }

        YioGdxGame.say("init scroller: " + (System.currentTimeMillis() - timeStart));
    }


    public void move() {
        infoPanelFactor.move();
        levelSelector.move();
        notificationHolder.move();
        specialActionController.move();
        for (CheckButtonYio checkButton : checkButtons) {
            checkButton.move();
        }
        for (SliderYio sliderYio : sliders) sliderYio.move();
        for (ButtonYio buttonYio : buttons) {
            buttonYio.move();
        }
        for (int i = buttons.size() - 1; i >= 0; i--) {
            if (buttons.get(i).checkToPerformAction()) break;
        }
    }


    public void addMenuBlockToArray(ButtonYio buttonYio) {
        // considered that menu block is not in array at this moment
        ListIterator iterator = buttons.listIterator();
        while (iterator.hasNext()) {
            iterator.next();
        }
        iterator.add(buttonYio);
    }


    private void removeMenuBlockFromArray(ButtonYio buttonYio) {
        ListIterator iterator = buttons.listIterator();
        ButtonYio currentBlock;
        while (iterator.hasNext()) {
            currentBlock = (ButtonYio) iterator.next();
            if (currentBlock == buttonYio) {
                iterator.remove();
                return;
            }
        }
    }


    public ButtonYio getButtonById(int id) { // can return null
        for (ButtonYio buttonYio : buttons) {
            if (buttonYio.id == id) return buttonYio;
        }
        return null;
    }


    public void onResume() {
        ListIterator iterator = buttons.listIterator();
        while (iterator.hasNext()) {
            ButtonYio buttonYio = (ButtonYio) iterator.next();
            if (buttonYio.isVisible()) continue;
            iterator.remove();
        }
    }


    private void loadButtonOnce(ButtonYio buttonYio, String fileName) {
        if (buttonYio.notRendered()) {
            buttonYio.loadTexture(fileName);
        }
    }


    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        for (SliderYio sliderYio : sliders)
            if (sliderYio.touchDown(screenX, screenY)) return true;
        if (levelSelector.touchDown(screenX, screenY, pointer, button)) return true;
        for (CheckButtonYio checkButton : checkButtons) {
            if (checkButton.isTouchable()) {
                if (checkButton.checkTouch(screenX, screenY, pointer, button)) return true;
            }
        }
        for (ButtonYio buttonYio : buttons) {
            if (buttonYio.isTouchable()) {
                if (buttonYio.checkTouch(screenX, screenY, pointer, button)) return true;
            }
        }
        return false;
    }


    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        for (SliderYio sliderYio : sliders)
            if (sliderYio.touchUp(screenX, screenY)) return true;
        if (levelSelector.touchUp(screenX, screenY, pointer, button)) return true;
        return false;
    }


    public void touchDragged(int screenX, int screenY, int pointer) {
        for (SliderYio sliderYio : sliders) sliderYio.touchDrag(screenX, screenY);
        levelSelector.touchDrag(screenX, screenY, pointer);
    }


    private void beginMenuCreation() {
        infoPanelFactor.setValues(1, 0);
        infoPanelFactor.beginDestroying(1, 3);
        levelSelector.destroy();
        for (CheckButtonYio checkButton : checkButtons) {
            checkButton.destroy();
        }
        for (SliderYio sliderYio : sliders) sliderYio.appearFactor.beginDestroying(2, 2);
        for (ButtonYio buttonYio : buttons) {
            buttonYio.destroy();
//            if (buttonLighty.id == 11 && buttonLighty.isVisible()) {
//                buttonLighty.factorModel.stopMoving();
//                buttonLighty.factorModel.beginDestroying(0, 1);
//            }
            if (buttonYio.id == 3 && buttonYio.isVisible()) {
                buttonYio.factorModel.setValues(1, 0);
                buttonYio.factorModel.beginDestroying(1, 2);
            }
            if (buttonYio.id >= 22 && buttonYio.id <= 29 && buttonYio.isVisible()) {
                buttonYio.factorModel.beginDestroying(1, 2.1);
            }
            if (buttonYio.id == 30 && buttonYio.factorModel.get() > 0) {
                buttonYio.factorModel.setValues(1, 0);
                buttonYio.factorModel.beginDestroying(1, 1);
            }
        }
        if (yioGdxGame.gameView != null) yioGdxGame.gameView.beginDestroyProcess();
    }


    private void endMenuCreation() {

    }


    void forceSpawningButtonsToTheEnd() {
        for (ButtonYio buttonYio : buttons) {
            if (buttonYio.factorModel.getGravity() > 0) {
                buttonYio.factorModel.setValues(1, 0);
            }
        }
    }


    public ArrayList<String> getArrayListFromString(String src) {
        ArrayList<String> list = new ArrayList<String>();
        StringTokenizer tokenizer = new StringTokenizer(src, "#");
        while (tokenizer.hasMoreTokens()) {
            list.add(tokenizer.nextToken());
        }
        return list;
    }


    private RectangleYio generateRectangle(double x, double y, double width, double height) {
        return new RectangleYio(x * Gdx.graphics.getWidth(), y * Gdx.graphics.getHeight(), width * Gdx.graphics.getWidth(), height * Gdx.graphics.getHeight());
    }


    private RectangleYio generateSquare(double x, double y, double size) {
        return generateRectangle(x, y, size / YioGdxGame.screenRatio, size);
    }


    public void createMainMenu() {
        beginMenuCreation();

        yioGdxGame.beginBackgroundChange(0, false, true);

        ButtonYio exitButton = buttonFactory.getButton(generateSquare(0.8, 0.87, 0.15 * YioGdxGame.screenRatio), 1, null);
        loadButtonOnce(exitButton, "shut_down.png");
        exitButton.setShadow(true);
        exitButton.setAnimType(ButtonYio.ANIM_UP);
        exitButton.setReactBehavior(ReactBehavior.rbExit);
        exitButton.disableTouchAnimation();

        ButtonYio settingsButton = buttonFactory.getButton(generateSquare(0.05, 0.87, 0.15 * YioGdxGame.screenRatio), 2, null);
        loadButtonOnce(settingsButton, "settings_icon.png");
        settingsButton.setShadow(true);
        settingsButton.setAnimType(ButtonYio.ANIM_UP);
        settingsButton.setReactBehavior(ReactBehavior.rbSettingsMenu);
        settingsButton.disableTouchAnimation();

        ButtonYio playButton = buttonFactory.getButton(generateSquare(0.3, 0.35, 0.4 * YioGdxGame.screenRatio), 3, null);
        loadButtonOnce(playButton, "play_button.png");
        playButton.setReactBehavior(ReactBehavior.rbChooseGameModeMenu);
//        playButton.enableDeltaAnimation();
        playButton.disableTouchAnimation();
        playButton.selectionFactor.setValues(1, 0);

        checkToAddAboutSkinButton();

        endMenuCreation();
    }


    private void checkToAddAboutSkinButton() {
        if (OneTimeInfo.getInstance().aboutShroomArts) return;

        ButtonYio skinButton = buttonFactory.getButton(generateRectangle(0.1, 0.02, 0.8, 0.04), 4, getString("check_shroomarts"));
        skinButton.setReactBehavior(ReactBehavior.rbForceEnableShroomArts);
        skinButton.setAnimType(ButtonYio.ANIM_DOWN);
        skinButton.setTouchOffset(0.1f * GraphicsYio.width);
    }


    private String getString(String key) {
        return languagesManager.getString(key);
    }


    public void createSettingsMenu() {
        beginMenuCreation();

        yioGdxGame.beginBackgroundChange(1, false, true);

        spawnBackButton(190, ReactBehavior.rbCloseSettingsMenu);

        ButtonYio infoButton = buttonFactory.getButton(generateSquare(0.8, 0.89, 0.15 * YioGdxGame.screenRatio), 191, null);
        loadButtonOnce(infoButton, "info_icon.png");
        infoButton.setShadow(true);
        infoButton.setAnimType(ButtonYio.ANIM_UP);
        infoButton.setReactBehavior(ReactBehavior.rbInfo);
        infoButton.disableTouchAnimation();

        ButtonYio mainLabel = buttonFactory.getButton(generateRectangle(0.02, 0.1, 0.96, 0.7), 192, null);
        mainLabel.cleatText();
        ArrayList<String> list = getArrayListFromString(getString("main_label"));
        mainLabel.addManyLines(list);
        int addedEmptyLines = 13 - list.size();
        for (int i = 0; i < addedEmptyLines; i++) {
            mainLabel.addTextLine(" ");
        }
        buttonRenderer.renderButton(mainLabel);
        mainLabel.setTouchable(false);
        mainLabel.setAnimType(ButtonYio.ANIM_FROM_CENTER);

        double checkButtonSize = 0.05;
        double hSize = GraphicsYio.convertToHeight(checkButtonSize);
        double chkX = 0.94 - checkButtonSize;
        double chkY = 0.73;

        CheckButtonYio chkAutosave = CheckButtonYio.getCheckButton(this, generateSquare(chkX, chkY - hSize / 2, hSize), 1);
        chkAutosave.setTouchPosition(generateRectangle(0.02, chkY - hSize * 1.5, 0.96, hSize * 3));

        chkY -= 0.086;
        CheckButtonYio chkSlots = CheckButtonYio.getCheckButton(this, generateSquare(chkX, chkY - hSize / 2, hSize), 2);
        chkSlots.setTouchPosition(generateRectangle(0.02, chkY - hSize * 1.5, 0.96, hSize * 3));

        chkY -= 0.086;
        CheckButtonYio chkTurnEnd = CheckButtonYio.getCheckButton(this, generateSquare(chkX, chkY - hSize / 2, hSize), 3);
        chkTurnEnd.setTouchPosition(generateRectangle(0.02, chkY - hSize * 1.5, 0.96, hSize * 3));

        chkY -= 0.086;
        CheckButtonYio chkCityNames = CheckButtonYio.getCheckButton(this, generateSquare(chkX, chkY - hSize / 2, hSize), 4);
        chkCityNames.setTouchPosition(generateRectangle(0.02, chkY - hSize * 1.5, 0.96, hSize * 3));

        chkY -= 0.086;
        CheckButtonYio chkSound = CheckButtonYio.getCheckButton(this, generateSquare(chkX, chkY - hSize / 2, hSize), 5);
        chkSound.setTouchPosition(generateRectangle(0.02, chkY - hSize * 1.5, 0.96, hSize * 3));

        chkY -= 0.086;
        CheckButtonYio chkTurnLimit = CheckButtonYio.getCheckButton(this, generateSquare(chkX, chkY - hSize / 2, hSize), 6);
        chkTurnLimit.setTouchPosition(generateRectangle(0.02, chkY - hSize * 1.5, 0.96, hSize * 3));

        chkY -= 0.086;
        CheckButtonYio chkLongTapToMove = CheckButtonYio.getCheckButton(this, generateSquare(chkX, chkY - hSize / 2, hSize), 7);
        chkLongTapToMove.setTouchPosition(generateRectangle(0.02, chkY - hSize * 1.5, 0.96, hSize * 3));

        for (int i = 1; i <= 7; i++) {
            getCheckButtonById(i).setAnimType(ButtonYio.ANIM_FROM_CENTER);
        }

        ButtonYio moreSettingsButton = buttonFactory.getButton(generateRectangle(0.65, 0.1, 0.3, 0.05), 199, getString("more"));
        moreSettingsButton.setReactBehavior(ReactBehavior.rbMoreSettings);
        moreSettingsButton.setAnimType(ButtonYio.ANIM_FROM_CENTER);
        moreSettingsButton.disableTouchAnimation();
        moreSettingsButton.setTouchOffset(0.05f * Gdx.graphics.getHeight());

        endMenuCreation();
    }


    public void createMoreSettingsMenu() {
        beginMenuCreation();

        yioGdxGame.beginBackgroundChange(1, false, true);

        spawnBackButton(310, ReactBehavior.rbSettingsMenu);

        ButtonYio resetButton = buttonFactory.getButton(generateRectangle(0.55, 0.9, 0.4, 0.07), 311, getString("menu_reset"));
        resetButton.setReactBehavior(ReactBehavior.rbConfirmReset);
        resetButton.setAnimType(ButtonYio.ANIM_UP);
        resetButton.disableTouchAnimation();

        double panelHeight = 0.14;
        double offset = 0.03;
        double y = 0.9 - offset - panelHeight;

        ButtonYio fieldCameraOffset = buttonFactory.getButton(generateRectangle(0.1, y, 0.8, panelHeight), 314, null);
        renderTextAndSomeEmptyLines(fieldCameraOffset, getString("camera_offset"), 2);
        fieldCameraOffset.setTouchable(false);
        fieldCameraOffset.setAnimType(ButtonYio.ANIM_UP);
        sliders.get(6).appear();
        sliders.get(6).setPos(0.15, y + 0.3 * panelHeight, 0.7, 0);
        sliders.get(6).setVerticalTouchOffset(0.06f * Gdx.graphics.getHeight());

        y -= panelHeight + offset;
        ButtonYio skinLabel = buttonFactory.getButton(generateRectangle(0.1, y, 0.8, panelHeight), 312, null);
        renderTextAndSomeEmptyLines(skinLabel, getString("skin"), 2);
        skinLabel.setTouchable(false);
        skinLabel.setAnimType(ButtonYio.ANIM_UP);
        sliders.get(5).appear();
        sliders.get(5).setPos(0.15, y + 0.3 * panelHeight, 0.7, 0);
        sliders.get(5).setVerticalTouchOffset(0.06f * Gdx.graphics.getHeight());

        y -= panelHeight + offset;
        ButtonYio animStyleButton = buttonFactory.getButton(generateRectangle(0.1, y, 0.8, panelHeight), 313, null);
        renderTextAndSomeEmptyLines(animStyleButton, getString("anim_style"), 2);
        animStyleButton.setTouchable(false);
        animStyleButton.setAnimType(ButtonYio.ANIM_DOWN);
        sliders.get(9).appear();
        sliders.get(9).setPos(0.15, y + 0.3 * panelHeight, 0.7, 0);
        sliders.get(9).setVerticalTouchOffset(0.06f * Gdx.graphics.getHeight());

        panelHeight = 0.17;
        y -= panelHeight + offset;
        ButtonYio chkPanel = buttonFactory.getButton(generateRectangle(0.1, y, 0.8, panelHeight), 316, null);
        if (chkPanel.notRendered()) {
            chkPanel.cleatText();
            chkPanel.addManyLines(getArrayListFromString(getString("more_settings_checks")));
            buttonRenderer.renderButton(chkPanel);
        }
        chkPanel.setTouchable(false);
        chkPanel.setAnimType(ButtonYio.ANIM_DOWN);

        double checkButtonSize = 0.045;
        double hSize = GraphicsYio.convertToHeight(checkButtonSize);
        double chkX = 0.87 - checkButtonSize;
        double chkY = 0.29;

        CheckButtonYio chkWaterTexture = CheckButtonYio.getCheckButton(this, generateSquare(chkX, chkY - hSize / 2, hSize), 10);
        chkWaterTexture.setTouchPosition(generateRectangle(0.1, chkY - hSize * 1.5, 0.8, hSize * 3));
        chkWaterTexture.setAnimType(ButtonYio.ANIM_DOWN);

        ButtonYio chooseLanguageButton = buttonFactory.getButton(generateRectangle(0.1, 0.08, 0.8, 0.07), 315, getString("language"));
        chooseLanguageButton.setReactBehavior(ReactBehavior.rbLanguageMenu);
        chooseLanguageButton.setAnimType(ButtonYio.ANIM_DOWN);

        endMenuCreation();
    }


    public void createLanguageMenu() {
        beginMenuCreation();

        yioGdxGame.beginBackgroundChange(1, false, true);

        spawnBackButton(330, ReactBehavior.rbMoreSettings);

        double buttonHeight = 0.08;
        int langNumber = 9;
        RectangleYio base = new RectangleYio(0.1, 0.5 * (0.9 - buttonHeight * langNumber), 0.8, buttonHeight * langNumber);
        double y = base.y + base.height;

        ButtonYio basePanel = buttonFactory.getButton(generateRectangle(base.x, base.y, base.width, base.height), 331, " ");
        basePanel.setTouchable(false);
        basePanel.onlyShadow = true;
        basePanel.setAnimType(ButtonYio.ANIM_FROM_CENTER);

        y -= buttonHeight;
        ButtonYio engButton = buttonFactory.getButton(generateRectangle(base.x, y, base.width, buttonHeight), 332, "English");
        engButton.setReactBehavior(ReactBehavior.rbSetLanguage);
        engButton.setShadow(false);
        engButton.setAnimType(ButtonYio.ANIM_FROM_CENTER);

        y -= buttonHeight;
        ButtonYio rusButton = buttonFactory.getButton(generateRectangle(base.x, y, base.width, buttonHeight), 333, "Russian");
        rusButton.setReactBehavior(ReactBehavior.rbSetLanguage);
        rusButton.setShadow(false);
        rusButton.setAnimType(ButtonYio.ANIM_FROM_CENTER);

        y -= buttonHeight;
        ButtonYio uaButton = buttonFactory.getButton(generateRectangle(base.x, y, base.width, buttonHeight), 334, "Ukrainian");
        uaButton.setReactBehavior(ReactBehavior.rbSetLanguage);
        uaButton.setShadow(false);
        uaButton.setAnimType(ButtonYio.ANIM_FROM_CENTER);

        y -= buttonHeight;
        ButtonYio gerButton = buttonFactory.getButton(generateRectangle(base.x, y, base.width, buttonHeight), 335, "German");
        gerButton.setReactBehavior(ReactBehavior.rbSetLanguage);
        gerButton.setShadow(false);
        gerButton.setAnimType(ButtonYio.ANIM_FROM_CENTER);

        y -= buttonHeight;
        ButtonYio czeButton = buttonFactory.getButton(generateRectangle(base.x, y, base.width, buttonHeight), 336, "Czech");
        czeButton.setReactBehavior(ReactBehavior.rbSetLanguage);
        czeButton.setShadow(false);
        czeButton.setAnimType(ButtonYio.ANIM_FROM_CENTER);

        y -= buttonHeight;
        ButtonYio polButton = buttonFactory.getButton(generateRectangle(base.x, y, base.width, buttonHeight), 337, "Polish");
        polButton.setReactBehavior(ReactBehavior.rbSetLanguage);
        polButton.setShadow(false);
        polButton.setAnimType(ButtonYio.ANIM_FROM_CENTER);

        y -= buttonHeight;
        ButtonYio itaButton = buttonFactory.getButton(generateRectangle(base.x, y, base.width, buttonHeight), 338, "Italian");
        itaButton.setReactBehavior(ReactBehavior.rbSetLanguage);
        itaButton.setShadow(false);
        itaButton.setAnimType(ButtonYio.ANIM_FROM_CENTER);

        y -= buttonHeight;
        ButtonYio freButton = buttonFactory.getButton(generateRectangle(base.x, y, base.width, buttonHeight), 340, "French");
        freButton.setReactBehavior(ReactBehavior.rbSetLanguage);
        freButton.setShadow(false);
        freButton.setAnimType(ButtonYio.ANIM_FROM_CENTER);

        y -= buttonHeight;
        ButtonYio spButton = buttonFactory.getButton(generateRectangle(base.x, y, base.width, buttonHeight), 341, "Spanish");
        spButton.setReactBehavior(ReactBehavior.rbSetLanguage);
        spButton.setShadow(false);
        spButton.setAnimType(ButtonYio.ANIM_FROM_CENTER);

        endMenuCreation();
    }


    public void createInfoMenu(String key, ReactBehavior backButtonBehavior, int id_offset) {
        beginMenuCreation();

        yioGdxGame.beginBackgroundChange(1, true, false);

        spawnBackButton(id_offset, backButtonBehavior);

        ButtonYio infoPanel = buttonFactory.getButton(generateRectangle(0.05, 0.1, 0.9, 0.7), id_offset + 1, null);

        infoPanel.cleatText();
        ArrayList<String> list = getArrayListFromString(getString(key));
        infoPanel.addManyLines(list);
        int lines = 18;
        int addedEmptyLines = lines - list.size();
        for (int i = 0; i < addedEmptyLines; i++) {
            infoPanel.addTextLine(" ");
        }
        buttonRenderer.renderButton(infoPanel);

        infoPanel.setTouchable(false);
        infoPanel.setAnimType(ButtonYio.ANIM_FROM_CENTER);
        infoPanel.factorModel.beginSpawning(2, 1.5);
//        infoPanel.factorModel.setValues(-0.3, 0);
//        infoPanel.factorModel.beginSpawning(1, 0.3);
//        infoPanelFactor.setValues(-0.3, 0);
//        infoPanelFactor.beginSpawning(1, 0.37);

        endMenuCreation();
    }


    public void createInfoMenu() {
        createInfoMenu("info_array", ReactBehavior.rbMainMenu, 10);

        ButtonYio helpIndexButton = buttonFactory.getButton(generateRectangle(0.5, 0.9, 0.45, 0.07), 38123714, getString("help"));
        helpIndexButton.setReactBehavior(ReactBehavior.rbHelpIndex);
        helpIndexButton.setAnimType(ButtonYio.ANIM_UP);

        ButtonYio moreInfoButton = buttonFactory.getButton(generateRectangle(0.65, 0.1, 0.3, 0.04), 38123717, getString("more"));
        moreInfoButton.setReactBehavior(ReactBehavior.rbSpecialThanksMenu);
        moreInfoButton.setAnimType(ButtonYio.ANIM_FROM_CENTER);
        moreInfoButton.disableTouchAnimation();
        moreInfoButton.setTouchOffset(0.05f * Gdx.graphics.getHeight());
    }


    public void createSpecialThanksMenu() {
        createInfoMenu("special_thanks", ReactBehavior.rbInfo, 312837182);
    }


    public void saveSkirmishSettings() {
        // 3 - difficulty
        // 0 - map size
        // 1 - player number
        // 2 - color number
        Preferences prefs = Gdx.app.getPreferences("skirmish");
        prefs.putInteger("difficulty", sliders.get(3).getCurrentRunnerIndex());
        prefs.putInteger("map_size", sliders.get(0).getCurrentRunnerIndex());
        prefs.putInteger("player_number", sliders.get(1).getCurrentRunnerIndex());
        prefs.putInteger("color_number", sliders.get(2).getCurrentRunnerIndex());
        prefs.flush();
    }


    public void saveMoreSkirmishOptions() {
        Preferences prefs = Gdx.app.getPreferences("skirmish");
        prefs.putInteger("color_offset", sliders.get(4).getCurrentRunnerIndex());
        prefs.putBoolean("slay_rules", getCheckButtonById(16).isChecked());
        prefs.flush();
    }


    public void loadMoreSkirmishOptions() {
        Preferences prefs = Gdx.app.getPreferences("skirmish");
        sliders.get(4).setRunnerValueByIndex(prefs.getInteger("color_offset", 0));
        sliders.get(4).setConfigureType(SliderYio.CONFIGURE_COLOR_OFFSET_SKIRMISH);
        getCheckButtonById(16).setChecked(prefs.getBoolean("slay_rules", false));
    }


    public void loadSkirmishSettings() {
        Preferences prefs = Gdx.app.getPreferences("skirmish");
        sliders.get(3).setRunnerValueByIndex(prefs.getInteger("difficulty", 1));
        sliders.get(0).setRunnerValueByIndex(prefs.getInteger("map_size", 1));
        sliders.get(2).setRunnerValueByIndex(prefs.getInteger("color_number", 2));
        sliders.get(1).setRunnerValueByIndex(prefs.getInteger("player_number", 1));

        loadMoreSkirmishOptions();
    }


    public void createMoreMatchOptionsMenu(ReactBehavior backBehavior) {
        beginMenuCreation();

        yioGdxGame.beginBackgroundChange(2, true, true);

        spawnBackButton(231, backBehavior);

        sliders.get(4).appear();
        sliders.get(4).setPos(0.15, 0.73, 0.7, 0);

        ButtonYio shadow = buttonFactory.getButton(generateRectangle(0.05, 0.45, 0.9, 0.42), 233, null);
        shadow.onlyShadow = true;
        shadow.setAnimType(ButtonYio.ANIM_UP);
        shadow.setTouchable(false);

        ButtonYio firstColorLabel = buttonFactory.getButton(generateRectangle(0.05, 0.67, 0.9, 0.2), 230, null);
        renderTextAndSomeEmptyLines(firstColorLabel, getString("player_color"), 2);
        firstColorLabel.setTouchable(false);
        firstColorLabel.setAnimType(ButtonYio.ANIM_UP);
        firstColorLabel.setShadow(false);

        ButtonYio chkBase = buttonFactory.getButton(generateRectangle(0.05, 0.45, 0.9, 0.25), 232, null);
        if (chkBase.notRendered()) {
            chkBase.cleatText();
            chkBase.addTextLine(" ");
            chkBase.addTextLine(" ");
            chkBase.addTextLine(" ");
            chkBase.addTextLine(getString("slay_rules"));
            chkBase.addTextLine(" ");
            buttonRenderer.renderButton(chkBase);
        }
        chkBase.setTouchable(false);
        chkBase.setAnimType(ButtonYio.ANIM_UP);
        chkBase.setShadow(false);

        double checkButtonSize = 0.05;
        double hSize = GraphicsYio.convertToHeight(checkButtonSize);
        double chkX = 0.88 - checkButtonSize;
        double chkY = 0.53;

        CheckButtonYio chkSlayRules = CheckButtonYio.getCheckButton(this, generateSquare(chkX, chkY - hSize / 2, hSize), 16);
        chkSlayRules.setTouchPosition(generateRectangle(0.05, chkY - hSize * 1.5, 0.9, hSize * 3));
        chkSlayRules.setAnimType(ButtonYio.ANIM_UP);

        endMenuCreation();
    }


    public void createSkirmishMenu() {
        beginMenuCreation();

        yioGdxGame.beginBackgroundChange(2, true, true);

        sliders.get(3).appear();
        sliders.get(3).setPos(0.15, 0.73, 0.7, 0);

        sliders.get(0).appear();
        sliders.get(0).setPos(0.15, 0.52, 0.7, 0);

        sliders.get(1).appear();
        sliders.get(1).setPos(0.15, 0.31, 0.7, 0);
        sliders.get(1).setVerticalTouchOffset(0.06f * Gdx.graphics.getHeight());

        sliders.get(2).appear();
        sliders.get(2).setPos(0.15, 0.1, 0.7, 0);
        sliders.get(2).setVerticalTouchOffset(0.06f * Gdx.graphics.getHeight());

        ButtonYio difficultyLabel = buttonFactory.getButton(generateRectangle(0.1, 0.67, 0.8, 0.2), 88, null);
        renderTextAndSomeEmptyLines(difficultyLabel, getString("difficulty"), 2);
        difficultyLabel.setTouchable(false);
        difficultyLabel.setAnimType(ButtonYio.ANIM_UP);

        ButtonYio mapSizeLabel = buttonFactory.getButton(generateRectangle(0.1, 0.46, 0.8, 0.2), 81, null);
        renderTextAndSomeEmptyLines(mapSizeLabel, getString("map_size"), 2);
        mapSizeLabel.setTouchable(false);
        mapSizeLabel.setAnimType(ButtonYio.ANIM_UP);

        ButtonYio playersLabel = buttonFactory.getButton(generateRectangle(0.1, 0.25, 0.8, 0.2), 84, null);
        renderTextAndSomeEmptyLines(playersLabel, getString("player_number"), 2);
        playersLabel.setTouchable(false);
        playersLabel.setAnimType(ButtonYio.ANIM_DOWN);

        ButtonYio colorsLabel = buttonFactory.getButton(generateRectangle(0.1, 0.04, 0.8, 0.2), 87, null);
        renderTextAndSomeEmptyLines(colorsLabel, getString("color_number"), 2);
        colorsLabel.setTouchable(false);
        colorsLabel.setAnimType(ButtonYio.ANIM_DOWN);

        getButtonById(88).factorModel.beginSpawning(2, 1.5);
        getButtonById(81).factorModel.beginSpawning(2, 1.5);
        getButtonById(84).factorModel.beginSpawning(2, 1.5);
        getButtonById(87).factorModel.beginSpawning(2, 1.5);

        spawnBackButton(80, ReactBehavior.rbBackFromSkirmish);
        getButtonById(80).setTouchable(true);

        ButtonYio startButton = buttonFactory.getButton(generateRectangle(0.55, 0.9, 0.4, 0.07), 83, getString("game_settings_start"));
        startButton.setReactBehavior(ReactBehavior.rbStartGame);
        startButton.setAnimType(ButtonYio.ANIM_UP);
        startButton.disableTouchAnimation();

        ButtonYio moreButton = buttonFactory.getButton(generateRectangle(0.6, 0.2, 0.3, 0.04), 86, getString("more"));
        moreButton.setReactBehavior(ReactBehavior.rbMoreSkirmishOptions);
        moreButton.setAnimType(ButtonYio.ANIM_DOWN);
        moreButton.disableTouchAnimation();

        loadSkirmishSettings();

        endMenuCreation();
    }


    private void renderTextAndSomeEmptyLines(ButtonYio buttonYio, String text, int emptyLines) {
        if (buttonYio.notRendered()) {
            buttonYio.addTextLine(text);
            for (int i = 0; i < emptyLines; i++) {
                buttonYio.addTextLine(" ");
            }
            buttonRenderer.renderButton(buttonYio);
        }
    }


    public void createTestMenu() {
        beginMenuCreation();

        yioGdxGame.beginBackgroundChange(2, false, true);


        spawnBackButton(38721132, ReactBehavior.rbChooseGameModeMenu);

        endMenuCreation();
    }


    private String getEditorSlotString(int index) {
        Preferences prefs = Gdx.app.getPreferences(LevelEditor.EDITOR_PREFS);
        String slotString = prefs.getString("slot" + (index + 1));
        if (slotString.length() < 10) {
            return getString("slot") + " " + (index + 1) + " - " + getString("empty");
        } else {
            return getString("slot") + " " + (index + 1);
        }
    }


    public void updateSaveSlotButton(int slotIndex) {
        ButtonYio slotButton = getButtonById(212 + slotIndex);
        if (slotButton == null) return;
        Preferences prefs = Gdx.app.getPreferences("save_slot" + slotIndex);
        String dateString = prefs.getString("date");
        String detailsInfo = " ";
        if (dateString.length() > 3) {
            slotButton.setTextLine(dateString);
            String diff = YioGdxGame.getDifficultyNameByPower(languagesManager, prefs.getInteger("save_difficulty"));
            if (prefs.getBoolean("save_campaign_mode")) {
                detailsInfo = getString("choose_game_mode_campaign") + "," + prefs.getInteger("save_current_level") + "|" + diff;
            } else {
                detailsInfo = getString("choose_game_mode_skirmish") + "|" + diff;
            }
        } else {
            slotButton.setTextLine(getString("empty"));
        }
        slotButton.addTextLine(detailsInfo);
        buttonRenderer.renderButton(slotButton);
    }


    public void createSaveSlotsMenu(boolean load) {
        beginMenuCreation();

        yioGdxGame.beginBackgroundChange(1, false, true);

        spawnBackButton(210, ReactBehavior.rbInGameMenu);
        if (load) getButtonById(210).setReactBehavior(ReactBehavior.rbChooseGameModeMenu);

        ButtonYio basePanel = buttonFactory.getButton(generateRectangle(0.05, 0.2, 0.9, 0.57), 211, null);
        if (basePanel.notRendered()) {
            basePanel.addTextLine(getString("slots") + ":");
            for (int i = 0; i < 10; i++) {
                basePanel.addTextLine(" ");
            }
            buttonRenderer.renderButton(basePanel);
        }
        basePanel.setTouchable(false);
//        basePanel.onlyShadow = true;
        basePanel.setAnimType(ButtonYio.ANIM_FROM_CENTER);
        basePanel.factorModel.beginSpawning(SPAWN_ANIM, SPAWN_SPEED);

        for (int i = 0; i < 5; i++) {
            ButtonYio slotButton = buttonFactory.getButton(generateRectangle(0.05, 0.6 - 0.1 * (double) i, 0.9, 0.1), 212 + i, null);
            updateSaveSlotButton(i);

            slotButton.setAnimType(ButtonYio.ANIM_FROM_CENTER);
            slotButton.setShadow(false);
            slotButton.setReactBehavior(ReactBehavior.rbSaveGameToSlot);
            slotButton.disableTouchAnimation();
            if (load) slotButton.setReactBehavior(ReactBehavior.rbLoadGameFromSlot);
            slotButton.factorModel.beginSpawning(SPAWN_ANIM, SPAWN_SPEED);
        }

        endMenuCreation();
    }


    public void createEditorSlotMenu() {
        beginMenuCreation();

        yioGdxGame.beginBackgroundChange(2, true, true);

        ButtonYio basePanel = buttonFactory.getButton(generateRectangle(0.05, 0.05, 0.9, 0.8), 139, null);
        basePanel.setTouchable(false);
        basePanel.onlyShadow = true;
        basePanel.setAnimType(ButtonYio.ANIM_FROM_CENTER);
        basePanel.factorModel.beginSpawning(SPAWN_ANIM, SPAWN_SPEED);

        for (int i = 0; i < 8; i++) {
            ButtonYio slotButton = buttonFactory.getButton(generateRectangle(0.05, 0.75 - 0.1 * (double) i, 0.9, 0.1), 131 + i, null);
            slotButton.cleatText();
            slotButton.addTextLine(getEditorSlotString(i));
            slotButton.addTextLine(" ");
            buttonRenderer.renderButton(slotButton);

            slotButton.setAnimType(ButtonYio.ANIM_FROM_CENTER);
            slotButton.setShadow(false);
            slotButton.setReactBehavior(ReactBehavior.rbEditorActionsMenu);
            slotButton.factorModel.beginSpawning(SPAWN_ANIM, SPAWN_SPEED);
        }

        spawnBackButton(130, ReactBehavior.rbChooseGameModeMenu);
    }


    public void createChooseGameModeMenu() {
        beginMenuCreation();

        yioGdxGame.beginBackgroundChange(1, true, true);

        ButtonYio basePanel = buttonFactory.getButton(generateRectangle(0.1, 0.3, 0.8, 0.4), 70, null);
        basePanel.setTouchable(false);
        basePanel.onlyShadow = true;
        basePanel.setAnimType(ButtonYio.ANIM_FROM_CENTER);

        ButtonYio skirmishButton = buttonFactory.getButton(generateRectangle(0.1, 0.62, 0.8, 0.08), 72, getString("choose_game_mode_skirmish"));
        skirmishButton.setReactBehavior(ReactBehavior.rbSkirmishMenu);
        skirmishButton.setShadow(false);
        skirmishButton.setAnimType(ButtonYio.ANIM_FROM_CENTER);

        ButtonYio tutorialButton = buttonFactory.getButton(generateRectangle(0.1, 0.46, 0.8, 0.08), 73, getString("choose_game_mode_tutorial"));
        tutorialButton.setShadow(false);
        tutorialButton.setReactBehavior(ReactBehavior.rbTutorialIndex);
        tutorialButton.setAnimType(ButtonYio.ANIM_FROM_CENTER);

        ButtonYio campaignButton = buttonFactory.getButton(generateRectangle(0.1, 0.38, 0.8, 0.08), 74, getString("choose_game_mode_campaign"));
        campaignButton.setReactBehavior(ReactBehavior.rbCampaignMenu);
        campaignButton.setShadow(false);
        campaignButton.setAnimType(ButtonYio.ANIM_FROM_CENTER);
        campaignButton.disableTouchAnimation();

        ButtonYio loadGameButton = buttonFactory.getButton(generateRectangle(0.1, 0.3, 0.8, 0.08), 75, getString("choose_game_mode_load"));
        loadGameButton.setShadow(false);
        loadGameButton.setAnimType(ButtonYio.ANIM_FROM_CENTER);
        loadGameButton.setReactBehavior(ReactBehavior.rbLoadGame);
        loadGameButton.disableTouchAnimation();

        ButtonYio editorButton = buttonFactory.getButton(generateRectangle(0.1, 0.54, 0.8, 0.08), 77, getString("editor"));
        editorButton.setShadow(false);
        editorButton.setAnimType(ButtonYio.ANIM_FROM_CENTER);
        editorButton.setReactBehavior(ReactBehavior.rbEditorSlotMenu);

        spawnBackButton(76, ReactBehavior.rbMainMenu);

        endMenuCreation();
    }


    public void createTutorialIndexMenu() {
        beginMenuCreation();

        yioGdxGame.beginBackgroundChange(1, false, true);

        ButtonYio basePanel = buttonFactory.getButton(generateRectangle(0.05, 0.37, 0.9, 0.34), 200, null);
        if (basePanel.notRendered()) {
            basePanel.addTextLine(getString("choose_game_mode_tutorial") + ":");
            for (int i = 0; i < 5; i++) {
                basePanel.addTextLine(" ");
            }
            buttonRenderer.renderButton(basePanel);
        }
        basePanel.setTouchable(false);
        basePanel.setAnimType(ButtonYio.ANIM_FROM_CENTER);

        ButtonYio helpIndexButton = buttonFactory.getButton(generateRectangle(0.05, 0.53, 0.9, 0.08), 202, getString("help"));
        helpIndexButton.setReactBehavior(ReactBehavior.rbHelpIndex);
        helpIndexButton.setShadow(false);
        helpIndexButton.setAnimType(ButtonYio.ANIM_FROM_CENTER);

        ButtonYio slayTutorialButton = buttonFactory.getButton(generateRectangle(0.05, 0.45, 0.9, 0.08), 203, getString("normal_rules"));
        slayTutorialButton.setShadow(false);
        slayTutorialButton.setReactBehavior(ReactBehavior.rbTutorialGeneric);
        slayTutorialButton.setAnimType(ButtonYio.ANIM_FROM_CENTER);

        ButtonYio genericTutorialButton = buttonFactory.getButton(generateRectangle(0.05, 0.37, 0.9, 0.08), 204, getString("slay_rules"));
        genericTutorialButton.setReactBehavior(ReactBehavior.rbTutorialSlay);
        genericTutorialButton.setShadow(false);
        genericTutorialButton.setAnimType(ButtonYio.ANIM_FROM_CENTER);

        spawnBackButton(209, ReactBehavior.rbChooseGameModeMenu);

        endMenuCreation();
    }


    public void createHelpIndexMenu() {
        beginMenuCreation();

        yioGdxGame.setGamePaused(true);

        yioGdxGame.beginBackgroundChange(1, false, true);

        ButtonYio basePanel = buttonFactory.getButton(generateRectangle(0.1, 0.21, 0.8, 0.5), 120, null);
        if (basePanel.notRendered()) {
            basePanel.addTextLine(getString("help") + ":");
            for (int i = 0; i < 6; i++) {
                basePanel.addTextLine(" ");
            }
            buttonRenderer.renderButton(basePanel);
        }
        basePanel.setTouchable(false);
        basePanel.setAnimType(ButtonYio.ANIM_FROM_CENTER);

        ButtonYio unitsButton = buttonFactory.getButton(generateRectangle(0.1, 0.53, 0.8, 0.08), 122, getString("help_about_units"));
        unitsButton.setReactBehavior(ReactBehavior.rbArticleUnits);
        unitsButton.setShadow(false);
        unitsButton.setAnimType(ButtonYio.ANIM_FROM_CENTER);

        ButtonYio treesButton = buttonFactory.getButton(generateRectangle(0.1, 0.45, 0.8, 0.08), 123, getString("help_about_trees"));
        treesButton.setShadow(false);
        treesButton.setReactBehavior(ReactBehavior.rbArticleTrees);
        treesButton.setAnimType(ButtonYio.ANIM_FROM_CENTER);

        ButtonYio towersButton = buttonFactory.getButton(generateRectangle(0.1, 0.37, 0.8, 0.08), 124, getString("help_about_towers"));
        towersButton.setReactBehavior(ReactBehavior.rbArticleTowers);
        towersButton.setShadow(false);
        towersButton.setAnimType(ButtonYio.ANIM_FROM_CENTER);

        ButtonYio moneyButton = buttonFactory.getButton(generateRectangle(0.1, 0.29, 0.8, 0.08), 125, getString("help_about_money"));
        moneyButton.setShadow(false);
        moneyButton.setAnimType(ButtonYio.ANIM_FROM_CENTER);
        moneyButton.setReactBehavior(ReactBehavior.rbArticleMoney);

        ButtonYio tacticsButton = buttonFactory.getButton(generateRectangle(0.1, 0.21, 0.8, 0.08), 126, getString("help_about_tactics"));
        tacticsButton.setShadow(false);
        tacticsButton.setAnimType(ButtonYio.ANIM_FROM_CENTER);
        tacticsButton.setReactBehavior(ReactBehavior.rbArticleTactics);

        spawnBackButton(129, ReactBehavior.rbMainMenu);

        endMenuCreation();
    }


    public void saveMoreCampaignOptions() {
        Preferences prefs = Gdx.app.getPreferences("campaign_options");
        prefs.putInteger("color_offset", sliders.get(4).getCurrentRunnerIndex());
        prefs.putBoolean("slay_rules", getCheckButtonById(16).isChecked());
        prefs.flush();
    }


    public void loadMoreCampaignOptions() {
        Preferences prefs = Gdx.app.getPreferences("campaign_options");
        sliders.get(4).setRunnerValueByIndex(prefs.getInteger("color_offset", 1));
        sliders.get(4).setConfigureType(SliderYio.CONFIGURE_COLOR_OFFSET_CAMPAIGN);
        getCheckButtonById(16).setChecked(prefs.getBoolean("slay_rules", false));
    }


    public void createCampaignMenu() {
        beginMenuCreation();

        yioGdxGame.beginBackgroundChange(2, true, true);

        spawnBackButton(20, ReactBehavior.rbChooseGameModeMenu);
        getButtonById(20).setTouchable(true);

        ButtonYio moreOptionsButton = buttonFactory.getButton(generateRectangle(0.75, 0.9, 0.2, 0.07), 21, getString("..."));
        moreOptionsButton.setReactBehavior(ReactBehavior.rbMoreCampaignOptions);
        moreOptionsButton.setAnimType(ButtonYio.ANIM_UP);
        moreOptionsButton.disableTouchAnimation();

//        if (scrollerYio.selectionIndex > 6) {
//            scrollerYio.pos = (scrollerYio.selectionIndex - 1) * scrollerYio.lineHeight - 0.5f * scrollerYio.lineHeight;
//            scrollerYio.limit();
//        }
//        scrollerYio.factorModel.setValues(0.03, 0);
//        scrollerYio.factorModel.beginSpawning(1, 1.5);

        levelSelector.appear();

        endMenuCreation();
    }


    public void hideConfirmEndTurnMenu() {
        for (int i = 320; i <= 322; i++) {
            ButtonYio b = getButtonById(i);
            b.destroy();
            b.factorModel.setValues(0, 0);
        }

        for (int i = 30; i <= 32; i++) {
            ButtonYio buttonYio = getButtonById(i);
            buttonYio.setTouchable(true);
        }
        for (int i = 38; i <= 39; i++) {
            ButtonYio buttonYio = getButtonById(i);
            if (buttonYio == null) continue;
            buttonYio.setTouchable(true);
        }
    }


    public void createConfirmEndTurnMenu() {
        ButtonYio basePanel = buttonFactory.getButton(generateRectangle(0.05, 0.12, 0.9, 0.2), 320, null);
        if (basePanel.notRendered()) {
            basePanel.addTextLine(getString("confirm_end_turn"));
            basePanel.addTextLine(" ");
            basePanel.addTextLine(" ");
            buttonRenderer.renderButton(basePanel);
        }
        basePanel.setTouchable(false);
        basePanel.setAnimType(ButtonYio.ANIM_DOWN);

        ButtonYio confirmButton = buttonFactory.getButton(generateRectangle(0.5, 0.12, 0.45, 0.07), 321, getString("yes"));
        confirmButton.setReactBehavior(ReactBehavior.rbEndTurn);
        confirmButton.setShadow(false);
        confirmButton.setAnimType(ButtonYio.ANIM_DOWN);
        confirmButton.disableTouchAnimation();

        ButtonYio cancelButton = buttonFactory.getButton(generateRectangle(0.05, 0.12, 0.45, 0.07), 322, getString("cancel"));
        cancelButton.setReactBehavior(ReactBehavior.rbHideEndTurnConfirm);
        cancelButton.setShadow(false);
        cancelButton.setAnimType(ButtonYio.ANIM_DOWN);
        cancelButton.disableTouchAnimation();

        for (int i = 30; i <= 32; i++) {
            ButtonYio buttonYio = getButtonById(i);
            buttonYio.setTouchable(false);
        }
        for (int i = 38; i <= 39; i++) {
            ButtonYio buttonYio = getButtonById(i);
            if (buttonYio == null) continue;
            buttonYio.setTouchable(false);
        }
    }


    public void createConfirmResetMenu() {
        beginMenuCreation();

        yioGdxGame.beginBackgroundChange(3, true, true);

        ButtonYio basePanel = buttonFactory.getButton(generateRectangle(0.05, 0.4, 0.9, 0.2), 410, null);
        if (basePanel.notRendered()) {
            basePanel.addTextLine(getString("confirm_reset"));
            basePanel.addTextLine(" ");
            basePanel.addTextLine(" ");
            buttonRenderer.renderButton(basePanel);
        }
        basePanel.setTouchable(false);
        basePanel.setAnimType(ButtonYio.ANIM_FROM_CENTER);

        ButtonYio restartButton = buttonFactory.getButton(generateRectangle(0.5, 0.4, 0.45, 0.07), 411, getString("menu_reset"));
        restartButton.setReactBehavior(ReactBehavior.rbResetProgress);
        restartButton.setShadow(false);
        restartButton.setAnimType(ButtonYio.ANIM_FROM_CENTER);

        ButtonYio cancelButton = buttonFactory.getButton(generateRectangle(0.05, 0.4, 0.45, 0.07), 412, getString("cancel"));
        cancelButton.setReactBehavior(ReactBehavior.rbMoreSettings);
        cancelButton.setShadow(false);
        cancelButton.setAnimType(ButtonYio.ANIM_FROM_CENTER);

        endMenuCreation();
    }


    public void createConfirmRestartMenu() {
        beginMenuCreation();

        yioGdxGame.beginBackgroundChange(3, true, true);

        ButtonYio basePanel = buttonFactory.getButton(generateRectangle(0.05, 0.4, 0.9, 0.2), 220, null);
        if (basePanel.notRendered()) {
            basePanel.addTextLine(getString("confirm_restart"));
            basePanel.addTextLine(" ");
            basePanel.addTextLine(" ");
            buttonRenderer.renderButton(basePanel);
        }
        basePanel.setTouchable(false);
        basePanel.setAnimType(ButtonYio.ANIM_FROM_CENTER);

        ButtonYio restartButton = buttonFactory.getButton(generateRectangle(0.5, 0.4, 0.45, 0.07), 221, getString("in_game_menu_restart"));
        restartButton.setReactBehavior(ReactBehavior.rbRestartGame);
        restartButton.setShadow(false);
        restartButton.setAnimType(ButtonYio.ANIM_FROM_CENTER);

        ButtonYio cancelButton = buttonFactory.getButton(generateRectangle(0.05, 0.4, 0.45, 0.07), 222, getString("cancel"));
        cancelButton.setReactBehavior(ReactBehavior.rbInGameMenu);
        cancelButton.setShadow(false);
        cancelButton.setAnimType(ButtonYio.ANIM_FROM_CENTER);

        endMenuCreation();
    }


    public void createEditorActionsMenu() {
        beginMenuCreation();

        yioGdxGame.setGamePaused(true);

        yioGdxGame.beginBackgroundChange(3, true, true);

        spawnBackButton(189, ReactBehavior.rbEditorSlotMenu);

        ButtonYio basePanel = buttonFactory.getButton(generateRectangle(0.1, 0.3, 0.8, 0.4), 181, null);
        basePanel.setTouchable(false);
        basePanel.onlyShadow = true;

        ButtonYio playButton = buttonFactory.getButton(generateRectangle(0.1, 0.3, 0.8, 0.1), 182, getString("play"));
        playButton.setReactBehavior(ReactBehavior.rbEditorPlay);
        playButton.setShadow(false);

        ButtonYio exportButton = buttonFactory.getButton(generateRectangle(0.1, 0.4, 0.8, 0.1), 183, getString("export"));
        exportButton.setReactBehavior(ReactBehavior.rbEditorExport);
        exportButton.setShadow(false);

        ButtonYio importButton = buttonFactory.getButton(generateRectangle(0.1, 0.5, 0.8, 0.1), 184, getString("import"));
        importButton.setReactBehavior(ReactBehavior.rbEditorImportConfirmMenu);
        importButton.setShadow(false);

        ButtonYio editButton = buttonFactory.getButton(generateRectangle(0.1, 0.6, 0.8, 0.1), 185, getString("edit"));
        editButton.setReactBehavior(ReactBehavior.rbStartEditorMode);
        editButton.setShadow(false);

        for (int i = 181; i <= 185; i++) {
            ButtonYio buttonYio = getButtonById(i);
            buttonYio.setAnimType(ButtonYio.ANIM_FROM_CENTER);
            buttonYio.disableTouchAnimation();
        }

        endMenuCreation();
    }


    public void createEditorImportConfirmationMenu() {
        beginMenuCreation();

        yioGdxGame.beginBackgroundChange(3, true, true);

        ButtonYio labelButton = buttonFactory.getButton(generateRectangle(0.1, 0.45, 0.8, 0.2), 350, null);
        if (labelButton.notRendered()) {
            labelButton.cleatText();
            renderTextAndSomeEmptyLines(labelButton, getString("confirm_import"), 3);
        }
        labelButton.setTouchable(false);
        labelButton.setAnimType(ButtonYio.ANIM_FROM_CENTER);

        ButtonYio noButton = buttonFactory.getButton(generateRectangle(0.1, 0.45, 0.4, 0.08), 351, getString("no"));
        noButton.setReactBehavior(ReactBehavior.rbEditorActionsMenu);
        noButton.setShadow(false);
        noButton.setAnimType(ButtonYio.ANIM_FROM_CENTER);

        ButtonYio yesButton = buttonFactory.getButton(generateRectangle(0.5, 0.45, 0.4, 0.08), 352, getString("yes"));
        yesButton.setReactBehavior(ReactBehavior.rbEditorImport);
        yesButton.setShadow(false);
        yesButton.setAnimType(ButtonYio.ANIM_FROM_CENTER);

        endMenuCreation();
    }


    public void hideAllEditorPanels() {
        hideEditorHexPanel();
        hideEditorObjectPanel();
        hideEditorOptionsPanel();
    }


    public void showEditorOptionsPanel() {
        ButtonYio basePanel = buttonFactory.getButton(generateRectangle(0, 0.07, 1, 0.21), 172, null);
        loadButtonOnce(basePanel, "gray_pixel.png");
        basePanel.setTouchable(false);

        ButtonYio hideButton = buttonFactory.getButton(generateSquare(1 - 0.07 / YioGdxGame.screenRatio, 0.21, 0.07), 171, null);
        loadButtonOnce(hideButton, "hide_panel.png");
        hideButton.setReactBehavior(ReactBehavior.rbHideOptionsPanel);

        ButtonYio clearLevelButton = buttonFactory.getButton(generateRectangle(0, 0.21, 0.8, 0.07), 173, getString("editor_clear"));
        clearLevelButton.setReactBehavior(ReactBehavior.rbClearLevel);

        ButtonYio changePlayerNumberButton = buttonFactory.getButton(generateRectangle(0, 0.14, 0.8, 0.07), 174, getString("player_number"));
        changePlayerNumberButton.setReactBehavior(ReactBehavior.rbEditorChangePlayersNumber);

        ButtonYio changeDifficultyButton = buttonFactory.getButton(generateRectangle(0, 0.07, 0.8, 0.07), 175, getString("difficulty"));
        changeDifficultyButton.setReactBehavior(ReactBehavior.rbEditorChangeDifficulty);

        ButtonYio randomButton = buttonFactory.getButton(generateSquare(1 - 0.07 / YioGdxGame.screenRatio, 0.07, 0.07), 176, "G");
        randomButton.setReactBehavior(ReactBehavior.rbEditorRandomize);

        for (int i = 171; i <= 176; i++) {
            ButtonYio buttonYio = getButtonById(i);
            buttonYio.factorModel.beginSpawning(SPAWN_ANIM, SPAWN_SPEED);
            buttonYio.enableRectangularMask();
            buttonYio.disableTouchAnimation();
            buttonYio.setAnimType(ButtonYio.ANIM_DOWN);
        }
    }


    public void hideEditorOptionsPanel() {
        for (int i = 171; i <= 176; i++) {
            destroyButton(i);
        }
    }


    public void showEditorObjectPanel() {
        ButtonYio basePanel = buttonFactory.getButton(generateRectangle(0, 0.07, 1, 0.07), 160, null);
        loadButtonOnce(basePanel, "gray_pixel.png");
        basePanel.setTouchable(false);

        ButtonYio cancelButton = buttonFactory.getButton(generateSquare(0, 0.07, 0.07), 162, null);
        loadButtonOnce(cancelButton, "cancel_icon.png");
        cancelButton.setReactBehavior(ReactBehavior.rbInputModeSetObject);

        ButtonYio hideButton = buttonFactory.getButton(generateSquare(1 - 0.07 / YioGdxGame.screenRatio, 0.07, 0.07), 161, null);
        loadButtonOnce(hideButton, "hide_panel.png");
        hideButton.setReactBehavior(ReactBehavior.rbHideObjectPanel);

        ButtonYio objectButton;
        for (int i = 0; i < 6; i++) {
            objectButton = buttonFactory.getButton(generateSquare((0.07 + 0.07 * i) / YioGdxGame.screenRatio, 0.07, 0.07), 163 + i, null);
            objectButton.setReactBehavior(ReactBehavior.rbInputModeSetObject);
            switch (i) {
                case 0:
                    loadButtonOnce(objectButton, "field_elements/pine_low.png");
                    break;
                case 1:
                    loadButtonOnce(objectButton, "field_elements/palm_low.png");
                    break;
                case 2:
                    loadButtonOnce(objectButton, "field_elements/house_low.png");
                    break;
                case 3:
                    loadButtonOnce(objectButton, "field_elements/tower_low.png");
                    break;
                case 4:
                    loadButtonOnce(objectButton, "field_elements/man0_low.png");
                    break;
                case 5:
                    loadButtonOnce(objectButton, "field_elements/man1_low.png");
                    break;
            }
        }

        for (int i = 160; i <= 168; i++) {
            ButtonYio buttonYio = getButtonById(i);
            buttonYio.factorModel.beginSpawning(SPAWN_ANIM, SPAWN_SPEED);
            buttonYio.enableRectangularMask();
            buttonYio.disableTouchAnimation();
            buttonYio.setAnimType(ButtonYio.ANIM_DOWN);
        }
    }


    public void hideEditorObjectPanel() {
        for (int i = 160; i <= 168; i++) {
            ButtonYio buttonYio = getButtonById(i);
            if (buttonYio == null) continue;
            buttonYio.destroy();
        }
    }


    public void showEditorHexPanel() {
        ButtonYio basePanel = buttonFactory.getButton(generateRectangle(0, 0.07, 1, 0.21), 12352, null);
        loadButtonOnce(basePanel, "gray_pixel.png");
        basePanel.setTouchable(false);

        ButtonYio cancelButton = buttonFactory.getButton(generateSquare(0, 0.21, 0.07), 12350, null);
        loadButtonOnce(cancelButton, "cancel_icon.png");
        cancelButton.setReactBehavior(ReactBehavior.rbInputModeDelete);

//        ButtonYio hideButton = buttonFactory.getButton(generateSquare(1 - 0.07 / YioGdxGame.screenRatio, 0.21, 0.07), 12351, null);
//        loadButtonOnce(hideButton, "hide_panel.png");
//        hideButton.setReactBehavior(ReactBehavior.rbHideHexPanel);

        ButtonYio filterButton = buttonFactory.getButton(generateRectangle(0, 0.08, 0.5, 0.05), 12353, null);
        filterButton.setReactBehavior(ReactBehavior.rbSwitchFilterOnlyLand);
        yioGdxGame.gameController.getLevelEditor().updateFilterOnlyLandButton();

        ButtonYio hexButton;
        double curVerPos = 0.21;
        double curHorPos = 0.07;
        for (int i = 0; i < 9; i++) {
            if (i == 4) {
                curVerPos = 0.14;
                curHorPos = 0;
            }
            hexButton = buttonFactory.getButton(generateSquare((curHorPos) / YioGdxGame.screenRatio, curVerPos, 0.07), 150 + i, null);
            curHorPos += 0.07;
            hexButton.setReactBehavior(ReactBehavior.rbInputModeHex);
            switch (i) {
                case 0:
                    loadButtonOnce(hexButton, "hex_green.png");
                    break;
                case 1:
                    loadButtonOnce(hexButton, "hex_red.png");
                    break;
                case 2:
                    loadButtonOnce(hexButton, "hex_blue.png");
                    break;
                case 3:
                    loadButtonOnce(hexButton, "hex_cyan.png");
                    break;
                case 4:
                    loadButtonOnce(hexButton, "hex_yellow.png");
                    break;
                case 5:
                    loadButtonOnce(hexButton, "hex_color1.png");
                    break;
                case 6:
                    loadButtonOnce(hexButton, "hex_color2.png");
                    break;
                case 7:
                    loadButtonOnce(hexButton, "hex_color3.png");
                    break;
                case 8:
                    loadButtonOnce(hexButton, "random_hex.png");
                    break;
            }
        }

        for (int i = 12350; i < 12359; i++) {
            ButtonYio buttonYio = getButtonById(i);
            if (buttonYio == null) continue;
            buttonYio.factorModel.beginSpawning(SPAWN_ANIM, SPAWN_SPEED);
            buttonYio.enableRectangularMask();
            buttonYio.disableTouchAnimation();
            buttonYio.setAnimType(ButtonYio.ANIM_DOWN);
        }

        for (int i = 150; i <= 158; i++) {
            ButtonYio buttonYio = getButtonById(i);
            buttonYio.factorModel.beginSpawning(SPAWN_ANIM, SPAWN_SPEED);
            buttonYio.enableRectangularMask();
            buttonYio.disableTouchAnimation();
            buttonYio.setAnimType(ButtonYio.ANIM_DOWN);
        }
    }


    private void destroyButton(int id) {
        ButtonYio buttonYio = getButtonById(id);
        if (buttonYio == null) return;
        buttonYio.destroy();
    }


    public void hideEditorHexPanel() {
        for (int i = 150; i <= 158; i++) {
            destroyButton(i);
        }
        for (int i = 12350; i < 12359; i++) {
            destroyButton(i);
        }
    }


    private void createEditorInstruments() {
        beginMenuCreation();

        showEditorHexPanel();
        showEditorObjectPanel();
        showEditorOptionsPanel();
        hideAllEditorPanels();

        ButtonYio menuButton = buttonFactory.getButton(generateSquare(1 - 0.07 / YioGdxGame.screenRatio, 0.93, 0.07), 140, null);
        loadButtonOnce(menuButton, "menu_icon.png");
        menuButton.setReactBehavior(ReactBehavior.rbEditorActionsMenu);
        menuButton.setAnimType(ButtonYio.ANIM_UP);
        menuButton.enableRectangularMask();
        menuButton.disableTouchAnimation();

        ButtonYio basePanel = buttonFactory.getButton(generateRectangle(0, 0, 1, 0.07), 141, null);
        loadButtonOnce(basePanel, "gray_pixel.png");
        basePanel.setTouchable(false);

        ButtonYio hexButton = buttonFactory.getButton(generateSquare(0, 0, 0.07), 142, null);
        loadButtonOnce(hexButton, "hex_black.png");
        hexButton.setReactBehavior(ReactBehavior.rbShowHexPanel);

        ButtonYio moveButton = buttonFactory.getButton(generateSquare(0.07 / YioGdxGame.screenRatio, 0, 0.07), 143, null);
        loadButtonOnce(moveButton, "icon_move.png");
        moveButton.setReactBehavior(ReactBehavior.rbInputModeMove);

        ButtonYio unitButton = buttonFactory.getButton(generateSquare(2 * 0.07 / YioGdxGame.screenRatio, 0, 0.07), 144, null);
        loadButtonOnce(unitButton, "field_elements/man0_low.png");
        unitButton.setReactBehavior(ReactBehavior.rbShowObjectPanel);

        ButtonYio optionsButton = buttonFactory.getButton(generateSquare(3 * 0.07 / YioGdxGame.screenRatio, 0, 0.07), 145, null);
        loadButtonOnce(optionsButton, "opened_level_icon.png");
        optionsButton.setReactBehavior(ReactBehavior.rbShowOptionsPanel);

        for (int i = 141; i <= 145; i++) {
            ButtonYio buttonYio = getButtonById(i);
            buttonYio.factorModel.beginSpawning(SPAWN_ANIM, SPAWN_SPEED);
            buttonYio.enableRectangularMask();
            buttonYio.disableTouchAnimation();
            buttonYio.setAnimType(ButtonYio.ANIM_DOWN);
        }

        endMenuCreation();
    }


    public void createGameOverlay() {
        if (GameRules.inEditorMode) {
            createEditorInstruments();
            return;
        }

        beginMenuCreation();

        ButtonYio inGameMenuButton = buttonFactory.getButton(generateSquare(1 - 0.07 / YioGdxGame.screenRatio, 0.93, 0.07), 30, null);
        loadButtonOnce(inGameMenuButton, "menu_icon.png");
        inGameMenuButton.setReactBehavior(ReactBehavior.rbInGameMenu);
        inGameMenuButton.setAnimType(ButtonYio.ANIM_UP);
        inGameMenuButton.enableRectangularMask();
        inGameMenuButton.disableTouchAnimation();

        ButtonYio endTurnButton = buttonFactory.getButton(generateSquare(1 - 0.07 / YioGdxGame.screenRatio, 0, 0.07), 31, null);
        loadButtonOnce(endTurnButton, "end_turn.png");
        endTurnButton.setReactBehavior(ReactBehavior.rbEndTurn);
        endTurnButton.setAnimType(ButtonYio.ANIM_DOWN);
        endTurnButton.enableRectangularMask();
        endTurnButton.disableTouchAnimation();
        endTurnButton.setPressSound(SoundControllerYio.soundEndTurn);

        ButtonYio undoButton = buttonFactory.getButton(generateSquare(0, 0, 0.07), 32, null);
        loadButtonOnce(undoButton, "undo_icon.png");
        undoButton.setReactBehavior(ReactBehavior.rbUndo);
        undoButton.setAnimType(ButtonYio.ANIM_DOWN);
        undoButton.enableRectangularMask();
        undoButton.disableTouchAnimation();

//        ButtonYio debugButton = buttonFactory.getButton(generateSquare(0.72, 0, 0.07), 3128773, "Q");
//        debugButton.setReactBehavior(ReactBehavior.rbDebugActions);
//        debugButton.setAnimType(ButtonYio.ANIM_DOWN);
//        debugButton.disableTouchAnimation();

        endMenuCreation();
    }


    public void showBuildButtons() {
        ButtonYio unitButton = getButtonById(39);
        if (unitButton == null) { // init
            unitButton = buttonFactory.getButton(generateSquare(0.57, 0, 0.13 * YioGdxGame.screenRatio), 39, null);
            unitButton.setReactBehavior(ReactBehavior.rbBuildUnit);
            unitButton.setAnimType(ButtonYio.ANIM_DOWN);
            unitButton.enableRectangularMask();
        }
        loadUnitButtonTexture(unitButton);
        unitButton.setTouchable(true);
        unitButton.setTouchOffset(0.05f * GraphicsYio.width);
        unitButton.factorModel.beginSpawning(3, 2);

        ButtonYio towerButton = getButtonById(38);
        if (towerButton == null) { // init
            towerButton = buttonFactory.getButton(generateSquare(0.30, 0, 0.13 * YioGdxGame.screenRatio), 38, null);
            towerButton.setReactBehavior(ReactBehavior.RB_BUILD_SOLID_OBJECT);
            towerButton.setAnimType(ButtonYio.ANIM_DOWN);
            towerButton.enableRectangularMask();
        }
        loadBuildObjectButton(towerButton);
        towerButton.setTouchable(true);
        towerButton.setTouchOffset(0.05f * GraphicsYio.width);
        towerButton.factorModel.beginSpawning(3, 2);

        ButtonYio coinButton = getButtonById(37);
        if (coinButton == null) { // init
            coinButton = buttonFactory.getButton(generateSquare(0, 0.93, 0.07), 37, null);
            coinButton.setAnimType(ButtonYio.ANIM_UP);
            coinButton.setPressSound(SoundControllerYio.soundCoin);
            coinButton.enableRectangularMask();
            coinButton.disableTouchAnimation();
        }
        loadCoinButtonTexture(coinButton);
        coinButton.factorModel.beginSpawning(3, 2);
        coinButton.setTouchable(true);
        coinButton.setReactBehavior(ReactBehavior.rbShowColorStats);
    }


    public void removeButtonById(int id) {
        ListIterator<ButtonYio> iterator = buttons.listIterator();
        while (iterator.hasNext()) {
            ButtonYio button = iterator.next();
            if (button.id == id) {
                iterator.remove();
                return;
            }
        }
    }


    private void loadBuildObjectButton(ButtonYio objectButton) {
        if (GameRules.slay_rules) {
            loadTowerButtonTexture(objectButton);
        } else {
            loadFarmButtonTexture(objectButton);
        }
    }


    private void loadFarmButtonTexture(ButtonYio farmButton) {
        if (Settings.isShroomArtsEnabled()) {
            loadButtonOnce(farmButton, "skins/ant/field_elements/house.png");
            return;
        }

        loadButtonOnce(farmButton, "field_elements/house.png");
    }


    private void loadTowerButtonTexture(ButtonYio towerButton) {
        if (Settings.isShroomArtsEnabled()) {
            loadButtonOnce(towerButton, "skins/ant/field_elements/tower.png");
            return;
        }

        loadButtonOnce(towerButton, "field_elements/tower.png");
    }


    private void loadUnitButtonTexture(ButtonYio unitButton) {
        if (Settings.isShroomArtsEnabled()) {
            loadButtonOnce(unitButton, "skins/ant/field_elements/man0.png");
            return;
        }

        loadButtonOnce(unitButton, "field_elements/man0.png");
    }


    private void loadCoinButtonTexture(ButtonYio coinButton) {
        if (Settings.isShroomArtsEnabled()) {
            loadButtonOnce(coinButton, "skins/ant/coin.png");
            return;
        }

        loadButtonOnce(coinButton, "coin.png");
    }


    public void hideBuildButtons() {
        destroyButton(39);
        destroyButton(38);
        destroyButton(37);
        yioGdxGame.gameController.selectionController.getSelMoneyFactor().beginDestroying(2, 8);
    }


    public void createInGameMenu() {
        beginMenuCreation();

        yioGdxGame.beginBackgroundChange(3, true, true);

        ButtonYio basePanel = buttonFactory.getButton(generateRectangle(0.1, 0.3, 0.8, 0.4), 40, null);
        basePanel.setTouchable(false);
        basePanel.onlyShadow = true;
        basePanel.setAnimType(ButtonYio.ANIM_FROM_CENTER);

        ButtonYio mainMenuButton = buttonFactory.getButton(generateRectangle(0.1, 0.3, 0.8, 0.1), 42, getString("in_game_menu_main_menu"));
        mainMenuButton.setReactBehavior(ReactBehavior.rbMainMenu);
        mainMenuButton.setShadow(false);
        mainMenuButton.setAnimType(ButtonYio.ANIM_FROM_CENTER);
        mainMenuButton.disableTouchAnimation();

        ButtonYio chooseLevelButton = buttonFactory.getButton(generateRectangle(0.1, 0.4, 0.8, 0.1), 43, getString("in_game_menu_save"));
        chooseLevelButton.setReactBehavior(ReactBehavior.rbSaveGame);
        chooseLevelButton.setShadow(false);
        chooseLevelButton.setAnimType(ButtonYio.ANIM_FROM_CENTER);
        chooseLevelButton.disableTouchAnimation();

        ButtonYio restartButton = buttonFactory.getButton(generateRectangle(0.1, 0.5, 0.8, 0.1), 44, getString("in_game_menu_restart"));
        restartButton.setReactBehavior(ReactBehavior.rbRestartGame);
        restartButton.setShadow(false);
        restartButton.setAnimType(ButtonYio.ANIM_FROM_CENTER);
        restartButton.disableTouchAnimation();

        ButtonYio resumeButton = buttonFactory.getButton(generateRectangle(0.1, 0.6, 0.8, 0.1), 45, getString("in_game_menu_resume"));
        resumeButton.setReactBehavior(ReactBehavior.rbResumeGame);
        resumeButton.setShadow(false);
        resumeButton.setAnimType(ButtonYio.ANIM_FROM_CENTER);
        resumeButton.disableTouchAnimation();

        endMenuCreation();
    }


    public void showColorStats() {
        yioGdxGame.gameController.selectionController.deselectAll();
        getButtonById(30).setTouchable(false);
        getButtonById(31).setTouchable(false);
        getButtonById(32).setTouchable(false);

        ButtonYio showPanel = buttonFactory.getButton(generateRectangle(0, 0.1, 1, 0.41), 56321, null);
        showPanel.setTouchable(false);
        showPanel.setAnimType(ButtonYio.ANIM_COLLAPSE_DOWN);
        showPanel.factorModel.beginSpawning(3, 1);

        ButtonYio okButton = buttonFactory.getButton(generateRectangle(0.6, 0.1, 0.4, 0.07), 56322, getString("end_game_ok"));
        okButton.setShadow(false);
        okButton.setReactBehavior(ReactBehavior.rbHideColorStats);
        okButton.setAnimType(ButtonYio.ANIM_COLLAPSE_DOWN);
        okButton.factorModel.beginSpawning(3, 1);
    }


    public void hideColorStats() {
        getButtonById(30).setTouchable(true);
        getButtonById(31).setTouchable(true);
        getButtonById(32).setTouchable(true);

        getButtonById(56321).destroy();
        getButtonById(56321).factorModel.beginDestroying(1, 3);
        getButtonById(56322).destroy();
        getButtonById(56322).factorModel.beginDestroying(1, 3);
    }


    public void applyAnimStyle() {
        anim_style = 2;
        switch (anim_style) {
            case 0: // simple
                SPAWN_ANIM = 0;
                SPAWN_SPEED = 10;
                DESTROY_ANIM = 0;
                DESTROY_SPEED = 1;
                break;
            case 1: // lighty
                SPAWN_ANIM = 1;
                SPAWN_SPEED = 1.5;
                DESTROY_ANIM = 1;
                DESTROY_SPEED = 3;
                break;
            case 2: // material
                SPAWN_ANIM = 2;
                SPAWN_SPEED = 1.5;
                DESTROY_ANIM = 2;
                DESTROY_SPEED = 1.5;
                break;
            case 3: // playful
                SPAWN_ANIM = 4;
                SPAWN_SPEED = 1.5;
                DESTROY_ANIM = 1;
                DESTROY_SPEED = 3;
                break;
        }
    }


    public void createTutorialTip(ArrayList<String> text) {
        getButtonById(31).setTouchable(false);
        getButtonById(32).setTouchable(false);

        for (int i = 0; i < 2; i++) text.add(" ");
        ButtonYio textPanel = buttonFactory.getButton(generateRectangle(0, 0, 1, 1), 50, null);
        textPanel.setPosition(generateRectangle(0, 0.1, 1, 0.05 * (double) text.size()));
        textPanel.cleatText();
        textPanel.addManyLines(text);
        buttonRenderer.renderButton(textPanel);
        textPanel.setTouchable(false);
        textPanel.setAnimType(ButtonYio.ANIM_COLLAPSE_DOWN);
        textPanel.factorModel.beginSpawning(3, 1);

        ButtonYio okButton = buttonFactory.getButton(generateRectangle(0.6, 0.1, 0.4, 0.07), 53, getString("end_game_ok"));
        okButton.setShadow(false);
        okButton.setReactBehavior(ReactBehavior.rbCloseTutorialTip);
        okButton.setAnimType(ButtonYio.ANIM_COLLAPSE_DOWN);
        okButton.factorModel.beginSpawning(3, 1);
    }


    public void createTutorialTipWithFixedHeight(ArrayList<String> text, int lines) {
        getButtonById(31).setTouchable(false);
        getButtonById(32).setTouchable(false);

        for (int i = 0; i < 2; i++) text.add(" ");
        ButtonYio textPanel = buttonFactory.getButton(generateRectangle(0, 0, 1, 1), 50, null);
        textPanel.setPosition(generateRectangle(0, 0.1, 1, 0.3));
        textPanel.cleatText();
        textPanel.addManyLines(text);
        while (textPanel.text.size() < lines) {
            textPanel.addTextLine(" ");
        }
        buttonRenderer.renderButton(textPanel);
        textPanel.setTouchable(false);
        textPanel.setAnimType(ButtonYio.ANIM_COLLAPSE_DOWN);
        textPanel.factorModel.beginSpawning(3, 1);

        ButtonYio okButton = buttonFactory.getButton(generateRectangle(0.6, 0.1, 0.4, 0.07), 53, getString("end_game_ok"));
        okButton.setShadow(false);
        okButton.setReactBehavior(ReactBehavior.rbCloseTutorialTip);
        okButton.setAnimType(ButtonYio.ANIM_COLLAPSE_DOWN);
        okButton.factorModel.beginSpawning(3, 1);
    }


    public void closeTutorialTip() {
        getButtonById(50).destroy();
        getButtonById(53).destroy();
        getButtonById(50).factorModel.beginDestroying(1, 3);
        getButtonById(53).factorModel.beginDestroying(1, 3);

        if (getButtonById(54) != null) { // help index button
            getButtonById(54).destroy();
            getButtonById(54).factorModel.beginDestroying(1, 3);
        }

        getButtonById(30).setTouchable(true);
        getButtonById(31).setTouchable(true);
        getButtonById(32).setTouchable(true);
    }


    public void addWinButtonToTutorialTip() {
        ButtonYio winButton = buttonFactory.getButton(generateRectangle(0, 0.1, 0.5, 0.05), 54, null);
        winButton.setTextLine(getString("win_game"));
        buttonRenderer.renderButton(winButton);
        winButton.setShadow(false);
        winButton.setReactBehavior(ReactBehavior.rbWinGame);
        winButton.setAnimType(ButtonYio.ANIM_COLLAPSE_DOWN);
        winButton.factorModel.beginSpawning(3, 1);

        ButtonYio okButton = getButtonById(53);
        okButton.setPosition(generateRectangle(0.5, 0.1, 0.5, 0.05));
        okButton.setTextLine(getString("continue"));
        okButton.setReactBehavior(ReactBehavior.rbRefuseEarlyGameEnd);
        buttonRenderer.renderButton(okButton);
    }


    public void addHelpButtonToTutorialTip() {
        ButtonYio helpButton = buttonFactory.getButton(generateRectangle(0, 0.1, 0.6, 0.07), 54, null);
        helpButton.setTextLine(getString("help"));
        buttonRenderer.renderButton(helpButton);
        helpButton.setShadow(false);
        helpButton.setReactBehavior(ReactBehavior.rbHelpIndex);
        helpButton.setAnimType(ButtonYio.ANIM_COLLAPSE_DOWN);
        helpButton.factorModel.beginSpawning(3, 1);
    }


    public void showNotification(String message, boolean autoHide) {
        ButtonYio notificationButton = buttonFactory.getButton(generateRectangle(0, 0.95, 1, 0.05), 3614, null);
        notificationButton.setTextLine(message);
        buttonRenderer.renderButton(notificationButton);
        notificationButton.setAnimType(ButtonYio.ANIM_UP);
        notificationButton.enableRectangularMask();
        notificationButton.setTouchable(false);
        notificationButton.factorModel.beginSpawning(3, 1);
        notificationButton.setShadow(false);

        removeMenuBlockFromArray(notificationButton);
        addMenuBlockToArray(notificationButton);

        notificationHolder.setButton(notificationButton);
        notificationHolder.setAutoHide(autoHide);
    }


    public void hideNotification() {
        ButtonYio notificationButton = getButtonById(3614);
        if (notificationButton == null) return;
        notificationButton.destroy();
        notificationButton.factorModel.beginDestroying(1, 3);
    }


    public boolean notificationIsDestroying() {
        ButtonYio notificationButton = getButtonById(3614);
        if (notificationButton == null) return false;
        if (notificationButton.factorModel.getGravity() < 0) return true;
        return false;
    }


    private String getColorNameByIndex(int index, String keyModifier) {
        index = yioGdxGame.gameController.getColorIndexWithOffset(index);
        switch (index) {
            default:
            case 6:
            case 0:
                return getString("green" + keyModifier);
            case 1:
            case 5:
                return getString("red" + keyModifier);
            case 2:
                return getString("magenta" + keyModifier);
            case 3:
                return getString("cyan" + keyModifier);
            case 4:
                return getString("yellow" + keyModifier);
            case 7:
                return getString("gray" + keyModifier);
        }
    }


    public void createAfterGameMenu(int whoWon, boolean playerIsWinner) {
        beginMenuCreation();

        yioGdxGame.setGamePaused(true);
        yioGdxGame.beginBackgroundChange(3, true, false);

        String message = "ERROR";
        if (playerIsWinner) {
            message = getColorNameByIndex(whoWon, "_player") + " " +
                    getString("player") + " " +
                    getString("won") + ".";
        } else {
            message = getColorNameByIndex(whoWon, "_ai") + " " +
                    getString("ai") + " " +
                    getString("won") + ".";
        }
        if (CampaignController.getInstance().completedCampaignLevel(whoWon))
            message = getString("level_complete");
        if (DebugFlags.CHECKING_BALANCE_MODE && yioGdxGame.gamesPlayed() % 50 == 0) {
            YioGdxGame.say(yioGdxGame.gamesPlayed() + " : " + yioGdxGame.getBalanceIndicatorString());
        }
        ButtonYio textPanel = buttonFactory.getButton(generateRectangle(0.05, 0.4, 0.9, 0.2), 60, null);
        textPanel.cleatText();
        textPanel.addTextLine(message);
        textPanel.addTextLine("");
        textPanel.addTextLine("");
        buttonRenderer.renderButton(textPanel);
        textPanel.setTouchable(false);
        textPanel.setAnimType(ButtonYio.ANIM_FROM_CENTER);

        ButtonYio okButton = buttonFactory.getButton(generateRectangle(0.55, 0.4, 0.4, 0.07), 62, null);
        if (CampaignController.getInstance().completedCampaignLevel(whoWon))
            okButton.setTextLine(getString("next"));
        else {
            if (playerIsWinner) {
                okButton.setTextLine(getString("end_game_ok"));
            } else {
                okButton.setTextLine(getString("end_game_okay"));
            }
        }
        buttonRenderer.renderButton(okButton);
        okButton.setShadow(false);
        okButton.setReactBehavior(ReactBehavior.rbChooseGameModeMenu);
        if (CampaignController.getInstance().completedCampaignLevel(whoWon))
            okButton.setReactBehavior(ReactBehavior.rbNextLevel);
        okButton.setAnimType(ButtonYio.ANIM_FROM_CENTER);

        ButtonYio statisticsButton = buttonFactory.getButton(generateRectangle(0.05, 0.4, 0.5, 0.07), 61, getString("statistics"));
        statisticsButton.setShadow(false);
        statisticsButton.setReactBehavior(ReactBehavior.rbStatisticsMenu);
        statisticsButton.setAnimType(ButtonYio.ANIM_FROM_CENTER);

        endMenuCreation();
    }


    public void forceDyingButtonsToEnd() {
        for (ButtonYio button : buttons) {
            if (button.factorModel.getGravity() < 0) {
                button.factorModel.setValues(0, 0);
            }
        }
    }


    public void createStatisticsMenu(Statistics statistics) {
        beginMenuCreation();

        yioGdxGame.beginBackgroundChange(0, false, true);

        spawnBackButton(111, ReactBehavior.rbChooseGameModeMenu);

        ButtonYio textPanel = buttonFactory.getButton(generateRectangle(0.05, 0.1, 0.9, 0.7), 112, null);
        textPanel.cleatText();
        textPanel.addTextLine(getString("statistics") + ":");
        textPanel.addTextLine(getString("turns_made") + " " + statistics.turnsMade);
        textPanel.addTextLine(getString("units_died") + " " + statistics.unitsDied);
        textPanel.addTextLine(getString("units_produced") + " " + statistics.unitsProduced);
        textPanel.addTextLine(getString("money_spent") + " " + statistics.moneySpent);
        textPanel.addTextLine(getString("time") + " " + statistics.getTimeString());
        for (int i = 0; i < 10; i++) {
            textPanel.addTextLine("");
        }
        buttonRenderer.renderButton(textPanel);
        textPanel.setTouchable(false);
        textPanel.setAnimType(ButtonYio.ANIM_FROM_CENTER);

        endMenuCreation();
    }


    private void spawnBackButton(int id, ReactBehavior reactBehavior) {
        ButtonYio backButton = buttonFactory.getButton(generateRectangle(0.05, 0.9, 0.4, 0.07), id, null);
        loadButtonOnce(backButton, "back_icon.png");
        backButton.setShadow(true);
        backButton.setAnimType(ButtonYio.ANIM_UP);
        backButton.setReactBehavior(reactBehavior);
        backButton.setTouchOffset(0.05f * Gdx.graphics.getHeight());
        yioGdxGame.registerBackButtonId(id);
    }


    void updateScrollerLinesBeforeIndex(int index) {
        for (int i = 0; i <= index; i++) {
            updateScrollerLineTexture(i);
        }
    }


    private void updateScrollerLineTexture(int index) {
        if (index < 0 || index > CampaignController.INDEX_OF_LAST_LEVEL) return;
        TextureRegion textureRegion;
        if (yioGdxGame.isLevelComplete(index)) textureRegion = openedLevelIcon;
        else if (yioGdxGame.isLevelLocked(index)) textureRegion = lockedLevelIcon;
        else textureRegion = unlockedLevelIcon;
//        scrollerYio.icons.set(index, textureRegion);
//        scrollerYio.updateCacheLine(index);
    }


    public void createSingleMessageMenu(String key) {
        beginMenuCreation();

        yioGdxGame.beginBackgroundChange(0, false, false);
        ButtonYio textPanel = buttonFactory.getButton(generateRectangle(0.05, 0.25, 0.9, 0.5), 400, null);
        if (textPanel.notRendered()) {
            textPanel.cleatText();
            ArrayList<String> list = getArrayListFromString(getString(key));
            textPanel.addManyLines(list);
            int lines = 12;
            int addedEmptyLines = lines - list.size();
            for (int i = 0; i < addedEmptyLines; i++) {
                textPanel.addTextLine(" ");
            }
            buttonRenderer.renderButton(textPanel);
        }
        textPanel.setTouchable(false);
        textPanel.setAnimType(ButtonYio.ANIM_FROM_CENTER);

        ButtonYio okButton = buttonFactory.getButton(generateRectangle(0.65, 0.25, 0.3, 0.07), 401, "Ok");
        okButton.setReactBehavior(ReactBehavior.rbMainMenu);
        okButton.setAnimType(ButtonYio.ANIM_FROM_CENTER);

        endMenuCreation();
    }


    public void createGameHackerMenu() {
        beginMenuCreation();

        yioGdxGame.beginBackgroundChange(0, false, false);
        ArrayList<String> text = new ArrayList<String>();
        text.add("Это специальный билд");
        text.add("Для game_hacker_#9");
        text.add("");
        text.add(":D");
        ButtonYio textPanel = buttonFactory.getButton(generateRectangle(0.1, 0.4, 0.8, 0.3), 312783, null);
        if (textPanel.notRendered()) {
            textPanel.addManyLines(text);
            for (int i = 0; i < (7 - text.size()); i++) textPanel.addTextLine(" ");
            buttonRenderer.renderButton(textPanel);
        }
        textPanel.setTouchable(false);
        textPanel.setAnimType(ButtonYio.ANIM_DEFAULT);

        ButtonYio okButton = buttonFactory.getButton(generateRectangle(0.1, 0.3, 0.8, 0.1), 8912732, "Ok");
        okButton.setReactBehavior(ReactBehavior.rbMainMenu);
        okButton.setAnimType(ButtonYio.ANIM_DEFAULT);

        endMenuCreation();
    }


    public void addCheckButtonToArray(CheckButtonYio checkButtonYio) {
        checkButtons.listIterator().add(checkButtonYio);
    }


    public CheckButtonYio getCheckButtonById(int id) {
        for (CheckButtonYio checkButton : checkButtons) {
            if (checkButton.id == id) {
                return checkButton;
            }
        }
        return null;
    }


    public void createExceptionReport(Exception exception) {
        beginMenuCreation();
        yioGdxGame.setGamePaused(true);

        int lineWidth = 44;
        int lineNumber = 25;
        ArrayList<String> text = new ArrayList<String>();
        String title = "Error : " + exception.toString();
        if (title.length() > lineWidth) title = title.substring(0, lineWidth);
        text.add(title);
        String temp;
        int start, end;
        boolean go;
        for (int i = 0; i < exception.getStackTrace().length; i++) {
            temp = exception.getStackTrace()[i].toString();
            start = 0;
            go = true;
            while (go) {
                end = start + lineWidth;
                if (end > temp.length() - 1) {
                    go = false;
                    end = temp.length() - 1;
                }
                try {
                    text.add(temp.substring(start, end));
                } catch (ArrayIndexOutOfBoundsException e) {
                }
                start = end + 1;
                if (text.size() > lineNumber) go = false;
            }
        }

        // generate special font for this purpose
        FileHandle fontFile = Gdx.files.internal("font.otf");
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(fontFile);
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        int FONT_SIZE = (int) (0.021 * Gdx.graphics.getHeight());
        parameter.size = FONT_SIZE;
        parameter.characters = Fonts.getAllCharacters();
        parameter.flip = true;
        BitmapFont font = generator.generateFont(parameter);

        ButtonYio textPanel = buttonFactory.getButton(generateRectangle(0.1, 0.2, 0.8, 0.7), 6731267, null);
        if (textPanel.notRendered()) {
            textPanel.addManyLines(text);
            for (int i = 0; i < (lineNumber - text.size()); i++) textPanel.addTextLine(" ");
            buttonRenderer.renderButton(textPanel, font, FONT_SIZE);
        }
        textPanel.setTouchable(false);

        ButtonYio okButton = buttonFactory.getButton(generateRectangle(0.1, 0.1, 0.8, 0.1), 73612321, "Ok");
        okButton.setReactBehavior(ReactBehavior.rbInGameMenu);

        endMenuCreation();
    }


    public void clear() {
        buttons.clear();
        initSliders();
    }


    public void close() {

    }
}
