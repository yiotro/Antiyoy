package yio.tro.antiyoy.gameplay;

import yio.tro.antiyoy.gameplay.campaign.CampaignProgressManager;
import yio.tro.antiyoy.gameplay.loading.LoadingManager;
import yio.tro.antiyoy.gameplay.loading.LoadingParameters;
import yio.tro.antiyoy.gameplay.loading.LoadingType;
import yio.tro.antiyoy.menu.AbstractRectangularUiElement;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.scenes.Scenes;
import yio.tro.antiyoy.stuff.LanguagesManager;
import yio.tro.antiyoy.stuff.RectangleYio;

public class TutorialScriptGenericRules extends TutorialScript{


    final String map = "11 7 7 0 0 0 10#11 8 7 0 0 0 10#12 2 1 0 0 0 6#12 3 1 3 0 0 6#12 6 7 0 0 0 10#13 2 1 0 1 0 6#13 3 1 0 0 0 6#13 5 7 0 0 0 10#14 2 1 0 0 0 6#14 3 7 0 0 0 10#14 4 7 0 0 0 10#14 5 7 0 0 0 10#15 2 7 0 0 0 10#15 3 7 0 0 0 10#15 4 7 0 0 0 10#15 5 7 0 0 0 10#15 10 7 0 0 0 10#15 11 7 0 0 0 10#16 3 7 0 0 0 10#16 4 7 0 0 0 10#16 5 7 0 0 0 10#16 6 7 0 0 0 10#16 9 7 0 0 0 10#16 10 7 0 0 0 10#16 11 0 0 2 1 58#17 2 7 2 0 0 10#17 3 7 0 0 0 10#17 4 7 0 0 0 10#17 5 7 0 0 0 10#17 6 7 0 0 0 10#17 7 7 0 0 0 10#17 8 7 0 0 0 10#17 9 7 0 0 0 10#17 10 0 0 0 0 58#17 11 0 0 0 0 58#18 2 7 0 0 0 10#18 4 7 0 0 0 10#18 5 7 0 0 0 10#18 6 7 0 0 0 10#18 7 7 0 0 0 10#18 8 7 0 0 0 10#18 9 0 0 1 1 58#18 10 0 0 0 0 58#18 11 0 0 0 0 58#19 6 7 0 0 0 10#19 7 7 0 0 0 10#19 8 7 1 0 0 10#19 9 0 0 0 0 58#19 10 0 0 0 0 58#19 11 0 3 0 0 58#20 5 7 2 0 0 10#20 6 7 0 0 0 10#20 7 7 0 0 0 10#20 8 7 1 0 0 10#20 9 7 0 0 0 10#20 11 0 2 0 0 58#21 2 7 0 0 0 10#21 4 7 2 0 0 10#21 5 7 2 0 0 10#21 6 7 0 0 0 10#21 7 7 2 0 0 10#21 8 7 2 0 0 10#22 2 7 0 0 0 10#22 3 7 0 0 0 10#22 7 7 0 0 0 10#22 8 7 0 0 0 10#22 9 7 0 0 0 10#23 2 7 0 0 0 10#23 7 7 0 0 0 10";
    public static final int STEP_GREETINGS = 0;
    public static final int STEP_SELECT_PROVINCE = 1;
    public static final int STEP_SELECT_UNIT = 2;
    public static final int STEP_MOVE_UNIT = 3;
    public static final int STEP_INCOME = 4;
    public static final int STEP_CHOOSE_FARM = 5;
    public static final int STEP_BUILD_FARM = 6;
    public static final int STEP_ABOUT_FARM = 7;
    public static final int STEP_CHOOSE_PEASANT = 8;
    public static final int STEP_BUILD_PEASANT = 9;
    public static final int STEP_CAPTURE_PEASANT = 10;
    public static final int STEP_CHOOSE_TOWER = 11;
    public static final int STEP_BUILD_TOWER = 12;
    public static final int STEP_ABOUT_TOWERS = 13;
    public static final int STEP_CHOOSE_SPEARMAN = 14;
    public static final int STEP_BUILD_SPEARMAN = 15;
    public static final int STEP_UNITS_CONSUME_MONEY = 16;
    public static final int STEP_UNDO_SPEARMAN = 17;
    public static final int STEP_WHY_UNITS_DIE = 18;
    public static final int STEP_ABOUT_TREES_ONE = 19;
    public static final int STEP_ABOUT_TREES_TWO = 20;
    public static final int STEP_END_TURN = 21;
    public static final int STEP_CHOOSE_TO_MERGE = 22;
    public static final int STEP_MERGE_UNITS = 23;
    public static final int STEP_LONG_TAP = 24;
    public static final int STEP_SEVERAL_PROVINCES = 25;
    public static final int STEP_GOODBYE = 26;
    int currentStep;
    LanguagesManager languagesManager;
    boolean waitingBeforeNextStep;
    long timeToCheck, timeForNextStep;


