package yio.tro.antiyoy.behaviors.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Matrix4;
import yio.tro.antiyoy.*;
import yio.tro.antiyoy.behaviors.ReactBehavior;
import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.GameView;
import yio.tro.antiyoy.menu.ButtonYio;

import java.util.Arrays;

/**
 * Created by ivan on 29.12.2015.
 */
public class RbShowColorStats extends ReactBehavior {



    @Override
    public void reactAction(ButtonYio buttonYio) {
        buttonYio.menuControllerYio.showColorStats();

        ColorStatsRenderer colorStatsRenderer = new ColorStatsRenderer(buttonYio.menuControllerYio);
        ButtonYio statButton = buttonYio.menuControllerYio.getButtonById(56321);
        int[] playerHexCount = getGameController(buttonYio).fieldController.getPlayerHexCount();
        colorStatsRenderer.renderStatButton(statButton, playerHexCount);
    }
}
