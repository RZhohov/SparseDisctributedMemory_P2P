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
	private ArrayList<String> superPeers = new ArrayList<String>();
	private SDMImpl sdm;
	private BitVector ID;
	private String SPeerIP;
	private int SPeerPort;
	private String GUI_IP;

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
		//add random SP selection
		SPeerIP = superPeers.get(0);
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
					   //ADD self search
					   GUI_IP = connectionSocket.getInetAddress().getHostAddress();
					   System.out.println("GUI IP is "+ GUI_IP);
					   BitVector query = (BitVector) fromPeer.getContent();
					   Message m = new Message(REQUEST, query);
					   System.out.println("Peer received query from GUI "+query.print());
					   Socket s = handleConnection(SPeerIP, SUPER_PORT);
					   send(s, m);
				   }
				   
				   
				   if (fromPeer.getType() == REPLY){
					   BitVector reply = (BitVector) fromPeer.getContent();
					   System.out.println("Reply received: "+reply.print());
					   
					   Socket toGUI = handleConnection(GUI_IP, GUI_PORT);
					   Message m = new Message(ACK, reply);
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
