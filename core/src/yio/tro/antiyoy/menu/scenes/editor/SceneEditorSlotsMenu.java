package yio.tro.antiyoy.menu.scenes.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import yio.tro.antiyoy.menu.behaviors.ReactBehavior;
import yio.tro.antiyoy.gameplay.editor.LevelEditor;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.editor.EditorReactions;
import yio.tro.antiyoy.menu.scenes.AbstractScene;

public class SceneEditorSlotsMenu extends AbstractScene{


    public SceneEditorSlotsMenu(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
    }


    String getEditorSlotString(int index) {
        Preferences prefs = Gdx.app.getPreferences(LevelEditor.EDITOR_PREFS);
        String slotString = prefs.getString("slot" + (index + 1));
        if (slotString.length() < 10) {
            return getString("slot") + " " + (index + 1) + " - " + getString("empty");
        } else {
            return getString("slot") + " " + (index + 1);
        }
    }


    @Override
    public void create() {
        menuControllerYio.beginMenuCreation();

        menuControllerYio.getYioGdxGame().beginBackgroundChange(2, true, true);

        ButtonYio basePanel = buttonFactory.getButton(generateRectangle(0.05, 0.05, 0.9, 0.8), 139, null);
        basePanel.setTouchable(false);
        basePanel.onlyShadow = true;
        basePanel.setAnimType(ButtonYio.ANIM_FROM_CENTER);
        basePanel.appearFactor.beginSpawning(MenuControllerYio.SPAWN_ANIM, MenuControllerYio.SPAWN_SPEED);

        for (int i = 0; i < 8; i++) {
            ButtonYio slotButton = buttonFactory.getButton(generateRectangle(0.05, 0.75 - 0.1 * (double) i, 0.9, 0.1), 131 + i, null);
            slotButton.cleatText();
            slotButton.addTextLine(getEditorSlotString(i));
            slotButton.addTextLine(" ");
            menuControllerYio.getButtonRenderer().renderButton(slotButton);

            slotButton.setAnimType(ButtonYio.ANIM_FROM_CENTER);
            slotButton.setShadow(false);
            slotButton.setReactBehavior(EditorReactions.rbEditorActionsMenu);
            slotButton.appearFactor.beginSpawning(MenuControllerYio.SPAWN_ANIM, MenuControllerYio.SPAWN_SPEED);
        }

        menuControllerYio.spawnBackButton(130, ReactBehavior.rbChooseGameModeMenu);
    }
}