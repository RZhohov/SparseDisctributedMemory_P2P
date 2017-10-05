package p2p_sdm;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import sdm.BitVector;



public class SuperPeer implements Node {

	@Override
	public void Init() {
	
	}
	
	
	public void handleJoin() throws ClassNotFoundException{
		ServerSocket welcomeSocket;
		try {
			welcomeSocket = new ServerSocket(8080);
			 while (true) {
				   System.out.println("Listening");
				   Socket connectionSocket = welcomeSocket.accept();
				   ObjectInputStream fromPeer = new ObjectInputStream(connectionSocket.getInputStream());
				   Object request = fromPeer.readObject();
				   if (request.equals("query"))
				   {
					   BitVector vector = (BitVector) fromPeer.readObject();
					   System.out.println("Peer looks for" + vector.print());
				   }
				   else
				   {
					   BitVector peerID = (BitVector) request;
					   System.out.println("Joined peer ID: "+peerID.print());
				   }
				   
				  }
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public static void main(String[] args) {
		SuperPeer speer = new SuperPeer();
		try {
			speer.handleJoin();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

}
