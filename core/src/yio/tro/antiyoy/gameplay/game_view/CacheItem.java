package yio.tro.antiyoy.gameplay.game_view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import yio.tro.antiyoy.stuff.FrameBufferYio;
import yio.tro.antiyoy.stuff.GraphicsYio;
import yio.tro.antiyoy.stuff.RectangleYio;
import yio.tro.antiyoy.stuff.object_pool.ReusableYio;

public class CacheItem implements ReusableYio{

    public TextureRegion textureRegion;
    public RectangleYio position;
    FrameBuffer frameBuffer;


    public CacheItem() {
        textureRegion = null;
        position = new RectangleYio();
        frameBuffer = FrameBufferYio.getInstance(Pixmap.Format.RGB565, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
    }


    @Override
    public void reset() {
        textureRegion = null;
        position.reset();
    }
}
