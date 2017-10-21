package probeGUI;

import java.io.IOException;
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
		//add receive and display results
		try {
			s.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	public static void main(String[] args) {
		Probe p = new Probe();
		
	}



}
