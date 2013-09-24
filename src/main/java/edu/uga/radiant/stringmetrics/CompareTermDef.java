/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.uga.radiant.stringmetrics;

import edu.uga.radiant.util.RemoveStopWords;

import java.net.URISyntaxException;
import java.util.StringTokenizer;

/**
 * This class is used to compare a Definition with a Term and return a score
 * @author Akshay Choche
 * @version 1.0
 */
public class CompareTermDef {

    /**
     * This method is used to score a term with definition
     * @param term Is the term to be compared
     * @param defination Is the definition to be compared
     * @return Returns the similarity score
     * @throws URISyntaxException 
     */
    public static double getSimilarity(String term, String definition) throws URISyntaxException{
        double score = 0;
        if(definition == null || term == null)
            return 0;
        if(term.isEmpty() || definition.isEmpty()) return score;
        
        definition = RemoveStopWords.removeStop(definition);
        String term1 = RemoveStopWords.removeStop(term);  
        if(term1 != null && !term1.isEmpty()) term = term1;
        StringTokenizer ter = new StringTokenizer(term);
        int tokenTer = ter.countTokens();
        for(int cnt = 0; cnt<tokenTer; cnt++){
            double intermediate_score = 0;
            String termword = ter.nextToken();
            termword = termword.toLowerCase();
            StringTokenizer def = new StringTokenizer(definition);
            while(def.hasMoreTokens()){
                String defWord = def.nextToken();
                defWord = defWord.toLowerCase();
                double newscore = CompareTerm.getSimilarity(termword, defWord);
                if(newscore > intermediate_score){
                    intermediate_score = newscore;
                }//if
            }//while
            score += intermediate_score;
        }//for
        score /= tokenTer;
        
        return score;
    }//getSimilarity    
}//CompareTermDef
