package p2p_sdm;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import sdm.BitVector;
import sdm.SDMImpl;

/*
 * Roman Zhohov, LTU, D7001 Network Programming, 2017
 */


public abstract class Node {
	
	protected int memory_size = 1000;
	protected int word_size = 1000;
	protected int T = word_size/2;
	public int ACK=0, JOIN=1, REQUEST=2, SUPER_REQUEST=3, REPLY=4, GUI=5, GUI_REQUEST=6;
	public int PEER_PORT = 7070;
	public int GUI_PORT = 9090;
	
	/**
	 * Handles connection to the host(IP; PORT)
	 * @param IP
	 * @param port
	 * @return Socket
	 */
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
	
	/**
	 * Sends message using socket
	 * @param sock
	 * @param m - message to be sent
	 */
	public void send(Socket sock, Message m) {
		try {
			ObjectOutputStream outToPeer = new ObjectOutputStream(sock.getOutputStream());
			outToPeer.writeObject(m);
			//sock.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Receives message from host
	 * @param sock
	 * @return Message
	 */
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
	
	/**
	 * Searches BitVector o in SDMImpl s
	 * @param s - SDMImpl
	 * @param o - BitVector to retrieve from SDMImpl
	 * @return - retrieved BitVector
	 * @throws Throwable
	 */
	protected BitVector search(SDMImpl s, BitVector o) throws Throwable{
		BitVector v = s.retrieve(o);
		return v;
	}
	
	
	
	




}
