import java.lang.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.crypto.spec.*;
import javax.crypto.*;
import org.apache.commons.codec.binary.Base64;

public class encodeco {
	private String salt="1234567890ABCDEF";

        public void encodeco() {
        }

        public byte[] encrypt(String message) throws Exception {
    		SecretKeySpec key = new SecretKeySpec(salt.getBytes("latin1"), "AES");
    		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding", "SunJCE");
    		cipher.init(Cipher.ENCRYPT_MODE, key);
    		return cipher.doFinal(message.getBytes());
	}

	public byte[] decrypt(byte[] encrypted) throws Exception {
        	SecretKeySpec key = new SecretKeySpec(salt.getBytes("latin1"), "AES");
        	Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding", "SunJCE");
        	cipher.init(Cipher.DECRYPT_MODE, key);
        	byte[] decrypted = cipher.doFinal(encrypted);
        	return decrypted;
	}


	public static void main (String[] args) throws Exception
	{
	if ( args.length<4 ) {
		System.out.println("usage: encodeco emailuser authorized_share smbuser pwdsmbuser");
		System.exit(1);
	}
    	encodeco ed=new encodeco();
    	byte[] aencrypt=ed.encrypt(args[3]);
    	String sencrypt=Arrays.toString(aencrypt);
    	byte[]   bytesEncoded = Base64.encodeBase64(aencrypt);
    	System.out.println(args[0]+"="+args[1]+":"+args[2]+":"+new String(bytesEncoded ));
    	//String sdecode="fCbOFhyNlIFG2K6KjQ==";
    	//byte[] valueDecoded= Base64.decodeBase64(sdecode.getBytes("latin1") );
    	//byte[] sdecrypt=ed.decrypt(valueDecoded);
    	//System.out.println(new String(sdecrypt));
	}
}
