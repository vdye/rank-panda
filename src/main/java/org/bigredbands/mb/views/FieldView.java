package org.bigredbands.mb.views;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;

import javax.swing.JPanel;

import org.bigredbands.mb.models.Field;
import org.bigredbands.mb.models.FieldStyle;
import org.bigredbands.mb.models.Point;
import org.bigredbands.mb.models.RankPosition;

/**
 * This class represents the generic "field view" used in rendering the editor,
 * move thumbnails, and exported PDF.
 */
public abstract class FieldView extends JPanel {

    protected final Field field;
    protected final FieldStyle fieldStyle;
    protected HashMap<String, RankPosition> rankPositions;

    private float scaleFactor;
    private int pxOffsetX;
    private int pxOffsetY;

    protected FieldView(Field field, FieldStyle style,
            HashMap<String, RankPosition> rankPositions,
            Dimension container) {

        // Set the field information
        this.field = field;
        this.fieldStyle = style;
        this.rankPositions = rankPositions;

        // Set the scaling information
        float containerAspectRatio = (float) (container.getWidth() / container.getHeight());
        if (containerAspectRatio > field.AspectRatio) {
            // Container is wider than image - scale to height and center
            this.scaleFactor = (float) container.getHeight() / field.TotalHeight;
            this.pxOffsetX = (int) (container.getWidth() - this.scaleFactor * field.TotalLength);
        } else {
            // Container is taller than image - scale to width and center
            this.scaleFactor = (float) container.getWidth() / field.TotalLength;
            this.pxOffsetY = (int) (container.getHeight() - this.scaleFactor * field.TotalHeight);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D)g;
        AffineTransform original = g2d.getTransform();

        // Convert to new coordinate system
        g2d.scale(this.scaleFactor, this.scaleFactor);
        g2d.translate(this.pxOffsetX, this.pxOffsetY);

        drawField(g2d);

        drawFieldLines(g2d);
        drawHashes(g2d);

        if (rankPositions != null) {
            drawRanks(g2d, rankPositions);
        }

        g2d.setTransform(original);
    }

