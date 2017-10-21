package p2p_sdm;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;
import sdm.BitVector;
import sdm.SDMImpl;

public class SuperPeer extends Node {   
    private SDMImpl sdm;
    private BitVector ID;
    private ArrayList<String[]> superPeers = new ArrayList<String[]>();
    private Hashtable<String, BitVector> register =new Hashtable<String, BitVector>(); 
    
    /**
     * Constructs SuperPeer
     * generates SDM and ID
     */
	public SuperPeer() {
		sdm = new SDMImpl(memory_size, 100, word_size);
		ID = sdm.generateID();
		getSPeers();
	}
	
	public void getSPeers(){
		Socket sns = handleConnection("127.0.0.1", 7777);
		ObjectInputStream fromSNS;
		try {
			fromSNS = new ObjectInputStream(sns.getInputStream());
			superPeers = (ArrayList<String[]>) fromSNS.readObject();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * Main loop
	 */
	public void loop() {
		ServerSocket welcomeSocket;
		try {
			welcomeSocket = new ServerSocket(8080);
			 while (true) {
				   System.out.println("Listening");
				   Socket connectionSocket = welcomeSocket.accept();
				   ObjectInputStream input = new ObjectInputStream(connectionSocket.getInputStream());
				   Message fromPeer = (Message) input.readObject();
				   
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
				   
				   if ((fromPeer.getType() == REQUEST) || (fromPeer.getType() == SUPER_REQUEST))
				   {
					   //search at SP itself
					   BitVector result = search(sdm, (BitVector) fromPeer.getContent());
					   Message m = new Message(REPLY, result);
					   send(connectionSocket, m);
					   
					   //search among connected Peers
					   searchChild(connectionSocket, fromPeer);
					   
					   //send request to other SPs
				       if (fromPeer.getType() == REQUEST){
				    	   searchSP(connectionSocket, fromPeer);
				       }
				   }
				      
				  }
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		}

	}
	
	public void searchChild(Socket s, Message m) throws IOException{
        Set<String> IPs = register.keySet();
        for(String IP: IPs){
        	if (!s.getInetAddress().equals(IP)){
        	Socket reqsock = handleConnection(IP, PeerPort);
        	Object[] content = (Object[]) new Object();
			content[0]= m.getContent();
			content[1]= s.getInetAddress();
			Message reqmes = new Message(REQUEST, content);
			send(reqsock, reqmes);
			reqsock.close();	
        }	
        }
	}
	
	public void searchSP(Socket s, Message m) throws IOException{
		for (String[] SPeer: superPeers){
			String local = InetAddress.getLocalHost().getHostAddress();
			if (!local.equals(SPeer[0])){
			Socket conn = handleConnection(SPeer[0], Integer.parseInt(SPeer[1]));
			Object[] content = (Object[]) new Object();
			content[0]= m.getContent();
			content[1]= s.getInetAddress();
			Message req = new Message(SUPER_REQUEST, content);
			send(conn, req);
			conn.close();
			}
	}
	}
	
	public static void main(String[] args) {
		SuperPeer speer = new SuperPeer();
		speer.loop();
	}

}
