package edu.unh.cs.treccar.proj.similarities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import edu.unh.cs.treccar.proj.util.ParaPair;
import edu.unh.cs.treccar.proj.util.ParaPairData;

public class SimilarityRunFileWriter {
	
	HashMap<String, ArrayList<ParaPairData>> allPagesData;
	int ppdScoreIndex;
	String outputRunfile, runComment;
	
	public SimilarityRunFileWriter(String datafile, String runfile, int scoreIndex, String comment){
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(datafile)));
			this.allPagesData = (HashMap<String, ArrayList<ParaPairData>>)ois.readObject();
			this.ppdScoreIndex = scoreIndex;
			this.outputRunfile = runfile;
			this.runComment = comment;
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void printRunFile() throws IOException{
		ArrayList<ParaPairData> ppdList;
		ParaPair pp;
		double simScore;
		HashSet<ParaPair> ppSet = new HashSet<ParaPair>();
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(this.outputRunfile)));
		for(String page:this.allPagesData.keySet()){
			ppdList = this.allPagesData.get(page);
			for(ParaPairData ppd:ppdList){
				pp = ppd.getParaPair();
				if(ppSet.contains(pp))
					continue;
				simScore = ppd.getSimScoreList().get(this.ppdScoreIndex);
				bw.write(pp.getPara1()+" Q0 "+pp.getPara2()+" 0 "+simScore+" "+this.runComment+"\n");
				ppSet.add(pp);
			}
		}
		bw.close();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String outdir = "/home/sumanta/Documents/new_research/unh/mongoose-results/simdata/ji-lin";
		SimilarityRunFileWriter srfw = new SimilarityRunFileWriter(outdir+"/simdata-fold4",
		outdir+"/simdata-fold4-lin-run", 1, "LIN");
		try {
			srfw.printRunFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Done");
	}

}
