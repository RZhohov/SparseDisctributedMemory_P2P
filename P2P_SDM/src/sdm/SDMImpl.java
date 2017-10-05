package sdm;

import java.util.logging.Logger;


public class SDMImpl implements SDM {

	
	/*
	 * Maximum iterations allowed in determining whether a retrieval converges 
	 */
	private static final int MAX_ITERATIONS = 20;
	
	/*
	 * The hard locations that store data in this sdm
	 */
	private HardLocation[] hardLocations;
	
	/*
	 * Number of hard locations in this sdm 
	 */
	private int memorySize;
	
	/*
	 * Size of vectors stored in the hard locations
	 */
	private int wordLength;
	
	/*
	 * Size of the address vectors 
	 */
	private int addressLength;
	
	/*
	 * Size of the radius of the hypersphere used to find the "nearby" hard locations of an address 
	 */
	private int activationRadius;

	/**
	 * Constructs a new {@link SparseDistributedMemory} with equal address and word sizes 
	 * 
	 * @param memorySize
	 *            number of hard locations
	 * @param radius
	 *            the activation radius used to find the nearest hard locations
	 * @param wordLength
	 *            the word size and the address size
	 */
	public SDMImpl(int memorySize, int radius, int wordLength) {
		this.memorySize = memorySize;
		hardLocations = new HardLocation[memorySize];
		for (int i = 0; i < memorySize; i++) {
			hardLocations[i] = new HardLocation(BitVectorUtils.getRandomVector(wordLength));
		}
		
		this.activationRadius = radius;
		this.wordLength = wordLength;
		this.addressLength = wordLength;
	}

	/**
	 * Constructs a new {@link SparseDistributedMemory} with specified parameters
	 * 
	 * @param memorySize
	 *            the number of hard location
	 * @param radius
	 *            the activation radius used to find the nearest hard locations
	 * @param wordLength
	 *            size of vectors stored at hard locations
	 * @param addrLength
	 *            the address size
	 */
	public SDMImpl(int memorySize, int radius, int wordLength, int addrLength) {
		this.memorySize = memorySize;
		hardLocations = new HardLocation[memorySize];
		for (int i = 0; i < memorySize; i++) {
			hardLocations[i] = new HardLocation(BitVectorUtils.getRandomVector(addrLength), wordLength);
		}
		
		this.activationRadius = radius;
		this.wordLength = wordLength;
		this.addressLength = addrLength;
	}


	public void store(BitVector wrd, BitVector addr) throws Throwable {
		for (int i = 0; i < memorySize; i++) {
			if (hardLocations[i].hammingDistance(addr) <= activationRadius) {
				hardLocations[i].write(wrd);
			}
		}
	}


	public void store(BitVector wrd) throws Throwable {
		store(wrd, wrd);
	}


	public void mappedStore(BitVector wrd, BitVector mapping) throws Throwable {
		if (wrd.size() == addressLength) {
			BitVector mapped = wrd.copy();
			mapped.xor(mapping);
			store(mapped);
		} else {
			BitVector mapped = wrd.partFromTo(0, addressLength - 1);
			mapped.xor(mapping);
			BitVector aux = wrd.copy();
			aux.replaceFromToWith(0, addressLength - 1, mapped, 0);
			store(aux, mapped);
		}
	}


	public BitVector retrieve(BitVector addr) throws Throwable {
		int[] locationsSum = new int[wordLength];
		for (int i = 0; i < memorySize; i++) {
			if (hardLocations[i].hammingDistance(addr) <= activationRadius) {
				hardLocations[i].read(locationsSum);
			}
		}
		BitVector res = new BitVector(wordLength);
		for (int i = 0; i < wordLength; i++) {
			boolean aux;
			if (locationsSum[i]==0) {
				//not clear if sum is positive or negative, so assign randomly
				aux = (Math.random() > 0.5);
			} else {
				aux = (locationsSum[i] > 0);
			}
			res.putQuick(i, aux);
		}
		return res;
	}


	public BitVector retrieve(BitVector addr, BitVector mapping) throws Throwable {
		BitVector mapped = addr.copy();
		mapped.xor(mapping);
		BitVector res = retrieve(mapped);
		if (res != null) {
			if (res.size() == addressLength) {
				res.xor(mapping);
			} else {
				BitVector aux = res.partFromTo(0, addressLength - 1);
				aux.xor(mapping);
				res.replaceFromToWith(0, addressLength - 1, aux, 0);
			}
		}
		return res;
	}


	public BitVector retrieveIterating(BitVector addr) throws Throwable {
		BitVector res = null;
		for (int i = 1; i < MAX_ITERATIONS; i++) {
			res = retrieve(addr);
			BitVector aux = res.partFromTo(0, addr.size() - 1);
			// TODO hamming distance tolerance instead of strict equality
			if (aux.equals(addr)) {
				//logger.log(Level.FINER, "number of iterations: {1}", new Object[]{TaskManager.getCurrentTick(), i});
				return res;
			}
			addr = aux;
		}
		return null;
	}


	public BitVector retrieveIterating(BitVector addr, BitVector mapping) throws Throwable {
		BitVector mapped = addr.copy();
		mapped.xor(mapping);

		BitVector res = retrieveIterating(mapped);
		if (res != null) {
			if (res.size() == addressLength) {
				res.xor(mapping);
			} else {
				BitVector aux = res.partFromTo(0, addressLength - 1);
				aux.xor(mapping);
				res.replaceFromToWith(0, addressLength - 1, aux, 0);
			}
		}
		return res;
	}
	
	public BitVector generateID(){
	    BitVectorUtils bitVectorHelper = new BitVectorUtils();
	    int[] sum = new int[wordLength];
		for (int i = 0; i < memorySize; i++) {
			BitVector binary = hardLocations[i].getAddress();
			int[] bipolar = bitVectorHelper.vectorToBipolar(binary);
			for (int j=0; j<wordLength; j++){
				sum[j]=sum[j]+bipolar[j];
			}
		}
		return BitVectorUtils.bipolarToVector(sum);
		}
	
	public float[] hammingDistance(BitVector v) throws Throwable{
		float[] dst = new float[memorySize];
		for (int i = 0; i < memorySize; i++) {
			dst[i] = (float) hardLocations[i].hammingDistance(v)/(float) wordLength;
		}
		return dst;
		
	}
	

}
