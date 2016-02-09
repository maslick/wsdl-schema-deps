package com.maslick.java;

import com.predic8.schema.Import;
import com.predic8.schema.Schema;
import com.predic8.wsdl.Definitions;
import com.predic8.wsdl.WSDLParser;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
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

        DirectedGraph<WsdlSchema, DefaultEdge> wsdlGraph = createGraph(defs);
        List<JSONObject> nodes = new ArrayList<JSONObject>();
        List<JSONObject> edges = new ArrayList<JSONObject>();
        JSONObject edge;
        int id = 0;
        for (WsdlSchema a : wsdlGraph.vertexSet()) {
            JSONObject obj = new JSONObject();
            obj.put("id", a.id);
            obj.put("label", a.name);

            JSONArray parentlist = new JSONArray();
            for(WsdlSchema parent : a.parent) {
                edge = new JSONObject();
                edge.put("from", a.id);
                edge.put("to", parent.id);
                edge.put("arrows", "to");
                parentlist.put(parent.name);
                edges.add(edge);
            }
            obj.put("parent", parentlist);
            nodes.add(obj);
        }
        JSONObject resp = new JSONObject();
        resp.put("nodes", nodes);
        resp.put("edges", edges);

        return Response.ok(resp.toString()).build();
    }

    private static DirectedGraph<WsdlSchema, DefaultEdge> createGraph(Definitions defs)
    {
        DirectedGraph<WsdlSchema, DefaultEdge> g =
                new DefaultDirectedGraph<WsdlSchema, DefaultEdge>(DefaultEdge.class);

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
            out("* Schema: " + w.name);
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

    public static void out(String str) {
        System.out.println(str);
    }
}
