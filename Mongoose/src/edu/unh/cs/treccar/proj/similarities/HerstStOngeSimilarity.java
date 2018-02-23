package edu.unh.cs.treccar.proj.similarities;

import java.util.ArrayList;

import edu.unh.cs.treccar.Data;
import edu.unh.cs.treccar.proj.util.ParaPair;
import edu.unh.cs.treccar.proj.util.ParaUtilities;
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
 * This class implements the HerstStOnge Semantic Similarity measure 
 * and returns the measure between two paragraphs.
 * @author Shubham Chatterjee
 *
 */

public class HerstStOngeSimilarity implements SimilarityFunction
{
	private static ArrayList<String> paraText1;
	private static ArrayList<String> paraText2;
	private static ArrayList<Double> scores;
	double score = 0.0d;
	
	/**
	 * @param pp ParaPair A ParaPair object representing a pair of paragraphs
	 * @param list ArrayList<Data.Paragraph> Conatins a list of Data.Paragraph objects 
	 * @return HerstStOnge score between two paragraphs
	 */
	
	public double simScore(ParaPair pp, ILexicalDatabase db)
	{
		paraText1 = pp.getPara1tokens();
		paraText2 = pp.getPara2tokens();
		
		score = getParaScore(paraText1, paraText2, db);
		
		return score;
	}
	
	/**
	 * 
	 * @param list1 ArrayList<String> List of words from first paragraph
	 * @param list2 ArrayList<String> List of words from second paragraph
	 * @return similarity score between two lists
	 */
	
	private static double getParaScore(ArrayList<String> list1, ArrayList<String> list2, ILexicalDatabase db)
	{
		double s = 0.0d;
		scores = new ArrayList<Double>();
		HirstStOnge hso = new HirstStOnge(db);
		for(String w1 : list1)
			for(String w2 : list2){
				if(w1.equals(w2))
					continue;
				else{
					s = hso.calcRelatednessOfWords(w1, w2);
					scores.add(s);
				}
			}
		s = findMeanScore(scores);
		return s;
	}
	
	/**
	 * 
	 * @param list ArrayList<Double> List of scores between words of two paragraphs
	 * @return mean of the score
	 */
	
	private static double findMeanScore(ArrayList<Double> list)
	{
		double total = 0.0d;
		double mean = 0.0d;
		for(double i : list )
			total += i;
		mean = total/list.size();
		return mean;
	}
}
