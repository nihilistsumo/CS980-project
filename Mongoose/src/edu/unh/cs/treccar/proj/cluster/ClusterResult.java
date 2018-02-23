package edu.unh.cs.treccar.proj.cluster;

import java.util.ArrayList;
import java.util.HashMap;

import edu.unh.cs.treccar.Data;

public class ClusterResult {
	private int[] parents;
	private String pageID;
	private ArrayList<String> sectionIDs;
	private ArrayList<Data.Paragraph> paras;
	
	public ClusterResult(int[] p, String page, ArrayList<String> secIDs,
			ArrayList<Data.Paragraph> paras){
		this.parents = p;
		this.pageID = page;
		this.sectionIDs = secIDs;
		this.paras = paras;
	}

	public int[] getParents() {
		return parents;
	}

	public String getPageID() {
		return pageID;
	}

	public ArrayList<String> getSectionIDs() {
		return sectionIDs;
	}

	public ArrayList<Data.Paragraph> getParas() {
		return paras;
	}
	
	private int getParent(int i, int[] parents){
		while(parents[i]!=i)
			i = parents[i];
		return i;
	}
	
	public ArrayList<ArrayList<String>> getParaClusters(){
		ArrayList<ArrayList<String>> clusters = new ArrayList<ArrayList<String>>();
		HashMap<Integer, ArrayList<String>> clustersMap = new HashMap<Integer, ArrayList<String>>();
		String pid;
		int currParent = 0;
		for(int i=0; i<paras.size(); i++){
			pid = paras.get(i).getParaId();
			currParent = this.getParent(i, this.parents);
			if(clustersMap.keySet().contains(currParent))
				clustersMap.get(currParent).add(pid);
			else{
				ArrayList<String> newList = new ArrayList<String>();
				newList.add(pid);
				clustersMap.put(currParent, newList);
			}
		}
		for(int parent:clustersMap.keySet())
			clusters.add(clustersMap.get(parent));
		return clusters;
	}
}