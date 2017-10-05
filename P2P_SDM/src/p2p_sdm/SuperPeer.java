package p2p_sdm;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

import sdm.BitVector;



public class SuperPeer extends Node {
	
	
    Hashtable<String, BitVector> register =new Hashtable<String, BitVector>();  	

	
	public void loop() {
		ServerSocket welcomeSocket;
		try {
			welcomeSocket = new ServerSocket(8080);
			 while (true) {
				   System.out.println("Listening");
				   Socket connectionSocket = welcomeSocket.accept();
				   ObjectInputStream input = new ObjectInputStream(connectionSocket.getInputStream());
				   Message fromPeer = (Message) input.readObject();
				   if (fromPeer.getType() == fromPeer.JOIN)
				   {
					   BitVector ID = (BitVector) fromPeer.getContent();
					   register.put(connectionSocket.getInetAddress().getHostAddress(), ID);
					   System.out.println("Join from "+connectionSocket.getInetAddress().getHostAddress());
					   System.out.println("ID is "+ ID.print());
					   
					   Message toPeer = new Message(0, "");
					   send(connectionSocket, toPeer);
					   connectionSocket.close();
				   }

				   
				  }
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}


	
	public static void main(String[] args) {
		SuperPeer speer = new SuperPeer();
		speer.loop();
	}

}
