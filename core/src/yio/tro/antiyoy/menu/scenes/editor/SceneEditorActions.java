package yio.tro.antiyoy.menu.scenes.editor;

import yio.tro.antiyoy.gameplay.editor.LevelEditor;
import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.behaviors.editor.EditorReactions;
import yio.tro.antiyoy.menu.scenes.AbstractScene;

public class SceneEditorActions extends AbstractScene{


    double bHeight, y;
    private ButtonYio basePanel;
    int lastSlotNumber;


    public SceneEditorActions(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        bHeight = 0.075;
        lastSlotNumber = -1;
    }


    @Override
    public void create() {
        menuControllerYio.beginMenuCreation();

        menuControllerYio.getYioGdxGame().setGamePaused(true);

        menuControllerYio.getYioGdxGame().beginBackgroundChange(3, true, true);

        menuControllerYio.spawnBackButton(189, EditorReactions.rbEditorSlotMenu);

        y = 0.24;

        basePanel = buttonFactory.getButton(generateRectangle(0.1, y, 0.8, 0.4), 181, null);
        updateBaseText();
        basePanel.setTouchable(false);
        basePanel.setIgnorePauseResume(true);

        addInternalButton(182, "play", EditorReactions.rbEditorPlay);
        addInternalButton(183, "export", EditorReactions.rbEditorExport);
        addInternalButton(184, "import", EditorReactions.rbEditorImportConfirmMenu);
        addInternalButton(185, "edit", EditorReactions.rbStartEditorMode);

        for (int i = 181; i <= 185; i++) {
            ButtonYio buttonYio = menuControllerYio.getButtonById(i);
            buttonYio.setAnimation(Animation.FROM_CENTER);
            buttonYio.disableTouchAnimation();
        }

        menuControllerYio.endMenuCreation();
    }


    private void updateBaseText() {
        basePanel.cleatText();

        LevelEditor levelEditor = menuControllerYio.yioGdxGame.gameController.getLevelEditor();
        int currentSlotNumber = levelEditor.getCurrentSlotNumber();
        if (lastSlotNumber == currentSlotNumber) return; // no need to update

        basePanel.addTextLine(getSlotString(currentSlotNumber));

        basePanel.addEmptyLines(7);

        menuControllerYio.buttonRenderer.renderButton(basePanel);

        lastSlotNumber = currentSlotNumber;
    }


    private String getSlotString(int currentSlotNumber) {
        if (currentSlotNumber == LevelEditor.TEMPORARY_SLOT_NUMBER) {
            return getString("temp_slot");
        }

        return getString("slot") + " " + currentSlotNumber;
    }


    private void addInternalButton(int id, String key, Reaction reaction) {
        ButtonYio button = buttonFactory.getButton(generateRectangle(0.1, y, 0.8, bHeight), id, getString(key));
        button.setReaction(reaction);
        button.setShadow(false);
        button.setVisualHook(basePanel);

        y += bHeight;
    }
}