package snserver;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/*
 * Roman Zhohov, LTU, D7001 Network Programming, 2017
 */


public class IPReader {
	
    /**
     * 
     * @return Array of Strings (IP addresses) from IP.txt
     * @throws IOException
     */
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
