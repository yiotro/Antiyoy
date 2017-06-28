package yio.tro.antiyoy.gameplay.game_view;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import yio.tro.antiyoy.Fonts;
import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.Hex;
import yio.tro.antiyoy.gameplay.editor.LevelEditor;
import yio.tro.antiyoy.gameplay.rules.GameRules;

public class RenderLevelEditorStuff extends GameRender{

    LevelEditor levelEditor;
    private final GameController gameController;
    BitmapFont moneyFont;


    public RenderLevelEditorStuff(GameView gameView) {
        super(gameView);

        gameController = gameView.gameController;
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
}
