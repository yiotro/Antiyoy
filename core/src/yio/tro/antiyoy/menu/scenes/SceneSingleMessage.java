package yio.tro.antiyoy.menu.scenes;

import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;

import java.util.ArrayList;

public class SceneSingleMessage extends AbstractScene{

    public SceneSingleMessage(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
    }


    public void createSingleMessageMenu(String key) {
        menuControllerYio.beginMenuCreation();

        menuControllerYio.getYioGdxGame().beginBackgroundChange(0, false, false);
        ButtonYio textPanel = buttonFactory.getButton(generateRectangle(0.05, 0.25, 0.9, 0.5), 400, null);
        if (textPanel.notRendered()) {
            textPanel.cleatText();
            ArrayList<String> list = menuControllerYio.getArrayListFromString(getString(key));
            textPanel.addManyLines(list);
            int lines = 12;
            int addedEmptyLines = lines - list.size();
            for (int i = 0; i < addedEmptyLines; i++) {
                textPanel.addTextLine(" ");
            }
            menuControllerYio.getButtonRenderer().renderButton(textPanel);
        }
        textPanel.setTouchable(false);
        textPanel.setAnimation(Animation.from_center);

        ButtonYio okButton = buttonFactory.getButton(generateRectangle(0.65, 0.25, 0.3, 0.07), 401, "Ok");
        okButton.setReaction(Reaction.rbMainMenu);
        okButton.setAnimation(Animation.from_center);

        menuControllerYio.endMenuCreation();
    }


    @Override
    public void create() {

    }
}