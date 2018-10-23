import java.io.*;
import java.util.Scanner;
import java.util.HashMap;
import java.io.FileWriter;
import java.util.Random;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
@SuppressWarnings("unchecked")

public class BlockChain {
	private ArrayList<Block> blocks;


	/**
	 * Entry point of the Java program. Calls the startUp method to begin the block chain validation/transaction process
	 * @param {String[]} args [Takes input from command line]
	 */
	public static void main(String[] args){
		startUp();
	}
	

	/**
	 * BlockChain constructor which takes a Block object and adds it to the block chain.
	 * @param {Block} block [The initial block to be added to the chain]
	 */
	public BlockChain(Block block){
		if(block != null){
			blocks = new ArrayList();			
			blocks.add(0, block);
		}

		else{
			throw new IllegalArgumentException("Block cannot be null");
		}
	}


	/**
	 * Starting method for main. First prompts the user for the file name, then validates the blockchain, then asks 
	 * if the user would like to add more transactions to the block chain
	 *  
	 */
	public static void startUp(){
		String bitcoinBankFilename;
		boolean untilValidFileName = false;
		BlockChain blockChain;
		while(!untilValidFileName){

			try{
				bitcoinBankFilename = promptUserForFilename();
				blockChain = fromFile(bitcoinBankFilename + ".txt");	

				//2. Validate blockchain
				boolean blockChainValidated = blockChain.validateBlockChain();

				//3. Prompt user for new transaction and verify it
				if(blockChainValidated){
					System.out.println("Your current block chain was validated!");
					System.out.println("Would you like to add more transactions? ");
					boolean validAnswer = false;
					while(!validAnswer){
						Scanner scanner  = new Scanner(System.in);
						String userAnswer = scanner.nextLine();
						if(userAnswer.equals("yes")){
							validAnswer = true;
							blockChain.addTransactionToChain(blockChain);
						}
						else if(userAnswer.equals("no")){
							validAnswer = true;
							untilValidFileName = true;
							break;
						}
						else{
							validAnswer = false;
							System.out.println("Invalid input");
						}
					}
					untilValidFileName = true;
					//6. Add to textfile regardless of new transaction being added
					blockChain.toFile(bitcoinBankFilename);
				}
				else{
					System.out.println("Block chain was not validated");
				}
			}
			catch(FileNotFoundException e){
				System.out.println("Illegal file name");
			}
		}
	}


	/**
	 * Prompts user for the file name to read block chain information from
	 * @return {String} File name value of the file to parse through
	 */
	public static String promptUserForFilename(){
		boolean validSenderInput = false;
		String senderInput;
		Scanner scannerInput = new Scanner(System.in);
		while(!validSenderInput){
			System.out.println("Please enter the bitcoin bank file name you would like to read from (please do not include '.txt' at the end of file name)");
			senderInput = scannerInput.nextLine();
			if(senderInput.equals("")){
				System.out.println("Invalid file");
			}
			else{
				validSenderInput = true;
				return senderInput;
			}
		}
		return null;
	}


	/**
	 * Prompts user for the desired sender of the transaction
	 * @return {String} Username of the sending party of the transaction
	 */
	public static String promptUserForSenderInput(){
		boolean validSenderInput = false;
		String senderInput;
		Scanner scannerInput = new Scanner(System.in);
		while(!validSenderInput){
			System.out.println("Please enter the Sender: ");
			senderInput = scannerInput.nextLine();
			if(senderInput.equals("")){
				System.out.println("Invalid Sender");
			}
			else{
				validSenderInput = true;
				return senderInput;
			}
		}
		return null;
	}


	/**
	 * Prompts the user for the desired receiver of the transaction
	 * @return {String} Username of the receiving party of the transaction
	 */
	public static String promptUserForReceiverInput(){
		boolean validReceiverInput = false;
		String receiverInput;
		Scanner scannerInput = new Scanner(System.in);

		while(!validReceiverInput){
			System.out.println("Please enter the Receiver: ");
			receiverInput = scannerInput.nextLine();
			if(receiverInput.equals("")){
				System.out.println("Invalid Receiver");
			}
			else{
				validReceiverInput = true;
				return receiverInput;
			}
		}
		return null;
	}


