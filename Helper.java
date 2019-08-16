package bawal_pasaway.obj;

import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;
import java.util.Locale;
import java.util.Date;
import java.text.DateFormat;  
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import java.util.List;
import java.util.Arrays;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import java.net.URL;
import java.net.URLConnection;

import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import java.text.SimpleDateFormat;  

public class Helper{
	Scanner input = new Scanner(System.in);
	private File file = new File("storage.txt");
	private int MIN_PW = /*INSERT MINIMIM LENGTH OF PASSWORD*/;
	private int MIN_DAYS = /*INSERT MINIMIM LENGTH OF LOCK DAYS*/;
	
	private String SERVER_URL = "http://worldclockapi.com/";
	private SimpleDateFormat format = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z");
	private String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private String DIGITS = "0123456789";
    private String PUNCTUATION = "!@#$%&*()_+-=[]|,./?><";
    private String SPLITTER = "~`^{}'";
    private final String KEY = /*INSERT NEW 16-bit KEY*/;
    private ArrayList<Account> accountsList = new ArrayList<Account>();

	public String generatePassword(Account a){
	  	// ref: https://stackoverflow.com/questions/19743124/java-password-generator

	  	Random random = new Random(System.nanoTime());
	  	
	    int size = 0;
	    List<String> charCategories = null;

	    switch(a.getMode()){
	    	case "ANP":
	    		charCategories = new ArrayList<String>(
	    			Arrays.asList(LOWER,UPPER,DIGITS,PUNCTUATION));
	    		size = 4;
	    		break;
	    	case "AN":
	    		charCategories = new ArrayList<String>(
		    		Arrays.asList(LOWER,UPPER,DIGITS));
		    	size = 3;
	    		break;
	    	case "N":
	    		charCategories = new ArrayList<String>(
		    		Arrays.asList(DIGITS));
		    	size = 1;
	    		break;
	    }

	    int length = a.getMaxLength() == MIN_PW ? MIN_PW : random.nextInt(a.getMaxLength()) + MIN_PW; //problem with this
	    if(length > a.getMaxLength()) length = a.getMaxLength();

	    StringBuilder  pw = new StringBuilder(length);
	    for (int i = 0; i < length; i++) {
	        String charCategory = charCategories.get(random.nextInt(size));
	        int position = random.nextInt(charCategory.length());
	        pw.append(charCategory.charAt(position));
	    }

	  	return new String(pw);
	}

	public ArrayList<Account> addMenu(ArrayList<String> names){
	  	String name, mode;
	  	int maxLen, lockDays;

	  	do{
	  		System.out.println("PLEASE ENTER");
	  		System.out.print("Name: ");
	  		name =  input.next();
	  		name = name.toUpperCase();
	  	}while(names.contains(name));
	  	do{
	  		System.out.print("TYPE 'N' if Numeric password 'AN' if Alphanumeric password  'ANP' if Alphanumeric with Special Characters : ");
	  		mode = input.next();
	  		mode = mode.toUpperCase();
	  	}while(!(mode.equals("N") || mode.equals("AN") || mode.equals("ANP")));
	  	
	  	do{
		  	System.out.print("Maximum Password Length (Minimum "+MIN_PW+" characters): ");
		  	maxLen = input.nextInt();
	  	}while(maxLen < MIN_PW);

	  	do{
	  		System.out.print("Number of lock days (Minimum "+MIN_DAYS+" days): ");
	  		lockDays = input.nextInt(); 
	  	}while(lockDays< MIN_DAYS);

	  	//set password of new account
	  	int len = accountsList.size();
	  	Account a = new Account(new Date(), "", name,mode,maxLen,lockDays);
	  	String password = generatePassword(a);
	  	a.setPassword(password);
	  	accountsList.add(a);
	  	return this.accountsList;
	}

  	public ArrayList<String> getNames(){
	  	ArrayList<String> names = new ArrayList<String>();
	  	int len = accountsList.size();
	  	for(int i=0; i<len;i++){
	  		names.add(accountsList.get(i).getName());
	  	}
	  	return names;
	 }


  /*
	File Reading & Writing
	File Encryption
	ref: https://www.codejava.net/coding/file-encryption-and-decryption-simple-example
  */

  public void writeAccounts(ArrayList<Account> accountsList){
  	
  	int len = accountsList.size();
  	Random random = new Random(System.nanoTime());
  	int position = random.nextInt(SPLITTER.length());
    char splitter = SPLITTER.charAt(position);
    StringBuilder  finalStr = new StringBuilder();
  	for(int i=0;i<len;i++){
  		String output = ""+accountsList.get(i).getOpenDate()+splitter+accountsList.get(i).getPassword()+splitter+accountsList.get(i).getName()+splitter+accountsList.get(i).getMode()
  						+splitter+accountsList.get(i).getMaxLength()+splitter+ accountsList.get(i).getLockDays()+"\n";
  		finalStr.append(output);
  	}
  	finalStr.append(splitter);
  	String text = new String(finalStr);
  	try 
        {
            Key aesKey = new SecretKeySpec(KEY.getBytes(), "AES");            
            Cipher cipher = Cipher.getInstance("AES");
            // encrypt the text
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            byte[] encrypted = cipher.doFinal(text.getBytes());
             
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(encrypted);
            outputStream.close();
        }
    catch(Exception e) 
    {
        e.printStackTrace();
    }
  }

  public ArrayList<Account> readAccounts(){
  		String decrypted = "";
	  	try 
	        {
	  		Key aesKey = new SecretKeySpec(KEY.getBytes(), "AES");            
	        Cipher cipher = Cipher.getInstance("AES");

	        FileInputStream inputStream = new FileInputStream(file);
	        byte[] encrypted = new byte[(int) file.length()];
	        inputStream.read(encrypted);

	  	  	// decrypt the text
		    cipher.init(Cipher.DECRYPT_MODE, aesKey);
		    decrypted = new String(cipher.doFinal(encrypted));
		    System.err.println(decrypted);

		    inputStream.close();

		    String lines[] = decrypted.split("\\r?\\n");
		    int size = lines.length-1;
		    String splitter = lines[size];
		    DateFormat df = DateFormat.getDateInstance();
		    for(String line : Arrays.copyOfRange(lines, 0, size)){
		    	String e[] = line.split("\\r?"+"\\"+splitter);
		    	if(e.length == 6){
		    		Account a = new Account(new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7)),e[1],e[2],e[3],Integer.valueOf(e[4]),Integer.valueOf(e[5]));
		    		this.accountsList.add(a);
		    	}
		    }

		}catch(Exception e){
	    }

	    return this.accountsList;
	}

	public Date getServerHttpDate(){
		String date = null;
		//ref: https://stackoverflow.com/questions/2891645/java-how-to-get-current-date-independent-from-system-date/13212981
		try{
			URL url = new URL(SERVER_URL);
		    URLConnection connection = url.openConnection();
		    Map<String, List<String>> httpHeaders = connection.getHeaderFields();
		    for (Map.Entry<String, List<String>> entry : httpHeaders.entrySet()) {
		      String headerName = entry.getKey();
		      if (headerName != null && headerName.equalsIgnoreCase("date")) {
		        date = entry.getValue().get(0);
		        break;
		      }
		    }

		    if(date == null) return null;

	    	Date now = format.parse(date);
	  		return now;

		}catch(Exception e){

		}
	    
	    return null;
	}

}