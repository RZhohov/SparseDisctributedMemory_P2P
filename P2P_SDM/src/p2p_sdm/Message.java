package p2p_sdm;

import java.io.Serializable;

//Message = {"TYPE", "CONTENT"}

class Message implements Serializable {
	
	public int ACK=0, JOIN=1, REQUEST=2, REPLY=3; 
	
	private int type;
	private Object content;
	
	public Message(int type, Object content){
		this.type = type;
		this.content = content;
	}
	
	//Should return Type of message
	public int getType(){
		return type;
	}
	
	//Should return Content of message (e.g BitVector, String, etc.)
	public Object getContent(){
		return content;
	}
	
	

}
