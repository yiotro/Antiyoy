package yio.tro.antiyoy.gameplay.game_view;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.antiyoy.gameplay.Hex;
import yio.tro.antiyoy.gameplay.Province;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomacyManager;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomaticEntity;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomaticRelation;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.stuff.CircleYio;
import yio.tro.antiyoy.stuff.GraphicsYio;

public class RenderDiplomaticIndicators extends GameRender{


    private TextureRegion enemyTexture;
    private TextureRegion friendTexture;
    CircleYio tempCircle;


    public RenderDiplomaticIndicators(GameRendersList gameRendersList) {
        super(gameRendersList);
        tempCircle = new CircleYio();
        tempCircle.setRadius(2 * GraphicsYio.borderThickness);
    }


    @Override
    public void loadTextures() {
        enemyTexture = GraphicsYio.loadTextureRegion("diplomacy/dip_ind_enemy.png", true);
        friendTexture = GraphicsYio.loadTextureRegion("diplomacy/dip_ind_friend.png", true);
    }


    @Override
    public void render() {
        if (!GameRules.diplomacyEnabled) return;
        DiplomacyManager diplomacyManager = gameController.fieldManager.diplomacyManager;
        DiplomaticEntity mainEntity = diplomacyManager.getMainEntity();
        if (!mainEntity.isHuman()) return;
        for (Province province : gameController.fieldManager.provinces) {
            if (province.getFraction() == mainEntity.fraction) continue;
            DiplomaticEntity entity = diplomacyManager.getEntity(province.getFraction());
            int relation = entity.getRelation(mainEntity);
            if (relation == DiplomaticRelation.NEUTRAL) continue;
            renderRelation(province, relation);
        }
    }


    private void renderRelation(Province province, int relation) {
        Hex capital = province.getCapital();
        if (capital == null) return;
        TextureRegion relationTexture = getRelationTexture(relation);
        if (relationTexture == null) return;
        tempCircle.center.setBy(capital.pos);
        tempCircle.center.y += 5 * GraphicsYio.borderThickness;
        tempCircle.center.x += 4 * GraphicsYio.borderThickness;
        GraphicsYio.drawByCircle(batchMovable, relationTexture, tempCircle);
    }


    private TextureRegion getRelationTexture(int relation) {
        switch (relation) {
            default:
                return null;
            case DiplomaticRelation.ENEMY:
                return enemyTexture;
            case DiplomaticRelation.FRIEND:
                return friendTexture;
        }
    }


    @Override
    public void disposeTextures() {

    }
}
