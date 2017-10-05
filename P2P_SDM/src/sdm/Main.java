package sdm;

import java.util.Arrays;

public class Main {

	public static void main(String[] args) throws Throwable {
		
		int memory_size = 100;
		int word_size = 10000; 
		SDMImpl sdm = new SDMImpl(memory_size, 100, word_size);
		
		BitVector ID = sdm.generateID();
		
		System.out.println(ID.print());
		
		
		float[] distances = sdm.hammingDistance(ID);
		
		boolean flag =true;
		for (int i=0; i<distances.length; i++){
			if (distances[i]>0.5){
				System.out.print(distances[i]+", ");
				flag=false;
			}
		}
		
		System.out.println(Arrays.toString(distances));
		System.out.println("All ditances < 0.5 - "+flag);
	
		
		
		
		
		
	}

}
