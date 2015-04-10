package eu.giuseppeurso.aws.s3.jclient.transport;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.ResourceBundle;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.amazonaws.auth.AWSCredentials;

import eu.giuseppeurso.aws.s3.jclient.transport.PBECredentialsProvider;
import eu.giuseppeurso.security.jca.crypto.PasswordBasedEncryption;


/**
 * Unit test for PBECredentialsProvider (JUNIT 4).
 * 
 * @author Giuseppe Urso - <a href="http://www.giuseppeurso.eu">www.giuseppeurso.eu</a> 
 * 
 */
public class PBECredentialsProviderTest {

	
	private static String resourceDir = "";
	private static String targetDir = "";
	private static String clearCredentials = "";
	private static String encryptedCredentials = "";
	private static String password = "";
	
	private static String sourceID= "";
	private static String sourceKey = "";
	
	private static PBECredentialsProvider pbecProvider;
	
	/**
	 * The setup method to prepare all test cases. @BeforeClass annotation causes it to be run once before any of the test methods in the class.
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
		
		// The ID and Key initial values from the plaintext source file
		//
		ResourceBundle settings = ResourceBundle.getBundle("example-credentials");
		sourceID = settings.getString("aws.access.id");
		sourceKey = settings.getString("aws.access.key");
		
		// The encryption of the source plain text
		//
		File sourceFile = new File(clearCredentials);
		byte[] originalText = FileUtils.readFileToByteArray(sourceFile);
		byte[] encryption = PasswordBasedEncryption.encrypt(originalText, password);
		
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

	
	/** Test case for decryptCredentials
	* 
	* @throws Exception
	*/
	@Test
	public void testDecryptCredentials() throws Exception {

		boolean actual = false;
		
		byte[] decryptedText = PBECredentialsProvider.decryptCredentials();	
		//String decryptedTextValue = new String(decryptedText, "UTF-8");
		//System.out.println("Decrypted text is: "+decryptedTextValue);
		
		File file = new File(clearCredentials);
		byte[] originalText = FileUtils.readFileToByteArray(file);
		//String originalTextValue = new String(originalText, "UTF-8");
		//System.out.println("Original text was: "+originalTextValue);
		if (Arrays.equals(decryptedText, originalText)) {
    		actual = true;
		}
    	Assert.assertEquals("Invalid decryption.", true, actual);
	}
	
	/**
	 * Test case for retrieveAWSKey
	 * @throws Exception
	 */
	@Test
	public void testRetrieveAWSKey() throws Exception {
		boolean actual = false;
		
		String keyToRetrieve = "aws.access.id";
		String id = PBECredentialsProvider.retrieveAWSKey(keyToRetrieve);
//		System.out.println("value of: "+keyToRetrieve+" is: "+id);
		keyToRetrieve = "aws.access.key";
		String key = PBECredentialsProvider.retrieveAWSKey(keyToRetrieve);
//		System.out.println("value of: "+keyToRetrieve+" is: "+key);
		if (id!=null && id.equals(sourceID) && (key!=null && key.equals(sourceKey)) ) {
    		actual = true;
		}
		Assert.assertEquals("Failed to retrieve the correct key values.", true, actual);
	}
	
	/**
	 * Test case for getCredentials
	 * @throws Exception
	 */
	@Test
	public void testGetCredentials() throws Exception {
		
		boolean actual = false;
		AWSCredentials awsc = pbecProvider.getCredentials();
		String id = awsc.getAWSAccessKeyId();
		String key = awsc.getAWSSecretKey();
		//Remove the attached char \n at the end of string
		//key= key.substring(0,key.length()-1);
		if (id!=null && id.equals(sourceID) && (key!=null && key.equals(sourceKey)) ) {
    		actual = true;
		}
		Assert.assertEquals("Invalid AWSCredentials. ID: "+id+" KEY: "+key, true, actual);
	}
	
	@Test
	public void testSetCipherFromFileName() throws IOException{
		boolean actual = false;
		PBECredentialsProvider	pbecp = new PBECredentialsProvider();
		pbecp.setCipherFromFileName(encryptedCredentials);
		pbecp.setPassword(password);
		AWSCredentials awsc = pbecp.getCredentials();
		String id = awsc.getAWSAccessKeyId();
		String key = awsc.getAWSSecretKey();
		//Remove the attached char \n at the end of string
		//key= key.substring(0,key.length()-1);
		if (id!=null && id.equals(sourceID) && (key!=null && key.equals(sourceKey)) ) {
    		actual = true;
		}
		Assert.assertEquals("Invalid AWSCredentials. ID: "+id+" KEY: "+key, true, actual);
		
	}
}
