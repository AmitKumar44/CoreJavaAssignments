package hospitalmanagement;
/*****
 * This file should be placed in the Default Package. All coding to be done in this single file.
 * No separate class files to be written.
 * Do not change the structure of this file.
 */


/****** Do not add any package declaration *******/

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;



public class PatientInformation {
	
	static final int MRN = 0;
	static final int P_NAME = 1;
	static final int GENDER = 2;
	static final int PHYSICAN_ID = 3;
	static final int ADD_DATE = 4;
	static final int REL_DATE = 5;
	
public static void main(String[] args) throws FileNotFoundException, PatientInformationException {
		
		/******** You should run the Validator before submitting to check if the code structure is valid ********/
		
		//new Validator(); // This class was provided in this same file to check if the code structure is valid
		
	PatientInformation ps = new PatientInformation();
	System.out.println(""+ps.getPatientInformationResponse());
	}
	
	private Map<Integer,Map> pMap = new HashMap<Integer,Map>();
		
	public Map<Integer,Map> getPatientInformationResponse () throws FileNotFoundException, PatientInformationException {
		
		
		/****** Associate should change the file path accordingly ******/
		
		//String fPath="C:\\Users\\303519\\Documents\\PatientInformation\\input.txt";
		String fpath = new File("").getAbsolutePath();
		fpath = fpath.concat("\\src\\input.txt");
		System.out.println(fpath);
		BufferedReader br = new BufferedReader(new FileReader(fpath));
		String info;
		List<String> lStr;
		
		Map<String,List<PatientVO>> mapPatient = new HashMap<String,List<PatientVO>>();
		Map<String,Integer> mapPhysicianCat = new HashMap<String,Integer>();
		
		try {
			while((info = br.readLine()) != null){
				
				lStr = new ArrayList<String>();
				lStr = Arrays.asList(info.split(";"));
				//"[|]"
				try {
					if (validateInput(lStr)){
						PatientVO patient = new PatientVO();
						
						List<Date> lDate = getDate(lStr);
						
						patient.setMrn(lStr.get(PatientInformation.MRN));
						patient.setAdmissionDate(lDate.get(0));
						patient.setDischargeDate(lDate.get(1));
						patient.setGender(lStr.get(PatientInformation.GENDER));
						patient.setPatientName(lStr.get(PatientInformation.P_NAME));
						patient.setPhysicanId(lStr.get(PatientInformation.PHYSICAN_ID));
						
						patient.setBill(calculateBill(lStr,mapPhysicianCat));
						
						if(mapPatient.get(lStr.get(PatientInformation.ADD_DATE)) != null){
							
							List<PatientVO> lstPatient = mapPatient.get(lStr.get(PatientInformation.ADD_DATE));
							lstPatient.add(patient);
							
						}
						else{
							
							List<PatientVO> listVo = new ArrayList<PatientVO>();
							listVo.add(patient);
							mapPatient.put(lStr.get(PatientInformation.ADD_DATE), listVo);
							
						}
							
					}else{
						throw new PatientInformationException("Invalid Patient Data");
					}
				} catch (ParseException e) {
					throw new PatientInformationException("Invalid Data Format");
				}
				
			}
			
			pMap.put(1, mapPatient);
			pMap.put(2, mapPhysicianCat);
			
		} catch (IOException e) {
			
		}
		return pMap; 
	}
	
	
	boolean validateInput(List<String> lStr) throws ParseException{
		
		boolean bRetval = true;
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

		List<Date> lDate = getDate(lStr);
		
		do{
			
			if((null == lStr.get(PatientInformation.P_NAME)) || 
					(lStr.get(PatientInformation.P_NAME).trim().equals("")) || 
					!Pattern.matches("[a-zA-Z\\s]+",lStr.get(PatientInformation.P_NAME))){
				
				bRetval = false;
				break;
			}
		
			if(!(lStr.get(PatientInformation.ADD_DATE).equals(df.format(lDate.get(0)))) || 
					!(lStr.get(PatientInformation.REL_DATE).equals(df.format(lDate.get(1))))){
				bRetval = false;
				break;
			}
			
			if(lDate.get(0).after(lDate.get(1))){
				bRetval = false;
				break;
			}
			
		
			if(!(Pattern.matches("[IN0-9]{5}", lStr.get(PatientInformation.MRN)))){
				
				if(!(Pattern.matches("[OUT0-9]{6}", lStr.get(PatientInformation.MRN)))){
					bRetval = false;
					break;
					
				}
			}
			
			
					
			if(!Pattern.matches("[0-9]*-?(ENT|GEN|NEU)", lStr.get(PatientInformation.PHYSICAN_ID))){
				bRetval = false;
				break;
			}
			
		}while(!bRetval);
		
		return bRetval;
		
	}
	
