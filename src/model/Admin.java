package model;

// Administrator with full system access
public class Admin extends User {
	public Admin(String id,String name,String password) {
		super(id,name,password,100);
		setadmin();
	}
	public void setadmin() {
		setAdmin();
	}
	public boolean login() {
		return true;
	}
}
