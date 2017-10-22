package probeGUI;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import p2p_sdm.Node;
import p2p_sdm.Peer;
import p2p_sdm.Message;
import sdm.BitVector;
import sdm.BitVectorUtils;

public class Probe extends Node {
	
	private View myGUI;
	private BitVector v;
	private String IP;
	
	public Probe(){
		myGUI = new View();
		myGUI.setVisible(true);
		myGUI.setProbe(this);
	}
	
	public void connect(String IP){
		this.IP = IP;
		Socket s = handleConnection(IP, PEER_PORT);
		Message m = new Message(GUI, "ACK");
		send(s, m);
		Message r = receive(s);
		if (r.getType()==ACK){
			String response = "ACK";
			myGUI.displayStatus(response);
		}
		else
		{
			String response = "NACK";
			myGUI.displayStatus(response);
		}
		try {
			s.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void generateVector(){
		BitVectorUtils util = new BitVectorUtils();
		v = util.getRandomVector(word_size);
		myGUI.displayVector(v);
		
	}
	
	public void query(){
		Socket s = handleConnection(IP, PEER_PORT);
		Message m = new Message(GUI_REQUEST, v);
		send(s, m);
		try {
			s.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ServerSocket GUISocket;
		try {
			GUISocket = new ServerSocket(GUI_PORT);
			while (true) {
				System.out.println("GUI Listens");
				   Socket connectionSocket = GUISocket.accept();
				   ObjectInputStream input = new ObjectInputStream(connectionSocket.getInputStream());
				   Message fromPeer = (Message) input.readObject();
				   BitVector response = (BitVector) fromPeer.getContent();
				   myGUI.displayResult(response);
				   input.close();
				   connectionSocket.close();
				   break;
			 }
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}	
	}
	
	
	public static void main(String[] args) {
		Probe p = new Probe();
	}



}
