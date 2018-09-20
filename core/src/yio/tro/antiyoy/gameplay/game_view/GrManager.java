package yio.tro.antiyoy.gameplay.game_view;

import java.util.ArrayList;

public class GrManager {


    GameView gameView;
    public ArrayList<GameRender> gameRenderList;

    public RenderLevelEditorStuff renderLevelEditorStuff;
    public RenderFogOfWar renderFogOfWar;
    public RenderCityNames renderCityNames;
    public RenderMoveZone renderMoveZone;
    // init them lower


    public GrManager(GameView gameView) {
        this.gameView = gameView;

        gameRenderList = new ArrayList<>();
    }


    public void create() {
        renderLevelEditorStuff = new RenderLevelEditorStuff(this);
        renderFogOfWar = new RenderFogOfWar(this);
        renderCityNames = new RenderCityNames(this);
        renderMoveZone = new RenderMoveZone(this);
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
