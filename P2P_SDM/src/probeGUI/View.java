package probeGUI;


import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import sdm.BitVector;

import javax.swing.JTextArea;



public class View extends JFrame{
	private JTextField IPField;
	private JTextField statusField;
	private JTextField vectorField;
	private Probe p;
	
	public View(){
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//setExtendedState(JFrame.MAXIMIZED_BOTH);
		setSize(510, 490);
		//getContentPane().setLayout(null);
		Component content = this.createContents();
		getContentPane().add(content);
		setDefaultLookAndFeelDecorated(true);
	}
	
	public void setProbe(Probe p){
		this.p = p;		
	}
	
	public void displayStatus(String s){
		statusField.setText(s);
	}
	
	public void displayVector(BitVector v){
		vectorField.setText(v.print());
	}

	protected Component createContents() {
		JPanel mainpanel = new JPanel();
		mainpanel.setLayout(null);
		
		JLabel lblConnectToPeer = new JLabel("Connect to Peer:");
		lblConnectToPeer.setBounds(15, 16, 124, 20);
		mainpanel.add(lblConnectToPeer);
		
		JLabel lblIp = new JLabel("IP");
		lblIp.setBounds(15, 52, 34, 20);
		mainpanel.add(lblIp);
		
		IPField = new JTextField();
		IPField.setBounds(42, 52, 97, 20);
		mainpanel.add(IPField);
		IPField.setColumns(10);
		
		JButton btnConnect = new JButton("Connect");
		btnConnect.setBounds(177, 28, 115, 29);
		mainpanel.add(btnConnect);
		btnConnect.addActionListener(new ActionListener() {
			   public void actionPerformed(ActionEvent e) {
			        System.out.println("Button Connect pressed");
			        String IP = IPField.getText();
			        p.connect(IP);
			   }
			});
		
		statusField = new JTextField();
		statusField.setBounds(386, 28, 72, 29);
		mainpanel.add(statusField);
		statusField.setColumns(10);
		
		JLabel lblStatus = new JLabel("Status:");
		lblStatus.setBounds(322, 32, 49, 20);
		mainpanel.add(lblStatus);
		
		JLabel lblBitvectorToSearch = new JLabel("BitVector to Search:");
		lblBitvectorToSearch.setBounds(15, 115, 164, 20);
		mainpanel.add(lblBitvectorToSearch);
		
		vectorField = new JTextField();
		vectorField.setBounds(15, 151, 443, 26);
		mainpanel.add(vectorField);
		vectorField.setColumns(10);
		
		JButton btnGenerate = new JButton("Generate");
		btnGenerate.setBounds(358, 193, 115, 29);
		mainpanel.add(btnGenerate);
		btnGenerate.addActionListener(new ActionListener() {
			   public void actionPerformed(ActionEvent e) {
			        System.out.println("Button Generate pressed");
			        p.generateVector();
			        
			   }
			});
		
		JTextArea textArea = new JTextArea();
		textArea.setBounds(15, 238, 458, 180);
		mainpanel.add(textArea);
		
		JLabel lblResult = new JLabel("Result:");
		lblResult.setBounds(15, 197, 69, 20);
		mainpanel.add(lblResult);
		
		JButton btnSearch = new JButton("Search");
		btnSearch.setBounds(228, 193, 115, 29);
		mainpanel.add(btnSearch);
		btnSearch.addActionListener(new ActionListener() {
			   public void actionPerformed(ActionEvent e) {
			        System.out.println("Button Search pressed");
			        p.query();
			   }
			});
				
		return mainpanel;

	}
	
	public static void main(String[] args){
		View view = new View();
		view.setVisible(true);
	}
}
