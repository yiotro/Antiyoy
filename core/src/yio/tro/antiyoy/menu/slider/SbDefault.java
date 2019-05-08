package yio.tro.antiyoy.menu.slider;

public class SbDefault extends SliderBehavior{

    @Override
    public String getValueString(SliderYio sliderYio) {
        return "" + sliderYio.getValueIndex();
    }

}
