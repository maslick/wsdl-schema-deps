package com.maslick.java;

import java.util.ArrayList;
import java.util.List;

public class Graph {
    List<Graph> children;
    String name;
    Graph parent, link;

    public Graph(String name) {
        this.children = new ArrayList<Graph>();
        this.name = name;
    }

    public void addName(String name) {
        this.name = name;
    }

    public Graph addChild(String childname) {
        Graph node = new Graph(childname);
        node.parent = this;
        this.children.add(node);
        return node;
    }

    public void deleteChild(Graph child) {
        for (Graph gr : children) {
            if (gr == child) {
                children.remove(child);
                return;
            }
        }
    }

    public void printClass() {
        Helper.out("Name: " + this.name);
        if (this.children.size() > 0) {
            Helper.out("* Children: ");
            for (Graph gr : this.children)  printClass(gr);
        }
    }

    public void printClass(Graph graph) {
        Helper.out("\tName: " + graph.name);
        if (graph.children.size() > 0) {
            Helper.out("\t* Children: ");
            for (Graph gr: graph.children)  printClass(gr);
        }
    }

    public boolean nodeExists(Graph graph, String name) {
        if (graph.name == name) { return true; }
        for (Graph chgr : graph.children) {
            if (chgr.name == name) { return true; }
            if (chgr.children.size() > 0) return nodeExists(chgr, name);
        }
        return false;
    }

    public Graph findNode(String name, Graph where) {
        if (where.name == name) return where;
        for (Graph chgr : where.children) {
            if (chgr.name == name) { return chgr; }
            if (chgr.children.size() > 0) return findNode(name, chgr);
        }
        return null;
    }

}