package yio.tro.antiyoy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import yio.tro.antiyoy.menu.MenuControllerYio;

public class Fonts {

        public static final String SPECIAL_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789][_!$%#@|\\/?-+=()*&.;:,{}\"´`'<^>";
        public static BitmapFont buttonFont;
        public static BitmapFont gameFont;
        public static BitmapFont listFont;
        public static BitmapFont cityFont;
        public static int FONT_SIZE;


        public static void initFonts() {
            CustomLanguageLoader.loadLanguage();

            long time1 = System.currentTimeMillis();
            FileHandle fontFile = Gdx.files.internal("font.otf");
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(fontFile);
            FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
            FONT_SIZE = (int) (0.041 * Gdx.graphics.getHeight());

            parameter.size = FONT_SIZE;
            parameter.characters = getAllCharacters();
            parameter.flip = true;
            buttonFont = generator.generateFont(parameter);

            parameter.size = (int) (1.5f * FONT_SIZE);
            parameter.flip = true;
            listFont = generator.generateFont(parameter);
            listFont.setColor(Color.BLACK);

            parameter.size = FONT_SIZE;
            parameter.flip = false;
            gameFont = generator.generateFont(parameter);
            gameFont.setColor(Color.BLACK);

            parameter.size = (int)(0.5 * FONT_SIZE);
            parameter.flip = false;
            cityFont = generator.generateFont(parameter);
            cityFont.setColor(Color.WHITE);

            generator.dispose();

            YioGdxGame.say("time to generate fonts: " + (System.currentTimeMillis() - time1));
        }


        public static String getAllCharacters() {
            String langChars = MenuControllerYio.languagesManager.getString("lang_characters");
            return langChars + SPECIAL_CHARACTERS;
        }
}
