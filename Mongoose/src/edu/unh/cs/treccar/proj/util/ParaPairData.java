package edu.unh.cs.treccar.proj.util;

import java.io.Serializable;
import java.util.ArrayList;

public class ParaPairData implements Serializable {
	private ParaPair paraPair;
	private ArrayList<Double> simScoreList;
	
	public ParaPairData(ParaPair pair, ArrayList<Double> scores){
		this.paraPair = pair;
		this.simScoreList = scores;
	}
	
	public ParaPair getParaPair() {
		return paraPair;
	}
	public void setParaPair(ParaPair wordPair) {
		this.paraPair = wordPair;
	}
	public ArrayList<Double> getSimScoreList() {
		return simScoreList;
	}
	public void setSimScoreList(ArrayList<Double> simScoreList) {
		this.simScoreList = simScoreList;
	}
}
