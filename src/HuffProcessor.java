
/**
 * Although this class has a history of several years,
 * it is starting from a blank-slate, new and clean implementation
 * as of Fall 2018.
 * <P>
 * Changes include relying solely on a tree for header information
 * and including debug and bits read/written information
 * 
 * @author Owen Astrachan
 */
//Samhitha Sunkara 
//Malavi Ravindran
public class HuffProcessor {

	public static final int BITS_PER_WORD = 8;
	public static final int BITS_PER_INT = 32;
	public static final int ALPH_SIZE = (1 << BITS_PER_WORD); 
	public static final int PSEUDO_EOF = ALPH_SIZE;
	public static final int HUFF_NUMBER = 0xface8200;
	public static final int HUFF_TREE  = HUFF_NUMBER | 1;

	private final int myDebugLevel;
	
	public static final int DEBUG_HIGH = 4;
	public static final int DEBUG_LOW = 1;
	
	public HuffProcessor() {
		this(0);
	}
	
	public HuffProcessor(int debug) {
		myDebugLevel = debug;
	}

	/**
	 * Compresses a file. Process must be reversible and loss-less.
	 *
	 * @param in
	 *            Buffered bit stream of the file to be compressed.
	 * @param out
	 *            Buffered bit stream writing to the output file.
	 */
	public void compress(BitInputStream in, BitOutputStream out){

		while (true){
			int val = in.readBits(BITS_PER_WORD);
			if (val == -1) break;
			out.writeBits(BITS_PER_WORD, val);
		}
		out.close();
	}
	/**
	 * Decompresses a file. Output file must be identical bit-by-bit to the
	 * original.
	 *
	 * @param in
	 *            Buffered bit stream of the file to be decompressed.
	 * @param out
	 *            Buffered bit stream writing to the output file.
	 */
	public void decompress(BitInputStream in, BitOutputStream out){
		int bits = in.readBits(BITS_PER_INT);
		if (bits != HUFF_TREE) {
			throw new HuffException("illegal heder starts with "+bits);
		}
		HuffNode root = readTreeHeader(in);
		readCompressedbits(root,in,out);
		out.close();
		//while (true){
			//int val = in.readBits(BITS_PER_WORD);
		//	if (val == -1) break;
		//	out.writeBits(BITS_PER_WORD, val);
		//}
		//out.close();
	}
	private HuffNode readTreeHeader(BitInputStream in) {
		while(true) {
			int bits = in.readBits(1);
			if (bits == -1) {
				throw new HuffException ("bad input, no PSEUDO_EOF");
				
			}
		
			if (bits == 0) {
				HuffNode left = readTreeHeader(in);
				HuffNode right = readTreeHeader(in);
				return new HuffNode (0,0,left,right);
			
			}
			else {
				int value = in.readBits(BITS_PER_WORD+1);
				return new HuffNode (value,0,null,null);
			}
		
		}
		
	}
	
	private void readCompressedbits(HuffNode root, BitInputStream in, BitOutputStream out) {
		HuffNode current = root;
		while(true) {
			int bits = in.readBits(1);
			if (bits == -1) {
				throw new HuffException ("bad input, no PSEUDO_EOF");
				
			}
			else {
				if (bits == 0) current = current.left;
				else current = current.right;
				if (current is a leaf node) {
					if (current.value==PSEUDO_EOF)
						break;
					else {
						write bits for current.value;
						current = root; //start back after leaf
					}
				}
			}
		}
		
	}

	
}