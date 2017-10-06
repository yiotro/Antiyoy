package yio.tro.antiyoy.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Matrix4;
import yio.tro.antiyoy.gameplay.campaign.CampaignLevelFactory;
import yio.tro.antiyoy.gameplay.campaign.CampaignProgressManager;
import yio.tro.antiyoy.menu.render.MenuRender;
import yio.tro.antiyoy.stuff.*;
import yio.tro.antiyoy.factor_yio.FactorYio;
import yio.tro.antiyoy.gameplay.ClickDetector;
import yio.tro.antiyoy.stuff.tabs_engine.TabsEngineYio;

public class LevelSelector extends InterfaceElement {

    MenuControllerYio menuControllerYio;

    public int selectedPanelIndex, selIndexX, selIndexY, touchDownPanelIndex;
    int levelNumber, rowSize, columnSize, w, h;
    long touchDownTime;
    public float iconRadius, iconDiameter, horOffset, verOffset, offsetBetweenPanels;
    float lastTouchX;
    float touchDownY;

    public RectangleYio pos[], defPos;
    public FactorYio appearFactor, selectionFactor;
    public TextureRegion textures[], backgroundTexture, completedIcon, lockIconTextures[], unlockIconTextures[];
    FrameBuffer frameBuffer;
    TabsEngineYio tabsEngineYio;
    ClickDetector clickDetector;
    PointYio currentTouch;
    SpriteBatch batch;
    private int levelsOnOnePanel;


    public LevelSelector(MenuControllerYio menuControllerYio, int id) {
        super(id);
        this.menuControllerYio = menuControllerYio;
        levelNumber = CampaignProgressManager.getIndexOfLastLevel();
        this.w = menuControllerYio.yioGdxGame.w;
        this.h = menuControllerYio.yioGdxGame.h;
        appearFactor = new FactorYio();
        selectionFactor = new FactorYio();
        batch = new SpriteBatch();
        defPos = new RectangleYio(0.075 * w, 0.05 * h, 0.85 * w, 0.8 * h);
        iconRadius = (float) ((defPos.width / 5 - 0.01f * w) / 2);
        iconDiameter = 2 * iconRadius;
        offsetBetweenPanels = (float) (defPos.width + 0.05f * w);

        tabsEngineYio = new TabsEngineYio();
        tabsEngineYio.setFriction(0);
        tabsEngineYio.setSoftLimitOffset(0.06 * GraphicsYio.width);
        tabsEngineYio.setMagnetMaxPower(0.01 * GraphicsYio.width);

        clickDetector = new ClickDetector();
        currentTouch = new PointYio();

        initTextures();
    }


    void initTextures() {
        rowSize = (int) (((float) defPos.width) / (iconDiameter));
        columnSize = (int) (((float) defPos.height) / (iconDiameter));
        horOffset = ((float) defPos.width - rowSize * iconDiameter) / 2;
        verOffset = ((float) defPos.height - columnSize * iconDiameter) / 2;
        selIndexY = columnSize - 1;

        int levelsOnOnePanel = rowSize * columnSize;
        int howManyPanels = levelNumber / levelsOnOnePanel + 1;
        textures = new TextureRegion[howManyPanels];
        pos = new RectangleYio[howManyPanels];

        backgroundTexture = GraphicsYio.loadTextureRegion("menu/level_selector/level_selector_background.png", false);

        lockIconTextures = new TextureRegion[4];
        lockIconTextures[0] = GraphicsYio.loadTextureRegion("menu/level_selector/easy_base.png", true);
        lockIconTextures[1] = GraphicsYio.loadTextureRegion("menu/level_selector/normal_base.png", true);
        lockIconTextures[2] = GraphicsYio.loadTextureRegion("menu/level_selector/hard_base.png", true);
        lockIconTextures[3] = GraphicsYio.loadTextureRegion("menu/level_selector/expert_base.png", true);

        unlockIconTextures = new TextureRegion[4];
        unlockIconTextures[0] = GraphicsYio.loadTextureRegion("menu/level_selector/unlocked_easy.png", false);
        unlockIconTextures[1] = GraphicsYio.loadTextureRegion("menu/level_selector/unlocked_normal.png", false);
        unlockIconTextures[2] = GraphicsYio.loadTextureRegion("menu/level_selector/unlocked_hard.png", false);
        unlockIconTextures[3] = GraphicsYio.loadTextureRegion("menu/level_selector/unlocked_expert.png", false);

        completedIcon = GraphicsYio.loadTextureRegion("menu/level_selector/completed_level_base.png", false);

        for (int i = 0; i < howManyPanels; i++) {
            pos[i] = new RectangleYio((float) defPos.x + i * ((float) defPos.width + 0.1 * w), (float) defPos.y, (float) defPos.width, (float) defPos.height);
            renderPanel(i);
        }
    }


