package yio.tro.antiyoy.stuff;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import yio.tro.antiyoy.stuff.object_pool.ObjectPoolYio;
import yio.tro.antiyoy.stuff.object_pool.ReusableYio;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class VisualTextContainer implements ReusableYio{

    public RectangleYio position; // relative to parent position
    public ArrayList<RenderableTextYio> textList;
    public String key;
    ObjectPoolYio<RenderableTextYio> poolRenderableTexts;
    ArrayList<String> processedStringList;
    public ArrayList<RenderableTextYio> viewList;


    public VisualTextContainer() {
        position = new RectangleYio();
        textList = new ArrayList<>();
        processedStringList = new ArrayList<>();
        viewList = new ArrayList<>();
        initPools();
        reset();
    }


    @Override
    public void reset() {
        position.reset();
        clear();
        processedStringList.clear();
        key = null;
    }


    public void move(RectangleYio parentPosition) {
        moveViewList(parentPosition);
    }


    private void moveViewList(RectangleYio parentPosition) {
        for (int i = 0; i < viewList.size(); i++) {
            RenderableTextYio src = textList.get(i);
            RenderableTextYio renderableTextYio = viewList.get(i);
            renderableTextYio.setBy(src);
            renderableTextYio.position.x += parentPosition.x + position.x;
            renderableTextYio.position.y += parentPosition.y + position.y;
            renderableTextYio.updateBounds();
        }
    }


    private void initPools() {
        poolRenderableTexts = new ObjectPoolYio<RenderableTextYio>() {
            @Override
            public RenderableTextYio makeNewObject() {
                return new RenderableTextYio();
            }
        };
    }


    private void applyTextFit() {
        float maxAllowedWidth = (float) (position.width - 4 * GraphicsYio.borderThickness);
        for (int i = textList.size() - 1; i >= 0; i--) {
            RenderableTextYio renderableTextYio = textList.get(i);
            if (renderableTextYio.width < maxAllowedWidth) continue;
            splitTextItemIntoFewLines(renderableTextYio, i, maxAllowedWidth);
        }
    }


    private void splitTextItemIntoFewLines(RenderableTextYio renderableTextYio, int index, float maxAllowedWidth) {
        textList.remove(renderableTextYio);
        poolRenderableTexts.add(renderableTextYio);
        StringBuilder builder = new StringBuilder();
        String[] split = renderableTextYio.string.split(" ");
        double currentX = 0;
        BitmapFont font = renderableTextYio.font;
        for (String token : split) {
            float textWidth = GraphicsYio.getTextWidth(font, token + " ");
            if (currentX + textWidth > maxAllowedWidth) {
                addTextLineByIndex(font, builder.toString(), index);
                index++;
                builder.delete(0, builder.length());
                currentX = 0;
            }
            builder.append(token).append(" ");
            currentX += textWidth;
        }
        if (builder.length() == 0) return;
        addTextLineByIndex(font, builder.toString(), index);
    }


    private void addTextLineByIndex(BitmapFont font, String string, int index) {
        RenderableTextYio next = poolRenderableTexts.getNext();
        next.setFont(font);
        next.setString(string);
        next.updateMetrics();
        textList.add(index, next);
    }


    public void clear() {
        while (textList.size() > 0) {
            RenderableTextYio rt = textList.get(0);
            poolRenderableTexts.add(rt);
            textList.remove(rt);
        }
        syncViewListSize();
    }


    public void updateHeightToMatchText(double incDelta) {
        setSize(position.width, getMinAcceptableHeight() + incDelta);
        updateTextPosition();
    }


    private float getMinAcceptableHeight() {
        RenderableTextYio bottomText = null;
        RenderableTextYio topText = null;
        for (RenderableTextYio renderableTextYio : textList) {
            if (bottomText == null || renderableTextYio.position.y < bottomText.position.y) {
                bottomText = renderableTextYio;
            }
            if (topText == null || renderableTextYio.position.y > topText.position.y) {
                topText = renderableTextYio;
            }
        }
        if (bottomText == null) return 0;
        return topText.position.y - (bottomText.position.y - bottomText.height);
    }


    public void applySingleTextLine(BitmapFont font, String string) {
        clear();
        setKey(string);
        RenderableTextYio nextObject = poolRenderableTexts.getNext();
        textList.add(nextObject);
        nextObject.setFont(font);
        nextObject.setString(string);
        nextObject.updateMetrics();
        nextObject.setCentered(true);
        updateTextPosition();
        syncViewListSize();
    }


    public void applyManyTextLines(BitmapFont font, String source) {
        setKey(source);
        processedStringList.clear();
        StringTokenizer tokenizer = new StringTokenizer(source, "#");
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            processedStringList.add(token);
        }
        applyManyTextLines(font, processedStringList);
    }


    public void applyManyTextLines(BitmapFont font, ArrayList<String> strings) {
        clear();
        for (String string : strings) {
            RenderableTextYio nextObject = poolRenderableTexts.getNext();
            textList.add(nextObject);
            nextObject.setFont(font);
            nextObject.setString(string);
            nextObject.updateMetrics();
        }
        if (strings.size() == 1) {
            addSingleEmptyLine(font);
        }
        applyTextFit();
        updateTextPosition();
        syncViewListSize();
    }


    private void addSingleEmptyLine(BitmapFont font) {
        RenderableTextYio nextObject = poolRenderableTexts.getNext();
        textList.add(nextObject);
        nextObject.setFont(font);
        nextObject.setString(" ");
        nextObject.updateMetrics();
    }


    private void syncViewListSize() {
        while (viewList.size() < textList.size()) {
            RenderableTextYio next = poolRenderableTexts.getNext();
            viewList.add(next);
        }
        while (viewList.size() > textList.size()) {
            RenderableTextYio renderableTextYio = viewList.get(0);
            viewList.remove(renderableTextYio);
            poolRenderableTexts.add(renderableTextYio);
        }
    }


    public void suppressEmptyLinesInTheEnd() {
        for (int i = textList.size() - 1; i >= 0; i--) {
            String string = textList.get(i).string;
            if (!string.equals(" ")) break;
            textList.remove(i);
        }
        syncViewListSize();
    }


    public void updateTextPosition() {
        float y = (float) (position.height - 2 * GraphicsYio.borderThickness);

        for (RenderableTextYio renderableTextYio : textList) {
            renderableTextYio.position.y = y;
            y -= 2f * renderableTextYio.height;
            if (renderableTextYio.centered) {
                renderableTextYio.position.x = (float) (position.width / 2 - renderableTextYio.width / 2);
            } else {
                renderableTextYio.position.x = 2 * GraphicsYio.borderThickness;
            }
            renderableTextYio.updateBounds();
        }

        if (textList.size() == 1) {
            RenderableTextYio renderableTextYio = textList.get(0);
            renderableTextYio.position.y = (float) (position.height / 2 + renderableTextYio.height / 2);
            renderableTextYio.updateBounds();
        }
    }


    public void setSize(double w, double h) {
        position.width = (float) w;
        position.height = (float) h;
    }


    public void centerHorizontal(RectangleYio parentPosition) {
        position.x = parentPosition.width / 2 - position.width / 2;
    }


    public void centerVertical(RectangleYio parentPosition) {
        position.y = parentPosition.height / 2 - position.height / 2;
    }


    public void alignLeft(RectangleYio parentPosition, double offset) {
        position.x = (float) offset;
    }


    public void alignBottom(RectangleYio parentPosition, double offset) {
        position.y = (float) offset;
    }


    public void alignRight(RectangleYio parentPosition, double offset) {
        position.x = (float) (parentPosition.width - offset - position.width);
    }


    public void alignTop(RectangleYio parentPosition, double offset) {
        position.y = (float) (parentPosition.height - offset - position.height);
    }


    public void alignAbove(RectangleYio pos, double offset) {
        position.y = (float) (pos.y + pos.height + offset);
    }


    public void alignUnder(RectangleYio pos, double offset) {
        position.y = (float) (pos.y - offset - position.height);
    }


    public void setKey(String key) {
        this.key = key;
    }


    @Override
    public String toString() {
        return "[VisualTextContainer: [" +
                textList.size() + "] " +
                key +
                "]";
    }
}
