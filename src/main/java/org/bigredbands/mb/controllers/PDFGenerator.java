package org.bigredbands.mb.controllers;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.swing.JPanel;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.util.Matrix;
import org.bigredbands.mb.models.CommandPair;
import org.bigredbands.mb.models.DrillInfo;
import org.bigredbands.mb.models.Field;
import org.bigredbands.mb.models.Move;
import org.bigredbands.mb.utils.WordWrap;
import org.bigredbands.mb.views.PdfImage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

/**
 *
 * Used to generate PDFs from marching band routine.
 *
 */
public class PDFGenerator {

    /**
     * Default constructor
     */
    public PDFGenerator() {
    }

    /**
     * Used to generate the pdf files
     *
     * @param drillInfo
     *            - contains information about the drill, including song name,
     *            ranks, moves, commands and comments
     * @param file
     *            - file to which the PDF will be saved
     * @throws IOException
     */
    public void createPDF(DrillInfo drillInfo, File file) throws IOException {
        // Create a new empty document
        PDDocument document = new PDDocument();

        int pageNumber = 1; // page number
        int moveNumber = 0; // move number
        int begMeasure = 0; // beginning measure of move
        int endMeasure = 0; // ending measure of move

        // iterating through each move in the drill
        for (Move move : drillInfo.getMoves()) {

            // Create a new blank page and add it to the document
            PDPage blankPage = new PDPage();

            // rotates page for landscape mode
            blankPage.setRotation(90);

            // add page to the document
            document.addPage(blankPage);

            PDRectangle pageSize = blankPage.getMediaBox();

            // Page dimensions
            // Units are in 1/72 of an inch ("user-space units")
            float pageHeight = pageSize.getWidth(); // height of page
                                                    // (landscape)
            float pageWidth = pageSize.getHeight(); // width of page (landscape)

            // Measurements in 1/72 in. increments
            Field field = Field.CollegeFootball; // TODO: allow setting this dynamically
            float drillWidth = 0.85f * pageWidth;
            float drillHeight = drillWidth / field.AspectRatio;
            float drillMarginX = (pageWidth - drillWidth) / 2.0f;
            float fieldMarginX = drillMarginX + (field.EndzoneWidth / field.TotalLength) * drillWidth;
            float drillMarginY = 0.08f * pageHeight;

            // Measurements in px
            float imageWidth = drillWidth * (300.0f / 72.0f),
                imageHeight = drillHeight * (300.0f / 72.0f);
            Dimension dim = new Dimension((int) imageWidth, (int) imageHeight);

            PdfImage image = new PdfImage(field, move.getEndPositions(), dim);
            image.setPreferredSize(dim);
            image.setSize(dim);
            BufferedImage bi = createImage(image);

            // add image to PDF
            PDImageXObject img = LosslessFactory.createFromImage(document, bi);

            // Initialize ContentStream to add content to PDF page
            PDPageContentStream contentStream = new PDPageContentStream(
                document, blankPage, PDPageContentStream.AppendMode.OVERWRITE,
                false);

            // add the rotation using the current transformation matrix
            // including a translation of pageWidth to use the lower left corner
            // as 0,0 reference; properly orients text and image
            contentStream.transform(new Matrix(0, 1, -1, 0, pageHeight, 0));

            contentStream.drawImage(img,
                drillMarginX, pageHeight - drillHeight - drillMarginY,
                drillWidth, drillHeight);

            // Get the header text
            String drillTitle, measureText, moveLabel;

            drillTitle = drillInfo.getSongName();

            // we have a map of measure number to count per measure
            // currently assumes 4 counts per measure
            // TODO: Make measures adjust with changes in count per measure
            begMeasure = endMeasure;
            endMeasure = begMeasure + move.getCounts() / 4;
            if (begMeasure != endMeasure) {
                measureText = "Measures:  " + (begMeasure + 1) + " - "
                        + endMeasure;
            } else {
                measureText = "Measures:  " + begMeasure + " - " + endMeasure;
            }

            moveLabel = "Move " + moveNumber;

            // setting font
            PDFont pdfFont = PDType1Font.HELVETICA;
            PDFont rankFont = PDType1Font.HELVETICA_BOLD;
            float commentFontSize = 12.0f;
            float fontSize = 10.0f;
            contentStream.setFont(pdfFont, fontSize);

            // Print drill title
            contentStream.beginText();
            contentStream.newLineAtOffset(fieldMarginX, pageHeight - drillMarginY);
            contentStream.showText(drillTitle);
            contentStream.endText();

            // Print measures
            float measureTextWidth = pdfFont.getStringWidth(measureText) / 1000.0f * fontSize;
            contentStream.beginText();
            contentStream.newLineAtOffset((pageWidth - measureTextWidth)/2,
                    pageHeight - drillMarginY);
            contentStream.showText(measureText);
            contentStream.endText();

            // Print move label
            float moveLabelWidth = pdfFont.getStringWidth(moveLabel) / 1000.0f * fontSize;
            contentStream.beginText();
            contentStream.newLineAtOffset(pageWidth - fieldMarginX - moveLabelWidth,
                    pageHeight - drillMarginY);
            contentStream.showText(moveLabel);
            contentStream.endText();

            // print instructions for ranks
            // if rank commands are the same of a prior rank add that rank to
            // one already there
            // otherwise start text in next column or if no more space on the
            // right, next row
            if (moveNumber > 0) {
                // print move's comments
                float yOffset = drillMarginY + drillHeight + fontSize;
                if (move.getComments().length() > 0) {
                    yOffset += (commentFontSize - fontSize);

                    contentStream.setFont(pdfFont, commentFontSize);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(fieldMarginX, pageHeight - yOffset);
                    contentStream.showText("Comments:  " + move.getComments());
                    contentStream.endText();

                    // Reset to original font size
                    contentStream.setFont(pdfFont, fontSize);

                    // Offset for the comment space
                    yOffset += 2*fontSize;
                }

                // Divide per-rank commands into columns
                int numColumns = 3;
                float columnWidth = (pageWidth - 2 * fieldMarginX) / (float) numColumns;
                float commandMaxWidth = columnWidth * 0.95f;

                contentStream.beginText();
                contentStream.newLineAtOffset(fieldMarginX, pageHeight - yOffset);

                // Get the ranks grouped by unique move
                Map<ArrayList<CommandPair>, String> rankGroups = move.getCommands().entrySet().stream()
                    .collect(
                        Collectors.groupingBy(
                            Map.Entry::getValue,
                            Collectors.mapping(Map.Entry::getKey, Collectors.joining(", "))
                        )
                    );

                int rankGroupCounter = 0;
                int maxLines = 1;
                float lineSpacing = 1.5f * fontSize;
                for (Entry<ArrayList<CommandPair>, String> rankGroup : rankGroups.entrySet()) {
                    String rankStr = rankGroup.getValue() + ":";
                    String commandStr = rankGroup.getKey().stream()
                        .map(Object::toString)
                        .collect(Collectors.joining(", "));

                    // Print the list of ranks and calculate where the list of
                    // commands should start.
                    contentStream.setFont(rankFont, fontSize);
                    String[] wrappedRanks = WordWrap.wrap(rankStr, commandMaxWidth, 0.0f, ",", rankFont, fontSize);
                    assert(wrappedRanks.length > 0);

                    for (String line : wrappedRanks) {
                        contentStream.showText(line);
                        contentStream.newLineAtOffset(0, -lineSpacing);
                    }

                    // Since we need to print the next text on the same line,
                    // back up a line and indent by the appropriate offset.
                    float commandStrOffset = rankFont.getStringWidth(wrappedRanks[wrappedRanks.length - 1] + " ") / 1000.0f * fontSize;
                    contentStream.newLineAtOffset(commandStrOffset, lineSpacing);

                    // Print the list of commands after the list of ranks
                    contentStream.setFont(pdfFont, fontSize);
                    String[] wrappedCommands = WordWrap.wrap(commandStr, commandMaxWidth, commandStrOffset, ",", pdfFont, fontSize);
                    assert(wrappedCommands.length > 0);

                    for (String line : wrappedCommands) {
                        contentStream.showText(line);
                        contentStream.newLineAtOffset(-commandStrOffset, -lineSpacing);

                        // Zero out the offset so we only apply it to the first
                        // line.
                        commandStrOffset = 0;
                    }

                    int totalLines = wrappedRanks.length + wrappedCommands.length - 1;
                    contentStream.newLineAtOffset(0, totalLines * lineSpacing);
                    maxLines = Math.max(maxLines, totalLines);

                    // Move to the next writeout position
                    rankGroupCounter++;
                    if (rankGroupCounter % numColumns == 0) {
                        contentStream.newLineAtOffset(-((numColumns - 1) * columnWidth), -(maxLines + 1) * lineSpacing);
                    } else {
                        contentStream.newLineAtOffset(columnWidth, 0);
                    }
                }

                contentStream.endText();
            }

            // print page number
            /*if (moveNumber >= 0) {
                contentStream.beginText();
                contentStream.newLineAtOffset((pageWidth / 2) - 15,
                        bufferBottom - 20);
                contentStream.showText("Page " + pageNumber);
                contentStream.endText();
            }*/

            pageNumber++; // increment page number
            moveNumber++; // increment move number

            // close out content stream
            contentStream.close();
        }

        // Save the newly created document
        document.save(file.getAbsolutePath());

        // properly close the document
        document.close();

    }

    /**
     * converts panel to BufferedImage
     *
     * @param panel
     *            - JPanel of football field
     * @return - returns BufferedImage of football field with ranks
     */
    public BufferedImage createImage(JPanel panel) {

        BufferedImage bi = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bi.createGraphics();
        panel.paint(g);
        return bi;
    }

}
