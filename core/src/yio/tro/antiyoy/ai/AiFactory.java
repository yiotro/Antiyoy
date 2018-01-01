package yio.tro.antiyoy.ai;

import yio.tro.antiyoy.gameplay.DebugFlags;
import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.rules.GameRules;

import java.util.ArrayList;

public class AiFactory {

    private final GameController gameController;
    ArrayList<ArtificialIntelligence> aiList;


    public AiFactory(GameController gameController) {
        this.gameController = gameController;
    }


    public void createAiList(int difficulty) {
        aiList = gameController.getAiList();
        aiList.clear();

        for (int i = 0; i < GameRules.colorNumber; i++) {
            addAiToList(difficulty, i);
        }
    }


    private void addAiToList(int difficulty, int i) {
        switch (difficulty) {
            default:
            case ArtificialIntelligence.DIFFICULTY_EASY:
                aiList.add(getEasyAi(i));
                break;
            case ArtificialIntelligence.DIFFICULTY_NORMAL:
                aiList.add(getNormalAi(i));
                break;
            case ArtificialIntelligence.DIFFICULTY_HARD:
                aiList.add(getHardAi(i));
                break;
            case ArtificialIntelligence.DIFFICULTY_EXPERT:
                aiList.add(getExpertAi(i));
                break;
            case ArtificialIntelligence.DIFFICULTY_BALANCER:
                aiList.add(getBalancerAi(i));
                break;
        }
    }


    private ArtificialIntelligence getBalancerAi(int i) {
        if (GameRules.slayRules) {
            return new AiBalancerSlayRules(gameController, i);
        }

        return new AiBalancerGenericRules(gameController, i);
    }


    private ArtificialIntelligence getExpertAi(int i) {
        if (GameRules.slayRules) {
            return new AiExpertSlayRules(gameController, i);
        }

        return new AiExpertGenericRules(gameController, i);
    }


    private ArtificialIntelligence getHardAi(int i) {
        if (GameRules.slayRules) {
            return new AiHardSlayRules(gameController, i);
        }

        return new AiHardGenericRules(gameController, i);
    }


    private ArtificialIntelligence getNormalAi(int i) {
        if (GameRules.slayRules) {
            return new AiNormalSlayRules(gameController, i);
        }

        return new AiNormalGenericRules(gameController, i);
    }


    private ArtificialIntelligence getEasyAi(int i) {
        return new AiEasy(gameController, i);
    }

}