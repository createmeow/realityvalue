package dev.ghen.thirst.foundation.gui.appleskin;

/* loaded from: ThirstWasTaken-1.20.1-1.4.0.jar:dev/ghen/thirst/foundation/gui/appleskin/ThirstValues.class */
public class ThirstValues {
    public final int thirst;
    public final float quenchedModifier;

    public ThirstValues(int thirst, float saturationModifier) {
        this.thirst = thirst;
        this.quenchedModifier = saturationModifier;
    }

    public float getQuenchedIncrement() {
        return this.thirst * this.quenchedModifier * 2.0f;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ThirstValues)) {
            return false;
        }
        ThirstValues that = (ThirstValues) o;
        return this.thirst == that.thirst && Float.compare(that.quenchedModifier, this.quenchedModifier) == 0;
    }

    public int hashCode() {
        int result = this.thirst;
        return (31 * result) + (this.quenchedModifier != 0.0f ? Float.floatToIntBits(this.quenchedModifier) : 0);
    }
}
