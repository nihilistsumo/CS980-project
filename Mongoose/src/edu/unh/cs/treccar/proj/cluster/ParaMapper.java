package edu.unh.cs.treccar.proj.cluster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

public class ParaMapper {
	Properties pr;
	HashMap<String, ArrayList<String>> cl;
	ArrayList<String> titleIDsToMap;
	
	public ParaMapper(Properties p, HashMap<String, ArrayList<String>> clusters, ArrayList<String> titles){
		this.pr = p;
		this.cl = clusters;
		this.titleIDsToMap = titles;
	}
	
	public HashMap<String, ArrayList<String>> map(){
		HashMap<String, ArrayList<String>> mappings = new HashMap<String, ArrayList<String>>();
		
		return mappings;
	}

}
