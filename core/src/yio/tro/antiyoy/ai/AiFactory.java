package yio.tro.antiyoy.ai;

import yio.tro.antiyoy.ai.master.AiMaster;
import yio.tro.antiyoy.gameplay.GameController;
import yio.tro.antiyoy.gameplay.rules.GameRules;

import java.util.ArrayList;

public class AiFactory {

    private final GameController gameController;
    ArrayList<AbstractAi> aiList;


    public AiFactory(GameController gameController) {
        this.gameController = gameController;
    }


    public void createAiList(int difficulty) {
        aiList = gameController.getAiList();
        aiList.clear();

        for (int i = 0; i < GameRules.fractionsQuantity; i++) {
            addAiToList(difficulty, i);
        }
    }


    public void createCustomAiList(int difficulties[]) {
        if (GameRules.fractionsQuantity != difficulties.length) {
            System.out.println("AiFactory.createCustomAiList(): problem");
        }

        aiList = gameController.getAiList();
        aiList.clear();

        for (int i = 0; i < GameRules.fractionsQuantity; i++) {
            addAiToList(difficulties[i], i);
        }
    }


    private void addAiToList(int difficulty, int fraction) {
        switch (difficulty) {
            default:
            case Difficulty.EASY:
                aiList.add(getEasyAi(fraction));
                break;
            case Difficulty.NORMAL:
                aiList.add(getNormalAi(fraction));
                break;
            case Difficulty.HARD:
                aiList.add(getHardAi(fraction));
                break;
            case Difficulty.EXPERT:
                aiList.add(getExpertAi(fraction));
                break;
            case Difficulty.BALANCER:
                aiList.add(getBalancerAi(fraction));
                break;
            case Difficulty.MASTER:
                aiList.add(getMasterAi(fraction));
                break;
        }
    }


    private AbstractAi getMasterAi(int fraction) {
        if (GameRules.slayRules) {
            return new AiExpertSlayRules(gameController, fraction);
        }

        return new AiMaster(gameController, fraction);
    }


    private ArtificialIntelligence getBalancerAi(int fraction) {
        if (GameRules.slayRules) {
            return new AiBalancerSlayRules(gameController, fraction);
        }

        return new AiBalancerGenericRules(gameController, fraction);
    }


    private ArtificialIntelligence getExpertAi(int fraction) {
        if (GameRules.slayRules) {
            return new AiExpertSlayRules(gameController, fraction);
        }

        return new AiExpertGenericRules(gameController, fraction);
    }


    private ArtificialIntelligence getHardAi(int fraction) {
        if (GameRules.slayRules) {
            return new AiHardSlayRules(gameController, fraction);
        }

        return new AiHardGenericRules(gameController, fraction);
    }


    private ArtificialIntelligence getNormalAi(int fraction) {
        if (GameRules.slayRules) {
            return new AiNormalSlayRules(gameController, fraction);
        }

        return new AiNormalGenericRules(gameController, fraction);
    }


    private ArtificialIntelligence getEasyAi(int fraction) {
        return new AiEasy(gameController, fraction);
    }

}