    public TutorialScriptGenericRules(GameController gameController) {
        super(gameController);
    }


    @Override
    public void createTutorialGame() {
        LoadingParameters instance = LoadingParameters.getInstance();
        instance.loadingType = LoadingType.tutorial;
        instance.activeHexes = map;
        instance.playersNumber = 1;
        instance.fractionsQuantity = 5;
        instance.levelSize = 1;
        instance.difficulty = 0;
        instance.colorOffset = 0;
        instance.slayRules = false;
        LoadingManager.getInstance().startGame(instance);

        menuControllerYio = gameController.yioGdxGame.menuControllerYio;
        languagesManager = LanguagesManager.getInstance();
        currentStep = -1;
        waitingBeforeNextStep = true;
        ignoreAll();
        showTutorialTip("gen_greetings");
        changeThreeDotsReaction();
    }


    private void changeThreeDotsReaction() {
        menuControllerYio.getButtonById(30).setReaction(new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                Scenes.sceneNotification.hideNotification();
                Scenes.sceneChooseGameMode.create();
                CampaignProgressManager.getInstance().markLevelAsCompleted(0);
                getYioGdxGame(buttonYio).setGamePaused(true);
                getYioGdxGame(buttonYio).setAnimToPlayButtonSpecial();
            }
        });
    }


    private Hex getHex(int x, int y) {
        return gameController.fieldManager.field[x][y];
    }


    private void pointToHex(int x, int y) {
        gameController.forefinger.setPointTo(getHex(x, y));
    }


    private void pointToMenu(double x, double y, double rotation) {
        gameController.forefinger.setPointTo(x, y, rotation);
    }


    private void showMessage(String key) {
        Scenes.sceneNotification.show(languagesManager.getString(key), false);
    }


    private void showTutorialTip(String key) {
        Scenes.sceneTutorialTip.createTutorialTipWithFixedHeight(menuControllerYio.getArrayListFromString(languagesManager.getString(key)), 6);
        tipIsCurrentlyShown = true;
    }


    private void stepSetup() {
        ButtonYio buttonYio;
        gameController.setIgnoreMarch(true);
        switch (currentStep) {
            default:
            case STEP_GREETINGS:
                CampaignProgressManager.getInstance().markLevelAsCompleted(0);
                break;
            case STEP_SELECT_PROVINCE:
                setHexToRespondByFraction(0);
                pointToHex(19, 11);
                break;
            case STEP_SELECT_UNIT:
                setOnlyHexToRespond(18, 9);
                break;
            case STEP_MOVE_UNIT:
                setOnlyHexToRespond(17, 9);
                break;
            case STEP_INCOME:
                ignoreAll();
                break;
            case STEP_CHOOSE_FARM:
                buttonYio = setOnlyButtonToRespond(38, "gen_press_button");
                pointToMenu(buttonYio.x1, buttonYio.y2, -0.75 * Math.PI);
                break;
            case STEP_BUILD_FARM:
                setOnlyHexToRespond(18, 11);
                break;
            case STEP_ABOUT_FARM:
                ignoreAll();
                break;
            case STEP_CHOOSE_PEASANT:
                buttonYio = setOnlyButtonToRespond(39, "gen_press_button");
                pointToMenu(buttonYio.x2, buttonYio.y2, 0.75 * Math.PI);
                break;
            case STEP_BUILD_PEASANT:
                setOnlyHexToRespond(19 ,9);
                break;
            case STEP_CAPTURE_PEASANT:
                ignoreAll();
                getHex(19, 9).setIgnoreTouch(false);
                getHex(20, 9).setIgnoreTouch(false);
                pointToHex(20, 9);
                showMessage("gen_capture_hex");
                break;
            case STEP_CHOOSE_TOWER:
                buttonYio = setOnlyButtonToRespond(38, "gen_press_button_twice"); // tower
                pointToMenu(buttonYio.x1, buttonYio.y2, -0.75 * Math.PI);
                break;
            case STEP_BUILD_TOWER:
                setOnlyHexToRespond(18, 10);
                break;
            case STEP_ABOUT_TOWERS:
                ignoreAll();
                break;
            case STEP_CHOOSE_SPEARMAN:
                buttonYio = setOnlyButtonToRespond(39, "gen_press_button_twice");
                pointToMenu(buttonYio.x2, buttonYio.y2, 0.75 * Math.PI);
                break;
            case STEP_BUILD_SPEARMAN:
                setOnlyHexToRespond(19, 8);
                break;
            case STEP_UNITS_CONSUME_MONEY:
                ignoreAll();
                break;
            case STEP_UNDO_SPEARMAN:
                buttonYio = setOnlyButtonToRespond(32, "tut_tap_to_undo");
                pointToMenu(buttonYio.x2, buttonYio.y2, 0.75 * Math.PI);
                break;
            case STEP_WHY_UNITS_DIE:
                ignoreAll();
                break;
            case STEP_ABOUT_TREES_ONE:
                ignoreAll();
                break;
            case STEP_ABOUT_TREES_TWO:
                ignoreAll();
                break;
            case STEP_END_TURN:
                setInterfaceElementToRespond(Scenes.sceneGameOverlay.endTurnButtonElement, "tut_tap_to_end_turn");
                break;
            case STEP_CHOOSE_TO_MERGE:
                setOnlyHexToRespond(17, 9);
                break;
            case STEP_MERGE_UNITS:
                setOnlyHexToRespond(20, 9);
                break;
            case STEP_LONG_TAP:
                ignoreAll();
                break;
            case STEP_SEVERAL_PROVINCES:
                ignoreAll();
                break;
            case STEP_GOODBYE:
                resetIgnores();
                menuControllerYio.getButtonById(30).setReaction(Reaction.rbPauseMenu);
                break;
        }
    }


    private void ignoreAll() {
        allButtonsIgnoreTouches();
        allHexesIgnoreTouches();
    }


    private void checkToShowTip() {
        switch (currentStep + 1) {
            default:
            case STEP_GREETINGS:
                break;
            case STEP_SELECT_PROVINCE:
                showTutorialTip("gen_select_province");
                break;
            case STEP_SELECT_UNIT:
                showTutorialTip("gen_select_unit");
                break;
            case STEP_MOVE_UNIT:
                showTutorialTip("gen_move_unit");
                break;
            case STEP_INCOME:
                showTutorialTip("gen_income");
                break;
            case STEP_CHOOSE_FARM:
                showTutorialTip("gen_lets_build_farm");
                break;
            case STEP_BUILD_FARM:
                showTutorialTip("gen_build_farm");
                break;
            case STEP_ABOUT_FARM:
                showTutorialTip("gen_about_farms");
                break;
            case STEP_CHOOSE_PEASANT:
                showTutorialTip("gen_lets_build_peasant");
                break;
            case STEP_BUILD_PEASANT:
                showTutorialTip("gen_build_peasant");
                break;
            case STEP_CAPTURE_PEASANT:
                showTutorialTip("gen_capture_peasant");
                break;
            case STEP_CHOOSE_TOWER:
                showTutorialTip("gen_lets_build_tower");
                break;
            case STEP_BUILD_TOWER:
                showTutorialTip("gen_build_tower");
                break;
            case STEP_ABOUT_TOWERS:
                showTutorialTip("gen_about_towers");
                break;
            case STEP_CHOOSE_SPEARMAN:
                showTutorialTip("gen_lets_build_spearman");
                break;
            case STEP_BUILD_SPEARMAN:
                showTutorialTip("gen_build_spearman");
                break;
            case STEP_UNITS_CONSUME_MONEY:
                showTutorialTip("gen_units_consume_money");
                break;
            case STEP_UNDO_SPEARMAN:
                showTutorialTip("gen_undo_spearman");
                break;
            case STEP_WHY_UNITS_DIE:
                showTutorialTip("gen_why_units_die");
                break;
            case STEP_ABOUT_TREES_ONE:
                showTutorialTip("gen_about_trees_one");
                break;
            case STEP_ABOUT_TREES_TWO:
                showTutorialTip("gen_about_trees_two");
                break;
            case STEP_END_TURN:
                showTutorialTip("gen_end_turn");
                break;
            case STEP_CHOOSE_TO_MERGE:
                showTutorialTip("gen_choose_to_merge");
                break;
            case STEP_MERGE_UNITS:
                showTutorialTip("gen_merge_units");
                break;
            case STEP_LONG_TAP:
                showTutorialTip("gen_long_tap");
                break;
            case STEP_SEVERAL_PROVINCES:
                showTutorialTip("gen_several_provinces");
                break;
            case STEP_GOODBYE:
                showTutorialTip("tip_help");
                Scenes.sceneTutorialTip.addHelpButtonToTutorialTip();
                break;
        }
    }


    private boolean isStepComplete() {
        switch (currentStep) {
            default:
            case STEP_GREETINGS:
                return true;
            case STEP_SELECT_PROVINCE:
                if (gameController.fieldManager.selectedProvince != null) return true;
                return false;
            case STEP_SELECT_UNIT:
                if (gameController.selectionManager.selectedUnit != null) return true;
                return false;
            case STEP_MOVE_UNIT:
                if (getHex(17, 9).containsUnit()) return true;
                return false;
            case STEP_INCOME:
                return true;
            case STEP_CHOOSE_FARM:
                if (gameController.selectionManager.getTipType() == SelectionTipType.FARM) return true;
                return false;
            case STEP_BUILD_FARM:
                if (getHex(18, 11).objectInside == Obj.FARM) return true;
                return false;
            case STEP_ABOUT_FARM:
                return true;
            case STEP_CHOOSE_PEASANT:
                if (gameController.selectionManager.getTipType() == SelectionTipType.UNIT_1) return true;
                return false;
            case STEP_BUILD_PEASANT:
                if (getHex(19, 9).containsUnit()) return true;
                return false;
            case STEP_CAPTURE_PEASANT:
                if (getHex(20, 9).containsUnit()) return true;
                return false;
            case STEP_CHOOSE_TOWER:
                if (gameController.selectionManager.getTipType() == SelectionTipType.TOWER) return true;
                return false;
            case STEP_BUILD_TOWER:
                if (getHex(18, 10).objectInside == Obj.TOWER) return true;
                return false;
            case STEP_ABOUT_TOWERS:
                return true;
            case STEP_CHOOSE_SPEARMAN:
                if (gameController.selectionManager.getTipType() == SelectionTipType.UNIT_2) return true;
                return false;
            case STEP_BUILD_SPEARMAN:
                if (getHex(19, 8).containsUnit()) return true;
                return false;
            case STEP_UNITS_CONSUME_MONEY:
                return true;
            case STEP_UNDO_SPEARMAN:
                if (!getHex(19, 8).containsUnit()) return true;
                return false;
            case STEP_WHY_UNITS_DIE:
                return true;
            case STEP_ABOUT_TREES_ONE:
                return true;
            case STEP_ABOUT_TREES_TWO:
                return true;
            case STEP_END_TURN:
                Hex hex = getHex(17, 9);
                if (hex == null) return false;
                Unit unit = hex.unit;
                return unit != null && unit.isReadyToMove();
            case STEP_CHOOSE_TO_MERGE:
                if (gameController.selectionManager.selectedUnit != null) return true;
                return false;
            case STEP_MERGE_UNITS:
                if (getHex(20, 9).unit.strength == 2) return true;
                return false;
            case STEP_LONG_TAP:
                return true;
            case STEP_SEVERAL_PROVINCES:
                return true;
            case STEP_GOODBYE:
                return false;
        }
    }


    private ButtonYio setOnlyButtonToRespond(int id, String message) {
        ignoreAll();
        ButtonYio buttonYio = menuControllerYio.getButtonById(id);
        buttonYio.setTouchable(true);
        showMessage(message);
        return buttonYio;
    }


    private void setInterfaceElementToRespond(AbstractRectangularUiElement uiElement, String message) {
        ignoreAll();
        showMessage(message);
        RectangleYio viewPosition = uiElement.viewPosition;
        uiElement.setTouchable(true);
        pointToMenu(
                viewPosition.x,
                viewPosition.y + viewPosition.height,
                -0.75 * Math.PI
        );
    }


    private void resetIgnores() {
        gameController.setIgnoreMarch(false);
        for (int i = 31; i <= 32; i++) {
            ButtonYio buttonYio = menuControllerYio.getButtonById(i);
            if (buttonYio == null) continue;
            buttonYio.setTouchable(true);
        }
        for (int i = 38; i <= 39; i++) {
            ButtonYio buttonYio = menuControllerYio.getButtonById(i);
            if (buttonYio == null) continue;
            buttonYio.setTouchable(true);
        }
        for (int i = 0; i < gameController.fieldManager.fWidth; i++) {
            for (int j = 0; j < gameController.fieldManager.fHeight; j++) {
                gameController.fieldManager.field[i][j].setIgnoreTouch(false);
            }
        }
        Scenes.sceneGameOverlay.endTurnButtonElement.setTouchable(true);
    }


    private void allButtonsIgnoreTouches() {
        for (int i = 31; i <= 32; i++) {
            ButtonYio buttonYio = menuControllerYio.getButtonById(i);
            if (buttonYio == null) continue;
            buttonYio.setTouchable(false);
        }
        for (int i = 38; i <= 39; i++) {
            ButtonYio buttonYio = menuControllerYio.getButtonById(i);
            if (buttonYio == null) continue;
            buttonYio.setTouchable(false);
        }
        Scenes.sceneGameOverlay.endTurnButtonElement.setTouchable(false);
    }


    private void setOnlyHexToRespond(int x, int y) {
        ignoreAll();
        getHex(x, y).setIgnoreTouch(false);
        pointToHex(x, y);
    }


    private void setHexToRespondByFraction(int fraction) {
        ignoreAll();
        for (Hex activeHex : gameController.fieldManager.activeHexes) {
            if (activeHex.fraction == fraction && !activeHex.containsUnit()) {
                activeHex.setIgnoreTouch(false);
            }
        }
    }


    private void allHexesIgnoreTouches() {
        for (int i = 0; i < gameController.fieldManager.fWidth; i++) {
            for (int j = 0; j < gameController.fieldManager.fHeight; j++) {
                gameController.fieldManager.field[i][j].setIgnoreTouch(true);
            }
        }
    }


    @Override
    public void move() {
        if (waitingBeforeNextStep) {
            if (gameController.currentTime > timeForNextStep && !tipIsCurrentlyShown) {
                waitingBeforeNextStep = false;
                timeToCheck = gameController.currentTime + 200;
                currentStep++;
                stepSetup();
            }
        } else {
            if (gameController.currentTime > timeToCheck) {
                timeToCheck = gameController.currentTime + 200;
                if (isStepComplete()) {
                    allHexesIgnoreTouches();
                    allButtonsIgnoreTouches();
                    waitingBeforeNextStep = true;
                    timeForNextStep = gameController.currentTime + 500;
                    Scenes.sceneNotification.hideNotification();
                    gameController.forefinger.hide();
                    checkToShowTip();
                }
            }
        }
    }

}
