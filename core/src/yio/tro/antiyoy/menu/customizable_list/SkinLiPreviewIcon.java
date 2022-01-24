package yio.tro.antiyoy.menu.customizable_list;

import yio.tro.antiyoy.Storage3xTexture;
import yio.tro.antiyoy.gameplay.skins.SkinType;
import yio.tro.antiyoy.stuff.CircleYio;
import yio.tro.antiyoy.stuff.PointYio;

public class SkinLiPreviewIcon {

    SkinListItem skinListItem;
    public Storage3xTexture storage3xTexture;
    public CircleYio viewPosition;
    public PointYio delta;


    public SkinLiPreviewIcon(SkinListItem skinListItem) {
        this.skinListItem = skinListItem;
        storage3xTexture = null;
        viewPosition = new CircleYio();
        delta = new PointYio();
    }


    void loadTexture(String name) {
        String fileName = convertSkinTypeToPrefix(skinListItem.skinType) + "_" + name + ".png";
        storage3xTexture = new Storage3xTexture(skinListItem.atlasLoader, fileName);
    }


    private String convertSkinTypeToPrefix(SkinType skinType) {
        switch (skinType) {
            default:
                return "" + skinType;
            case original:
            case grid:
            case points:
                return "def";
        }
    }


    void move() {
        viewPosition.center.x = (float) (skinListItem.viewPosition.x + delta.x);
        viewPosition.center.y = (float) (skinListItem.viewPosition.y + delta.y);
    }
}
