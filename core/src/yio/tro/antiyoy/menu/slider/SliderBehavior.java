package yio.tro.antiyoy.menu.slider;

public abstract class SliderBehavior {


    public abstract String getValueString(SliderYio sliderYio);


    public void onAnotherSliderValueChanged(SliderYio sliderYio, SliderYio anotherSlider) {

    }


    public void onValueChanged(SliderYio sliderYio) {

    }
}
