package train.assignment;

import java.util.Date;

public class TrainDetails {
	private String trainNumber;
	private String route;
	private int source;
	private int destination;
	private char special;
	private Date dateOfTravel;
	public String getTrainNumber() {
		return trainNumber;
	}
	public void setTrainNumber(String trainNumber) {
		this.trainNumber = trainNumber;
	}
	public String getRoute() {
		return route;
	}
	
	public void setRoute(String route) {
		this.route = route;
	}
	public int getSource() {
		return source;
	}
	public void setSource(int source) {
		this.source = source;
	}
	public int getDestination() {
		return destination;
	}
	public void setDestination(int destination) {
		this.destination = destination;
	}
	public char getSpecial() {
		return special;
	}
	public void setSpecial(char special) {
		this.special = special;
	}
	public Date getDateOfTravel() {
		return dateOfTravel;
	}
	public void setDateOfTravel(Date dateOfTravel) {
		this.dateOfTravel = dateOfTravel;
	}
	

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TrainDetails other = (TrainDetails) obj;
		if (dateOfTravel == null) {
			if (other.dateOfTravel != null)
				return false;
		} else if (!dateOfTravel.equals(other.dateOfTravel))
			return false;
		if (destination != other.destination)
			return false;
		if (route == null) {
			if (other.route != null)
				return false;
		} else if (!route.equals(other.route))
			return false;
		if (source != other.source)
			return false;
		if (special != other.special)
			return false;
		if (trainNumber == null) {
			if (other.trainNumber != null)
				return false;
		} else if (!trainNumber.equals(other.trainNumber))
			return false;
		return true;
	}
}
