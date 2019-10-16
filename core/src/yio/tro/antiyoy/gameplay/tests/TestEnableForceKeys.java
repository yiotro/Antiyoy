package yio.tro.antiyoy.gameplay.tests;

import yio.tro.antiyoy.gameplay.DebugFlags;
import yio.tro.antiyoy.menu.scenes.Scenes;
import yio.tro.antiyoy.stuff.Fonts;

public class TestEnableForceKeys extends AbstractTest{

    @Override
    public String getName() {
        return "Force string keys";
    }


    @Override
    protected void execute() {
        DebugFlags.forceKeys = true;
        Fonts.initFonts(); // calls loadLanguage()
        Scenes.createScenes(gameController.yioGdxGame.menuControllerYio);
        Scenes.sceneMainMenu.create();
    }
}
