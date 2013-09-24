package edu.uga.radiant.stringmetrics;



import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import edu.uga.radiant.stringmetrics.InvalidNGramException;
import edu.uga.radiant.stringmetrics.interfaces.StringMatchAdapter;

public class NGramSimilarity implements StringMatchAdapter{
	
	/**A N-Gram Similarity class with n = 1*/
	public static final NGramSimilarity UNIGRAM_SIMILARITY;
	/**A N-Gram Similarity class with n = 2*/
	public static final NGramSimilarity BIGRAM_SIMILARITY;
	/**A N-Gram Similarity class with n = 3*/
	public static final NGramSimilarity TRIGRAM_SIMILARITY;
	
	static {
		UNIGRAM_SIMILARITY = new NGramSimilarity();
		UNIGRAM_SIMILARITY.n = 1;
		
		BIGRAM_SIMILARITY = new NGramSimilarity();
		BIGRAM_SIMILARITY.n = 2;
		
		TRIGRAM_SIMILARITY = new NGramSimilarity();
		TRIGRAM_SIMILARITY.n = 3;
	}
	
	private int n;
	
	public NGramSimilarity() { this.n=3; }
	
	/**
	 * Create an N-Gram similarity class. The parameter n describes how many
	 * characters are in each gram.
	 * @param n The number of characters in a gram. (n >= 1)
	 */
	public NGramSimilarity(int n) throws InvalidNGramException {
		if(n < 1)
			throw new InvalidNGramException("n must be >= 1");
		this.n = n;
	}
	
	/**
	 * The N-Gram similarity between two strings.
	 * 
	 * @param strOne A string to compare.
	 * @param strTwo Another string to compare.
	 * @return The similarity as a percentage match.
	 */
	public double similarity(String strOne, String strTwo) {
		return similarity(strOne.toCharArray(), strTwo.toCharArray());
	}
	
	/**
	 * The N-Gram similarity between two strings, as character arrays.
	 * 
	 * @param strOne A string to compare.
	 * @param strTwo Another string to compare.
	 * @return The similarity as a percentage match.
	 */
	private double similarity(char[] strOne, char[] strTwo) {
		Set<char[]> strOneGrams = getGrams(strOne);
		Set<char[]> strTwoGrams = getGrams(strTwo);
		
		int oneSize = strOneGrams.size();
		int twoSize = strTwoGrams.size();
		
		Set<char[]> intersection = null;
		int maxSize = 0;
		if(oneSize > twoSize) {
			strTwoGrams.retainAll(strOneGrams);
			intersection = strTwoGrams;
			maxSize = oneSize;
		} else {
			strOneGrams.retainAll(strTwoGrams);
			intersection = strOneGrams;
			maxSize = twoSize;
		}
		
		return ((double) intersection.size()) / maxSize;
	}
	
	private Set<char[]> getGrams(char[] str) {
		Set<char[]> grams = new TreeSet<char[]>(new Comparator<char[]>() {

			
			public int compare(char[] o1, char[] o2) {
				for(int i = 0; i < o1.length && i < o2.length; i++) {
					if(o1[i] < o2[i])
						return -1;
					else if(o1[i] > o2[i])
						return 1;
				}
				return 0;
			}
			
		});
		
		for(int i = n - 1; i < str.length; i++) {
			char[] gram = new char[n];
			for(int j = i, r = gram.length - 1; r >= 0 && j > i - n; r--, j--) {
				gram[r] = str[j];
			}
			grams.add(gram);
		}
		
		return grams;
	}
	
	public double getMatchScore(String firstString, String secondString) {
		return 	this.similarity(firstString, secondString);
	}

	/**
	 * for test
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
//		NGramSimilarity ngs = new NGramSimilarity();
		System.out.println(NGramSimilarity.TRIGRAM_SIMILARITY.getMatchScore("ruira", "wangrarui"));
		
	}

	
}
