package yio.tro.antiyoy.ai;

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


    public void createCustomAiList(int difficulties[]) {
        if (GameRules.colorNumber != difficulties.length) {
            System.out.println("AiFactory.createCustomAiList(): problem");
        }

        aiList = gameController.getAiList();
        aiList.clear();

        for (int i = 0; i < GameRules.colorNumber; i++) {
            addAiToList(difficulties[i], i);
        }
    }


    private void addAiToList(int difficulty, int colorIndex) {
        switch (difficulty) {
            default:
            case Difficulty.EASY:
                aiList.add(getEasyAi(colorIndex));
                break;
            case Difficulty.NORMAL:
                aiList.add(getNormalAi(colorIndex));
                break;
            case Difficulty.HARD:
                aiList.add(getHardAi(colorIndex));
                break;
            case Difficulty.EXPERT:
                aiList.add(getExpertAi(colorIndex));
                break;
            case Difficulty.BALANCER:
                aiList.add(getBalancerAi(colorIndex));
                break;
        }
    }


    private ArtificialIntelligence getBalancerAi(int colorIndex) {
        if (GameRules.slayRules) {
            return new AiBalancerSlayRules(gameController, colorIndex);
        }

        return new AiBalancerGenericRules(gameController, colorIndex);
    }


    private ArtificialIntelligence getExpertAi(int colorIndex) {
        if (GameRules.slayRules) {
            return new AiExpertSlayRules(gameController, colorIndex);
        }

        return new AiExpertGenericRules(gameController, colorIndex);
    }


    private ArtificialIntelligence getHardAi(int colorIndex) {
        if (GameRules.slayRules) {
            return new AiHardSlayRules(gameController, colorIndex);
        }

        return new AiHardGenericRules(gameController, colorIndex);
    }


    private ArtificialIntelligence getNormalAi(int colorIndex) {
        if (GameRules.slayRules) {
            return new AiNormalSlayRules(gameController, colorIndex);
        }

        return new AiNormalGenericRules(gameController, colorIndex);
    }


    private ArtificialIntelligence getEasyAi(int colorIndex) {
        return new AiEasy(gameController, colorIndex);
    }

}