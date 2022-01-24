package yio.tro.antiyoy.menu;

import yio.tro.antiyoy.KeyboardManager;
import yio.tro.antiyoy.SoundManagerYio;
import yio.tro.antiyoy.YioGdxGame;
import yio.tro.antiyoy.gameplay.FinishGameManager;
import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.GoalType;
import yio.tro.antiyoy.menu.editor_elements.color_picker.IColorChoiceListener;
import yio.tro.antiyoy.menu.keyboard.AbstractKbReaction;
import yio.tro.antiyoy.menu.render.MenuRender;
import yio.tro.antiyoy.menu.scenes.Scenes;
import yio.tro.antiyoy.menu.scenes.editor.IGoalChoiceListener;
import yio.tro.antiyoy.stuff.*;

public class EditGoalElement extends AbstractRectangularUiElement implements IGoalChoiceListener, IColorChoiceListener{

    public GoalType goalType;
    public RenderableTextYio title;
    public RenderableTextYio description;
    public SelectionEngineYio selectionEngineYio;
    public int chosenValue;
    public RenderableTextYio argument;
    public int chosenColor;
    public CircleYio colorIconPosition;


    public EditGoalElement(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
        goalType = null;
        initTitle();
        description = new RenderableTextYio();
        description.setFont(Fonts.smallerMenuFont);
        selectionEngineYio = new SelectionEngineYio();
        argument = new RenderableTextYio();
        argument.setFont(Fonts.smallerMenuFont);
        chosenColor = -1;
        chosenValue = -1;
        colorIconPosition = new CircleYio();
        colorIconPosition.setRadius(0.03f * GraphicsYio.width);
    }


    private void initTitle() {
        title = new RenderableTextYio();
        title.setFont(Fonts.smallerMenuFont);
        title.setString(LanguagesManager.getInstance().getString("goal") + ":");
        title.updateMetrics();
    }


    @Override
    protected void onMove() {
        updateTitlePosition();
        updateDescriptionPosition();
        updateArgumentPosition();
        updateColorIconPosition();
        moveSelection();
    }


    private void updateColorIconPosition() {
        colorIconPosition.center.x = (float) (viewPosition.x + viewPosition.width - 0.02f * GraphicsYio.width - colorIconPosition.radius);
        colorIconPosition.center.y = description.position.y - description.height / 2;
    }


    private void updateArgumentPosition() {
        argument.position.x = (float) (viewPosition.x + viewPosition.width - 0.02f * GraphicsYio.width - argument.width);
        argument.position.y = description.position.y;
        argument.updateBounds();
    }


    private void moveSelection() {
        if (touched) return;
        selectionEngineYio.move();
    }


    private void updateDescriptionPosition() {
        description.position.x = title.position.x;
        description.position.y = title.position.y - title.height - 0.015f * GraphicsYio.height;
        description.updateBounds();
    }


    public void loadValues() {
        FinishGameManager finishGameManager = getFinishGameManager();

        setGoalType(finishGameManager.goalType);

        if (isColorIconNeeded()) {
            chosenColor = finishGameManager.arg1;
        }

        if (isStringArgumentNeeded()) {
            setArgument(finishGameManager.arg1);
        }
    }


    private FinishGameManager getFinishGameManager() {
        FinishGameManager finishGameManager;YioGdxGame yioGdxGame = menuControllerYio.yioGdxGame;
        GameController gameController = yioGdxGame.gameController;
        finishGameManager = gameController.finishGameManager;
        return finishGameManager;
    }


    public void saveValues() {
        FinishGameManager finishGameManager = getFinishGameManager();

        finishGameManager.setGoalType(goalType);

        if (isColorIconNeeded()) {
            finishGameManager.arg1 = chosenColor;
        }

        if (isStringArgumentNeeded()) {
            try {
                finishGameManager.arg1 = chosenValue;
            } catch (Exception e) {
                finishGameManager.arg1 = 0;
            }
        }
    }


    private void updateTitlePosition() {
        title.position.x = (float) (viewPosition.x + 0.02f * GraphicsYio.width);
        title.position.y = (float) (viewPosition.y + viewPosition.height - 0.015f * GraphicsYio.height);
        title.updateBounds();
    }


    @Override
    protected void onDestroy() {

    }


    @Override
    protected void onAppear() {

    }


    @Override
    protected void onTouchDown() {
        selectionEngineYio.select();
        SoundManagerYio.playSound(SoundManagerYio.soundPressButton);
    }


    @Override
    protected void onTouchDrag() {

    }


    @Override
    protected void onTouchUp() {

    }


    @Override
    protected void onClick() {
        Scenes.sceneChooseGoalType.create();
        Scenes.sceneChooseGoalType.setGoalChoiceListener(this);
    }


    @Override
    public void onGoalTypeChosen(GoalType goalType) {
        setGoalType(goalType);
        checkToShowKeyboardForArgument();
        checkToShowColorPicker();
    }


    private void checkToShowColorPicker() {
        if (!isColorIconNeeded()) return;
        showColorPicker();
    }


    private void showColorPicker() {
        Scenes.sceneColorPicker.create();
        Scenes.sceneColorPicker.setListener(this);
    }


    @Override
    public void onColorChosen(int color) {
        chosenColor = color;
    }


    @Override
    public boolean checkToPerformAction() {
        return false;
    }


    public void setGoalType(GoalType goalType) {
        this.goalType = goalType;
        updateDescriptionString();
    }


    private void checkToShowKeyboardForArgument() {
        if (!isStringArgumentNeeded()) return;
        showKeyboardForArgument();
    }


    public boolean isStringArgumentNeeded() {
        return getFinishGameManager().isStringArgumentNeeded(goalType);
    }


    public boolean isColorIconNeeded() {
        return getFinishGameManager().isColorIconNeeded(goalType);
    }


    private void showKeyboardForArgument() {
        KeyboardManager.getInstance().apply(new AbstractKbReaction() {
            @Override
            public void onInputFromKeyboardReceived(String input) {
                setArgumentByInput(input);
            }
        });
    }


    private void setArgumentByInput(String input) {
        int value = 1;
        if (Yio.isNumeric(input)) {
            value = Integer.valueOf(input);
        }
        setArgument(value);
    }


    public void setArgument(int value) {
        chosenValue = value;
        String prefix = "";
        if (goalType == GoalType.reach_target_income) {
            prefix = "$";
        }
        argument.setString(prefix + value);
        argument.updateMetrics();
    }


    private void updateDescriptionString() {
        String string = LanguagesManager.getInstance().getString("" + goalType);
        if (goalType == GoalType.def) {
            string = LanguagesManager.getInstance().getString("default");
        }
        description.setString(string);
        description.updateMetrics();
    }


    @Override
    public MenuRender getRenderSystem() {
        return MenuRender.renderEditGoalElement;
    }
}
