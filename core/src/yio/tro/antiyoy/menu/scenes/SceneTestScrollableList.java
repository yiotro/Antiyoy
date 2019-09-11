package yio.tro.antiyoy.menu.scenes;

import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.menu.scrollable_list.ListBehaviorYio;
import yio.tro.antiyoy.menu.scrollable_list.ListItemYio;
import yio.tro.antiyoy.menu.scrollable_list.ScrollableListYio;

public class SceneTestScrollableList extends AbstractScene{

    private ButtonYio backButton;
    public ScrollableListYio scrollableListYio;


    public SceneTestScrollableList(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        scrollableListYio = null;
    }


    @Override
    public void create() {
        menuControllerYio.beginMenuCreation();

        menuControllerYio.getYioGdxGame().beginBackgroundChange(1, false, true);

        backButton = menuControllerYio.spawnBackButton(710, new Reaction() {
            @Override
            public void perform(ButtonYio buttonYio) {
                Scenes.sceneMainMenu.create();
            }
        });

        createList();

        menuControllerYio.endMenuCreation();
    }


    private void createList() {
        initList();

        scrollableListYio.appear();
    }


    private void initList() {
        if (scrollableListYio != null) return;

        scrollableListYio = new ScrollableListYio(menuControllerYio);
        scrollableListYio.setPosition(generateRectangle(0.05, 0.07, 0.9, 0.75));

        scrollableListYio.setTitle("Title");
//        for (int i = 0; i < 3; i++) {
//            scrollableListYio.addItem("key" + i, "item " + i, "description");
//        }

        scrollableListYio.setListBehavior(new ListBehaviorYio() {
            @Override
            public void applyItem(ListItemYio item) {
                System.out.println();
                System.out.println("SceneTestScrollableList.applyItem");
                System.out.println("item = " + item.title);
            }


            @Override
            public void onItemRenamed(ListItemYio item) {

            }


            @Override
            public void onItemDeleteRequested(ListItemYio item) {

            }
        });

        menuControllerYio.addElementToScene(scrollableListYio);
    }
}
