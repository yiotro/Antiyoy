package yio.tro.antiyoy.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.antiyoy.*;
import yio.tro.antiyoy.gameplay.game_view.GameView;
import yio.tro.antiyoy.menu.behaviors.ReactBehavior;
import yio.tro.antiyoy.factor_yio.FactorYio;
import yio.tro.antiyoy.menu.scenes.*;
import yio.tro.antiyoy.stuff.LanguagesManager;
import yio.tro.antiyoy.stuff.RectangleYio;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.StringTokenizer;

/**
 * Created by ivan on 22.07.14.
 */
public class MenuControllerYio {

    public static int SPAWN_ANIM = 2, DESTROY_ANIM = 2;
    public static double SPAWN_SPEED = 1.5, DESTROY_SPEED = 1.5;

    public final YioGdxGame yioGdxGame;
    private final ButtonFactory buttonFactory;
    public ButtonRenderer buttonRenderer;
    TextureRegion unlockedLevelIcon, lockedLevelIcon, openedLevelIcon;
    public LevelSelector levelSelector;
    public FactorYio infoPanelFactor;
    public final ArrayList<ButtonYio> buttons;
    public ArrayList<SliderYio> sliders;
    public ArrayList<CheckButtonYio> checkButtons;
    public NotificationHolder notificationHolder;
    public SpecialActionController specialActionController;
    public ArrayList<InterfaceElement> interfaceElements;


    public MenuControllerYio(YioGdxGame yioGdxGame) {
        this.yioGdxGame = yioGdxGame;
        buttonFactory = new ButtonFactory(this);
        buttons = new ArrayList<ButtonYio>();
        buttonRenderer = new ButtonRenderer();
        infoPanelFactor = new FactorYio();
        specialActionController = new SpecialActionController(this);
        unlockedLevelIcon = GameView.loadTextureRegion("unlocked_level_icon.png", true);
        lockedLevelIcon = GameView.loadTextureRegion("locked_level_icon.png", true);
        openedLevelIcon = GameView.loadTextureRegion("opened_level_icon.png", true);
        interfaceElements = new ArrayList<>();
        initCheckButtons();
        initLevelSelector();
        initSliders();
        notificationHolder = new NotificationHolder();
        applyAnimStyle();

        Scenes.createScenes(this);

        Scenes.sceneMainMenu.create();
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
//        levelSelectorOld = new LevelSelectorOld(this, 175);
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

        sliders.get(0).setValues(0.5f, 1, 3, true, SliderYio.CONFIGURE_SIZE);
        sliders.get(1).setValues(0.2f, 0, 5, false, SliderYio.CONFIGURE_HUMANS);
        sliders.get(2).setValues(0.6, 2, 6, false, SliderYio.CONFIGURE_COLORS);
        sliders.get(3).setValues(0.33, 1, 5, true, SliderYio.CONFIGURE_DIFFICULTY);
        sliders.get(4).setValues(0, 0, 6, true, SliderYio.CONFIGURE_COLOR_OFFSET_SKIRMISH);
        sliders.get(5).setValues(0, 0, 3, true, SliderYio.CONFIGURE_SKIN);
        sliders.get(6).setValues(0, 0, 6, true, SliderYio.CONFIGURE_COLOR_OFFSET_CAMPAIGN);
//        sliders.get(7).setValues(0, 0, 1, false, SliderYio.CONFIGURE_COLOR_OFFSET_SKIRMISH); // autosave
//        sliders.get(8).setValues(0, 0, 1, true, SliderYio.CONFIGURE_ASK_END_TURN); // ask to end turn
        sliders.get(9).setValues(0.5, 0, 9, false, SliderYio.CONFIGURE_SENSITIVITY);
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

        for (InterfaceElement interfaceElement : interfaceElements) {
            if (interfaceElement.isVisible()) {
                interfaceElement.move();
            }
        }

        for (CheckButtonYio checkButton : checkButtons) {
            checkButton.move();
        }

        for (SliderYio sliderYio : sliders) {
            sliderYio.move();
        }

        for (ButtonYio buttonYio : buttons) {
            buttonYio.move();
        }

        checkToPerformAction();
    }


