package edu.unh.cs.treccar.proj.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;

import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
import edu.unh.cs.treccar.proj.similarities.HerstStOngeSimilarity;
import edu.unh.cs.treccar.proj.similarities.JiangConrathSimilarity;
import edu.unh.cs.treccar.proj.similarities.LeacockChodorowSimilarity;
import edu.unh.cs.treccar.proj.similarities.LeskSimilarity;
import edu.unh.cs.treccar.proj.similarities.LinSimilarity;
import edu.unh.cs.treccar.proj.similarities.PathSimilarity;
import edu.unh.cs.treccar.proj.similarities.ResnikSimilarity;
import edu.unh.cs.treccar.proj.similarities.SimilarityCalculator;
import edu.unh.cs.treccar.proj.similarities.SimilarityFunction;
import edu.unh.cs.treccar.proj.similarities.WuPalmerSimilarity;
import edu.unh.cs.treccar_v2.Data;

public class MongooseHelper {
	Properties p;
	HashMap<String, Data.Paragraph> parasMap;
	HashMap<String, ArrayList<String>> preprocessedParasMap;
	//HashMap<String, ArrayList<String>> reducedParasMap;
	SimilarityCalculator sc;
	
	public MongooseHelper(Properties pr) {
		// TODO Auto-generated constructor stub
		this.p = pr;
		this.parasMap = DataUtilities.getParaMapFromPath(pr.getProperty("data-dir")+"/"+pr.getProperty("parafile"));
		this.preprocessedParasMap = DataUtilities.getPreprocessedParaMap(parasMap);
		//this.reducedParasMap = DataUtilities.getReducedParaMap(preprocessedParasMap);
		this.sc = new SimilarityCalculator();
	}
	
	public void runClustering(Properties p){
		HashMap<String, ArrayList<String>> pageSecMap = DataUtilities.getArticleSecMap(p.getProperty("outline"));
	}
	
	public HashMap<String, ArrayList<ParaPairData>> processParaPairData(
			HashMap<String, ArrayList<String>> pageParasMap) throws IOException, ParseException{
		HashMap<String, ArrayList<ParaPairData>> allPagesData = new HashMap<String, ArrayList<ParaPairData>>();
		ILexicalDatabase db = new NictWordNet();
		int i=0;
		int n=pageParasMap.keySet().size();
		IndexSearcher is = new IndexSearcher(DirectoryReader.open(FSDirectory.open((new File(this.p.getProperty("index-dir")).toPath()))));
		Analyzer analyzer = new StandardAnalyzer();
		QueryParser qp = new QueryParser("paraid", analyzer);
		File logOut = new File(this.p.getProperty("out-dir")+"/"+this.p.getProperty("log-file"));
		for(String pageID:pageParasMap.keySet()){
			ArrayList<String> paraIDs = pageParasMap.get(pageID);
			//ArrayList<String> secIDs = this.pageSecMap.get(pageID);
			//ArrayList<Data.Paragraph> paras = new ArrayList<Data.Paragraph>();
			System.out.println("Page ID: "+pageID+", "+paraIDs.size()+" paras");
			BufferedWriter bw = new BufferedWriter(new FileWriter(logOut));
			bw.append(pageID+" has started with "+paraIDs.size()+" paras, "+(n-i-1)+" to go after this\n");
			bw.close();
			/*
			for(String paraID:paraIDs)
				paras.add(this.parasMap.get(paraID));
			*/
			
			//Expensive op
			ArrayList<ParaPairData> data = this.getParaPairData(paraIDs, db, is, qp, analyzer);
			//
			
			allPagesData.put(pageID, data);
			i++;
			System.out.println(pageID+" is done, "+(n-i)+" to go");
			//bw.append(" is done, "+(n-i)+" to go");
			//bw.close();
			//System.out.println(data.size());
		}
		return allPagesData;
	}
	
	public ArrayList<ParaPairData> getParaPairData(ArrayList<String> paraIDList, ILexicalDatabase db, 
			IndexSearcher is, QueryParser qp, Analyzer a) throws IOException, ParseException{
		ArrayList<ParaPairData> pairData = new ArrayList<ParaPairData>();
		Document paradoc1, paradoc2;
		ArrayList<String> para1tokens, para2tokens;
		for(int i=0; i<paraIDList.size()-1; i++){
			for(int j=i+1; j<paraIDList.size(); j++){ 
				String pid1 = paraIDList.get(i);
				String pid2 = paraIDList.get(j);
				paradoc1 = is.doc(is.search(qp.parse(pid1), 1).scoreDocs[0].doc);
				paradoc2 = is.doc(is.search(qp.parse(pid2), 1).scoreDocs[0].doc);
				para1tokens = tokenizeString(a, paradoc1.get("parabody"));
				para2tokens = tokenizeString(a, paradoc2.get("parabody"));
				//ParaPair pp = new ParaPair(pid1, pid2, this.preprocessedParasMap.get(pid1), this.preprocessedParasMap.get(pid2));
				ParaPair pp = new ParaPair(pid1, pid2, para1tokens, para2tokens);
				//ArrayList<Double> scores = this.computeScores(3);
				ArrayList<Double> scores = this.sc.computeScores(pp, this.p.getProperty("func"));
				//System.out.println(scores);
				ParaPairData ppd = new ParaPairData(pp, scores);
				pairData.add(ppd);
			}
		}
		return pairData;
	}
	
	public static ArrayList<String> tokenizeString(Analyzer analyzer, String string) {
	    ArrayList<String> result = new ArrayList<String>();
	    try {
	    	TokenStream stream  = analyzer.tokenStream(null, new StringReader(string));
	    	stream.reset();
	    	while (stream.incrementToken()) {
	    		result.add(stream.getAttribute(CharTermAttribute.class).toString());
	    	}
	    	stream.close();
	    } catch (IOException e) {
	    	// not thrown b/c we're using a string reader...
	    	throw new RuntimeException(e);
	    }
	    return result;
	}
	
	public void saveParaSimilarityData(HashMap<String, ArrayList<ParaPairData>> allPagesData, String filePath){
		try {
			FileOutputStream fos = new FileOutputStream(filePath);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(allPagesData);
			fos.close();
			oos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
