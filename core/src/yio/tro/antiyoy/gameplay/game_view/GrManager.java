package yio.tro.antiyoy.gameplay.game_view;

import java.util.ArrayList;

public class GrManager {


    GameView gameView;
    RenderLevelEditorStuff renderLevelEditorStuff;
    public RenderFogOfWar renderFogOfWar;
    public ArrayList<GameRender> gameRenderList;


    public GrManager(GameView gameView) {
        this.gameView = gameView;

        gameRenderList = new ArrayList<>();
    }


    public void create() {
        renderLevelEditorStuff = new RenderLevelEditorStuff(this);
        renderFogOfWar = new RenderFogOfWar(this);
    }


    public void render() {
        renderLevelEditorStuff.render();
        renderFogOfWar.render();
    }


    public void loadTextures() {
        for (GameRender gameRender : gameRenderList) {
            gameRender.loadTextures();
        }
    }


    public void disposeTextures() {
        for (GameRender gameRender : gameRenderList) {
            gameRender.disposeTextures();
        }
    }
}
