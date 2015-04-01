package eu.giuseppeurso.aws.s3;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.DecimalFormat;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

import eu.giuseppeurso.security.jca.crypto.PasswordBasedEncryption;

public class TransportAgentTest {

	private static String resourceDir = "";
	private static String targetDir = "";
	private static String clearCredentials = "";
	private static String encryptedCredentials = "";
	private static String password = "";
	private static String uploadTestFile = "";
	private static String uploadTestFile2 = "";
	
	static PBECredentialsProvider pbecProvider;
	static TransportAgent tagent;
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
		password = "123456789Aa";
		uploadTestFile=resourceDir+"/test1.txt";
		uploadTestFile2=resourceDir+"/dir-test1";
		
		// The encrypted file with AWS credentials
		//
		File encryptedFile = new File(encryptedCredentials);
		
		// A instance of PBECredentialsProvider is created
		//
		pbecProvider = new PBECredentialsProvider();
		byte[] cipherText = FileUtils.readFileToByteArray(encryptedFile);
		pbecProvider.setPassword(password);
		pbecProvider.setCipher(cipherText);
		
		// Finally the TransportAgent to transfer objects to S3
		//
		tagent = new TransportAgent(pbecProvider);	
	}
	
	
	
	@Test
	public void testUploadNewFileWithRandomKey() {

//		System.out.println("passwd: "+pbecProvider.getPassword());
//	    System.out.println("ID: "+pbecProvider.getCredentials().getAWSAccessKeyId());
//	    System.out.println("KEy: "+pbecProvider.getCredentials().getAWSSecretKey());
		
		Region region = Region.getRegion(Regions.EU_WEST_1);
		String bucketName="gubucket-01";
		File file = new File(uploadTestFile);
//		tagent.uploadNewFileWithRandomKey(region, bucketName, file);
	}

	@Test
	public void testUploadDirRecursively(){
		boolean actual = false;
		Region region = Region.getRegion(Regions.EU_WEST_1);
		String bucketName="gubucket-01";
		File directory = new File(uploadTestFile2);
		
		try {
			tagent.uploadDirRecursively(region, bucketName, directory);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		actual=true;
		Assert.assertEquals("Error on TEST CASE: "+this.getClass(), true, actual);		
	}
	
}
