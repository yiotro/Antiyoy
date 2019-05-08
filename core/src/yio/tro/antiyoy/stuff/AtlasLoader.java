package yio.tro.antiyoy.stuff;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.antiyoy.gameplay.game_view.GameView;

import java.util.ArrayList;
import java.util.StringTokenizer;


public class AtlasLoader {

    private String srcName, txtFileName;
    private TextureRegion atlasRegion;
    private boolean antialias;
    private ArrayList<String> fileNames;
    private ArrayList<RectangleYio> imageSpecs;
    private int rows;


    public AtlasLoader(String srcName, String txtFileName, boolean antialias) {
        this.srcName = srcName;
        this.txtFileName = txtFileName;
        this.antialias = antialias;
        loadEverything();
    }


    private AtlasLoader() {
    }


    private void loadEverything() {
        atlasRegion = GraphicsYio.loadTextureRegion(srcName, antialias);
        FileHandle fileHandle = Gdx.files.internal(txtFileName);
        String atlasStructure = fileHandle.readString();
        ArrayList<String> lines = new ArrayList<String>();
        StringTokenizer stringTokenizer = new StringTokenizer(atlasStructure, "\n");
        while (stringTokenizer.hasMoreTokens()) {
            String token = stringTokenizer.nextToken();
            if (token.contains("rows=")) {
                String s = token.substring(5, token.length() - 1);
                rows = Integer.valueOf(s);
            }
            if (token.length() > 5 && !token.contains("compression=") && !token.contains("rows="))
                lines.add(token);
        }
        fileNames = new ArrayList<String>();
        imageSpecs = new ArrayList<>();
        for (String line : lines) {
            int charPos = line.indexOf("#");
            String fileName = line.substring(0, charPos);
            fileNames.add(fileName);
            String sizeString = line.substring(charPos + 1, line.length() - 1);
            int array[] = getArrayFromString(sizeString, 4);
            RectangleYio rect = new RectangleYio(array[0], array[1], array[2], array[3]);
            imageSpecs.add(rect);
        }
    }


    private int[] getArrayFromString(String str, int size) {
        StringTokenizer stringTokenizer = new StringTokenizer(str, " ");
        int array[] = new int[size];
        int i = 0;
        while (stringTokenizer.hasMoreTokens()) {
            String token = stringTokenizer.nextToken();
            array[i] = Integer.valueOf(token);
            i++;
        }
        return array;
    }


    public TextureRegion getTexture(String fileName) {
        int index = fileNames.indexOf(fileName);
        return new TextureRegion(
                atlasRegion,
                (int) imageSpecs.get(index).x,
                (int) imageSpecs.get(index).y,
                (int) imageSpecs.get(index).width,
                (int) imageSpecs.get(index).height
        );
    }


    public void disposeAtlasRegion() {
        atlasRegion.getTexture().dispose();
    }

}
