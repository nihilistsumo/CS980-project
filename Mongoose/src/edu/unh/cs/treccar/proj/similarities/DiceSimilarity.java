package edu.unh.cs.treccar.proj.similarities;

import java.util.ArrayList;

import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.unh.cs.treccar.proj.util.*;
import edu.unh.cs.treccar.Data;

public class DiceSimilarity implements SimilarityFunction
{
	private static ArrayList<String> paraText1;
	private static ArrayList<String> paraText2;
	private static ArrayList<Double> scores;
	double score = 0.0d;
	
	/**
	 * @param pp ParaPair A ParaPair object representing a pair of paragraphs
	 * @param list ArrayList<Data.Paragraph> Conatins a list of Data.Paragraph objects 
	 * @return dice score between two paragraphs
	 */
	
	public double simScore(ParaPair pp, ILexicalDatabase db)
	{
		paraText1 = pp.getPara1tokens();
		paraText2 = pp.getPara2tokens();
		int c = ParaUtilities.findIntersection(paraText1, paraText2).size();
		score = (double)(2*c)/(double)(paraText1.size() + paraText2.size());
		
		return score;
	}
	
	

}


