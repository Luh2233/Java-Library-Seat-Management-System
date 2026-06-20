package model;

// Abstract seat with floor, zone, status.
public abstract class Seat implements Reservable {
	private String seatId;
	private int floor;
	private String zone;
	SeatStatus status;
	public Seat(String seatId,int floor,String zone) {
		this.seatId=seatId;
		this.floor=floor;
		this.zone=zone;
		status=SeatStatus.AVAILABLE;
	}
	public Seat(String seatId,int floor,String zone,SeatStatus status) {
		this.seatId=seatId;
		this.floor=floor;
		this.zone=zone;
		this.status=status;
	}
	public String getInfo() {
		return "Seat is "+status;
	}
	public String getInfo(Boolean d) {
		if (!d) {
			return "Seat is " + status;
		}
		return "SeatId is "+seatId
				+". The seat is in "+floor+" floor, "
				+zone+" Zone. It is "+status+" now.";
	}
	public String getSeatId() {
		return seatId;
	}
	public String getZone() {
		return zone;
	}
	public int getFloor() {
		return floor;
	}
	public abstract String display();
	public SeatStatus getStatus() {
		return status;
	}
	public void setStatus(SeatStatus s) {
		status = s;
	}
	public void reserve() throws exception.SeatUnavailableException {
		if (status != SeatStatus.AVAILABLE) {
			throw new exception.SeatUnavailableException(
				"Seat " + seatId + " is " + status);
		}
		status = SeatStatus.RESERVED;
	}
	public void release() {
		status=SeatStatus.AVAILABLE;
	}
	public void checkIn() {
		status=SeatStatus.OCCUPIED;
	}
}
