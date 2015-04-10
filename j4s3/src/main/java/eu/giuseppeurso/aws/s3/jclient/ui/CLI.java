package eu.giuseppeurso.aws.s3.jclient.ui;

import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import eu.giuseppeurso.aws.s3.jclient.transport.PBECredentialsProvider;
import eu.giuseppeurso.aws.s3.jclient.transport.TransportAgent;

/**
 * This class represents a simply interactive command-line interface.
 * If the virtual machine is started from a command-line interpreter, without redirecting the standard input and output streams then
 * its console will exist and will typically be connected to the keyboard and display from which the virtual machine was launched.
 * 
 * @author Giuseppe Urso - <a href="http://www.giuseppeurso.eu">www.giuseppeurso.eu</a>
 *
 */
public class CLI {
	
	/**
	 * The standard main method to get the input arguments.
	 * @param args
	 * @throws Exception
	 */
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
		System.out.print("Please select an option [1]: ");
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
	
	/**
	 * This method launches the wizard for the AWS credentials encryption. A file with the encrypted credentials is generated. 
	 * @throws IOException
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws NoSuchPaddingException
	 * @throws InvalidAlgorithmParameterException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public static void generateEncryptedFile() throws IOException, InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException{
		System.out.println("");
		System.out.println("In order to store your credentials into a file with password-based encryption, please provide the following properties.");
		System.out.println("");
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		Map<String,String> inputMap = new LinkedHashMap<String, String>();
		
		System.out.println("Enter a name for the file where your credentials will be stored (ex. my-aws.encrypted): ");
		inputMap.put("file", in.readLine());
		
		System.out.println("Your AWS ID: ");
		String awsid="";
		for (awsid = in.readLine(); !Utilities.isValidID(awsid); awsid = in.readLine()) {
			System.out.println("Attention, please provide a valid string for AWS ID (20 alphanumeric chars): ");
		}
		inputMap.put("awsid", awsid);
				
		System.out.println("Your AWS Key: ");
		String keyid="";
		for (keyid = in.readLine(); !Utilities.isValidKey(keyid); keyid = in.readLine()) {
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
	
		byte[] awsAccessId = inputMap.get("awsid").getBytes("UTF8");
		byte[] awsAccessKey = inputMap.get("awskey").getBytes("UTF8");
		
		Utilities.encryptCredentialsWithConstantSalt(inputMap.get("file"), awsAccessId, awsAccessKey, inputMap.get("password"));
	}
	
	private static void getFromS3() {
		// TODO Auto-generated method stub
		
	}


    /**
     * A method to send objects to the S3 bucket.
     * @throws IOException
     */
	private static void sendToS3() throws IOException {
		System.out.println("Make sure you've a password-based encripted file with your AWS credentials (MODE 1 to make it).");
		System.out.println("");
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		Map<String,String> inputMap = new LinkedHashMap<String, String>();
		
		System.out.println("Your credentials file path (ex. ./my-aws.file): ");
		inputMap.put("credentials", in.readLine());
		
		System.out.println("Password: ");
		inputMap.put("password", in.readLine());
		
		System.out.println("AWS Region (ex. EU_WEST_1 ): ");
		inputMap.put("region", in.readLine());
		
		System.out.println("AWS Bucket Name: ");
		inputMap.put("bucket", in.readLine());
		
		System.out.println("Directroy to send: ");
		inputMap.put("sourceDir", in.readLine());
		
		System.out.println("Prefix you want to create in the S3 bucket (ex. 2015.03.22): ");
		inputMap.put("prefix", in.readLine());
		in.close();
		
		//Providing the AWS credentials
		PBECredentialsProvider pbecProvider = new PBECredentialsProvider();
		pbecProvider.setCipherFromFileName(inputMap.get("credentials"));
		pbecProvider.setPassword(inputMap.get("password"));
		
		
		// Uploading to S3
		String region = inputMap.get("region");
		String bucketName=inputMap.get("bucket");
		File file = new File(inputMap.get("sourceDir"));
		String prefix=inputMap.get("prefix");
				
		TransportAgent tagent = new TransportAgent(pbecProvider, region, bucketName);
		tagent.uploadDirRecursively(file, prefix);
		System.out.println("");
		System.out.flush();
		
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
		System.out.println("------------------------------------------");
		System.out.println("J4S3 is a java tool which provides a simple way to store or retrieve data from any Amazon S3 bucket.");
		System.out.println("The J4S3 Client uses IAM user security credentials to send authenticated requests to Amazon S3.");
		System.out.println("For more information about setting up a IAM user in you AWS Account see: ");
		System.out.println("http://docs.aws.amazon.com/IAM/latest/UserGuide/Using_SettingUpUser.html#Using_CreateUser_console");
		System.out.println("");
		System.out.println("After creating a IAM user for a bucket, run MODE 1 to store your AWS access ID and secret key into a file with password-based encryption.");
		System.out.println("");
		System.out.println("");
		System.out.println("\033[1mMODE 1.\033[0m");
		System.out.println("java -jar j4s3.jar");
		System.out.println("Store AWS access ID and secret key to a file with password-based encryption");
		System.out.println("");
		System.out.println("\033[1mMODE 2.\033[0m");
		System.out.println("java -jar j4s3.jar");
		System.out.println("Send files to S3 bucket.");
		System.out.println("Requires:");
		System.out.println(" - credentials file path (ex. ./my-aws.file);");
		System.out.println(" - credentials password;");
		System.out.println(" - S3 region (valid regions are: AP_NORTHEAST_1|AP_SOUTHEAST_1|AP_SOUTHEAST_2|CN_NORTH_1|EU_CENTRAL_1|EU_WEST_1|GovCloud|SA_EAST_1||US_EAST_1|US_WEST_1|US_WEST_2);");
		System.out.println(" - S3 bucket name;");
		System.out.println(" - path of the source directory to send;");
		System.out.println("");
		System.out.println("\033[1mMODE 3.\033[0m");
		System.out.println("java -jar j4s3.jar");
		System.out.println("Retrieve files from S3 bucket");
	}	
}
