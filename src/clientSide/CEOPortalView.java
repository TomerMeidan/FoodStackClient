package clientSide;

import java.io.IOException;

import org.json.simple.JSONObject;

import common.Logger;
import common.Logger.Level;
import common.Message;
import javaFXControllers.ViewMonthlyReportsWindow;
import javaFXControllers.CEO.CEOWindow;
import javaFXControllers.CEO.ViewDownloadQuarterlyReportsWindow;
import javaFXControllers.CEO.ViewQuarterlyIncomeReportsWindow;
import javaFXControllers.CEO.ViewQuarterlyReportsWindow;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class CEOPortalView implements PortalViewInterface {

	private ComController com;
	private String firstName;	
	private JSONObject personalInfo;
	
	// ----- LOGIN variables

	private Stage primaryStage;
	private CEOWindow ceoWindow;
	private ViewMonthlyReportsWindow viewMonthlyReportsWindow;
	private ViewQuarterlyReportsWindow viewQuarterlyReportsWindow;
	private ViewQuarterlyIncomeReportsWindow viewQuarterlyIncomeReports;
	private ViewDownloadQuarterlyReportsWindow viewDownloadQuarterlyReportsWindow;
	private HBox homePageHBox;

	public CEOPortalView(Stage primaryStage, ComController com) {
		this.primaryStage = primaryStage;
		this.com = com;
	}

	@Override
	public void init(JSONObject json) {

		try {
			personalInfo = json;

			// LOGIN
			FXMLLoader loader1 = new FXMLLoader();
			loader1.setLocation(getClass().getResource("/templates/CEOHomepageTemplate.fxml"));
			homePageHBox = loader1.load();
			ceoWindow = loader1.getController();
			ceoWindow.init(homePageHBox, primaryStage, this);

			// MONTHLY REPORTS
			FXMLLoader loader2 = new FXMLLoader();
			loader2.setLocation(getClass().getResource("/templates/ViewMonthlyReportsTemplate.fxml"));
			homePageHBox = loader2.load();
			viewMonthlyReportsWindow = loader2.getController();
			viewMonthlyReportsWindow.init(homePageHBox, primaryStage, this, personalInfo);

			// QUARTERLY REPORTS
			FXMLLoader loader3 = new FXMLLoader();
			loader3.setLocation(getClass().getResource("/templates/ViewQuarterlyReportsTemplate.fxml"));
			homePageHBox = loader3.load();
			viewQuarterlyReportsWindow = loader3.getController();
			viewQuarterlyReportsWindow.init(homePageHBox, primaryStage, this, personalInfo);
			
			// INCOME QUARTERLY REPORTS
			FXMLLoader loader4 = new FXMLLoader();
			loader4.setLocation(getClass().getResource("/templates/ViewQuarterlyIncomeReportsTemplate.fxml"));
			homePageHBox = loader4.load();
			viewQuarterlyIncomeReports = loader4.getController();
			viewQuarterlyIncomeReports.init(homePageHBox, primaryStage, this, personalInfo);
			
			// DOWNLOAD QUARTERLY REPORTS
			FXMLLoader loader5 = new FXMLLoader();
			loader5.setLocation(getClass().getResource("/templates/CEODownloadReportWindow.fxml"));
			homePageHBox = loader5.load();
			viewDownloadQuarterlyReportsWindow = loader5.getController();
			viewDownloadQuarterlyReportsWindow.init(homePageHBox, primaryStage, this, personalInfo);
			
			// log
			Logger.log(Level.DEBUG, "CEOPortalView: init: ceoWindow initialized");
			System.out.println("CEOPortalView: init: ceoWindow initialized");
			firstName = Message.getValueString(json, "FirstName");
			Logger.log(Level.DEBUG, "CEOPortalView: init: CEO User " + firstName + " is now on the CEO window");
			System.out.println("CEOPortalView: init: CEO User " + firstName + " is now on the CEO window");
			ceoWindow.showWindow();
		} catch (IOException e) {
			Logger.log(Level.DEBUG, "CEOPortalView: init: IOException was thrown");
			System.out.println("CEOPortalView: init: IOException was thrown");
		}
	}
	/** Handle Message<p>
	 * This method will handle all the actions sent from the server side regarding the ceo.<br>
	 * All sent actions to this class are related for the CEO user alone.
	 * @param descriptor - holds a "command" key with a certain action in the server side under CEO
	 * @author Tomer Meidan
	 * */
	@Override
	public void handleMsg(JSONObject descriptor) {
		switch (Message.getValueString(descriptor, "command")) {

		case "update":
			String message = Message.getValueString(descriptor, "update");
			if (message.equals("all restaurants reports")) {
				Logger.log(Level.DEBUG, "CEOPortalView: handleMsg: recieved list of reports from server: "
						+ descriptor.toString());
				System.out.println("CEOPortalView: handleMsg: recieved list of reports from server: "
						+ descriptor.toString());
				viewMonthlyReportsWindow.setRestaurantsReportData(descriptor);
				viewMonthlyReportsWindow.showWindow();
			} else if(message.equals("quarterly income restaurants reports")) {
				Logger.log(Level.DEBUG, "CEOPortalView: handleMsg: recieved list of income reports from server: "
						+ descriptor.toString());
				System.out.println("CEOPortalView: handleMsg: recieved list of income reports from server: "
						+ descriptor.toString());
				viewQuarterlyIncomeReports.setRestaurantsReportData(descriptor);
				viewQuarterlyIncomeReports.showWindow();
			} else if(message.equals("show view quarterly reports window")) {
				viewQuarterlyReportsWindow.showWindow();
			}
			else if(message.equals("download quarterly reports window")) {
				viewDownloadQuarterlyReportsWindow.setFilesTable(descriptor);
				viewDownloadQuarterlyReportsWindow.showWindow();
			}
			else {
				Logger.log(Level.DEBUG, "CEOPortalView: handleMsg: update: invalid command");
				System.out.println("CEOPortalView: handleMsg: update: invalid command");
			}
			break;

		default:
			break;

		}
	}
	/** Ready<p>
	 * This method calls the handle user action method in order to send to the server
	 * that the homepage ceo window is ready for use.
	 * @param json - holds a "command" key with a certain action in the server side under CEO
	 * @author Tomer Meidan
	 * */
	@Override
	public void ready(JSONObject json) {
		com.handleUserAction(json);
	}

	/** Get Communication Controller<p>
	 * This method returns the communication controller object.
	 * @author Tomer Meidan
	 * @return com - return communication controller.
	 * */
	@Override
	public ComController getComController() {
		return com;
	}

	/** Get View Monthly Reports Window<p>
	 * This method returns the FXML controller for the monthly reports window which was initiated<br>
	 * in the start of CEOPortalView class init method.
	 * @author Tomer Meidan
	 * @return viewMonthlyReportsWindow - return FXML controller class for monthly reports window.
	 * */
	public ViewMonthlyReportsWindow getViewMonthlyReportsWindow() {
		return viewMonthlyReportsWindow;
	}
	
	/** Get View Quarterly Reports Window<p>
	 * This method returns the FXML controller for the quarterly reports window which was initiated<br>
	 * in the start of CEOPortalView class init method.
	 * @author Tomer Meidan
	 * @return viewQuarterlyReportsWindow - return FXML controller class for quarterly reports window.
	 * */
	public ViewQuarterlyReportsWindow getViewQuarterlyReportsWindow() {
		return viewQuarterlyReportsWindow;
	}
	
	/** Get View Quarterly Income Reports Window<p>
	 * This method returns the FXML controller for the quarterly income reports window which was initiated<br>
	 * in the start of CEOPortalView class init method.
	 * @author Tomer Meidan
	 * @return viewQuarterlyReportsWindow - return FXML controller class for quarterly income reports window.
	 * */
	public ViewQuarterlyIncomeReportsWindow getViewQuarterlyIncomeReports() {
		return viewQuarterlyIncomeReports;
	}
	
	/** Get First Name<p>
	 * This method obtains and returns the first name of the ceo user.
	 * @author Tomer Meidan
	 * @return firstName - first name of the ceo is returned.
	 * */
	public String getFirstName() {
		return firstName;
	}
	
	/** Get Ceo Window<p>
	 * This method returns the FXML controller for the ceo homepage window which was initiated<br>
	 * in the start of CEOPortalView class init method.
	 * @author Tomer Meidan
	 * @return viewQuarterlyReportsWindow - return FXML controller class for ceo homepage window.
	 * */
	public CEOWindow getCeoWindow() {
		return ceoWindow;
	}
	
	

}
