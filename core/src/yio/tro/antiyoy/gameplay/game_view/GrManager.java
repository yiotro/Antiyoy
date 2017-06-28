package yio.tro.antiyoy.gameplay.game_view;

public class GrManager {


    GameView gameView;
    RenderLevelEditorStuff renderLevelEditorStuff;


    public GrManager(GameView gameView) {
        this.gameView = gameView;
    }


    public void create() {
        renderLevelEditorStuff = new RenderLevelEditorStuff(gameView);
    }


    public void render() {
        renderLevelEditorStuff.render();
    }
}
