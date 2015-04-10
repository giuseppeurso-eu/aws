package eu.giuseppeurso.aws.s3.jclient.transport;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.SimpleDateFormat;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import eu.giuseppeurso.aws.s3.jclient.transport.PBECredentialsProvider;
import eu.giuseppeurso.aws.s3.jclient.transport.TransportAgent;


public class TransportAgentTest {

	private static String resourceDir = "";
	private static String targetDir = "";
	private static String clearCredentials = "";
	private static String encryptedCredentials = "";
	private static String password = "";
	private static SimpleDateFormat sdf;
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
		sdf = new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss");
		resourceDir = "src/test/resources";
		targetDir = "target";
		clearCredentials = resourceDir + "/example-credentials.properties";
		encryptedCredentials = resourceDir + "/guaws.encrypted";
		password = "12345";
		uploadTestFile=resourceDir+"/test1.txt";
		uploadTestFile2=resourceDir+"/dir-test1";
				
		// A instance of PBECredentialsProvider is created
		//
		pbecProvider = new PBECredentialsProvider();
		pbecProvider.setPassword(password);
		pbecProvider.setCipherFromFileName(encryptedCredentials);
		
		// Finally the TransportAgent to transfer objects to S3
		//
		String region= "EU_WEST_1";
		String bucketName="gubucket-01";
		tagent = new TransportAgent(pbecProvider, region, bucketName);	
	}
	
	
	
	@Test
	public void testUploadNewFileWithRandomKey() {

//		System.out.println("passwd: "+pbecProvider.getPassword());
//	    System.out.println("ID: "+pbecProvider.getCredentials().getAWSAccessKeyId());
//	    System.out.println("KEy: "+pbecProvider.getCredentials().getAWSSecretKey());
		
		File file = new File(uploadTestFile);
		tagent.uploadNewFileWithRandomKey(file);
	}

	@Test
	public void testUploadDirRecursively(){
		boolean actual = false;
		
		File directory = new File(uploadTestFile2);
		String now = sdf.format(new java.util.Date());
	    
		try {
			tagent.uploadDirRecursively(directory, now);
			actual=true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Assert.assertEquals("Error on TEST CASE: "+this.getClass(), true, actual);		
	}
	
	@Test
	public void testIsValidAWSAccess(){
		boolean actual = false;
		if(tagent.isBucketAccessible()){
			actual=true;
		}
		Assert.assertEquals("Error on TEST CASE: "+this.getClass(), true, actual);
	}
	
}
