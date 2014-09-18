package com.hajduczek.xmleditor;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class MyEntityResolver implements EntityResolver {

	private String[] newWorkingDirPath;
	
	public MyEntityResolver(String[] newWorkingDirPath) {
		this.newWorkingDirPath = newWorkingDirPath;
	}
	
	@Override
	public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        if (systemId != null && systemId.startsWith("file:/")) {
            for (String newPath : this.newWorkingDirPath) {
	        	try {
	            	URI workingDirectoryURI = new File(newPath).toURI();
	            	URI workingFile;
	            	URI currentJavaWorkingDirectory = new File(System.getProperty("user.dir")).toURI();
	                workingFile = convertToNewWorkingDirectory(currentJavaWorkingDirectory, workingDirectoryURI, new File(new URI(systemId)).toURI());
	           	 	
	           	 	return new InputSource(workingFile.toString());
	            } catch (URISyntaxException ex) {
	           	 	ex.printStackTrace();
	            }
            }
        }
        return null;
	}
	
	private URI convertToNewWorkingDirectory(URI oldwd, URI newwd, URI file) throws IOException, URISyntaxException {
        String oldwdStr = oldwd.toString();
        String newwdStr = newwd.toString();
        String fileStr = file.toString();
        
        String cmpStr = null;

        if (fileStr.startsWith(oldwdStr) && (cmpStr = fileStr.substring(oldwdStr.length())).indexOf('/') == -1) {
            return new URI(newwdStr + '/' + cmpStr);
        }
        
        String[] oldwdSplit = oldwdStr.split("/");
        String[] newwdSplit = newwdStr.split("/");
        String[] fileSplit = fileStr.split("/");
        
        int diff;
        for(diff = 0; diff<oldwdSplit.length & diff<fileSplit.length; diff++) {
            if (!oldwdSplit[diff].equals(fileSplit[diff])) {
                break;
            }
        }
        
        int diffNew;
        for(diffNew=0; diffNew<newwdSplit.length && diffNew<fileSplit.length; diffNew++) {
            if (!newwdSplit[diffNew].equals(fileSplit[diffNew])) {
                break;
            }
        }
        
        //Workaround for case, when extrnal imported entity has imports other entity
        //in that case systemId has correct path, not based on user.dir
        if (diffNew > diff) {
            return file;
        }

        int elemsToSub = oldwdSplit.length - diff;
        StringBuffer resultStr = new StringBuffer(100);
        for(int i=0; i<newwdSplit.length - elemsToSub; i++) {
            resultStr.append(newwdSplit[i]);
            resultStr.append('/');
        }
                
        for(int i=diff; i<fileSplit.length; i++) {
            resultStr.append(fileSplit[i]);
            if (i < fileSplit.length - 1) resultStr.append('/');
        }
        
        return new URI(resultStr.toString());
    }
}
