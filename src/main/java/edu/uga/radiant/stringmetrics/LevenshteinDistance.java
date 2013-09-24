/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.uga.radiant.stringmetrics;

/**
 *
 * @author Akshay
 * source: http://en.wikibooks.org/wiki/Algorithm_Implementation/Strings/Levenshtein_distance
 */
public class LevenshteinDistance {
        private static int minimum(int a, int b, int c) {
                return Math.min(Math.min(a, b), c);
        }

        public static int computeLevenshteinDistance(CharSequence str1,
                        CharSequence str2) {
                int[][] distance = new int[str1.length() + 1][str2.length() + 1];

                for (int i = 0; i <= str1.length(); i++)
                        distance[i][0] = i;
                for (int j = 0; j <= str2.length(); j++)
                        distance[0][j] = j;

                for (int i = 1; i <= str1.length(); i++)
                        for (int j = 1; j <= str2.length(); j++)
                                distance[i][j] = minimum(
                                                distance[i - 1][j] + 1,
                                                distance[i][j - 1] + 1,
                                                distance[i - 1][j - 1]
                                                                + ((str1.charAt(i - 1) == str2.charAt(j - 1)) ? 0
                                                                                : 1));

                return distance[str1.length()][str2.length()];
        }

        public static float computeScore(String def1, String def2){
            return (1 - (LevenshteinDistance.computeLevenshteinDistance(def1, def2)/(float) Math.max(def1.length(), def2.length())));
        }

        public static void main(String[] args) {
                       
            long startTime = System.currentTimeMillis();
            String def1 = "A software module is software composed of a collection of software methods";
            String def2 = "A software method also called subroutine, subprogram, procedure, method, function, or routine is software designed to execute a specific task";
            System.out.println("Score between Software Module and Software Method" + LevenshteinDistance.computeScore(def1, def2));
            def1 = "A symbol used to identify a submitted web service job";
            def2 = "An identifier of molecular sequence(s) or entries from a molecular sequence database";
            System.out.println("Score between jobid and sequence id " + LevenshteinDistance.computeScore(def1, def2));
            def1 = "An animal care protocol is a protocol which specifies the environment in which animals are being kept in captivity for research purposes";
            def2 = "A rodent care protocol is an animal protocol in which the animals being taken care of are rodents";
            System.out.println("Score between Animal care protocol and Rodent care protocol " + LevenshteinDistance.computeScore(def1, def2));
            def1 = "A sequence analysis data transformation process that attempts to align two molecular sequences in order to study the equivalence between sites of molecular sequences including nucleic acid and protein sequence";
            def2 = "An algorithm that attempts to align two molecular sequences, nucleic acid or protein sequence";
            System.out.println("Score between Pairwise Sequence Alignment and Pairwise Sequence Alignment Algo " + LevenshteinDistance.computeScore(def1, def2));
            long endTime = System.currentTimeMillis();
            System.out.println("Time of Execution: "+ (endTime - startTime)/1000 + " seconds");
        }
}