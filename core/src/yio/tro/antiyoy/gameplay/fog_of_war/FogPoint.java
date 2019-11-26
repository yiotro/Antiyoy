package yio.tro.antiyoy.gameplay.fog_of_war;

import yio.tro.antiyoy.gameplay.Hex;
import yio.tro.antiyoy.stuff.PointYio;
import yio.tro.antiyoy.stuff.object_pool.ReusableYio;

public class FogPoint implements ReusableYio{


    FogOfWarManager fogOfWarManager;
    Hex hex;
    public boolean status;
    public PointYio position;


    public FogPoint(FogOfWarManager fogOfWarManager) {
        this.fogOfWarManager = fogOfWarManager;

        position = new PointYio();
    }


    @Override
    public void reset() {
        hex = null;
        position.reset();
        status = true;
    }


    public boolean isVisible() {
        return status && fogOfWarManager.visibleArea.isPointInside(position, fogOfWarManager.fieldManager.hexSize);
    }


    public void setHexByIndexes(int i, int j) {
        hex = fogOfWarManager.fieldManager.getHex(i, j);

        fogOfWarManager.fieldManager.updatePointByHexIndexes(position, i, j);
    }


    public void setStatus(boolean status) {
        this.status = status;
    }


    @Override
    public String toString() {
        return "[FogPoint: " +
                hex + " " + status +
                "]";
    }
}
