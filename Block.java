import java.sql.Timestamp;

public class Block {
	
	private int index;
	private Timestamp timestamp;
	private Transaction transaction;
	private String nonce;
	private String previousHash; // In first block, set to string of zeroes of size of complexity "00000"
	private String hash; //hash of block (hash of string obtained from previous variables via toString() method)

	public Block(int index, Timestamp timestamp, Transaction transaction, String nonce, String previousHash, String hash){

		if(transaction == null || nonce == null || previousHash == null || hash == null || timestamp == null){
			throw new IllegalArgumentException("Block instantiating values cannot be null");
		}

		if(index < 0){
			throw new IllegalArgumentException("Index cannot be less than 0");
		}

		this.index 		  = index; 
		this.timestamp    = timestamp;
		this.transaction  = transaction;
		this.nonce 		  = nonce; 
		this.previousHash = previousHash; 
		this.hash  		  = hash;

	}

	public void setIndex(int index){
		this.index = index;
	}

	public int getIndex(){
		return this.index;
	}

	public void setNonce(String nonce){
		this.nonce = nonce;
	}

	public String getNonce(){
		return this.nonce;
	}

	public Timestamp getTimestamp(){
		return this.timestamp;
	}

	public void setPreviousHash(String prevHash){
		this.previousHash = prevHash;
	}

	public String getPreviousHash(){
		return this.previousHash;
	}
	
	public void setHash(String hash){
		this.hash = hash;
	}

	public String getHash(){
		return this.hash;
	}

	public Transaction getTransaction(){
		return this.transaction;
	}

	public String toString(){
		return timestamp.toString() + ":" + transaction.toString()
			+ "." + nonce + previousHash;
	}
}