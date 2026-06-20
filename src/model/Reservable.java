package model;

//interface
public interface Reservable {
	public void reserve() throws exception.SeatUnavailableException;
	public void release();
	public void checkIn();
}
