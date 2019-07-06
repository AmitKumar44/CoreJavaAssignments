package insurance.dump;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Insurance {
	
	//static final String FILE_PATH = "C:\\Temp\\input.txt";
	static ValueObject vo = new ValueObject();
	static List<ValueObject> HTDelayedList = new ArrayList<ValueObject>();
	static List<ValueObject> HTOnTimeList = new ArrayList<ValueObject>();
	static List<ValueObject> MBDelayedList = new ArrayList<ValueObject>();
	static List<ValueObject> MBOnTimeList = new ArrayList<ValueObject>();
	static Map<String, Map<String, List<ValueObject>>> finalOutputMap = new HashMap<String, Map<String, List<ValueObject>>>();

	public static void main(String[] args) {
		String file = new File("").getAbsolutePath();
		file= file.concat("\\src\\insurance.txt");

		/** Read the Input data from file */
		List<String[]> inputDataList = readFromInputFile(file);

		/** Validate the Input data and filter out the illegal data */
		List<ValueObject> voList = validateInputs(inputDataList, vo);

		/** Prepare final output MAP */
		finalOutputMap = prepareFinalOutput(voList);

	}
//READ FROM INPUT
	private static List<String[]> readFromInputFile(final String fileName) {
		List<String[]> inputDataList = new ArrayList<String[]>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			String singleLineFromInput = null;
			String[] wordsInSingleLine = new String[10];
			while ((singleLineFromInput = br.readLine()) != null) {
				if (null != singleLineFromInput
						&& singleLineFromInput.length() > 0) {
					wordsInSingleLine = singleLineFromInput.split(",");
					inputDataList.add(wordsInSingleLine);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return inputDataList;
	}
//VALIDATE VALIDATE INPUT
	private static List<ValueObject> validateInputs(
			final List<String[]> inputDataList, ValueObject vo) {
		final String PATTERN_POLICY_NO = "^(\\w{2})(/)(\\w{1})(\\d{5})(-)(\\d{8})";
		List<ValueObject> voList = new ArrayList<ValueObject>();
		String temp = null;
		Date premiumDueDate = null;
		Date premiumPaidDate = null;
		double premAmt = 0.0;

		// Iterating the rows of the Input File for Validation
		for (String[] singleRowOfInput : inputDataList) {
			vo = new ValueObject();
			System.out
					.println("----------------------------------------------------------------------");
			boolean dataFromFileIsOK = true;
			//temp = null;
			System.out.println("Validation Policy Number - "
					+ singleRowOfInput[0]);

			// Validation 1 ------ Policy Number ----------------------
			if (null != singleRowOfInput[0]
					&& singleRowOfInput[0].length() > 17) {
				Pattern p1 = Pattern.compile(PATTERN_POLICY_NO);
				if (p1.matcher(singleRowOfInput[0]).matches()) {
					System.out
							.println(" Policy Number PatternMatch Succesfull");
					// Substring the Date within the Policy Number
					temp = singleRowOfInput[0].substring(10,
							singleRowOfInput[0].length());

					// validating Date within the Policy Number
					if (validateDate1(temp.toString())) {
						System.out.println(" Date within Policy Number OK");
						vo.setPolicyNo(singleRowOfInput[0]);
					} else {
						dataFromFileIsOK = false; // reject this row from Input  file
						continue; // leave processing this row and move to the  next row
					}				}
			} else {
				dataFromFileIsOK = false; // reject this row from Input file
				continue; // leave processing this row and move to the next row
			}

			// Validation 2 ------ Premium Amount ----------------------
			System.out.println("Validation Premium Amount - "
					+ singleRowOfInput[2]);
			if (null != singleRowOfInput[2] && singleRowOfInput[2].length() > 1) {
				premAmt = Double.parseDouble(singleRowOfInput[2]);
				if (premAmt < 0) {
					System.out.println("Premium Amount is Negative");
					dataFromFileIsOK = false; // reject this row from Input file
					continue; // leave processing this row and move to the next  row
				}
			}

			// Validation 3 - Premium Due Date
			System.out.println("Validation Premium Due Date - "
					+ singleRowOfInput[3]);
			premiumDueDate = validateDate2(singleRowOfInput[3].trim());
			if (null == premiumDueDate) {
				dataFromFileIsOK = false; // reject this row from Input file
				continue; // leave processing this row and move to the next row
			}

			// Validation 4 - Premium Paid Date
			System.out.println("Validation Premium Paid Date - "
					+ singleRowOfInput[3]);
			premiumPaidDate = validateDate2(singleRowOfInput[3].trim());
			if (null == premiumPaidDate) {
				dataFromFileIsOK = false; // reject this row from Input file
				continue; // leave processing this row and move to the next row
			}

			// If all validation is passed then Add the Input data in VO and put
			// it in the List
			if (dataFromFileIsOK) {
				vo.setPolicyNo(singleRowOfInput[0]);
				vo.setPolicyHolderName(singleRowOfInput[1]);
				vo.setPremiumAmt(premAmt);
				vo.setPremiumDueDate(premiumDueDate);
				vo.setPremiumPaidDate(premiumPaidDate);
				voList.add(vo);
			}

		} // FOR LOOP ends - iteration of each
		return voList;
	}

	/** This method will covert from String to Date using Pattern yyyyMMdd */
	private static boolean validateDate1(final String input) {
		boolean isValidDate = false;
		if (null != input && input.length() > 1) {
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
			try {
				format.setLenient(false);
				Date d = format.parse(input);
				isValidDate = true;
			} catch (ParseException e) {
				System.out.println("Parse Exception Im-Proper Date");
			}
		}
		return isValidDate;
	}

	/** This method will covert from String to Date using Pattern dd/MM/yyyy */
	private static Date validateDate2(final String input) {
		Date formattedDate = null;
		if (null != input && input.length() > 1) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			try {
				sdf.setLenient(false);
				formattedDate = sdf.parse(input);
			} catch (ParseException e) {
				System.out.println("Parse Exception Im-Proper Date");
			}
			System.out.println("Proper Date" + formattedDate);
			return formattedDate;
		}
		return formattedDate;
	}
//PREPARE FINAL OUTPUT
	private static Map<String, Map<String, List<ValueObject>>> prepareFinalOutput(
			List<ValueObject> voList) {
		for (ValueObject vo : voList) {

			if (vo.getPolicyNo().contains("MB")) {
				if (isDelayedPayment(vo.getPremiumDueDate(),
						vo.getPremiumPaidDate())) {
					vo.setFine(vo.getPremiumAmt() * 1.08);
					MBDelayedList.add(vo);
				} else {
					MBOnTimeList.add(vo);
				}

			} else {
				if (isDelayedPayment(vo.getPremiumDueDate(),
						vo.getPremiumPaidDate())) {
					vo.setFine(vo.getPremiumAmt() * 1.08);
					HTDelayedList.add(vo);
				} else {
					HTOnTimeList.add(vo);
				}

			}
		}

		Map<String, List<ValueObject>> innerMapHT = new HashMap<String, List<ValueObject>>();
		innerMapHT.put("DT", HTDelayedList);
		innerMapHT.put("OT", HTOnTimeList);

		Map<String, List<ValueObject>> innerMapMB = new HashMap<String, List<ValueObject>>();
		innerMapHT.put("DT", MBDelayedList);
		innerMapHT.put("OT", MBOnTimeList);

		finalOutputMap.put("HT", innerMapHT);
		finalOutputMap.put("MB", innerMapMB);

		return finalOutputMap;
	}

//DELAYED PAYMENT CALCULATION
	private static boolean isDelayedPayment(final Date dueDate,
			final Date paidDate) {
		if (paidDate.after(dueDate)) {
			return true;
		}
		return false;
	}

}

/** The Value Object Class where the data will be populated from the Input File */

class ValueObject {
	private String PolicyNo;
	private String PolicyHolderName;
	private Double PremiumAmt;
	private Date PremiumDueDate;
	private Date PremiumPaidDate;
	private Double fine;

	public String getPolicyNo() {
		return PolicyNo;
	}

	public void setPolicyNo(String policyNo) {
		PolicyNo = policyNo;
	}

	public String getPolicyHolderName() {
		return PolicyHolderName;
	}

	public void setPolicyHolderName(String policyHolderName) {
		PolicyHolderName = policyHolderName;
	}

	public Double getPremiumAmt() {
		return PremiumAmt;
	}

	public void setPremiumAmt(Double premiumAmt) {
		PremiumAmt = premiumAmt;
	}

	public Date getPremiumDueDate() {
		return PremiumDueDate;
	}

	public void setPremiumDueDate(Date premiumDueDate) {
		PremiumDueDate = premiumDueDate;
	}

	public Date getPremiumPaidDate() {
		return PremiumPaidDate;
	}

	public void setPremiumPaidDate(Date premiumPaidDate) {
		PremiumPaidDate = premiumPaidDate;
	}

	public Double getFine() {
		return fine;
	}

	public void setFine(Double fine) {
		this.fine = fine;
	}

}

