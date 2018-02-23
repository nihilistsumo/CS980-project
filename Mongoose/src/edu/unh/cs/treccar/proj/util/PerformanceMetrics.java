package edu.unh.cs.treccar.proj.util;

import java.util.ArrayList;

public class PerformanceMetrics {

	private int getParentLabel(int i, int[] parents){
		while(parents[i]!=i)
			i = parents[i];
		return i;
	}
	
	public double getAccuracy(ArrayList<ArrayList<String>> gtClusters, ArrayList<String> paraids, int[] labels){
		double score = 0;
		String p1,p2;
		int pos = 0, count = 0;
		for(int i=0; i<paraids.size()-1; i++){
			p1 = paraids.get(i);
			for(int j=i+1; j<paraids.size(); j++){
				p2 = paraids.get(j);
				if(this.getParentLabel(i, labels)==this.getParentLabel(j, labels)){
					for(ArrayList<String> cluster:gtClusters){
						if(cluster.contains(p1)){
							if(cluster.contains(p2))
								pos++;
							break;
						}
					}
				}
				else{
					for(ArrayList<String> cluster:gtClusters){
						if(cluster.contains(p1)){
							if(!cluster.contains(p2))
								pos++;
							break;
						}
					}
				}
				count++;
			}
		}
		score = (double)pos/(double)count;
		return score;
	}
	
	public double calculateRandIndex(ArrayList<ArrayList<String>> candidateClusters, ArrayList<ArrayList<String>> gtClusters){
		//candidateClusters will be map between cluster labels and cluster of para IDs
		//String resultString = "";
		//HashMap<String, ArrayList<String>> correct = this.groundTruth;
		//HashMap<AssignParagraphs.SectionPathID, ArrayList<Data.Paragraph>> candidate = this.candidateAssign;
		//String[] correctSections = new String[correct.size()];
		//String[] candLabels = new String[candidateClusters.size()];
		//correct.keySet().toArray(correctSections);
		//candidateClusters.keySet().toArray(candLabels);
		
		int[][] contingencyMatrix = new int[gtClusters.size()][candidateClusters.size()];
		double randIndex = 0.0;
		ArrayList<String> correctParas = new ArrayList<String>();
		ArrayList<String> candParas = new ArrayList<String>();
		for(int i=0; i<gtClusters.size(); i++){
			for(int j=0; j<candidateClusters.size(); j++){
				int matchCount = 0;
				correctParas = gtClusters.get(i);
				candParas = candidateClusters.get(j);
				if(correctParas == null){
					System.out.println("We have null in correctParas!");
				} else if(candParas != null){
					for(String candPara : candParas){
						if(correctParas.contains(candPara)){
							matchCount++;
						}
					}
				}
				contingencyMatrix[i][j] = matchCount;
			}
		}
		printContingencyMatrix(contingencyMatrix);
		if((new Double(this.computeRand(contingencyMatrix))).isNaN()) {
			System.out.println("Adjusted Rand index could not be computed!");
			return -99;
		}
		else {
			randIndex = this.computeRand(contingencyMatrix);
			System.out.println("Calculated RAND index: "+randIndex);
			return randIndex;
		}
	}
	
	private double computeRand(int[][] contMat){
		double score = 0.0;
		int sumnij=0, sumni=0, sumnj=0, nC2=0, nrow=0, ncol=0;		
		ncol = contMat[0].length;
		nrow = contMat.length;
		int[] njvals = new int[ncol];
		nC2 = this.nC2(ncol+nrow);
		for(int i=0; i<nrow; i++){
			int ni=0;
			for(int j=0; j<ncol; j++){
				sumnij+=this.nC2(contMat[i][j]);
				ni+=contMat[i][j];
				njvals[j]+=contMat[i][j];
			}
			sumni+=this.nC2(ni);
		}
		for(int j=0; j<njvals.length; j++){
			sumnj+=this.nC2(njvals[j]);
		}
		
		/* ################### 
		 * This code is for simple Rand index
		
		int a=0, b=0, c=0, d=0;
		a = sumnij;
		b = sumni - sumnij;
		c = sumnj - sumnij;
		d = nC2 - (a+b+c);
		score = ((double)(a+d))/(a+b+c+d);
		
		#################### */
		
		double denom = ((double)(sumni+sumnj))/2-((double)sumni*sumnj/nC2);
		double nom = (sumnij-((double)(sumni*sumnj))/nC2);
		System.out.println("sumnij: "+sumnij+", sumni: "+sumni+", sumnj: "+sumnj+", nC2: "+nC2+", nom: "+nom+", denom: "+denom);
		score = nom/denom;
		return score;
	}
	
	private int nC2(int n){
		if(n<2) return 0;
		else if(n==2) return 1;
		else{
			return n*(n-1)/2;
		}
	}
	
	private void printContingencyMatrix(int[][] contingency){
		int colNum = contingency[0].length;
		int rowNum = contingency.length;
		for(int i=0; i<rowNum; i++){
			for(int j=0; j<colNum; j++){
				System.out.print(contingency[i][j]+" ");
			}
			System.out.println();
		}
	}
}
