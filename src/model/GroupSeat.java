package model;

// Group-zone seat with capacity.
public class GroupSeat extends Seat {
	private int maxCapacity=6;
	public String display() {
		return getSeatId()+"[group]";
	}
	public GroupSeat(String sid,int sfloor,String szone) {
		super(sid,sfloor,szone);
	}
}
