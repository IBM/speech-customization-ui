package com.ibm.sttcustomization.utils;

public class RichTextStringToHtml {
//
//    private final HSSFWorkbook wb;
//    private final HSSFPalette colors;
//    private StringBuilder sb;
//    private Cell cell;
//    private Sheet sheet;
//
//    private static final Map<Short, String> ALIGN = mapFor(ALIGN_LEFT, "left",
//            ALIGN_CENTER, "center", ALIGN_RIGHT, "right", ALIGN_FILL, "left",
//            ALIGN_JUSTIFY, "left", ALIGN_CENTER_SELECTION, "center");
//    private static final Map<Short, String> VERTICAL_ALIGN = mapFor(
//            VERTICAL_BOTTOM, "bottom", VERTICAL_CENTER, "middle", VERTICAL_TOP,
//            "top");
//    private static final Map<Short, String> BORDER = mapFor(BORDER_DASH_DOT,
//            "dashed 1pt", BORDER_DASH_DOT_DOT, "dashed 1pt", BORDER_DASHED,
//            "dashed 1pt", BORDER_DOTTED, "dotted 1pt", BORDER_DOUBLE,
//            "double 3pt", BORDER_HAIR, "solid 1px", BORDER_MEDIUM, "solid 2pt",
//            BORDER_MEDIUM_DASH_DOT, "dashed 2pt", BORDER_MEDIUM_DASH_DOT_DOT,
//            "dashed 2pt", BORDER_MEDIUM_DASHED, "dashed 2pt", BORDER_NONE,
//            "none", BORDER_SLANTED_DASH_DOT, "dashed 2pt", BORDER_THICK,
//            "solid 3pt", BORDER_THIN, "dashed 1pt");
//    private static final HSSFColor HSSF_AUTO = new HSSFColor.AUTOMATIC();
//
//    public RichTextStringToHtml(Cell c, StringBuilder sb) {
//        cell = c;
//        sheet = cell.getSheet();
//        wb = (HSSFWorkbook) sheet.getWorkbook();
//        colors = wb.getCustomPalette();
//        this.sb = sb;
//    }
//
//    private static <K, V> Map<K, V> mapFor(Object... mapping) {
//        Map<K, V> map = new HashMap<K, V>();
//        for (int i = 0; i < mapping.length; i += 2) {
//            map.put((K) mapping[i], (V) mapping[i + 1]);
//        }
//        return map;
//    }
//
//    public void colorStyles(CellStyle style) {
//        HSSFCellStyle cs = (HSSFCellStyle) style;
//
//        styleColor("background-color", cs.getFillForegroundColor());
//        styleColor("color", cs.getFont(wb).getColor());
//
//    }
//
//    private void styleColor(String attr, short index) {
//        HSSFColor color = colors.getColor(index);
//        if (index == HSSF_AUTO.getIndex() || color == null) {
//        } else {
//            short[] rgb = color.getTriplet();
//            sb.append(String.format("  %s: #%02x%02x%02x; /* index = %d */", attr, rgb[0],
//                    rgb[1], rgb[2], index));
//        }
//    }
//
//    private <K> void styleOut(String attr, K key, Map<K, String> mapping) {
//        String value = mapping.get(key);
//        if (value != null) {
//            sb.append("  ").append(attr).append(": ").append(value);
//
//        }
//    }
//
//    private void fontStyle(short fontIndex) {
//        Font font = wb.getFontAt(fontIndex);
//        if (font.getBoldweight() >= HSSFFont.BOLDWEIGHT_NORMAL) {
//            sb.append("  font-weight: bold;");
//        }
//        if (font.getItalic()) {
//            sb.append("  font-style: italic;");
//        }
//        int fontheight = font.getFontHeightInPoints();
//        if (fontheight == 9) {
//            //fix for stupid ol Windows
//            fontheight = 10;
//        }
//        sb.append("  font-size: ").append(fontheight).append("pt;");
//        styleColor("color", font.getColor());
//
//        // Font color is handled with the other colors
//    }
//
//    private void getHtmlFromHss(HSSFRichTextString richTextString) {
//
//
//        //List<FormattingRun> formattingRuns = new ArrayList<FormattingRun>();
//        int numFormattingRuns = richTextString.numFormattingRuns();
//        String baseString = richTextString.getString();
//        CellStyle cs = cell.getCellStyle();
//        short csFont = cs.getFontIndex();
//        sb.append("<span style=").append('"');
//        fontStyle(csFont);
//        sb.append('"').append(">");
//        if (numFormattingRuns <= 0) {
//            // no formatting so just copy in the string
//            sb.append(StringEscapeUtils.escapeHtml4(baseString));
//            sb.append("</span>");
//
//            return;
//        }
//        int firstIndex = richTextString.getIndexOfFormattingRun(0);
//        int currOffset = 0;
//
//        while (currOffset < firstIndex) {
//            sb.append(escapeHtml(baseString.charAt(currOffset)));
//            currOffset++;
//        }
//        for (int fmtIdx = 0; fmtIdx < numFormattingRuns; fmtIdx++) {
//            int begin = richTextString.getIndexOfFormattingRun(fmtIdx);
//            short fontIndex = richTextString.getFontOfFormattingRun(fmtIdx);
//            // apply the font at this point
//            // Walk the string to determine the length of the formatting run.
//            sb.append("</span><span style=").append('"');
//            fontStyle(fontIndex);
//            sb.append('"').append(">");
//            for (int j = begin; j < richTextString.length(); j++) {
//                short currFontIndex = richTextString.getFontAtIndex(j);
//
//                if (currFontIndex == fontIndex) {
//                    sb.append(escapeHtml(baseString.charAt(currOffset)));
//                    currOffset++;
//                } else {
//
//                    break;
//                }
//            }
//            //formattingRuns.add(new FormattingRun(begin, length, fontIndex));
//        }
//        sb.append("</span>");
//
//    }
//
//    public String escapeHtml(char in) {
//        switch (in) {
//            case '\n':
//            case '\r':
//                return "<br/>";
//
//        }
//        return StringEscapeUtils.escapeHtml4("" + in);
//    }
//
//    public void formatCell() {
//        String content = null;
//        String attrs = "";
//        CellStyle style = null;
//
//        style = cell.getCellStyle();
//        attrs = tagStyle(cell, style);
//        //Set the value that is rendered for the cell
//        //also applies the format
//        CellFormat cf = CellFormat.getInstance(
//                style.getDataFormatString());
//        CellFormatResult result = cf.apply(cell);
//        try {
//            getHtmlFromHss((HSSFRichTextString) cell.getRichStringCellValue());
//        } catch (java.lang.IllegalStateException ex) {
//        }
//
//
//    }
//
//    private String tagStyle(Cell cell, CellStyle style) {
//        if (style.getAlignment() == ALIGN_GENERAL) {
//            switch (ultimateCellType(cell)) {
//                case HSSFCell.CELL_TYPE_STRING:
//                    return "style=\"text-align: left;\"";
//                case HSSFCell.CELL_TYPE_BOOLEAN:
//                case HSSFCell.CELL_TYPE_ERROR:
//                    return "style=\"text-align: center;\"";
//                case HSSFCell.CELL_TYPE_NUMERIC:
//                default:
//                    // "right" is the default
//                    break;
//            }
//        }
//        return "";
//    }
//
//    private static int ultimateCellType(Cell c) {
//        int type = c.getCellType();
//        if (type == Cell.CELL_TYPE_FORMULA) {
//            type = c.getCachedFormulaResultType();
//        }
//        return type;
//    }
//

}