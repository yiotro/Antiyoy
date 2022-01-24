package yio.tro.antiyoy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;


public class SoundManagerYio {

    public static Sound soundPressButton;
    public static Sound soundSelectUnit;
    public static Sound soundAttack;
    public static Sound soundCoin;
    public static Sound soundBuild;
    public static Sound soundWalk;
    public static Sound soundEndTurn;
    public static Sound soundHoldToMarch;
    public static Sound soundKeyboardPress;


    public static void loadAllSounds() {
        soundPressButton = loadSound("menu_button");
        soundSelectUnit = loadSound("select_unit");
        soundAttack = loadSound("attack");
        soundCoin = loadSound("coin");
        soundBuild = loadSound("build");
        soundWalk = loadSound("walk");
        soundEndTurn = loadSound("end_turn");
        soundHoldToMarch = loadSound("hold_to_march");
        soundKeyboardPress = loadSound("kb_press");
    }


    private static Sound loadSound(String name) {
        return Gdx.audio.newSound(Gdx.files.internal("sound/" + name + getExtention()));
    }


    private static String getExtention() {
        if (YioGdxGame.platformType == PlatformType.ios) {
            return ".mp3";
        }

        return ".ogg";
    }


    public static void playSound(Sound sound) {
        if (!SettingsManager.soundEnabled) return;

        sound.play();
    }
}
