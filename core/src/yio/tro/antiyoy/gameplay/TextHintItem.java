package yio.tro.antiyoy.gameplay;

import yio.tro.antiyoy.factor_yio.FactorYio;
import yio.tro.antiyoy.stuff.Fonts;
import yio.tro.antiyoy.stuff.RenderableTextYio;
import yio.tro.antiyoy.stuff.object_pool.ReusableYio;

public class TextHintItem implements ReusableYio{

    GameController gameController;
    public Hex hex;
    public RenderableTextYio title;
    public FactorYio appearFactor;


    public TextHintItem(GameController gameController) {
        this.gameController = gameController;
        title = new RenderableTextYio();
        title.setFont(Fonts.microFont);
        appearFactor = new FactorYio();
    }


    @Override
    public void reset() {
        hex = null;
        appearFactor.reset();
    }


    public void move() {
        appearFactor.move();
        moveTitle();
    }


    private void moveTitle() {
        title.position.x = hex.pos.x - title.width / 2;
        title.position.y = hex.pos.y + title.height / 2;
        title.updateBounds();
    }


    public boolean isActive() {
        return appearFactor.get() > 0 || appearFactor.getGravity() > 0;
    }


    public void set(Hex hex, String string) {
        this.hex = hex;
        title.setString(string);
        title.updateMetrics();
        appearFactor.reset();
        appearFactor.setValues(1, 0);
        appearFactor.destroy(1, 0.5);
    }
}
