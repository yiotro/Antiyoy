package yio.tro.antiyoy.gameplay.game_view;

public abstract class GameRender {


    protected GameView gameView;


    public GameRender(GameView gameView) {
        this.gameView = gameView;

        loadTextures();
    }


    public abstract void loadTextures();


    public abstract void render();
}
