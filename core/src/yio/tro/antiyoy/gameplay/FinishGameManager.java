package yio.tro.antiyoy.gameplay;

import yio.tro.antiyoy.gameplay.campaign.CampaignProgressManager;
import yio.tro.antiyoy.gameplay.data_storage.EncodeableYio;
import yio.tro.antiyoy.gameplay.diplomacy.DiplomaticEntity;
import yio.tro.antiyoy.gameplay.rules.GameRules;
import yio.tro.antiyoy.gameplay.user_levels.UserLevelsManager;
import yio.tro.antiyoy.menu.scenes.Scenes;
import yio.tro.antiyoy.stuff.Yio;

public class FinishGameManager implements EncodeableYio{

    GameController gameController;
    private FieldManager fieldManager;
    public GoalType goalType;
    public int arg1;


    public FinishGameManager(GameController gameController) {
        this.gameController = gameController;
        goalType = GoalType.def;
    }


    public void defaultValues() {
        goalType = GoalType.def;
        arg1 = 0;
    }


    public void perform() {
        fieldManager = gameController.fieldManager;
        if (GameRules.replayMode) return;

        if (areDefaultConditionsForced()) {
            applyDefaultConditions();
            return;
        }

        switch (goalType) {
            default:
                System.out.println("FinishGameManager.perform: " + goalType);
                break;
            case def:
                applyDefaultConditions();
                break;
            case destroy_everyone:
                applyDestroyEveryone();
                break;
            case diplomatic_victory:
                applyDiplomaticVictory();
                break;
            case destroy_target_kingdom:
                applyDestroyTargetKingdom();
                break;
            case ensure_target_victory:
                applyEnsureTargetVictory();
                break;
            case survive_long_enough:
                applySurviveLongEnough();
                break;
            case reach_target_income:
                applyReachTargetIncome();
                break;
        }
    }


    private boolean areDefaultConditionsForced() {
        if (goalType == GoalType.ensure_target_victory) return false;
        if (goalType == GoalType.destroy_everyone) return false;
        return !atLeastOneHumanPlayerAlive();
    }


    private boolean atLeastOneHumanPlayerAlive() {
        if (fieldManager.gameController.playersNumber == 0) return false;
        for (Province province : fieldManager.provinces) {
            if (province.getFraction() == 0) return true;
        }
        return false;
    }


    private void applyReachTargetIncome() {
        int income = calculateIncome();
        if (income < arg1) return;
        endGame(0);
    }


    private int calculateIncome() {
        int sum = 0;
        for (Province province : fieldManager.provinces) {
            if (province.getFraction() != 0) continue;
            sum += province.getIncome();
        }
        DiplomaticEntity entity = fieldManager.diplomacyManager.getEntity(0);
        if (entity != null) {
            sum += entity.getStateDotations();
        }
        return sum;
    }


    private void applySurviveLongEnough() {
        int winnerFraction = checkIfWeHaveWinner(true);
        if (winnerFraction == 0) {
            endGame(winnerFraction);
            return;
        }

        if (gameController.matchStatistics.turnsMade < arg1 - 1) return;
        Province biggestProvince = fieldManager.getBiggestProvince(0);
        if (biggestProvince == null) {
            System.out.println("FinishGameManager.applySurviveLongEnough");
            return;
        }
        endGame(0);
    }


    private void applyEnsureTargetVictory() {
        int fraction = arg1;

        if (fieldManager.getBiggestProvince(fraction) == null) {
            endGame(GameRules.NEUTRAL_FRACTION);
            return;
        }

        boolean onlyOneFractionAlive = fieldManager.isOnlyOneFractionAlive(fraction);
        if (!onlyOneFractionAlive) return;
        endGame(0);
    }


    private void applyDestroyTargetKingdom() {
        int fraction = arg1;
        Province biggestProvince = fieldManager.getBiggestProvince(fraction);
        if (biggestProvince != null) return;
        endGame(0);
    }


    private void applyDiplomaticVictory() {
        int winnerFraction = fieldManager.diplomacyManager.getDiplomaticWinner();
        if (winnerFraction < 0) return;
        endGame(winnerFraction);
    }


