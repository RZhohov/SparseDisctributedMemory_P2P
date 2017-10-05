package p2p_sdm;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import sdm.BitVector;
import sdm.BitVectorUtility;
import sdm.SDMImpl;

public class Peer extends Node {
	
	private Message toPeer;
	private Message fromPeer;
	
	private String super_IP = "localhost";
	private int super_port = 8080;
	
	private SDMImpl sdm;
	private BitVector ID;


	public Peer() {
		sdm = new SDMImpl(memory_size, 100, word_size);
	}
	
	public void init(){
		ID = sdm.generateID();
	}
	
	
	public void join() {
		toPeer = new Message(1, ID);
		Socket conn = handleConnection(super_IP, super_port);
		send(conn, toPeer);
		fromPeer = receive(conn);
		if (fromPeer.getType() == 0){
			System.out.println("Successfully joined ACK");
			try {
				conn.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	


}
