/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.uga.radiant.stringmetrics;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
/**
 *
 * @author http://en.wikibooks.org/wiki/Algorithm_Implementation/Strings/Dice's_coefficient
 *
 */
public class DiceCustome {
    public static double computeScore(String s1, String s2){
        double totcombigrams = 0;
        Set nx = new HashSet();
        Set ny = new HashSet();
        Set intersection = null;

        for (int i=0; i < s1.length()-1; i++) {
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
        }

        intersection = new TreeSet(nx);
        intersection.retainAll(ny);
        totcombigrams = intersection.size();

        return (2*totcombigrams) / (nx.size()+ny.size());
    }
    public static void main(String[] args) {
                       
            long startTime = System.currentTimeMillis();
            String def1 = "A software module is software composed of a collection of software methods";
            String def2 = "A software method also called subroutine, subprogram, procedure, method, function, or routine is software designed to execute a specific task";
            System.out.println("Score between Software Module and Software Method" + DiceCustome.computeScore(def1, def2));
            def1 = "A symbol used to identify a submitted web service job";
            def2 = "An identifier of molecular sequence(s) or entries from a molecular sequence database";
            System.out.println("Score between jobid and sequence id " + DiceCustome.computeScore(def1, def2));
            def1 = "An animal care protocol is a protocol which specifies the environment in which animals are being kept in captivity for research purposes";
            def2 = "A rodent care protocol is an animal protocol in which the animals being taken care of are rodents";
            System.out.println("Score between Animal care protocol and Rodent care protocol " + DiceCustome.computeScore(def1, def2));
            def1 = "A sequence analysis data transformation process that attempts to align two molecular sequences in order to study the equivalence between sites of molecular sequences including nucleic acid and protein sequence";
            def2 = "An algorithm that attempts to align two molecular sequences, nucleic acid or protein sequence";
            System.out.println("Score between Pairwise Sequence Alignment and Pairwise Sequence Alignment Algo " + DiceCustome.computeScore(def1, def2));
            long endTime = System.currentTimeMillis();
            System.out.println("Time of Execution: "+ (endTime - startTime)/1000 + " seconds");
        }
}
