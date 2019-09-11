package yio.tro.antiyoy.gameplay.replays.actions;

public class RepActionFactory {


    public RepAction createAction(int actionType) {
        switch (actionType) {
            default: return null;
            case RepAction.UNIT_BUILT: return new RaUnitBuilt(null, null, -1);
            case RepAction.UNIT_MOVED: return new RaUnitMoved(null, null);
            case RepAction.TOWER_BUILT: return new RaTowerBuilt(null, false);
            case RepAction.FARM_BUILT: return new RaFarmBuilt(null);
            case RepAction.PALM_SPAWNED: return new RaPalmSpawned(null);
            case RepAction.PINE_SPAWNED: return new RaPineSpawned(null);
            case RepAction.TURN_ENDED: return new RaTurnEnded();
            case RepAction.CITY_SPAWNED: return new RaCitySpawned(null);
            case RepAction.UNIT_DIED_FROM_STARVATION: return new RaUnitDiedFromStarvation(null);
            case RepAction.HEX_CHANGED_FRACTION: return new RaHexFractionChanged(null, -1);
            case RepAction.UNIT_SPAWNED: return new RaUnitSpawned(null, -1);
        }
    }

}
