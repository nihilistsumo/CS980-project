package edu.unh.cs.treccar.proj.similarities;

import java.util.Vector;

import edu.cmu.lti.ws4j.RelatednessCalculator;

public class WordRelatednessThread implements Runnable{

	RelatednessCalculator rc;
	Vector<Double> scores;
	String word1, word2;
	
	public WordRelatednessThread(RelatednessCalculator r, Vector<Double> scoreList, String w1, String w2) {
		// TODO Auto-generated constructor stub
		this.rc = r;
		this.scores = scoreList;
		this.word1 = w1;
		this.word2 = w2;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		this.scores.add(this.rc.calcRelatednessOfWords(word1, word2));
	}

}
