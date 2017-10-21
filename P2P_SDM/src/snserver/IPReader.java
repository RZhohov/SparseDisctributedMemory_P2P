package snserver;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class IPReader {
	
    
    public  ArrayList<String> getIPList() throws IOException
    {
    	BufferedReader bRead = new BufferedReader(new FileReader("IP.txt"));
    	ArrayList<String> IPs = new ArrayList<String>();
    	String line;
    	while((line = bRead.readLine()) != null) {
    		IPs.add(line);
    	}
    	bRead.close();
    	return IPs;
    }
}
