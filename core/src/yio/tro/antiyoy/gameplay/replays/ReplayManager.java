package yio.tro.antiyoy.gameplay.replays;

import yio.tro.antiyoy.SettingsManager;
import yio.tro.antiyoy.gameplay.FieldManager;
import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.Hex;
import yio.tro.antiyoy.gameplay.Province;
import yio.tro.antiyoy.gameplay.campaign.CampaignProgressManager;
import yio.tro.antiyoy.gameplay.loading.LoadingManager;
import yio.tro.antiyoy.gameplay.loading.LoadingParameters;
import yio.tro.antiyoy.gameplay.loading.LoadingType;
import yio.tro.antiyoy.gameplay.replays.actions.*;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.menu.scenes.Scenes;

public class ReplayManager {

    GameController gameController;
    Replay replay;


    public ReplayManager(GameController gameController) {
        this.gameController = gameController;

        replay = null;
    }


    public void defaultValues() {
        replay = new Replay(gameController);
    }


    public void onEndCreation() {
        replay.updateInitialLevelString();
        replay.prepare();

        // some actions can be added during loading process
        // they shouldn't be in replay
        if (!GameRules.replayMode) {
            replay.actions.clear();
            replay.buffer.clear();
        }
    }


    public void onPineSpawned(Hex hex) {
        if (!canAddAction()) return;
        replay.addAction(new RaPineSpawned(hex));
    }


    public void onLoadingFromSlotFinished(FieldManager fieldManager) {
        if (replay != null) {
            replay.updateActionsFromString(fieldManager);
        }
    }


    public void performStep() {
        if (replay != null) {
            replay.performStep();
        }
    }


    private boolean canAddAction() {
        if (replay == null) return false;
        if (GameRules.replayMode) return false;
        if (!SettingsManager.replaysEnabled) return false;

        return true;
    }


    public void onPalmSpawned(Hex hex) {
        if (!canAddAction()) return;
        replay.addAction(new RaPalmSpawned(hex));
    }


    public void onUnitBuilt(Province src, Hex dst, int strength) {
        if (!canAddAction()) return;
        replay.addAction(new RaUnitBuilt(src.getCapital(), dst, strength));
    }


    public void onUnitSpawned(Hex hex, int strength) {
        if (!canAddAction()) return;
        replay.addAction(new RaUnitSpawned(hex, strength));
    }


    public void onUnitMoved(Hex src, Hex dst) {
        if (!canAddAction()) return;
        replay.addAction(new RaUnitMoved(src, dst));
    }


    public void onTowerBuilt(Hex hex, boolean strong) {
        if (!canAddAction()) return;
        replay.addAction(new RaTowerBuilt(hex, strong));
    }


    public void onFarmBuilt(Hex hex) {
        if (!canAddAction()) return;
        replay.addAction(new RaFarmBuilt(hex));
    }


    public void onStopButtonPressed() {
        replay.recreateInitialSituation();
        replay.prepare();
        gameController.onInitialSnapshotRecreated();
    }


    public void startInstantReplay() {
        Replay copyReplay = new Replay(gameController);
        replay.saveToPreferences("temp");
        copyReplay.loadFromPreferences("temp");

        LoadingParameters loadingParameters = new LoadingParameters();
        loadingParameters.loadingType = LoadingType.load_replay;
        gameController.gameSaver.legacyImportManager.applyFullLevel(loadingParameters, copyReplay.initialLevelString);
        loadingParameters.replay = copyReplay;
        loadingParameters.playersNumber = 0;
        loadingParameters.colorOffset = gameController.colorsManager.colorOffset;
        loadingParameters.slayRules = GameRules.slayRules;

        if (GameRules.campaignMode) {
            loadingParameters.campaignLevelIndex = CampaignProgressManager.getInstance().currentLevelIndex;
        } else {
            loadingParameters.campaignLevelIndex = -1;
        }

        LoadingManager.getInstance().startGame(loadingParameters);

        Scenes.sceneAiOnlyOverlay.speedPanel.showSaveIcon();
    }


    public void saveCurrentReplay() {
        ReplaySaveSystem instance = ReplaySaveSystem.getInstance();
        instance.saveReplay(replay);
    }


    public void onTurnEnded() {
        if (!canAddAction()) return;
        replay.addAction(new RaTurnEnded());
    }


    public void onCitySpawned(Hex hex) {
        if (!canAddAction()) return;
        replay.addAction(new RaCitySpawned(hex));
    }


    public void onUnitDiedFromStarvation(Hex hex) {
        if (!canAddAction()) return;
        replay.addAction(new RaUnitDiedFromStarvation(hex));
    }


    public void onHexChangedFractionWithoutObviousReason(Hex hex) {
        replay.addAction(new RaHexFractionChanged(hex, hex.fraction));
    }


    public void setReplay(Replay replay) {
        this.replay = replay;
    }


    public Replay getReplay() {
        return replay;
    }


    public void showInConsole() {
        if (replay != null) {
            replay.showInConsole();
        } else {
            System.out.println("replay is null");
        }
    }
}
