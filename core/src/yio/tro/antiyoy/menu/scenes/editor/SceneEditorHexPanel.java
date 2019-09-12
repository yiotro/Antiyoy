package yio.tro.antiyoy.menu.scenes.editor;

import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.editor_elements.edit_land.EditLandElement;

public class SceneEditorHexPanel extends AbstractEditorPanel{


    private ButtonYio basePanel;
    EditLandElement editLandElement;


    public SceneEditorHexPanel(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
        editLandElement = null;
    }


    @Override
    public void create() {
        createBasePanel();
        createFilterButton();
        createEditLandElement();
    }


    private void createEditLandElement() {
        initEditLandElement();
        editLandElement.appear();
    }


    private void initEditLandElement() {
        if (editLandElement != null) return;
        editLandElement = new EditLandElement(menuControllerYio);
        editLandElement.setPosition(generateRectangle(0.025, SceneEditorOverlay.PANEL_HEIGHT + 0.075, 0.95, 0.15));
        menuControllerYio.addElementToScene(editLandElement);
    }


    private void createFilterButton() {
        ButtonYio filterButton = buttonFactory.getButton(generateRectangle(0, SceneEditorOverlay.PANEL_HEIGHT, 0.5, 0.05), 12353, null);
        filterButton.setReaction(Reaction.rbSwitchFilterOnlyLand);
        filterButton.setAnimation(Animation.down);
        menuControllerYio.getYioGdxGame().gameController.getLevelEditor().updateFilterOnlyLandButton();
    }


    private void createBasePanel() {
        basePanel = buttonFactory.getButton(generateRectangle(0, SceneEditorOverlay.PANEL_HEIGHT, 1, 0.25), 12352, null);
        menuControllerYio.loadButtonOnce(basePanel, "gray_pixel.png");
        basePanel.setAnimation(Animation.down);
        basePanel.setTouchable(false);
    }


    @Override
    public void hide() {
        destroyByIndex(12350, 12359);
        if (editLandElement != null) {
            editLandElement.destroy();
        }
    }


    @Override
    public boolean isCurrentlyOpened() {
        return basePanel != null && basePanel.appearFactor.get() == 1;
    }
}