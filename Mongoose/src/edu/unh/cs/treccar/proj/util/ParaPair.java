package edu.unh.cs.treccar.proj.util;

import java.io.Serializable;
import java.util.ArrayList;

public class ParaPair implements Serializable {
	private String para1;
	private String para2;
	private ArrayList<String> para1tokens;
	private ArrayList<String> para2tokens;
	
	public ParaPair(String p1, String p2, ArrayList<String> t1, ArrayList<String> t2){
		this.para1 = p1;
		this.para2 = p2;
		this.para1tokens = t1;
		this.para2tokens = t2;
	}
	
	public ArrayList<String> getPara1tokens() {
		return para1tokens;
	}

	public void setPara1tokens(ArrayList<String> para1tokens) {
		this.para1tokens = para1tokens;
	}

	public ArrayList<String> getPara2tokens() {
		return para2tokens;
	}

	public void setPara2tokens(ArrayList<String> para2tokens) {
		this.para2tokens = para2tokens;
	}

	public String getPara1() {
		return para1;
	}
	public void setPara1(String word1) {
		this.para1 = word1;
	}
	public String getPara2() {
		return para2;
	}
	public void setPara2(String word2) {
		this.para2 = word2;
	}
	@Override
	public boolean equals(Object o){
		if(((ParaPair)o).getPara1()==this.para1 
				&& ((ParaPair)o).getPara2()==this.para2)
				return true;
		else if(((ParaPair)o).getPara2()==this.para1 
				&& ((ParaPair)o).getPara1()==this.para2)
				return true;
		return false;
	}
	@Override
	public int hashCode(){
		return this.para1.hashCode()+this.para2.hashCode();
	}
}