	/**
	 * Prompts the user for the desired transaction amount
	 * @return {int} BitCoin value of transaction
	 */
	public static int promptUserForAmount(){
		int transactionAmount = 0;
		boolean validAmount = false;
		Scanner scannerInput = new Scanner(System.in);

		while(!validAmount){
			System.out.println("Please enter a transaction amount: ");
			String transactionValue = scannerInput.nextLine();
			try{
				transactionAmount = Integer.parseInt(transactionValue);
				if(transactionAmount <= 0){
					System.out.println("Please enter an amount > 0");
				}
				else{
					validAmount = true;
					return transactionAmount;
				}
			}
			catch(NumberFormatException e){
				System.out.println("Invalid amount");
			}
		}
		return transactionAmount;
	}


	/**
	 * Allows user to add transactions to the blockChain. The user can add the desired amount of validated transactions 
	 * @param {BlockChain} blockChain [The BlockChain which transactions are being added]
	 */
	public void addTransactionToChain(BlockChain blockChain){
		int amountToSend;
		String senderInput, receiverInput, userAnswer;
		Scanner scannerInput = new Scanner(System.in);
		boolean addAnotherTransaction = true, validInput = true;

		while(addAnotherTransaction){
			senderInput = blockChain.promptUserForSenderInput();
			receiverInput = blockChain.promptUserForReceiverInput();
			amountToSend = blockChain.promptUserForAmount();
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			Transaction transaction = new Transaction(senderInput, receiverInput, amountToSend);
			int incrementedIndex;
			String previousHash;

			//4. Adding transaction to blockchain
			if(blockChain.getBalance(senderInput) >= amountToSend){
				int currentBlockChainLength = blockChain.blocks.size();

				if(currentBlockChainLength == 0){
					incrementedIndex = 0;
					previousHash = "00000";
				}
				else{
					incrementedIndex = blockChain.blocks.get(currentBlockChainLength - 1).getIndex() + 1;
					previousHash 	 = blockChain.blocks.get(currentBlockChainLength - 1).getHash();
				}

				String nonce = blockChain.generateNonce(timestamp.toString(), transaction.toString(), previousHash);

				//Make hash with all new strings
				try{

					String hash = Sha1.hash(timestamp.toString() + ":" + transaction.toString() + "." + nonce + previousHash);
					Block newBlock  = new Block(incrementedIndex, timestamp, transaction, nonce, previousHash, hash);
					blockChain.add(newBlock);
					validInput = false;

					while(!validInput){
						validInput = false;
						System.out.println("Would you like to add another Transaction?" + "\n" + 
										"Please enter yes or no");
						userAnswer = scannerInput.nextLine();
						if(userAnswer.equals("yes")){
							validInput = true;
							addAnotherTransaction = true;
						}
						else if(userAnswer.equals("no")){
							validInput = true;
							addAnotherTransaction = false;
							//toFile();
						}
						else{
							System.out.println("Invalid entry. Please try again");{
								validInput = false;
							}
						}
					}
				}
				catch(UnsupportedEncodingException e){
					e.printStackTrace();
				}
			}
			else{
				System.out.println("Insufficient Funds");
			}
		}
	}


