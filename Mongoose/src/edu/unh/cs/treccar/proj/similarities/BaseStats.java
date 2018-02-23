package edu.unh.cs.treccar.proj.similarities;
import java.util.ArrayList;
import edu.unh.cs.treccar.proj.util.*;
import java.util.Arrays;

import edu.unh.cs.treccar.Data;
public class BaseStats 
{
	private static double tf;
	private static int df;
	private static double idf;
	private static ArrayList<Double> vector;
	
	public static int getTermFreq(String term, ArrayList<String> document)
	{
		int termFreq = 0;
		for(String word : document)
			if(word.equalsIgnoreCase(term))
				termFreq++;
		return termFreq;
	}
	
	public static int getDocFreq(String term, ArrayList<Data.Paragraph> list )
	{
		int docFreq = 0;
		for(Data.Paragraph p : list)
			if(isPresent(p, term))
				docFreq++;
		return docFreq ;
	}
	private static boolean isPresent(Data.Paragraph p, String word)
	{
		/*String text = p.getTextOnly();
		ArrayList<String> words = new ArrayList<String>(Arrays.asList(text.split(" ")));
		return words.contains(word);*/
		
		return (new ArrayList<String>(Arrays.asList(p.getTextOnly().split(" "))).contains(word));
	}
	
	public static int getTotalParas(ArrayList<Data.Paragraph> paras)
	{
		return paras.size();
	}
	public static ArrayList <Double> getNormalisedTermVector(ArrayList<String> doc, String method, ArrayList<Data.Paragraph> paras)
	{
		char tfMethod = method.charAt(0);
		char idfMethod = method.charAt(1);
		char normMethod = method.charAt(2);
		
		for(String term : doc)
		{	
			tf = getTermFreq(term,doc);
			df = getDocFreq(term,paras);
			switch(tfMethod)
			{
				case 'n': break;
				case 'l': tf = 1 + Math.log(tf);
						  break;
				case 'b': if(tf > 0)
							tf = 1;
				          else
				        	tf = 0;
				          break;
				default : break;
			}
			switch(idfMethod)
			{
				case 'n': idf = 1;
					      break;
				case 't': idf = Math.log(getTotalParas(paras)/df);
						  break;
				default : break;
			}
			vector.add(tf * idf);
		}
		switch(normMethod)
		{
			case 'n': break;
			case 't': double norm = 0;
				      for(double v : vector)
						norm += v*v;
				      norm = 1/Math.sqrt(norm);
				      for(double v : vector)
							vector.add(v * norm);
					  break;
			default : break;
		}
		return vector;
		
	}

}
