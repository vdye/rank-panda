package org.bigredbands.mb.views;

import java.awt.Dimension;
import java.util.HashMap;

import org.bigredbands.mb.models.Field;
import org.bigredbands.mb.models.FieldStyle;
import org.bigredbands.mb.models.RankPosition;

public class PdfImage extends FieldView {

    // Use default FieldStyle
    private static final FieldStyle fieldStyle = new FieldStyle.Builder().build();

    public PdfImage(Field field, HashMap<String, RankPosition> rankPositions, Dimension container) {
        super(field, fieldStyle, rankPositions, container);
    }
}
