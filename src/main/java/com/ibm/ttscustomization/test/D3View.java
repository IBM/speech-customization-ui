package com.ibm.ttscustomization.test;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.List;

@Route("test-d3-svg")
@JavaScript("https://d3js.org/d3.v5.min.js")
public class D3View extends VerticalLayout {
    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        add(new ThreeBalls());
        ComboBox<String> colorSelect = new ComboBox<>();
        List<String> colors = new ArrayList<>();
        colors.add("Fuchsia");
        colors.add("Gold");
        colors.add("Teal");
        colors.add("Aqua");
        add(colorSelect);
        colorSelect.setItems(colors);
        Page page = attachEvent.getUI().getPage();
        colorSelect.addValueChangeListener(e -> {
            page.executeJavaScript("d3.selectAll('circle').style('fill',$0)",
                    colorSelect.getValue());
        });
    }

    @Tag("svg")
    public static class ThreeBalls extends Component {
        public ThreeBalls() {
            getElement().setProperty("innerHTML",
                    "<svg width=\"720\" height=\"120\">"
                            + "  <circle cx=\"40\" cy=\"60\" r=\"10\"></circle>"
                            + "  <circle cx=\"80\" cy=\"60\" r=\"10\"></circle>"
                            + "  <circle cx=\"120\" cy=\"60\" r=\"10\"></circle>"
                            + "</svg>");
        }

    }


}
