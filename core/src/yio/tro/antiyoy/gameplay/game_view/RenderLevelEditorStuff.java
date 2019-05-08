package yio.tro.antiyoy.gameplay.game_view;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import yio.tro.antiyoy.stuff.Fonts;
import yio.tro.antiyoy.gameplay.editor.LevelEditor;
import yio.tro.antiyoy.gameplay.rules.GameRules;

public class RenderLevelEditorStuff extends GameRender{

    LevelEditor levelEditor;
    BitmapFont moneyFont;


    public RenderLevelEditorStuff(GameRendersList gameRendersList) {
        super(gameRendersList);

        levelEditor = gameController.getLevelEditor();
        moneyFont = Fonts.microFont;
    }


    @Override
    public void loadTextures() {

    }


    @Override
    public void render() {
        if (!GameRules.inEditorMode) return;

        if (levelEditor.showMoney) {
            renderMoney();
        }
    }


    private void renderMoney() {

    }


    @Override
    public void disposeTextures() {

    }
}
