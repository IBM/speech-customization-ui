package com.ibm.ttscustomization.ttsui.words.ipa;


import com.ibm.sttcustomization.utils.Utils;
import com.ibm.ttscustomization.ttsmodel.customizations.TTSCustomization;
import com.ibm.ttscustomization.ttsutils.RichTextStringToHtml1;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.InputEvent;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.dom.ElementFactory;
import org.apache.poi.ss.usermodel.Row;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;


public class IPAKeyboard extends Div {
    private final TTSCustomization customization;
    private final TextField txtFieldTranslation;
    private IPAEntries ipaentries = null;
    private TextField textField;

    public IPAKeyboard(TTSCustomization customization, TextField textField, TextField txtFieldTranslation) {
        this.customization = customization;
        this.textField = textField;
        this.txtFieldTranslation = txtFieldTranslation;

        ComponentEventListener<InputEvent> listener = (ComponentEventListener<InputEvent>) event -> {
        };
        textField.addInputListener(listener);

        VerticalLayout verticalLayoutContent = new VerticalLayout();
        verticalLayoutContent.setHeight("100%");
        verticalLayoutContent.setWidth("100%");

        Div cssLayout = new Div();
        cssLayout.setHeight("100%");
        cssLayout.setWidth("100%");

        try {
            createGridLayoutForIPEEntries(cssLayout);
        } catch (IOException e) {
            Utils.displayErrorMessage(e);
        }

        verticalLayoutContent.add(cssLayout);


        // The composition root MUST be set
        add(verticalLayoutContent);
    }

    @ClientCallable
    public void println(String text) {
        System.out.println(text);
    }

    private void createGridLayoutForIPEEntries(Div cssLayout) throws IOException {
        loadIPAFromExcel(customization.getLanguage());

        int nGridWidth = 7;

        int nEntries = ipaentries.getIpaentries().size();
        int nGridHeight = nEntries / nGridWidth + 1;

        Div gridLayout = new Div();
        gridLayout.setWidth("100%");
        gridLayout.setClassName("grid-container");

        int column = 0;
        int row = 0;
        for (int i =0; i < nEntries; i++) {
            final IPAEntry ipae = ipaentries.getIpaentries().get(i);
            Element clickableElement = ElementFactory.createButton();
            String sIPAE = "<span class=\"ipaspan\">" + ipae.getIpa() + "</span>";
            String sExaamples = "<span class=\"ipaexamplesspan\">" + ipae.getExamples() + "</span>";
//            String sIPAE = "<span>" + ipae.getIpa() + "</span>";
//            String sExaamples = "<span>" + ipae.getExamples() + "</span>";
            clickableElement.setProperty("innerHTML", "<span class=\"grid-item\"  >" + sIPAE + sExaamples + "</span>");

            clickableElement.addEventListener("click", e -> {
                //Element response = ElementFactory.createDiv(ipae.getIpatext());
                //getElement().appendChild(response);
                textField.setValue(textField.getValue() + ipae.getIpatext() );
            });
            gridLayout.getElement().appendChild(clickableElement);

            column = column + 1;
            if (column >= nGridWidth) {
                column = 0;
                row = row + 1;
            }
        }
        cssLayout.add(gridLayout);
    }

    private String htmlStringFromCell(org.apache.poi.hssf.usermodel.HSSFCell xssCell) {
        StringBuilder sb = new StringBuilder();
        RichTextStringToHtml1 richTextStringToHtml = new RichTextStringToHtml1(xssCell, sb);
        richTextStringToHtml.formatCell();
        return sb.toString();
    }

    private void loadIPAFromExcel(String language) throws IOException {
        ipaentries = new IPAEntries();

        //InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/ipa_" + "en_us" + ".xls");
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/ipa_" + language + ".xls");
        if (inputStream == null)
            return;

        try {
            org.apache.poi.hssf.usermodel.HSSFWorkbook workbook = new org.apache.poi.hssf.usermodel.HSSFWorkbook(inputStream);
            org.apache.poi.hssf.usermodel.HSSFSheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            rowIterator.next();//Skip the 1st Row while reading
            while (rowIterator.hasNext())
            {
                org.apache.poi.hssf.usermodel.HSSFRow row = (org.apache.poi.hssf.usermodel.HSSFRow)rowIterator.next();
                if (row == null)
                    continue;

                org.apache.poi.hssf.usermodel.HSSFCell xssCell = row.getCell(0);
                if (xssCell == null)
                    continue;
                if (row.getCell(0) == null)
                    continue;
                if (row.getCell(0).getStringCellValue().equalsIgnoreCase(""))
                    continue;

                IPAEntry ipae = new IPAEntry();

                ipae.setIpa(htmlStringFromCell(row.getCell(0)));

                if (row.getCell(3) != null)
                    ipae.setGroup(row.getCell(3).getStringCellValue().replace('\u00A0',' ').trim());

                ipae.setIpatext(row.getCell(0).getStringCellValue().replace('\u00A0',' ').trim());

                if (row.getCell(1) != null)
                    ipae.setExamples(htmlStringFromCell(row.getCell(1)));

                ipaentries.getIpaentries().add(ipae);
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
