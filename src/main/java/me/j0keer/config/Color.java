package me.j0keer.config;

import com.google.common.collect.ImmutableMap;
import me.j0keer.config.serialization.ConfigurationSerializable;
import me.j0keer.config.serialization.SerializableAs;

import java.util.Map;

@SerializableAs("Color")
public final class Color implements ConfigurationSerializable {
    private static final int BIT_MASK = 255;
    public static final Color WHITE = fromRGB(16777215);
    public static final Color SILVER = fromRGB(12632256);
    public static final Color GRAY = fromRGB(8421504);
    public static final Color BLACK = fromRGB(0);
    public static final Color RED = fromRGB(16711680);
    public static final Color MAROON = fromRGB(8388608);
    public static final Color YELLOW = fromRGB(16776960);
    public static final Color OLIVE = fromRGB(8421376);
    public static final Color LIME = fromRGB(65280);
    public static final Color GREEN = fromRGB(32768);
    public static final Color AQUA = fromRGB(65535);
    public static final Color TEAL = fromRGB(32896);
    public static final Color BLUE = fromRGB(255);
    public static final Color NAVY = fromRGB(128);
    public static final Color FUCHSIA = fromRGB(16711935);
    public static final Color PURPLE = fromRGB(8388736);
    public static final Color ORANGE = fromRGB(16753920);
    private final byte red;
    private final byte green;
    private final byte blue;

    public static Color fromRGB(int red, int green, int blue) throws IllegalArgumentException {
        return new Color(red, green, blue);
    }

    public static Color fromBGR(int blue, int green, int red) throws IllegalArgumentException {
        return new Color(red, green, blue);
    }

    public static Color fromRGB(int rgb) throws IllegalArgumentException {
        Validate.isTrue(rgb >> 24 == 0, "Extrenuous data in: ", rgb);
        return fromRGB(rgb >> 16 & 255, rgb >> 8 & 255, rgb >> 0 & 255);
    }

    public static Color fromBGR(int bgr) throws IllegalArgumentException {
        Validate.isTrue(bgr >> 24 == 0, "Extrenuous data in: ", bgr);
        return fromBGR(bgr >> 16 & 255, bgr >> 8 & 255, bgr >> 0 & 255);
    }

    private Color(int red, int green, int blue) {
        Validate.isTrue(red >= 0 && red <= 255, "Red is not between 0-255: ", red);
        Validate.isTrue(green >= 0 && green <= 255, "Green is not between 0-255: ", green);
        Validate.isTrue(blue >= 0 && blue <= 255, "Blue is not between 0-255: ", blue);
        this.red = (byte)red;
        this.green = (byte)green;
        this.blue = (byte)blue;
    }

    public int getRed() {
        return 255 & this.red;
    }

    public Color setRed(int red) {
        return fromRGB(red, this.getGreen(), this.getBlue());
    }

    public int getGreen() {
        return 255 & this.green;
    }

    public Color setGreen(int green) {
        return fromRGB(this.getRed(), green, this.getBlue());
    }

    public int getBlue() {
        return 255 & this.blue;
    }

    public Color setBlue(int blue) {
        return fromRGB(this.getRed(), this.getGreen(), blue);
    }

    public int asRGB() {
        return this.getRed() << 16 | this.getGreen() << 8 | this.getBlue() << 0;
    }

    public int asBGR() {
        return this.getBlue() << 16 | this.getGreen() << 8 | this.getRed() << 0;
    }

    public Color mixColors(Color... colors) {
        Validate.noNullElements(colors, "Colors cannot be null");
        int totalRed = this.getRed();
        int totalGreen = this.getGreen();
        int totalBlue = this.getBlue();
        int totalMax = Math.max(Math.max(totalRed, totalGreen), totalBlue);
        Color[] var6 = colors;
        int var7 = colors.length;

        for(int var8 = 0; var8 < var7; ++var8) {
            Color color = var6[var8];
            totalRed += color.getRed();
            totalGreen += color.getGreen();
            totalBlue += color.getBlue();
            totalMax += Math.max(Math.max(color.getRed(), color.getGreen()), color.getBlue());
        }

        float averageRed = (float)(totalRed / (colors.length + 1));
        float averageGreen = (float)(totalGreen / (colors.length + 1));
        float averageBlue = (float)(totalBlue / (colors.length + 1));
        float averageMax = (float)(totalMax / (colors.length + 1));
        float maximumOfAverages = Math.max(Math.max(averageRed, averageGreen), averageBlue);
        float gainFactor = averageMax / maximumOfAverages;
        return fromRGB((int)(averageRed * gainFactor), (int)(averageGreen * gainFactor), (int)(averageBlue * gainFactor));
    }

    public boolean equals(Object o) {
        if (!(o instanceof Color that)) {
            return false;
        } else {
            return this.blue == that.blue && this.green == that.green && this.red == that.red;
        }
    }

    public int hashCode() {
        return this.asRGB() ^ Color.class.hashCode();
    }

    public Map<String, Object> serialize() {
        return ImmutableMap.of("RED", this.getRed(), "BLUE", this.getBlue(), "GREEN", this.getGreen());
    }

    public static Color deserialize(Map<String, Object> map) {
        return fromRGB(asInt("RED", map), asInt("GREEN", map), asInt("BLUE", map));
    }

    private static int asInt(String string, Map<String, Object> map) {
        Object value = map.get(string);
        if (value == null) {
            throw new IllegalArgumentException(string + " not in map " + map);
        } else if (!(value instanceof Number)) {
            throw new IllegalArgumentException(string + '(' + value + ") is not a number");
        } else {
            return ((Number)value).intValue();
        }
    }

    public String toString() {
        return "Color:[rgb0x" + Integer.toHexString(this.getRed()).toUpperCase() + Integer.toHexString(this.getGreen()).toUpperCase() + Integer.toHexString(this.getBlue()).toUpperCase() + "]";
    }
}
