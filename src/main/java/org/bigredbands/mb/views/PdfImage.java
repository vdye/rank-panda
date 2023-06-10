package org.bigredbands.mb.views;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Shape;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.JPanel;

import org.bigredbands.mb.models.RankPosition;

public class PdfImage extends JPanel {

    private float scaleFactor;
    private Dimension containingDimension;
    private MainView mainView;
    private HashMap<String, RankPosition> rankPositions;

    public PdfImage(float scaleFactor, Dimension containingDimension, HashMap<String, RankPosition> rankPositions) {
        super();

        this.scaleFactor = scaleFactor;
        this.containingDimension = containingDimension;
        this.rankPositions = rankPositions;

    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, (int) (FootballFieldView.FIELD_LENGTH * scaleFactor)+100, (int) (FootballFieldView.FIELD_HEIGHT * scaleFactor));

        g.setColor(Color.BLACK);
        FootballFieldView.drawFieldLines(g, containingDimension, 0, 0, 0);
        FootballFieldView.drawHashes(g, (int) (FootballFieldView.END_ZONE_LENGTH * scaleFactor), 0, scaleFactor);

        FootballFieldView.drawRanks(
                FootballFieldView.createShapes(rankPositions, (int) (FootballFieldView.END_ZONE_LENGTH * scaleFactor), 0, scaleFactor),
                new HashMap<String, Shape>(),
                new HashSet<String>(),
                g,
                (int) (FootballFieldView.END_ZONE_LENGTH * scaleFactor),
                0,
                scaleFactor);
    }

    public float getScaleFactor(){
        return this.scaleFactor;
    }

//    public HashMap<String, RankPosition> getRankPositions() {
//        return drillInfo.getMoves().get(currentMove).getEndPositions();
//    }
}
