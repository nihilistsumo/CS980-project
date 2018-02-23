package edu.unh.cs.treccar.proj.util;

import java.util.ArrayList;
import java.util.Arrays;
import edu.unh.cs.treccar.Data;

/**
 * This class implements various methods to convert the paragraph pair contained in a ParaPair object
 * into two lists of words contained in the paragraphs. 
 * @author Shubham Chatterjee
 *
 */

public class ParaUtilities
{
	private static String text1, text2, paraId1, paraId2;
	private static ArrayList<String> paraText1;
	private static ArrayList<String> paraText2;
	
	/**
	 * 
	 * @param paraId String Contains the paragraph ID of the paragraph whose text is required
	 * @param list ArrayList<Data.Paragraph> Conatins a list of Data.Paragraph objects
	 * @return Paragraph text corresponding to paraId
	 */
	
	private static String getParaText(String paraId,ArrayList<Data.Paragraph> list)
	{
		/*Get the paragraph text of paragraph with ID paraId*/
		String text = "";
		for(Data.Paragraph p : list)
			if((p.getParaId()).equals(paraId))
				text = p.getTextOnly();
		return text;
	}
	/**
	 * 
	 * @param text String Contains the text of the paragraph to be converted into list
	 * @return An ArrayList of words conatined in the paragraph text
	 */
	
	private static ArrayList<String> createListOfWords(String text)
	{
		return (new ArrayList<String>(Arrays.asList(text.split(" "))));
	}
	/**
	 * 
	 * @param pp ParaPair A ParaPair object representing a pair of paragraphs
	 * @param list ArrayList<Data.Paragraph> Conatins a list of Data.Paragraph objects 
	 * @return ArrayList of words in first paragraph
	 */
	
	public static ArrayList<String> getParaText1(ParaPair pp, ArrayList<Data.Paragraph> list)
	{
		/*Get the paraId of the first paragraph*/
		paraId1 = pp.getPara1();
		
		/*Get the paragraph text of first paragraph*/
		text1 = getParaText(paraId1,list);
		
		/*Convert the paragraph texts into an ArrayList of words*/
		paraText1 = createListOfWords(text1);
		
		return paraText1;
	}
	/**
	 * 
	 * @param pp ParaPair A ParaPair object representing a pair of paragraphs
	 * @param list ArrayList<Data.Paragraph> Conatins a list of Data.Paragraph objects
	 * @return ArrayList of words in second paragraph
	 */
	
	public static ArrayList<String> getParaText2(ParaPair pp, ArrayList<Data.Paragraph> list)
	{
		/*Get the paraId of the second paragraph*/
		paraId2 = pp.getPara2();
		
		/*Get the paragraph text of second paragraph*/
		text2 = getParaText(paraId2,list);
		
		/*Convert the paragraph texts into an ArrayList of words*/
		paraText2 = createListOfWords(text2);
		
		return paraText2;
	}
	public static ArrayList<String> findIntersection(ArrayList<String> list1, ArrayList<String> list2)
	{
		ArrayList<String> common = new ArrayList<String>();
		for(String w : list1)
			if(list2.contains(w) && !common.contains(w))
				common.add(w);
		return common;
	}
}
