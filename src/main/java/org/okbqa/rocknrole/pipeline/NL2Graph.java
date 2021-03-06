package org.okbqa.rocknrole.pipeline;

import com.google.gson.JsonParser;
import java.util.Set;
import org.okbqa.rocknrole.graph.Graph;
import org.okbqa.rocknrole.graph.Pair;
import org.okbqa.rocknrole.parsing.ETRI;
import org.okbqa.rocknrole.parsing.ParseResult;
import org.okbqa.rocknrole.parsing.Parser;
import org.okbqa.rocknrole.parsing.Stanford;
import org.okbqa.rocknrole.transforming.Transformer;

/**
 *
 * @author cunger
 */
public class NL2Graph {
    
    
    String language;
    
    Parser parser;
    Transformer transformer;
    
    JsonParser json;
        
    boolean verbose;
    

    public NL2Graph(String l) {

        verbose = false;

        language = l;
                
        switch (language) {
            case "en": parser = new Stanford(); break;
            case "ko": parser = new ETRI(); break;
        }
        
        transformer = new Transformer(language);
        
        json  = new JsonParser();
        
    }
    
    public void debugMode() {
        verbose = true;
        transformer.debugMode();
    }
    
    public Graph constructGraph(String input, Set<Pair<Integer,Integer>> entities) {
        
        if (verbose) {
            System.out.println("\n\n------ INPUT ------\n");
            System.out.println(input);
        }
        
        ParseResult parse = parser.parse(input,entities);
                 
        try {            
            Graph synGraph = parse.toDependencyGraph();
            
            if (verbose) {
                System.out.println("\n------ SYN ------\n");
                System.out.println(synGraph.toString(true));            
            }
            
            Graph semGraph = transformer.transform(synGraph);
            
            if (verbose) {
                System.out.println("\n------ SEM ------\n");
                System.out.println(semGraph.toString(true));
            }
            
            return semGraph;
            
        } catch (Exception e) {
            
            return null;
        }
    }
    
}
