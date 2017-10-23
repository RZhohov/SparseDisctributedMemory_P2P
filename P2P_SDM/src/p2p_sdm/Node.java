package p2p_sdm;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import sdm.BitVector;
import sdm.SDMImpl;

public abstract class Node {
	
	protected int memory_size = 100;
	protected int word_size = 1000;
	public int ACK=0, JOIN=1, REQUEST=2, SUPER_REQUEST=3, REPLY=4, GUI=5, GUI_REQUEST=6;
	public int PEER_PORT, SUPER_PORT = 7070;
	//public int SUPER_PORT = 8080;
	public int GUI_PORT = 9090;
	
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
	
	public void send(Socket sock, Message m) {
		try {
			ObjectOutputStream outToPeer = new ObjectOutputStream(sock.getOutputStream());
			outToPeer.writeObject(m);
			//sock.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Message receive(Socket sock){
		ObjectInputStream fromPeer;
		Message m = null;
		try {
			fromPeer = new ObjectInputStream(sock.getInputStream());
			m = (Message) fromPeer.readObject();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return m;
	}
	
	protected BitVector search(SDMImpl s, BitVector o) throws Throwable{
		BitVector v = s.retrieve(o);
		return v;
	}
	
	protected void request(BitVector v, String ip, int port) throws IOException{
		Message m = new Message(REQUEST, v);
		Socket s = handleConnection(ip, port);
		send(s, m);
		s.close();
	}
	
	
	




}
