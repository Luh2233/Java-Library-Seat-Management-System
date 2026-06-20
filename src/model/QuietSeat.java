package model;

// Quiet seat
public class QuietSeat extends Seat  {
	private boolean quiet=true;
	public String display() {
		return getSeatId()+"[quiet]";
	}
	public QuietSeat(String sid,int sfloor,String szone) {
		super(sid,sfloor,szone);
	}
}
