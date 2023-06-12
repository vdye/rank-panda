package org.bigredbands.mb.views;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.HashMap;

import org.bigredbands.mb.models.Field;
import org.bigredbands.mb.models.RankPosition;

public class PdfImage extends FieldView {

    private float scaleFactor;
    private Dimension containingDimension;
    private HashMap<String, RankPosition> rankPositions;

    public PdfImage(Field field, float scaleFactor, Dimension containingDimension, HashMap<String, RankPosition> rankPositions) {
        super(field);

        this.scaleFactor = scaleFactor;
        this.containingDimension = containingDimension;
        this.rankPositions = rankPositions;
    }

    // @Override
    // public Dimension getPreferredSize() {
    //     Dimension d = this.containingDimension;

    //     // Get the width-to-height ratio
    //     float parentAspectRatio = (float) d.width / d.height;
    //     float aspectRatio = (field.EndzoneWidth * 2 + field.Length) / (field.SidelineWidth * 2 + field.Height);
    //     if (parentAspectRatio > aspectRatio) {
    //         // Parent is wider than image
    //         return new Dimension((int) (d.height * aspectRatio), d.height);
    //     } else {
    //         return new Dimension (d.width, (int) (d.width / aspectRatio));
    //     }
    // }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D)g;

        drawField(g2d);

        drawFieldLines(g2d);
        drawHashes(g2d);

        drawRanks(g2d, rankPositions);
    }

    public float toPx(float measurementFt) {
        return this.scaleFactor * measurementFt;
    }

//    public HashMap<String, RankPosition> getRankPositions() {
//        return drillInfo.getMoves().get(currentMove).getEndPositions();
//    }
}
