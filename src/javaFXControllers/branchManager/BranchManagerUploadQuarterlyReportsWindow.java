package javaFXControllers.branchManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import org.json.simple.JSONObject;

import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;

import clientSide.BranchManagerPortalView;
import common.Logger;
import common.Logger.Level;
import common.Message;
import javaFXControllers.branchManager.CreatePDF.PDFGenerator;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class BranchManagerUploadQuarterlyReportsWindow {

	private HBox homePageHBox;
	private Stage primaryStage;
	private BranchManagerPortalView view;
	private Scene scene;
	private JSONObject personalInfo;
	private JSONObject allReports;

	private String currentBranch = "";
	private String currentQuarter = "";
	private String currentYear = "";
	private String managerName = "";
	private ArrayList<String> quarterArray;

	private PDFGenerator PDFGenerator = new PDFGenerator();
	@FXML
	private Button BackButton;

	@FXML
	private Label branchNameLabel;

	@FXML
	private Label managerNameLabel;

	@FXML
	private Label messageAfterUploadLabel;

	@FXML
	private Label quarterNameLabel;

	@FXML
	private Button uploadReportsButton;

	@FXML
	private Label yearNameLabel;

	public void init(HBox homePageHBox, Stage primaryStage, BranchManagerPortalView view, JSONObject personalInfo) {
		this.homePageHBox = homePageHBox;
		this.primaryStage = primaryStage;
		this.view = view;
		this.personalInfo = personalInfo;
		scene = new Scene(homePageHBox);
	}

	/**
	 * Get Relevant Branch Report Info
	 * <p>
	 * 
	 * This method returns a JSON holding relevant information about what time and
	 * place the report is taking place, for example, the method takes three global
	 * variables and updates the relevant information there is to use for the
	 * current last quarter of this year or the next one.
	 * 
	 * @return The object returned is JSONObject that contains the relevant
	 *         information about the current guidelines which data should be saved
	 *         for the FILE creation.
	 * @author Tomer Meidan
	 * 
	 */
	public JSONObject getRelevantBranchReportInfo() {

		JSONObject relevantReportGuidelines = new JSONObject();
		relevantReportGuidelines.put("current quarter", quarterArray);
		relevantReportGuidelines.put("current branch", currentBranch);
		relevantReportGuidelines.put("current year", currentYear);

		return relevantReportGuidelines;
	}

	/**
	 * Set Lables
	 * <p>
	 * This method is determining which current month and year we are at the moment.
	 * In this manner, it is able to update the current last quarter that passed,
	 * which branch the BM belongs to and also set some FX visuals for the gui side
	 * such as the name of the BM, year, month and quarter. The purpose of this
	 * method is to determine which current last quarter has passed to enable the BM
	 * the upload option only by the last quarter.
	 * 
	 * @author Tomer Meidan
	 * 
	 */
	public void setLabels() {

		Date now = new Date();
		managerName = Message.getValue(personalInfo, "FirstName") + " " + Message.getValue(personalInfo, "LastName");
		managerNameLabel.setText(managerName);
		managerNameLabel.setStyle("-fx-font-weight: bold");
		branchNameLabel.setText(Message.getValue(personalInfo, "branch"));
		branchNameLabel.setStyle("-fx-font-weight: bold");

		quarterArray = setChosenQuarter(now.getMonth());
		if (now.getMonth() < 3)
			yearNameLabel.setText(now.getYear() + 1899 + "");
		else
			yearNameLabel.setText(now.getYear() + 1900 + "");
		yearNameLabel.setStyle("-fx-font-weight: bold");
		currentBranch = Message.getValue(personalInfo, "branch");
		currentYear = yearNameLabel.getText();
		currentQuarter = quarterArray.get(0) + " - " + quarterArray.get(2);

	}

	/**
	 * Set Chosen Quarter
	 * <p>
	 * 
	 * This method is setting an ArrayList object to hold a certain array of strings
	 * regarding the previous quarter months of this or last year. The purpose of
	 * this arrayList is to help indicate which quarters are to be returned from the
	 * data base when pulling the reports information about the income, item per
	 * restaurants and performance. In this manner, we initiate a Date object to
	 * learn which current month and year we are at the moment and by this, enabling
	 * to determine which last quarter is needed.
	 * 
	 * @author Tomer Meidan
	 * @param month - input is the current month (INT ranges between 0 to 11
	 *              depending on the current month)
	 * @return ArrayList<String> - returns an array list consisting three months on
	 *         the last quarter.
	 */
	private ArrayList<String> setChosenQuarter(int month) {

		ArrayList<String> quarterArray = new ArrayList<>();

		if (month == 0 || month == 1 || month == 2) {
			quarterArray.add("10");
			quarterArray.add("11");
			quarterArray.add("12");
			quarterNameLabel.setText("October - December");
		} else if (month == 3 || month == 4 || month == 5) {
			quarterArray.add("01");
			quarterArray.add("02");
			quarterArray.add("03");
			quarterNameLabel.setText("January - March");
		} else if (month == 6 || month == 7 || month == 8) {
			quarterArray.add("04");
			quarterArray.add("05");
			quarterArray.add("06");
			quarterNameLabel.setText("April - June");
		} else if (month == 9 || month == 10 || month == 11) {
			quarterArray.add("07");
			quarterArray.add("08");
			quarterArray.add("09");
			quarterNameLabel.setText("July - September");

		}
		quarterNameLabel.setStyle("-fx-font-weight: bold");
		return quarterArray;

	}

	public void showWindow() {
		// log
		Logger.log(Level.INFO,
				"BranchManagerUploadQuarterlyReportsWindow: showWindow: showing the upload reports window");
		System.out.println("BranchManagerUploadQuarterlyReportsWindow: showWindow: showing the upload reports window");

		Platform.runLater(() -> {
			primaryStage.setScene(scene);
			primaryStage.show();
			JSONObject json = new JSONObject();
			json.put("command", "upload reports window is ready");
			view.ready(json);
		});
	}

	/**
	 * On Back Button
	 * <p>
	 * 
	 * This method is a FX trigger that will be initiated upon clicking the back
	 * button on the current upload reports window screen. the method will initiate
	 * a DESTRUCTOR that will reset all relevant variables to the class and returns
	 * to the previous page by using the view communication controllers showWindow
	 * option.
	 * 
	 * @param event - indicates that an event was pressed on the back button option.
	 * @author Tomer Meidan
	 * 
	 */
	@FXML
	void onBackButton(ActionEvent event) {
		Logger.log(Level.WARNING, "BranchManagerUploadQuarterlyReportsWindow: onBackButton : back button was pressed");
		System.out.println("BranchManagerUploadQuarterlyReportsWindow: onBackButton:  back button was pressed");

		// DESTRUCTOR -------------------------
		currentQuarter = "";
		currentYear = "";
		quarterArray = new ArrayList<String>();
		managerName = "";
		messageAfterUploadLabel.setText("");
		// ------------------------------------

		view.showBranchManagerHomePage();
	}

	/**
	 * On Upload Reports Button
	 * <p>
	 * This method is a defined FX trigger to be initiated upon clicking the upload
	 * reports button. this method calls in the PDFGenerator class and creates the
	 * PDF file according to the report file that was given in the global variable
	 * as allReports.
	 * <p>
	 * 
	 * @Stages There are three stages in this method, firstly the PDF class will
	 *         generate a pdf file and temporarily saves it on the src directory.
	 *         Next, the temp file will be transported into a bytearray object and
	 *         will be deleted from the computer. And lastly, using the Base64
	 *         encoding system, the file's bytearray is turned into an encoded
	 *         String object in order to send it to the server side inside a JSON
	 *         object.
	 * @param event - indicates that an event was pressed on the upload report
	 *              button option.
	 * @author Tomer Meidan
	 */
	@FXML
	void onUploadReportsButton(ActionEvent event) {

		Logger.log(Level.WARNING,
				"BranchManagerUploadQuarterlyReportsWindow: onUploadReportsButton : Upload Reports button was pressed");
		System.out.println(
				"BranchManagerUploadQuarterlyReportsWindow: onUploadReportsButton:  Upload Reports button was pressed");

		try {

			File tempFile = createTempPDF(allReports, currentBranch, currentYear, currentQuarter, managerName,
					PDFGenerator);

			JSONObject pdfJSON = prepareFileToSend(tempFile);

			sendFileToServer(tempFile, pdfJSON);

		} catch (IOException e) {
			Logger.log(Level.WARNING,
					"BranchManagerUploadQuarterlyReportsWindow: onUploadReportsButton : IOException was thrown in PDF creation");
			System.out.println(
					"BranchManagerUploadQuarterlyReportsWindow: onUploadReportsButton:  IOException was thrown in PDF creation");
			return;
		}

	}

	/**
	 * Send File To Server
	 * <p>
	 * 
	 * This method will recieve a certain file to transport to the server side using
	 * a JSONObject object.
	 * 
	 * @param tempFile - The file to be transported and deleted from the client
	 *                 side.
	 * @param pdfJSON  - The JSON object that will contain the file inside of it.
	 */
	public void sendFileToServer(File tempFile, JSONObject pdfJSON) {
		if (!tempFile.delete()) {
			System.out.println(
					"BranchManagerUploadQuarterlyReportsWindow: onUploadReportsButton : Temp quarterly report file was not deleted from src/.../tempPDF");
			Logger.log(Level.WARNING,
					"BranchManagerUploadQuarterlyReportsWindow: onUploadReportsButton : Temp quarterly report file was not deleted from src/.../tempPDF");
		} else {
			System.out.println(
					"BranchManagerUploadQuarterlyReportsWindow: onUploadReportsButton : Temp quarterly report file was deleted");
			Logger.log(Level.WARNING,
					"BranchManagerUploadQuarterlyReportsWindow: onUploadReportsButton : Temp quarterly report file was deleted");
		}

		view.getComController().handleUserAction(pdfJSON);
	}

	/**
	 * Prepare File To Send
	 * <p>
	 * This method will take a certain file that was created temporarily on the
	 * computer and push it into a JSONObject in order to send it to the server side
	 * for saving on the DB. The way it is being stored inside the JSON is by using
	 * a certain Base64 system that allows encoding a byteArray into a String, that
	 * way, we can store it inside a JSONObject and decode it at the server side
	 * later on.
	 * 
	 * @param tempFile - File that was created temporarily on the computer.
	 * @return JSONObject - Final JSON the holds a String encoded byte array
	 *         representing the file.
	 * 
	 * 
	 */
	public JSONObject prepareFileToSend(File tempFile) throws IOException {

		JSONObject pdfJSON = new JSONObject();
		pdfJSON.put("command", "upload report file");

		byte[] bytes = getByteArrayFromFile(tempFile);
		String base64String = Base64.encode(bytes);
		pdfJSON.put("byteArray", base64String);

		pdfJSON.put("currentBranch", currentBranch);
		pdfJSON.put("currentYear", currentYear);
		pdfJSON.put("currentQuarter", currentQuarter);
		return pdfJSON;

	}

	/**
	 * Create Temp PDF This method is the main creator of the PDF file as requested.
	 * The method receives various of inputs regarding the certain quarter and also
	 * all the data regarding that quarter. Using iText methods and jars to initiate
	 * and build the PDF file, it will then be stored temporarily on the client side
	 * in order to be transported to the server side later on.
	 * 
	 * @param allReports     - All related report data about a certain quarter by a
	 *                       branch in a certain year.
	 * @param currentBranch  - Certain user\client branch.
	 * @param currentYear    - Certain last year.
	 * @param currentQuarter - Certain last quarter.
	 * @param managerName    - Branch manager name.
	 * @param PDFGenerator   - The main class that will create the PDF file using
	 *                       iText jar methods.
	 * 
	 * @return File - final File that will be created temporarily on the computer.
	 * 
	 */
	public File createTempPDF(JSONObject allReports, String currentBranch, String currentYear, String currentQuarter,
			String managerName, PDFGenerator PDFGenerator) {

		PDFGenerator.setReportsInfo(allReports, currentBranch, currentYear, currentQuarter, managerName);
		PDFGenerator.createPDF();
		String filePath = PDFGenerator.getFilePath();
		File newFile = new File(filePath);
		return newFile;

	}

	/**
	 * Get Byte Array From File
	 * <p>
	 * This method takes a certain File object and turns the file into a byte array
	 * object. The method is static so it can be used by other places in the
	 * project.
	 * 
	 * @param file - a certain file that will be transformed into a byte array.
	 * @return byte[] array object representing a certain input file.
	 * @author Tomer Meidan
	 */
	public static byte[] getByteArrayFromFile(File file) throws IOException {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		final InputStream in = new FileInputStream(file);
		final byte[] buffer = new byte[(int) file.length()];

		int read = -1;
		while ((read = in.read(buffer)) > 0) {
			baos.write(buffer, 0, read);
		}
		in.close();

		return baos.toByteArray();
	}

	/**
	 * Set Upload Message
	 * <p>
	 * 
	 * This method sets the current message about the status of the upload report
	 * file to the server. At the moment there are three types of messages.<br>
	 * 1) Success - the upload was successful and the db was updated.<br>
	 * 2) Failure - the upload was not able to be updated to the server.<br>
	 * 3) Exist - the upload already exist in the data base.
	 * 
	 * @param message - holds current message about thestatus of the upload.
	 * @author Tomer Meidan
	 */
	public void setUploadMessage(String message) {
		Platform.runLater(() -> {
			messageAfterUploadLabel.setText(message);
		});
	}

	/**
	 * Set Restaurants Report Data
	 * <p>
	 * 
	 * This methods updates the main allReports object with the current data about
	 * report information.
	 * 
	 * @param descriptor - Holds three types of report information abut reports.
	 *                   (Income from all restaurants, Items per restaurant and
	 *                   performance of the deliveries)
	 * @author Tomer Meidan
	 */
	public void setRestaurantsReportData(JSONObject descriptor) {
		allReports = descriptor;
	}

}
