package credit.dump;

import java.io.BufferedReader;
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

public class CreditCardMainMap {

	// --DELETE THIS BLOCK - START --------
	public static void main(String args[]) throws CreditCardException {
		final String file_path = "C:\\Temp\\cardInputFile.txt";
		CreditCardMainMap cc = new CreditCardMainMap();
		cc.execute(file_path);
	}

	// --DELETE THIS BLOCK - END --------
	
	public Map<String, Map<String, ValueObjectForCards1>> execute(
			String filename) throws CreditCardException {

		Map<String, Map<String, ValueObjectForCards1>> finalOutputMap = new HashMap<String, Map<String, ValueObjectForCards1>>();
		List<String[]> inputDataList = readFromInputFile(filename);
		Map<String, ValueObjectForCards1> voMap = validateInputs(inputDataList);
		finalOutputMap = prepareFinalOutput(voMap);
		// this is only for testing, do not write this in exam
		printOutputForTesting(finalOutputMap);
		return finalOutputMap;
	}

	//read data from the input file
	private List<String[]> readFromInputFile(final String filePath)
			throws CreditCardException {
		List<String[]> inputDataList = new ArrayList<String[]>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			String singleLineFromInput = null;
			String[] wordsInSingleLine = new String[20];

			while ((singleLineFromInput = br.readLine()) != null) {
				if (singleLineFromInput != null
						&& singleLineFromInput.length() > 0) {
					wordsInSingleLine = singleLineFromInput.split("\\|"); // this is  special
					inputDataList.add(wordsInSingleLine);
				}
				// System.out.println("inputdataList::::::::::::"+inputDataList);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new CreditCardException("Input File Not Found");
		} catch (IOException e) {
			throw new CreditCardException("Input Output Exception");
		}
		return inputDataList;
	}

	//validate the input data
	private Map<String, ValueObjectForCards1> validateInputs(
			final List<String[]> inputDataList) throws CreditCardException {
		Map<String, ValueObjectForCards1> voMap = new HashMap<String, ValueObjectForCards1>();
		ValueObjectForCards1 vo = new ValueObjectForCards1();
		String amexCardNo = null; // 16 digit
		String visaCardNo = null; // 14 or 15 digit starting with 4 or 5
		Date dueDate = null;
		Date billPaidDate = null;
		boolean dataFromFileIsOK = true;
		double billAmount = 0.0;

		for (String[] singleRowOfInput : inputDataList) {
			vo = new ValueObjectForCards1();
			// -------------- for card no and card type validation
			String firstDigit = singleRowOfInput[0].substring(0, 1);
			if (singleRowOfInput[0] != null
					&& singleRowOfInput[0].length() > 15) {
				amexCardNo = singleRowOfInput[0].substring(0,
						singleRowOfInput[0].length());
				vo.setCardType("Amex");
				vo.setCardNo(amexCardNo);
				System.out.println("amexCardNo:::" + vo.getCardNo()
						+ ":CardType:" + vo.getCardType());

			} else if (singleRowOfInput[0] != null
					&& singleRowOfInput[0].length() < 16
					&& firstDigit.equals("4") || firstDigit.equals("5")) {
				visaCardNo = singleRowOfInput[0].substring(0,
						singleRowOfInput[0].length());
				vo.setCardType("Visa");
				vo.setCardNo(visaCardNo);
				System.out.println("visaCardNo:::" + vo.getCardNo()
						+ ":CardType:" + vo.getCardType());

			} else {
				dataFromFileIsOK = false; // discard the card
				throw new CreditCardException("Invalid Credit Card Number "
						+ singleRowOfInput[0].substring(0,
								singleRowOfInput[0].length()));
			}

			// -------------validate DUE date
			dueDate = validateDate(singleRowOfInput[1]);
			if (dueDate == null) {
				dataFromFileIsOK = false; // discard the card
				throw new CreditCardException("DueDate is null for Card "
						+ vo.getCardNo());
			}

			// -------------validate PAID date
			billPaidDate = validateDate(singleRowOfInput[2]);
			// System.out.println("billPaidDate:::::" + billPaidDate);
			if (billPaidDate == null) {
				dataFromFileIsOK = false; // discard the card
				throw new CreditCardException("billPaidDate is null for Card "
						+ vo.getCardNo());
			}

			// ---------- calculate the amount to be paid
			billAmount = Double.parseDouble(singleRowOfInput[3]);
			// System.out.println("Bill amount::::"+billAmount);
			if (billAmount < 0) {
				dataFromFileIsOK = false; // discard the card
				throw new CreditCardException(
						"billAmount is less than 0 for Card " + vo.getCardNo());
			}

			if (dataFromFileIsOK) {
				// vo.setCardNo(singleRowOfInput[0]);
				vo.setDueDate(dueDate);
				vo.setBillDate(billPaidDate);
				vo.setBillAmount(billAmount);
				voMap.put(vo.getCardNo(), vo);
			}

		}

		return voMap;
	}

