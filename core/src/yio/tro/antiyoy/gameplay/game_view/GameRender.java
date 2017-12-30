package yio.tro.antiyoy.gameplay.game_view;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import yio.tro.antiyoy.gameplay.GameController;

public abstract class GameRender {


    protected final GrManager grManager;
    protected GameView gameView;
    protected final SpriteBatch batchMovable;
    protected final GameController gameController;


    public GameRender(GrManager grManager) {
        this.grManager = grManager;

        gameView = grManager.gameView;
        batchMovable = gameView.batchMovable;
        gameController = gameView.gameController;

        grManager.gameRenderList.add(this);
    }


    public abstract void loadTextures();


    public abstract void render();


    public abstract void disposeTextures();
}
