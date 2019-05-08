package yio.tro.antiyoy.gameplay.tests;

import yio.tro.antiyoy.menu.scenes.Scenes;

public class TestLaunchFireworks extends AbstractTest{

    @Override
    public String getName() {
        return "Fireworks";
    }


    @Override
    protected void execute() {
        Scenes.sceneFireworks.create();
    }
}
