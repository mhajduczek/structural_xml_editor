package com.hajduczek.xmleditor;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import com.hajduczek.xmleditor.preferences.PreferenceConstants;

public class GraphVizCaller {
	public static String callGraphViz(String dot) throws IOException {
		long startTime = System.currentTimeMillis();
		//TODO: uzyc katalogu domowego uzytkownika na tworzenie plikow tymczasowych!!
		FileWriter outFile = new FileWriter(new File(System.getProperty("user.home"), "g.gv"));
		//		"C:\\Program Files\\Graphviz 2.28\\bin\\", "g.gv"));
		PrintWriter out = new PrintWriter(outFile);
		//"digraph G {  subgraph cluster_2 {  subgraph cluster_3 {  }  subgraph cluster_4 {  }  }  subgraph cluster_5 {  } }"
		out.println(dot);
		out.close();
		
		//Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_PATH_TO_GRAPHVIZ);  - sciezka do graphviza - preferencje workspace'u!
		String graphVizBinPath = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_PATH_TO_GRAPHVIZ);
		
		String dotExecPath = "fdp.exe -Tsvg " + System.getProperty("user.home") + File.separator +  "g.gv";

		Process process = Runtime.getRuntime().exec(dotExecPath, null,
				new File(graphVizBinPath));
				

		LogStreamReader lsr = new LogStreamReader(process.getInputStream());
		Thread thread = new Thread(lsr, "LogStreamReader");
		thread.start();

		try {
			process.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("GraphVizCaller.callGraphViz duration: " + ((System.currentTimeMillis()-startTime)) + "ms");
		return lsr.getAllText();
	}
}