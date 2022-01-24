package yio.tro.antiyoy.menu.scenes.editor;

import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.menu.*;

public class SceneEditorGameRulesPanel extends AbstractEditorPanel{

    private ButtonYio basePanel;
    private double bottom;
    private double pHeight;
    private CheckButtonYio chkFog;
    private CheckButtonYio chkDiplomacy;
    private CheckButtonYio chkLockRelations;
    EditGoalElement editGoalElement;


    public SceneEditorGameRulesPanel(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        initMetrics();
        chkFog = null;
        editGoalElement = null;
    }


    private void initMetrics() {
        bottom = SceneEditorOverlay.PANEL_HEIGHT;
        pHeight = 0.4;
    }


    @Override
    public void create() {
        createBasePanel();

        createCheckButtons();
        createEditGoalElement();

        loadValues();
    }


    private void createEditGoalElement() {
        initEditGoalElement();
        editGoalElement.appear();
    }


    private void initEditGoalElement() {
        if (editGoalElement != null) return;

        editGoalElement = new EditGoalElement(menuControllerYio);
        editGoalElement.setPosition(generateRectangle(0.08, 0.1, 0.84, 0.1));
        editGoalElement.setAnimation(Animation.down);
        menuControllerYio.addElementToScene(editGoalElement);
    }


    private void createCheckButtons() {
        initCheckButtons();
        chkFog.appear();
        chkDiplomacy.appear();
        chkLockRelations.appear();
    }


    private void initCheckButtons() {
        if (chkFog != null) return;

        chkFog = CheckButtonYio.getFreshCheckButton(menuControllerYio);
        chkFog.setParent(basePanel);
        chkFog.alignTop(0.02);
        chkFog.setTitle(getString("fog_of_war"));
        chkFog.centerHorizontal(0.05);

        chkDiplomacy = CheckButtonYio.getFreshCheckButton(menuControllerYio);
        chkDiplomacy.setParent(basePanel);
        chkDiplomacy.alignUnderPreviousElement();
        chkDiplomacy.setTitle(getString("diplomacy"));
        chkDiplomacy.centerHorizontal(0.05);

        chkLockRelations = CheckButtonYio.getFreshCheckButton(menuControllerYio);
        chkLockRelations.setParent(basePanel);
        chkLockRelations.alignUnderPreviousElement();
        chkLockRelations.setTitle(getString("lock_relations"));
        chkLockRelations.centerHorizontal(0.05);
    }


    private void saveValues() {
        if (!saveAllowed) return;
        GameRules.editorFog = chkFog.isChecked();
        GameRules.editorDiplomacy = chkDiplomacy.isChecked();
        GameRules.diplomaticRelationsLocked = chkLockRelations.isChecked();
        if (editGoalElement != null) {
            editGoalElement.saveValues();
        }
    }


    private void loadValues() {
        chkFog.setChecked(GameRules.editorFog);
        chkDiplomacy.setChecked(GameRules.editorDiplomacy);
        chkLockRelations.setChecked(GameRules.diplomaticRelationsLocked);
        editGoalElement.loadValues();
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
        if (editGoalElement != null && editGoalElement.getFactor().get() > 0.1) {
            saveValues();
        }

        destroyByIndex(920, 929);

        chkFog.destroy();
        chkDiplomacy.destroy();
        chkLockRelations.destroy();

        if (editGoalElement != null) {
            editGoalElement.destroy();
        }
    }


    @Override
    public boolean isCurrentlyOpened() {
        return basePanel.appearFactor.get() == 1;
    }
}
