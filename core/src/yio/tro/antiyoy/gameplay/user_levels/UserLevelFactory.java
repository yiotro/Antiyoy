package yio.tro.antiyoy.gameplay.user_levels;

import yio.tro.antiyoy.gameplay.user_levels.pack_five.*;
import yio.tro.antiyoy.gameplay.user_levels.pack_four.*;
import yio.tro.antiyoy.gameplay.user_levels.pack_one.*;
import yio.tro.antiyoy.gameplay.user_levels.pack_three.*;
import yio.tro.antiyoy.gameplay.user_levels.pack_two.*;

import java.util.ArrayList;

public class UserLevelFactory {


    private static UserLevelFactory instance;
    ArrayList<AbstractUserLevel> levels;


    public static void initialize() {
        instance = null;
    }


    public static UserLevelFactory getInstance() {
        if (instance == null) {
            instance = new UserLevelFactory();
        }

        return instance;
    }


    public UserLevelFactory() {
        levels = new ArrayList<>();
        initLevels();
    }


    private void initLevels() {
        // 5
        add(new UlevSeventhTimesTheCharm());
        add(new UlevHexahedron());
        add(new UlevTripleDuo());
        add(new UlevForestSpirit());
        add(new UlevHoot());
        add(new UlevSnowflake());
        add(new UlevEyeOfTheStorm());
        add(new UlevLure());
        add(new UlevCarrotAndBunny());
        add(new UlevHistoryFight());
        add(new UlevNowhereToHide());
        add(new UlevAntFarm());
        add(new UlevGreatForest());
        add(new UlevCastleIsles());
        add(new UlevLittleKingdom());
        add(new UlevDichArik());
        add(new UlevBoneBreaker());
        add(new UlevDoubleThreat());
        add(new UlevCatan());
        add(new UlevTahaFidan());
        add(new UlevDna());
        add(new UlevJustinSullivan());
        add(new UlevHoneyComb());
        add(new UlevWagonWheel());
        add(new UlevNinoBambino());
        add(new UlevDieBrucke());
        add(new UlevFlowerpot());
        add(new UlevTiny());
        add(new UlevComingForYou());
        add(new UlevPolishVoivodeships());
        add(new Ulev900to1200());
        add(new UlevJustinSullivanIII());
        add(new UlevSecurityLevels());
        add(new UlevFairOrSquare());
        add(new UlevDoubleTrouble());
        add(new UlevOttomanEmpire());
        add(new UlevForbiddenForest());
        add(new UlevAmplob());
        add(new UlevRainbow());
        add(new UlevEpicenter());
        add(new UlevNetherlands());
        add(new UlevGreatWar());
        add(new UlevThorSnowflake());
        add(new UlevVikingInvasion());
        add(new UlevBattleForGermanEmpire());
        add(new UlevSurrounded());
        add(new UlevSurroundMap());
        add(new UlevStopTheRevolution());
        add(new UlevForest());
        add(new UlevPrisonBreak());
        add(new UlevNinoAlo());
        add(new UlevBreakUpOfYugoslavia());
        add(new UlevTurkey1919());
        add(new UlevStar());
        add(new UlevALotToKeepTrackOf());
        add(new Ulev1940());
        add(new UlevJustinSullivanII());
        add(new UlevPeninsularWar());
        add(new UlevWarOfLove());
        add(new UlevKrzycho());
        add(new UlevGoBlue());
        add(new UlevEuropeInMonkey());
        add(new UlevPipeNet());
        add(new UlevIslandByIsland());
        add(new UlevRegionsRussianFederation());
        add(new UlevFetih1453());

        // default examples
        add(new UlevExample1());
        add(new UlevExample2());
        add(new UlevExample3());

        // 4
        add(new UlevTheWeb());
        add(new UlevKingOfTheIsland());
        add(new UlevZeraXenonArena());
        add(new UlevBlackForest());
        add(new UlevLeBlanc());
        add(new UlevAlmostFair());
        add(new UlevPurpleWiggles());
        add(new UlevTwoForOne());
        add(new UlevTheophileDugue());
        add(new UlevMinasTirith());
        add(new UlevGreatColonization());
        add(new UlevAfrica1936());
        add(new UlevIslandOfDeath());
        add(new UlevWarOnIsland());
        add(new UlevWoman());
        add(new UlevAtlantidaWar());

        // 3
        add(new UlevSpiderTrap());
        add(new UlevManInTheMiddle());
        add(new UlevSixKingdoms());
        add(new UlevInsideOut());
        add(new UlevTarget());
        add(new UlevSouthAmerica());
        add(new UlevBackdoor());
        add(new UlevStroke());
        add(new UlevConquestOfBritain());
        add(new UlevAbandonedValley());
        add(new UlevScaryWWII());

        // 2
        add(new UlevSpiral());
        add(new UlevAssymetry());
        add(new UlevFennia());
        add(new UlevButterfly());
        add(new UlevSevenEmpires());
        add(new UlevSorvixChallengeArena());
        add(new UlevHalma());
        add(new UlevMeetInTheMiddle());
        add(new UlevAntsNest());
        add(new UlevTwoLands());
        add(new UlevWarOnTheRiver());
        add(new UlevWarOfStrategy());
        add(new UlevWarToEndAllWars());
        add(new UlevOneOnOne());
        add(new UlevBridgeFort());
        add(new UlevReikEpsilon());
        add(new UlevWesternBalkan());
        add(new UlevJapanConquest());
        add(new UlevKWar());

        // 1
        add(new UlevPuhtaytoe());
        add(new UlevHumeniuk());
        add(new UlevHansJurgen());
        add(new UlevVladSender1());
        add(new UlevVladSender2());
        add(new UlevLattice());
        add(new UlevSixFlags());
        add(new UlevCoastalCrescent());
        add(new UlevSteveUpsideDown());
        add(new UlevOlegDonskih1());
        add(new UlevOlegDonskih2());
        add(new UlevMirage212());
        add(new UlevConquestOfItalicPeninsula());
    }


    private void add(AbstractUserLevel level) {
        levels.add(level);
    }


    public ArrayList<AbstractUserLevel> getLevels() {
        return levels;
    }


    public AbstractUserLevel getLevel(String key) {
        for (AbstractUserLevel level : levels) {
            if (level.getKey().equals(key)) {
                return level;
            }
        }

        return null;
    }
}
