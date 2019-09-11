package yio.tro.antiyoy.menu.speed_panel;

import yio.tro.antiyoy.factor_yio.FactorYio;
import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.SpeedManager;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.menu.InterfaceElement;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.render.MenuRender;
import yio.tro.antiyoy.menu.scenes.Scenes;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.PointYio;
import yio.tro.antiyoy.stuff.RectangleYio;

import java.util.ArrayList;

public class SpeedPanel extends InterfaceElement {

    MenuControllerYio menuControllerYio;
    public RectangleYio position, viewPosition;
    FactorYio appearFactor;
    private float height;
    public ArrayList<SpItem> items, centerItems;
    PointYio currentTouch;
    private float itemTouchOffset;


    public SpeedPanel(MenuControllerYio menuControllerYio, int id) {
        super(id);
        this.menuControllerYio = menuControllerYio;

        position = new RectangleYio();
        viewPosition = new RectangleYio();
        appearFactor = new FactorYio();
        currentTouch = new PointYio();
        centerItems = new ArrayList<>();

        initMetrics();
        initPosition();
        initItems();
    }


    private void initItems() {
        itemTouchOffset = 0.05f * GraphicsYio.width;
        items = new ArrayList<>();

        SpItem item;

        item = new SpItem(this);
        item.setAction(SpItem.ACTION_STOP);
        item.setDelta(0, height / 2);
        items.add(item);
        centerItems.add(item);

        item = new SpItem(this);
        item.setAction(SpItem.ACTION_PLAY_PAUSE);
        item.setDelta(0, height / 2);
        items.add(item);
        centerItems.add(item);

        item = new SpItem(this);
        item.setAction(SpItem.ACTION_FAST_FORWARD);
        item.setDelta(0, height / 2);
        items.add(item);
        centerItems.add(item);

        item = new SpItem(this);
        item.setAction(SpItem.ACTION_SAVE);
        item.setDelta(position.width - height / 2, height / 2);
        items.add(item);

        for (SpItem spItem : items) {
            spItem.setRadius(height / 2);
            spItem.setTouchOffset(itemTouchOffset);
        }
    }


    private void initPosition() {
        position.x = 0;
        position.y = 0;
        position.width = GraphicsYio.width;
        position.height = height;
    }


    private void initMetrics() {
        height = 0.05f * GraphicsYio.height;
    }


    @Override
    public void move() {
        appearFactor.move();

        updateViewPosition();

        for (SpItem item : items) {
            item.move();
        }
    }


    private void updateViewPosition() {
        viewPosition.setBy(position);
        viewPosition.y -= (1 - appearFactor.get()) * position.height;
    }


    @Override
    public FactorYio getFactor() {
        return appearFactor;
    }


    @Override
    public void destroy() {
        appearFactor.destroy(2, 2);
    }


    @Override
    public void appear() {
        appearFactor.setValues(0.01, 0);
        appearFactor.appear(3, 0.8);

        onAppear();
    }


    private void onAppear() {
        hideSomeItems();
        applyCenterItems();
    }


    private void applyCenterItems() {
        int n = 0;
        for (SpItem centerItem : centerItems) {
            if (centerItem.isVisible()) {
                n++;
            }
        }

        float stepDelta = 4 * itemTouchOffset;
        float fullWidth = stepDelta * (n - 1);
        float cx = (float) (position.width / 2 - fullWidth / 2);
        for (SpItem centerItem : centerItems) {
            if (!centerItem.isVisible()) continue;
            centerItem.delta.x = cx;
            cx += stepDelta;
        }
    }


    private void hideSomeItems() {
        for (SpItem item : items) {
            item.appearFactor.setValues(1, 0);
            item.appearFactor.appear(1, 1);

            if (item.action == SpItem.ACTION_SAVE) {
                item.appearFactor.setValues(0, 0);
                item.appearFactor.destroy(1, 1);
            }

            if (item.action == SpItem.ACTION_STOP && !GameRules.replayMode) {
                item.appearFactor.setValues(0, 0);
                item.appearFactor.destroy(1, 1);
            }
        }
    }


    private void hideSaveButton() {
        for (SpItem item : items) {
            if (item.action == SpItem.ACTION_SAVE) {
                item.destroy();
                break;
            }
        }
    }


    public void showSaveIcon() {
        for (SpItem item : items) {
            if (item.action == SpItem.ACTION_SAVE) {
                item.defaultAppearFactorState();
                break;
            }
        }
    }


    @Override
    public boolean isVisible() {
        return appearFactor.get() > 0;
    }


    @Override
    public boolean checkToPerformAction() {
        return false;
    }


    @Override
    public boolean isTouchable() {
        return true;
    }


    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        currentTouch.set(screenX, screenY);

        return checkToClickItems();
    }


    private boolean checkToClickItems() {
        for (SpItem item : items) {
            if (item.appearFactor.get() < 1) continue;

            if (item.isTouched(currentTouch)) {
                item.select();
                onItemClicked(item);
                return true;
            }
        }

        return false;
    }


    private void onItemClicked(SpItem item) {
        GameController gameController = menuControllerYio.yioGdxGame.gameController;
        SpeedManager speedManager = gameController.speedManager;

        switch (item.action) {
            case SpItem.ACTION_STOP:
                speedManager.stop();
                break;
            case SpItem.ACTION_PLAY_PAUSE:
                onPlayPauseButtonPressed(speedManager);
                break;
            case SpItem.ACTION_FAST_FORWARD:
                speedManager.onFastForwardButtonPressed();
                break;
            case SpItem.ACTION_SAVE:
                saveCurrentReplay();
                break;
        }
    }


    public void onPlayPauseButtonPressed(SpeedManager speedManager) {
        speedManager.onPlayPauseButtonPressed();
    }


    private void saveCurrentReplay() {
        hideSaveButton();

        GameController gameController = menuControllerYio.yioGdxGame.gameController;
        gameController.replayManager.saveCurrentReplay();

        Scenes.sceneNotification.show("replay_saved");
    }


    @Override
    public boolean touchDrag(int screenX, int screenY, int pointer) {
        return false;
    }


    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }


    @Override
    public void setTouchable(boolean touchable) {

    }


    @Override
    public void setPosition(RectangleYio position) {
        this.position.setBy(position);
    }


    @Override
    public MenuRender getRenderSystem() {
        return MenuRender.renderSpeedPanel;
    }
}
