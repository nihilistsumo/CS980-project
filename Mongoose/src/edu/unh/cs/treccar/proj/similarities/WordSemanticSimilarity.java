package edu.unh.cs.treccar.proj.similarities;

import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.ws4j.RelatednessCalculator;
import edu.cmu.lti.ws4j.impl.HirstStOnge;
import edu.cmu.lti.ws4j.impl.JiangConrath;
import edu.cmu.lti.ws4j.impl.LeacockChodorow;
import edu.cmu.lti.ws4j.impl.Lesk;
import edu.cmu.lti.ws4j.impl.Lin;
import edu.cmu.lti.ws4j.impl.Path;
import edu.cmu.lti.ws4j.impl.Resnik;
import edu.cmu.lti.ws4j.impl.WuPalmer;
import edu.cmu.lti.ws4j.util.WS4JConfiguration;

/** 
 * This class implements the semantic similarity between two words using the predefined libraries 
 * from the WordNet API for Java called WS4J. 
 * 
 * @author Shubham Chatterjee
 *
 */

public class WordSemanticSimilarity 
{
	private static ILexicalDatabase db = new NictWordNet();
	private static RelatednessCalculator rc;
	
	/**
	 * 
	 * @param word1 String Holds the first word
	 * @param word2 String Holds the second word
	 * @param method String Hold the method to be used for finding the similarity measure
	 * @return A similarity score between two words based on the given method
	 */
	public static double findSimilarity( String word1, String word2, String method ) 
	{
		WS4JConfiguration.getInstance().setMFS(true);
		switch(method)
		{
			case "hso"	: rc = new HirstStOnge(db);
						  return (rc.calcRelatednessOfWords(word1, word2));
						  
			case "lc" 	: rc = new LeacockChodorow(db);
						  return (rc.calcRelatednessOfWords(word1, word2));
						  
			case "lesk"	: rc = new Lesk(db);
						  return (rc.calcRelatednessOfWords(word1, word2));
						  
			case "wp"	: rc = new WuPalmer(db);
						  return (rc.calcRelatednessOfWords(word1, word2));
						  
			case "res"	: rc = new Resnik(db);
						  return (rc.calcRelatednessOfWords(word1, word2));
						  
			case "jc"	: rc = new JiangConrath(db);
						  return (rc.calcRelatednessOfWords(word1, word2));
						  
			case "lin"	: rc = new Lin(db);
						  return (rc.calcRelatednessOfWords(word1, word2));
						  
			case "path"	: rc = new Path(db);
						  return (rc.calcRelatednessOfWords(word1, word2));
						  
			default		: return 0;
		}
	}
	public static void main(String[] args) 
	{
		
		double s = findSimilarity( "There ","During ","hso" );
		System.out.println( "score="+s );
	}
}


