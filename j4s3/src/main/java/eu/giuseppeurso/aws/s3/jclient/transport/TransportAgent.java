package eu.giuseppeurso.aws.s3.jclient.transport;

import java.io.File;
import java.text.DecimalFormat;
import java.util.UUID;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.MultipleFileUpload;
import com.amazonaws.services.s3.transfer.TransferManager;

/**
 * This class represents  sample demonstrates how to make basic requests to Amazon S3 using
 * the AWS SDK for Java <a href="http://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/index.html">AWS SDK Java API Reference</a><br>
 * The low-level AWS APIs correspond to the Amazon S3 REST operations.
 * Data are securely transferred  from/to Amazon S3 via SSL endpoints by using the HTTPS protocol. 
 * 
 * <p>
 * <b>Prerequisites:</b> You must have a valid Amazon Web Services 
 * account, and be signed up to use Amazon S3. For more information on
 * Amazon S3, see http://aws.amazon.com/s3.
 * <p>
 * 
 * @author Giuseppe Urso - <a href="http://www.giuseppeurso.eu">www.giuseppeurso.eu</a>
 * @see <a href="http://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/s3/transfer/TransferManager.html">S3 TransferManager</a>
 * @see <a href="http://docs.aws.amazon.com/AmazonS3/latest/dev/UsingEncryption.html">S3 Data protection</a>
 * @see <a href="http://docs.aws.amazon.com/AmazonS3/latest/dev/UploadingObjects.html">S3 Uploading Objects</a>
 */
public class TransportAgent {

	private AmazonS3 s3Client;
	private String bucketName;
	
	/**
	 * A quick way for accessing the Amazon S3 web service using the PBECredentialsProvider in the constructor.
	 * You can create the AmazonS3Client class without providing your security credential but requests sent using this client are anonymous requests
	 * without a signature. Amazon S3 returns an error if you send anonymous requests for a resource that is not publicly available.
	 *  
	 * @param pbecProvider
	 * @param region - Valid Amazon Regions are:</br>AP_NORTHEAST_1 </br>AP_SOUTHEAST_1</br> AP_SOUTHEAST_2</br>
	 *  CN_NORTH_1</br> EU_CENTRAL_1</br>EU_WEST_1</br>GovCloud</br>SA_EAST_1</br>US_EAST_1</br>US_WEST_1</br>US_WEST_2 
	 * @param bucket the target S3 bucket name 
	 */
	public TransportAgent (PBECredentialsProvider pbecProvider, String region, String bucket) {
		this.setS3Client(new AmazonS3Client(pbecProvider.getCredentials()));
		Region r = Region.getRegion(Regions.valueOf(region));
		s3Client.setRegion(r);	
		this.setBucketName(bucket);
		if (!isBucketAccessible()) {
			throw new AmazonS3Exception("The provided AWS bucket is not accessible, please check it!"+" - Region ID is: "+region+ " - Bucket name is: "+bucketName);
			
		}		
	}

	/**
	 * @return the s3Client
	 */
	private AmazonS3 getS3Client() {
		return s3Client;
	}

	/**
	 * @param s3Client the s3Client to set
	 */
	private void setS3Client(AmazonS3 s3Client) {
		this.s3Client = s3Client;
	}

	/**
	 * @return the bucketName
	 */
	private String getBucketName() {
		return bucketName;
	}


	/**
	 * @param bucketName the bucketName to set
	 */
	private void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	/**
	 * A simple method to put a new object to a S3 bucket. To prevent name collisions, a random UUID is used.
	 * @param file
	 */
	public void uploadNewFileWithRandomKey(File file) {
		
		String key = "ID_" + UUID.randomUUID();
		System.out.println("Uploading a new object to S3 from a file...");
		try {
			s3Client.putObject(new PutObjectRequest(bucketName, key, file));
		} catch (AmazonServiceException e) {
			System.out.println("Service error while uploading a new object to S3.");
			System.out.println(e);
			e.printStackTrace();
		} catch (AmazonClientException c) {
			System.out.println("Client error while uploading a new object to S3.");
			System.out.println(c);
		}
		System.out.println("Creation of the new object completed!");
	}

