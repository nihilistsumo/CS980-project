package edu.unh.cs.treccar.proj.similarities;

import java.util.ArrayList;

import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.unh.cs.treccar.proj.util.*;
import edu.unh.cs.treccar.Data;

public class JaccardSimilarity implements SimilarityFunction
{
	double score = 0.0d;
	
	/**
	 * @param pp ParaPair A ParaPair object representing a pair of paragraphs
	 * @param list ArrayList<Data.Paragraph> Conatins a list of Data.Paragraph objects 
	 * @return jaccard score between two paragraphs
	 */
	
	public double simScore(ParaPair pp, ILexicalDatabase db)
	{
		double D = new DiceSimilarity().simScore(pp, db);
		score = D/(2 - D);
		
		return score;
	}
}
	
	
