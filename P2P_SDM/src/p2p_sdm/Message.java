package p2p_sdm;

import java.io.Serializable;


public class Message implements Serializable {
	
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
