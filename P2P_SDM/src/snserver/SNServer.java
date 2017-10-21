package snserver;


/*Roman Zhohov
 * threaded TCP server 
 * 
 * 
 * 
 * 
 * PORT: 8080*/

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.logging.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class SNServer implements Runnable {
	 
   private static int PORT = 7777;
   private ArrayList<String> superPeers;
   Socket csocket;
   
   public SNServer(Socket csocket) { //Constructor takes socket to the client
      this.csocket = csocket;
   }
   
   public void run() {
    	//I/O streams  
		try {
	  		while (true)
	  		{
	  			IPReader r = new IPReader();
	  			superPeers = r.getIPList();
				ObjectOutputStream outToPeer = new ObjectOutputStream(csocket.getOutputStream());
				outToPeer.writeObject(superPeers);
				outToPeer.close();
				break;
	  		}
		} catch (IOException e) {
			e.printStackTrace();
		} 
   }
   
   
   public static void main(String args[]) {
	      ServerSocket ssock = null;
	      
	      try {
			ssock = new ServerSocket(PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}

	      System.out.println("Listening");
	      
	      while (true) {
	        Socket sock = null;
			try {
				sock = ssock.accept();
			} catch (IOException e) {
				e.printStackTrace();
			}
	        System.out.println("Connected");
	        new Thread(new SNServer(sock)).start(); //Starting new thread
	      }
	   }
   
}

