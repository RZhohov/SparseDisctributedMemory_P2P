package p2p_sdm;

import sdm.BitVector;
import sdm.BitVectorUtility;

public class Main {

	public static void main(String[] args) {
		
		BitVectorUtility generator = new BitVectorUtility();
		BitVector vector1 = generator.getRandomVector(100);
		BitVector vector2 = generator.getRandomVector(100);
		vector1.xor(vector2);
		
		
		Peer peer = new Peer();
		peer.init();
		peer.join();
		
		

	}

}
