package yio.tro.antiyoy.stuff;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import yio.tro.antiyoy.CustomLanguageLoader;
import yio.tro.antiyoy.YioGdxGame;

public class Fonts {

    public static final String SPECIAL_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789][_!$%#@|\\/?-+=()*&.;:,{}\"´`'<^>";
    public static BitmapFont buttonFont;
    public static BitmapFont gameFont;
    public static BitmapFont microFont;
    public static BitmapFont smallerMenuFont;
    public static int FONT_SIZE;


    public static void initFonts() {
        CustomLanguageLoader.loadLanguage();

        long time1 = System.currentTimeMillis();
        FileHandle fontFile = loadFontFile();
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(fontFile);
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        FONT_SIZE = (int) (0.041 * Gdx.graphics.getHeight());

        parameter.size = (int) (0.85f * FONT_SIZE);
        parameter.characters = getAllCharacters();
        parameter.flip = true;
        buttonFont = generator.generateFont(parameter);

        parameter.size = FONT_SIZE;
        parameter.flip = false;
        gameFont = generator.generateFont(parameter);
        gameFont.setColor(Color.WHITE);

        parameter.size = (int) (0.5 * FONT_SIZE);
        parameter.flip = false;
        microFont = generator.generateFont(parameter);
        microFont.setColor(Color.WHITE);

        parameter.size = (int) (0.8 * FONT_SIZE);
        parameter.flip = false;
        smallerMenuFont = generator.generateFont(parameter);
        smallerMenuFont.setColor(Color.BLACK);

        generator.dispose();

        initFontChinese();

        YioGdxGame.say("time to generate fonts: " + (System.currentTimeMillis() - time1));
    }


    private static FileHandle loadFontFile() {
        if (LanguagesManager.getInstance().getLanguage().equals("el_GR")) {
            System.out.println("Greek font loaded");
            return Gdx.files.internal("font_greek.ttf");
        }

        return Gdx.files.internal("font.otf");
    }


    public static String getAllCharacters() {
        String langChars = LanguagesManager.getInstance().getString("lang_characters");
        return langChars + SPECIAL_CHARACTERS;
    }


    private static void initFontChinese() {
        if (!LanguagesManager.getInstance().getLanguage().equals("cn_CN")) return;

        FileHandle fontFile1 = Gdx.files.internal("chinese/Chinese.fnt");
        FileHandle fontFile2 = Gdx.files.internal("chinese/Chinese.png");
        buttonFont = new BitmapFont(fontFile1, fontFile2, true);
        gameFont = new BitmapFont(fontFile1, fontFile2, false);
        microFont = new BitmapFont(fontFile1, fontFile2, false);
        microFont.getData().setScale(0.5f);

        float resFactor = (float) Gdx.graphics.getWidth() / 450f;
        buttonFont.getData().setScale(resFactor);
        gameFont.getData().setScale(resFactor);
        microFont.getData().setScale(resFactor);
    }
}
