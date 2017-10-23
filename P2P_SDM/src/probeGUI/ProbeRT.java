package probeGUI;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import p2p_sdm.Message;
import p2p_sdm.Node;
import sdm.BitVector;
import sdm.BitVectorUtils;

public class ProbeRT extends Node {
	
	private ViewRT myGUI;
	private BitVector v;
	private String IP;
	
	public void initUI(){
		myGUI = new ViewRT();
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
	
	public void doWork(){
		 SwingWorker<Void, BitVector> worker = new SwingWorker<Void, BitVector>() {
			protected Void doInBackground() throws Exception {
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
						   publish(response);
						   input.close();
						   connectionSocket.close();
					 }
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}	
				return null;
			}
			
            protected void process(List<BitVector> chunks) {
                myGUI.displayResult(chunks.get(chunks.size() - 1));
            }

            protected void done() {
            }
			 
		 };
		 worker.execute();
	}
	
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ProbeRT().initUI();
            }
        });
    }
}

