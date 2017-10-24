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
import java.util.Random;

import sdm.BitVector;
import sdm.BitVectorUtility;
import sdm.SDMImpl;

public class Peer extends Node {
	
	private Message toPeer;
	private Message fromPeer;
	public ArrayList<String> superPeers = new ArrayList<String>();
	private SDMImpl sdm;
	private BitVector ID;
	private String SPeerIP;
	private int SPeerPort;
	private String GUI_IP;
	private BitVector slfResult;

	

	/**
	 * Constructor of Peer
	 * creates SDM of given size and radius
	 * get_SuperPeers() obtains IP address and Port of SuperPeer
	 * Generates ID out of SDM
	 */
	public Peer() {
		sdm = new SDMImpl(memory_size, T, word_size);
		getSPeers();
		ID = sdm.generateID();
		
	}
	
	/**
	 * Obtains IP address and port of SuperPeers and stores them into ArrrayList superPeers
	 */
	public void getSPeers() {
		Socket socket = handleConnection("52.26.203.26", 7777);
		try {
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			superPeers = (ArrayList<String>) in.readObject();
			in.close();
			socket.close();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}


	/**
	 * Joins P2P network by connecting to one of SuperPeers
	 * 
	 */
	public void join() {
		toPeer = new Message(JOIN, ID);
		int max = superPeers.size() - 1;
		int min = 0;
	    Random rand = new Random();
	    int randomSPeer = rand.nextInt((max - min) + 1) + min;
		SPeerIP = superPeers.get(randomSPeer);
		Socket conn = handleConnection(SPeerIP, SUPER_PORT);
		send(conn, toPeer);
		fromPeer = receive(conn);
		if (fromPeer.getType() == ACK){
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
			welcomeSocket = new ServerSocket(PEER_PORT);
			 while (true) {
				   System.out.println("Peer is Listening");
				   Socket connectionSocket = welcomeSocket.accept();
				   Message fromPeer = (Message) receive(connectionSocket);
				   
				   if (fromPeer.getType() == REQUEST)
				   {
					   System.out.println("Received Request");
					   Object[] payload = (Object[]) fromPeer.getContent();
					   BitVector result = search(sdm, (BitVector) payload[0]);
					   Message m = new Message(REPLY, result);
					   Socket reply = handleConnection((String) payload[1], PEER_PORT);
					   send(reply, m);
					   reply.close();
				   }
				   
				   if (fromPeer.getType() == GUI)
				   {
					   Message toGUI = new Message(ACK, "ACK");
					   send(connectionSocket, toGUI);
				   }
				   
				   if (fromPeer.getType() == GUI_REQUEST)
				   {
					   GUI_IP = connectionSocket.getInetAddress().getHostAddress();
					   System.out.println("GUI IP is "+ GUI_IP);
					   BitVector query = (BitVector) fromPeer.getContent();
					   slfResult = sdm.retrieve(query);					   
					   Message m = new Message(REQUEST, query);
					   System.out.println("Peer received query from GUI "+query.print());
					   Socket s = handleConnection(SPeerIP, SUPER_PORT);
					   send(s, m);
				   }
				   
				   
				   if (fromPeer.getType() == REPLY){
					   BitVector reply = (BitVector) fromPeer.getContent();
					   System.out.println("Reply received: "+reply.print());
					   
					   // slfResult = slfResult | reply
					   slfResult.or(reply);
					   
					   Socket toGUI = handleConnection(GUI_IP, GUI_PORT);
					   Message m = new Message(ACK, slfResult);
					   System.out.println("SENDING REPLY TO GUI");
					   send(toGUI, m);
					   toGUI.close();
					   
				   }
				   
				   
			}

		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	
	


	



	


}
