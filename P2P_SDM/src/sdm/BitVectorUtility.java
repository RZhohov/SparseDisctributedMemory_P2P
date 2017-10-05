package sdm;

import java.util.concurrent.ThreadLocalRandom;

public class BitVectorUtility {
	
	public BitVector getRandomVector(int size){
		BitVector vector = new BitVector(size);
		for (int index=0; index<size; index++)
		{
			int randomNum = ThreadLocalRandom.current().nextInt(0, 1 + 1);
			if (randomNum == 1){
				vector.set(index);
			}
		}
		return vector;
	}
	
}
