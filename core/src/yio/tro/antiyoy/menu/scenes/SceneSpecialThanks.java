package yio.tro.antiyoy.menu.scenes;

import yio.tro.antiyoy.menu.ButtonYio;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.menu.SpecialThanksDialog;
import yio.tro.antiyoy.menu.behaviors.Reaction;
import yio.tro.antiyoy.stuff.LanguagesManager;

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

        backButton = menuControllerYio.spawnBackButton(900, Reaction.RB_ABOUT_GAME);

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

        String src = LanguagesManager.getInstance().getString("special_thanks");
        for (String token : src.split("#")) {
            if (token.length() < 2) continue;

            String[] split = token.split(":");
            String name = split[0];
            String desc = split[1];
            if (desc.length() < 2) continue;

            dialog.addItem("-", name, desc);
        }
    }
}
