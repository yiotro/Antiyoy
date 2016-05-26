package yio.tro.antiyoy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

/**
 * Created by ivan on 14.04.2016.
 */
public class SoundControllerYio {

    private static Sound soundPressButton;


    public static void loadSounds() {
        soundPressButton = Gdx.audio.newSound(Gdx.files.internal("sound/menu_button.ogg"));
    }


    private static void playSound(Sound sound) {
        if (!YioGdxGame.SOUND) return;

        sound.play();
    }


    public static void playPressButton() {
        playSound(soundPressButton);
    }
}
