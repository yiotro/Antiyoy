package yio.tro.antiyoy.menu.scenes.editor;

import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.menu.*;

public class SceneEditorGameRulesPanel extends AbstractEditorPanel{

    private ButtonYio basePanel;
    private double bottom;
    private double pHeight;
    private CheckButtonYio chkFog;
    private CheckButtonYio chkDiplomacy;


    public SceneEditorGameRulesPanel(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        initMetrics();
        chkFog = null;
    }


    private void initMetrics() {
        bottom = SceneEditorOverlay.PANEL_HEIGHT;
        pHeight = 0.25;
    }


    @Override
    public void create() {
        createBasePanel();

        createCheckButtons();

        loadValues();
    }


    private void createCheckButtons() {
        initCheckButtons();
        chkFog.appear();
        chkDiplomacy.appear();
    }


    private void initCheckButtons() {
        if (chkFog != null) return;

        chkFog = CheckButtonYio.getFreshCheckButton(menuControllerYio);
        chkFog.setParent(basePanel);
        chkFog.alignTop(0.03);
        chkFog.setTitle(getString("fog_of_war"));
        chkFog.centerHorizontal(0.05);
        chkFog.setListener(new ICheckButtonListener() {
            @Override
            public void onStateChanged(boolean checked) {
                saveValues();
            }
        });

        chkDiplomacy = CheckButtonYio.getFreshCheckButton(menuControllerYio);
        chkDiplomacy.setParent(basePanel);
        chkDiplomacy.alignUnderPreviousElement();
        chkDiplomacy.setTitle(getString("diplomacy"));
        chkDiplomacy.centerHorizontal(0.05);
        chkDiplomacy.setListener(new ICheckButtonListener() {
            @Override
            public void onStateChanged(boolean checked) {
                saveValues();
            }
        });
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
            basePanel.loadCustomBackground("gray_pixel.png");
            basePanel.setIgnorePauseResume(true);
            menuControllerYio.buttonRenderer.renderButton(basePanel);
        }
        basePanel.setTouchable(false);
        basePanel.setAnimation(Animation.fixed_down);
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
