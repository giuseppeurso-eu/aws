package eu.giuseppeurso.aws.s3;

import java.io.File;
import java.text.DecimalFormat;
import java.util.UUID;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.MultipleFileUpload;
import com.amazonaws.services.s3.transfer.TransferManager;

/**
 * This sample demonstrates how to make basic requests to Amazon S3 using
 * the AWS SDK for Java <a href="http://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/index.html">AWS SDK Java API Reference</a>
 * <p>
 * <b>Prerequisites:</b> You must have a valid Amazon Web Services 
 * account, and be signed up to use Amazon S3. For more information on
 * Amazon S3, see http://aws.amazon.com/s3.
 * <p>
 * 
 * @author Giuseppe Urso - <a href="http://www.giuseppeurso.eu">www.giuseppeurso.eu</a>
 * @see <a href="http://docs.aws.amazon.com/AmazonS3/latest/dev/UsingEncryption.html">S3 Data protection</a>
 * @see <a href="http://docs.aws.amazon.com/AmazonS3/latest/dev/UploadingObjects.html">S3 Uploading Objects</a>
 */
public class TransportAgent {

	private static AmazonS3 s3Client;
	private static TransferManager tx;
	
	/**
	 * A quick way for accessing the Amazon S3 web service using the PBECredentialsProvider in the constructor.
	 * @param pbecProvider
	 */
	public TransportAgent(PBECredentialsProvider pbecProvider) {
		s3Client = new AmazonS3Client(pbecProvider.getCredentials());
		tx = new TransferManager(s3Client);
	}
		
	/**
	 * A simple method to put a new object to a S3 bucket. To prevent name collisions, a random UUID is used.
	 * @param region
	 * @param bucketName
	 * @param file
	 */
	public void uploadNewFileWithRandomKey(String region, String bucketName, File file) {
		Region r = Region.getRegion(Regions.valueOf(region));
		s3Client.setRegion(r);
		String key = "ID_" + UUID.randomUUID();
		System.out.println("Uploading a new object to S3 from a file...");
		try {
			s3Client.putObject(new PutObjectRequest(bucketName, key, file));
		} catch (AmazonServiceException e) {
			System.out.println("Service error while uploading a new object to S3.");
			System.out.println(e);
			e.printStackTrace();
		} catch (AmazonClientException e) {
			System.out.println("Client error while uploading a new object to S3.");
			System.out.println(e);
		}
		System.out.println("Creation of the new object completed!");
	}

