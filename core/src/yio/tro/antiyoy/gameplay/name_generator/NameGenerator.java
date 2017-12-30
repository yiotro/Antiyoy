package yio.tro.antiyoy.gameplay.name_generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.StringTokenizer;

public class NameGenerator {

    HashMap<String, String> groups;
    ArrayList<String> masks;
    Random defRandom, random;
    boolean capitalize;


    public NameGenerator() {
        groups = new HashMap<>();
        masks = new ArrayList<>();
        defRandom = new Random();
        capitalize = true;
    }


    public String generateName() {
        return generateName(defRandom);
    }


    public String generateName(Random random) {
        this.random = random;
        StringBuffer stringBuffer = new StringBuffer();

        String mask = getRandomMask();
        for (int i = 0; i < mask.length(); i++) {
            String key = "" + mask.charAt(i);
            String group = getGroup(key);
            if (group == null) {
                stringBuffer.append(key);
                continue;
            }
            stringBuffer.append(getRandomSymbol(group));
        }

        if (capitalize) {
            capitalizeFirstLetter(stringBuffer);
        }

        return stringBuffer.toString();
    }


    private String getRandomSymbol(String src) {
        StringTokenizer tokenizer = new StringTokenizer(src, " ");
        ArrayList<String> tokens = new ArrayList<String>();
        while (tokenizer.hasMoreTokens()) {
            tokens.add(tokenizer.nextToken());
        }
        int index = random.nextInt(tokens.size());
        return tokens.get(index);
    }


    private String getGroup(String key) {
        return groups.get(key);
    }


    private String getRandomMask() {
        int index = random.nextInt(masks.size());
        return masks.get(index);
    }


    private void capitalizeFirstLetter(StringBuffer buffer) {
        buffer.setCharAt(0, Character.toUpperCase(buffer.charAt(0)));
    }


    public void setCapitalize(boolean capitalize) {
        this.capitalize = capitalize;
    }


    public void addGroup(String key, String symbols) {
        groups.put(key, symbols);
    }


    public void addMask(String mask) {
        masks.add(mask);
    }


    public void clearMasks() {
        masks.clear();
    }


    public void clearGroups() {
        groups.clear();
    }


    public void setMasks(ArrayList<String> masks) {
        this.masks = masks;
    }


    public void setGroups(HashMap<String, String> groups) {
        this.groups = groups;
    }
}
