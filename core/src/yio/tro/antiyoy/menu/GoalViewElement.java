package yio.tro.antiyoy.menu;

import yio.tro.antiyoy.YioGdxGame;
import yio.tro.antiyoy.gameplay.FinishGameManager;
import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.GoalType;
import yio.tro.antiyoy.menu.render.MenuRender;
import yio.tro.antiyoy.menu.scenes.Scenes;
import yio.tro.antiyoy.stuff.*;

public class GoalViewElement extends AbstractRectangularUiElement{

    public RenderableTextYio title;
    public RenderableTextYio description;
    public RenderableTextYio argument;
    public int chosenFraction;
    public CircleYio colorIconPosition;
    private GoalType goalType;


    public GoalViewElement(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
        initTitle();
        description = new RenderableTextYio();
        description.setFont(Fonts.smallerMenuFont);
        argument = new RenderableTextYio();
        argument.setFont(Fonts.smallerMenuFont);
        chosenFraction = -1;
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


    private void updateDescriptionPosition() {
        description.position.x = title.position.x;
        description.position.y = title.position.y - title.height - 0.015f * GraphicsYio.height;
        description.updateBounds();
    }


    public void loadValues() {
        FinishGameManager finishGameManager = getFinishGameManager();
        setGoalType(finishGameManager.goalType);
    }


    private FinishGameManager getFinishGameManager() {
        FinishGameManager finishGameManager;YioGdxGame yioGdxGame = menuControllerYio.yioGdxGame;
        GameController gameController = yioGdxGame.gameController;
        finishGameManager = gameController.finishGameManager;
        return finishGameManager;
    }


    private void updateTitlePosition() {
        title.position.x = (float) (viewPosition.x + 0.02f * GraphicsYio.width);
        title.position.y = (float) (viewPosition.y + viewPosition.height - 0.015f * GraphicsYio.height);
        title.updateBounds();
    }


    public void setGoalType(GoalType goalType) {
        FinishGameManager finishGameManager = getFinishGameManager();

        this.goalType = goalType;

        if (isColorIconNeeded()) {
            chosenFraction = finishGameManager.arg1;
        }

        if (isStringArgumentNeeded()) {
            setArgument(finishGameManager.arg1);
        }

        updateDescriptionString();
    }


    public void setArgument(int value) {
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


    public boolean isStringArgumentNeeded() {
        return getFinishGameManager().isStringArgumentNeeded(goalType);
    }


    public boolean isColorIconNeeded() {
        return getFinishGameManager().isColorIconNeeded(goalType);
    }


    @Override
    protected void onDestroy() {

    }


    @Override
    protected void onAppear() {
        loadValues();
    }


    @Override
    protected void onTouchDown() {

    }


    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Scenes.sceneGoalView.hide();
        return false;
    }


    @Override
    protected void onTouchDrag() {

    }


    @Override
    protected void onTouchUp() {

    }


    @Override
    protected void onClick() {

    }


    @Override
    public boolean checkToPerformAction() {
        return false;
    }


    @Override
    public MenuRender getRenderSystem() {
        return MenuRender.renderGoalView;
    }
}
