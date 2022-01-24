package yio.tro.antiyoy.menu.scenes.gameplay;

import yio.tro.antiyoy.gameplay.ColorsManager;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomacyManager;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomaticEntity;
import yio.tro.antiyoy.gameplay.editor.EditorRelation;
import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.customizable_list.CustomizableListYio;
import yio.tro.antiyoy.menu.customizable_list.RelationListItem;
import yio.tro.antiyoy.menu.customizable_list.TitleListItem;
import yio.tro.antiyoy.menu.scenes.AbstractScene;
import yio.tro.antiyoy.menu.scenes.Scenes;

public class SceneDiplomaticRelations extends AbstractModalScene {

    private Reaction rbHide;
    private ButtonYio basePanel;
    private double panelHeight;
    private CustomizableListYio customizableListYio;
    int chosenFraction;
    AbstractScene parentScene;


    public SceneDiplomaticRelations(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
        panelHeight = 0.5;
        initReactions();
    }


    private void initReactions() {
        rbHide = new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                hide();
                if (parentScene != null) {
                    parentScene.create();
                }
            }
        };
    }


    @Override
    public void create() {
        createInvisibleCloseButton(rbHide);
        createBasePanel();
        createList();
        parentScene = Scenes.sceneDiplomacy;
    }


    private void loadValues() {
        customizableListYio.clearItems();

        TitleListItem titleListItem = new TitleListItem();
        titleListItem.setTitle(getString("diplomacy"));
        customizableListYio.addItem(titleListItem);

        DiplomacyManager diplomacyManager = getGameController().fieldManager.diplomacyManager;
        DiplomaticEntity chosenEntity = diplomacyManager.getEntity(chosenFraction);
        ColorsManager colorsManager = getGameController().colorsManager;
        for (DiplomaticEntity entity : diplomacyManager.entities) {
            if (chosenEntity == entity) continue;
            if (!entity.alive) continue;
            EditorRelation editorRelation = new EditorRelation();
            editorRelation.relation = chosenEntity.getRelation(entity);
            editorRelation.color1 = colorsManager.getColorByFraction(chosenEntity.fraction);
            editorRelation.color2 = colorsManager.getColorByFraction(entity.fraction);
            RelationListItem relationListItem = new RelationListItem();
            relationListItem.set(
                    editorRelation,
                    chosenEntity.capitalName,
                    entity.capitalName
            );
            customizableListYio.addItem(relationListItem);
        }
    }


    private void createList() {
        initCustomList();
        customizableListYio.appear();
    }


    private void initCustomList() {
        if (customizableListYio != null) return;
        customizableListYio = new CustomizableListYio(menuControllerYio);
        customizableListYio.setAnimation(Animation.down);
        customizableListYio.setEmbeddedMode(true);
        customizableListYio.setPosition(generateRectangle(0.02, 0, 0.96, panelHeight - 0.02));

        menuControllerYio.addElementToScene(customizableListYio);
    }


    private void createBasePanel() {
        basePanel = buttonFactory.getButton(generateRectangle(0, 0, 1, panelHeight), 371, null);
        if (basePanel.notRendered()) {
            basePanel.cleatText();
            basePanel.addEmptyLines(1);
            basePanel.loadCustomBackground("diplomacy/background.png");
            basePanel.setIgnorePauseResume(true);
            menuControllerYio.buttonRenderer.renderButton(basePanel);
        }
        basePanel.setTouchable(false);
        basePanel.setAnimation(Animation.fixed_down);
        basePanel.enableRectangularMask();
        basePanel.setShadow(true);
    }


    @Override
    public void hide() {
        destroyByIndex(370, 379);
        if (customizableListYio != null) {
            customizableListYio.destroy();
            customizableListYio.getFactor().destroy(1, 3);
        }
    }


    public void setParentScene(AbstractScene parentScene) {
        this.parentScene = parentScene;
    }


    public void setChosenFraction(int chosenFraction) {
        this.chosenFraction = chosenFraction;
        loadValues();
    }
}
