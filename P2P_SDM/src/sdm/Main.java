package sdm;

import java.util.Arrays;

import p2p_sdm.Peer;

public class Main {

	public static void main(String[] args) throws Throwable {
		
		int memory_size = 10;
		int word_size = 100; 
		SDMImpl sdm = new SDMImpl(memory_size, 1, word_size);
		
		BitVector ID = sdm.generateID();
		
		System.out.println(sdm.retrieve(ID).print());
		
	
		
		
	
		
		
		
		
		
	}

}
