package eu.giuseppeurso.aws.s3;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.io.FileUtils;

import eu.giuseppeurso.security.jca.crypto.PasswordBasedEncryption;

/**
 * This class includes a series of methods for general use.
 * @author Giuseppe Urso - <a href="http://www.giuseppeurso.eu">www.giuseppeurso.eu</a>
 *
 */
public class Utilities {
	
	private static final String awsAccessIdProperty="aws.access.id";
	private static final String awsAccessKeyProperty="aws.access.key";

	
	public Utilities() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * A method to store credentials in a file with password-based encryption.
	 * @param outputFile
	 * @param awsAccessId
	 * @param awsAccessKey
	 * @param password
	 * @throws IOException
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws NoSuchPaddingException
	 * @throws InvalidAlgorithmParameterException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	
	//TODO Rifattorizzazione con password in formato char[]
	public static void encryptCredentialsWithConstantSalt(String outputFile, byte[] awsAccessId, byte[] awsAccessKey, String password) throws IOException, InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException{
		
		byte[] id = (awsAccessIdProperty+"=").getBytes("UTF8");
		byte[] key = (awsAccessKeyProperty+"=").getBytes("UTF8");
		int total = id.length+awsAccessId.length+key.length+awsAccessKey.length;
		byte[] plainText= new byte[total];
		
		System.arraycopy(id,0,plainText,0,id.length);
		//String s = new String(plainText, "UTF-8");
		//System.out.println("1: "+s);
		System.arraycopy(awsAccessId,0,plainText,id.length,awsAccessId.length);
		//s = new String(plainText, "UTF-8");
		//System.out.println("2: "+s);
		System.arraycopy(key,0,plainText,id.length+awsAccessId.length,key.length);
		//s = new String(plainText, "UTF-8");
		//System.out.println("3: "+s);
		System.arraycopy(awsAccessKey,0,plainText,id.length+awsAccessId.length+key.length,awsAccessKey.length);
		//s = new String(plainText, "UTF-8");
		//System.out.println("4: "+s);
		
		byte[] encrypted = PasswordBasedEncryption.encrypt(plainText, password);
		File f = new File(outputFile);
		FileUtils.writeByteArrayToFile(f, encrypted);
	}
	
	/**
	 * A simple validation to check the number of chars for the AWS ID string
	 * @param input
	 * @return
	 */
	public static boolean isValidID(String input){
		boolean actual=false;
		if (input!=null && input!=""&& input.length()==20) {
			actual=true;
		}
		return actual;
	}
	
	/**
	 * A simple validation to check the number of chars for the AWS Key string
	 * @param input
	 * @return
	 */
	public static boolean isValidKey(String input){
		boolean actual=false;
		if (input!=null && input!=""&& input.length()==40) {
			actual=true;
		}
		return actual;
	}

	/**
	 * A method to check the password
	 * @param value
	 * @param confirmation
	 * @return
	 */
	public static boolean isValidPassword(String value, String confirmation){
		boolean actual=false;
		if ( (value!=null && confirmation!=null) && (value.equals(confirmation)) ) {
			actual=true;
		}
		return actual;
	}

}
