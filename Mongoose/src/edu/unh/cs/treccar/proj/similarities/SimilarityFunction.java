package edu.unh.cs.treccar.proj.similarities;

import java.util.ArrayList;

import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.unh.cs.treccar.Data;
import edu.unh.cs.treccar.proj.util.ParaPair;

public interface SimilarityFunction {
	public double simScore(ParaPair pp, ILexicalDatabase db);
}
