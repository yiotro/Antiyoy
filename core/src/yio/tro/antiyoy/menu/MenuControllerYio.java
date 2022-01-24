package yio.tro.antiyoy.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.antiyoy.YioGdxGame;
import yio.tro.antiyoy.gameplay.ColorsManager;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.scenes.Scenes;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.LanguagesManager;
import yio.tro.antiyoy.stuff.RectangleYio;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.StringTokenizer;


public class MenuControllerYio {

    public static int SPAWN_ANIM = 2, DESTROY_ANIM = 2;
    public static double SPAWN_SPEED = 1.5, DESTROY_SPEED = 1.5;

    public final YioGdxGame yioGdxGame;
    private final ButtonFactory buttonFactory;
    public ButtonRenderer buttonRenderer;
    TextureRegion unlockedLevelIcon, lockedLevelIcon, openedLevelIcon;
    public final ArrayList<ButtonYio> buttons;
    public SpecialActionController specialActionController;
    public ArrayList<InterfaceElement> interfaceElements;


    public MenuControllerYio(YioGdxGame yioGdxGame) {
        this.yioGdxGame = yioGdxGame;
        buttonFactory = new ButtonFactory(this);
        buttons = new ArrayList<ButtonYio>();
        buttonRenderer = new ButtonRenderer();
        specialActionController = new SpecialActionController(this);
        unlockedLevelIcon = GraphicsYio.loadTextureRegion("unlocked_level_icon.png", true);
        lockedLevelIcon = GraphicsYio.loadTextureRegion("locked_level_icon.png", true);
        openedLevelIcon = GraphicsYio.loadTextureRegion("opened_level_icon.png", true);
        interfaceElements = new ArrayList<>();
        applyAnimStyle();

        Scenes.createScenes(this);

        prepareCertainScenes();
        Scenes.sceneMainMenu.create();
        checkToCreateSingleMessage();
    }


    private void prepareCertainScenes() {
        Scenes.sceneDiplomaticExchange.create();
        Scenes.sceneDiplomaticExchange.hide();
    }


    private void checkToCreateSingleMessage() {

    }


    public void move() {
        specialActionController.move();
        moveButtons();
        moveInterfaceElements();
        checkToPerformAction();
    }


    private void moveButtons() {
        for (ButtonYio buttonYio : buttons) {
            buttonYio.move();
        }
    }


    private void moveInterfaceElements() {
        for (InterfaceElement interfaceElement : interfaceElements) {
            if (!interfaceElement.isVisible()) continue;
            interfaceElement.move();
        }
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


    public void addButtonToArray(ButtonYio buttonYio) {
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


    public void onAppPause() {
        for (ButtonYio button : buttons) {
            button.onAppPause();
        }

        for (InterfaceElement interfaceElement : interfaceElements) {
            interfaceElement.onAppPause();
        }
    }


    public void onAppResume() {
        for (ButtonYio button : buttons) {
            button.onAppResume();
        }

        for (InterfaceElement interfaceElement : interfaceElements) {
            interfaceElement.onAppResume();
        }
    }


    public void loadButtonOnce(ButtonYio buttonYio, String fileName) {
        if (buttonYio.notRendered()) {
            buttonYio.loadTexture(fileName);
        }
    }


    public ButtonYio getCoinButton() {
        return Scenes.sceneFinances.coinButton;
    }


    public InterfaceElement getPreviouslyAddedElement() {
        return interfaceElements.get(interfaceElements.size() - 2);
    }


    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        for (int i = interfaceElements.size() - 1; i >= 0; i--) {
            InterfaceElement interfaceElement = interfaceElements.get(i);
            if (!interfaceElement.isTouchable() || !interfaceElement.isVisible()) continue;
            if (interfaceElement.touchDown(screenX, screenY, pointer, button)) return true;
        }

        for (int i = buttons.size() - 1; i >= 0; i--) {
            ButtonYio buttonYio = buttons.get(i);
            if (!buttonYio.isTouchable()) continue;

            if (buttonYio.checkTouch(screenX, screenY, pointer, button)) {
                return true;
            }
        }

        return false;
    }


    public void touchDragged(int screenX, int screenY, int pointer) {
        // order doesn't matter here because no 'break' here
        for (InterfaceElement interfaceElement : interfaceElements) {
            if (interfaceElement.isTouchable() && interfaceElement.isVisible()) {
                interfaceElement.touchDrag(screenX, screenY, pointer);
            }
        }
    }


    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        for (int i = interfaceElements.size() - 1; i >= 0; i--) {
            InterfaceElement interfaceElement = interfaceElements.get(i);
            if (interfaceElement.isTouchable() && interfaceElement.isVisible()) {
                if (interfaceElement.touchUp(screenX, screenY, pointer, button)) return true;
            }
        }

        return false;
    }


