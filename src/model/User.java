package model;

// Abstract user with credit score and ban
public abstract class User {
	private String userId;
	private String name;
	private String password;
	private boolean admin=false;
	private int creditScore;
	private boolean banned=false;
	public User(String userId,String name,String password,int creditScore) {
		this.userId=userId;
		this.name=name;
		this.password=password;
		this.creditScore=creditScore;
	}
	public abstract boolean login();
	public int getCreditScore() {
		return creditScore;
	}
	public void setAdmin() {
		admin=true;
	}
	public void addCreditScore(int score) {
		creditScore = Math.min(100, creditScore + score);
	}
	public void trueBanned() {
		banned=true;
	}
	public void falseBanned() {
		banned=false;
	}
	public String getUserId() {
		return userId;
	}
	public String getName() {
		return name;
	}
	public String getPassword() {
		return password;
	}
	public boolean isAdmin() {
		return admin;
	}
	public boolean isBanned() {
		return banned;
	}
}
