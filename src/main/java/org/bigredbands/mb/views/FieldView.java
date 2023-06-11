package org.bigredbands.mb.views;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.HashMap;

import javax.swing.JPanel;

import org.bigredbands.mb.models.Field;
import org.bigredbands.mb.models.RankPosition;

/**
 * This class represents the generic "field view" used in rendering the editor,
 * move thumbnails, and exported PDF.
 */
public abstract class FieldView extends JPanel {

    // All measurements are in *feet* to allow for uniform UI scaling
    public static class FieldStyle {

        public static final float ArrowWidth = 2.0f;
        public static final float RankLabelSize = 8.0f;

        public static final float FieldNumberSize = 8.0f;
        public static final float HashWidth = 1.0f;
        public static final float HashPeriod = 5.2f;
        public static final float MajorIncrementWidth = 1.0f;
        public static final float MinorIncrementWidth = 0.5f;
        public static final float GridWidth = 0.3f;
    }

    private final Field field;

    protected FieldView(Field field) {
        this.field = field;
    }

    public void drawField(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, (int) toPx(field.Length + 2*field.EndzoneWidth), (int) toPx(field.Height + 2*field.SidelineWidth));
    }

    /**
     * Draw the hashmarks on the football field
     * @param g - the graphics used to draw
     * @param topLeftX - top left x coordinate of the football field
     * @param topLeftY - top left y coordinate of the football field
     */
    public void drawHashes(Graphics2D g) {
        BasicStroke dashed = new BasicStroke(
            toPx(FieldStyle.HashWidth),
            BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_MITER,
            10.0f,
            new float[]{ toPx(FieldStyle.HashPeriod) },
            0.0f);

        g.setStroke(dashed);
        g.setColor(Color.black);

        for (float hash : field.Hashes) {
            g.drawLine(
                (int) toPx(field.EndzoneWidth),
                (int) toPx(field.TotalHeight - field.SidelineWidth - hash),
                (int) toPx(field.EndzoneWidth + field.Length),
                (int) toPx(field.TotalHeight - field.SidelineWidth - hash)
            );
        }
    }

    /**
     * Helper function used to draw the numbers on the football field
     * @param g - the graphics used to draw
     */
    private void drawNumbers(Graphics2D g) {
        int increments = (int) (field.Length / field.Increment);

        g.setColor(Color.black);
        g.setFont(new Font(Font.SANS_SERIF, 0, (int) toPx(FieldStyle.FieldNumberSize)));
        FontMetrics metrics = g.getFontMetrics();

        int markedIncrements = increments / field.MajorIncrementFrequency;
        for (int i = 1; i < markedIncrements; i++) {
            String numberString;
            if (i <= markedIncrements / 2) {
                numberString = i + " 0"; // Prints "1 0" (for example)
            } else {
                numberString = (markedIncrements - i) + " 0";
            }
            int pxStringWidth = metrics.stringWidth(numberString);

            // Draw numbers at top of page
            g.drawString(numberString,
                toPx(field.EndzoneWidth + i * field.MajorIncrementFrequency * field.Increment) - pxStringWidth / 2,
                toPx(field.SidelineWidth + 20.0f));

            // Draw numbers at bottom of page
            g.drawString(numberString,
                toPx(field.EndzoneWidth + i * field.MajorIncrementFrequency * field.Increment) - pxStringWidth / 2,
                toPx(field.TotalHeight - field.SidelineWidth - 20.0f));
        }
    }

    /**
     * This is a helper function designed to draw lines on the field
     */
    public void drawFieldLines(Graphics2D g) {
        g.setColor(Color.black);

        drawNumbers(g);

        // Note: image origin (0,0) is in upper left

        // Divide the field horizontally into increments & draw each one
        int increments = (int) (field.Length / field.Increment);
        for (int i = 0; i < increments + 1; i++) {
            float strokeWidth = FieldStyle.GridWidth;
            if (i % field.MajorIncrementFrequency == 0) {
                strokeWidth = toPx(FieldStyle.MajorIncrementWidth);
            } else if (i % field.MinorIncrementFrequency == 0) {
                strokeWidth = toPx(FieldStyle.MinorIncrementWidth);
            }
            g.setStroke(new BasicStroke(strokeWidth));

            g.drawLine(
                (int) toPx(field.EndzoneWidth + field.Increment * i),
                (int) toPx(field.SidelineWidth),
                (int) toPx(field.EndzoneWidth + field.Increment * i),
                (int) toPx(field.SidelineWidth + field.Height)
            );

        }

        increments = (int) (field.Height / field.Increment);
        for (int i = 0; i < increments + 1; i++) {
            float strokeWidth = FieldStyle.GridWidth;

            // Unlike horizontal increments, do not include major increment style
            if (i % field.MinorIncrementFrequency == 0) {
                strokeWidth = toPx(FieldStyle.MinorIncrementWidth);
            }
            g.setStroke(new BasicStroke(strokeWidth));

            g.drawLine(
                (int) toPx(field.EndzoneWidth),
                (int) toPx(field.TotalHeight - field.SidelineWidth - field.Increment * i),
                (int) toPx(field.EndzoneWidth + field.Length),
                (int) toPx(field.TotalHeight - field.SidelineWidth - field.Increment * i)
            );

        }

        // Reset stroke
        g.setStroke(new BasicStroke());
    }

     /**
      * This function draws the shapes to the screen
      */
    public void drawRanks(Graphics2D g, HashMap<String, RankPosition> ranks) {
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, (int) toPx(FieldStyle.RankLabelSize)));
        for (String rankName : ranks.keySet()) {
            // Draw the rank arrow
            // Draw the line

            // drawArrow(g,
            //     rankName,
            //     selectedRanks,
            //     shapeMap.get(rankName));

            // Draw the text
        }
    }

    /**
     * Applies the feet-to-pixels scale factor for the given field
     */
    public abstract float toPx(float measurementFt);
}