	//validate and convert the date
	private Date validateDate(final String input) throws CreditCardException {
		Date formattedDate = null;
		if (input != null && input.length() > 1) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			try {
				sdf.setLenient(false);
				formattedDate = sdf.parse(input);
			} catch (ParseException e) {
				throw new CreditCardException("Parse Exception for Date "
						+ input);
			}
		}
		return formattedDate;
	}

	//prepare the output
	private Map<String, Map<String, ValueObjectForCards1>> prepareFinalOutput(
			Map<String, ValueObjectForCards1> voMap) {
		Map<String, ValueObjectForCards1> innerVisaMap = new HashMap<String, ValueObjectForCards1>();
		Map<String, ValueObjectForCards1> innerAmexMap = new HashMap<String, ValueObjectForCards1>();
		Map<String, Map<String, ValueObjectForCards1>> finalOutputMap = new HashMap<String, Map<String, ValueObjectForCards1>>();

		for (String key : voMap.keySet()) {
			ValueObjectForCards1 vo = voMap.get(key);
			if (vo.getCardType().equals("Visa")) {
				if (delayedPayment(vo.getDueDate(), vo.getBillDate())) {
					String grade = determineGrade(vo.getCardType(),
							vo.getDueDate(), vo.getBillDate());
					vo.setGrade(grade);
					System.out.println("Grade::::::" + vo.getGrade());
					Double fine = determineFine(vo.getGrade(),
							vo.getBillAmount());
					vo.setFine(fine);
					System.out.println("Fine::::" + fine);
				} else {
					vo.setFine(0.0); // if there is no fine, then please put the
										// value as 0.0
				}
				innerVisaMap.put(vo.getCardNo(), vo);
			} else {
				if (delayedPayment(vo.getDueDate(), vo.getBillDate())) {
					String grade = determineGrade(vo.getCardType(),
							vo.getDueDate(), vo.getBillDate());
					vo.setGrade(grade);
					System.out.println("Grade::::::" + vo.getGrade());
					Double fine = determineFine(vo.getGrade(),
							vo.getBillAmount());
					vo.setFine(fine);
					System.out.println("Fine::::" + fine);
				} else {
					vo.setFine(0.0); // if there is no fine, then please put the
										// value as 0.0
				}

				innerAmexMap.put(vo.getCardNo(), vo);
			}

		}
		finalOutputMap.put("Visa", innerVisaMap);
		finalOutputMap.put("Amex", innerAmexMap);
		return finalOutputMap;
	}

	//determine the grade
		private String determineGrade(String cardType, Date dueDate,
				Date billPaidDate) {
			String grade = "";
			int diffInDays = (int) ((billPaidDate.getTime() - dueDate.getTime()) / (1000 * 60 * 60 * 24));
			// System.out.println("diffInDays::::::::::::"+diffInDays);
			if (diffInDays > 1 && diffInDays < 5) {
				grade = "A";
			} else if (diffInDays > 5) {
				grade = "B";
			}
			return grade;
		}
		
		//determine the fine
		private Double determineFine(String grade, Double billAmount) {
			Double fine = 0.0;
			if (grade.equals("A")) {
				fine = billAmount * 0.15;
			} else if (grade.equals("B")) {
				fine = billAmount * 0.2;
			}
			return fine;
		}
		
		//check delayed payment
		private boolean delayedPayment(Date dueDate, Date billPaidDate) {
			if (billPaidDate.after(dueDate)) {
				return true;
			}
			return false;
		}

		//print the output
	private void printOutputForTesting(
			Map<String, Map<String, ValueObjectForCards1>> finalOutputMap) {
		Map<String, ValueObjectForCards1> innerVisaMap = finalOutputMap
				.get("Visa");
		Map<String, ValueObjectForCards1> innerAmexMap = finalOutputMap
				.get("Amex");

		System.out.println("VISA MAP");
		for (String key : innerVisaMap.keySet()) {
			ValueObjectForCards1 vo = innerVisaMap.get(key);
			System.out.println(vo.getCardNo() + " | " + vo.getCardType()
					+ " | " + vo.getBillAmount() + " | " + vo.getBillDate()
					+ " | " + vo.getDueDate() + " | " + vo.getFine());
		}

		System.out.println("AMEX MAP");
		for (String key : innerAmexMap.keySet()) {
			ValueObjectForCards1 vo = innerAmexMap.get(key);
			System.out.println(vo.getCardNo() + " | " + vo.getCardType()
					+ " | " + vo.getBillAmount() + " | " + vo.getBillDate()
					+ " | " + vo.getDueDate() + " | " + vo.getFine());
		}

	}


}


class CreditCardException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4991739990256489507L;
	
	public CreditCardException (String message) {
		super(message);
	}
	
	public CreditCardException (Throwable th) {
		super(th);
	}
	
}

class ValueObjectForCards1{
	private String cardType;
	private String cardNo;
	private String grade;
	private Double fine;
	private Double billAmount;
	private Date dueDate;
	public Date getDueDate() {
		return dueDate;
	}
	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}
	private Date billDate;
	public String getCardType() {
		return cardType;
	}
	public void setCardType(String cardType) {
		this.cardType = cardType;
	}
	public String getCardNo() {
		return cardNo;
	}
	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}
	public String getGrade() {
		return grade;
	}
	public void setGrade(String grade) {
		this.grade = grade;
	}
	public Double getFine() {
		return fine;
	}
	public void setFine(Double fine) {
		this.fine = fine;
	}
	public Double getBillAmount() {
		return billAmount;
	}
	public void setBillAmount(Double billAmount) {
		this.billAmount = billAmount;
	}
	public Date getBillDate() {
		return billDate;
	}
	public void setBillDate(Date billDate) {
		this.billDate = billDate;
	}
	
}


