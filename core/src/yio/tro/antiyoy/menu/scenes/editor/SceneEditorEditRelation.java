package yio.tro.antiyoy.menu.scenes.editor;

import yio.tro.antiyoy.gameplay.editor.EditorRelation;
import yio.tro.antiyoy.gameplay.editor.EditorRelationsManager;
import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.editor_elements.add_relation.AddEditorRelationElement;
import yio.tro.antiyoy.menu.scenes.Scenes;
import yio.tro.antiyoy.menu.scenes.gameplay.AbstractModalScene;

public class SceneEditorEditRelation extends AbstractModalScene{


    private Reaction rbHide;
    private ButtonYio basePanel;
    private double panelHeight;
    private ButtonYio applyButton;
    private Reaction rbApply;
    AddEditorRelationElement addEditorRelationElement;
    EditorRelation selectedRelation;
    private ButtonYio deleteButton;
    private Reaction rbDelete;
    private ButtonYio changeButton;
    private Reaction rbChange;


    public SceneEditorEditRelation(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
        panelHeight = 0.27;
        addEditorRelationElement = null;
        initReactions();
    }


    @Override
    public void create() {
        createInvisibleCloseButton(rbHide);
        invisibleCloseElement.setPosition(generateRectangle(0, SceneEditorOverlay.PANEL_HEIGHT + panelHeight, 1, 1));
        createBasePanel();
        createApplyButton();
        createAddEditorRelationElement();
    }


    void createDeleteButton() {
        double h = 0.045;
        deleteButton = buttonFactory.getButton(generateRectangle(0.6, SceneEditorOverlay.PANEL_HEIGHT + panelHeight - h, 0.4, h), 294, getString("delete"));
        deleteButton.setAnimation(Animation.fixed_down);
        deleteButton.setShadow(false);
        deleteButton.setReaction(rbDelete);
    }


    private void onDeleteButtonPressed() {
        if (selectedRelation == null) return;

        getEditorRelationsManager().deleteRelation(selectedRelation);
        hide();
        Scenes.sceneEditorDiplomacy.create();
    }


    private void createAddEditorRelationElement() {
        initAddEditorRelationElement();
        addEditorRelationElement.appear();
    }


    private void initAddEditorRelationElement() {
        if (addEditorRelationElement != null) return;
        addEditorRelationElement = new AddEditorRelationElement(menuControllerYio);
        addEditorRelationElement.setPosition(generateRectangle(0.1, 0.19, 0.8, 0.07));
        menuControllerYio.addElementToScene(addEditorRelationElement);
        forceElementToTop(addEditorRelationElement);
    }


    private void createApplyButton() {
        applyButton = buttonFactory.getButton(generateRectangle(0.2, SceneEditorOverlay.PANEL_HEIGHT + 0.03, 0.6, 0.055), 293, getString("create"));
        applyButton.setAnimation(Animation.fixed_down);
        applyButton.setShadow(false);
        applyButton.setReaction(rbApply);
    }


    private void createChangeButton() {
        changeButton = buttonFactory.getButton(generateRectangle(0.2, SceneEditorOverlay.PANEL_HEIGHT + 0.03, 0.6, 0.055), 296, getString("change"));
        changeButton.setAnimation(Animation.fixed_down);
        changeButton.setShadow(false);
        changeButton.setReaction(rbChange);
    }


    private void onChangeButtonClicked() {
        selectedRelation.color1 = addEditorRelationElement.getFirstColor();
        selectedRelation.color2 = addEditorRelationElement.getSecondColor();
        selectedRelation.relation = addEditorRelationElement.getRelation();
        hide();
        Scenes.sceneEditorDiplomacy.create();
    }


    private void createBasePanel() {
        basePanel = buttonFactory.getButton(generateRectangle(0, SceneEditorOverlay.PANEL_HEIGHT, 1, panelHeight), 291, null);
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


    private void initReactions() {
        rbHide = new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                hide();
            }
        };
        rbApply = new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                onApplyButtonPressed();
            }
        };
        rbDelete = new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                onDeleteButtonPressed();
            }
        };
        rbChange = new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                onChangeButtonClicked();
            }
        };
    }


    private void onApplyButtonPressed() {
        int firstColor = addEditorRelationElement.getFirstColor();
        int secondColor = addEditorRelationElement.getSecondColor();
        int relation = addEditorRelationElement.getRelation();
        getEditorRelationsManager().onAddRelationRequested(firstColor, secondColor, relation);
    }


    private EditorRelationsManager getEditorRelationsManager() {
        return getGameController().levelEditorManager.editorRelationsManager;
    }


    @Override
    public void hide() {
        destroyByIndex(290, 299);
        if (addEditorRelationElement != null) {
            addEditorRelationElement.destroy();
        }
    }


    public void setSelectedRelation(EditorRelation selectedRelation) {
        this.selectedRelation = selectedRelation;
        createDeleteButton();
        applyButton.destroy();
        createChangeButton();
        addEditorRelationElement.loadValues(selectedRelation);
    }
}
