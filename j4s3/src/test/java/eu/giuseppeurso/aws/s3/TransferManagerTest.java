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
import org.junit.BeforeClass;
import org.junit.Test;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

import eu.giuseppeurso.security.jca.crypto.PasswordBasedEncryption;

public class TransferManagerTest {

	private static String resourceDir = "";
	private static String targetDir = "";
	private static String clearCredentials = "";
	private static String encryptedCredentials = "";
	private static String password = "";
	private static String uploadTestFile = "";
	
	static PBECredentialsProvider pbecProvider;
	static TransferManager transferManager;
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
		encryptedCredentials = resourceDir + "/guaws.encrypted";
		password = "Giuseppe1974";
		uploadTestFile=resourceDir+"/test1.txt";
		
		// The encryption of the source plain text
		//
//		File sourceFile = new File(clearCredentials);
//		byte[] originalText = FileUtils.readFileToByteArray(sourceFile);
//		byte[] encryption = PasswordBasedEncryption.encrypt(originalText,password);

		// The encryption is written to a file
		//
//		File destFile = new File(encryptedCredentials);
//		FileUtils.writeByteArrayToFile(destFile, encryption);
		File encryptedFile = new File(encryptedCredentials);
		
		// Finally an instance of PBECredentialsProvider is created
		//
		pbecProvider = new PBECredentialsProvider();
		byte[] cipherText = FileUtils.readFileToByteArray(encryptedFile);
		pbecProvider.setPassword(password);
		pbecProvider.setCipher(cipherText);
		transferManager = new TransferManager(pbecProvider);	

	}
	
	
	
	@Test
	public void testUploadNewFile() {

		System.out.println("passwd: "+pbecProvider.getPassword());
	    System.out.println("ID: "+pbecProvider.getCredentials().getAWSAccessKeyId());
	    System.out.println("KEy: "+pbecProvider.getCredentials().getAWSSecretKey());
		
		Region region = Region.getRegion(Regions.EU_WEST_1);
		String bucketName="gubucket-01";
		File file = new File(uploadTestFile);
		transferManager.uploadNewFile(region, bucketName, file);
	}

}
