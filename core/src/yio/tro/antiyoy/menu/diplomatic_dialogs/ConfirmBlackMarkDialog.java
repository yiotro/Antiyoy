package yio.tro.antiyoy.menu.diplomatic_dialogs;

import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomacyManager;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomaticEntity;
import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.stuff.Fonts;
import yio.tro.antiyoy.stuff.LanguagesManager;

import java.util.ArrayList;

public class ConfirmBlackMarkDialog extends AbstractDiplomaticDialog{

    ArrayList<String> lines;
    DiplomaticEntity selectedEntity;


    public ConfirmBlackMarkDialog(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        lines = new ArrayList<>();
    }


    @Override
    protected void makeLabels() {
        convertSourceLineToList(LanguagesManager.getInstance().getString("black_mark_description"), lines);

        LanguagesManager instance = LanguagesManager.getInstance();
        float y = (float) (position.height - topOffset);

        addLabel(instance.getString("confirm_black_mark"), Fonts.gameFont, leftOffset, y);
        y -= titleOffset;

        for (String line : lines) {
            addLabel(line, Fonts.smallerMenuFont, leftOffset, y);
            y -= lineOffset;
        }
    }


    @Override
    protected void onYesButtonPressed() {
        DiplomacyManager diplomacyManager = getDiplomacyManager();
        diplomacyManager.onUserRequestedBlackMark(selectedEntity);

        destroy();
    }


    public void setSelectedEntity(DiplomaticEntity selectedEntity) {
        this.selectedEntity = selectedEntity;
    }


    private DiplomacyManager getDiplomacyManager() {
        GameController gameController = menuControllerYio.yioGdxGame.gameController;
        return gameController.fieldManager.diplomacyManager;
    }


    @Override
    protected void onNoButtonPressed() {
        destroy();
    }


    @Override
    public boolean areButtonsEnabled() {
        return true;
    }
}
