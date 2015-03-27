package eu.giuseppeurso.aws.s3;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Timestamp;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Assert;
import org.junit.Test;

import eu.giuseppeurso.security.jca.crypto.PasswordBasedEncryption;

/**
 * The test cases for Utilities class (JUNIT 4).
 * @author Giuseppe Urso - <a href="http://www.giuseppeurso.eu">www.giuseppeurso.eu</a>
 *
 */
public class UtilitiesTest {
	
	private static String resourceDir = "";
	private static String targetDir = "";
	private static String clearCredentials = "";
	private static String encryptedCredentials = "";
	private static String password = "";

	static PBECredentialsProvider pbecProvider;
	
	/**
	 * The method to setup test cases. @BeforeClass annotation causes it to be run once before any of the test methods in the class.
	 * @throws IOException 
	 * @throws BadPaddingException 
	 * @throws IllegalBlockSizeException 
	 * @throws InvalidAlgorithmParameterException 
	 * @throws NoSuchPaddingException 
	 * @throws InvalidKeySpecException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 */
	@BeforeClass
	public static void oneTimeSetUp() throws IOException, InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

		// Resources setup
		//
		resourceDir = "src/test/resources";
		targetDir = "target";
		clearCredentials = resourceDir + "/example-credentials.properties";
		encryptedCredentials = targetDir + "/example-awskeys.bin";
		password = "12345";

		// The encryption of the source plain text
		//
		File sourceFile = new File(clearCredentials);
		byte[] originalText = FileUtils.readFileToByteArray(sourceFile);
		byte[] encryption = PasswordBasedEncryption.encrypt(originalText,password);

		// The encryption is written to a file
		//
		File destFile = new File(encryptedCredentials);
		FileUtils.writeByteArrayToFile(destFile, encryption);

		// Finally an instance of PBECredentialsProvider is created
		//
		pbecProvider = new PBECredentialsProvider();
		byte[] cipherText = FileUtils.readFileToByteArray(destFile);
		pbecProvider.setPassword(password);
		pbecProvider.setCipher(cipherText);

	}
	
	/**
	 * Test case for encryptCredentialsWithConstantSalt().
	 * @throws IOException
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws NoSuchPaddingException
	 * @throws InvalidAlgorithmParameterException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	@Test
	public void testEncryptCredentialsWithConstantSalt() throws IOException, InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		boolean actual = false;
		String id = "A test ID";
		String key = "A test KEY";
		byte[] bId = id.getBytes("UTF8");
		byte[] bKey = key.getBytes("UTF8");
		
		java.util.Date date= new java.util.Date();
		Timestamp now = new Timestamp(date.getTime());
		encryptedCredentials = targetDir +"/"+now+ "-aws-keys.bin";
		Utilities.encryptCredentialsWithConstantSalt(encryptedCredentials, bId, bKey,password);
		
		File awskeys = new File(encryptedCredentials); 
		if (awskeys.exists() && !awskeys.isDirectory()) {
			actual=true;
		}
		Assert.assertEquals("Failed to check the file with encrypted credentials.", true, actual);	
	}
	
	/**
	 * A test case for isValid
	 */
	@Test
	public void testIsValid(){
		boolean actual = false;

		String input = null;
		if (!Utilities.isValid(input)) {
			actual=true;
		}
		Assert.assertEquals("Failed to check ID (case null).", true, actual);
		actual = false;

		input="123";
		if (!Utilities.isValid(input)) {
			actual=true;
		}
		Assert.assertEquals("Failed to check ID (case number chars).", true, actual);
		actual = false;
		
		input="";
		if (!Utilities.isValid(input)) {
			actual=true;
		}
		Assert.assertEquals("Failed to check ID (case blank string).", true, actual);
		actual = false;
		
		input = "xxxxxxxxxxxxxxxxxxxx";
		if (Utilities.isValid(input)) {
			actual=true;
		}
		Assert.assertEquals("Failed to check ID (case ID ok).", true, actual);
		actual = false;
		
		input = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
		if (Utilities.isValid(input)) {
			actual=true;
		}
		Assert.assertEquals("Failed to check ID (case Key ok).", true, actual);
	}

	@Test
	public void testIsValidPassword(){
		boolean actual = false;
		
		if (!Utilities.isValidPassword(null, null)) {
			actual=true;
		}
		Assert.assertEquals("Failed to validate Password (case both null).", true, actual);
		actual = false;
		
		if (!Utilities.isValidPassword("123", null)) {
			actual=true;
		}
		Assert.assertEquals("Failed to validate Password (case one null).", true, actual);
		actual = false;
		
		if (!Utilities.isValidPassword("12345", "123456")) {
			actual=true;
		}
		Assert.assertEquals("Failed to validate Password (case not match).", true, actual);
		actual = false;
		
		if (Utilities.isValidPassword("abcd", "abcd")) {
			actual=true;
		}
		Assert.assertEquals("Failed to validate Password (case match).", true, actual);
		actual = false;
	}
}