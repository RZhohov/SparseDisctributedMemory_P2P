package p2p_sdm;

import java.io.Serializable;


class Message implements Serializable {
	
	public int ACK=0, JOIN=1, REQUEST=2, REPLY=3; 
	private int type;
	private Object content;
	
	/**
	 * Creates Message = {type, content}
	 * @param type
	 * @param content
	 */
	public Message(int type, Object content){
		this.type = type;
		this.content = content;
	}
	
	/**
	 * Returns type of Message
	 * @return type
	 */
	public int getType(){
		return type;
	}
	
	/**
	 * Returns content of Message: String, BitVector, etc.
	 * @return content
	 */
	public Object getContent(){
		return content;
	}
	
}
