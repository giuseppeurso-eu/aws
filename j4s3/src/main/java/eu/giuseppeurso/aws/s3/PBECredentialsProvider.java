package eu.giuseppeurso.aws.s3;


import java.io.UnsupportedEncodingException;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;

import eu.giuseppeurso.security.jca.crypto.PasswordBasedEncryption;

/**
 * This class implements the AWSCredentialsProvider interface for providing AWS credentials. This implementation uses a strategy based on 
 * simply providing static credentials that don't change. The AWS user credentials come from a password-based encrypted file.
 * @see eu.giuseppeurso.security.jca.crypto.PasswordBasedEncryption
 * @author giuseppe
 *
 */
public class PBECredentialsProvider implements AWSCredentialsProvider {
	
	private static final String awsAccessIdProperty="aws.access.id";
	private static final String awsAccessKeyProperty="aws.access.key";
	
	private byte[] cipher;
	private String password;
	
	/**
	 * @return the cipher
	 */
	public byte[] getCipher() {
		return cipher;
	}

	/**
	 * @param cipher the cipher to set
	 */
	public void setCipher(byte[] cipher) {
		this.cipher = cipher;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Returns AWSCredentials which the caller can use to authorize an AWS request. This method return a BasicAWSCredentials object containing the
	 * AWS access key ID and secret access key stored in a password-based encrypted file. These credentials are used to securely sign
	 * requests to AWS services.  
	 */
	public AWSCredentials getCredentials() {
		
		String awsAccessId="";
		String awsAccessKey="";
		try {
			awsAccessId = retrieveKeyValue(awsAccessIdProperty);
			awsAccessKey = retrieveKeyValue(awsAccessKeyProperty);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		AWSCredentials awsc = new BasicAWSCredentials(awsAccessId, awsAccessKey);
		return awsc;
	}

	/**
	 * Inherited from the AWSCredentialsProvider interface.
	 */
	public void refresh() {
	}
	
	/**
	 * A method to decrypt a bytes array of credentials.
	 * @return
	 * @throws Exception
	 */
	protected byte[] decryptCredentials() throws Exception {
		byte[] credentials=null;
		credentials = PasswordBasedEncryption.decrypt(cipher, password);
		return credentials;
	}
	
	/**
	 * A method to retrieve the AWS key values.
	 * @param key property to retrieve
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws Exception
	 */
	protected String retrieveKeyValue(String key) throws UnsupportedEncodingException, Exception{
		String keyValue="";
		String plainTextValue = new String(decryptCredentials(), "UTF-8");
		String[] lines = plainTextValue.split(System.getProperty("line.separator"));
		for (int i = 0; i < lines.length; i++) {
			if (lines[i]!=null && lines[i].contains(key)){
			keyValue= lines[i].replaceAll(key+"=", "");
			}
		}
	 return keyValue;	
	}

}
