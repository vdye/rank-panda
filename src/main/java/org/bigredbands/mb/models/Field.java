package org.bigredbands.mb.models;

public final class Field {
    public final float Length;
    public final float Height;

    public final float Increment;
    public final int MajorIncrementFrequency;
    public final int MinorIncrementFrequency;

    public final float[] Hashes; // Measured in distance from front sideline

    public Field(float length, float height, float increment, int major, int minor, float[] hashes) {
        Length = length;
        Height = height;
        Increment = increment;
        MajorIncrementFrequency = major;
        MinorIncrementFrequency = minor;
        Hashes = hashes;
    }

    // Static instances
    public static Field CollegeFootball = new Field(
        100.0f * 3, // 100 yd
        160.0f, // 160 ft
        10.0f * 3 / 4.0f, // 4 increments between 10 yd lines
        4, // 10 yd increment
        2, // 5 yd increment
        new float[]{ 20.0f * 3, 20.0f * 3 + 40 } // Hashes are 40 ft apart, 20 yd from the sidelines
    );

    public static Field NFL = new Field(
        100.0f * 3, // 100 yd
        160.0f, // 160 ft
        10.0f * 3 / 4.0f, // 4 increments between 10 yd lines
        4, // 10 yd increment
        2, // 5 yd increment
        new float[]{ 70.75f, 70.75f + 18.5f } // Hashes are 18.5 ft apart, 70.75 ft from the sidelines
    );

    public static Field CFL = new Field(
        110.0f * 3, // 110 yd
        65.0f * 3, // 65 yd
        10.0f * 3 / 4.0f, // 4 increments between 10 yd lines
        4, // 10 yd increment
        2, // 5 yd increment
        new float[]{ 24.0f * 3, 24.0f * 3 + 51 } // Hashes are 51 ft apart, 24 yd from the sidelines
    );
}
