package train.assignment;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import java.util.TreeSet;

public class TrainServiceManagerMain {
	FileReader fileReader;
	BufferedReader reader;
	final int TR1MIN = 1; /* Route TR1 minimum station number */
	final int TR1MAX = 10; /* Route TR2 maximum station number */
	final int TR2MIN = 11; /* Route TR2 minimum station number */
	final int TR2MAX = 20; /* Route TR2 maximum station number */
	
	
	public List<TrainDetailsVO> getTrainDetails(final String filePath, int source,
			int destination, String dateOfTravel)
			throws TrainServiceException {
		
		if (source < TR1MIN || destination > TR2MAX) { /* check for the source and destination outside of the range */
			throw new TrainServiceException(
					"Train source/destination route input does not match the master inventory");
		}
		if (source==destination) throw new TrainServiceException("Train source/destination are same"); /* check for the same source and destination */
				
		SimpleDateFormat formatter=null;
		formatter = new SimpleDateFormat("yyyy/MM/dd"); 
		formatter.setLenient(false);
		Date tempDateOfTravel=null;
		int dayOfWeek=0;
		char special = 'Y';
		try {
			tempDateOfTravel = formatter.parse(dateOfTravel);
			} catch (ParseException e) {
			throw new TrainServiceException("Invalid date input");
		}
			
			System.out.println(tempDateOfTravel.compareTo(new Date()));
			
		if((tempDateOfTravel.compareTo(new Date())<0)) throw new TrainServiceException("Travel date should always be greater than current date");
		
		Calendar c = Calendar.getInstance();
		c.setTime(tempDateOfTravel);
		dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
		special =(dayOfWeek==1?'Y':'N');			/* Identifying the day of the travel. 1 - Sunday */
		System.out.println("DayOfWeek "+dayOfWeek);
		TrainDetailsVO trainDetailsVO = null;
		List<TrainDetailsVO> trainDetailsVOList = new ArrayList<TrainDetailsVO>(); /* List of Train details that need to be returned */
		String currLine=null;
		String[] tempArray = null;
		try {
			fileReader = new FileReader(filePath);
			reader = new BufferedReader(fileReader);
			while ((currLine = (reader.readLine())) != null) {
				tempArray = currLine.split(",");
				int tempSource = Integer.parseInt(tempArray[2]);
				int tempDestination = Integer.parseInt(tempArray[3]);
				char tempSpecial = tempArray[4].toCharArray()[0];
				char fullsearch ='Y';
				if(special == 'Y')fullsearch = 'N';else fullsearch ='Y'; //whether it is search for special train or not*/
				if(tempSource == source && tempDestination == destination && (fullsearch=='Y')?true:(tempSpecial==special)) {
				trainDetailsVO = new TrainDetailsVO();
				trainDetailsVO.setTrainNumber(tempArray[0]); /*train number*/
				trainDetailsVO.setRoute(tempArray[1]);       /*train route*/
				trainDetailsVO.setSource(tempSource); /*train source station*/
				trainDetailsVO.setDestination(tempDestination); /*train destination station*/
				trainDetailsVO.setSpecial(tempSpecial); /*special status*/ 
				trainDetailsVO.setDateOfTravel(tempDateOfTravel); /*date of travel*/
				trainDetailsVOList.add(trainDetailsVO);
				}
			}
	    } catch (FileNotFoundException e) {
			throw new TrainServiceException ("File Not Found"+e.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}

		return trainDetailsVOList;

	}

	 /* Return the special trains */
	public Map getTrainSchedule(String filePath) throws TrainServiceException {

		String currLine=null;
		String[] tempArray = null;
		TreeSet<Integer> trainNumbers = new TreeSet<Integer>(); 
		TreeMap<Integer,TreeSet<Integer>> specialTrains= new TreeMap<Integer,TreeSet<Integer>>(); /* Map of special train to be returned*/
		try {
			fileReader = new FileReader(filePath);
			reader = new BufferedReader(fileReader);
			while ((currLine = (reader.readLine())) != null) {
				tempArray = currLine.split(",");
				if(tempArray[4].equals("Y")){ /*Check for special train */
					trainNumbers.add(Integer.parseInt(tempArray[0]));			
				}
			}
					
		} catch (FileNotFoundException e) {
			throw new TrainServiceException ("File Not Found"+e.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		specialTrains.put(1, trainNumbers);
		return specialTrains;
		
	}

}

/* Train Detail Value Object */
class TrainDetailsVO {
	private String trainNumber;
	private String route;
	private int source;
	private int destination;
	private char special;
	private Date dateOfTravel;
	
	public String getTrainNumber() {
		return trainNumber;
	}

	public void setTrainNumber(final String trainNumber) {
		this.trainNumber = trainNumber;
	}

	public String getRoute() {
		return route;
	}

	public void setRoute(final String route) {
		this.route = route;
	}

	public int getSource() {
		return source;
	}

	public void setSource(final int source) {
		this.source = source;
	}

	public int getDestination() {
		return destination;
	}

	public void setDestination(final int destination) {
		this.destination = destination;
	}

	public char getSpecial() {
		return special;
	}

	public void setSpecial(final char status) {
		this.special = special;
	}

	public void setDateOfTravel(final Date dateOfTravel){
		this.dateOfTravel= dateOfTravel;
	}
	public Date getDateOfTravel(){
		return dateOfTravel;
	}
	    
	

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		TrainDetailsVO other = (TrainDetailsVO) obj;
		
		if (trainNumber == null) {
			if (other.trainNumber != null) {
				return false;
			}
		} else if (!trainNumber.equals(other.trainNumber)) {
			return false;
		}
		if (route == null) {
			if (other.route != null) {
				return false;
			}
		} else if (!route.equals(other.route)) {
			return false;
		}
		if (special == ' ') {
			if (other.special != ' ') {
				return false;
			}
		} else if (special != other.special) {
			return false;
		}
		if (destination != other.destination) {
			return false;
		}
		if (source != other.source) {
			return false;
		}
	
		return true;
	}

}

/* User defined Exception */
class TrainServiceException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TrainServiceException(String message) {
		super(message);
	}

	public TrainServiceException(Throwable throwable) {
		super(throwable);
	}
}