	/**
	 * Reads and adds the blockchain values from the file given. Scans through all the information of the block while adding 
	 * values to a hash map. After the Scanner has retrieved all block information, it generates a new block and adds to the chain.
	 * @param {String} filename [Reads block chain information from this file]
	 * @return {BlockChain} The new BlockChain object created after parsing through file
	 * @throws FileNotFoundException [description]
	 */
	public static BlockChain fromFile(String filename) throws FileNotFoundException{
		//Read file and add blocks into ArrayList
		//filename is path location of file
		HashMap<Integer, String> fileValuesMap = new HashMap();
		int blockNumber = 0;		
		int lineNumber = 0;
		BlockChain blockChain;
		Scanner scanner;
		File file;

		if(filename == null){
			throw new IllegalArgumentException("File name cannot be null");
		}

		try{
			file = new File(filename);

			try{
				scanner = new Scanner(file);

				for(int i = 0; i < 7; i++){
					fileValuesMap.put(i, scanner.nextLine());
				}

				int index 				= Integer.parseInt(fileValuesMap.get(0));
				Timestamp timestamp 	= new Timestamp(Long.parseLong(fileValuesMap.get(1))); 
				String sender		    = fileValuesMap.get(2);
				String receiver 		= fileValuesMap.get(3);
				int amountOfTransaction = Integer.parseInt(fileValuesMap.get(4));
				String nonce 			= fileValuesMap.get(5);
				String expectedHash 	= fileValuesMap.get(6);
				Transaction transaction = new Transaction(sender, receiver, amountOfTransaction);
				Block block             = new Block(index, timestamp, transaction, nonce, "00000", expectedHash);
				blockChain 				= new BlockChain(block);
				blockNumber++;
				fileValuesMap.clear();

				while(scanner.hasNext()){
					String currentScannerValue = scanner.nextLine();
					fileValuesMap.put(lineNumber, currentScannerValue);
					lineNumber++;

					if(lineNumber == 7){
						
						//Use values stored in hash map and create a Block and Transaction.

						index 				= Integer.parseInt(fileValuesMap.get(0));
						timestamp 			= new Timestamp(Long.parseLong(fileValuesMap.get(1))); 
						sender 				= fileValuesMap.get(2);
						receiver 			= fileValuesMap.get(3);
						amountOfTransaction = Integer.parseInt(fileValuesMap.get(4));
						nonce 				= fileValuesMap.get(5);
						expectedHash 		= fileValuesMap.get(6);
						transaction 		= new Transaction(sender, receiver, amountOfTransaction);
						String previousHash = blockChain.getPreviousHash(blockChain, blockNumber);
						block               = new Block(index, timestamp, transaction, nonce, previousHash, expectedHash);
						blockChain.add(block);
						blockNumber++;				
						
						//Reset counter and clear all mappings from hash map.
						lineNumber = 0;
						fileValuesMap.clear();
					}
				}
				return blockChain;
			}

			catch(FileNotFoundException e){
				throw new FileNotFoundException();
				//return null;
			}
		}
		catch(NullPointerException e){
			e.printStackTrace();
		}
		return null;
	}


	/**
	 * Returns the previous Blocks hash value
	 * @param {BlockChain} blockChain [Current blockChain object to access static information]
	 * @param {int} blockNumber [Current block index]
	 * @return {String} value of the previous hash
	 */
	public static String getPreviousHash(BlockChain blockChain, int blockNumber){
		return blockChain.blocks.get(blockNumber - 1).getHash();
	}


	/**
	 * Adds the Block given to the ArrayList of Blocks at the next available index
	 * @param {Block} block [Block to add to the chain]
	 */
	public void add(Block block){
		blocks.add(block.getIndex(), block);
	}


	/**
	 * Writes the blockChain values to the desired text file
	 * @param {String} filename [The desired file name the user has selected]
	 */
	public void toFile(String filename){
		ArrayList<Block> blockList = this.getBlocks();
		int size = blockList.size();
		File file;
		PrintWriter printWriter;

		try{
			file = new File(filename + "_ghope049.txt");
			try{
				printWriter = new PrintWriter(file, "UTF-8");
				
				for(int i = 0; i < size; i++){

						String nonce 		= blockList.get(i).getNonce();
						String expectedHash = blockList.get(i).getHash();
						String index 		= "" + blockList.get(i).getIndex();
						String amount 		= "" + blockList.get(i).getTransaction().getAmount();
						String timestamp 	= String.valueOf(blockList.get(i).getTimestamp().getTime());
						String sender 		= blockList.get(i).getTransaction().getSender();
						String receiver 	= blockList.get(i).getTransaction().getReceiver();
						
						printWriter.println(index);
						printWriter.println(timestamp);
						printWriter.println(sender);
						printWriter.println(receiver);
						printWriter.println(amount);
						printWriter.println(nonce);
						printWriter.println(expectedHash);			
				}
				printWriter.close();
			}
			catch(SecurityException | IOException e){
				e.printStackTrace();
			}
		}
		catch(NullPointerException e){
			e.printStackTrace();
		}
	}


