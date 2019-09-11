package yio.tro.antiyoy.menu.scenes;

import yio.tro.antiyoy.gameplay.GlobalStatistics;
import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.stuff.RectangleYio;
import yio.tro.antiyoy.stuff.Yio;

public class SceneGlobalStatistics extends AbstractScene{


    private Reaction rbBack;
    private ButtonYio label;


    public SceneGlobalStatistics(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        initReactions();
    }


    private void initReactions() {
        rbBack = new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                Scenes.sceneMoreSettings.create();
            }
        };
    }


    @Override
    public void create() {
        menuControllerYio.beginMenuCreation();
        menuControllerYio.getYioGdxGame().beginBackgroundChange(1, false, true);

        menuControllerYio.spawnBackButton(1200, rbBack);

        createLabel();

        menuControllerYio.endMenuCreation();
    }


    private void createLabel() {
        double bw = 0.9;
        double bh = 0.8;
        RectangleYio pos = generateRectangle(0.5 - bw / 2, 0.45 - bh / 2, bw, bh);
        label = buttonFactory.getButton(pos, 1201, null);
        updateLabelText();
        menuControllerYio.buttonRenderer.renderButton(label);
        label.setAnimation(Animation.from_center);
        label.setTouchable(false);
    }


    private void updateLabelText() {
        GlobalStatistics instance = GlobalStatistics.getInstance();

        label.cleatText();
        label.addTextLine(getString("statistics"));
        label.addTextLine(getString("time") + ": " + Yio.convertTime(instance.timeInGame));
        label.addTextLine(getString("turns_made") + ": " + instance.turnsMade);
        label.addTextLine(getString("money_spent") + ": " + instance.moneySpent);
        label.addTextLine(getString("wins") + ": " + instance.wins);
        label.applyNumberOfLines(16);
    }
}
