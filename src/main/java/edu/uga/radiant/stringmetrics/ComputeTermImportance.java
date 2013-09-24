/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uga.radiant.stringmetrics;

import edu.uga.radiant.util.RemoveStopWords;
import edu.uga.radiant.util.Stemmer;
import java.lang.Math;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Chaitu
 */
public class ComputeTermImportance {
    
    public static Map<String,Double> calculateTermImportance(Set<String> ontCorpus) throws URISyntaxException{
        Map<String,Double> imp = new HashMap<String,Double>();
        Map<String,Integer> freq = new HashMap<String,Integer>(); 
        for(String s:ontCorpus){
            s = RemoveStopWords.removeStop(s);
            String [] sParts = s.trim().split(" ");
            for(String str : sParts){
                str = str.toLowerCase();
                Stemmer stem = new Stemmer();
                stem.add(str.toCharArray(), str.toCharArray().length);
                stem.stem();
                str = stem.toString();
                if(freq.containsKey(str)){
                    Integer count = freq.get(str);
                    count = count+1;
                    freq.remove(str);
                    freq.put(str, count);
                }else{
                    Integer count = 1;
                    freq.put(str, count);
                }
            }
        }
        Iterator it = freq.keySet().iterator();
        while(it.hasNext()){
            String key = (String) it.next();
            Integer count = freq.get(key);
            double im = 1.0/count;  //Math.log10(1.0/count);
            imp.put(key, im);
        }
        
        return imp;
    }
    
}
