import jcifs.netbios.NbtAddress;
import jcifs.smb.SmbFile;
import java.util.Date;
import java.lang.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.net.MalformedURLException;

/**
jacquesr
ListShare2Buffer crawl smb server
*/
public class ListShare2Buffer {
   StringBuffer xml=new StringBuffer(10248);
   private int increment=1;

   public void traverse( SmbFile f, int depth , String masq)  throws MalformedURLException, IOException {
        if( depth == 0 || f == null ) {
            return;
        }
       	SmbFile[] l = f.listFiles();
	try {
        	for(int i = 0; l != null && i < l.length; i++ ) {
			if ( l[i] != null ) {
                		xml.append( String.valueOf(increment++) ); 
                		xml.append( ';' ); 
				int p=l[i].toString().indexOf("@");
                		xml.append( masq);
                                xml.append( l[i].toString().substring(p+1) ); 
				xml.append("\n");
			}
                	if( l[i].isDirectory() ) {
                    		traverse( l[i], depth - 1 , masq);
                	}
        	}
	} catch (IOException ioe) {
	}
   }
}
