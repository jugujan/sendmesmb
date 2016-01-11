import javax.mail.*;
import javax.mail.internet.*;
import java.util.*;
import java.io.*;
import javax.crypto.spec.*;
import javax.crypto.*;
import org.apache.commons.codec.binary.Base64;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import java.io.FileOutputStream;
import java.net.MalformedURLException;




 
/**
 *
 * @author jacquesr.
 * a faire : classe propre
 */
public class SendMeSmb
{
  boolean debug=false;
  String mailhost = "";
  String delim=";";
  String action1=null;
  String action2=null;
  private String usershare=null;
  private String usersmb=null;
  private String pwdsmb=null;
  private String pwdsmbclair=null;
  private String imapusername  = null;
  private String imappassword  = null;
  private String provider  = "imaps";

  public void setAction1(String a) {
  	action1=a;
  }
  public void setAction2(String a) {
  	action2=a;
  }
  public void setMailhost(String h) {
	mailhost=h;
  }
  public void setDelim(String s) {
  	delim=s;
  }
  public void infowarn(String s) {
	System.out.println(s);
  }
  public void infoerreur(String s) {
	System.out.println(s);
	System.exit(1);	
  }
  public void infodebug(String s) {
	if ( debug) 
		System.out.println(s);
  }

  public void envoimail(String alias,String expe,String sujet, StringBuffer contenu,String file) {
                if ( alias == null || expe == null || sujet == null || contenu == null )
                        return;
                try {
                        Properties props = new Properties();
                        props.put("mail.smtp.host",mailhost);
                        Session mailSession = Session.getInstance(props, null);
                        mailSession.setDebug(false);
                        Address fromUser =
                                new InternetAddress(expe);
                        Address toUser =
                                new InternetAddress(alias);
                        Message body = new MimeMessage(mailSession);
                        body.setFrom(fromUser);
                        body.setRecipient(Message.RecipientType.TO, fromUser);
                        body.setSubject(sujet);
                        body.setSentDate(new java.util.Date());
			if (file != null) {
                		MimeBodyPart mbp1 = new MimeBodyPart();
                		mbp1.setText(contenu.toString());
                		MimeBodyPart mbp2 = new MimeBodyPart();
                		mbp2.attachFile(file);
                		MimeMultipart mp = new MimeMultipart();
                		mp.addBodyPart(mbp1);
                		mp.addBodyPart(mbp2);
                		body.setContent(mp);
            		} else
                        	body.setContent(contenu.toString(),"text/plain");
                        body.setHeader("Content-Transfer-Encoding","quoted-printable");
                        Transport.send(body);
                } catch (Exception excep) {
                        if (debug) excep.printStackTrace();
                }
  }

  public StringBuffer recherche(String s, String nom) {
	StringBuffer sb=new StringBuffer("");
	try {
              BufferedReader fh = new BufferedReader(new FileReader(nom));
              String str;
              while ( (str=fh.readLine()) != null ) {
                        try {
                                if ( !str.equals("null") && str!=null && str.contains(s) ) {
                                        sb.append(str);
                                        sb.append("\n");
                                }
                        } catch ( NumberFormatException excp ) {

                        }
              }
        } catch (IOException ioe) {
		if (debug) ioe.printStackTrace();
		infowarn("Redemander la liste. Fichier utilisateur perdu");
        }
        return sb;
  }

  public String copielocale(String urlsmb,String pwd) {
 	SmbFile smbf = null;
	try {
	String str=urlsmb.replaceFirst(":PWD@",":"+pwd+"@");
 	smbf = new SmbFile( str );
        SmbFileInputStream in = new SmbFileInputStream( smbf );
        FileOutputStream out = new FileOutputStream( smbf.getName() );

        byte[] b = new byte[8192];
        int n = 0;
        while(( n = in.read( b )) > 0 ) {
            out.write( b, 0, n );
        }
	in.close();
        out.close();
	} catch (Exception ioe) {
		if (debug) ioe.printStackTrace();
		infowarn("Le document demandé n'a pas été copié.");
	}
	return smbf.getName();
  }

  public String extrace(String nom,int fileindex) {
	try {
              BufferedReader fh = new BufferedReader(new FileReader(nom));
	      String str;
              while ( (str=fh.readLine()) != null ) {
			String[] strwrd=str.split(delim);
			try {
				if ( Integer.parseInt(strwrd[0]) == fileindex ) {
					int p=str.indexOf(delim);
					return str.substring(p+1);
				}
			} catch ( NumberFormatException excp ) {
				infowarn("Index fichier incorrect");
			}
	      } 
	} catch (IOException ioe) {
        	if ( debug ) ioe.printStackTrace();
		infowarn("Redemander la liste. Fichier utilisateur perdu");
	}
	return null;
  }

  public void trace(String nom,StringBuffer sb,String urlsmb) {
	try {
		if ( nom != null ) {
        		FileWriter out = new FileWriter(nom);
			sb.append("\n");
        		out.write(sb.toString());
        		out.flush();
        		out.close();
		}
  	} catch (IOException ioe) {
        	if (debug) ioe.printStackTrace();
		infowarn("Le fichier utilisateur n'a pas pu être créé");
  	}
  }

  public String authentifier(String oneprop) {
    if ( oneprop !=null ){
    	int pp    =  oneprop.indexOf(':');
    	usershare =  oneprop.substring(0,pp);
    	usersmb   =  oneprop.substring(pp+1,oneprop.indexOf(':',pp+1));
    	pwdsmb    =  oneprop.substring(oneprop.lastIndexOf(':')+1,oneprop.length());
	infodebug(usershare+" "+usersmb+" "+pwdsmb);
	encodeco ed=new encodeco();
        try {
    		byte[] valueDecoded = Base64.decodeBase64(pwdsmb.getBytes("latin1") );
    		byte[] sdecrypt = ed.decrypt(valueDecoded);
    		pwdsmbclair = new String(sdecrypt);
        } catch (Exception eee) { 
		if (debug) eee.printStackTrace();
        }
    }
    return usershare;
  }

