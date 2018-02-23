package edu.unh.cs.treccar.proj.similarities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.apache.pivot.collections.HashSet;

import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.ws4j.impl.HirstStOnge;
import edu.cmu.lti.ws4j.impl.JiangConrath;
import edu.cmu.lti.ws4j.impl.LeacockChodorow;
import edu.cmu.lti.ws4j.impl.Lesk;
import edu.cmu.lti.ws4j.impl.Lin;
import edu.cmu.lti.ws4j.impl.Path;
import edu.cmu.lti.ws4j.impl.Resnik;
import edu.cmu.lti.ws4j.impl.WuPalmer;
import edu.unh.cs.treccar.proj.util.DataUtilities;
import edu.unh.cs.treccar_v2.Data;

public class SimilarityComparator {
	
	ILexicalDatabase db;
	HirstStOnge hso;
	JiangConrath ji;
	LeacockChodorow lea;
	Lesk les;
	Lin lin;
	Path pat;
	Resnik res;
	WuPalmer wu;
	HashMap<String, Data.Paragraph> parasMap;
	HashMap<String, ArrayList<String>> preprocessedParasMap;
	
	public SimilarityComparator(String parafilePath){
		db = new NictWordNet();
		hso = new HirstStOnge(db);
		ji = new JiangConrath(db);
		lea = new LeacockChodorow(db);
		les = new Lesk(db);
		lin = new Lin(db);
		pat = new Path(db);
		res = new Resnik(db);
		wu = new WuPalmer(db);
		this.parasMap = DataUtilities.getParaMapFromPath(parafilePath);
		this.preprocessedParasMap = DataUtilities.getPreprocessedParaMap(parasMap);
	}
	
	public void printData(String outfilePath) throws IOException{
		ArrayList<String> paraTokens1, paraTokens2;
		String pid1, pid2;
		int count = 0, x, y;
		HashSet<String> calculatedSims = new HashSet<String>();
		Random rand = new Random();
		ArrayList<String> pids = new ArrayList<String>();
		for(String pid:this.preprocessedParasMap.keySet())
			pids.add(pid);
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(outfilePath)));
		bw.write("Token1 Token2 ji lea les lin pat res wu\n");
		for(int i=0; i<3; i++){
			String output;
			x = rand.nextInt(100);
			y = rand.nextInt(100);
			pid1 = pids.get(x);
			pid2 = pids.get(y);
			paraTokens1 = this.preprocessedParasMap.get(pid1);
			paraTokens2 = this.preprocessedParasMap.get(pid2);
			for(String token1:paraTokens1){
				for(String token2:paraTokens2){
					if(token1.equalsIgnoreCase(token2))
						continue;
					if(calculatedSims.contains(token1+" "+token2) ||
							calculatedSims.contains(token2+" "+token1))
						continue;
					output = "";
					output+=token1+" "+token2+" ";
					output+=ji.calcRelatednessOfWords(token1, token2)+" "+
					lea.calcRelatednessOfWords(token1, token2)+" "+
					les.calcRelatednessOfWords(token1, token2)+" "+
					lin.calcRelatednessOfWords(token1, token2)+" "+
					pat.calcRelatednessOfWords(token1, token2)+" "+
					res.calcRelatednessOfWords(token1, token2)+" "+
					wu.calcRelatednessOfWords(token1, token2);
					bw.write(output+"\n");
					calculatedSims.add(token1+" "+token2);
				}
			}
		}
		bw.close();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SimilarityComparator scmp = new SimilarityComparator("/home/sumanta/Documents/new_research/unh/benchmarkY1/benchmarkY1-train/fold-0-train.pages.cbor-paragraphs.cbor");
		try {
			scmp.printData("/home/sumanta/Documents/new_research/unh/mongoose-results/wordsim-data4");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
