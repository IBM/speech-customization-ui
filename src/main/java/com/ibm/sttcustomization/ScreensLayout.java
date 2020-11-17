/*
 * Copyright 2000-2017 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.ibm.sttcustomization;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Nav;
import com.vaadin.flow.router.*;

import java.util.HashMap;
import java.util.Map;

/**
 * The main view contains a menu and a container for either of the hello world
 * variants.
 */
@StyleSheet("frontend://src/styles.css")
public class ScreensLayout extends Composite<Div>
        implements RouterLayout, AfterNavigationObserver {

    private Div container;

    public ScreensLayout() {
        Div d1 = new Div();
        d1.addClassName("container");

        H1 header = new H1("Header");
        d1.add(header);

        Div d2 = new Div();
//        d2.addClassName("wrapper clearfix");

        Div sidebar = new Div();
        sidebar.setClassName("sidebar");
        d2.add(sidebar);

        Nav nav = new Nav();
        nav.add();


        getContent().add(d1, d2);

        H1 heading = new H1("333 ways to say Hello");
        Text intro = new Text(
                "Three different ways of implementing Hello World using Vaadin Flow");

        // Set up the container where sub views will be shown
        container = new Div();
        container.addClassName("container");

        getContent().addClassName("main-view");
        getContent().add(heading, intro, container);
    }


    @Override
    public void showRouterLayoutContent(HasElement child) {
        // Update what we show whenever the sub view changes
        container.removeAll();

        if (child != null) {
            container.add(new H2(
                    child.getClass().getAnnotation(PageTitle.class).value()));
            container.add((Component) child);
        }
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {

    }
}
