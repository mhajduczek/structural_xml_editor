package com.hajduczek.xmleditor;

import com.mxgraph.swing.mxGraphOutline;

public class GraphData {
	private static GraphData INSTANCE = null; 
	
	private mxGraphOutline graphOutline = new mxGraphOutline(null);
	
	private GraphData() {
		this.graphOutline.setDoubleBuffered(true);
		this.graphOutline.setTripleBuffered(true);
	}
	
	public static synchronized GraphData getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new GraphData();
		}
		return INSTANCE;
	}

	public mxGraphOutline getGraphOutline() {
		return graphOutline;
	}

	public synchronized void setGraphOutline(mxGraphOutline graphOutline) {
		this.graphOutline = graphOutline;
	}
}