  public int extractindex(String str) {
	int idx=0;
	for (String s: str.split(" ")) {
		s.replaceFirst("\r\n"," ");
		try {
System.out.println("*"+s+"*");
                    idx=Integer.parseInt(s);
		    break;
                } catch (NumberFormatException nfe) { nfe.printStackTrace(); }
	}
	return idx;
  }

  public SendMeSmb() {
    String sujet=null;
    String stexte=null;
    String share=null;

    Properties speprops  = new Properties();
    try {
          FileInputStream in = new FileInputStream("sendme.props");
          speprops.load(in);
    } catch (IOException ioe) {
          if (debug) ioe.printStackTrace();
	  infoerreur("Le fichier de configuration sendme.props n'est pas présent");
    }
    String serveursmb   = speprops.getProperty("smbserver");
    setDelim(speprops.getProperty("delim"));
    setAction1(speprops.getProperty("action1"));
    setAction2(speprops.getProperty("action2"));
    String imapdir      = speprops.getProperty("imapdir");
    String imaphost     = speprops.getProperty("imaphost");
    String imapusername = speprops.getProperty("imapusername");
    String imappassword = speprops.getProperty("imappassword");
    String imapdebug    = speprops.getProperty("imapdebug");
    String printdebug   = speprops.getProperty("debug");
    if ( printdebug.equals("true") )
	debug=true;
    else
        debug=false;
    String pmailhost    = speprops.getProperty("mailhost");
    if ( pmailhost == null )
	infoerreur("mailhost absent");
    setMailhost(pmailhost);

    int depthcrawl = Integer.parseInt(speprops.getProperty("depthcrawl"));
    
    try
    {
      Properties props = System.getProperties();
      props.put("mail.imaps.ssl.trust", imaphost);
      Session session = Session.getInstance(props, null);
      session.setDebug(imapdebug.equals("true")?true:false);
      Store store     = session.getStore(provider);
      store.connect(mailhost, imapusername, imappassword);
      Folder inbox = store.getFolder(imapdir);
      inbox.open(Folder.READ_WRITE);
      Message[] messages = inbox.getMessages();
 
      String urlsmb=null;
      String urlsmbmasq=null;
      Address[] adresse;
      boolean lu;
      for(int i = 0; i < messages.length; i++)
      {
        sujet   = messages[i].getSubject();

        adresse = messages[i].getFrom();
	InternetAddress nia=new InternetAddress(adresse[0].toString());
	String[] str=nia.getAddress().split("@");
	infodebug("adresse.toString:"+adresse[0].toString()+" "+adresse[0].getType()+" "+nia.getAddress());
    	String oneprop = speprops.getProperty(str[0]);
    	share = authentifier(oneprop);

        lu = messages[i].isSet( Flags.Flag.SEEN);
        if ( !lu ) {
//CHERCHE
            if ( sujet.toUpperCase().startsWith( action1 ) ) {
		String[] tsujet = sujet.split( action1+" ");
		if ( tsujet.length>1 )
			stexte=tsujet[1];
		urlsmb="smb://"+usersmb+':'+pwdsmbclair+'@'+serveursmb+'/'+share+'/';
		urlsmbmasq="smb://"+usersmb+":PWD@";
		infodebug("urlsmb:"+urlsmb);
		infodebug("stexte:"+stexte);
		infodebug(adresse[0].toString());
		infodebug(' '+sujet+' '+(lu?"Lu":"Non lu"));
		if ( urlsmb!=null) {
	    		ListShare2Buffer lf = new ListShare2Buffer(delim);
			lf.xml=new StringBuffer("");
			try {
				lf.traverse(new SmbFile(urlsmb),depthcrawl,urlsmbmasq);
			} catch (MalformedURLException ie) {
			} catch (Exception ie) {
			}
			trace(usersmb,lf.xml,null);
            		envoimail(adresse[0].toString(),adresse[0].toString(),action2+" indiquer le numero d'index du fichier dans le sujet ou le corps du message", recherche(stexte,usersmb),null);
			}
            }
//DONNE
            if ( sujet.toUpperCase().contains( action2 ) ) {
		int idssj = sujet.indexOf( action2 );
                String ssj = sujet.substring(idssj+5);
                String urlsmbtmp=null;
		int indexfichier = extractindex(ssj);
  		if ( indexfichier <= 0 ) {
			try {
				Object o = messages[i].getContent();
        			if (o instanceof String) {
            				ssj=(String)o;
					indexfichier = extractindex(ssj.substring(0,10));
        			} 
			} catch (Exception e123) { e123.printStackTrace(); }
		}
		urlsmbtmp = extrace(usersmb,indexfichier);
		if ( urlsmbtmp != null ) {
			String toattach = copielocale(urlsmbtmp,pwdsmbclair);
            		envoimail(adresse[0].toString(),adresse[0].toString(),"Fichier demande", new StringBuffer("fichier demandé"),toattach);
		}
	    }
	    messages[i].setFlag( Flags.Flag.SEEN,true);
        }
      }
      inbox.close(false);
      store.close();
    }
    catch (NoSuchProviderException nspe)
    {
      if (debug) nspe.printStackTrace();
      infoerreur("Le nom du provider n'est pas bon");
    }
    catch (MessagingException me)
    {
      if (debug) me.printStackTrace();
      infowarn("Erreur dans la lecture des messages");
    }
  }
 
  public static void main(String[] args)
  {
    SendMeSmb es = new SendMeSmb();
  }
}