	/**
	 * A method to upload a directory recursively to a S3 bucket. The provided prefix name is used as root element.
	 * 
	 * @param region - Valid Amazon Regions are:</br>AP_NORTHEAST_1 </br>AP_SOUTHEAST_1</br> AP_SOUTHEAST_2</br>
	 *  CN_NORTH_1</br> EU_CENTRAL_1</br>EU_WEST_1</br>GovCloud</br>SA_EAST_1</br>US_EAST_1</br>US_WEST_1</br>US_WEST_2 
	 * @param bucketName
	 * @param directory - the source dir
	 * @param prefix - the root destination dir
	 */
	public void uploadDirRecursively(String region, String bucketName, File directory, String prefix){
		Region r = Region.getRegion(Regions.valueOf(region));
		s3Client.setRegion(r);
		try {
			System.out.println("Uploading directory recursively to S3...");
			MultipleFileUpload mfu = tx.uploadDirectory(bucketName, prefix+"/"+directory.getName(), directory, true);
			//mfu.waitForCompletion();
			uploadProgressBar(mfu);
			} catch (Exception e) {
			System.out.println("Error while uploading directories recursively to S3.");
			System.out.println(e);			
		}		
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
				Thread.currentThread().sleep(2000);
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
		
	
	
    
//	public static void main(String[] args) throws IOException {
//        /*
//         * Create your credentials file at ~/.aws/credentials (C:\Users\USER_NAME\.aws\credentials for Windows users) 
//         * and save the following lines after replacing the underlined values with your own.
//         *
//         * [default]
//         * aws_access_key_id = YOUR_ACCESS_KEY_ID
//         * aws_secret_access_key = YOUR_SECRET_ACCESS_KEY
//         */
//
////        AmazonS3 s3 = new AmazonS3Client();
//    	//user gubkup
////    	String access_key_id = "AKIAJXUTJVYGR3V2W6GA";
////        String secret_access_key= "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";
////        BasicAWSCredentials awsCreds = new BasicAWSCredentials(access_key_id, secret_access_key);
//        PBECredentialsProvider pbecp = new PBECredentialsProvider();
//        AmazonS3 s3 = new AmazonS3Client(pbecp.getCredentials());
//        Region usWest2 = Region.getRegion(Regions.EU_WEST_1);
//        s3.setRegion(usWest2);
//        
//        
//
////        String bucketName = "gubucket-01" + UUID.randomUUID();
//        String bucketName = "gubucket-01";
//        String key = "MyObjectKey"+UUID.randomUUID();
//
//        System.out.println("===========================================");
//        System.out.println("Getting Started with Amazon S3");
//        System.out.println("===========================================\n");
//
//        try {
//            /*
//             * Create a new S3 bucket - Amazon S3 bucket names are globally unique,
//             * so once a bucket name has been taken by any user, you can't create
//             * another bucket with that same name.
//             *
//             * You can optionally specify a location for your bucket if you want to
//             * keep your data closer to your applications or users.
//             */
////            System.out.println("Creating bucket " + bucketName + "\n");
////            s3.createBucket(bucketName);
//
//            /*
//             * List the buckets in your account
//             */
////            System.out.println("Listing buckets");
////            for (Bucket bucket : s3.listBuckets()) {
////                System.out.println(" - " + bucket.getName());
////            }
////            System.out.println();
//
//            /*
//             * Upload an object to your bucket - You can easily upload a file to
//             * S3, or upload directly an InputStream if you know the length of
//             * the data in the stream. You can also specify your own metadata
//             * when uploading to S3, which allows you set a variety of options
//             * like content-type and content-encoding, plus additional metadata
//             * specific to your applications.
//             */
//            System.out.println("Uploading a new object to S3 from a file\n");
//            s3.putObject(new PutObjectRequest(bucketName, key, createSampleFile()));
//
//            /*
//             * Download an object - When you download an object, you get all of
//             * the object's metadata and a stream from which to read the contents.
//             * It's important to read the contents of the stream as quickly as
//             * possibly since the data is streamed directly from Amazon S3 and your
//             * network connection will remain open until you read all the data or
//             * close the input stream.
//             *
//             * GetObjectRequest also supports several other options, including
//             * conditional downloading of objects based on modification times,
//             * ETags, and selectively downloading a range of an object.
//             */
//            System.out.println("Downloading an object");
//            S3Object object = s3.getObject(new GetObjectRequest(bucketName, key));
//            System.out.println("Content-Type: "  + object.getObjectMetadata().getContentType());
//            displayTextInputStream(object.getObjectContent());
//
//            /*
//             * List objects in your bucket by prefix - There are many options for
//             * listing the objects in your bucket.  Keep in mind that buckets with
//             * many objects might truncate their results when listing their objects,
//             * so be sure to check if the returned object listing is truncated, and
//             * use the AmazonS3.listNextBatchOfObjects(...) operation to retrieve
//             * additional results.
//             */
//            System.out.println("Listing objects");
//            ObjectListing objectListing = s3.listObjects(new ListObjectsRequest()
//                    .withBucketName(bucketName)
//                    .withPrefix("My"));
//            for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
//                System.out.println(" - " + objectSummary.getKey() + "  " +
//                        "(size = " + objectSummary.getSize() + ")");
//            }
//            System.out.println();
//
//            /*
//             * Delete an object - Unless versioning has been turned on for your bucket,
//             * there is no way to undelete an object, so use caution when deleting objects.
//             */
//            System.out.println("Deleting an object\n");
//            s3.deleteObject(bucketName, key);
//
//            /*
//             * Delete a bucket - A bucket must be completely empty before it can be
//             * deleted, so remember to delete any objects from your buckets before
//             * you try to delete them.
//             */
////            System.out.println("Deleting bucket " + bucketName + "\n");
////            s3.deleteBucket(bucketName);
//        } catch (AmazonServiceException ase) {
//            System.out.println("Caught an AmazonServiceException, which means your request made it "
//                    + "to Amazon S3, but was rejected with an error response for some reason.");
//            System.out.println("Error Message:    " + ase.getMessage());
//            System.out.println("HTTP Status Code: " + ase.getStatusCode());
//            System.out.println("AWS Error Code:   " + ase.getErrorCode());
//            System.out.println("Error Type:       " + ase.getErrorType());
//            System.out.println("Request ID:       " + ase.getRequestId());
//        } catch (AmazonClientException ace) {
//            System.out.println("Caught an AmazonClientException, which means the client encountered "
//                    + "a serious internal problem while trying to communicate with S3, "
//                    + "such as not being able to access the network.");
//            System.out.println("Error Message: " + ace.getMessage());
//        }
//    }
    
   

	




	



	    
    
    

}