    public void renderAllPanels() {
        for (int i = 0; i < pos.length; i++) {
            renderPanel(i);
        }
    }


    void renderPanel(int panelIndex) {
        beginRender(panelIndex, Fonts.gameFont);

        Color color = Fonts.gameFont.getColor();
        Fonts.gameFont.setColor(Color.BLACK);

        batch.begin();
        for (int i = 0; i < rowSize; i++) {
            for (int j = 0; j < columnSize; j++) {
                int levelNumber = getLevelNumber(i, j, panelIndex);
                if (levelNumber > CampaignProgressManager.getIndexOfLastLevel()) continue;
                TextureRegion icon;
                if (CampaignProgressManager.getInstance().isLevelLocked(levelNumber)) {
                    int difficulty = CampaignLevelFactory.getDifficultyByIndex(levelNumber);
                    icon = lockIconTextures[difficulty];
                } else if (CampaignProgressManager.getInstance().isLevelComplete(levelNumber)) {
                    icon = completedIcon;
                } else {
                    int difficulty = CampaignLevelFactory.getDifficultyByIndex(levelNumber);
                    icon = unlockIconTextures[difficulty];
                }
                GraphicsYio.drawFromCenter(batch, icon, horOffset + iconRadius + i * iconDiameter, verOffset + iconRadius + j * iconDiameter, iconRadius);
                String levelString = getLevelStringNumber(levelNumber);
                float textWidth = GraphicsYio.getTextWidth(Fonts.gameFont, levelString);
                Fonts.gameFont.draw(batch, levelString, horOffset + iconRadius + i * iconDiameter - textWidth / 2, verOffset + iconRadius + j * iconDiameter + 0.25f * iconRadius);
            }
        }
        batch.end();

        Fonts.gameFont.setColor(color);

        endRender(panelIndex);
    }


    public void updateTextures(int levelIndex) {
        int panelIndex = levelIndex / (rowSize * columnSize);
        renderPanel(panelIndex);

        int secondPanelIndex = (levelIndex + 1) / (rowSize * columnSize);
        if (secondPanelIndex != panelIndex) renderPanel(secondPanelIndex);
    }


    private String getLevelStringNumber(int number) {
        if (number == 0) return "?";
        return "" + number;
    }


    int getLevelNumber(int row_x, int column_y, int panelIndex) {
        return panelIndex * rowSize * columnSize + (columnSize - 1 - column_y) * rowSize + row_x;
    }


