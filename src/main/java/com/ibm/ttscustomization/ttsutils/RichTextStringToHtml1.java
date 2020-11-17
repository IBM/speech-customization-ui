package com.ibm.ttscustomization.ttsutils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.format.CellFormat;
import org.apache.poi.ss.format.CellFormatResult;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Sheet;


public class RichTextStringToHtml1 {

    private final HSSFWorkbook wb;
    private final HSSFPalette colors;
    private StringBuilder sb;
    private Cell cell;
    private Sheet sheet;

    private static final HSSFColor HSSF_AUTO = new HSSFColor.AUTOMATIC();

    public RichTextStringToHtml1(Cell c, StringBuilder sb) {
        cell = c;
        sheet = cell.getSheet();
        wb = (HSSFWorkbook) sheet.getWorkbook();
        colors = wb.getCustomPalette();
        this.sb = sb;
    }

    public void colorStyles(CellStyle style) {
        HSSFCellStyle cs = (HSSFCellStyle) style;

        styleColor("background-color", cs.getFillForegroundColor());
        styleColor("color", cs.getFont(wb).getColor());

    }

    private void styleColor(String attr, short index) {
        HSSFColor color = colors.getColor(index);
        if (index == HSSF_AUTO.getIndex() || color == null) {
        } else {
            short[] rgb = color.getTriplet();
            sb.append(String.format("  %s: #%02x%02x%02x; /* index = %d */", attr, rgb[0],
                    rgb[1], rgb[2], index));
        }
    }

    private void fontStyle(short fontIndex) {
        Font font = wb.getFontAt(fontIndex);
        if (font.getUnderline() >  HSSFFont.U_NONE) {
            sb.append("  text-decoration: underline;");
        }
        if (font.getBold()) {
            sb.append("  font-weight: bold;");
        }
        if (font.getItalic()) {
            sb.append("  font-style: italic;");
        }
        int fontheight = font.getFontHeightInPoints();
        if (fontheight == 9) {
            // fix for stupid ol Windows
            fontheight = 10;
        }
        sb.append("  font-size: ").append(fontheight).append("pt;");
        styleColor("color", font.getColor());

        // Font color is handled with the other colors
    }

    private void getHtmlFromHss(HSSFRichTextString richTextString) {

        // List<FormattingRun> formattingRuns = new ArrayList<FormattingRun>();
        int numFormattingRuns = richTextString.numFormattingRuns();
        String baseString = richTextString.getString();
        CellStyle cs = cell.getCellStyle();
        short csFont = cs.getFontIndex();
        sb.append("<span style=").append('"');
        fontStyle(csFont);
        sb.append('"').append(">");
        if (numFormattingRuns <= 0) {
            // no formatting so just copy in the string
            sb.append(StringEscapeUtils.escapeHtml4(baseString));
            sb.append("</span>");

            return;
        }
        int firstIndex = richTextString.getIndexOfFormattingRun(0);
        int currOffset = 0;

        while (currOffset < firstIndex) {
            sb.append(escapeHtml(baseString.charAt(currOffset)));
            currOffset++;
        }
        for (int fmtIdx = 0; fmtIdx < numFormattingRuns; fmtIdx++) {
            int begin = richTextString.getIndexOfFormattingRun(fmtIdx);
            short fontIndex = richTextString.getFontOfFormattingRun(fmtIdx);
            // apply the font at this point
            // Walk the string to determine the length of the formatting run.
            sb.append("</span><span style=").append('"');
            fontStyle(fontIndex);
            sb.append('"').append(">");
            for (int j = begin; j < richTextString.length(); j++) {
                short currFontIndex = richTextString.getFontAtIndex(j);

                if (currFontIndex == fontIndex) {
                    sb.append(escapeHtml(baseString.charAt(currOffset)));
                    currOffset++;
                } else {

                    break;
                }
            }
            // formattingRuns.add(new FormattingRun(begin, length, fontIndex));
        }
        sb.append("</span>");

    }

    public String escapeHtml(char in) {
        switch (in) {
            case '\n':
            case '\r':
                return "<br/>";

        }
        return StringEscapeUtils.escapeHtml4("" + in);
    }

    public void formatCell() {

        String content = null;
        CellStyle style = null;

        style = cell.getCellStyle();
        // Set the value that is rendered for the cell
        // also applies the format
        CellFormat cf = CellFormat.getInstance(
                style.getDataFormatString());
        CellFormatResult result = cf.apply(cell);
        try {
            getHtmlFromHss((HSSFRichTextString) cell.getRichStringCellValue());
        } catch (java.lang.IllegalStateException ex) {
        }

    }

}
