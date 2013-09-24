/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.uga.radiant.stringmetrics;

import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import edu.uga.radiant.util.RemoveStopWords;
import edu.uga.radiant.util.Stemmer;

/**
 * This algorithm is based on Dice Algorithm
 * Reference: http://en.wikibooks.org/wiki/Algorithm_Implementation/Strings/Dice's_coefficient
 * @author Akshay Choche
 * @version 1.0
 */
public class CompareDefinition {
    /**
     * This method computes the similarity between two definitions using Dice Algorithm
     * @param s1 Definition to be compared
     * @param s2 Definition to be compared
     * @return The Similarity score
     * @throws URISyntaxException 
     */
    public static double getSimilarity(String s1, String s2) throws URISyntaxException{
        if(s1 == null || s2 == null ) return 0;
        if (s1.equals("") && s2.equals("")) return 0;        
        
        s1 = RemoveStopWords.removeStop(s1);
        s2 = RemoveStopWords.removeStop(s2);
        double totcombigrams = 0;
        Set nx = new HashSet();
        Set ny = new HashSet();
        Set intersection = null;
        //Considering just the substring that contains only 2 charecters
        /*for (int i=0; i < s1.length()-1; i++) {
                char x1 = s1.charAt(i);
                char x2 = s1.charAt(i+1);
                String tmp = Character.toString(x1) + Character.toString(x2);
                nx.add(tmp);
        }
        for (int j=0; j < s2.length()-1; j++) {
                char y1 = s2.charAt(j);
                char y2 = s2.charAt(j+1);
                String tmp = Character.toString(y1) + Character.toString(y2);
                ny.add(tmp);
        }*/

        //Considering the words instead of just substring contianing 2 charecters.
        String [] s1words = s1.trim().split(" ");
        String [] s2words = s2.trim().split(" ");
        for (int i=0; i < s1words.length; i++) {
            Stemmer s = new Stemmer();
            String str = s1words[i].toLowerCase();
            //Double imp = RecommendTerms.termImp.get(str);
            s.add(str.toCharArray(), str.toCharArray().length);
            s.stem();
            //termImp.put(str);
            nx.add(s.toString());
        }
        for (int j=0; j < s2words.length; j++) {
            Stemmer s = new Stemmer();
            String str = s2words[j].toLowerCase();
            s.add(str.toCharArray(), str.toCharArray().length);
            s.stem();
            ny.add(s.toString());
        }
        
        intersection = new TreeSet(nx);
        intersection.retainAll(ny);
        /*double totalWeight = 0.0;
        for(Object st : intersection){
            double weight = RecommendTerms.termImp.get(st);
            totalWeight += weight;
        }*/
        totcombigrams = intersection.size();
        return (2*totcombigrams) / (nx.size()+ny.size());
        //return (2*totalWeight) / (nx.size()+ny.size());
        
    }//getSimilarity
}
