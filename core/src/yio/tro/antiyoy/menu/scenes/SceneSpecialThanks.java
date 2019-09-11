package yio.tro.antiyoy.menu.scenes;

import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.SpecialThanksDialog;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.stuff.LanguageChooseItem;
import yio.tro.antiyoy.stuff.LanguagesManager;

import java.util.ArrayList;

public class SceneSpecialThanks extends AbstractScene{

    SpecialThanksDialog dialog;
    ButtonYio backButton;


    public SceneSpecialThanks(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        dialog = null;
    }


    @Override
    public void create() {
        menuControllerYio.beginMenuCreation();

        menuControllerYio.getYioGdxGame().beginBackgroundChange(1, false, true);

        backButton = menuControllerYio.spawnBackButton(900, Reaction.rbAboutGame);

        createDialog();

        menuControllerYio.endMenuCreation();
    }


    private void createDialog() {
        if (dialog == null) {
            initDialogOnce();
        }

        dialog.appear();
    }


    private void initDialogOnce() {
        dialog = new SpecialThanksDialog(menuControllerYio);
        dialog.setPosition(generateRectangle(0.05, 0.07, 0.9, 0.75));
        dialog.setTitle(getString("special_thanks_title"));
        menuControllerYio.addElementToScene(dialog);

        String src = getTranslatorsString() + " " + LanguagesManager.getInstance().getString("special_thanks");
        for (String token : src.split("#")) {
            if (token.length() < 2) continue;

            String[] split = token.split(":");
            String name = split[0];
            String desc = split[1];
            if (desc.length() < 2) continue;

            if (name.length() == 5 && !name.equals("Music")) {
                name = name.substring(3);
            }

            dialog.addItem("-", name, desc);
        }
    }


    private String getTranslatorsString() {
        ArrayList<LanguageChooseItem> chooseListItems = LanguagesManager.getInstance().getChooseListItems();

        StringBuilder builder = new StringBuilder();

        for (LanguageChooseItem chooseListItem : chooseListItems) {
            if (chooseListItem.author.equals("yiotro")) continue;

            builder.append("#").append(chooseListItem.name).append(": ").append(chooseListItem.author);
        }

        return builder.toString();
    }
}