	List<Date> getDate(List<String> lStr ) throws ParseException{
	
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		Date addDate = null;
		Date relDate = null;
	
		List<Date> dt = new ArrayList<Date>();
		
		try {
			relDate = df.parse(lStr.get(PatientInformation.REL_DATE));
			addDate = df.parse(lStr.get(PatientInformation.ADD_DATE));
			
			dt.add(addDate);
			dt.add(relDate);
		} catch (ParseException e) {
			throw e;
		}
		
		return dt;
		
	}
	
	Integer calculateBill(List<String> lStr ,Map<String,Integer> mapPhysicianCat) throws ParseException{
		
		Integer bill = null;
	
		List<Date> lDate = getDate(lStr);
	
		long datediff = lDate.get(1).getTime() - lDate.get(0).getTime();
		long days = TimeUnit.DAYS.convert(datediff, TimeUnit.MILLISECONDS);
		
		String type = lStr.get(PatientInformation.PHYSICAN_ID).substring(lStr.get(PatientInformation.PHYSICAN_ID).indexOf("-") +1, lStr.get(PatientInformation.PHYSICAN_ID).length());
		
		switch(type){
		case "GEN" :
			bill = 1250 * (int)days;
			break;
		case "ENT" :
			bill = 1500* (int)days;
			break;
		case "NEU" :
			bill = 1750* (int)days;
			break;
		}
		
		Integer noOfP =  mapPhysicianCat.get(type);
		if(noOfP!= null){
			int no = noOfP.intValue();
			noOfP = new Integer(++no);
		}
		else{
			noOfP = new Integer(1);
		}
		
		mapPhysicianCat.put(type, noOfP);
		return bill;
		
	}

}

class PatientVO {
	
	private String mrn;
	private String patientName;
	private String gender;
	private String physicanId;
	private Integer bill;
	private Date admissionDate;
	private Date dischargeDate;
	
	public String getMrn() {
		return mrn;
	}
	public void setMrn(String mrn) {
		this.mrn = mrn;
	}
	public String getPatientName() {
		return patientName;
	}
	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getPhysicanId() {
		return physicanId;
	}
	public void setPhysicanId(String physicanId) {
		this.physicanId = physicanId;
	}
	public Integer getBill() {
		return bill;
	}
	public void setBill(Integer bill) {
		this.bill = bill;
	}
	public Date getAdmissionDate() {
		return admissionDate;
	}
	public void setAdmissionDate(Date admissionDate) {
		this.admissionDate = admissionDate;
	}
	public Date getDischargeDate() {
		return dischargeDate;
	}
	public void setDischargeDate(Date dischargeDate) {
		this.dischargeDate = dischargeDate;
	}
	
	@Override
	public String toString() {
		
		StringBuilder sbd = new StringBuilder();
		
		sbd.append(" { mrn: "+this.getMrn());
		sbd.append(" | patientName: "+this.getPatientName());
		sbd.append(" | gender: "+this.getGender());
		sbd.append(" | physicanId: "+this.getPhysicanId());
		sbd.append(" | bill: "+this.getBill());
		sbd.append(" | admissionDate: "+this.getAdmissionDate());
		sbd.append(" | dischargeDate: "+this.getDischargeDate());
		sbd.append(" }");
		
		return sbd.toString();
	}
	
	
	/****** Necessary Override for equals method was also provided *****/
	
}

class PatientInformationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4991739990256489507L;
	
	public PatientInformationException (String message) {
		super(message);
	}
	
	public PatientInformationException (Throwable th) {
		super(th);
	}
	
}