	/**
	 * Iterates through each Block in the chain and ensures correct values for index, previous hash, and current hash.
	 * @return {boolean} determining whether the blockChain was successfully validated 
	 */
	public boolean validateBlockChain(){
		int numberOfBlocks = blocks.size();
		String toStringHashValue;
		boolean isValid = true;

		for(int i = 0; i < numberOfBlocks; i++){

			//Check Index
			String toStringValueFromBlock = blocks.get(i).toString();
			if(i != blocks.get(i).getIndex()){
				isValid = false;
				break;
			}

			//Check Previous Hash
			if(blocks.get(i).getIndex() == 0){
				if(!(blocks.get(0).getPreviousHash().equals("00000"))){
					isValid = false;
					break;
				}
			}
			else{
				if(!(blocks.get(i).getPreviousHash().equals(blocks.get(i-1).getHash()))){
					isValid = false;
					break;
				}
			}

			//Check Current Hash
			try{
				toStringHashValue = Sha1.hash(toStringValueFromBlock);
				if(!(blocks.get(i).getHash().equals(toStringHashValue))){
					isValid = false;
					break;
				}
			}
			catch(UnsupportedEncodingException e){
				e.printStackTrace();
			}

			//Check Balance
			String receiver 	= blocks.get(i).getTransaction().getReceiver();
			String sender       = blocks.get(i).getTransaction().getSender();
			int receiverBalance = getBalance(receiver);
			int senderBalance   = getBalance(sender);
			if(sender.equals("bitcoin")){
				if(receiverBalance < 0){
					isValid = false;
					break;
				}
			}
			else if(receiverBalance < 0 || senderBalance < 0){
				isValid = false;
				break;
			}			
		}

		return isValid;
	}


	/**
	 * Get method for the blockChain
	 * @return {ArrayList<Block>} returns the blockChain
	 */
	public ArrayList<Block> getBlocks(){
		return this.blocks;
	}


	/**
	 * Returns the balance of specified user in the blockchain
	 * @param {String} username [Checks balance of this user]
	 * @return {int} balance amount of specified user
	 */
	public int getBalance(String username){
		int amount = 0;
		int blockChainSize = blocks.size();

		if(username == null){
			throw new IllegalArgumentException("Username cannot be null");
		}

		for(int i = 0;  i < blockChainSize; i++){
			if(blocks.get(i).getTransaction().getReceiver().equals(username)){
				amount += blocks.get(i).getTransaction().getAmount();
			}
			if(blocks.get(i).getTransaction().getSender().equals(username)){
				amount -= blocks.get(i).getTransaction().getAmount();
			}
		}
		return amount;
	}


	/**
	 * Generates a nonce which will provide a valid hash code after encrypted through Sha1
	 * @param {String} timestamp [The timestamp value for transaction]
	 * @param {String} transaction [String value of the transaction object]
	 * @param {String} previousHash [Previous hash of the block chain]
	 * @return {String} value of the generated nonce which gives valid hash code
	 */
	public String generateNonce(String timestamp, String transaction, String previousHash){
		String beforeNonce = timestamp + ":" + transaction + ".";
		String combineNonce, hash, firstFiveChars;
		char[] generatedNonce = new char[4];
		int nonceAttempt = 0;

		for(int i = 0; i < 95; i++){

			for(int j = 0; j < 95; j++){

				for(int k = 0; k < 95; k++){

					for(int m = 0; m < 95; m++){
						generatedNonce[0] = (char) (i + 33);
						generatedNonce[1] = (char) (j + 33);
						generatedNonce[2] = (char) (k + 33);
						generatedNonce[3] = (char) (m + 33);

						combineNonce = beforeNonce + String.valueOf(generatedNonce[0]) + String.valueOf(generatedNonce[1]) + String.valueOf(generatedNonce[2]) + String.valueOf(generatedNonce[3]) + previousHash;


						try{
							hash = Sha1.hash(combineNonce);
							firstFiveChars = hash.substring(0, Math.min(hash.length(), 5));
							if(firstFiveChars.equals("00000")){
								System.out.println("Attempts of generating nonce: " + nonceAttempt);
								return String.valueOf(generatedNonce[0]) + String.valueOf(generatedNonce[1]) + String.valueOf(generatedNonce[2]) + String.valueOf(generatedNonce[3]);
							}
							else{
								nonceAttempt++;
							}
						}
						catch(UnsupportedEncodingException e){
							e.printStackTrace();
						}
					}
				}
			}
		}
		return null;
	}
}