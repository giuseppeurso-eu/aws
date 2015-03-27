package eu.giuseppeurso.aws.s3;

import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.text.Normalizer.Form;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class UI {

	
	public static void main(String[] args) throws Exception {
		printBanner();
		selectOption();
	}

	/**
	 * A playful ascii-art banner printer
	 * 
	 */
	public static void printBanner() {
		System.out.println("=================================================");
		System.out.println("     _ _  _  ____ _____ ");
		System.out.println("    | | || |/ ___|___ / ");
		System.out.println(" _  | | || |\\__  \\ |_ \\");
		System.out.println("| |_| |__   _|__) |__) |");
		System.out.println(" \\___/   |_||____/____/ ");
		System.out.println("");
		System.out.println("=================================================");
		System.out.println("\033[1m POWERED by Giuseppe Urso - 2015");
		System.out.println("\033[0m Software licensed under GPL v2");
		System.out.println("");
		System.out.println(" https://github.com/giuseppeurso-eu");
		System.out.println(" http://www.giuseppeurso.eu");
		System.out.println("=================================================");	 
	}
	
	/**
	 * The method to initialize the wizard options.
	 * 
	 * @throws Exception
	 */
	public static void selectOption() throws Exception {

		System.out.println("");
		System.out.println("0) Help");
		System.out.println("1) Encrypt AWS Credentials");
		System.out.println("2) Send to Amazon S3");
		System.out.println("3) Retrieve from Amazon S3");
		System.out.println("");

		BufferedReader input = null;
		System.out.print("Seleziona un'opzione [1]: ");
		String option = "";
		input = new BufferedReader(new InputStreamReader(System.in));

		try {
			option = input.readLine();
		} catch (Exception e) {
			System.out.println("Reading error!");
			System.exit(1);
		}
		
		String[] inputValue = {"",""};
		if (option.equals("") || option.equals("1")) {
			System.out.println("");
			System.out.println("\033[1mMODE 1\033[0m");
			generateEncryptedFile();
		}else if (option.equals("2")) {
			System.out.println("");
			System.out.println("\033[1mMODE 2\033[0m");
			System.out.print("Inserire il file di testo da cui generare la licenza: ");
			inputValue[0]= input.readLine();
			sendToS3();
		}else if (option.equals("3")) {
			System.out.println("");
			System.out.println("\033[1mMODE 3\033[0m");
			System.out.print("Inserire file di licenza da validare: ");
			inputValue[0]=input.readLine();
			System.out.print("Inserire il file di testo del Cliente: ");
			inputValue[1]=input.readLine();
			getFromS3();
		}else {
			printUsage();
		}
	}
	
	


	public static void generateEncryptedFile() throws IOException, InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException{
		System.out.println("");
		System.out.println("In order to store your credentials into a file with password-based encryption, please provide the following properties.");
		System.out.println("");
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		Map<String,String> inputMap = new LinkedHashMap<String, String>();
		
		System.out.println("File name: ");
		inputMap.put("file", in.readLine());
		
		System.out.println("AWS ID: ");
		String awsid="";
		for (awsid = in.readLine(); !Utilities.isValid(awsid); awsid = in.readLine()) {
			System.out.println("Attention, please provide a valid string for AWS ID (20 alphanumeric chars): ");
		}
		inputMap.put("awsid", awsid);
				
		System.out.println("AWS Key: ");
		String keyid="";
		for (keyid = in.readLine(); !Utilities.isValid(keyid); keyid = in.readLine()) {
			System.out.println("Attention, please provide a valid string for AWS KEY (40 alphanumeric chars): ");
		}
		inputMap.put("awskey", keyid);
		
		
		String[] passwordValues = passwordForm(in);
		while (!Utilities.isValidPassword(passwordValues[0], passwordValues[1])) {
			System.out.println("Attention, the two passwords you typed do not match !");
			passwordValues = passwordForm(in);
		}
		inputMap.put("password", passwordValues[0]);
	    
		
		for (Entry<String, String> entry : inputMap.entrySet()) {
			System.out.println("K: "+entry.getKey());
			System.out.println("V: "+entry.getValue());
		}
		
//		int inputNumbers = 4;
//		String[] inputs = new String[inputNumbers];
//		for (int i = 0; i < inputNumbers; i++){
//		    inputs[i] = in.readLine();
//		    form.put("file", in.readLine());
//			System.out.println(inputs[i]);
//		}
//		 while ((s = in.readLine()) != null && s.length() != 0)
//		      System.out.println(s);
		
		
		byte[] awsAccessId = inputMap.get("awsid").getBytes("UTF8");
		byte[] awsAccessKey = inputMap.get("awskey").getBytes("UTF8");
		
		Utilities.encryptCredentialsWithConstantSalt(inputMap.get("file"), awsAccessId, awsAccessKey, inputMap.get("password"));
	}
	
	private static void getFromS3() {
		// TODO Auto-generated method stub
		
	}



	private static void sendToS3() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * A method to create a basic form for password input. The form consists of two field: the password input and the confirmation input.
	 * In order to hide the provided strings, a Console object is used. It is a good idea to check whether this object exists,
	 * since it does not exist for non-interactive Java programs, which includes program in which stdin is redirected. For example the default Eclipse IDE stdin or
	 * an invocation like "java foo < inputfile".
	 * @param in
	 * @return
	 * @throws IOException
	 */
	private static String[] passwordForm(BufferedReader in) throws IOException{
		// Password masking
		Console console = System.console();
		char[] passvalue;
		char[] confirmation;
		if (console != null) {
			System.out.println("Password : ");
			passvalue = System.console().readPassword();
			System.out.println("Confirm password : ");
			confirmation = System.console().readPassword();
			
		} else {
			EraserSystemInput esi = new EraserSystemInput();
			esi.start();
			System.out.println("Password : ");
			passvalue = in.readLine().toCharArray();
			System.out.println("Confirm password : ");
			confirmation = in.readLine().toCharArray();
			esi.halt();
		}
		String[] password = new String[]{String.valueOf(passvalue),String.valueOf(confirmation)};
		return password;				
	}
	
	
	
	/**
	 * This method shows a minimal user-help on the standard output
	 * 
	 */
	public static void printUsage() {
		System.out.println("");
		System.out.println("\033[1mHELP\033[0m");
		System.out.println("J4S3 is a java tool which provides a simple way to store and retrieve data from any Amazon S3 bucket.");
		System.out.println("In order to securely sign requests to AWS services, both AWS access key ID and secret access key must be provided.");
		System.out.println("Use MODE 1 to store your credentials into a file with password-based encryption.");
		System.out.println("");
		System.out.println("");
		System.out.println("\033[MODE 1.\033[0m");
		System.out.println("java -jar j4s3.jar");
		System.out.println("It stores AWS access key ID and secret access key to a file with password-based encryption");
		System.out.println("");
		System.out.println("\033[1mMODE 2.\033[0m");
		System.out.println("java -jar j4s3.jar  --put [your-bucket-id]");
		System.out.println("Send files to S3 bucket");
		System.out.println("");
		System.out.println("\033[1mMODE' 3.\033[0m");
		System.out.println("java -jar j4s3.jar  --get [your-bucket-id]");
		System.out.println("Retrieve files from S3 bucket");
	}
	
	
	
	
}
