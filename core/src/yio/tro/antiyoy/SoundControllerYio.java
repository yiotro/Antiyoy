package yio.tro.antiyoy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;


public class SoundControllerYio {

    public static Sound soundPressButton;
    public static Sound soundSelectUnit;
    public static Sound soundAttack;
    public static Sound soundCoin;
    public static Sound soundBuild;
    public static Sound soundWalk;
    public static Sound soundEndTurn;
    public static Sound soundHoldToMarch;


    public static void loadAllSounds() {
        soundPressButton = loadSound("menu_button");
        soundSelectUnit = loadSound("select_unit");
        soundAttack = loadSound("attack");
        soundCoin = loadSound("coin");
        soundBuild = loadSound("build");
        soundWalk = loadSound("walk");
        soundEndTurn = loadSound("end_turn");
        soundHoldToMarch = loadSound("hold_to_march");
    }


    private static Sound loadSound(String name) {
        return Gdx.audio.newSound(Gdx.files.internal("sound/" + name + ".ogg"));
    }


    public static void playSound(Sound sound) {
        if (!Settings.soundEnabled) return;

        sound.play();
    }
}
