package bawal_pasaway.obj;


import java.util.Date;

public class Account{
	private Date openDate = new Date();
	private String password = "";
	private String name;
	private String mode;
	private int maxLength;
	private int lockDays;

	public Account(Date date, String pw, String name, String mode, int maxLength, int lockDays){
		this.openDate = date;
		this.password = pw;
		this.name = name;
		this.mode = mode;
		this.maxLength = maxLength;
		this.lockDays = lockDays;
	}

	public Date getOpenDate(){
		return this.openDate;
	}

	public String getPassword(){
		return this.password;
	}

	public String getName(){
		return this.name;
	}

	public String getMode(){
		return this.mode;
	}

	public int getMaxLength(){
		return this.maxLength;
	}

	public int getLockDays(){
		return this.lockDays;
	}

	public void setOpenDate(Date date){
		this.openDate = date;
	}

	public void setPassword(String pw){
		this.password = pw;
	}

	public void setName(String name){
		this.name = name;
	}

	public void setMode(String mode){
		this.mode = mode;
	}

	public void setMaxLength(int maxLength){
		this.maxLength = maxLength;
	}

	public void setLockDays(int lockDays){
		this.lockDays = lockDays;
	}

}