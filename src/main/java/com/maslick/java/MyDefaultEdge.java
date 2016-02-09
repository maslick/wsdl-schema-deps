package com.maslick.java;

import org.jgrapht.graph.DefaultEdge;

/**
 * Created by maslick on 09/02/16.
 */
public class MyDefaultEdge extends DefaultEdge {

    public Object getSource1() {
        return getSource();
    }

    public Object getTarget1() {
        return getTarget();
    }
}
