package yio.tro.antiyoy.menu.diplomatic_dialogs;

import yio.tro.antiyoy.menu.MenuControllerYio;
import yio.tro.antiyoy.stuff.Fonts;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.LanguagesManager;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class DipMessageDialog extends AbstractDiplomaticDialog {

    String sourceText, title;
    ArrayList<String> lines;


    public DipMessageDialog(MenuControllerYio menuControllerYio) {
        super(menuControllerYio);

        sourceText = null;
        title = null;
        lines = new ArrayList<>();
    }


    @Override
    protected void makeLabels() {
        LanguagesManager instance = LanguagesManager.getInstance();
        float y = (float) (position.height - topOffset);

        addLabel(instance.getString(title), Fonts.gameFont, leftOffset, y);
        y -= titleOffset;

        for (String line : lines) {
            addLabel(line, Fonts.smallerMenuFont, leftOffset, y);
            y -= lineOffset;
        }
    }


    public void setMessage(String title, String messageKey) {
        this.title = title;
        sourceText = LanguagesManager.getInstance().getString(messageKey);
        updateLines();
        updateAll();
        cutOffExcessiveLabels();
    }


    private void cutOffExcessiveLabels() {
        while (labels.size() > 5) {
            labels.remove(labels.size() - 1);
        }
    }


    private void updateLines() {
        convertSourceLineToList(sourceText, lines);
    }


    @Override
    protected void onYesButtonPressed() {

    }


    @Override
    protected void onNoButtonPressed() {

    }


    @Override
    public boolean areButtonsEnabled() {
        return false;
    }
}
