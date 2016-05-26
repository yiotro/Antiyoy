package yio.tro.antiyoy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import yio.tro.antiyoy.behaviors.ReactBehavior;
import yio.tro.antiyoy.factor_yio.FactorYio;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.StringTokenizer;

/**
 * Created by ivan on 22.07.14.
 */
public class MenuControllerLighty {

    public static int anim_style;
    public static int SPAWN_ANIM = 2, DESTROY_ANIM = 2;
    public static double SPAWN_SPEED = 1.5, DESTROY_SPEED = 1.5;
    public final YioGdxGame yioGdxGame;
    final ArrayList<ButtonLighty> buttons;
    private final ButtonFactory buttonFactory;
    private ButtonRenderer buttonRenderer;
    public LanguagesManager languagesManager;
    TextureRegion unlockedLevelIcon, lockedLevelIcon, openedLevelIcon;
    public ScrollerYio scrollerYio;
    FactorYio infoPanelFactor;
    ArrayList<SliderYio> sliders;
    private NotificationHolder notificationHolder;


    public MenuControllerLighty(YioGdxGame yioGdxGame) {
        this.yioGdxGame = yioGdxGame;
        buttonFactory = new ButtonFactory(this);
        buttons = new ArrayList<ButtonLighty>();
        buttonRenderer = new ButtonRenderer();
        infoPanelFactor = new FactorYio();
        languagesManager = LanguagesManager.getInstance();
        unlockedLevelIcon = GameView.loadTextureRegionByName("unlocked_level_icon.png", true);
        lockedLevelIcon = GameView.loadTextureRegionByName("locked_level_icon.png", true);
        openedLevelIcon = GameView.loadTextureRegionByName("opened_level_icon.png", true);
        initScroller();
        initSliders();
        notificationHolder = new NotificationHolder();

        createMainMenu();
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
        sliders.get(0).setValues(0.5f, 1, 3, true, SliderYio.CONFIGURE_SIZE); // map size
        sliders.get(1).setValues(0.2f, 0, 5, false, SliderYio.CONFIGURE_HUMANS); // humans
        sliders.get(2).setValues(0.6, 3, 6, false, SliderYio.CONFIGURE_COLORS); // colors
        sliders.get(3).setValues(0.33, 1, 4, true, SliderYio.CONFIGURE_DIFFICULTY); // difficulty
        sliders.get(4).setValues(1, 0, 1, true, SliderYio.CONFIGURE_ON_OFF); // sound
        sliders.get(5).setValues(0, 0, 2, true, SliderYio.CONFIGURE_SKIN); // hex skin
        sliders.get(6).setValues(0, 0, 1, true, SliderYio.CONFIGURE_SLOT_NUMBER); // slot number
        sliders.get(7).setValues(0, 0, 1, false, SliderYio.CONFIGURE_ON_OFF); // autosave
        sliders.get(8).setValues(0, 0, 1, true, SliderYio.CONFIGURE_ASK_END_TURN); // ask to end turn
        sliders.get(9).setValues(0.75, 0, 3, false, SliderYio.CONFIGURE_ANIM_STYLE); // animation style
        sliders.get(10).setValues(0, 0, 1, false, SliderYio.CONFIGURE_ON_OFF); // city names
    }


    private void initScroller() {
        long timeStart = System.currentTimeMillis();

        scrollerYio = new ScrollerYio(yioGdxGame, generateRectangle(0.05, 0.05, 0.9, 0.8), 0.09f * Gdx.graphics.getHeight(), yioGdxGame.batch);
//        if (scrollerYio.selectionIndex == 0) scrollerYio.addLine(unlockedLevelIcon, languagesManager.getString("how_to_play"));
//        else scrollerYio.addLine(openedLevelIcon, languagesManager.getString("how_to_play"));
        scrollerYio.addLine(openedLevelIcon, languagesManager.getString("how_to_play"));
//        int si = scrollerYio.selectionIndex;
        TextureRegion textureRegion;
        for (int i = 1; i <= YioGdxGame.INDEX_OF_LAST_LEVEL; i++) {
//            if (i == si) textureRegion = unlockedLevelIcon;
//            if (i >= si + 1) textureRegion = lockedLevelIcon;
            if (scrollerYio.isLevelLocked(i)) textureRegion = lockedLevelIcon;
            else if (scrollerYio.isLevelComplete(i)) textureRegion = openedLevelIcon;
            else textureRegion = unlockedLevelIcon;

            scrollerYio.addLine(textureRegion, scrollerYio.getLevelStringByIndex(languagesManager, i));
        }
        if (scrollerYio.selectionIndex > 6) {
            scrollerYio.pos = (scrollerYio.selectionIndex - 1) * scrollerYio.lineHeight - 0.5f * scrollerYio.lineHeight;
            scrollerYio.limit();
        }

        YioGdxGame.say("init scroller: " + (System.currentTimeMillis() - timeStart));
    }


    public void move() {
        infoPanelFactor.move();
        scrollerYio.move();
        notificationHolder.move();
        for (SliderYio sliderYio : sliders) sliderYio.move();
        for (ButtonLighty buttonLighty : buttons) {
            buttonLighty.move();
        }
        for (int i = buttons.size() - 1; i >= 0; i--) {
            if (buttons.get(i).checkToPerformAction()) break;
        }
    }


    public void addMenuBlockToArray(ButtonLighty buttonLighty) {
        // considered that menu block is not in array at this moment
        ListIterator iterator = buttons.listIterator();
        while (iterator.hasNext()) {
            iterator.next();
        }
        iterator.add(buttonLighty);
    }


    private void removeMenuBlockFromArray(ButtonLighty buttonLighty) {
        ListIterator iterator = buttons.listIterator();
        ButtonLighty currentBlock;
        while (iterator.hasNext()) {
            currentBlock = (ButtonLighty) iterator.next();
            if (currentBlock == buttonLighty) {
                iterator.remove();
                return;
            }
        }
    }


    public ButtonLighty getButtonById(int id) { // can return null
        for (ButtonLighty buttonLighty : buttons) {
            if (buttonLighty.id == id) return buttonLighty;
        }
        return null;
    }


