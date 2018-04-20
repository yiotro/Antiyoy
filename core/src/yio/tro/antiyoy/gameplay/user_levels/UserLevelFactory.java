package yio.tro.antiyoy.gameplay.user_levels;

import yio.tro.antiyoy.gameplay.user_levels.pack_one.*;

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
        // 1
        add(new UlevExample1());
        add(new UlevExample2());
        add(new UlevExample3());
        add(new UlevPuhtaytoe());
        add(new UlevHumeniuk());
        add(new UlevHansJurgen());
        add(new UlevVladSender1());
        add(new UlevVladSender2());
        add(new UlevLattice());
        add(new UlevSixFlags());
        add(new UlevTheRedstoneBlaze());
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
