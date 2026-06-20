package model;

//Store the reservation information
public class Reservation {
	private String reservationId;
	private Student student;
	private Seat seat;
	private int startTime;
	private int endTime;
	private ReservationStatus status;
	/*public void cancel() {
		status=ReservationStatus.CANCELLED;
	}*/
	public Reservation(String reservationId,Student student,Seat seat,int startTime,int endTime,ReservationStatus status) {
		this.reservationId=reservationId;
		this.student=student;
		this.seat=seat;
		this.startTime=startTime;
		this.endTime=endTime;
		this.status=status;
	}
	public String getDetails() {
		return "The reserver is "+student
				+". Reserved seat is "+seat
				+". Start from "+startTime+". End at "+endTime+".";
	}
	public ReservationStatus getStatus() {
		return status;
	}
	public void setStatus(ReservationStatus r) {
		status=r;
	}
	public String getReservationId() {
		return reservationId;
	}
	public Student getStudent() {
		return student;
	}
	public Seat getSeat() {
		return seat;
	}
	public int getStartTime() {
		return startTime;
	}
	public int getEndTime() {
		return endTime;
	}
}
