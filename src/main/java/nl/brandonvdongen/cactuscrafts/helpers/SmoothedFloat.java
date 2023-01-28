package nl.brandonvdongen.cactuscrafts.helpers;

public class SmoothedFloat {

    public SmoothedFloat(float initial, float smoothing){
        this.value = initial;
        this.lastValue = initial;
        this.target = initial;
        this.smoothing = smoothing;
    }
    float value;
    float lastValue;
    float target;
    float smoothing;

    float delta;

    public float getValue(){
        lastValue = value;
        value = math.lerp(lastValue, target, smoothing);
        return value;
    }
    public void setTarget(float target){
        this.target = target;
    }

    public void tick(){
        delta = value-lastValue;
        lastValue = value;
        value = math.lerp(lastValue, target, smoothing);
    }

    public float getDelta(){
        return delta;
    }
}
