package yio.tro.antiyoy.gameplay.highlight;

import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.Hex;
import yio.tro.antiyoy.gameplay.editor.EditorProvinceData;
import yio.tro.antiyoy.stuff.object_pool.ObjectPoolYio;

import java.util.ArrayList;

public class HighlightManager {

    GameController gameController;
    public ArrayList<HighlightItem> items;
    ObjectPoolYio<HighlightItem> poolItems;


    public HighlightManager(GameController gameController) {
        this.gameController = gameController;
        items = new ArrayList<>();
        initPools();
    }


    private void initPools() {
        poolItems = new ObjectPoolYio<HighlightItem>(items) {
            @Override
            public HighlightItem makeNewObject() {
                return new HighlightItem();
            }
        };
    }


    public void move() {
        moveHighlightItems();
    }


    public void highlightHexList(ArrayList<Hex> hexList, boolean stuck) {
        for (Hex hex : hexList) {
            for (int dir = 0; dir < 6; dir++) {
                Hex adjacentHex = hex.getAdjacentHex(dir);
                if (adjacentHex == null) continue;
                if (adjacentHex.isNullHex()) continue;
                if (hexList.contains(adjacentHex)) continue;

                HighlightItem highlightItem = addItem(hex, adjacentHex);
                highlightItem.setStuck(stuck);
            }
        }
    }


    public void unStuckAllItems() {
        for (HighlightItem highlightItem : items) {
            highlightItem.setStuck(false);
        }
    }


    public void highlightEditorProvince(EditorProvinceData editorProvinceData) {
        highlightHexList(editorProvinceData.hexList, false);
    }


    private HighlightItem addItem(Hex one, Hex two) {
        HighlightItem freshObject = poolItems.getFreshObject();
        freshObject.setBy(one, two);
        return freshObject;
    }


    private void moveHighlightItems() {
        for (HighlightItem highlightItem : items) {
            highlightItem.move();
        }
    }


    public void clear() {
        poolItems.clearExternalList();
    }
}