    void beginRender(int panelIndex, BitmapFont font) {
        frameBuffer = FrameBufferYio.getInstance(Pixmap.Format.RGB565, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
        frameBuffer.getColorBufferTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        frameBuffer.begin();
        Matrix4 matrix4 = new Matrix4();
        int orthoWidth = Gdx.graphics.getWidth();
        int orthoHeight = (orthoWidth / Gdx.graphics.getWidth()) * Gdx.graphics.getHeight();
        matrix4.setToOrtho2D(0, 0, orthoWidth, orthoHeight);
        batch.setProjectionMatrix(matrix4);
        batch.begin();
        batch.draw(backgroundTexture, 0, 0, orthoWidth, orthoHeight);
        batch.end();
    }


    void endRender(int panelIndex) {
        frameBuffer.end();
        Texture texture = frameBuffer.getColorBufferTexture();
        textures[panelIndex] = new TextureRegion(texture, (int) defPos.width, (int) defPos.height);
        textures[panelIndex].flip(false, true);
    }


    TextureRegion getPanelTexture(int index) {
        return textures[index];
    }


    @Override
    public void move() {
        appearFactor.move();
        tabsEngineYio.move();
        applyTabEngine();
        applyTransitionAnimation();

        if (appearFactor.get() == 1) {
            selectionFactor.move();
        }

        checkToPerformAction();
    }


    private void applyTransitionAnimation() {
        if (appearFactor.get() == 1 || appearFactor.get() == 0) return;

        float delta = (float) (0.3f * defPos.height * (1 - appearFactor.get()));
        for (int i = 0; i < pos.length; i++) {
            pos[i].y -= delta;
            pos[i].width -= delta;
            pos[i].x += delta / 2;
        }
    }


    private void jumpToTab(int tabIndex) {
        int currentTabIndex = tabsEngineYio.getCurrentTabIndex();

        int jumpDelta = tabIndex - currentTabIndex;
        if (jumpDelta == 0) return;

        if (Math.abs(jumpDelta) == 1) {
            tabsEngineYio.swipeTab(jumpDelta);
        }

        if (Math.abs(jumpDelta) == 2) {
            tabsEngineYio.swipeTab(jumpDelta);
        }
    }


    private void applyTabEngine() {
        float delta = (float) (-tabsEngineYio.getSlider().a);

        for (int i = 0; i < pos.length; i++) {
            pos[i].setBy(defPos);
            pos[i].x += GraphicsYio.width * i;
            pos[i].x += delta;
        }
    }


    @Override
    public FactorYio getFactor() {
        return appearFactor;
    }


    public RectangleYio getPosition() {
        return defPos;
    }


    @Override
    public void destroy() {
        appearFactor.setDy(0);
        appearFactor.beginDestroying(InterfaceElement.DES_TYPE, 0.7 * InterfaceElement.DES_SPEED);
    }


    @Override
    public void appear() {
        appearFactor.setValues(0.03, 0);
        appearFactor.beginSpawning(InterfaceElement.SPAWN_TYPE, 0.7 * InterfaceElement.SPAWN_SPEED);

        selectionFactor.setValues(0, 0);
        selectionFactor.beginDestroying(1, 1);
    }


    public void updateTabsMetrics() {
        tabsEngineYio.setLimits(0, GraphicsYio.width * pos.length);
        tabsEngineYio.setSlider(0, GraphicsYio.width);
        tabsEngineYio.setNumberOfTabs(pos.length);
    }


    @Override
    public boolean isVisible() {
        return appearFactor.get() > 0;
    }


    @Override
    public MenuRender getRenderSystem() {
        return MenuRender.renderLevelSelector;
    }


    @Override
    public boolean checkToPerformAction() {
        if (selectionFactor.get() == 1) {
            int levelNumber = getLevelNumber(selIndexX, selIndexY, selectedPanelIndex);
            CampaignLevelFactory campaignLevelFactory = menuControllerYio.yioGdxGame.campaignLevelFactory;
            boolean result = campaignLevelFactory.createCampaignLevel(levelNumber);
            CampaignProgressManager.getInstance();
            if (result) { // level loaded
                selectionFactor.setValues(0.99, 0);
                selectionFactor.beginDestroying(0, 0);
            } else { // tried to load locked level
                selectionFactor.setValues(0.99, 0);
                selectionFactor.beginDestroying(1, 2);
                menuControllerYio.getButtonById(20).setTouchable(true);
            }
            return true;
        }

        return false;
    }


    @Override
    public boolean isTouchable() {
        return true;
    }


    private boolean checkTouchableConditions() {
        if (touchDownY < defPos.y) return false;
        if (touchDownY > defPos.y + defPos.height) return false;
        return true;
    }


    private void updateCurrentTouch(int screenX, int screenY) {
        currentTouch.set(screenX, screenY);
    }


    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (appearFactor.get() < 1) return false;
        updateCurrentTouch(screenX, screenY);
        touchDownY = screenY;
        if (!checkTouchableConditions()) return false;

        clickDetector.touchDown(currentTouch);
        tabsEngineYio.onTouchDown();

        lastTouchX = screenX;
        touchDownPanelIndex = -indexOfPanelThatContainsPoint(screenX, screenY); // minus is here for reason
        touchDownTime = System.currentTimeMillis();
        return true;
    }


