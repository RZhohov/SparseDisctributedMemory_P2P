package p2p_sdm;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;
import java.util.Set;

import sdm.BitVector;
import sdm.SDMImpl;

public class MegaPeer extends Node {
	
	private Message toPeer;
	private Message fromPeer;
	public ArrayList<String> superPeers = new ArrayList<String>();
	private SDMImpl sdm;
	private BitVector ID;
	private String SPeerIP;
	private int SPeerPort;
	private String GUI_IP;
	private BitVector slfResult;
	
	private boolean SP_status;
	
	//SP field
    private Hashtable<String, BitVector> register =new Hashtable<String, BitVector>();

	
	/**
	 * Constructor of Peer
	 * creates SDM of given size and radius
	 * get_SuperPeers() obtains IP address and Port of SuperPeer
	 * Generates ID out of SDM
	 * @param flag 
	 */
	public MegaPeer(boolean flag) {
		sdm = new SDMImpl(memory_size, 100, word_size);
		getSPeers();
		ID = sdm.generateID();
		SP_status = flag;
	}

	public void getSPeers(){
		
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
	
	public void join() {
		toPeer = new Message(JOIN, ID);
		int max = superPeers.size() - 1;
		int min = 0;
	    Random rand = new Random();
	    int randomSPeer = rand.nextInt((max - min) + 1) + min;
		SPeerIP = superPeers.get(randomSPeer);
		Socket conn = handleConnection(SPeerIP, PEER_PORT);
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
				   
				   if (fromPeer.getType() == JOIN)
				   {
					   BitVector peerID = (BitVector) fromPeer.getContent();
					   register.put(connectionSocket.getInetAddress().getHostAddress(), peerID);
					   System.out.println("Join from "+connectionSocket.getInetAddress().getHostAddress());
					   System.out.println("ID is "+ peerID.print());
					   Message toPeer = new Message(ACK, "");
					   send(connectionSocket, toPeer);
					   connectionSocket.close();
				   }
				   
				   
				   if (fromPeer.getType() == REQUEST)
				   {
					   if (SP_status){
						   System.out.println("SP received request from peer: " + connectionSocket.getInetAddress().getHostAddress());
						   //search at SP itself
						   BitVector result = search(sdm, (BitVector) fromPeer.getContent());
						   Message m = new Message(REPLY, result);
						   Socket toPeer = handleConnection(connectionSocket.getInetAddress().getHostAddress(), PEER_PORT);
						   send(toPeer, m);
						   //search among connected Peers
						   searchChild(connectionSocket.getInetAddress().getHostAddress(), fromPeer);
						   //send request to other SPs
					       searchSP(connectionSocket.getInetAddress().getHostAddress(), fromPeer);
					   }
					   else {
						   System.out.println("Received Request");
						   Object[] payload = (Object[]) fromPeer.getContent();
						   BitVector result = search(sdm, (BitVector) payload[0]);
						   Message m = new Message(REPLY, result);
						   Socket reply = handleConnection((String) payload[1], PEER_PORT);
						   send(reply, m);
						   reply.close();
					   }

				   }
				   
				   if (fromPeer.getType() == GUI)
				   {
					   Message toGUI = new Message(ACK, "ACK");
					   send(connectionSocket, toGUI);
				   }
				   
				   if (fromPeer.getType() == GUI_REQUEST)
				   {
					   if (SP_status){
						   GUI_IP = connectionSocket.getInetAddress().getHostAddress();
						   System.out.println("GUI IP is "+ GUI_IP);
						   BitVector query = (BitVector) fromPeer.getContent();
						   slfResult = sdm.retrieve(query);					   
						   Message m = new Message(REQUEST, query);
						   System.out.println("Peer received query from GUI "+query.print());
						   searchChild(InetAddress.getLocalHost().getHostAddress(), fromPeer);
						   searchSP(InetAddress.getLocalHost().getHostAddress(), fromPeer);
					   }
					   else {
						   GUI_IP = connectionSocket.getInetAddress().getHostAddress();
						   System.out.println("GUI IP is "+ GUI_IP);
						   BitVector query = (BitVector) fromPeer.getContent();
						   slfResult = sdm.retrieve(query);					   
						   Message m = new Message(REQUEST, query);
						   System.out.println("Peer received query from GUI "+query.print());
						   Socket s = handleConnection(SPeerIP, PEER_PORT);
						   send(s, m);
					   }
				   }
				   
				   if (fromPeer.getType() == SUPER_REQUEST)
				   {
					   System.out.println("SP received request from Super Peer: " + connectionSocket.getInetAddress().getHostAddress());
					   //search at SP itself
					   Object[] payload = (Object[]) fromPeer.getContent();
					   BitVector query = (BitVector) payload[0];
					   String peerIP = (String) payload[1];
					   BitVector result = search(sdm, query);
					   Message m = new Message(REPLY, result);
					   Socket toPeer = handleConnection(peerIP, PEER_PORT);
					   send(toPeer, m);
					   //search among connected Peers
					   Message forChild = new Message(REQUEST, query);
					   searchChild(peerIP, forChild);
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
	
	public void searchChild(String local, Message m) throws IOException{
        Set<String> IPs = register.keySet();
        if (IPs.size()>0)
        {
        for(String IP: IPs){
        	if (!local.equals(IP)){
        	Socket reqsock = handleConnection(IP, PEER_PORT);
        	Object[] content = new Object[2];
			content[0]= m.getContent();
			content[1]= local;
			Message reqmes = new Message(REQUEST, content);
			send(reqsock, reqmes);
			reqsock.close();	
        }	
        }
        }
	}
	
	public void searchSP(String local, Message m) throws IOException{
		String this_local = InetAddress.getLocalHost().getHostAddress();
		System.out.println("THIS IP "+this_local);
		if (superPeers.size()>0)
		{
		for (String SPeer: superPeers){
			if (!this_local.equals(SPeer)){
			Socket conn = handleConnection(SPeer, PEER_PORT);
			Object[] content = new Object[2];
			content[0]= m.getContent();
			content[1]= local;
			Message req = new Message(SUPER_REQUEST, content);
			send(conn, req);
			conn.close();
			}
			}
		}
	}
	
	
	

	public static void main(String[] args) {
		if (args[0].equals("super")){
			MegaPeer speer = new MegaPeer(true);
			speer.loop();
		}
		else {
			MegaPeer peer = new MegaPeer(false);
			peer.join();
			peer.loop();
		}

	}

}
