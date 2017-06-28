package yio.tro.antiyoy.menu.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import yio.tro.antiyoy.LanguagesManager;
import yio.tro.antiyoy.YioGdxGame;
import yio.tro.antiyoy.menu.behaviors.ReactBehavior;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;

public class SceneSaveSlots extends AbstractScene{


    public SceneSaveSlots(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
    }


    public void updateSaveSlotButton(int slotIndex) {
        ButtonYio slotButton = menuControllerYio.getButtonById(212 + slotIndex);
        if (slotButton == null) return;
        Preferences prefs = Gdx.app.getPreferences("save_slot" + slotIndex);
        String dateString = prefs.getString("date");
        String detailsInfo = " ";
        if (dateString.length() > 3) {
            slotButton.setTextLine(dateString);
            String diff = YioGdxGame.getDifficultyNameByPower(LanguagesManager.getInstance(), prefs.getInteger("save_difficulty"));
            if (prefs.getBoolean("save_campaign_mode")) {
                detailsInfo = getString("choose_game_mode_campaign") + "," + prefs.getInteger("save_current_level") + "|" + diff;
            } else {
                detailsInfo = getString("choose_game_mode_skirmish") + "|" + diff;
            }
        } else {
            slotButton.setTextLine(getString("empty"));
        }
        slotButton.addTextLine(detailsInfo);
        menuControllerYio.getButtonRenderer().renderButton(slotButton);
    }


    public void create(boolean load) {
        menuControllerYio.beginMenuCreation();

        menuControllerYio.getYioGdxGame().beginBackgroundChange(1, false, true);

        menuControllerYio.spawnBackButton(210, ReactBehavior.rbPauseMenu);
        if (load) menuControllerYio.getButtonById(210).setReactBehavior(ReactBehavior.rbChooseGameModeMenu);

        ButtonYio basePanel = buttonFactory.getButton(generateRectangle(0.05, 0.2, 0.9, 0.57), 211, null);
        if (basePanel.notRendered()) {
            basePanel.addTextLine(getString("slots") + ":");
            for (int i = 0; i < 10; i++) {
                basePanel.addTextLine(" ");
            }
            menuControllerYio.getButtonRenderer().renderButton(basePanel);
        }
        basePanel.setTouchable(false);
//        basePanel.onlyShadow = true;
        basePanel.setAnimType(ButtonYio.ANIM_FROM_CENTER);
        basePanel.factorModel.beginSpawning(MenuControllerYio.SPAWN_ANIM, MenuControllerYio.SPAWN_SPEED);

        for (int i = 0; i < 5; i++) {
            ButtonYio slotButton = buttonFactory.getButton(generateRectangle(0.05, 0.6 - 0.1 * (double) i, 0.9, 0.1), 212 + i, null);
            updateSaveSlotButton(i);

            slotButton.setAnimType(ButtonYio.ANIM_FROM_CENTER);
            slotButton.setShadow(false);
            slotButton.setReactBehavior(ReactBehavior.rbSaveGameToSlot);
            slotButton.disableTouchAnimation();
            if (load) slotButton.setReactBehavior(ReactBehavior.rbLoadGameFromSlot);
            slotButton.factorModel.beginSpawning(MenuControllerYio.SPAWN_ANIM, MenuControllerYio.SPAWN_SPEED);
        }

        menuControllerYio.endMenuCreation();
    }


    @Override
    public void create() {

    }
}