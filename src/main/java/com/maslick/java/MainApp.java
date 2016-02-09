package com.maslick.java;


import com.predic8.schema.Schema;
import com.predic8.wsdl.*;
import com.predic8.schema.Import;
import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;


public class MainApp {


    public static void main(String... args) throws Exception {
        WSDLParser parser = new WSDLParser();
        Definitions defs = parser.parse("/Users/maslick/Desktop/Organ.wsdl");
        //Definitions defs = parser.parse("/Users/maslick/Desktop/weather.wsdl");

        DirectedGraph<WsdlSchema, MyDefaultEdge> wsdlGraph = test2(defs);

        for (MyDefaultEdge e : wsdlGraph.edgeSet()) {
            WsdlSchema src = (WsdlSchema) e.getSource1();
            WsdlSchema target = (WsdlSchema) e.getTarget1();
            Helper.out(src.name + " -> " + target.name);
        }

        List<JSONObject> jlist = new ArrayList<JSONObject>();
        List<JSONObject> edges = new ArrayList<JSONObject>();
        JSONObject edge;
        int id = 0;
        for (WsdlSchema a : wsdlGraph.vertexSet()) {
            JSONObject obj = new JSONObject();
            obj.put("id", a.id);
            obj.put("value", 20);
            obj.put("label", a.name);

            JSONArray parentlist = new JSONArray();
            for(WsdlSchema parent : a.parent) {
                edge = new JSONObject();
                edge.put("from", a.id);
                edge.put("to", parent.id);
                edge.put("value", 3);
                parentlist.put(parent.name);
                edges.add(edge);
            }
            obj.put("parent", parentlist);
            jlist.add(obj);
        }
        Helper.out(jlist.toString());
        Helper.out(edges.toString());
    }

    private static DirectedGraph<WsdlSchema, MyDefaultEdge> test2(Definitions defs)
    {
        DirectedGraph<WsdlSchema, MyDefaultEdge> g =
                new DefaultDirectedGraph<WsdlSchema, MyDefaultEdge>(MyDefaultEdge.class);

        // add the vertices
        List<WsdlSchema> schemaList = new ArrayList<WsdlSchema>();
        WsdlSchema w;
        int id = 0;
        for (Schema schema : defs.getSchemas()) {
            w = new WsdlSchema();
            w.name = schema.getTargetNamespace();
            w.id = ++id;
            w.imports = new ArrayList<String>();
            w.parent = new ArrayList<WsdlSchema>();
            g.addVertex(w);
            Helper.out("* Schema: " + w.name);
            for (Import imp : schema.getImports()) {
                for (WsdlSchema l : schemaList) {
                    if (imp.getNamespace().equals(l.name)) {
                        g.addEdge(w, l);
                        w.parent.add(l);
                    }
                }
                w.imports.add(imp.getNamespace());
            }
            schemaList.add(w);
        }
        return g;
    }
}

