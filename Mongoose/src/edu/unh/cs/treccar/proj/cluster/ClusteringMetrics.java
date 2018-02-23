package edu.unh.cs.treccar.proj.cluster;

import java.util.ArrayList;
import java.util.HashMap;

public class ClusteringMetrics {
	ArrayList<ArrayList<String>> trueClusters;
	ArrayList<ArrayList<String>> candidateClusters;
	ArrayList<String> paraIDs;
	HashMap<String, String> trueParaClMap;
	HashMap<String, String> candidateParaClMap;
	boolean detailed;

	public ClusteringMetrics(ArrayList<ArrayList<String>> trueC, ArrayList<ArrayList<String>> candC, boolean printDetails){
		this.trueClusters = trueC;
		this.candidateClusters = candC;
		this.paraIDs = new ArrayList<String>();
		for(ArrayList<String> c:trueC){
			for(String p:c){
				if(!paraIDs.contains(p))
					paraIDs.add(p);
			}
		}
		this.trueParaClMap = new HashMap<String, String>();
		this.candidateParaClMap = new HashMap<String, String>();
		for(String pid:this.paraIDs){
			for(int i=0;i<trueClusters.size();i++){
				if(trueClusters.get(i).contains(pid))
					trueParaClMap.put(pid, "t"+i);
			}
			for(int i=0;i<candidateClusters.size();i++){
				if(candidateClusters.get(i).contains(pid))
					candidateParaClMap.put(pid, "c"+i);
			}
		}
		this.detailed = printDetails;
	}
	
	public double getAdjRAND(){
		double rand = this.calculateRandIndex(candidateClusters, trueClusters);
		return rand;
	}
	
	public double bCubed(){
		double b = 0.0;
		
		return b;
	}
	
	public double fMeasure(){
		double f = 0.0;
		double[] precRec = pairPrecisionRecall();
		if(!(precRec[0]==0 && precRec[1]==0))
			f = 2*precRec[0]*precRec[1]/(precRec[0]+precRec[1]);
		return f;
	}
	
	public double[] pairPrecisionRecall(){
		//pr = {prec, rec}
		double[] pr = {0.0, 0.0};
		int[] stats = getStats();
		if(!(stats[3]==0 && stats[0]==0))
			pr[0] = (double)stats[3]/((double)(stats[3]+stats[0]));
		if(!(stats[3]==0 && stats[1]==0))
			pr[1] = (double)stats[3]/((double)(stats[3]+stats[1]));
		return pr;
	}
	
	public int[] getStats(){
		// stats[4] = {tp, tn, fp, fn}
		int[] stats = {0,0,0,0};
		String p1, p2;
		if(trueParaClMap.keySet().containsAll(paraIDs) && 
				paraIDs.containsAll(trueParaClMap.keySet()) &&
				candidateParaClMap.keySet().containsAll(paraIDs) &&
				paraIDs.containsAll(candidateParaClMap.keySet())){
			for(int i=0; i<this.paraIDs.size()-1; i++){
				for(int j=i+1; j<this.paraIDs.size(); j++){
					p1 = this.paraIDs.get(i);
					p2 = this.paraIDs.get(j);
					if(this.trueParaClMap.get(p1).equalsIgnoreCase(this.trueParaClMap.get(p2))){
						//same cluster in true
						if(this.candidateParaClMap.get(p1).equalsIgnoreCase(this.candidateParaClMap.get(p2))){
							//same cluster in cand
							stats[3]++;
						}
						else{
							//diff cluster in cand  
							stats[0]++;
						}
					}
					else{
						//diff cluster in true
						if(this.candidateParaClMap.get(p1).equalsIgnoreCase(this.candidateParaClMap.get(p2))){
							//same cluster in cand
							stats[1]++;
						}
						else{
							//diff cluster in cand
							stats[2]++;
						}
					}
				}
			}
		}
		else{
			System.out.println("Page does not contain all paras, ignoring it with output 0 for F1");
		}
		return stats;
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
		if(this.detailed)
			printContingencyMatrix(contingencyMatrix);
		if((new Double(this.computeRand(contingencyMatrix))).isNaN()) {
			System.out.println("Adjusted Rand index could not be computed!");
			return 0.0;
		}
		else {
			randIndex = this.computeRand(contingencyMatrix);
			//System.out.println("Calculated RAND index: "+randIndex);
			return randIndex;
		}
	}
	
	private double computeRand(int[][] contMat){
		double score = 0.0;
		int sumnij=0, sumni=0, sumnj=0, n=0, nC2=0, nrow=0, ncol=0;		
		ncol = contMat[0].length;
		nrow = contMat.length;
		int[] njvals = new int[ncol];
		for(int i=0; i<nrow; i++){
			int ni=0;
			for(int j=0; j<ncol; j++){
				sumnij+=this.nC2(contMat[i][j]);
				ni+=contMat[i][j];
				njvals[j]+=contMat[i][j];
				n+=contMat[i][j];
			}
			sumni+=this.nC2(ni);
		}
		for(int j=0; j<njvals.length; j++){
			sumnj+=this.nC2(njvals[j]);
		}
		nC2 = this.nC2(n);
		
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
		if(detailed)
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
