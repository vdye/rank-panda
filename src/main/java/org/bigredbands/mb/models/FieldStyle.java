package org.bigredbands.mb.models;

import java.awt.Color;

public class FieldStyle {
    // All measurements are in *feet* to allow for uniform UI scaling
    public final float ArrowWidth;
    public final float RankEndDiameter;
    public final float RankLabelSize;
    public final float RankStrokeWidth;

    public final float FieldNumberSize;
    public final float HashWidth;
    public final float HashPeriod;
    public final float MajorIncrementWidth;
    public final float MinorIncrementWidth;
    public final float GridWidth;

    public final Color RankColor;
    public final Color RankLabelColor;

    private FieldStyle(Builder builder) {
        this.ArrowWidth = builder.ArrowWidth;
        this.RankEndDiameter = builder.RankEndDiameter;
        this.RankLabelSize = builder.RankLabelSize;
        this.RankStrokeWidth = builder.RankStrokeWidth;

        this.FieldNumberSize = builder.FieldNumberSize;
        this.HashWidth = builder.HashWidth;
        this.HashPeriod = builder.HashPeriod;
        this.MajorIncrementWidth = builder.MajorIncrementWidth;
        this.MinorIncrementWidth = builder.MinorIncrementWidth;
        this.GridWidth = builder.GridWidth;

        this.RankColor = builder.RankColor;
        this.RankLabelColor = builder.RankLabelColor;
    }

    public static class Builder {
        private float ArrowWidth;
        private float RankEndDiameter;
        private float RankLabelSize;
        private float RankStrokeWidth;

        private float FieldNumberSize;
        private float HashWidth;
        private float HashPeriod;
        private float MajorIncrementWidth;
        private float MinorIncrementWidth;
        private float GridWidth;

        private Color RankColor;
        private Color RankLabelColor;

        public Builder() {
            // Setup defaults
            ArrowWidth = 7.0f;
            RankEndDiameter = 5.0f;
            RankLabelSize = 10.0f;
            RankStrokeWidth = 2.5f;

            FieldNumberSize = 8.0f;
            HashWidth = 1.0f;
            HashPeriod = 5.2f;
            MajorIncrementWidth = 1.0f;
            MinorIncrementWidth = 0.5f;
            GridWidth = 0.3f;

            RankColor = Color.BLUE;
            RankLabelColor = Color.RED;
        }

        public Builder grid(float majorIncrementWidth, float minorIncrementWidth, float gridWidth) {
            this.MajorIncrementWidth = majorIncrementWidth;
            this.MinorIncrementWidth = minorIncrementWidth;
            this.GridWidth = gridWidth;
            return this;
        }

        public FieldStyle build() {
            return new FieldStyle(this);
        }
    }
}
