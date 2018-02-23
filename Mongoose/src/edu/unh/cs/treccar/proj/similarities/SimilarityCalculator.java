package edu.unh.cs.treccar.proj.similarities;

import java.util.ArrayList;

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
import edu.unh.cs.treccar.proj.util.ParaPair;

public class SimilarityCalculator {
	
	ILexicalDatabase db;
	HirstStOnge hso;
	JiangConrath ji;
	LeacockChodorow lea;
	Lesk les;
	Lin lin;
	Path pat;
	Resnik res;
	WuPalmer wu;
	
	static final double MAX_SCORE = 10000.0; 
	//ji lea les lin pat res wu
	public SimilarityCalculator(){
		db = new NictWordNet();
		hso = new HirstStOnge(db);
		ji = new JiangConrath(db);
		lea = new LeacockChodorow(db);
		les = new Lesk(db);
		lin = new Lin(db);
		pat = new Path(db);
		res = new Resnik(db);
		wu = new WuPalmer(db);
	}
	
	public ArrayList<Double> computeScores(ParaPair pp, String funcs){
		ArrayList<Double> scores = new ArrayList<Double>();
		int fCount = 0;
		for(String f:funcs.split(" ")){
			double simScore = 0.0, currScore = 0.0;
			int n = 0;
			RelatednessCalculator rfunc = null;
			switch(f){
			case "hso":
				rfunc = this.hso;
				break;
			case "ji":
				rfunc = this.ji;
				break;
			case "lea":
				rfunc = this.lea;
				break;
			case "les":
				rfunc = this.les;
				break;
			case "lin":
				rfunc = this.lin;
				break;
			case "pat":
				rfunc = this.pat;
				break;
			case "res":
				rfunc = this.res;
				break;
			case "wu":
				rfunc = this.wu;
				break;
			}
			for(String token1:pp.getPara1tokens()){
				for(String token2:pp.getPara2tokens()){
					currScore = rfunc.calcRelatednessOfWords(token1, token2);
					if(currScore<SimilarityCalculator.MAX_SCORE){
						simScore+=currScore;
						n++;
					}
				}
			}
			simScore = simScore/n;
			scores.add(fCount, simScore);
			fCount++;
		}
		return scores;
	}

}
