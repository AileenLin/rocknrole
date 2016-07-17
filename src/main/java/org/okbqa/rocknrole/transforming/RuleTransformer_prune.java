package org.okbqa.rocknrole.transforming;

import java.util.ArrayList;
import org.okbqa.rocknrole.graph.Edge;
import org.okbqa.rocknrole.graph.Pair;
import org.okbqa.rocknrole.graph.Graph;
import java.util.List;
import java.util.Map;

/**
 *
 * @author cunger
 */
public class RuleTransformer_prune extends RuleTransformer {

        
    @Override
    public Graph transform(Graph graph) {
        
        Graph g = new Graph();
                
        // Keep only semantic edges
                
        for (Edge e : graph.getEdges()) {
             if (e.getColor() == Edge.Color.SEM) {
                 g.addEdge(e);
                 g.addNode(graph.getNode(e.getHead()));
                 g.addNode(graph.getNode(e.getDependent()));
             }
        }
        
        // Get rid of edges that don't make sense 
        
        List<Edge> del = new ArrayList<>();
        for (Edge e : graph.getEdges()) {
            if (e.getLabel().equals("THING")) {
                del.add(e);
            }
        }
        for (Edge e : del) g.deleteEdge(e);
        
        // Done
        
        return g;
    }
    
    private List<Pair<Graph,Map<Integer,Integer>>> getSubgraphs(Graph graph, String regex) {
               
        Graph subgraph = reader.interpret(regex);
                
        return subgraph.subGraphMatches(graph);
    }
}