    private void checkToPerformAction() {
        for (int i = interfaceElements.size() - 1; i >= 0; i--) {
            InterfaceElement interfaceElement = interfaceElements.get(i);
            if (!interfaceElement.isVisible()) continue;

            if (interfaceElement.checkToPerformAction()) return;
        }

        for (int i = buttons.size() - 1; i >= 0; i--) {
            if (buttons.get(i).checkToPerformAction()) return;
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


    public void removeInterfaceElementFromArray(ButtonYio buttonYio) {
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


    public void onPause() {
        for (ButtonYio button : buttons) {
            button.onPause();
        }

        for (InterfaceElement interfaceElement : interfaceElements) {
            interfaceElement.onPause();
        }
    }


    public void onResume() {
        for (ButtonYio button : buttons) {
            button.onResume();
        }

        for (InterfaceElement interfaceElement : interfaceElements) {
            interfaceElement.onResume();
        }
    }


    public void loadButtonOnce(ButtonYio buttonYio, String fileName) {
        if (buttonYio.notRendered()) {
            buttonYio.loadTexture(fileName);
        }
    }


    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        for (SliderYio sliderYio : sliders) {
            if (sliderYio.touchDown(screenX, screenY)) return true;
        }

        if (levelSelector.touchDown(screenX, screenY, pointer, button)) return true;

        for (InterfaceElement interfaceElement : interfaceElements) {
            if (interfaceElement.isTouchable() && interfaceElement.isVisible()) {
                if (interfaceElement.touchDown(screenX, screenY, pointer, button)) return true;
            }
        }

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
        for (SliderYio sliderYio : sliders) {
            if (sliderYio.touchUp(screenX, screenY)) return true;
        }

        if (levelSelector.touchUp(screenX, screenY, pointer, button)) return true;

        for (InterfaceElement interfaceElement : interfaceElements) {
            if (interfaceElement.isTouchable() && interfaceElement.isVisible()) {
                if (interfaceElement.touchUp(screenX, screenY, pointer, button)) return true;
            }
        }

        return false;
    }


    public void touchDragged(int screenX, int screenY, int pointer) {
        for (SliderYio sliderYio : sliders) {
            sliderYio.touchDrag(screenX, screenY);
        }

        levelSelector.touchDrag(screenX, screenY, pointer);

        for (InterfaceElement interfaceElement : interfaceElements) {
            if (interfaceElement.isTouchable() && interfaceElement.isVisible()) {
                interfaceElement.touchDrag(screenX, screenY, pointer);
            }
        }
    }


    public void beginMenuCreation() {
        infoPanelFactor.setValues(1, 0);
        infoPanelFactor.beginDestroying(1, 3);
        levelSelector.destroy();

        for (InterfaceElement interfaceElement : interfaceElements) {
            interfaceElement.destroy();
        }

        for (CheckButtonYio checkButton : checkButtons) {
            checkButton.destroy();
        }

        for (SliderYio sliderYio : sliders) {
            sliderYio.appearFactor.beginDestroying(2, 2);
        }

        for (ButtonYio buttonYio : buttons) {
            buttonYio.destroy();

            if (buttonYio.id == 3 && buttonYio.isVisible()) {
                buttonYio.appearFactor.setValues(1, 0);
                buttonYio.appearFactor.beginDestroying(1, 2);
            }
            if (buttonYio.id >= 22 && buttonYio.id <= 29 && buttonYio.isVisible()) {
                buttonYio.appearFactor.beginDestroying(1, 2.1);
            }
            if (buttonYio.id == 30 && buttonYio.appearFactor.get() > 0) {
                buttonYio.appearFactor.setValues(1, 0);
                buttonYio.appearFactor.beginDestroying(1, 1);
            }
        }
        if (yioGdxGame.gameView != null) yioGdxGame.gameView.beginDestroyProcess();
    }


    public void endMenuCreation() {

    }


    void forceSpawningButtonsToTheEnd() {
        for (ButtonYio buttonYio : buttons) {
            if (buttonYio.appearFactor.getGravity() > 0) {
                buttonYio.appearFactor.setValues(1, 0);
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


    public RectangleYio generateRectangle(double x, double y, double width, double height) {
        return new RectangleYio(x * Gdx.graphics.getWidth(), y * Gdx.graphics.getHeight(), width * Gdx.graphics.getWidth(), height * Gdx.graphics.getHeight());
    }


    public RectangleYio generateSquare(double x, double y, double size) {
        return generateRectangle(x, y, size / YioGdxGame.screenRatio, size);
    }


    public String getString(String key) {
        return LanguagesManager.getInstance().getString(key);
    }


    public void createSpecialThanksMenu() {
        Scenes.sceneInfoMenu.create("special_thanks", ReactBehavior.rbInfo, 312837182);
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


    public void renderTextAndSomeEmptyLines(ButtonYio buttonYio, String text, int emptyLines) {
        if (buttonYio.notRendered()) {
            buttonYio.addTextLine(text);
            for (int i = 0; i < emptyLines; i++) {
                buttonYio.addTextLine(" ");
            }
            buttonRenderer.renderButton(buttonYio);
        }
    }


    public void saveMoreCampaignOptions() {
        Preferences prefs = Gdx.app.getPreferences("campaign_options");
        prefs.putInteger("color_offset", sliders.get(6).getCurrentRunnerIndex());
        prefs.putBoolean("slay_rules", getCheckButtonById(17).isChecked());
        prefs.flush();
    }


    public void loadMoreCampaignOptions() {
        Preferences prefs = Gdx.app.getPreferences("campaign_options");

        SliderYio colorOffsetSlider = sliders.get(6);
        colorOffsetSlider.setRunnerValueByIndex(prefs.getInteger("color_offset", 1));
        colorOffsetSlider.setConfigureType(SliderYio.CONFIGURE_COLOR_OFFSET_CAMPAIGN);

        getCheckButtonById(17).setChecked(prefs.getBoolean("slay_rules", false));
    }


    public void hideAllEditorPanels() {
        Scenes.sceneEditorHexPanel.hide();
        Scenes.sceneEditorObjectPanel.hide();
        Scenes.sceneEditorOptionsPanel.hide();
        Scenes.sceneEditorAutomationPanel.hide();
        Scenes.sceneEditorMoneyPanel.hide();

        yioGdxGame.gameController.getLevelEditor().onAllPanelsHide();
    }


    public void destroyButton(int id) {
        ButtonYio buttonYio = getButtonById(id);
        if (buttonYio == null) return;
        buttonYio.destroy();
    }


    public void applyAnimStyle() {
        SPAWN_ANIM = 2;
        SPAWN_SPEED = 1.5;
        DESTROY_ANIM = 2;
        DESTROY_SPEED = 1.5;
    }


    public void addHelpButtonToTutorialTip() {
        ButtonYio helpButton = buttonFactory.getButton(generateRectangle(0, 0.1, 0.6, 0.07), 54, null);
        helpButton.setTextLine(getString("help"));
        buttonRenderer.renderButton(helpButton);
        helpButton.setShadow(false);
        helpButton.setReactBehavior(ReactBehavior.rbHelpIndex);
        helpButton.setAnimType(ButtonYio.ANIM_COLLAPSE_DOWN);
        helpButton.appearFactor.beginSpawning(3, 1);
    }


    public String getColorNameByIndex(int index, String keyModifier) {
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


    public void forceDyingButtonsToEnd() {
        for (ButtonYio button : buttons) {
            if (button.appearFactor.getGravity() < 0) {
                button.appearFactor.setValues(0, 0);
            }
        }
    }


    public void spawnBackButton(int id, ReactBehavior reactBehavior) {
        ButtonYio backButton = buttonFactory.getButton(generateRectangle(0.05, 0.9, 0.4, 0.07), id, null);
        loadButtonOnce(backButton, "back_icon.png");
        backButton.setShadow(true);
        backButton.setAnimType(ButtonYio.ANIM_UP);
        backButton.setReactBehavior(reactBehavior);
        backButton.setTouchOffset(0.05f * Gdx.graphics.getHeight());
        yioGdxGame.registerBackButtonId(id);
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


    public ButtonFactory getButtonFactory() {
        return buttonFactory;
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


    public void addElementToScene(InterfaceElement interfaceElement) {
        // considered that menu block is not in array at this moment
        ListIterator iterator = interfaceElements.listIterator();
        while (iterator.hasNext()) {
            iterator.next();
        }
        iterator.add(interfaceElement);
    }


    public void removeElementFromScene(InterfaceElement interfaceElement) {
        ListIterator iterator = interfaceElements.listIterator();
        InterfaceElement currentElement;
        while (iterator.hasNext()) {
            currentElement = (InterfaceElement) iterator.next();
            if (currentElement == interfaceElement) {
                iterator.remove();
                return;
            }
        }
    }


    public YioGdxGame getYioGdxGame() {
        return yioGdxGame;
    }


    public ButtonRenderer getButtonRenderer() {
        return buttonRenderer;
    }


    public ArrayList<SliderYio> getSliders() {
        return sliders;
    }


    public void clear() {
        buttons.clear();
        initSliders();
    }


    public void close() {

    }
}
