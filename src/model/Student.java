package model;
import java.util.*;
// student user
public class Student extends User {
	private List<Reservation> reservations;
	public Student(String id,String name,String password,int creditscore) {
		super(id,name,password,creditscore);
	}
	public boolean login() {
		return true;
	}
	public void reserve() {}
}
