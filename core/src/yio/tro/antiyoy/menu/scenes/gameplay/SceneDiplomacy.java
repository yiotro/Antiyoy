package yio.tro.antiyoy.menu.scenes.gameplay;

import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.diplomacy_element.DiplomacyElement;

public class SceneDiplomacy extends AbstractModalScene {

    public DiplomacyElement diplomacyElement;


    public SceneDiplomacy(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        diplomacyElement = null;
    }


    @Override
    public void create() {
        if (diplomacyElement == null) {
            initDiplomacyElement();
        }

        diplomacyElement.appear();
    }


    private void initDiplomacyElement() {
        diplomacyElement = new DiplomacyElement(menuControllerYio, -1);

        diplomacyElement.setPosition(generateRectangle(0, 0, 1, 0.65));

        menuControllerYio.addElementToScene(diplomacyElement);
    }


    @Override
    public void hide() {
        diplomacyElement.destroy();
    }
}