    private void loadButtonOnce(ButtonLighty buttonLighty, String fileName) {
        if (buttonLighty.notRendered()) {
            buttonLighty.loadTexture(fileName);
        }
    }


    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        for (SliderYio sliderYio : sliders)
            if (sliderYio.touchDown(screenX, screenY)) return true;
        if (scrollerYio.touchDown(screenX, screenY, pointer, button)) return true;
        for (ButtonLighty buttonLighty : buttons) {
            if (buttonLighty.isTouchable()) {
                if (buttonLighty.checkTouch(screenX, screenY, pointer, button)) return true;
            }
        }
        return false;
    }


    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        for (SliderYio sliderYio : sliders)
            if (sliderYio.touchUp(screenX, screenY)) return true;
        if (scrollerYio.touchUp(screenX, screenY, pointer, button)) return true;
        return false;
    }


    public void touchDragged(int screenX, int screenY, int pointer) {
        for (SliderYio sliderYio : sliders) sliderYio.touchDrag(screenX, screenY);
        scrollerYio.touchDragged(screenX, screenY, pointer);
    }


    private void beginMenuCreation() {
        infoPanelFactor.setValues(1, 0);
        infoPanelFactor.beginDestroying(1, 3);
        scrollerYio.factorModel.setDy(0);
        scrollerYio.factorModel.beginDestroying(0, 1);
        for (SliderYio sliderYio : sliders) sliderYio.appearFactor.beginDestroying(2, 2);
        for (ButtonLighty buttonLighty : buttons) {
            buttonLighty.destroy();
//            if (buttonLighty.id == 11 && buttonLighty.isVisible()) {
//                buttonLighty.factorModel.stopMoving();
//                buttonLighty.factorModel.beginDestroying(0, 1);
//            }
            if (buttonLighty.id == 3 && buttonLighty.isVisible()) {
                buttonLighty.factorModel.setValues(1, 0);
                buttonLighty.factorModel.beginDestroying(1, 2);
            }
            if (buttonLighty.id >= 22 && buttonLighty.id <= 29 && buttonLighty.isVisible()) {
                buttonLighty.factorModel.beginDestroying(1, 2.1);
            }
            if (buttonLighty.id == 30) {
                buttonLighty.factorModel.setValues(1, 0);
                buttonLighty.factorModel.beginDestroying(1, 1);
            }
        }
        if (yioGdxGame.gameView != null) yioGdxGame.gameView.beginDestroyProcess();
    }


    private void endMenuCreation() {

    }


    void forceSpawningButtonsToTheEnd() {
        for (ButtonLighty buttonLighty : buttons) {
            if (buttonLighty.factorModel.getGravity() > 0) {
                buttonLighty.factorModel.setValues(1, 0);
            }
        }
    }


    ArrayList<String> getArrayListFromString(String src) {
        ArrayList<String> list = new ArrayList<String>();
        StringTokenizer tokenizer = new StringTokenizer(src, "#");
        while (tokenizer.hasMoreTokens()) {
            list.add(tokenizer.nextToken());
        }
        return list;
    }


    private SimpleRectangle generateRectangle(double x, double y, double width, double height) {
        return new SimpleRectangle(x * Gdx.graphics.getWidth(), y * Gdx.graphics.getHeight(), width * Gdx.graphics.getWidth(), height * Gdx.graphics.getHeight());
    }


    private SimpleRectangle generateSquare(double x, double y, double size) {
        return generateRectangle(x, y, size / YioGdxGame.screenRatio, size);
    }


    public void createMainMenu() {
        beginMenuCreation();

        yioGdxGame.beginBackgroundChange(0, false, true);

        ButtonLighty exitButton = buttonFactory.getButton(generateSquare(0.8, 0.87, 0.15 * YioGdxGame.screenRatio), 1, null);
        loadButtonOnce(exitButton, "shut_down.png");
        exitButton.setShadow(true);
        exitButton.setAnimType(ButtonLighty.ANIM_UP);
        exitButton.setReactBehavior(ReactBehavior.rbExit);
        exitButton.disableTouchAnimation();

        ButtonLighty settingsButton = buttonFactory.getButton(generateSquare(0.05, 0.87, 0.15 * YioGdxGame.screenRatio), 2, null);
        loadButtonOnce(settingsButton, "settings_icon.png");
        settingsButton.setShadow(true);
        settingsButton.setAnimType(ButtonLighty.ANIM_UP);
        settingsButton.setReactBehavior(ReactBehavior.rbSettingsMenu);
        settingsButton.disableTouchAnimation();

        ButtonLighty playButton = buttonFactory.getButton(generateSquare(0.3, 0.35, 0.4 * YioGdxGame.screenRatio), 3, null);
        loadButtonOnce(playButton, "play_button.png");
        playButton.setReactBehavior(ReactBehavior.rbChooseGameModeMenu);
//        playButton.enableDeltaAnimation();
        playButton.disableTouchAnimation();
        playButton.selectionFactor.setValues(1, 0);

        endMenuCreation();
    }


    public void createSettingsMenu() {
        beginMenuCreation();

        yioGdxGame.beginBackgroundChange(1, false, true);

        spawnBackButton(190, ReactBehavior.rbCloseSettingsMenu);

        ButtonLighty infoButton = buttonFactory.getButton(generateSquare(0.8, 0.89, 0.15 * YioGdxGame.screenRatio), 191, null);
        loadButtonOnce(infoButton, "info_icon.png");
        infoButton.setShadow(true);
        infoButton.setAnimType(ButtonLighty.ANIM_UP);
        infoButton.setReactBehavior(ReactBehavior.rbInfo);
        infoButton.disableTouchAnimation();

        ButtonLighty soundLabel = buttonFactory.getButton(generateRectangle(0.1, 0.67, 0.8, 0.2), 192, null);
        renderTextAndSomeEmptyLines(soundLabel, languagesManager.getString("sound"), 2);
        soundLabel.setTouchable(false);
        soundLabel.setAnimType(ButtonLighty.ANIM_UP);

        ButtonLighty skinLabel = buttonFactory.getButton(generateRectangle(0.1, 0.46, 0.8, 0.2), 193, null);
        renderTextAndSomeEmptyLines(skinLabel, languagesManager.getString("skin"), 2);
        skinLabel.setTouchable(false);
        skinLabel.setAnimType(ButtonLighty.ANIM_UP);

        ButtonLighty interfaceSettingsButton = buttonFactory.getButton(generateRectangle(0.1, 0.17, 0.8, 0.07), 194, languagesManager.getString("interface_label"));
//        renderTextAndSomeEmptyLines(interfaceSettingsButton, languagesManager.getString("interface_label"), 2);
//        interfaceSettingsButton.setTouchable(false);
        interfaceSettingsButton.setReactBehavior(ReactBehavior.rbInterfaceSettings);
        interfaceSettingsButton.setAnimType(ButtonLighty.ANIM_DOWN);

        ButtonLighty autosaveLabel = buttonFactory.getButton(generateRectangle(0.1, 0.25, 0.8, 0.2), 195, null);
        renderTextAndSomeEmptyLines(autosaveLabel, languagesManager.getString("autosave"), 2);
        autosaveLabel.setTouchable(false);
        autosaveLabel.setAnimType(ButtonLighty.ANIM_DOWN);

        for (int i = 192; i <= 195; i++) {
            getButtonById(i).factorModel.beginSpawning(2, 1.5);
        }

//        ButtonLighty questionInterfaceMark = buttonFactory.getButton(generateSquare(0.9 - 0.06 / YioGdxGame.screenRatio, 0.39, 0.06), 196, null);
//        loadButtonOnce(questionInterfaceMark, "question_mark.png");
//        questionInterfaceMark.disableTouchAnimation();
//        questionInterfaceMark.setAnimType(ButtonLighty.ANIM_DOWN);
//        questionInterfaceMark.setReactBehavior(ReactBehavior.rbArticleComplicatedMode);

        sliders.get(4).appear();
        sliders.get(4).setPos(0.15, 0.73, 0.7, 0);

        sliders.get(5).appear();
        sliders.get(5).setPos(0.15, 0.52, 0.7, 0);
        sliders.get(5).setVerticalTouchOffset(0.06f * Gdx.graphics.getHeight());

        sliders.get(7).appear();
        sliders.get(7).setPos(0.15, 0.31, 0.7, 0);
        sliders.get(7).setVerticalTouchOffset(0.06f * Gdx.graphics.getHeight());

        endMenuCreation();
    }


    public void createInterfaceMenu() {
        beginMenuCreation();

        yioGdxGame.beginBackgroundChange(1, false, true);

        spawnBackButton(310, ReactBehavior.rbSettingsMenu);

        ButtonLighty slotNumberButton = buttonFactory.getButton(generateRectangle(0.1, 0.67, 0.8, 0.2), 311, null);
        renderTextAndSomeEmptyLines(slotNumberButton, languagesManager.getString("slot_number"), 2);
        slotNumberButton.setTouchable(false);
        slotNumberButton.setAnimType(ButtonLighty.ANIM_UP);
        sliders.get(6).appear();
        sliders.get(6).setPos(0.15, 0.73, 0.7, 0);
        sliders.get(6).setVerticalTouchOffset(0.06f * Gdx.graphics.getHeight());

        ButtonLighty confirmButton = buttonFactory.getButton(generateRectangle(0.1, 0.46, 0.8, 0.2), 312, null);
        renderTextAndSomeEmptyLines(confirmButton, languagesManager.getString("ask_to_end_turn"), 2);
        confirmButton.setTouchable(false);
        confirmButton.setAnimType(ButtonLighty.ANIM_UP);
        sliders.get(8).appear();
        sliders.get(8).setPos(0.15, 0.52, 0.7, 0);
        sliders.get(8).setVerticalTouchOffset(0.06f * Gdx.graphics.getHeight());

        ButtonLighty animStyleButton = buttonFactory.getButton(generateRectangle(0.1, 0.25, 0.8, 0.2), 313, null);
        renderTextAndSomeEmptyLines(animStyleButton, languagesManager.getString("anim_style"), 2);
        animStyleButton.setTouchable(false);
        animStyleButton.setAnimType(ButtonLighty.ANIM_DOWN);
        sliders.get(9).appear();
        sliders.get(9).setPos(0.15, 0.31, 0.7, 0);
        sliders.get(9).setVerticalTouchOffset(0.06f * Gdx.graphics.getHeight());

        ButtonLighty provinceNameButton = buttonFactory.getButton(generateRectangle(0.1, 0.04, 0.8, 0.2), 314, null);
        renderTextAndSomeEmptyLines(provinceNameButton, languagesManager.getString("city_names"), 2);
        provinceNameButton.setTouchable(false);
        provinceNameButton.setAnimType(ButtonLighty.ANIM_DOWN);
        sliders.get(10).appear();
        sliders.get(10).setPos(0.15, 0.1, 0.7, 0);
        sliders.get(10).setVerticalTouchOffset(0.06f * Gdx.graphics.getHeight());


        for (int i = 311; i <= 314; i++) {
            getButtonById(i).factorModel.beginSpawning(2, 1.5);
        }

        endMenuCreation();
    }


    public void createInfoMenu(String key, ReactBehavior backButtonBehavior, int lines) {
        beginMenuCreation();

        yioGdxGame.beginBackgroundChange(1, true, false);

        spawnBackButton(10, backButtonBehavior);

        ButtonLighty infoPanel = buttonFactory.getButton(generateRectangle(0.05, 0.1, 0.9, 0.7), 11, null);

        infoPanel.cleatText();
        ArrayList<String> list = getArrayListFromString(languagesManager.getString(key));
        infoPanel.addManyLines(list);
        int addedEmptyLines = lines - list.size();
        for (int i = 0; i < addedEmptyLines; i++) {
            infoPanel.addTextLine(" ");
        }
        buttonRenderer.renderButton(infoPanel);

        infoPanel.setTouchable(false);
        infoPanel.setAnimType(ButtonLighty.ANIM_FROM_CENTER);
        infoPanel.factorModel.beginSpawning(2, 1.5);
//        infoPanel.factorModel.setValues(-0.3, 0);
//        infoPanel.factorModel.beginSpawning(1, 0.3);
//        infoPanelFactor.setValues(-0.3, 0);
//        infoPanelFactor.beginSpawning(1, 0.37);

        endMenuCreation();
    }


    public void createInfoMenu() {
        createInfoMenu("info_array", ReactBehavior.rbMainMenu, 18);

        ButtonLighty helpIndexButton = buttonFactory.getButton(generateRectangle(0.5, 0.9, 0.45, 0.07), 38123714, languagesManager.getString("help"));
        helpIndexButton.setReactBehavior(ReactBehavior.rbHelpIndex);
        helpIndexButton.setAnimType(ButtonLighty.ANIM_UP);
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

        sliders.get(2).appear();
        sliders.get(2).setPos(0.15, 0.1, 0.7, 0);

        ButtonLighty difficultyLabel = buttonFactory.getButton(generateRectangle(0.1, 0.67, 0.8, 0.2), 88, null);
        renderTextAndSomeEmptyLines(difficultyLabel, languagesManager.getString("difficulty"), 2);
        difficultyLabel.setTouchable(false);
        difficultyLabel.setAnimType(ButtonLighty.ANIM_UP);

        ButtonLighty mapSizeLabel = buttonFactory.getButton(generateRectangle(0.1, 0.46, 0.8, 0.2), 81, null);
        renderTextAndSomeEmptyLines(mapSizeLabel, languagesManager.getString("map_size"), 2);
        mapSizeLabel.setTouchable(false);
        mapSizeLabel.setAnimType(ButtonLighty.ANIM_UP);

        ButtonLighty playersLabel = buttonFactory.getButton(generateRectangle(0.1, 0.25, 0.8, 0.2), 84, null);
        renderTextAndSomeEmptyLines(playersLabel, languagesManager.getString("player_number"), 2);
        playersLabel.setTouchable(false);
        playersLabel.setAnimType(ButtonLighty.ANIM_DOWN);

        ButtonLighty colorsLabel = buttonFactory.getButton(generateRectangle(0.1, 0.04, 0.8, 0.2), 87, null);
        renderTextAndSomeEmptyLines(colorsLabel, languagesManager.getString("color_number"), 2);
        colorsLabel.setTouchable(false);
        colorsLabel.setAnimType(ButtonLighty.ANIM_DOWN);

        getButtonById(88).factorModel.beginSpawning(2, 1.5);
        getButtonById(81).factorModel.beginSpawning(2, 1.5);
        getButtonById(84).factorModel.beginSpawning(2, 1.5);
        getButtonById(87).factorModel.beginSpawning(2, 1.5);

        spawnBackButton(80, ReactBehavior.rbChooseGameModeMenu);

        ButtonLighty startButton = buttonFactory.getButton(generateRectangle(0.55, 0.9, 0.4, 0.07), 83, languagesManager.getString("game_settings_start"));
        startButton.setReactBehavior(ReactBehavior.rbStartGame);
        startButton.setAnimType(ButtonLighty.ANIM_UP);
        startButton.disableTouchAnimation();

        endMenuCreation();
    }


    private void renderTextAndSomeEmptyLines(ButtonLighty buttonLighty, String text, int emptyLines) {
        if (buttonLighty.notRendered()) {
            buttonLighty.addTextLine(text);
            for (int i = 0; i < emptyLines; i++) {
                buttonLighty.addTextLine(" ");
            }
            buttonRenderer.renderButton(buttonLighty);
        }
    }


    public void createTestMenu() {
        beginMenuCreation();

        yioGdxGame.beginBackgroundChange(2, false, true);


        spawnBackButton(38721132, ReactBehavior.rbChooseGameModeMenu);

        endMenuCreation();
    }


    private String getEditorSlotString(int index) {
        Preferences prefs = Gdx.app.getPreferences("editor");
        String slotString = prefs.getString("slot" + (index + 1));
        if (slotString.length() < 10) {
            return languagesManager.getString("slot") + " " + (index + 1) + " - " + languagesManager.getString("empty");
        } else {
            return languagesManager.getString("slot") + " " + (index + 1);
        }
    }


    public void updateSaveSlotButton(int slotIndex) {
        ButtonLighty slotButton = getButtonById(212 + slotIndex);
        if (slotButton == null) return;
        Preferences prefs = Gdx.app.getPreferences("save_slot" + slotIndex);
        String dateString = prefs.getString("date");
        String detailsInfo = " ";
        if (dateString.length() > 3) {
            slotButton.setTextLine(dateString);
            String diff = ScrollerYio.getDifficultyNameByPower(languagesManager, prefs.getInteger("save_difficulty"));
            if (prefs.getBoolean("save_campaign_mode")) {
                detailsInfo = "- " + languagesManager.getString("choose_game_mode_campaign") + "," + prefs.getInteger("save_current_level") + "|" + diff;
            } else {
                detailsInfo = "- " + languagesManager.getString("choose_game_mode_skirmish") + "|" + diff;
            }
        } else {
            slotButton.setTextLine(languagesManager.getString("empty"));
        }
        slotButton.addTextLine(detailsInfo);
        buttonRenderer.renderButton(slotButton);
    }


    public void createSaveSlotsMenu(boolean load) {
        beginMenuCreation();

        yioGdxGame.beginBackgroundChange(1, false, true);

        spawnBackButton(210, ReactBehavior.rbInGameMenu);
        if (load) getButtonById(210).setReactBehavior(ReactBehavior.rbChooseGameModeMenu);

        ButtonLighty basePanel = buttonFactory.getButton(generateRectangle(0.05, 0.2, 0.9, 0.57), 211, null);
        if (basePanel.notRendered()) {
            basePanel.addTextLine(languagesManager.getString("slots") + ":");
            for (int i = 0; i < 10; i++) {
                basePanel.addTextLine(" ");
            }
            buttonRenderer.renderButton(basePanel);
        }
        basePanel.setTouchable(false);
//        basePanel.onlyShadow = true;
        basePanel.setAnimType(ButtonLighty.ANIM_FROM_CENTER);
        basePanel.factorModel.beginSpawning(SPAWN_ANIM, SPAWN_SPEED);

        for (int i = 0; i < 5; i++) {
            ButtonLighty slotButton = buttonFactory.getButton(generateRectangle(0.05, 0.6 - 0.1 * (double) i, 0.9, 0.1), 212 + i, null);
            updateSaveSlotButton(i);

            slotButton.setAnimType(ButtonLighty.ANIM_FROM_CENTER);
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

        ButtonLighty basePanel = buttonFactory.getButton(generateRectangle(0.05, 0.05, 0.9, 0.8), 139, null);
        basePanel.setTouchable(false);
        basePanel.onlyShadow = true;
        basePanel.setAnimType(ButtonLighty.ANIM_FROM_CENTER);
        basePanel.factorModel.beginSpawning(SPAWN_ANIM, SPAWN_SPEED);

        for (int i = 0; i < 8; i++) {
            ButtonLighty slotButton = buttonFactory.getButton(generateRectangle(0.05, 0.75 - 0.1 * (double) i, 0.9, 0.1), 131 + i, null);
            slotButton.cleatText();
            slotButton.addTextLine(getEditorSlotString(i));
            slotButton.addTextLine(" ");
            buttonRenderer.renderButton(slotButton);

            slotButton.setAnimType(ButtonLighty.ANIM_FROM_CENTER);
            slotButton.setShadow(false);
            slotButton.setReactBehavior(ReactBehavior.rbEditorActionsMenu);
            slotButton.factorModel.beginSpawning(SPAWN_ANIM, SPAWN_SPEED);
        }

        spawnBackButton(130, ReactBehavior.rbChooseGameModeMenu);
    }


    public void createChooseGameModeMenu() {
        beginMenuCreation();

        yioGdxGame.beginBackgroundChange(1, true, true);

        ButtonLighty basePanel = buttonFactory.getButton(generateRectangle(0.1, 0.3, 0.8, 0.4), 70, null);
        basePanel.setTouchable(false);
        basePanel.onlyShadow = true;
        basePanel.setAnimType(ButtonLighty.ANIM_FROM_CENTER);

        ButtonLighty skirmishButton = buttonFactory.getButton(generateRectangle(0.1, 0.62, 0.8, 0.08), 72, languagesManager.getString("choose_game_mode_skirmish"));
        skirmishButton.setReactBehavior(ReactBehavior.rbSkirmishMenu);
        skirmishButton.setShadow(false);
        skirmishButton.setAnimType(ButtonLighty.ANIM_FROM_CENTER);

        ButtonLighty tutorialButton = buttonFactory.getButton(generateRectangle(0.1, 0.46, 0.8, 0.08), 73, languagesManager.getString("choose_game_mode_tutorial"));
        tutorialButton.setShadow(false);
        tutorialButton.setReactBehavior(ReactBehavior.rbTutorial);
        tutorialButton.setAnimType(ButtonLighty.ANIM_FROM_CENTER);

        ButtonLighty campaignButton = buttonFactory.getButton(generateRectangle(0.1, 0.38, 0.8, 0.08), 74, languagesManager.getString("choose_game_mode_campaign"));
        campaignButton.setReactBehavior(ReactBehavior.rbCampaignMenu);
        campaignButton.setShadow(false);
        campaignButton.setAnimType(ButtonLighty.ANIM_FROM_CENTER);
        campaignButton.disableTouchAnimation();

        ButtonLighty loadGameButton = buttonFactory.getButton(generateRectangle(0.1, 0.3, 0.8, 0.08), 75, languagesManager.getString("choose_game_mode_load"));
        loadGameButton.setShadow(false);
        loadGameButton.setAnimType(ButtonLighty.ANIM_FROM_CENTER);
        loadGameButton.setReactBehavior(ReactBehavior.rbLoadGame);
        loadGameButton.disableTouchAnimation();

        ButtonLighty editorButton = buttonFactory.getButton(generateRectangle(0.1, 0.54, 0.8, 0.08), 77, languagesManager.getString("editor"));
        editorButton.setShadow(false);
        editorButton.setAnimType(ButtonLighty.ANIM_FROM_CENTER);
        editorButton.setReactBehavior(ReactBehavior.rbEditorSlotMenu);

        spawnBackButton(76, ReactBehavior.rbMainMenu);

        endMenuCreation();
    }


    public void createHelpIndexMenu() {
        beginMenuCreation();

        yioGdxGame.setGamePaused(true);

        yioGdxGame.beginBackgroundChange(1, false, true);

        ButtonLighty basePanel = buttonFactory.getButton(generateRectangle(0.1, 0.21, 0.8, 0.5), 120, null);
        if (basePanel.notRendered()) {
            basePanel.addTextLine(languagesManager.getString("help") + ":");
            for (int i = 0; i < 6; i++) {
                basePanel.addTextLine(" ");
            }
            buttonRenderer.renderButton(basePanel);
        }
        basePanel.setTouchable(false);
        basePanel.setAnimType(ButtonLighty.ANIM_FROM_CENTER);

        ButtonLighty unitsButton = buttonFactory.getButton(generateRectangle(0.1, 0.53, 0.8, 0.08), 122, languagesManager.getString("help_about_units"));
        unitsButton.setReactBehavior(ReactBehavior.rbArticleUnits);
        unitsButton.setShadow(false);
        unitsButton.setAnimType(ButtonLighty.ANIM_FROM_CENTER);

        ButtonLighty treesButton = buttonFactory.getButton(generateRectangle(0.1, 0.45, 0.8, 0.08), 123, languagesManager.getString("help_about_trees"));
        treesButton.setShadow(false);
        treesButton.setReactBehavior(ReactBehavior.rbArticleTrees);
        treesButton.setAnimType(ButtonLighty.ANIM_FROM_CENTER);

        ButtonLighty towersButton = buttonFactory.getButton(generateRectangle(0.1, 0.37, 0.8, 0.08), 124, languagesManager.getString("help_about_towers"));
        towersButton.setReactBehavior(ReactBehavior.rbArticleTowers);
        towersButton.setShadow(false);
        towersButton.setAnimType(ButtonLighty.ANIM_FROM_CENTER);

        ButtonLighty moneyButton = buttonFactory.getButton(generateRectangle(0.1, 0.29, 0.8, 0.08), 125, languagesManager.getString("help_about_money"));
        moneyButton.setShadow(false);
        moneyButton.setAnimType(ButtonLighty.ANIM_FROM_CENTER);
        moneyButton.setReactBehavior(ReactBehavior.rbArticleMoney);

        ButtonLighty tacticsButton = buttonFactory.getButton(generateRectangle(0.1, 0.21, 0.8, 0.08), 126, languagesManager.getString("help_about_tactics"));
        tacticsButton.setShadow(false);
        tacticsButton.setAnimType(ButtonLighty.ANIM_FROM_CENTER);
        tacticsButton.setReactBehavior(ReactBehavior.rbArticleTactics);

        spawnBackButton(129, ReactBehavior.rbMainMenu);

        endMenuCreation();
    }


    public void createCampaignMenu() {
        beginMenuCreation();

        yioGdxGame.beginBackgroundChange(2, true, true);

        spawnBackButton(20, ReactBehavior.rbChooseGameModeMenu);

        ButtonLighty startButton = buttonFactory.getButton(generateRectangle(0.55, 0.9, 0.4, 0.07), 21, languagesManager.getString("game_settings_start"));
        startButton.setReactBehavior(ReactBehavior.rbCampaignLevel);
        startButton.setAnimType(ButtonLighty.ANIM_UP);

        if (scrollerYio.selectionIndex > 6) {
            scrollerYio.pos = (scrollerYio.selectionIndex - 1) * scrollerYio.lineHeight - 0.5f * scrollerYio.lineHeight;
            scrollerYio.limit();
        }
        scrollerYio.factorModel.setValues(0.03, 0);
        scrollerYio.factorModel.beginSpawning(1, 1.5);

        endMenuCreation();
    }


    public void hideConfirmEndTurnMenu() {
        for (int i = 320; i <= 322; i++) {
            ButtonLighty b = getButtonById(i);
            b.destroy();
            b.factorModel.setValues(0, 0);
        }

        for (int i = 30; i <= 32; i++) {
            ButtonLighty buttonLighty = getButtonById(i);
            buttonLighty.setTouchable(true);
        }
        for (int i = 38; i <= 39; i++) {
            ButtonLighty buttonLighty = getButtonById(i);
            if (buttonLighty == null) continue;
            buttonLighty.setTouchable(true);
        }
    }


    public void createConfirmEndTurnMenu() {
        ButtonLighty basePanel = buttonFactory.getButton(generateRectangle(0.05, 0.12, 0.9, 0.2), 320, null);
        if (basePanel.notRendered()) {
            basePanel.addTextLine(languagesManager.getString("confirm_end_turn"));
            basePanel.addTextLine(" ");
            basePanel.addTextLine(" ");
            buttonRenderer.renderButton(basePanel);
        }
        basePanel.setTouchable(false);
        basePanel.setAnimType(ButtonLighty.ANIM_DOWN);

        ButtonLighty confirmButton = buttonFactory.getButton(generateRectangle(0.5, 0.12, 0.45, 0.07), 321, languagesManager.getString("yes"));
        confirmButton.setReactBehavior(ReactBehavior.rbEndTurn);
        confirmButton.setShadow(false);
        confirmButton.setAnimType(ButtonLighty.ANIM_DOWN);
        confirmButton.disableTouchAnimation();

        ButtonLighty cancelButton = buttonFactory.getButton(generateRectangle(0.05, 0.12, 0.45, 0.07), 322, languagesManager.getString("cancel"));
        cancelButton.setReactBehavior(ReactBehavior.rbHideEndTurnConfirm);
        cancelButton.setShadow(false);
        cancelButton.setAnimType(ButtonLighty.ANIM_DOWN);
        cancelButton.disableTouchAnimation();

        for (int i = 30; i <= 32; i++) {
            ButtonLighty buttonLighty = getButtonById(i);
            buttonLighty.setTouchable(false);
        }
        for (int i = 38; i <= 39; i++) {
            ButtonLighty buttonLighty = getButtonById(i);
            if (buttonLighty == null) continue;
            buttonLighty.setTouchable(false);
        }
    }


    public void createConfirmRestartMenu() {
        beginMenuCreation();

        yioGdxGame.beginBackgroundChange(3, true, true);

        ButtonLighty basePanel = buttonFactory.getButton(generateRectangle(0.05, 0.4, 0.9, 0.2), 220, null);
        if (basePanel.notRendered()) {
            basePanel.addTextLine(languagesManager.getString("confirm_restart"));
            basePanel.addTextLine(" ");
            basePanel.addTextLine(" ");
            buttonRenderer.renderButton(basePanel);
        }
        basePanel.setTouchable(false);
        basePanel.setAnimType(ButtonLighty.ANIM_FROM_CENTER);

        ButtonLighty restartButton = buttonFactory.getButton(generateRectangle(0.5, 0.4, 0.45, 0.07), 221, languagesManager.getString("in_game_menu_restart"));
        restartButton.setReactBehavior(ReactBehavior.rbRestartGame);
        restartButton.setShadow(false);
        restartButton.setAnimType(ButtonLighty.ANIM_FROM_CENTER);

        ButtonLighty cancelButton = buttonFactory.getButton(generateRectangle(0.05, 0.4, 0.45, 0.07), 222, languagesManager.getString("cancel"));
        cancelButton.setReactBehavior(ReactBehavior.rbInGameMenu);
        cancelButton.setShadow(false);
        cancelButton.setAnimType(ButtonLighty.ANIM_FROM_CENTER);

        endMenuCreation();
    }


    public void createEditorActionsMenu() {
        beginMenuCreation();

        yioGdxGame.setGamePaused(true);

        yioGdxGame.beginBackgroundChange(3, true, true);

        spawnBackButton(189, ReactBehavior.rbEditorSlotMenu);

        ButtonLighty basePanel = buttonFactory.getButton(generateRectangle(0.1, 0.3, 0.8, 0.4), 181, null);
        basePanel.setTouchable(false);
        basePanel.onlyShadow = true;

        ButtonLighty mainMenuButton = buttonFactory.getButton(generateRectangle(0.1, 0.3, 0.8, 0.1), 182, languagesManager.getString("play"));
        mainMenuButton.setReactBehavior(ReactBehavior.rbEditorPlay);
        mainMenuButton.setShadow(false);

        ButtonLighty exportButton = buttonFactory.getButton(generateRectangle(0.1, 0.4, 0.8, 0.1), 183, languagesManager.getString("export"));
        exportButton.setReactBehavior(ReactBehavior.rbEditorExport);
        exportButton.setShadow(false);

        ButtonLighty restartButton = buttonFactory.getButton(generateRectangle(0.1, 0.5, 0.8, 0.1), 184, languagesManager.getString("import"));
        restartButton.setReactBehavior(ReactBehavior.rbEditorImport);
        restartButton.setShadow(false);

        ButtonLighty resumeButton = buttonFactory.getButton(generateRectangle(0.1, 0.6, 0.8, 0.1), 185, languagesManager.getString("edit"));
        resumeButton.setReactBehavior(ReactBehavior.rbStartEditorMode);
        resumeButton.setShadow(false);

        for (int i = 181; i <= 185; i++) {
            ButtonLighty buttonLighty = getButtonById(i);
            buttonLighty.setAnimType(ButtonLighty.ANIM_FROM_CENTER);
            buttonLighty.disableTouchAnimation();
        }

        endMenuCreation();
    }


    public void hideAllEditorPanels() {
        hideEditorHexPanel();
        hideEditorObjectPanel();
        hideEditorOptionsPanel();
    }


    public void showEditorOptionsPanel() {
        ButtonLighty basePanel = buttonFactory.getButton(generateRectangle(0, 0.07, 1, 0.21), 172, null);
        loadButtonOnce(basePanel, "gray_pixel.png");
        basePanel.setTouchable(false);

        ButtonLighty hideButton = buttonFactory.getButton(generateSquare(1 - 0.07 / YioGdxGame.screenRatio, 0.21, 0.07), 171, null);
        loadButtonOnce(hideButton, "hide_panel.png");
        hideButton.setReactBehavior(ReactBehavior.rbHideOptionsPanel);

        ButtonLighty clearLevelButton = buttonFactory.getButton(generateRectangle(0, 0.21, 0.8, 0.07), 173, languagesManager.getString("editor_clear"));
        clearLevelButton.setReactBehavior(ReactBehavior.rbClearLevel);

        ButtonLighty changePlayerNumberButton = buttonFactory.getButton(generateRectangle(0, 0.14, 0.8, 0.07), 174, languagesManager.getString("player_number"));
        changePlayerNumberButton.setReactBehavior(ReactBehavior.rbEditorChangePlayersNumber);

        ButtonLighty changeDifficultyButton = buttonFactory.getButton(generateRectangle(0, 0.07, 0.8, 0.07), 175, languagesManager.getString("difficulty"));
        changeDifficultyButton.setReactBehavior(ReactBehavior.rbEditorChangeDifficulty);

        ButtonLighty randomButton = buttonFactory.getButton(generateSquare(1 - 0.07 / YioGdxGame.screenRatio, 0.07, 0.07), 176, "G");
        randomButton.setReactBehavior(ReactBehavior.rbEditorRandomize);

        for (int i = 171; i <= 176; i++) {
            ButtonLighty buttonLighty = getButtonById(i);
            buttonLighty.factorModel.beginSpawning(SPAWN_ANIM, SPAWN_SPEED);
            buttonLighty.enableRectangularMask();
            buttonLighty.disableTouchAnimation();
            buttonLighty.setAnimType(ButtonLighty.ANIM_DOWN);
        }
    }


    public void hideEditorOptionsPanel() {
        for (int i = 171; i <= 176; i++) {
            ButtonLighty buttonLighty = getButtonById(i);
            buttonLighty.destroy();
        }
    }


    public void showEditorObjectPanel() {
        ButtonLighty basePanel = buttonFactory.getButton(generateRectangle(0, 0.07, 1, 0.07), 160, null);
        loadButtonOnce(basePanel, "gray_pixel.png");
        basePanel.setTouchable(false);

        ButtonLighty cancelButton = buttonFactory.getButton(generateSquare(0, 0.07, 0.07), 162, null);
        loadButtonOnce(cancelButton, "cancel_icon.png");
        cancelButton.setReactBehavior(ReactBehavior.rbInputModeSetObject);

        ButtonLighty hideButton = buttonFactory.getButton(generateSquare(1 - 0.07 / YioGdxGame.screenRatio, 0.07, 0.07), 161, null);
        loadButtonOnce(hideButton, "hide_panel.png");
        hideButton.setReactBehavior(ReactBehavior.rbHideObjectPanel);

        ButtonLighty objectButton;
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
            ButtonLighty buttonLighty = getButtonById(i);
            buttonLighty.factorModel.beginSpawning(SPAWN_ANIM, SPAWN_SPEED);
            buttonLighty.enableRectangularMask();
            buttonLighty.disableTouchAnimation();
            buttonLighty.setAnimType(ButtonLighty.ANIM_DOWN);
        }
    }


    public void hideEditorObjectPanel() {
        for (int i = 160; i <= 168; i++) {
            ButtonLighty buttonLighty = getButtonById(i);
            buttonLighty.destroy();
        }
    }


    public void showEditorHexPanel() {
        ButtonLighty basePanel = buttonFactory.getButton(generateRectangle(0, 0.07, 1, 0.14), 12352, null);
        loadButtonOnce(basePanel, "gray_pixel.png");
        basePanel.setTouchable(false);

        ButtonLighty cancelButton = buttonFactory.getButton(generateSquare(0, 0.14, 0.07), 12350, null);
        loadButtonOnce(cancelButton, "cancel_icon.png");
        cancelButton.setReactBehavior(ReactBehavior.rbInputModeDelete);

        ButtonLighty hideButton = buttonFactory.getButton(generateSquare(1 - 0.07 / YioGdxGame.screenRatio, 0.14, 0.07), 12351, null);
        loadButtonOnce(hideButton, "hide_panel.png");
        hideButton.setReactBehavior(ReactBehavior.rbHideHexPanel);

        ButtonLighty hexButton;
        double curVerPos = 0.14;
        double curHorPos = 0.07;
        for (int i = 0; i < 9; i++) {
            if (i == 4) {
                curVerPos = 0.07;
                curHorPos = 0.07;
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

        for (int i = 12350; i < 12353; i++) {
            ButtonLighty buttonLighty = getButtonById(i);
            buttonLighty.factorModel.beginSpawning(SPAWN_ANIM, SPAWN_SPEED);
            buttonLighty.enableRectangularMask();
            buttonLighty.disableTouchAnimation();
            buttonLighty.setAnimType(ButtonLighty.ANIM_DOWN);
        }

        for (int i = 150; i <= 158; i++) {
            ButtonLighty buttonLighty = getButtonById(i);
            buttonLighty.factorModel.beginSpawning(SPAWN_ANIM, SPAWN_SPEED);
            buttonLighty.enableRectangularMask();
            buttonLighty.disableTouchAnimation();
            buttonLighty.setAnimType(ButtonLighty.ANIM_DOWN);
        }
    }


    public void hideEditorHexPanel() {
        for (int i = 150; i <= 158; i++) {
            ButtonLighty buttonLighty = getButtonById(i);
            buttonLighty.destroy();
        }
        for (int i = 12350; i < 12353; i++) {
            ButtonLighty buttonLighty = getButtonById(i);
            buttonLighty.destroy();
        }
    }


    private void createEditorInstruments() {
        beginMenuCreation();

        showEditorHexPanel();
        showEditorObjectPanel();
        showEditorOptionsPanel();
        hideAllEditorPanels();

        ButtonLighty menuButton = buttonFactory.getButton(generateSquare(1 - 0.07 / YioGdxGame.screenRatio, 0.93, 0.07), 140, null);
        loadButtonOnce(menuButton, "menu_icon.png");
        menuButton.setReactBehavior(ReactBehavior.rbEditorActionsMenu);
        menuButton.setAnimType(ButtonLighty.ANIM_UP);
        menuButton.enableRectangularMask();
        menuButton.disableTouchAnimation();

        ButtonLighty basePanel = buttonFactory.getButton(generateRectangle(0, 0, 1, 0.07), 141, null);
        loadButtonOnce(basePanel, "gray_pixel.png");
        basePanel.setTouchable(false);

        ButtonLighty hexButton = buttonFactory.getButton(generateSquare(0, 0, 0.07), 142, null);
        loadButtonOnce(hexButton, "hex_black.png");
        hexButton.setReactBehavior(ReactBehavior.rbShowHexPanel);

        ButtonLighty moveButton = buttonFactory.getButton(generateSquare(0.07 / YioGdxGame.screenRatio, 0, 0.07), 143, null);
        loadButtonOnce(moveButton, "icon_move.png");
        moveButton.setReactBehavior(ReactBehavior.rbInputModeMove);

        ButtonLighty unitButton = buttonFactory.getButton(generateSquare(2 * 0.07 / YioGdxGame.screenRatio, 0, 0.07), 144, null);
        loadButtonOnce(unitButton, "field_elements/man0_low.png");
        unitButton.setReactBehavior(ReactBehavior.rbShowObjectPanel);

        ButtonLighty optionsButton = buttonFactory.getButton(generateSquare(3 * 0.07 / YioGdxGame.screenRatio, 0, 0.07), 145, null);
        loadButtonOnce(optionsButton, "opened_level_icon.png");
        optionsButton.setReactBehavior(ReactBehavior.rbShowOptionsPanel);

        for (int i = 141; i <= 145; i++) {
            ButtonLighty buttonLighty = getButtonById(i);
            buttonLighty.factorModel.beginSpawning(SPAWN_ANIM, SPAWN_SPEED);
            buttonLighty.enableRectangularMask();
            buttonLighty.disableTouchAnimation();
            buttonLighty.setAnimType(ButtonLighty.ANIM_DOWN);
        }

        endMenuCreation();
    }


    public void createGameOverlay() {
        if (yioGdxGame.gameController.editorMode) {
            createEditorInstruments();
            return;
        }

        beginMenuCreation();

        ButtonLighty inGameMenuButton = buttonFactory.getButton(generateSquare(1 - 0.07 / YioGdxGame.screenRatio, 0.93, 0.07), 30, null);
        loadButtonOnce(inGameMenuButton, "menu_icon.png");
        inGameMenuButton.setReactBehavior(ReactBehavior.rbInGameMenu);
        inGameMenuButton.setAnimType(ButtonLighty.ANIM_UP);
        inGameMenuButton.enableRectangularMask();
        inGameMenuButton.disableTouchAnimation();

        ButtonLighty endTurnButton = buttonFactory.getButton(generateSquare(1 - 0.07 / YioGdxGame.screenRatio, 0, 0.07), 31, null);
        loadButtonOnce(endTurnButton, "end_turn.png");
        endTurnButton.setReactBehavior(ReactBehavior.rbEndTurn);
        endTurnButton.setAnimType(ButtonLighty.ANIM_DOWN);
        endTurnButton.enableRectangularMask();
        endTurnButton.disableTouchAnimation();

        ButtonLighty undoButton = buttonFactory.getButton(generateSquare(0, 0, 0.07), 32, null);
        loadButtonOnce(undoButton, "undo_icon.png");
        undoButton.setReactBehavior(ReactBehavior.rbUndo);
        undoButton.setAnimType(ButtonLighty.ANIM_DOWN);
        undoButton.enableRectangularMask();
        undoButton.disableTouchAnimation();

//        ButtonLighty debugButton = buttonFactory.getButton(generateSquare(0.72, 0, 0.07), 3128773, "Q");
//        debugButton.setReactBehavior(ReactBehavior.rbDebugActions);
//        debugButton.setAnimType(ButtonLighty.ANIM_DOWN);
//        debugButton.disableTouchAnimation();

        endMenuCreation();
    }


    public void revealBuildButtons() {
        ButtonLighty unitButton = getButtonById(39);
        if (unitButton == null) { // init
            unitButton = buttonFactory.getButton(generateSquare(0.57, 0, 0.13 * YioGdxGame.screenRatio), 39, null);
            loadButtonOnce(unitButton, "field_elements/man0.png");
            unitButton.setReactBehavior(ReactBehavior.rbBuildUnit);
            unitButton.setAnimType(ButtonLighty.ANIM_DOWN);
            unitButton.enableRectangularMask();
        }
        unitButton.setTouchable(true);
        unitButton.factorModel.beginSpawning(3, 2);

        ButtonLighty towerButton = getButtonById(38);
        if (towerButton == null) { // init
            towerButton = buttonFactory.getButton(generateSquare(0.30, 0, 0.13 * YioGdxGame.screenRatio), 38, null);
            loadButtonOnce(towerButton, "field_elements/tower.png");
            towerButton.setReactBehavior(ReactBehavior.rbBuildTower);
            towerButton.setAnimType(ButtonLighty.ANIM_DOWN);
            towerButton.enableRectangularMask();
        }
        towerButton.setTouchable(true);
        towerButton.factorModel.beginSpawning(3, 2);

        ButtonLighty coinButton = getButtonById(37);
        if (coinButton == null) { // init
            coinButton = buttonFactory.getButton(generateSquare(0, 0.93, 0.07), 37, null);
            loadButtonOnce(coinButton, "coin.png");
            coinButton.setAnimType(ButtonLighty.ANIM_UP);
            coinButton.enableRectangularMask();
            coinButton.disableTouchAnimation();
        }
        coinButton.factorModel.beginSpawning(3, 2);
        coinButton.setTouchable(true);
        coinButton.setReactBehavior(ReactBehavior.rbShowColorStats);
    }


    public void hideBuildButtons() {
        ButtonLighty unitButton = getButtonById(39);
        if (unitButton != null) unitButton.destroy();

        ButtonLighty towerButton = getButtonById(38);
        if (towerButton != null) towerButton.destroy();

        ButtonLighty coinButton = getButtonById(37);
        if (coinButton != null) coinButton.destroy();

        yioGdxGame.gameController.selMoneyFactor.beginDestroying(2, 8);
    }


    public void createInGameMenu() {
        beginMenuCreation();

        yioGdxGame.beginBackgroundChange(3, true, true);

        ButtonLighty basePanel = buttonFactory.getButton(generateRectangle(0.1, 0.3, 0.8, 0.4), 40, null);
        basePanel.setTouchable(false);
        basePanel.onlyShadow = true;
        basePanel.setAnimType(ButtonLighty.ANIM_FROM_CENTER);

        ButtonLighty mainMenuButton = buttonFactory.getButton(generateRectangle(0.1, 0.3, 0.8, 0.1), 42, languagesManager.getString("in_game_menu_main_menu"));
        mainMenuButton.setReactBehavior(ReactBehavior.rbMainMenu);
        mainMenuButton.setShadow(false);
        mainMenuButton.setAnimType(ButtonLighty.ANIM_FROM_CENTER);
        mainMenuButton.disableTouchAnimation();

        ButtonLighty chooseLevelButton = buttonFactory.getButton(generateRectangle(0.1, 0.4, 0.8, 0.1), 43, languagesManager.getString("in_game_menu_save"));
        chooseLevelButton.setReactBehavior(ReactBehavior.rbSaveGame);
        chooseLevelButton.setShadow(false);
        chooseLevelButton.setAnimType(ButtonLighty.ANIM_FROM_CENTER);
        chooseLevelButton.disableTouchAnimation();

        ButtonLighty restartButton = buttonFactory.getButton(generateRectangle(0.1, 0.5, 0.8, 0.1), 44, languagesManager.getString("in_game_menu_restart"));
        restartButton.setReactBehavior(ReactBehavior.rbRestartGame);
        restartButton.setShadow(false);
        restartButton.setAnimType(ButtonLighty.ANIM_FROM_CENTER);
        restartButton.disableTouchAnimation();

        ButtonLighty resumeButton = buttonFactory.getButton(generateRectangle(0.1, 0.6, 0.8, 0.1), 45, languagesManager.getString("in_game_menu_resume"));
        resumeButton.setReactBehavior(ReactBehavior.rbResumeGame);
        resumeButton.setShadow(false);
        resumeButton.setAnimType(ButtonLighty.ANIM_FROM_CENTER);
        resumeButton.disableTouchAnimation();

        endMenuCreation();
    }


    public void showColorStats() {
        yioGdxGame.gameController.deselectAll();
        getButtonById(30).setTouchable(false);
        getButtonById(31).setTouchable(false);
        getButtonById(32).setTouchable(false);

        ButtonLighty showPanel = buttonFactory.getButton(generateRectangle(0, 0.1, 1, 0.41), 56321, null);
        showPanel.setTouchable(false);
        showPanel.setAnimType(ButtonLighty.ANIM_COLLAPSE_DOWN);
        showPanel.factorModel.beginSpawning(3, 1);

        ButtonLighty okButton = buttonFactory.getButton(generateRectangle(0.6, 0.1, 0.4, 0.07), 56322, languagesManager.getString("end_game_ok"));
        okButton.setShadow(false);
        okButton.setReactBehavior(ReactBehavior.rbHideColorStats);
        okButton.setAnimType(ButtonLighty.ANIM_COLLAPSE_DOWN);
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
//        yioGdxGame.setGamePaused(true);
        getButtonById(30).setTouchable(false);
        getButtonById(31).setTouchable(false);
        getButtonById(32).setTouchable(false);

        for (int i = 0; i < 2; i++) text.add("");
        ButtonLighty textPanel = buttonFactory.getButton(generateRectangle(0, 0, 1, 1), 50, null);
        textPanel.setPosition(generateRectangle(0, 0.1, 1, 0.05 * (double) text.size()));
        textPanel.cleatText();
        textPanel.addManyLines(text);
        buttonRenderer.renderButton(textPanel);
        textPanel.setTouchable(false);
        textPanel.setAnimType(ButtonLighty.ANIM_COLLAPSE_DOWN);
        textPanel.factorModel.beginSpawning(3, 1);

        ButtonLighty okButton = buttonFactory.getButton(generateRectangle(0.6, 0.1, 0.4, 0.07), 53, languagesManager.getString("end_game_ok"));
        okButton.setShadow(false);
        okButton.setReactBehavior(ReactBehavior.rbCloseTutorialTip);
        okButton.setAnimType(ButtonLighty.ANIM_COLLAPSE_DOWN);
        okButton.factorModel.beginSpawning(3, 1);
    }


    public void addWinButtonToTutorialTip() {
        ButtonLighty winButton = buttonFactory.getButton(generateRectangle(0, 0.1, 0.6, 0.07), 54, null);
        winButton.setTextLine(languagesManager.getString("win_game"));
        buttonRenderer.renderButton(winButton);
        winButton.setShadow(false);
        winButton.setReactBehavior(ReactBehavior.rbWinGame);
        winButton.setAnimType(ButtonLighty.ANIM_COLLAPSE_DOWN);
        winButton.factorModel.beginSpawning(3, 1);

        ButtonLighty okButton = getButtonById(53);
        okButton.setTextLine(languagesManager.getString("next"));
        buttonRenderer.renderButton(okButton);
    }


    public void addHelpButtonToTutorialTip() {
        ButtonLighty helpButton = buttonFactory.getButton(generateRectangle(0, 0.1, 0.6, 0.07), 54, null);
        helpButton.setTextLine(languagesManager.getString("help"));
        buttonRenderer.renderButton(helpButton);
        helpButton.setShadow(false);
        helpButton.setReactBehavior(ReactBehavior.rbHelpIndex);
        helpButton.setAnimType(ButtonLighty.ANIM_COLLAPSE_DOWN);
        helpButton.factorModel.beginSpawning(3, 1);
    }


    public void showNotification(String message, boolean autoHide) {
        ButtonLighty notificationButton = buttonFactory.getButton(generateRectangle(0, 0.95, 1, 0.05), 3614, null);
        notificationButton.setTextLine(message);
        buttonRenderer.renderButton(notificationButton);
        notificationButton.setAnimType(ButtonLighty.ANIM_UP);
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
        ButtonLighty notificationButton = getButtonById(3614);
        notificationButton.destroy();
        notificationButton.factorModel.beginDestroying(1, 3);
    }


    public boolean notificationIsDestroying() {
        ButtonLighty notificationButton = getButtonById(3614);
        if (notificationButton == null) return false;
        if (notificationButton.factorModel.getGravity() < 0) return true;
        return false;
    }


    private String getColorNameByIndex(int index) {
        switch (index) {
            default:
            case 6:
            case 0:
                return languagesManager.getString("green");
            case 1:
            case 5:
                return languagesManager.getString("red");
            case 2:
                return languagesManager.getString("magenta");
            case 3:
                return languagesManager.getString("cyan");
            case 4:
                return languagesManager.getString("yellow");
            case 7:
                return languagesManager.getString("gray");
        }
    }


    public void createAfterGameMenu(int whoWon, boolean playerIsWinner) {
        beginMenuCreation();

        yioGdxGame.setGamePaused(true);
        yioGdxGame.beginBackgroundChange(3, true, false);

        String message = "ERROR";
        if (playerIsWinner) {
            message = getColorNameByIndex(whoWon) + " " +
                    languagesManager.getString("player") + " " +
                    languagesManager.getString("won") + ".";
        } else {
            message = getColorNameByIndex(whoWon) + " " +
                    languagesManager.getString("ai") + " " +
                    languagesManager.getString("won") + ".";
        }
        if (yioGdxGame.gameController.completedCampaignLevel(whoWon))
            message = languagesManager.getString("level_complete");
        if (YioGdxGame.CHECKING_BALANCE_MODE && yioGdxGame.gamesPlayed() % 50 == 0)
            YioGdxGame.say(yioGdxGame.gamesPlayed() + " : " + yioGdxGame.getBalanceIndicatorString());
        ButtonLighty textPanel = buttonFactory.getButton(generateRectangle(0.05, 0.4, 0.9, 0.2), 60, null);
        textPanel.cleatText();
        textPanel.addTextLine(message);
        textPanel.addTextLine("");
        textPanel.addTextLine("");
        buttonRenderer.renderButton(textPanel);
        textPanel.setTouchable(false);
        textPanel.setAnimType(ButtonLighty.ANIM_FROM_CENTER);

        ButtonLighty okButton = buttonFactory.getButton(generateRectangle(0.55, 0.4, 0.4, 0.07), 62, null);
        if (yioGdxGame.gameController.completedCampaignLevel(whoWon))
            okButton.setTextLine(languagesManager.getString("next"));
        else okButton.setTextLine(languagesManager.getString("end_game_ok"));
        buttonRenderer.renderButton(okButton);
        okButton.setShadow(false);
        okButton.setReactBehavior(ReactBehavior.rbChooseGameModeMenu);
        if (yioGdxGame.gameController.completedCampaignLevel(whoWon))
            okButton.setReactBehavior(ReactBehavior.rbNextLevel);
        okButton.setAnimType(ButtonLighty.ANIM_FROM_CENTER);

        ButtonLighty statisticsButton = buttonFactory.getButton(generateRectangle(0.05, 0.4, 0.5, 0.07), 61, languagesManager.getString("statistics"));
        statisticsButton.setShadow(false);
        statisticsButton.setReactBehavior(ReactBehavior.rbStatisticsMenu);
        statisticsButton.setAnimType(ButtonLighty.ANIM_FROM_CENTER);

        endMenuCreation();
    }


    public void createStatisticsMenu(Statistics statistics) {
        beginMenuCreation();

        yioGdxGame.beginBackgroundChange(0, false, true);

        spawnBackButton(111, ReactBehavior.rbChooseGameModeMenu);

        ButtonLighty textPanel = buttonFactory.getButton(generateRectangle(0.05, 0.1, 0.9, 0.7), 112, null);
        textPanel.cleatText();
        textPanel.addTextLine(languagesManager.getString("statistics") + ":");
        textPanel.addTextLine(languagesManager.getString("turns_made") + " " + statistics.turnsMade);
        textPanel.addTextLine(languagesManager.getString("units_died") + " " + statistics.unitsDied);
        textPanel.addTextLine(languagesManager.getString("units_produced") + " " + statistics.unitsProduced);
        textPanel.addTextLine(languagesManager.getString("money_spent") + " " + statistics.moneySpent);
        for (int i = 0; i < 10; i++) {
            textPanel.addTextLine("");
        }
        buttonRenderer.renderButton(textPanel);
        textPanel.setTouchable(false);
        textPanel.setAnimType(ButtonLighty.ANIM_FROM_CENTER);

        endMenuCreation();
    }


    private void spawnBackButton(int id, ReactBehavior reactBehavior) {
        ButtonLighty backButton = buttonFactory.getButton(generateRectangle(0.05, 0.9, 0.4, 0.07), id, null);
        loadButtonOnce(backButton, "back_icon.png");
        backButton.setShadow(true);
        backButton.setAnimType(ButtonLighty.ANIM_UP);
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
        if (index < 0 || index > YioGdxGame.INDEX_OF_LAST_LEVEL) return;
        TextureRegion textureRegion;
        if (scrollerYio.isLevelComplete(index)) textureRegion = openedLevelIcon;
        else if (scrollerYio.isLevelLocked(index)) textureRegion = lockedLevelIcon;
        else textureRegion = unlockedLevelIcon;
        scrollerYio.icons.set(index, textureRegion);
        scrollerYio.updateCacheLine(index);
    }


    public void createGameHackerMenu() {
        beginMenuCreation();

        yioGdxGame.beginBackgroundChange(0, false, false);
        ArrayList<String> text = new ArrayList<String>();
        text.add("Это специальный билд");
        text.add("Для game_hacker_#9");
        text.add("");
        text.add(":D");
        ButtonLighty textPanel = buttonFactory.getButton(generateRectangle(0.1, 0.4, 0.8, 0.3), 312783, null);
        if (textPanel.notRendered()) {
            textPanel.addManyLines(text);
            for (int i = 0; i < (7 - text.size()); i++) textPanel.addTextLine(" ");
            buttonRenderer.renderButton(textPanel);
        }
        textPanel.setTouchable(false);
        textPanel.setAnimType(ButtonLighty.ANIM_DEFAULT);

        ButtonLighty okButton = buttonFactory.getButton(generateRectangle(0.1, 0.3, 0.8, 0.1), 8912732, "Ok");
        okButton.setReactBehavior(ReactBehavior.rbMainMenu);
        okButton.setAnimType(ButtonLighty.ANIM_DEFAULT);

        endMenuCreation();
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
        parameter.characters = YioGdxGame.FONT_CHARACTERS;
        parameter.flip = true;
        BitmapFont font = generator.generateFont(parameter);

        ButtonLighty textPanel = buttonFactory.getButton(generateRectangle(0.1, 0.2, 0.8, 0.7), 6731267, null);
        if (textPanel.notRendered()) {
            textPanel.addManyLines(text);
            for (int i = 0; i < (lineNumber - text.size()); i++) textPanel.addTextLine(" ");
            buttonRenderer.renderButton(textPanel, font, FONT_SIZE);
        }
        textPanel.setTouchable(false);

        ButtonLighty okButton = buttonFactory.getButton(generateRectangle(0.1, 0.1, 0.8, 0.1), 73612321, "Ok");
        okButton.setReactBehavior(ReactBehavior.rbInGameMenu);

        endMenuCreation();
    }


    public void close() {

    }
}