    public boolean onMouseWheelScrolled(int amount) {
        for (InterfaceElement interfaceElement : interfaceElements) {
            if (interfaceElement.isTouchable() && interfaceElement.isVisible()) {
                if (interfaceElement.onMouseWheelScrolled(amount)) {
                    return true;
                }
            }
        }

        return false;
    }


    public void beginMenuCreation() {
        for (InterfaceElement interfaceElement : interfaceElements) {
            if (interfaceElement.isAnotherSceneCreationIgnored()) continue;
            interfaceElement.destroy();
        }

        for (ButtonYio buttonYio : buttons) {
            buttonYio.destroy();

            if (buttonYio.id == 3 && buttonYio.isVisible()) {
                buttonYio.appearFactor.setValues(1, 0);
                buttonYio.appearFactor.destroy(1, 2);
            }
            if (buttonYio.id >= 22 && buttonYio.id <= 29 && buttonYio.isVisible()) {
                buttonYio.appearFactor.destroy(1, 2.1);
            }
            if (buttonYio.id == 30 && buttonYio.appearFactor.get() > 0) {
                buttonYio.appearFactor.setValues(1, 0);
                buttonYio.appearFactor.destroy(1, 1);
            }
        }

        if (yioGdxGame.gameView != null) {
            yioGdxGame.gameView.checkToDestroy();
        }
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


    public void renderTextAndSomeEmptyLines(ButtonYio buttonYio, String text, int emptyLines) {
        if (buttonYio.notRendered()) {
            buttonYio.addTextLine(text);
            for (int i = 0; i < emptyLines; i++) {
                buttonYio.addTextLine(" ");
            }
            buttonRenderer.renderButton(buttonYio);
        }
    }


    public void hideAllEditorPanels() {
        Scenes.sceneEditorHexPanel.hide();
        Scenes.sceneEditorObjectPanel.hide();
        Scenes.sceneEditorParams.hide();
        Scenes.sceneEditorAutomationPanel.hide();
        Scenes.sceneEditorGameRulesPanel.hide();
        Scenes.sceneEditorDiplomacy.hide();
        Scenes.sceneEditorEditRelation.hide();

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


    public ColorsManager getColorsManager() {
        return yioGdxGame.gameController.colorsManager;
    }


    public void forceDyingButtonsToEnd() {
        for (ButtonYio button : buttons) {
            if (button.appearFactor.getGravity() >= 0) continue;
            button.appearFactor.setValues(0, 0);
        }
        for (InterfaceElement interfaceElement : interfaceElements) {
            if (interfaceElement.getFactor().getGravity() >= 0) continue;
            interfaceElement.getFactor().setValues(0, 0);
        }
    }


    public ButtonYio spawnBackButton(int id, Reaction reaction) {
        double x = 0.05;
        double y = 0.9;
        double width = 0.4;
        double height = 0.07;

        ButtonYio backButton = buttonFactory.getButton(generateRectangle(x, y, width, height), id, null);
        loadButtonOnce(backButton, "menu/back_icon.png");
        backButton.setShadow(true);
        backButton.setAnimation(Animation.up);
        backButton.setReaction(reaction);
        backButton.setTouchOffset(0.05f * Gdx.graphics.getHeight());
        backButton.tagAsBackButton();

        double arrowVerSize = 0.07;
        RectangleYio position = generateSquare(x + width / 2 - GraphicsYio.convertToWidth(arrowVerSize) / 2, y + height / 2 - arrowVerSize / 2, arrowVerSize);
        ButtonYio arrowIcon = buttonFactory.getButton(position, id + 317263313, null);
        loadButtonOnce(arrowIcon, "menu/arrow.png");
        arrowIcon.setShadow(false);
        arrowIcon.setAnimation(Animation.up);
        arrowIcon.setTouchable(false);

        return backButton;
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


    public void addElementToScene(InterfaceElement element) {
        // considered that ui element is not in array at this moment
        ListIterator iterator = interfaceElements.listIterator();
        while (iterator.hasNext()) {
            iterator.next();
        }
        iterator.add(element);
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


    public void onSkinChanged() {

    }


    public void clear() {
        buttons.clear();
    }


    public void close() {

    }
}
