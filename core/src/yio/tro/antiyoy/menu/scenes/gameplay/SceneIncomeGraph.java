package yio.tro.antiyoy.menu.scenes.gameplay;

import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.income_graph.IncomeGraphElement;

public class SceneIncomeGraph extends AbstractModalScene {


    private Reaction rbHide;
    IncomeGraphElement incomeGraphElement;


    public SceneIncomeGraph(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        incomeGraphElement = null;
        initReactions();
    }


    private void initReactions() {
        rbHide = new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                hide();
            }
        };
    }


    @Override
    public void create() {
        menuControllerYio.getYioGdxGame().gameController.selectionManager.deselectAll();

        createCloseButton();
        createIncomeGraph();
    }


    private void createIncomeGraph() {
        initIncomeGraph();
        incomeGraphElement.appear();
    }


    private void initIncomeGraph() {
        if (incomeGraphElement != null) return;
        incomeGraphElement = new IncomeGraphElement(menuControllerYio);
        incomeGraphElement.setPosition(generateRectangle(0, 0.18, 1, 0.41));
        menuControllerYio.addElementToScene(incomeGraphElement);
    }


    private void createCloseButton() {
        ButtonYio closeButton = buttonFactory.getButton(generateRectangle(0, 0, 1, 1), 430, null);
        if (closeButton.notRendered()) {
            closeButton.loadTexture("pixels/transparent_black_pixel.png");
        }
        closeButton.setReaction(rbHide);
        closeButton.setSelectionRenderable(false);
        closeButton.setAnimation(Animation.none);
    }


    @Override
    public void hide() {
        destroyByIndex(430, 439);
        if (incomeGraphElement != null) {
            incomeGraphElement.destroy();
        }
    }
}