    @Override
    public boolean touchDrag(int screenX, int screenY, int pointer) {
        if (appearFactor.get() < 1) return false;
        updateCurrentTouch(screenX, screenY);
        if (!checkTouchableConditions()) return false;

        tabsEngineYio.setSpeed(lastTouchX - screenX);
        clickDetector.touchDrag(currentTouch);

        lastTouchX = screenX;
        return true;
    }


    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (appearFactor.get() < 1) return false;
        updateCurrentTouch(screenX, screenY);
        if (!checkTouchableConditions()) return false;

        clickDetector.touchUp(currentTouch);
        tabsEngineYio.onTouchUp();

        if (clickDetector.isClicked()) {
            onClick();

            return true;
        }

        return true;
    }


    @Override
    public void onMouseWheelScrolled(int amount) {
        if (amount == 1) {
            tabsEngineYio.swipeTab(1);
        } else if (amount == -1) {
            tabsEngineYio.swipeTab(-1);
        }
    }


    void onClick() {
        tabsEngineYio.setSpeed(0);

        int panelIndex = indexOfPanelThatContainsPoint(currentTouch.x, currentTouch.y);
        if (panelIndex == -1) return;

        float internalX = currentTouch.x - ((float) pos[panelIndex].x + horOffset);
        float internalY = currentTouch.y - ((float) pos[panelIndex].y + verOffset);

        int selX = (int) (internalX / (2 * iconRadius));
        int selY = (int) (internalY / (2 * iconRadius));

        if (getLevelNumber(selX, selY, panelIndex) > CampaignProgressManager.getIndexOfLastLevel()) return;

        if (selX >= 0 && selX < rowSize && selY >= 0 && selY < columnSize) {
            selIndexX = selX;
            selIndexY = selY;
            selectedPanelIndex = panelIndex;
            selectionFactor.setValues(0, 0);
            selectionFactor.beginSpawning(3, 3);
        }
    }


    int indexOfPanelThatContainsPoint(float x, float y) {
        for (int i = 0; i < pos.length; i++) {
            if (isPointInsideRectangle(pos[i], x, y)) return i;
        }
        return -1;
    }


    boolean isPointInsideRectangle(RectangleYio rect, float x, float y) {
        return (x > rect.x && x < rect.x + rect.width && y > rect.y && y < rect.y + rect.height);
    }


    public void focusOnRelevantLevel() {
        int indexOfRelevantLevel;
        indexOfRelevantLevel = CampaignProgressManager.getInstance().getIndexOfRelevantLevel();
//        indexOfRelevantLevel = 0;

        int panelIndex = indexOfRelevantLevel / levelsOnOnePanel;
        if (panelIndex == 0) return;

//        jumpToTab(panelIndex);
        tabsEngineYio.teleportToTab(panelIndex);
    }


    @Override
    public void setTouchable(boolean touchable) {

    }


    @Override
    public boolean isButton() {
        return false;
    }


    public float getCircleX() {
        return (float) (defPos.x + defPos.width / 2);
    }


    public float getCircleY() {
        return (float) (defPos.y + appearFactor.get() * defPos.height / 2);
    }


    public float getCircleR() {
        return (float) (appearFactor.get() * appearFactor.get() * defPos.height * 0.6);
    }


    @Override
    public void onPause() {
        for (int i = 0; i < textures.length; i++) {
            if (textures[i] != null) {
                textures[i].getTexture().dispose();
            }
        }
    }


    @Override
    public void onResume() {
        renderAllPanels();
    }


    @Override
    public void setPosition(RectangleYio position) {

    }


    public void resetToBottom() {
        tabsEngineYio.resetToBottom();
    }
}