	/**
	 * A method to upload a directory recursively to a S3 bucket. The provided prefix name is used as root element.
	 * 
	 * @param directory - the source dir
	 * @param prefix - the root destination dir
	 */
	public void uploadDirRecursively (File directory, String prefix){
		TransferManager tx = new TransferManager(s3Client);
		try{
			System.out.println("Uploading directory recursively to S3...");
			MultipleFileUpload mfu = tx.uploadDirectory(bucketName, prefix+"/"+directory.getName(), directory, true);
			//mfu.waitForCompletion();
			uploadProgressBar(mfu);
			} catch (AmazonServiceException e) {
			System.out.println("AWS Service error while uploading directories recursively to S3.");
			System.out.println(e);			
		}catch (AmazonClientException c) {
			System.out.println("AWS Client error while uploading directories recursively to S3.");
			System.out.println(c);			
		}
		//tx.shutdownNow();
		System.out.println("Directory upload completed!");		
	}
	
	/**
	 * A progress bar for the upload to the standard output.
	 * The escape sequence \b doesn't work in Eclipse because of an old bug not fixed yet.
	 * @param multipleFileUpload
	 * @see <a href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=76936"> Eclipse Bug 76936 </a>
	 */
	private void uploadProgressBar(MultipleFileUpload multipleFileUpload){
		
		System.out.print("Progress:[=");
		try {
		while (multipleFileUpload.isDone() == false) {
			//System.out.print("\r Progress: "+ (long) mfu.getProgress().getPercentTransferred()+" %");
			long perc = (long) multipleFileUpload.getProgress().getPercentTransferred();
			//System.out.print("\b");
			System.out.print("= ");
			System.out.print(perc+"%");
			if (perc<10) {
				System.out.print("\b\b\b");	
			}else {
				System.out.print("\b\b\b\b");
			}
				Thread.currentThread();
				Thread.sleep(2000);
			}
		}catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		System.out.print("\b] Done "+(long) multipleFileUpload.getProgress().getPercentTransferred()+" %\n");
		long total = multipleFileUpload.getProgress().getBytesTransferred();
		String roundOff = new DecimalFormat("#.##").format((double)total/1000000);
		System.out.println("Total transferred: "+total +" bytes (~ "+roundOff+ " MB)");	
		}
		
	
		/**
		 * The AWS Credentials checker. This method initially tried to list objects included in a S3 bucket using the AWSCredentials provider. This
		 * caused performance issues with the SSL handshake for buckets with a large number of objects.
		 * Now the method try to get a single fake object in the bucket using a random key ID. This will throw an exception because you're trying to fetch a key that doesn't exist.
		 * If AmazonServiceException shows an error code equals "NoSuchKey"  then you have read access to the bucket.
		 * 
		 * @see <a href="http://docs.aws.amazon.com/AmazonS3/latest/dev/AuthUsingAcctOrUserCredJava.html"> Making requests using AWS credentials</a>
		 * @param region
		 * @param bucketName
		 */
		public boolean isBucketAccessible(){
			boolean valid = false;
			try {
			 s3Client.getObject(bucketName, UUID.randomUUID().toString());
			} catch (AmazonServiceException ase) {
	            if	(ase.getErrorCode().equals("NoSuchKey")){
	            	valid = true;
	            }else {
	            	System.out.println("AmazonServiceException,"+"your request to Amazon S3 was rejected with an error response.");
		            System.out.println("Error Message:    " + ase.getMessage());
		            System.out.println("Error Type:       " + ase.getErrorType());
				}
	        } catch (AmazonClientException ace) {
	            System.out.println("AmazonClientException,"+ "the client encountered an internal error while trying to communicate with S3, such as not being able to access the network.");
	            System.out.println("Error Message: " + ace.getMessage());
	        }
			return valid;
			
		}
}
