package org.okbqa.rocknrole.parsing;

import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.BasicDependenciesAnnotation;
import edu.stanford.nlp.util.CoreMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import org.okbqa.rocknrole.graph.Pair;


/**
 *
 * @author cunger
 */
public class Stanford implements Parser {
    
    StanfordCoreNLP pipeline;
    
    public Stanford() {
        
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma, parse, ner");
        pipeline = new StanfordCoreNLP(props);
    }
   
    @Override
    public ParseResult parse(String text, Set<Pair<Integer,Integer>> entities) {
        
        ParseResult result = new ParseResult();

        Annotation document = new Annotation(text);
        pipeline.annotate(document);
        
        int i = 0;
        List<CoreMap> annotations = document.get(SentencesAnnotation.class);
        for (CoreMap s: annotations) {
            i++;
            
            // Sentence string
            result.addSentence(i,s.toString());

            // Tokens and POS tags
            for (CoreLabel token: s.get(TokensAnnotation.class)) {
            result.addToken(i,token.index(),token.originalText());
            result.addPOS(i,token.index(),token.getString(PartOfSpeechAnnotation.class));
            // Mark named entities
            if (entities == null) {
                // Use Stanford NEs
                if (!token.get(NamedEntityTagAnnotation.class).equals("O")) {
                    result.addPOS(i,token.index(),"NE");
                }
            } else {
                // Use NEs provided in input
                for (Pair<Integer,Integer> entity : entities) {
                    if (entity.getLeft()  <= token.beginPosition() 
                     && entity.getRight() >= token.endPosition())
                        result.addPOS(i,token.index(),"NE");
                }
            }}
            
            // Dependency parse
            SemanticGraph dependencies = s.get(BasicDependenciesAnnotation.class);
            result.addParse(i,dependencies.toList().trim());
        }
        
        return result;
    }

}