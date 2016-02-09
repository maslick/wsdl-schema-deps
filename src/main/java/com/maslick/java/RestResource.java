package com.maslick.java;

import com.predic8.schema.Import;
import com.predic8.schema.Schema;
import com.predic8.wsdl.Definitions;
import com.predic8.wsdl.WSDLParser;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/")
@Produces(MediaType.TEXT_PLAIN)
public class RestResource {
    @POST
    @Consumes("application/x-www-form-urlencoded")
    public Response getNodes(@FormParam("url") String wsdlUrl) {
        WSDLParser parser = new WSDLParser();
        Definitions defs = parser.parse(wsdlUrl);

        DirectedGraph<WsdlSchema, MyDefaultEdge> wsdlGraph = test2(defs);
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
                edge.put("value", 1);
                edge.put("arrows", "to");
                parentlist.put(parent.name);
                edges.add(edge);
            }
            obj.put("parent", parentlist);
            jlist.add(obj);
        }
        JSONObject resp = new JSONObject();
        resp.put("nodes", jlist);
        resp.put("edges", edges);

        return Response.ok(resp.toString()).build();
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
