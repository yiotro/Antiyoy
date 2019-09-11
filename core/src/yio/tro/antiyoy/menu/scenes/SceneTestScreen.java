package yio.tro.antiyoy.menu.scenes;

import yio.tro.antiyoy.menu.*;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.customizable_list.CustomizableListYio;
import yio.tro.antiyoy.menu.customizable_list.SampleListItem;
import yio.tro.antiyoy.menu.customizable_list.TitleListItem;

public class SceneTestScreen extends AbstractScene{


    private double labelHeight;
    private double labelTopY;
    private ButtonYio mainLabel;
    private CheckButtonYio chkOne;
    private CheckButtonYio chkTwo;
    private CustomizableListYio customizableListYio;


    public SceneTestScreen(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
        chkOne = null;
        customizableListYio = null;
    }


    @Override
    public void create() {
        menuControllerYio.beginMenuCreation();
        menuControllerYio.getYioGdxGame().beginBackgroundChange(2, false, true);
        menuControllerYio.spawnBackButton(38721132, Reaction.rbChooseGameModeMenu);

        createCustomizableList();

        menuControllerYio.endMenuCreation();
    }


    private void createCustomizableList() {
        initCustomList();
        customizableListYio.appear();
    }


    private void initCustomList() {
        if (customizableListYio != null) return;
        customizableListYio = new CustomizableListYio(menuControllerYio);
        customizableListYio.setAnimation(Animation.from_center);
        customizableListYio.setPosition(generateRectangle(0.1, 0.2, 0.8, 0.6));

        TitleListItem titleListItem = new TitleListItem();
        titleListItem.setTitle("Title");
        customizableListYio.addItem(titleListItem);

        for (int i = 0; i < 12; i++) {
            SampleListItem sampleListItem = new SampleListItem();
            sampleListItem.setTitle("Item " + (i + 1));
            customizableListYio.addItem(sampleListItem);
        }

        menuControllerYio.addElementToScene(customizableListYio);
    }


    private void createChecks() {
        initChecks();
        chkOne.appear();
        chkTwo.appear();
    }


    private void initChecks() {
        if (chkOne != null) return;

        chkOne = CheckButtonYio.getFreshCheckButton(menuControllerYio);
        chkOne.setParent(mainLabel);
        chkOne.alignTop(0);
        chkOne.setTitle("Check button");

        chkTwo = CheckButtonYio.getFreshCheckButton(menuControllerYio);
        chkTwo.setParent(mainLabel);
        chkTwo.alignUnder(chkOne, 0);
        chkTwo.setTitle("One more chk");
    }


    private void createMainLabel() {
        labelHeight = 0.52;
        labelTopY = 0.7;
        mainLabel = buttonFactory.getButton(generateRectangle(0.04, labelTopY - labelHeight, 0.92, labelHeight), 873526371, null);
        mainLabel.setTextLine(" ");
        mainLabel.setTouchable(false);
        mainLabel.setAnimation(Animation.from_center);
        menuControllerYio.buttonRenderer.renderButton(mainLabel);
    }
}