package p2p_sdm;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import sdm.BitVector;
import sdm.BitVectorUtility;

public class Peer implements Node {


	private BitVector vector;
	private BitVector ID;
	
	private String super_IP = "localhost";
	private int super_port = 8080;
	
	
	public void Init() {
		BitVectorUtility generator = new BitVectorUtility();
		BitVector vector = generator.getRandomVector(100);
		BitVector vector2 = generator.getRandomVector(100);	
	}

	
	public void join(BitVector ID) {		
		Socket conn = handleConnection(super_IP, super_port);
		try {
			ObjectOutputStream outToPeer = new ObjectOutputStream(conn.getOutputStream());
			outToPeer.writeObject(ID);
			conn.close();
		} catch (IOException e) {
			e.printStackTrace();
		}	
		
	}
	
	public void request(BitVector query){
		String message = "query";
		Socket conn = handleConnection(super_IP, super_port);
		try {
			ObjectOutputStream outToPeer = new ObjectOutputStream(conn.getOutputStream());
			outToPeer.writeObject(message);
			outToPeer.writeObject(query);
			conn.close();
		} catch (IOException e) {
			e.printStackTrace();
		}			
	}
	
	public Socket handleConnection(String IP, int port){
		  Socket conn = null;
		try {
			conn = new Socket(IP, port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		  return conn;
	}
	
	
	
}
