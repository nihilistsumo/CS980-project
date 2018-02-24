package edu.unh.cs.treccar.proj.similarities;

import java.util.ArrayList;
import java.util.Properties;
import java.util.Vector;

import edu.cmu.lti.ws4j.RelatednessCalculator;
import edu.unh.cs.treccar.proj.util.ParaPair;
import edu.unh.cs.treccar.proj.util.ParaPairData;

public class SimilarityCalculatorThread implements Runnable{

	Properties p;
	private ParaPair pp;
	private String funcs;
	private SimilarityCalculator sc;
	private Vector<ParaPairData> ppdVec;
	
	public SimilarityCalculatorThread(Properties pr, ParaPair parapair, String flist, SimilarityCalculator scalc, Vector<ParaPairData> ppdList) {
		// TODO Auto-generated constructor stub
		this.p = pr;
		this.pp = parapair;
		this.funcs = flist;
		this.sc = scalc;
		this.ppdVec = ppdList;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		ArrayList<Double> scores = new ArrayList<Double>();
		int fCount = 0;
		for(String f:funcs.split(" ")){
			double simScore = 0.0, currScore = 0.0;
			int n = 0;
			RelatednessCalculator rfunc = null;
			switch(f){
			case "hso":
				rfunc = this.sc.hso;
				break;
			case "ji":
				rfunc = this.sc.ji;
				break;
			case "lea":
				rfunc = this.sc.lea;
				break;
			case "les":
				rfunc = this.sc.les;
				break;
			case "lin":
				rfunc = this.sc.lin;
				break;
			case "pat":
				rfunc = this.sc.pat;
				break;
			case "res":
				rfunc = this.sc.res;
				break;
			case "wu":
				rfunc = this.sc.wu;
				break;
			}
			for(String token1:this.pp.getPara1tokens()){
				for(String token2:this.pp.getPara2tokens()){
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
		if(this.p.getProperty("show-msg").equalsIgnoreCase("yes")||this.p.getProperty("show-msg").equalsIgnoreCase("y"))
			System.out.println("ParaPair "+this.pp.getPara1()+this.pp.getPara2()+" done");
		ParaPairData ppd = new ParaPairData(this.pp, scores);
		this.ppdVec.add(ppd);
	}

}
