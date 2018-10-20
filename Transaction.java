public class Transaction {
	
	private String sender;
	private String receiver;
	private int amount;

	public Transaction(String sender, String receiver, int amount){

		if(sender == null || receiver == null){
			throw new IllegalArgumentException("Transaction values cannot be null");
		}

		if(amount <= 0){
			throw new IllegalArgumentException("Amount cannot be less than 0");
		}

		this.sender   = sender; 
		this.receiver = receiver;
		this.amount   = amount;
	}

	public void setSender(String sender){
		this.sender = sender;
	}

	public String getSender(){
		return this.sender;
	}

	public void setReceiver(String receiver){
		this.receiver = receiver;
	}

	public String getReceiver(){
		return this.receiver;
	}

	public int getAmount(){
		return this.amount;
	}

	public String toString(){
		return sender + ":" + receiver + "=" + amount;
	}
}