    private void applyDestroyEveryone() {
        int winnerFraction = checkIfWeHaveWinner(false);
        if (winnerFraction < 0) return;
        endGame(winnerFraction);
    }


    private void applyDefaultConditions() {
        int winnerFraction = checkIfWeHaveWinner(true);
        if (winnerFraction >= 0) {
            endGame(winnerFraction);
            return;
        }

        checkToProposeSurrender();
    }


    private void checkToProposeSurrender() {
        if (GameRules.diplomacyEnabled) return;
        if (gameController.playersNumber != 1) return;

        if (!gameController.proposedSurrender) {
            int possibleWinner = fieldManager.possibleWinner();
            if (possibleWinner >= 0 && isPlayerTurn(possibleWinner)) {
                doProposeSurrender();
                gameController.proposedSurrender = true;
            }
        }
    }


    private void doProposeSurrender() {
        onGameFinished(gameController.turn);
        Scenes.sceneSurrenderDialog.create();
    }


    private int checkIfWeHaveWinner(boolean def) {
        if (fieldManager.activeHexes.size() == 0) return -1;
        if (def && GameRules.diplomacyEnabled) {
            return fieldManager.diplomacyManager.getDiplomaticWinner();
        }
        if (!fieldManager.isThereOnlyOneKingdomOnMap()) return -1;

        for (Province province : fieldManager.provinces) {
            if (province.hexList.get(0).isNeutral()) continue;
            return province.getFraction();
        }

        System.out.println("FinishGameManager.checkIfWeHaveWinner(): problem");
        return -1;
    }


    private void endGame(int winFraction) {
        if (DebugFlags.testMode) {
            DebugFlags.testWinner = winFraction;
            return;
        }

        onGameFinished(winFraction);

        if (winFraction == 0) {
            GlobalStatistics.getInstance().onGameWon();
        }

        Scenes.sceneIncomeGraph.hide();
        Scenes.sceneAfterGameMenu.create(winFraction, isPlayerTurn(winFraction));
    }


    public boolean isPlayerTurn(int turn) {
        return gameController.isPlayerTurn(turn);
    }


    private void onGameFinished(int winFraction) {
        checkToTagCampaignLevel(winFraction);
        checkToTagUserLevel(winFraction);
    }


    private void checkToTagUserLevel(int winFraction) {
        if (!isPlayerTurn(winFraction)) return;

        String key = GameRules.ulKey;
        if (key == null) return;

        UserLevelsManager instance = UserLevelsManager.getInstance();

        instance.onLevelCompleted(key);
    }


    private void checkToTagCampaignLevel(int winFraction) {
        CampaignProgressManager instance = CampaignProgressManager.getInstance();

        if (instance.areCampaignLevelCompletionConditionsSatisfied(winFraction)) {
            instance.markLevelAsCompleted(instance.currentLevelIndex);
            Scenes.sceneCampaignMenu.updateLevelSelector();
        }
    }


    public void onEndCreation() {
        checkToShowGoalView();
    }


    public void checkToShowGoalView() {
        if (gameController.isInEditorMode()) return;
        if (goalType == GoalType.def) return;
        Scenes.sceneGoalView.create();
    }


    public void checkToApplyAdditionalData() {
        String goalData = gameController.initialParameters.goalData;
        if (goalData == null) return;
        if (goalData.length() < 3) return;

        decode(goalData);

        onEndCreation();
    }


    public boolean isStringArgumentNeeded(GoalType goalType) {
        switch (goalType) {
            default:
                return false;
            case reach_target_income:
            case survive_long_enough:
                return true;
        }
    }


    public boolean isColorIconNeeded(GoalType goalType) {
        switch (goalType) {
            default:
                return false;
            case ensure_target_victory:
            case destroy_target_kingdom:
                return true;
        }
    }


    @Override
    public String encode() {
        return goalType + " " + arg1;
    }


    public void setGoalType(GoalType goalType) {
        this.goalType = goalType;
    }


    @Override
    public void decode(String source) {
        if (source == null) return;
        if (source.length() < 3) return;

        String[] split = source.split(" ");
        if (split.length < 2) return;

        setGoalType(GoalType.valueOf(split[0]));
        arg1 = Integer.valueOf(split[1]);
    }
}
