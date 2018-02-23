package edu.unh.cs.treccar.proj.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

public class ParaSimQrelsWriter {
	
	Properties p;
	String qrelsFilename;
	String mode;
	
	public ParaSimQrelsWriter(Properties prop, String outfile, String m){
		this.p = prop;
		this.qrelsFilename = outfile;
		this.mode = m;
	}
	
	public void printQrels() throws IOException{
		HashMap<String, ArrayList<String>> inputQrels = new HashMap<String, ArrayList<String>>();
		String inputPath = "";
		switch(this.mode){
		case "art":
			inputPath = this.p.getProperty("data-dir")+"/"+this.p.getProperty("art-qrels");
			break;
		case "top":
			inputPath = this.p.getProperty("data-dir")+"/"+this.p.getProperty("top-qrels");
			break;
		case "hier":
			inputPath = this.p.getProperty("data-dir")+"/"+this.p.getProperty("hier-qrels");
			break;
		}
		BufferedReader br = new BufferedReader(new FileReader(new File(inputPath)));
		String line = br.readLine();
		String q,para;
		while(line != null){
			q = line.split(" ")[0];
			para = line.split(" ")[2];
			if(inputQrels.keySet().contains(q))
				inputQrels.get(q).add(para);
			else{
				ArrayList<String> paralist = new ArrayList<String>();
				paralist.add(para);
				inputQrels.put(q, paralist);
			}
			line = br.readLine();
		}
		br.close();
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(this.p.getProperty("out-dir")+"/"+this.qrelsFilename)));
		ArrayList<String> plist;
		for(String qid:inputQrels.keySet()){
			plist = inputQrels.get(qid);
			for(String pid:plist){
				for(String simpid:plist){
					if(pid.equals(simpid))
						continue;
					bw.write(pid+" 0 "+simpid+" 1\n");
					//System.out.println(pid+" 0 "+simpid+" 1\n");
				}
			}
		}
		bw.close();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(new File("project.properties")));
			ParaSimQrelsWriter pw = new ParaSimQrelsWriter(prop, "simpara-fold0-top.qrels", "top");
			pw.printQrels();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
