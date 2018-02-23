package edu.unh.cs.treccar.proj.cluster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;

import edu.unh.cs.treccar.Data;
import edu.unh.cs.treccar.proj.similarities.SimilarityFunction;
import edu.unh.cs.treccar.proj.util.ParaPairData;

public class CustomHAC {
	public HashMap<String, ArrayList<String>> clusterData;
	public HashMap<HashSet<String>, ArrayList<Double>> clusterPairData;
	public Properties prop;
	public String pageID;
	public int secNo;
	ArrayList<Data.Paragraph> paraList;
	ArrayList<ParaPairData> ppds;
	ArrayList<String> secids;
	double[] wVec;
	
	public CustomHAC(Properties p, String pID, double[] w, ArrayList<SimilarityFunction> funcs, 
			ArrayList<String> sectionIDs, ArrayList<Data.Paragraph> paras, ArrayList<ParaPairData> ppdList){
		this.prop = p;
		this.pageID = pID;
		this.secNo = sectionIDs.size();
		this.paraList = paras;
		this.ppds = ppdList;
		this.wVec = w;
		this.secids = sectionIDs;
	}

	public HashMap<String, ArrayList<String>> cluster(){
		
		ArrayList<Data.Paragraph> paras = this.paraList;
		ArrayList<ParaPairData> ppdList = this.ppds;
		double[] optw = this.wVec;
		ArrayList<String> secIDs = this.secids;
		// Initialization //
		HashMap<String, ArrayList<String>> clusters = new HashMap<String, ArrayList<String>>();
		HashMap<HashSet<String>, ArrayList<Double>> clusterPairData = new HashMap<HashSet<String>, ArrayList<Double>>();
		int noClusters = 0;
		Integer clusterID = 1;
		for(Data.Paragraph p:paras){
			ArrayList<String> paraList = new ArrayList<String>();
			paraList.add(p.getParaId());
			clusters.put("c"+clusterID, paraList);
			clusterID++;
		}
		noClusters = clusters.size();
		for(ParaPairData ppd:ppdList){
			String p1 = ppd.getParaPair().getPara1();
			String p2 = ppd.getParaPair().getPara2();
			String c1 = "", c2 = "";
			for(String c:clusters.keySet()){
				if(p1.equals(clusters.get(c).get(0))){
					c1 = c;
					break;
				}
			}
			for(String c:clusters.keySet()){
				if(p2.equals(clusters.get(c).get(0))){
					c2 = c;
					break;
				}
			}
			HashSet<String> clusterPairKey = new HashSet<String>();
			clusterPairKey.add(c1);
			clusterPairKey.add(c2);
			clusterPairData.put(clusterPairKey, ppd.getSimScoreList());
		}
		// ------------ //
		
		boolean isConverged = false;
		while(noClusters>this.secNo){
			HashSet<String> clusterPairMax = null;
			double maxScore = -Double.MAX_VALUE;
			for(HashSet<String> cp:clusterPairData.keySet()){
				double score = 0.0;
				for(int i=0; i<optw.length; i++)
					score+=clusterPairData.get(cp).get(i)*optw[i];
				if(score>maxScore){
					clusterPairMax = cp;
					maxScore = score;
				}
			}
			Iterator<String> it = clusterPairMax.iterator();
			String mergeC1 = it.next();
			String mergeC2 = it.next();
			String mergedC = mergeC1+mergeC2;
			
			// merge two clusters mergeC1 and mergeC2
			for(String cid:clusters.keySet()){
				if(cid.equals(mergeC1) || cid.equals(mergeC2))
					continue;
				HashSet<String> cxz = new HashSet<String>();
				HashSet<String> cyz = new HashSet<String>();
				HashSet<String> cxyz = new HashSet<String>();
				cxz.add(mergeC1);cxz.add(cid);
				cyz.add(mergeC2);cyz.add(cid);
				cxyz.add(mergedC); cxyz.add(cid);
				clusterPairData.put(cxyz, this.avg(clusterPairData.get(cxz), clusterPairData.get(cyz)));
				clusterPairData.remove(cxz);
				clusterPairData.remove(cyz);
			}
			HashSet<String> cxy = new HashSet<String>();
			cxy.add(mergeC1);cxy.add(mergeC2);
			clusterPairData.remove(cxy);
			
			ArrayList<String> mergedParas = clusters.get(mergeC1);
			mergedParas.addAll(clusters.get(mergeC2));
			clusters.put(mergedC, mergedParas);
			clusters.remove(mergeC1);
			clusters.remove(mergeC2);
			
			noClusters--;
		}
		
		return clusters;
	}
	
	public ArrayList<Double> avg(ArrayList<Double> s1, ArrayList<Double> s2){
		ArrayList<Double> r = new ArrayList<Double>();
		for(int i=0; i<s1.size(); i++)
			r.add(0.0);
		for(int i=0; i<s1.size(); i++)
			r.set(i, (s1.get(i)+s2.get(i))/2);
		return r;
	}
	
	public boolean checkConverged(){
		return false;
	}
}
