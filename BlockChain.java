import java.io.*;
import java.util.Scanner;
import java.util.HashMap;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.io.IOException;
import java.io.UnsupportedEncodingException;


public class BlockChain {

	private ArrayList<Block> blocks;

	public static void main(String[] args){
		String senderInput;
		String receiverInput;
		int amountToSend;
		//1. Read blockchain from file
		BlockChain blockChain = fromFile("C:\\Users\\Graydon\\Documents\\School\\University of Ottawa\\Second Year\\Data Structures and Algorithms - CSI 2110\\CSI_Asignment2\\bitCoinBank.txt");		
		/*
		int size = blockChain.blocks.size();
		for(int i = 0; i < size; i++){
			System.out.println("Previous Hash:  " + blockChain.blocks.get(i).getPreviousHash() + "\n" + "   Current Hash:  " + blockChain.blocks.get(i).getHash());
		}
		
		*/
		
		//ToFile Test
		blockChain.toFile("C:\\Users\\Graydon\\Documents\\School\\University of Ottawa\\Second Year\\Data Structures and Algorithms - CSI 2110\\CSI_Asignment2\\test.txt");

		//2. Validate blockchain
		boolean blockChainValidated = blockChain.validateBlockChain();
		System.out.println("Block chain is validated?: " + blockChainValidated);

		//3. Prompt user for new transaction and verify it
		if(blockChainValidated){
			System.out.println("Your current BlockChain is Valid!" + "\n" + "Please create a new Transaction." + "\n");
			senderInput = blockChain.promptUserForSenderInput();
			receiverInput = blockChain.promptUserForReceiverInput();
			amountToSend = blockChain.promptUserForAmount();
		}

		//4. Adding transaction to blockchain
		//5. asking if user wants to add more transactions (if yes go back to 3)
		//6. Save blockchain to a file with specific filename
	}

	public BlockChain(Block block){
		if(block != null){
			blocks = new ArrayList();			
			blocks.add(0, block);
		}

		else{
			throw new IllegalArgumentException("Block cannot be null");
		}
	}

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


	public static BlockChain fromFile(String filename){
		//Read file and add blocks into ArrayList
		//filename is path location of file
		HashMap<Integer, String> fileValuesMap = new HashMap();
		int blockNumber = 0;		
		int lineNumber = 0;
		BlockChain blockChain;
		Scanner scanner;
		File file;

		if(filename == null){
			throw new IllegalArgumentException("File path cannot be null");
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
						blockChain.addBlock(block);
						blockNumber++;				
						
						//Reset counter and clear all mappings from hash map.
						lineNumber = 0;
						fileValuesMap.clear();
					}
				}
				return blockChain;
			}

			catch(FileNotFoundException e){
				e.printStackTrace();
				return null;
			}
		}
		catch(NullPointerException e){
			e.printStackTrace();
		}
		return null;
	}


	public static String getPreviousHash(BlockChain blockChain, int blockNumber){
		return blockChain.blocks.get(blockNumber - 1).getHash();
	}

	public void addBlock(Block block){
		blocks.add(block.getIndex(), block);
	}


	public void toFile(String filename){
		ArrayList<Block> blockList = this.getBlocks();
		int size = blockList.size();
		File file;
		PrintWriter printWriter;

		try{
			file = new File(filename);
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


	public boolean validateBlockChain(){
		//Compare hash and the specific block.toString hash given with the nonce concatenated (for all blocks in blockChain)
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


	public ArrayList<Block> getBlocks(){
		return this.blocks;
	}


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

	public String toString(BlockChain blockChain){
		int size = blockChain.blocks.size();


		for(int i = 0; i < size; i++){

			
		}
		return null;
	}
	
}