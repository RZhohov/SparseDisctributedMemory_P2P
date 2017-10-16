package p2p_sdm;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Hashtable;

import sdm.BitVector;
import sdm.BitVectorUtility;
import sdm.SDMImpl;

public class Peer extends Node {
	
	private Message toPeer;
	private Message fromPeer;
	private ArrayList<String[]> superPeers = new ArrayList<String[]>();
	private SDMImpl sdm;
	private BitVector ID;

	/**
	 * Constructor of Peer
	 * creates SDM of given size and radius
	 * get_SuperPeers() obtains IP address and Port of SuperPeer
	 * Generates ID out of SDM
	 */
	public Peer() {
		sdm = new SDMImpl(memory_size, 100, word_size);
		getSPeers();
		ID = sdm.generateID();
	}
	
	/**
	 * Obtains IP address and port of SuperPeers and stores them into ArrrayList superPeers
	 */
	private void getSPeers() {
		String[] ip_port = {"localhost", "8080"};
		superPeers.add(ip_port);
	}


	/**
	 * Joins P2P network by connecting to one of SuperPeers
	 * 
	 */
	public void join() {
		toPeer = new Message(JOIN, ID);
		Socket conn = handleConnection(superPeers.get(0)[0], Integer.parseInt(superPeers.get(0)[1]));
		send(conn, toPeer);
		fromPeer = receive(conn);
		if (fromPeer.getType() == fromPeer.ACK){
			System.out.println("Successfully joined ACK");
			try {
				conn.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		}
	
	public void loop(){
		ServerSocket welcomeSocket;
		try {
			welcomeSocket = new ServerSocket(PeerPort);
			 while (true) {
				   System.out.println("Peer is Listening");
				   Socket connectionSocket = welcomeSocket.accept();
				   Message fromPeer = (Message) receive(connectionSocket);
				   if (fromPeer.getType() == REQUEST)
				   {
					   Object[] payload = (Object[]) fromPeer.getContent();
					   BitVector result = search(sdm, (BitVector) payload[0]);
					   Message m = new Message(REPLY, result);
					   Socket reply = handleConnection((String) payload[1], PeerPort);
					   send(reply, m);
					   reply.close();
				   }
				   
				   
			}

		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	
	


	



	


}
