package bawal_pasaway;

import bawal_pasaway.obj.*;
import java.util.Scanner;
import java.util.Date;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.Random;


/*
JOKE: Walang tiwala sa sarili kaya pinahirapan ang sarili hHAHAH

*/

public class PWManager {
	static Scanner input = new Scanner(System.in);
	private static Helper helper = new Helper();
	private static ArrayList<Account> accountsList = new ArrayList<Account>();
	private static int MIN_DAYS = 3;
	

  public static void printMenu(ArrayList<String> names, int len){
  	System.out.println("     __________");
  	System.out.println("    | ACCOUNTS |");
  	System.out.println("     ----------");
  	for(int i=1; i<len; i++){
  		System.out.println("["+i+"] "+names.get(i-1));
  	}
  	System.out.println("    ------------");
  	System.out.println("["+len+"] ADD ANOTHER");
  	System.out.println("["+(len+1)+"] EXIT");
  }

  public static void printSubMenu(){
	System.out.println("\t[1] VIEW Password");
	System.out.println("\t[2] GENERATE NEW Password");
	System.out.println("\t[3] UPDATE Lock days");
	System.out.println("\t[4] DELETE THIS OPTION");
	System.out.println("\t[5] EXIT");
	System.out.print("\tChoice: ");
  }

  public static void printConfirmation(){
  	System.out.println("\tAre you sure?");
	System.out.println("\t[1] Yes \n\t[2] No ");
	System.out.print("\tChoice: ");
  }

  public static void main(String[] args){
  	accountsList = helper.readAccounts();
  	//try{ readAccounts();}catch(Exception e){ System.out.println("Hello");}
  	//Account one = new Account(new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7)), "112223AA", "IG","AN",40,7);
  	//Account two = new Account(new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(6)), "112223AA", "Twitter","AN",40,7);
  	//Account three = new Account(new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(8)), "112223AA", "FB","AN",40,7);
  	//accountsList.add(one);
  	//accountsList.add(two);
  	//accountsList.add(three);
  	Date now = helper.getServerHttpDate();
  	if(now == null){
  		System.out.println("!!! You have a problem with your System Date !!!");
  		return;	
  	}
  	System.out.println("Current Date: " + now);
  	
  	

   	ArrayList<String> names = helper.getNames();
    boolean returnFlag=false;
    int choice;
    int len = names.size()+1;
    
    do{
    	printMenu(names,len);
    	System.out.print("Choice: ");
    	choice = input.nextInt();
    	if(choice > 0 && choice < len){
    		int subChoice;
  			do{
  				Account account = accountsList.get(choice-1);
  				Date date1 = account.getOpenDate();
  				int lockDays = account.getLockDays();
    			printSubMenu();
    			subChoice = input.nextInt();

    			if(subChoice==1){
    				Date date2 = helper.getServerHttpDate();
    				long diff = date2.getTime() - date1.getTime();
    				Long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    				System.out.println ("Days: " + days);
    				
    				if(days==0 || lockDays<=days){
	    				//if lock days <= Days diff --> if diff ==0 allow

	    				System.out.println("\n==========================================================================");
	    				System.out.println("PASSWORD: " + account.getPassword());
	    				System.out.println("==========================================================================\n");

	    				accountsList.get(choice-1).setOpenDate(date2);
	    				System.out.println("UPDATED OPEN DATE:"+ date1+ " --> " + date2 +"\n");
    				}
    				else{
    					System.out.println("\n\tViewing is PROHIBITED.\n");
    					System.out.println ("Remaining Days: " + (lockDays - days));
    				}
    				//else print viewing is prohibited 0
    			}
    			else if(subChoice==2){
    				//Days diff == 0 in open date
    				Date date2 = helper.getServerHttpDate();
    				Long diff = date2.getTime() - date1.getTime();
    				Long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    				System.out.println ("Days: " + days);
    				if(days==0){
	    				String password = "";
	    				password = helper.generatePassword(account);
	    				printConfirmation();
	    				int finality = input.nextInt();
	    				if(finality==1){
	    					accountsList.get(choice-1).setPassword(password);
	    					System.out.println("\n==========================================================================");
		    				System.out.println("NEW PASSWORD: " + password);
		    				System.out.println("==========================================================================\n");
	    				}
    				}else{
    					System.out.println("\n\tPlease view your current PASSWORD first.\n");
    				}
    			}
    			else if(subChoice==3){
    				int inputLockDays = 0;
    				do{
				  		System.out.println("LOCK DAYS: " + account.getLockDays());
    					System.out.print("Number of lock days (More than 2 days): ");
  						inputLockDays = input.nextInt(); 
				  	}while(inputLockDays< MIN_DAYS);
  					accountsList.get(choice-1).setLockDays(inputLockDays);
    			}
    			else if(subChoice==4){
    				printConfirmation();
    				int finality = input.nextInt();
    				if(finality==1){
	    				accountsList.remove(choice-1);
	    				names = helper.getNames();
	    				len = names.size()+1;
	    				subChoice=5;
    				}
    			}
    			else{
    				if(subChoice!=5)
    					System.out.println("Wrong choice input.");
    			}
  			}while(subChoice!=5);
    	}
    	else if(choice==len){
    		accountsList = helper.addMenu(names);
    		names = helper.getNames();
    		len = names.size()+1;
    	}
    	else{
    		if(choice!=len+1)
    			System.out.println("Wrong choice input.");
    	}

    }while(choice!=len+1);
  
    helper.writeAccounts(accountsList);
  }
}