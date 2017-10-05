package sdm;

public interface SDM {
	public interface SparseDistributedMemory {

		/**
		 * Stores word in the given address in this sparse distributed memory.
		 * 
		 * @param word
		 *            the word to be stored
		 * @param addr
		 *            the address where the word is to be stored
		 */
		public void store(BitVector word, BitVector addr);

		/**
		 * Stores word in this sparse distributed memory using the word as address.
		 * 
		 * @param word the word to be stored
		 */
		public void store(BitVector word);

		/**
		 * Stores word in this sparse distributed memory using the word as address.
		 * The word is mapped (using XOR) with the mapping address.
		 * 
		 * @param word
		 *            the word to be stored.
		 * @param mapping
		 *            the mapping address.
		 */
		public void mappedStore(BitVector word, BitVector mapping);

		/**
		 * Retrieves the contents of this sparse distributed memory at the given
		 * address.
		 * 
		 * @param addr
		 *            the address of the contents to be retrieved
		 * @return the contents of this sparse distributed memory associated with
		 *         the given address
		 */
		public BitVector retrieve(BitVector addr);

		/**
		 * Retrieves the contents of this sparse distributed memory at the given
		 * address iterating this process until result is equal to the address. 
		 * 
		 * 
		 * @param addr
		 *            the address of the contents to be retrieved
		 * @return the contents of this sparse distributed memory associated with
		 *         the given address or null if the iteration did not converge
		 */
		public BitVector retrieveIterating(BitVector addr);

		/**
		 * 
		 * Retrieves the contents of this SDM at specified address. 
		 * The address is first mapped using specified mapping then
		 * the mapped address is used to retrieve the contents of this SDM until result of retrieval is 
		 * equal to address.  
		 * @param addr {@link BitVector} address that is being retrieved
		 * @param mapping mapping to use
		 * @return the contents of this sparse distributed memory associated with
		 *         the given address or null if the iteration did not converge
		 */
		public BitVector retrieveIterating(BitVector addr,
				BitVector mapping);

		/**
		 * Retrieves the contents of this SDM at specified address. 
		 * The address is first mapped using specified mapping then
		 * the mapped address is used to retrieve content from this SDM.<br/>
		 * Note that the retrieved {@link BitVector} is mapped back before it is returned.
		 *  
		 * @param addr the address vector
		 * @param mapping the mapping vector
		 * @return the contents of this sparse distributed memory associated with
		 *         the given address or null if the iteration did not converge
		 */
		public BitVector retrieve(BitVector addr, BitVector mapping);

	}

}
