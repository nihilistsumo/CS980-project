package edu.unh.cs.treccar.proj.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import co.nstant.in.cbor.CborException;
import edu.unh.cs.treccar_v2.Data;
import edu.unh.cs.treccar_v2.read_data.DeserializeData;

public class DataUtilities {
	
	public static HashMap<String, Data.Paragraph> getParaMapFromPath(String path){
		HashMap<String, Data.Paragraph> paras = new HashMap<String, Data.Paragraph>();
		try {
			FileInputStream fis = new FileInputStream(new File(path));
			final Iterator<Data.Paragraph> paraIt = DeserializeData.iterParagraphs(fis);
			for (int i=1; paraIt.hasNext(); i++) {
				Data.Paragraph p = paraIt.next();
				paras.put(p.getParaId(), p);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return paras;
	}
	
	public static HashMap<String, ArrayList<String>> getReducedParaMap(HashMap<String, ArrayList<String>> map){
		HashMap<String, ArrayList<String>> reduced = new HashMap<String, ArrayList<String>>();
		
		return reduced;
	}
	
	public static HashMap<String, ArrayList<String>> getPreprocessedParaMap(HashMap<String, Data.Paragraph> paras){
		HashMap<String, ArrayList<String>> processed = new HashMap<String, ArrayList<String>>();
		for(String pid:paras.keySet()){
			String paratext = paras.get(pid).getTextOnly();
			ArrayList<String> paratokens = new ArrayList<String>();
			for(String w:paratext.replaceAll("%20", " ").split(" ")){
				w = w.toLowerCase().replaceAll("[^a-zA-Z]", "");
				if(!DataUtilities.stopwords.contains(w) && w.length()>1)
					paratokens.add(w);
			}
			processed.put(pid, paratokens);
		}
		return processed;
	}
	
	public static HashMap<String, ArrayList<String>> getTrueArticleParasMapFromPath(String path){
		HashMap<String, ArrayList<String>> articleMap = new HashMap<String, ArrayList<String>>();
		BufferedReader br;
		try{
			br = new BufferedReader(new FileReader(path));
			String line;
			String[] lineData = new String[4];
			while((line = br.readLine()) != null){
				lineData = line.split(" ");
				if(articleMap.containsKey(lineData[0])){
					articleMap.get(lineData[0]).add(lineData[2]);
				} else{
					ArrayList<String> paraList = new ArrayList<String>();
					paraList.add(lineData[2]);
					articleMap.put(lineData[0], paraList);
				}	
			}
			br.close();
		} catch (Exception e){
			e.printStackTrace();
		}
		return articleMap;
	}
	
	/*
	public static HashMap<String, ArrayList<String>> getArticleSecMapFromPath(String path){
		HashMap<String, ArrayList<String>> articleSecMap = new HashMap<String, ArrayList<String>>();
		BufferedReader br;
		try{
			br = new BufferedReader(new FileReader(path));
			String line, pageid;
			String[] lineData = new String[4];
			while((line = br.readLine()) != null){
				lineData = line.split(" ");
				pageid = lineData[0].split("/")[0];
				if(articleSecMap.containsKey(pageid)){
					articleSecMap.get(pageid).add(lineData[0]);
				} else{
					ArrayList<String> secIDList = new ArrayList<String>();
					secIDList.add(lineData[2]);
					articleSecMap.put(pageid, secIDList);
				}	
			}
			br.close();
		} catch (Exception e){
			e.printStackTrace();
		}
		return articleSecMap;
	}
	*/
	
	public static HashMap<String, ArrayList<String>> getArticleSecMap(String outlinePath){
		HashMap<String, ArrayList<String>> articleSecMap = new HashMap<String, ArrayList<String>>();
		try {
			FileInputStream fis = new FileInputStream(new File(outlinePath));
			final Iterator<Data.Page> pageIt = DeserializeData.iterAnnotations(fis);
			for(int i=0; pageIt.hasNext(); i++){
				Data.Page page = pageIt.next();
				ArrayList<String> secIDsInPage = new ArrayList<String>();
				for(Data.Section sec:getAllSections(page))
					secIDsInPage.add(page.getPageId()+"/"+sec.getHeadingId());
				articleSecMap.put(page.getPageId(), secIDsInPage);
			}
		} catch(IOException e){
			e.printStackTrace();
		}
		return articleSecMap;
	}
	
	public static ArrayList<Data.Section> getAllSections(Data.Page page){
		ArrayList<Data.Section> secList = new ArrayList<Data.Section>();
		for(Data.Section sec:page.getChildSections())
			addSectionToList(sec, secList);
		return secList;
	}
	
	private static void addSectionToList(Data.Section sec, ArrayList<Data.Section> secList){
		if(sec.getChildSections() == null || sec.getChildSections().size() == 0)
			secList.add(sec);
		else{
			for(Data.Section child:sec.getChildSections())
				addSectionToList(child, secList);
			secList.add(sec);
		}
	}
	// Converts arraylist of para objects into their corresponding para id array
	public static ArrayList<String> getOrderedParaIDArray(ArrayList<Data.Paragraph> paras){
		ArrayList<String> ids = new ArrayList<String>(paras.size());
		for(Data.Paragraph p:paras)
			ids.add(p.getParaId());
		return ids;
	}
	
	public static HashMap<String,ArrayList<String>> getGTMapQrels(String qrelsPath){
		HashMap<String, ArrayList<String>> gtMap = new HashMap<String, ArrayList<String>>();
		BufferedReader br;
		try{
			br = new BufferedReader(new FileReader(qrelsPath));
			String line;
			String[] lineData = new String[4];
			while((line = br.readLine()) != null){
				lineData = line.split(" ");
				if(gtMap.containsKey(lineData[0])){
					gtMap.get(lineData[0]).add(lineData[2]);
				} else{
					ArrayList<String> paraList = new ArrayList<String>();
					paraList.add(lineData[2]);
					gtMap.put(lineData[0], paraList);
				}	
			}
			br.close();
		} catch (Exception e){
			e.printStackTrace();
		}
		return gtMap;
	}
	
	public static ArrayList<ArrayList<String>> getGTClusters(String pageid, String qrelsPath){
		HashMap<String, ArrayList<String>> gtMap = DataUtilities.getGTMapQrels(qrelsPath);
		ArrayList<ArrayList<String>> clusters = new ArrayList<ArrayList<String>>();
		for(String secID:gtMap.keySet()){
			if(secID.startsWith(pageid)){
				ArrayList<String> clust = gtMap.get(secID);
				clusters.add(clust);
			}
		}
		return clusters;
	}
	
	public static HashMap<String, ArrayList<String>> getPageParaMapFromRunfile(String runfile){
		HashMap<String, ArrayList<String>> pageParaMap = new HashMap<String, ArrayList<String>>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(runfile)));
			String line = br.readLine();
			while(line!=null){
				String pageid = line.split(" ")[0];
				String paraid = line.split(" ")[2];
				if(pageParaMap.keySet().contains(pageid))
					pageParaMap.get(pageid).add(paraid);
				else{
					ArrayList<String> paralist = new ArrayList<String>();
					paralist.add(paraid);
					pageParaMap.put(pageid, paralist);
				}
				line = br.readLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pageParaMap;
	}
	
	public static List<String> stopwords = Arrays.asList("a", "as", "able", "about",
				"above", "according", "accordingly", "across", "actually",
				"after", "afterwards", "again", "against", "aint", "all",
				"allow", "allows", "almost", "alone", "along", "already",
				"also", "although", "always", "am", "among", "amongst", "an",
				"and", "another", "any", "anybody", "anyhow", "anyone", "anything",
				"anyway", "anyways", "anywhere", "apart", "appear", "appreciate",
				"appropriate", "are", "arent", "around", "as", "aside", "ask", "asking",
				"associated", "at", "available", "away", "awfully", "be", "became", "because",
				"become", "becomes", "becoming", "been", "before", "beforehand", "behind", "being",
				"believe", "below", "beside", "besides", "best", "better", "between", "beyond", "both",
				"brief", "but", "by", "cmon", "cs", "came", "can", "cant", "cannot", "cant", "cause", "causes",
				"certain", "certainly", "changes", "clearly", "co", "com", "come",
				"comes", "concerning", "consequently", "consider", "considering", "contain",
				"containing",    "contains","corresponding","could", "couldnt", "course", "currently",
				"definitely", "described", "despite", "did", "didnt", "different", "do", "does",
				"doesnt", "doing", "dont", "done", "down", "downwards", "during", "each", "edu",
				"eg", "eight", "either", "else", "elsewhere", "enough", "entirely", "especially",
				"et", "etc", "even", "ever", "every", "everybody", "everyone", "everything", "everywhere",
				"ex", "exactly", "example", "except", "far", "few", "ff", "fifth", "first", "five", "followed",   
				"following", "follows", "for", "former", "formerly", "forth", "four", "from", "further",
				"furthermore", "get", "gets", "getting", "given", "gives", "go", "goes", "going", "gone"
				    , "got", "gotten", "greetings", "had", "hadnt", "happens", "hardly", "has", "hasnt", "have",
				    "havent", "having", "he", "hes", "hello", "help", "hence", "her", "here", "heres", "hereafter",
				    "hereby", "herein", "hereupon", "hers", "herself", "hi", "him", "himself", "his", "hither", 
				    "hopefully", "how", "howbeit", "however", "i", "id", "ill", "im", "ive", "ie", "if", "ignored", 
				    "immediate", "in", "inasmuch", "inc", "indeed", "indicate", "indicated", "indicates", "inner", 
				    "insofar", "instead", "into", "inward", "is", "isnt", "it", "itd", "itll", "its", "its", "itself", 
				    "just", "keep", "keeps", "kept", "know", "knows", "known", "last", "lately", "later", "latter", 
				    "latterly", "least", "less", "lest", "let", "lets", "like", "liked", "likely", "little", "look", 
				    "looking", "looks", "ltd", "mainly", "many", "may", "maybe", "me", "mean", "meanwhile", "merely", 
				    "might", "more", "moreover", "most", "mostly", "much", "must", "my", "myself", "name", "namely", 
				    "nd", "near", "nearly", "necessary", "need", "needs", "neither", "never", "nevertheless", "new", 
				    "next", "nine", "no", "nobody", "non", "none", "noone", "nor", "normally", "not", "nothing", "novel", 
				    "now", "nowhere", "obviously", "of", "off", "often", "oh", "ok", "okay", "old", "on", "once", 
				    "one", "ones", "only", "onto", "or", "other", "others", "otherwise", "ought", "our", "ours", 
				    "ourselves", "out", "outside", "over", "overall", "own", "particular", "particularly", "per", 
				    "perhaps", "placed", "please", "plus", "possible", "presumably", "probably", "provides", "que", 
				    "quite", "qv", "rather", "rd", "re", "really", "reasonably", "regarding", "regardless", "regards", 
				    "relatively", "respectively", "right", "said", "same", "saw", "say", "saying", "says", "second", 
				    "secondly", "see", "seeing", "seem", "seemed", "seeming", "seems", "seen", "self", "selves", 
				    "sensible", "sent", "serious", "seriously", "seven", "several", "shall", "she", "should", "shouldnt", 
				    "since", "six", "so", "some", "somebody", "somehow", "someone", "something", "sometime", "sometimes", 
				    "somewhat", "somewhere", "soon", "sorry", "specified", "specify", "specifying", "still", "sub", "such", 
				    "sup", "sure", "ts", "take", "taken", "tell", "tends", "th", "than", "thank", "thanks", "thanx", "that", 
				    "thats", "thats", "the", "their", "theirs", "them", "themselves", "then", "thence", "there", "theres", 
				    "thereafter", "thereby", "therefore", "therein", "theres", "thereupon", "these", "they", "theyd", "theyll", 
				    "theyre", "theyve", "think", "third", "this", "thorough", "thoroughly", "those", "though", "three", 
				    "through", "throughout", "thru", "thus", "to", "together", "too", "took", "toward", "towards", "tried", 
				    "tries", "truly", "try", "trying", "twice", "two", "un", "under", "unfortunately", "unless", "unlikely", 
				    "until", "unto", "up", "upon", "us", "use", "used", "useful", "uses", "using", "usually", "value", "various", 
				    "very", "via", "viz", "vs", "want", "wants", "was", "wasnt", "way", "we", "wed", "well", "were", "weve", 
				    "welcome", "well", "went", "were", "werent", "what", "whats", "whatever", "when", "whence", "whenever", 
				    "where", "wheres", "whereafter", "whereas", "whereby", "wherein", "whereupon", "wherever", "whether", 
				    "which", "while", "whither", "who", "whos", "whoever", "whole", "whom", "whose", "why", "will", "willing", 
				    "wish", "with", "within", "without", "wont", "wonder", "would", "would", "wouldnt", "yes", "yet", "you", 
				    "youd", "youll", "youre", "youve", "your", "yours", "yourself", "yourselves", "zero");
}
