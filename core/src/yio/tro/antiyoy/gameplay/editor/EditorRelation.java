package yio.tro.antiyoy.gameplay.editor;

import yio.tro.antiyoy.gameplay.data_storage.EncodeableYio;

public class EditorRelation implements EncodeableYio{

    public int color1;
    public int color2;
    public int relation;


    @Override
    public String encode() {
        return color1 + " " + relation + " " + color2;
    }


    @Override
    public void decode(String source) {
        String[] split = source.split(" ");
        if (split.length < 3) return;
        color1 = Integer.valueOf(split[0]);
        relation = Integer.valueOf(split[1]);
        color2 = Integer.valueOf(split[2]);
    }
}
