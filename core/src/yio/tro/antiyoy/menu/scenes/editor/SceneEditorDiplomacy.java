package yio.tro.antiyoy.menu.scenes.editor;

import yio.tro.antiyoy.gameplay.editor.*;
import yio.tro.antiyoy.menu.Animation;
import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.customizable_list.*;
import yio.tro.antiyoy.menu.scenes.Scenes;
import yio.tro.antiyoy.menu.scenes.gameplay.AbstractModalScene;

public class SceneEditorDiplomacy extends AbstractModalScene {


    private Reaction rbHide;
    private ButtonYio basePanel;
    private double panelHeight;
    private CustomizableListYio customizableListYio;
    private SliReaction sliRelationClick;


    public SceneEditorDiplomacy(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);
        panelHeight = 0.5;
        initReactions();
    }


    @Override
    public void create() {
        createInvisibleCloseButton(rbHide);
        createBasePanel();
        createList();
        loadValues();
    }


    private void loadValues() {
        customizableListYio.clearItems();

        TitleListItem titleListItem = new TitleListItem();
        titleListItem.setTitle(getString("diplomacy"));
        customizableListYio.addItem(titleListItem);

        LevelEditorManager levelEditorManager = getGameController().levelEditorManager;
        EditorRelationsManager editorRelationsManager = levelEditorManager.editorRelationsManager;
        for (EditorRelation relation : editorRelationsManager.relations) {
            RelationListItem relationListItem = new RelationListItem();
            relationListItem.set(
                    relation,
                    getLargestProvinceName(relation.color1),
                    getLargestProvinceName(relation.color2)
            );
            relationListItem.setClickReaction(sliRelationClick);
            customizableListYio.addItem(relationListItem);
        }

        ScrollListItem scrollListItem = new ScrollListItem();
        scrollListItem.setTitle("+");
        scrollListItem.setCentered(true);
        scrollListItem.setClickReaction(getAddNewRelationReaction());
        customizableListYio.addItem(scrollListItem);
    }


    private String getLargestProvinceName(int fraction) {
        LevelEditorManager levelEditorManager = getGameController().levelEditorManager;
        EditorProvinceManager editorProvinceManager = levelEditorManager.editorProvinceManager;
        EditorProvinceData largestProvince = editorProvinceManager.getLargestProvince(fraction);
        if (largestProvince == null) {
            return " ";
        }

        return largestProvince.name;
    }


    private SliReaction getAddNewRelationReaction() {
        return new SliReaction() {
            @Override
            public void apply(AbstractCustomListItem item) {
                onAddRelationItemClicked();
            }
        };
    }


    private void onRelationItemClicked(RelationListItem relationListItem) {
        hide();
        Scenes.sceneEditorEditRelation.create();
        Scenes.sceneEditorEditRelation.setSelectedRelation(relationListItem.editorRelation);
    }


    private void onAddRelationItemClicked() {
        hide();
        Scenes.sceneEditorEditRelation.create();
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
        customizableListYio.setPosition(generateRectangle(0.02, SceneEditorOverlay.PANEL_HEIGHT, 0.96, panelHeight - 0.02));

        menuControllerYio.addElementToScene(customizableListYio);
    }


    private void createBasePanel() {
        basePanel = buttonFactory.getButton(generateRectangle(0, SceneEditorOverlay.PANEL_HEIGHT, 1, panelHeight), 281, null);
        if (basePanel.notRendered()) {
            basePanel.cleatText();
            basePanel.addEmptyLines(1);
            basePanel.loadCustomBackground("gray_pixel.png");
            basePanel.setIgnorePauseResume(true);
            menuControllerYio.buttonRenderer.renderButton(basePanel);
        }
        basePanel.setTouchable(false);
        basePanel.setAnimation(Animation.fixed_down);
        basePanel.enableRectangularMask();
        basePanel.setShadow(true);
    }


    private void initReactions() {
        rbHide = new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                hide();
            }
        };
        sliRelationClick = new SliReaction() {
            @Override
            public void apply(AbstractCustomListItem item) {
                onRelationItemClicked((RelationListItem) item);
            }
        };
    }


    @Override
    public void hide() {
        destroyByIndex(280, 289);
        if (customizableListYio != null) {
            customizableListYio.destroy();
        }
    }
}
