package yio.tro.antiyoy.menu.scenes.gameplay.choose_entity;

import yio.tro.antiyoy.gameplay.FieldManager;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomacyManager;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomaticEntity;
import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.customizable_list.AbstractCustomListItem;
import yio.tro.antiyoy.menu.customizable_list.CustomizableListYio;
import yio.tro.antiyoy.menu.customizable_list.SimpleDipEntityItem;
import yio.tro.antiyoy.menu.scenes.gameplay.AbstractModalScene;

public class SceneChooseDiplomaticEntity extends AbstractModalScene {

    CustomizableListYio customizableListYio;
    public IDipEntityReceiver iDipEntityReceiver;
    private Reaction rbBack;


    public SceneChooseDiplomaticEntity(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
        customizableListYio = null;
        iDipEntityReceiver = null;
        initReactions();
    }


    @Override
    public void create() {
        createInvisibleCloseButton(rbBack);
        createList();
    }


    private void createList() {
        initList();
        customizableListYio.appear();
    }


    public void loadValues() {
        customizableListYio.clearItems();

        FieldManager fieldManager = getGameController().fieldManager;
        DiplomacyManager diplomacyManager = fieldManager.diplomacyManager;
        for (DiplomaticEntity entity : diplomacyManager.entities) {
            if (!entity.alive) continue;
            SimpleDipEntityItem simpleDipEntityItem = new SimpleDipEntityItem();
            customizableListYio.addItem(simpleDipEntityItem);
            simpleDipEntityItem.setDiplomaticEntity(entity);
        }
        customizableListYio.updateItemDeltas();
    }


    public void excludeEntity(DiplomaticEntity diplomaticEntity) {
        for (int i = customizableListYio.items.size() - 1; i >= 0; i--) {
            AbstractCustomListItem abstractCustomListItem = customizableListYio.items.get(i);
            SimpleDipEntityItem simpleDipEntityItem = (SimpleDipEntityItem) abstractCustomListItem;
            if (simpleDipEntityItem.diplomaticEntity != diplomaticEntity) continue;
            customizableListYio.items.remove(abstractCustomListItem);
            customizableListYio.updateItemDeltas();
            break;
        }
    }


    private void initList() {
        if (customizableListYio != null) return;

        customizableListYio = new CustomizableListYio(menuControllerYio);
        customizableListYio.setPosition(generateRectangle(0.2, 0.1, 0.6, 0.5));
        customizableListYio.setAnimation(Animation.down);
        menuControllerYio.addElementToScene(customizableListYio);
    }


    private void initReactions() {
        rbBack = new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                hide();
            }
        };
    }


    public void setiDipEntityReceiver(IDipEntityReceiver iDipEntityReceiver) {
        this.iDipEntityReceiver = iDipEntityReceiver;
    }


    public void onDiplomaticEntityChosen(DiplomaticEntity diplomaticEntity) {
        if (iDipEntityReceiver != null) {
            iDipEntityReceiver.onDiplomaticEntityChosen(diplomaticEntity);
        }
        hide();
    }


    @Override
    public void hide() {
        if (invisibleCloseElement != null) {
            invisibleCloseElement.destroy();
        }
        if (customizableListYio != null) {
            customizableListYio.destroy();
        }
    }
}
