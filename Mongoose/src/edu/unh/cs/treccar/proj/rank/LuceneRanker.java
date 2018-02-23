package edu.unh.cs.treccar.proj.rank;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.BasicStats;
import org.apache.lucene.search.similarities.SimilarityBase;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import co.nstant.in.cbor.CborException;
import edu.unh.cs.treccar_v2.Data;
import edu.unh.cs.treccar_v2.read_data.DeserializeData;

public class LuceneRanker {

	static String INDEX_DIR;
	static String CBOR_PARA_FILE;
	static String CBOR_OUTLINE_FILE;
	static String TRECEVAL_RUN;
	static int RET_NO;
	static String DO_INDEX;
	BufferedWriter runFileWriter;

	private IndexSearcher is = null;
	private QueryParser qp = null;
	private boolean customScore = false;
	
	public LuceneRanker(String ind, String para, String out, String run, int retN, String i){
		INDEX_DIR = ind;
		CBOR_PARA_FILE = para;
		CBOR_OUTLINE_FILE = out;
		TRECEVAL_RUN = run;
		RET_NO = retN;
		DO_INDEX = i;
		FileWriter fw;
		try {
			fw = new FileWriter(new File(TRECEVAL_RUN));
			runFileWriter = new BufferedWriter(fw);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void doRanking(){
		try {
			if(DO_INDEX.equalsIgnoreCase("i") || DO_INDEX.equalsIgnoreCase("index"))
				this.indexAllParas();
			this.searchPages();
			
			this.closeAll();
	
		} catch (CborException | IOException e) {
			e.printStackTrace();
		}
	}

	private void indexAllParas() throws CborException, IOException {
		Directory indexdir = FSDirectory.open((new File(INDEX_DIR)).toPath());
		IndexWriterConfig conf = new IndexWriterConfig(new StandardAnalyzer());
		conf.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
		IndexWriter iw = new IndexWriter(indexdir, conf);
		
		FileInputStream fis = new FileInputStream(new File(CBOR_PARA_FILE));
		final Iterator<Data.Paragraph> paragraphIterator = DeserializeData.iterParagraphs(fis);
		for (int i=1; paragraphIterator.hasNext(); i++) {
			Data.Paragraph p = paragraphIterator.next();
			this.indexPara(iw, p);
			System.out.println(p.getParaId());
		}
		iw.close();
		
		System.out.println("\n" + StringUtils.repeat("=", 128) + "\n");
	}

	private void indexPara(IndexWriter iw, Data.Paragraph para) throws IOException {
		Document paradoc = new Document();
		paradoc.add(new StringField("paraid", para.getParaId(), Field.Store.YES));
		paradoc.add(new TextField("parabody", para.getTextOnly(), Field.Store.YES));
		iw.addDocument(paradoc);
	}

	private HashMap<String, Float> doSearch(String qstring, int n) throws IOException, ParseException {
		HashMap<String, Float> retrievedResult = new HashMap<String, Float>();
		Query q;
		TopDocs tds;
		ScoreDoc[] retDocs;
		Document d;
		if (is == null) {
			is = new IndexSearcher(DirectoryReader.open(FSDirectory.open((new File(INDEX_DIR).toPath()))));
			// here you set the similarity for the searcher
			is.setSimilarity(new BM25Similarity());
		}

		if (customScore) {
			SimilarityBase mySimiliarity = new SimilarityBase() {
				protected float score(BasicStats stats, float freq, float docLen) {
					return freq;
				}

				@Override
				public String toString() {
					return null;
				}
			};
			is.setSimilarity(mySimiliarity);
		}

		/*
		 * The first arg of QueryParser constructor specifies which field of document to
		 * match with query, here we want to search in the para text, so we chose
		 * parabody.
		 * 
		 */
		if (qp == null) {
			qp = new QueryParser("parabody", new StandardAnalyzer());
		}

		System.out.println("Query: " + qstring);
		q = qp.parse(qstring);
		tds = is.search(q, n);
		retDocs = tds.scoreDocs;
		
		for (int i = 0; i < retDocs.length; i++) {
			d = is.doc(retDocs[i].doc);
			//System.out.println("Doc " + i);
			//System.out.println("Score " + tds.scoreDocs[i].score);
			//System.out.println(d.getField("paraid").stringValue());
			//System.out.println(d.getField("parabody").stringValue() + "\n");
			retrievedResult.put(d.getField("paraid").stringValue(), tds.scoreDocs[i].score);
		}
		return retrievedResult;
	}

	public void customScore(boolean custom) throws IOException {
		customScore = custom;
	}
	
	private void searchPages(){
		try {
			FileInputStream fis = new FileInputStream(new File(CBOR_OUTLINE_FILE));
			HashMap<String, HashMap<String, Float>> resultSet = new HashMap<String, HashMap<String,Float>>();
			final Iterator<Data.Page> pageIt = DeserializeData.iterAnnotations(fis);
			for(int i=0; pageIt.hasNext(); i++){
				Data.Page page = pageIt.next();
				//List<List<Data.Section>> paths = page.flatSectionPaths();
				HashSet<String> allSecIDsInPage = getAllSectionIDs(page);
				String pageQ = page.getPageName();
				for(String secid:allSecIDsInPage)
					pageQ = pageQ + "/" + secid;
				pageQ = pageQ.replaceAll("[/,%20]", " ").replaceAll("enwiki:", "");
				HashMap<String, Float> result = this.doSearch(page.getPageName(), RET_NO);
				resultSet.put(page.getPageId(), result);
			}
			this.printTrecevalRun(resultSet, "PAGE", this.runFileWriter);
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void searchSections(){
		try {
			FileInputStream fis = new FileInputStream(new File(CBOR_OUTLINE_FILE));
			HashMap<String, HashMap<String, Float>> resultSet = new HashMap<String, HashMap<String,Float>>();
			final Iterator<Data.Page> pageIt = DeserializeData.iterAnnotations(fis);
			for(int i=0; pageIt.hasNext(); i++){
				Data.Page page = pageIt.next();
				HashSet<String> secIDList = this.getAllSectionIDs(page);
				for(String secID:secIDList){
					//System.out.println(sec.getHeading());
					//System.out.println(sec.getHeadingId());
					
					// use HEAD_PART to form different part of query
					String secQ = secID.replaceAll("[/,%20]", " ").replaceAll("enwiki:", "");
					HashMap<String, Float> result = this.doSearch(secQ, RET_NO);
					resultSet.put(secID, result);
				}
			}
			this.printTrecevalRun(resultSet, "SECTION", this.runFileWriter);
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void printTrecevalRun(HashMap<String, HashMap<String, Float>> results, String runid, BufferedWriter bw){
		try {
			for(String q:results.keySet()){
				HashMap<String, Float> retParas = results.get(q);
				for(String para:retParas.keySet()){
					// query-id Q0 document-id rank score STANDARD
					bw.append(q+" Q0 "+para+" 0 "+retParas.get(para)+" "+runid+"\n");
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private ArrayList<Data.Section> getAllSections(Data.Page page){
		ArrayList<Data.Section> secList = new ArrayList<Data.Section>();
		for(Data.Section sec:page.getChildSections())
			addSectionToList(sec, secList);
		return secList;
	}
	
	private HashSet<String> getAllSectionIDs(Data.Page page){
		HashSet<String> secIDList = new HashSet<String>();
		String parent = page.getPageId();
		/*
		for(List<Data.Section> seclist:page.flatSectionPaths()){
			for(Data.Section sec:seclist){
				addSectionIDToList(sec, secIDList, parent);
			}
		}
		*/
		for(Data.Section sec:page.getChildSections())
			addSectionIDToList(sec, secIDList, parent);
		return secIDList;
	}
	
	private void addSectionToList(Data.Section sec, ArrayList<Data.Section> secList){
		if(sec.getChildSections() == null || sec.getChildSections().size() == 0)
			secList.add(sec);
		else{
			for(Data.Section child:sec.getChildSections())
				addSectionToList(child, secList);
			secList.add(sec);
		}
	}
	
	private void addSectionIDToList(Data.Section sec, HashSet<String> idlist, String parent){
		if(sec.getChildSections() == null || sec.getChildSections().size() == 0){
			idlist.add(parent+"/"+sec.getHeadingId());
		}
		else{
			idlist.add(parent+"/"+sec.getHeadingId());
			parent = parent+"/"+sec.getHeadingId();
			for(Data.Section child:sec.getChildSections())
				addSectionIDToList(child, idlist, parent);
		}
	}
	
	private void closeAll(){
		try {
			this.runFileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		// args = index file, para file, outline file, treceval run, no of ret, index or not
		if(args.length != 7){
			System.out.println("Usage: \n java -jar lucene-ret-model-v[n].jar "
					+ "index file, para file, outline file, treceval run, no of ret, index or not");
			System.exit(0);
		}
		LuceneRanker a = new LuceneRanker(args[0], args[1], args[2], args[3], Integer.parseInt(args[4]), args[5]);

		try {
			if(DO_INDEX.equalsIgnoreCase("i") || DO_INDEX.equalsIgnoreCase("index"))
				a.indexAllParas();
			a.searchPages();
			
			a.closeAll();

		} catch (CborException | IOException e) {
			e.printStackTrace();
		}

	}

}


