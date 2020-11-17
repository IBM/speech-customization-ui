package com.ibm.ttscustomization.test;


import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;

public class VaadinWelcome extends Composite<Div> implements HasComponents {

	public VaadinWelcome() {

		add(new Image("hero-reindeer.svg", "vaadin"));
		add(new Paragraph("Hello Vaadin 10!"));
	}
}
