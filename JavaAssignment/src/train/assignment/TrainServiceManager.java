package train.assignment;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TrainServiceManager {
	FileReader filereader;
	BufferedReader bufferedReader;
	final int TR1MIN=1;
	final int TR1MAX=10;
	final int TR2MIN=11;
	final int TR2MAX=20;
		
		public List<TrainDetails> getTrainDetails(String filePath, int source, int destination,
				String dateOfTravel) throws TrainServCustomExc{
			if(source<TR1MIN || destination>TR2MAX){
				throw new TrainServCustomExc("Train source/destination route doesnt match the inventory");
			}
			if(source==destination){
				throw new TrainServCustomExc("Source and Destination cant be same");
			}
			SimpleDateFormat sf = new SimpleDateFormat("dd-MM-yyyy");
			sf.setLenient(false);
			Date formatteddateOfTravel = null;
			try {
				formatteddateOfTravel = sf.parse(dateOfTravel);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				throw new TrainServCustomExc("Date of Travel is invalid");
			}
			System.out.println(formatteddateOfTravel);
			System.out.println(sf.format(formatteddateOfTravel));
			if(formatteddateOfTravel.compareTo(new Date())<0)
				throw new TrainServCustomExc("Date of travel should always be greater than current date");
			int dayOfWeek=0;
			char special='Y';
			Calendar c = Calendar.getInstance();
			c.setTime(formatteddateOfTravel);
			dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
			special=(dayOfWeek==1?'Y':'N');
			System.out.println(dayOfWeek);
			System.out.println(special);
			
			TrainDetails trainDetails=null;
			List<TrainDetails> listTrainDetails=new ArrayList<TrainDetails>();
			
					return listTrainDetails;
			
		}
	
		public static void main(String[] args) {
			TrainServiceManager trainServiceManager=new TrainServiceManager();
			try {
				trainServiceManager.getTrainDetails("F:\\javadumps\\train.txt", 1, 20, "30-05-2019");
			} catch (TrainServCustomExc e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
}