    public void drawField(Graphics2D g) {
        // Paint the canvas background
        g.setColor(Color.WHITE);
        g.fill(g.getClipBounds());

        Rectangle2D fieldRect = new Rectangle2D.Float(field.EndzoneWidth, field.SidelineWidth,
            field.Length, field.Height);
        g.setColor(Color.WHITE);
        g.fill(fieldRect);

        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(fieldStyle.MajorIncrementWidth,
            BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_MITER));
        g.draw(fieldRect);
    }

    /**
     * Draw the hashmarks on the football field
     * @param g - the graphics used to draw
     * @param topLeftX - top left x coordinate of the football field
     * @param topLeftY - top left y coordinate of the football field
     */
    public void drawHashes(Graphics2D g) {
        BasicStroke dashed = new BasicStroke(
            fieldStyle.HashWidth,
            BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_MITER,
            10.0f,
            new float[]{ fieldStyle.HashPeriod },
            0.0f);

        g.setStroke(dashed);
        g.setColor(Color.black);

        for (float hash : field.Hashes) {
            g.drawLine(
                (int) field.EndzoneWidth,
                (int) (field.TotalHeight - field.SidelineWidth - hash),
                (int) (field.EndzoneWidth + field.Length),
                (int) (field.TotalHeight - field.SidelineWidth - hash)
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
        g.setFont(new Font(Font.SANS_SERIF, 0, (int) fieldStyle.FieldNumberSize));
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
                field.EndzoneWidth + i * field.MajorIncrementFrequency * field.Increment - pxStringWidth / 2,
                field.SidelineWidth + 20.0f);

            // Draw numbers at bottom of page
            g.drawString(numberString,
                field.EndzoneWidth + i * field.MajorIncrementFrequency * field.Increment - pxStringWidth / 2,
                field.TotalHeight - field.SidelineWidth - 20.0f);
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
            float strokeWidth = fieldStyle.GridWidth;
            if (i % field.MajorIncrementFrequency == 0) {
                strokeWidth = fieldStyle.MajorIncrementWidth;
            } else if (i % field.MinorIncrementFrequency == 0) {
                strokeWidth = fieldStyle.MinorIncrementWidth;
            }
            g.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));

            g.drawLine(
                (int) (field.EndzoneWidth + field.Increment * i),
                (int) (field.SidelineWidth),
                (int) (field.EndzoneWidth + field.Increment * i),
                (int) (field.SidelineWidth + field.Height)
            );

        }

        increments = (int) (field.Height / field.Increment);
        for (int i = 0; i < increments + 1; i++) {
            float strokeWidth = fieldStyle.GridWidth;

            // Unlike horizontal increments, do not include major increment style
            if (i % field.MinorIncrementFrequency == 0) {
                strokeWidth = fieldStyle.MinorIncrementWidth;
            }
            g.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));

            g.drawLine(
                (int) (field.EndzoneWidth),
                (int) (field.TotalHeight - field.SidelineWidth - field.Increment * i),
                (int) (field.EndzoneWidth + field.Length),
                (int) (field.TotalHeight - field.SidelineWidth - field.Increment * i)
            );

        }

        // Reset stroke
        g.setStroke(new BasicStroke());
    }

    private void drawArrowhead(Graphics2D g, Point startPx, Point direction) {
        Path2D arrowhead = new Path2D.Float();
        Point dirNorm = direction.normalize();
        float arrowHeightPx = (float) Math.sin(Math.toRadians(60)) * fieldStyle.ArrowWidth;
        float arrowBasePx = fieldStyle.ArrowWidth;

        // The arrowhead point
        Point vertex = startPx.add(dirNorm.multiply(arrowHeightPx / 2.0f));
        arrowhead.moveTo(vertex.X(), vertex.Y());

        // Base of the arrowhead
        Point orthoNorm = dirNorm.orthogonal();
        vertex = startPx.subtract(dirNorm.multiply(arrowHeightPx / 2.0f)
            .add(orthoNorm.multiply(arrowBasePx / 2.0f)));
            arrowhead.lineTo(vertex.X(), vertex.Y());

        vertex = startPx.subtract(dirNorm.multiply(arrowHeightPx / 2.0f)
            .subtract(orthoNorm.multiply(arrowBasePx / 2.0f)));
        arrowhead.lineTo(vertex.X(), vertex.Y());

        arrowhead.closePath();
        g.fill(arrowhead);
    }

    public void drawRank(Graphics2D g, String rankName, RankPosition rank) {
        Point fieldOffset = new Point(field.EndzoneWidth, field.SidelineWidth);

        // Draw the rank arrow
        // Draw the line
        g.setStroke(new BasicStroke(fieldStyle.RankStrokeWidth,
            BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
        g.setColor(fieldStyle.RankColor);

        // TODO: convert rank scale to feet (avoid the multiply())
        Point start = rank.getFront().multiply(3.0f).add(fieldOffset);
        Point midpoint = rank.getMidpoint().multiply(3.0f).add(fieldOffset);
        Point end = rank.getEnd().multiply(3.0f).add(fieldOffset);

        Point arrowDir;
        switch (rank.getLineType()) {
            case RankPosition.LINE:
                g.draw(new Line2D.Float(start, end));
                arrowDir = start.subtract(end);
                break;
            case RankPosition.CURVE:
                // TODO: convert rank scale to feet (avoid the multiply())
                Point control = rank.getCurveControlPoint().multiply(3.0f).add(fieldOffset);
                g.draw(new QuadCurve2D.Float(start.X(), start.Y(),
                    control.X(), control.Y(),
                    end.X(), end.Y()));
                arrowDir = start.subtract(control);
                break;
            case RankPosition.CORNER:
                Path2D.Float rankLine = new Path2D.Float();
                rankLine.moveTo(start.X(), start.Y());
                rankLine.lineTo(midpoint.X(), midpoint.Y());
                rankLine.lineTo(end.X(), end.Y());
                g.draw(rankLine);
                arrowDir = start.subtract(midpoint);
                break;
            default:
                System.out.println("TRIED CREATE SOMETHING THAT WASN'T A LINE, CURVE, OR CORNER");
                return;
        }

        // Draw endpoints
        g.fill(new Arc2D.Float((end.X() - 0.5f * fieldStyle.RankEndDiameter),
            (end.Y() - 0.5f * fieldStyle.RankEndDiameter),
            fieldStyle.RankEndDiameter,
            fieldStyle.RankEndDiameter,
            0.0f, 360.0f,
            Arc2D.CHORD));
        drawArrowhead(g, start, arrowDir);

        // Draw the rank label
        Font rankFont = new Font(Font.SANS_SERIF, Font.BOLD, (int) fieldStyle.RankLabelSize);
        GlyphVector rankLabel = rankFont.createGlyphVector(g.getFontRenderContext(), rankName);
        Shape rankShape = rankLabel.getOutline(midpoint.X(), midpoint.Y());

        g.setStroke(new BasicStroke(0.1f * fieldStyle.RankLabelSize));
        g.setColor(fieldStyle.RankLabelBackground);
        g.draw(rankShape);
        g.setColor(fieldStyle.RankLabelColor);
        g.fill(rankShape);
    }

     /**
      * This function draws the ranks (line, curve, and corner) to the canvas.
      */
    public void drawRanks(Graphics2D g, HashMap<String, RankPosition> ranks) {
        for (String rankName : ranks.keySet()) {
            drawRank(g, rankName, ranks.get(rankName));
        }
    }
}
