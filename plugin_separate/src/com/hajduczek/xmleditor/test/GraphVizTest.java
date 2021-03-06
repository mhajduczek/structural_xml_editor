package com.hajduczek.xmleditor.test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.hajduczek.xmleditor.LogStreamReader;

public class GraphVizTest {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
	/* ZWRACA PO�O�ENIE
		Process process = Runtime.getRuntime().
				exec("C:\\Program Files\\Graphviz 2.28\\bin\\dot.exe \"C:\\Program Files\\Graphviz 2.28\\bin\\g.gv\""); //-Tpng -oC:\\Program Files\\Graphviz 2.28\\bin\\out.png
	*/			
		// generowanie pliku z opisem grafu
		FileWriter outFile = new FileWriter(new File("C:\\Program Files\\Graphviz 2.28\\bin\\","g.gv"));
		PrintWriter out = new PrintWriter(outFile);
		out.println("digraph G {");
		out.println("A->B;");
		out.println("subgraph cluster_A {C->D;E->F; C->F;}");
		out.println("}");
		out.close();
		
		String dotExecPath = "dot.exe -T png -o out.png g.gv";
		
		Process process = Runtime.getRuntime().
				exec(dotExecPath, null, new File("C:\\Program Files\\Graphviz 2.28\\bin\\"));
		
		LogStreamReader lsr = new LogStreamReader(process.getInputStream());
		Thread thread = new Thread(lsr, "LogStreamReader");
		thread.start();
		
		try {
			process.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} //czeka na zakonczenie DOT'a
		
		ImageLoader loader = new ImageLoader();
		ImageData[] imageData = loader.load("C:\\Program Files\\Graphviz 2.28\\bin\\out.png");
		Image image = new Image(Display.getDefault(), imageData[0]);
		
		
		// setup the SWT window
	    Display display = Display.getDefault();//new Display();
	    final Shell shell = new Shell(display);
	    shell.setLayout(new RowLayout());
	    shell.setText("Photo Application");
	    
	    Composite parent = new Composite(shell, SWT.NONE);
	    GridLayout gridLayout = new GridLayout();
	    gridLayout.numColumns = 1;
	    parent.setLayout(gridLayout);
	    
	    Label label = new Label(parent, SWT.NONE);
	    label.setImage(image);
	    
	    shell.pack();
	    shell.open();

	    while (!shell.isDisposed()) {
	      if (!display.readAndDispatch())
	        display.sleep();
	    }
	    // tear down the SWT window
	    display.dispose();
	}
}
