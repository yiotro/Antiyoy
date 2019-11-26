package yio.tro.antiyoy.menu.customizable_list;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomaticEntity;
import yio.tro.antiyoy.menu.render.AbstractRenderCustomListItem;
import yio.tro.antiyoy.menu.render.MenuRender;
import yio.tro.antiyoy.menu.scenes.Scenes;
import yio.tro.antiyoy.menu.scenes.gameplay.choose_entity.IDipEntityReceiver;
import yio.tro.antiyoy.stuff.Fonts;
import yio.tro.antiyoy.stuff.GraphicsYio;

public class SimpleDipEntityItem extends AbstractSingleLineItem{

    public DiplomaticEntity diplomaticEntity;
    public int backgroundColor;


    @Override
    protected BitmapFont getTitleFont() {
        return Fonts.smallerMenuFont;
    }


    @Override
    protected double getHeight() {
        return 0.07f * GraphicsYio.height;
    }


    @Override
    protected void onClicked() {
        Scenes.sceneChooseDiplomaticEntity.onDiplomaticEntityChosen(diplomaticEntity);
    }


    public void setDiplomaticEntity(DiplomaticEntity diplomaticEntity) {
        this.diplomaticEntity = diplomaticEntity;
        backgroundColor = getGameController().colorsManager.getColorByFraction(diplomaticEntity.fraction);
        setTitle("" + diplomaticEntity.capitalName);
    }


    @Override
    public AbstractRenderCustomListItem getRender() {
        return MenuRender.renderSimpleDipEntityItem;
    }
}
