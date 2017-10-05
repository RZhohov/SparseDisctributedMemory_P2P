package sdm;



public class HardLocation  {
	
	
	private static final byte DEFAULT_COUNTER_MAX = 40;
	private final byte counterMax = DEFAULT_COUNTER_MAX;
	
	private BitVector address;
	private int wordLength;
	private byte[] counters;
	private int writeCount;

	/**
	 * Constructs a new hard location with specified address and length.
	 * @param address {@link BitVector}
	 * @param wordLength length of the words
	 */
	public HardLocation(BitVector address, int wordLength) {
		this.address = address;
		this.wordLength = wordLength;
		counters = new byte[wordLength];
	}

	/**
	 * Constructs a new hard location with specified address
	 * @param address {@link BitVector} address of this HardLocation
	 */
	public HardLocation(BitVector address) {
		this(address, address.size());
	}

	public BitVector getAddress() {
		return address;
	}

	public void setAddress(BitVector address) {
		this.address = address;
	}

	public byte[] getCounters() {
		return counters;
	}


	public int getWriteCount() {
		return writeCount;
	}

	public void write(BitVector word) {
		writeCount++;
		int size = word.size();

		// if (size>wordLength){
		// throw new IllegalArgumentException();
		// }
		for (int j = 0; j < size; j++) {
			if (word.getQuick(j)) {
				if (counters[j] < counterMax) {
					counters[j] += 1;
				}
			} else {
				if (counters[j] > -counterMax) {
					counters[j] += -1;
				}
			}
		}
	}


	public void setCounters(byte[] newCounters) {
		for (int i = 0; i < this.wordLength; i++) {
			counters[i] = newCounters[i];
		}
	}


	public int[] read(int[] buff) {

		// if (buff.length<wordLength){
		// throw new IllegalArgumentException();
		// }

		for (int i = 0; i < wordLength; i++) {
//			int inc=0;
			buff[i] += Integer.signum(counters[i]);
		}
		return buff;
	}


	public int hammingDistance(BitVector vector) throws Throwable {
		if(vector == null){
			//logger.log(Level.WARNING,"The vector can not be null.",TaskManager.getCurrentTick());
			return Integer.MAX_VALUE;
		}
		
		BitVector aux = vector.copy();
		aux.xor(address);

		return aux.cardinality();
	}
	

}