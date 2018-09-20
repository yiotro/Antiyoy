package yio.tro.antiyoy.menu.scenes.editor;

import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.CheckButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.stuff.GraphicsYio;

public class SceneEditorChecks extends AbstractEditorPanel{

    private final Reaction rbSaveValues;
    private ButtonYio basePanel;
    private double bottom;
    private double pHeight;
    private double yOffset;
    private double bSize;
    private CheckButtonYio chkFog;
    private CheckButtonYio chkDiplomacy;


    public SceneEditorChecks(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        initMetrics();

        rbSaveValues = new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                saveValues();
            }
        };
    }


    private void initMetrics() {
        bottom = 0.07;
        pHeight = 0.25;
        yOffset = 0.02;
        bSize = 0.06;
    }


    @Override
    public void create() {
        createBasePanel();

        createCheckButtons();

        loadValues();
    }


    private void createCheckButtons() {
        double checkButtonSize = 0.045;
        double hSize = GraphicsYio.convertToHeight(checkButtonSize);
        double chkX = 0.88 - checkButtonSize;
        double chkY = pHeight - 0.008;
        double delta = hSize + 0.07;

        chkFog = CheckButtonYio.getCheckButton(menuControllerYio, generateSquare(chkX, chkY - hSize / 2, hSize), 921);
        chkFog.setTouchPosition(generateRectangle(0.05, chkY - hSize * 1.5, 0.9, hSize * 3));
        chkFog.setAnimation(Animation.FIXED_DOWN);
        chkFog.setReaction(rbSaveValues);
        chkY -= delta;

        chkDiplomacy = CheckButtonYio.getCheckButton(menuControllerYio, generateSquare(chkX, chkY - hSize / 2, hSize), 922);
        chkDiplomacy.setTouchPosition(generateRectangle(0.05, chkY - hSize * 1.5, 0.9, hSize * 3));
        chkDiplomacy.setAnimation(Animation.FIXED_DOWN);
        chkDiplomacy.setReaction(rbSaveValues);
        chkY -= delta;
    }


    private void saveValues() {
        GameRules.editorFog = chkFog.isChecked();
        GameRules.editorDiplomacy = chkDiplomacy.isChecked();
    }


    private void loadValues() {
        chkFog.setChecked(GameRules.editorFog);
        chkDiplomacy.setChecked(GameRules.editorDiplomacy);
    }


    private void createBasePanel() {
        basePanel = buttonFactory.getButton(generateRectangle(0, bottom, 1, pHeight), 920, null);
        if (basePanel.notRendered()) {
            basePanel.cleatText();
            basePanel.addEmptyLines(1);
            basePanel.addTextLine(getString("fog_of_war"));
            basePanel.addEmptyLines(1);
            basePanel.addTextLine(getString("diplomacy"));
            basePanel.addEmptyLines(1);
            basePanel.setTextOffset(0.07f * GraphicsYio.width);
            basePanel.loadCustomBackground("gray_pixel.png");
            basePanel.setIgnorePauseResume(true);
            menuControllerYio.buttonRenderer.renderButton(basePanel);
        }
        basePanel.setTouchable(false);
        basePanel.setAnimation(Animation.FIXED_DOWN);
        basePanel.disableTouchAnimation();
        basePanel.enableRectangularMask();
        basePanel.setShadow(true);
    }


    @Override
    public void hide() {
        destroyByIndex(920, 929);

        chkFog.destroy();
        chkDiplomacy.destroy();
    }


    @Override
    public boolean isCurrentlyOpened() {
        return basePanel.appearFactor.get() == 1;
    }
}
