package yio.tro.antiyoy.gameplay.touch_mode;

import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.Hex;
import yio.tro.antiyoy.gameplay.editor.EditorProvinceData;
import yio.tro.antiyoy.gameplay.editor.EditorProvinceManager;
import yio.tro.antiyoy.gameplay.game_view.GameRender;
import yio.tro.antiyoy.menu.scenes.Scenes;
import yio.tro.antiyoy.stuff.object_pool.ObjectPoolYio;

import java.util.ArrayList;

public class TmEditProvinces extends TouchMode{

    public ArrayList<TmepCityName> cityNames;
    ObjectPoolYio<TmepCityName> poolCityNames;


    public TmEditProvinces(GameController gameController) {
        super(gameController);
        cityNames = new ArrayList<>();
        initPools();
    }


    private void initPools() {
        poolCityNames = new ObjectPoolYio<TmepCityName>(cityNames) {
            @Override
            public TmepCityName makeNewObject() {
                return new TmepCityName();
            }
        };
    }


    @Override
    public void onModeBegin() {
        gameController.highlightManager.clear();
        updateCityNames();
    }


    private void updateCityNames() {
        poolCityNames.clearExternalList();
        EditorProvinceManager editorProvinceManager = gameController.levelEditorManager.editorProvinceManager;
        for (EditorProvinceData editorProvinceData : editorProvinceManager.provincesList) {
            TmepCityName freshObject = poolCityNames.getFreshObject();
            freshObject.setEditorProvinceData(editorProvinceData);
        }
    }


    @Override
    public void onModeEnd() {
        poolCityNames.clearExternalList();
    }


    @Override
    public void move() {
        moveCityNames();
    }


    private void moveCityNames() {
        for (TmepCityName cityName : cityNames) {
            cityName.move();
        }
    }


    @Override
    public boolean isCameraMovementEnabled() {
        return true;
    }


    @Override
    public void onTouchDown() {

    }


    @Override
    public void onTouchDrag() {

    }


    @Override
    public void onTouchUp() {

    }


    @Override
    public boolean onClick() {
        gameController.fieldManager.updateFocusedHex();
        EditorProvinceManager editorProvinceManager = gameController.levelEditorManager.editorProvinceManager;
        Hex focusedHex = gameController.fieldManager.focusedHex;
        EditorProvinceData provinceByHex = editorProvinceManager.getProvinceByHex(focusedHex);
        if (provinceByHex != null) {
            gameController.highlightManager.highlightEditorProvince(provinceByHex);
            Scenes.sceneEditorProvincePanel.create();
            Scenes.sceneEditorProvincePanel.setEditorProvinceData(provinceByHex);
        }
        return true;
    }


    @Override
    public GameRender getRender() {
        return gameController.yioGdxGame.gameView.rList.renderTmEditProvinces;
    }


    @Override
    public String getNameKey() {
        return null;
    }
}
