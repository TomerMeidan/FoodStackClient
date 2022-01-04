package clientSide;

import java.io.IOException;

import org.json.simple.JSONObject;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import common.Logger;
import common.Logger.Level;
import javaFXControllers.ViewMonthlyReportsWindow;
import javaFXControllers.branchManager.BranchManagerApproveEmployerWindow;
import javaFXControllers.branchManager.BranchManagerEditChoicesWindow;
import javaFXControllers.branchManager.BranchManagerEditRoleWindow;
import javaFXControllers.branchManager.BranchManagerEditStatusWindow;
import javaFXControllers.branchManager.BranchManagerHomePageWindow;
import javaFXControllers.branchManager.BranchManagerRegistrationWindow;
import javaFXControllers.branchManager.BranchManagerSupplierRgstrWindow;
import javaFXControllers.branchManager.BranchManagerUploadQuarterlyReportsWindow;
import common.Message;

/**
 * BranchManagerPortalView
 * 
 * This class is the portalView which handles BranchManager behavior.
 * This class implements PortalViewInterface.
 * This class holds all the javaFX controllers which BranchManager uses.
 * Holds ComController to send messages.
 * Holds JSONObject personalInfo,which holds: portalType,FirstName,LastName,branch.
 * @author Roman Milman
 */
public class BranchManagerPortalView implements PortalViewInterface {

	private ComController com;

	private JSONObject personalInfo;

	// ----- GUI variables

	private Stage primaryStage;
	private ViewMonthlyReportsWindow branchManagerMonthlyReportsWindow;
	private BranchManagerHomePageWindow branchManagerHomePage;
	private BranchManagerRegistrationWindow branchManagerRegistrationWindow;
	private BranchManagerApproveEmployerWindow branchManagerApproveEmployerWindow;
	private BranchManagerSupplierRgstrWindow branchManagerSupplierRgstrWindow;
	private BranchManagerEditStatusWindow branchManagerEditStatusWindow;
	private BranchManagerUploadQuarterlyReportsWindow branchManagerUploadQuarterlyReportsWindow;
	private BranchManagerEditChoicesWindow branchManagerEditChoicesWindow;
	private BranchManagerEditRoleWindow branchManagerEditRoleWindow;
	
	private HBox homePageRoot, uploadQuarterlyRoot, approveEmployerRoot, supplierRgstrRoot, registrationRoot;
	private HBox monthlyReportRoot, editChoicesRoot, editRoleRoot, editStatusRoot;

	public BranchManagerPortalView(Stage primaryStage, ComController com) {
		this.primaryStage = primaryStage;
		this.com = com;
	}

	/**
	 * init
	 * 
	 * This method initializing all the javaFX controllers.
	 * Sets personalInfo to JSONObject descriptor, received from server.
	 * Calls showWindow to show branch Manager's home page.
	 * @param JSONObject descriptor
	 * @author Roman Milman
	 */
	@Override
	public void init(JSONObject descriptor) {
		personalInfo = descriptor;

		try {
			// branchManagerHomePage
			// ------------------------------------------------------------------------------
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/templates/BranchManagerHomePageTemplate.fxml"));
			homePageRoot = loader.load();
			branchManagerHomePage = loader.getController();
			branchManagerHomePage.init(homePageRoot, primaryStage, this);

			// MONTHLY REPORTS
			loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/templates/ViewMonthlyReportsTemplate.fxml"));
			monthlyReportRoot = loader.load();
			branchManagerMonthlyReportsWindow = loader.getController();
			branchManagerMonthlyReportsWindow.init(monthlyReportRoot, primaryStage, this, personalInfo);

			// UPLOAD QUARTERLY REPORTS
			FXMLLoader loader3 = new FXMLLoader();
			loader3.setLocation(getClass().getResource("/templates/BranchManagerUploadQuarterlyReportsTemplate.fxml"));
			uploadQuarterlyRoot = loader3.load();
			branchManagerUploadQuarterlyReportsWindow = loader3.getController();
			branchManagerUploadQuarterlyReportsWindow.init(uploadQuarterlyRoot, primaryStage, this, personalInfo);

			// log
			Logger.log(Level.DEBUG, "BranchManagerPortalView: branchManagerHomePage initialized");
			System.out.println("BranchManagerPortalView: branchManagerHomePage initialized");
			// -------------------------------------------------------------------------------

			// displays branchManagerHomePage window
			branchManagerHomePage.showWindow(descriptor);


		} catch (IOException e) {
			Logger.log(Level.WARNING, "BranchManagerPortalView: init: IOException");
			System.out.println("BranchManagerPortalView: init: IOException");
		}
	}

	/**
	 * handleMsg
	 * 
	 * This method handles Branch Manager's messages received from server.
	 * @param descriptor - read's 'command' key to define which handleCommand method to call.
	 * @author Roman Milman
	 */
	@Override
	public void handleMsg(JSONObject descriptor) {

		switch (Message.getValue(descriptor, "command")) {
		case "update":

			handleUpdateCommand(descriptor);
			break;

		default:
			// log
			Logger.log(Level.DEBUG, "BranchManagerPortalView: handleMsg: unknown msg");
			System.out.println("BranchManagerPortalView: handleMsg: unknown msg");
			break;
		}

	}

	/**
	 * handleUpdateCommand
	 * 
	 * This method handles Branch Manager's update messages received from server.
	 * @param descriptor - read's 'update' key to define what to do.
	 * @author Roman Milman
	 */
	private void handleUpdateCommand(JSONObject descriptor) {
		String updateType = Message.getValue(descriptor, "update");

		switch (updateType) {

		case "show registration window":
			handleEventShowRegistration(descriptor);
			break;
		case "show approve employer window":
			handleEventShowApproveEmployer(descriptor);
			break;
		case "could not register supplier":
			branchManagerSupplierRgstrWindow.showPopup(descriptor);
			break;
		case "supplier has been registered":
			branchManagerSupplierRgstrWindow.showPopup(descriptor);
			break;
		case "all restaurants reports":
			handleEventMonthlyReports(descriptor);
			break;
		case "show supplier registration window":
			handleEventShowSupplierRegistration(descriptor);
			break;
		case "could not add business user to database":
			branchManagerRegistrationWindow.showPopup(descriptor);
			break;
		case "business customer has been registered":
			branchManagerRegistrationWindow.showPopup(descriptor);
			break;
		case "could not add regular user to database":
			branchManagerRegistrationWindow.showPopup(descriptor);
			break;
		case "regular customer has been registered":
			branchManagerRegistrationWindow.showPopup(descriptor);
			break;
		case "could not register regular customer":
			branchManagerRegistrationWindow.showPopup(descriptor);
			break;
		case "employer has been activated":
			branchManagerApproveEmployerWindow.showPopup(descriptor);
			break;
		case "show upload quarterly reports window":
			handleEventUploadReports(descriptor);
			break;
		case "file was uploaded":
			handleEventFileStatus(descriptor);
			break;
		case "show edit choices window":
			handleEventShowEditChoices(descriptor);
			break;
		case "show edit role window":
			branchManagerEditRoleWindow.showWindow(descriptor);
			break;
		case "customer role has been switched":
			branchManagerEditRoleWindow.showPopup(descriptor);
			break;
		case "could not switch customer role":
			branchManagerEditRoleWindow.showPopup(descriptor);
			break;
		case "could not switch role. employer doesn't exists":
			branchManagerEditRoleWindow.showPopup(descriptor);
			break;
		case "show edit status window":
			branchManagerEditStatusWindow.showWindow(descriptor);
			break;
		case "user status has been changed":
			branchManagerEditStatusWindow.showPopup(descriptor);
			break;
		case "could not change user status":
			branchManagerEditStatusWindow.showPopup(descriptor);
			break;
		default:
			// log
			Logger.log(Level.DEBUG, "BranchManagerPortalView: handleUpdateCommand: unknown msg");
			System.out.println("BranchManagerPortalView: handleUpdateCommand: unknown msg");
			break;
		}
	}
	
	private void handleEventFileStatus(JSONObject descriptor) {
		String message = Message.getValue(descriptor, "message");
		Logger.log(Level.DEBUG, message);
		System.out.println(message);
		branchManagerUploadQuarterlyReportsWindow.setUploadMessage(message);

	}

	private void handleEventUploadReports(JSONObject descriptor) {
		Logger.log(Level.DEBUG, "recieved list of reports from server: " + descriptor.toString());
		System.out.println("recieved list of reports from server: " + descriptor.toString());
		branchManagerUploadQuarterlyReportsWindow.setRestaurantsReportData(descriptor);
		branchManagerUploadQuarterlyReportsWindow.showWindow();
	}

	private void handleEventMonthlyReports(JSONObject descriptor) {
		Logger.log(Level.DEBUG, "recieved list of reports from server: " + descriptor.toString());
		System.out.println("recieved list of reports from server: " + descriptor.toString());

		branchManagerMonthlyReportsWindow.setRestaurantsReportData(descriptor);
		branchManagerMonthlyReportsWindow.showWindow();
	}

	/**
	 * handleEventShowEditChoices
	 * 
	 * This method loads all javaFX controllers for edit feature.
	 * Calls loadEditChoicesWindow method.
	 * Shows edit choices window.
	 * @param descriptor - currently not used, (for future flexibility).
	 * @author Roman Milman
	 */
	private void handleEventShowEditChoices(JSONObject descriptor) {
		try {
			loadEditChoicesWindow(descriptor);
			branchManagerEditChoicesWindow.showWindow(descriptor);
		} catch (IOException e) {
			// log
			Logger.log(Level.DEBUG, "BranchManagerPortalView: handleEventShowEditChoices: IOException");
			System.out.println("BranchManagerPortalView: handleEventShowEditChoices: IOException");
		}
	}

	/**
	 * handleEventShowSupplierRegistration
	 * 
	 * This method loads all javaFX controllers for supplier registration feature.
	 * Calls loadSupplierRegistrationWindow method.
	 * Shows supplier registration window.
	 * @param descriptor - 'suppliers' key holds list of potential suppliers for registration.
	 * @author Roman Milman
	 */
	private void handleEventShowSupplierRegistration(JSONObject descriptor) {
		try {
			loadSupplierRegistrationWindow(descriptor);
			branchManagerSupplierRgstrWindow.showWindow(descriptor);
		} catch (IOException e) {
			// log
			Logger.log(Level.DEBUG, "BranchManagerPortalView: handleEventShowSupplierRegistration: IOException");
			System.out.println("BranchManagerPortalView: handleEventShowSupplierRegistration: IOException");
		}
	}

	/**
	 * handleEventShowApproveEmployer
	 * 
	 * This method loads all javaFX controllers for approve employer feature.
	 * Calls loadApproveEmployerWindow method.
	 * Shows employer approval window.
	 * @param descriptor - 'employers' key holds list of potential employer for approval.
	 * @author Roman Milman
	 */
	private void handleEventShowApproveEmployer(JSONObject descriptor) {
		try {
			loadApproveEmployerWindow(descriptor);
			branchManagerApproveEmployerWindow.showWindow(descriptor);
		} catch (IOException e) {
			// log
			Logger.log(Level.DEBUG, "BranchManagerPortalView: handleEventShowApproveEmployer: IOException");
			System.out.println("BranchManagerPortalView: handleEventShowApproveEmployer: IOException");
		}
	}

	/**
	 * handleEventShowRegistration
	 * 
	 * This method loads all javaFX controllers for customer registration feature.
	 * Calls loadRegistrationWindows method.
	 * Shows customer registration window.
	 * @param descriptor - 'customers' key holds list of potential customers for registration.
	 * @author Roman Milman
	 */
	private void handleEventShowRegistration(JSONObject descriptor) {
		try {
			loadRegistrationWindows();
			branchManagerRegistrationWindow.showWindow(descriptor);
		} catch (IOException e) {
			// log
			Logger.log(Level.DEBUG, "BranchManagerPortalView: handleEventShowRegistration: IOException");
			System.out.println("BranchManagerPortalView: handleEventShowRegistration: IOException");
		}
	}

	/**
	 * ready
	 * 
	 * This method sends the server (Window name here) is ready.
	 * @param descriptor - 'command' key specifies which window is ready.
	 * @author Roman Milman
	 */
	@Override
	public void ready(JSONObject json) {
		com.handleUserAction(json);
	}

	/**
	 * getComController
	 * 
	 * This method returns ComController instance.
	 * @return ComController
	 * @author Roman Milman
	 */
	@Override
	public ComController getComController() {
		return com;
	}

	/**
	 * getBranch
	 * 
	 * This method returns this users branch.
	 * @return String
	 * @author Roman Milman
	 */
	public String getBranch() {
		return Message.getValue(personalInfo, "branch");
	}

	/**
	 * getFirstname
	 * 
	 * This method returns this users first name.
	 * @return String
	 * @author Roman Milman
	 */
	public String getFirstname() {
		return Message.getValue(personalInfo, "first name");
	}

	/**
	 * getLastname
	 * 
	 * This method returns this users last name.
	 * @return String
	 * @author Roman Milman
	 */
	public String getLastname() {
		return Message.getValue(personalInfo, "last name");
	}

	/**
	 * showBranchManagerHomePage
	 * 
	 * This method shows branch managers home page.
	 * @author Roman Milman
	 */
	public void showBranchManagerHomePage() {
		branchManagerHomePage.showWindow(personalInfo);
	}

	/**
	 * showRegistrationWindow
	 * 
	 * This method shows branch managers, customer registration window.
	 * @author Roman Milman
	 */
	public void showRegistrationWindow() {
		branchManagerRegistrationWindow.showWindow(personalInfo);
	}

	
	public ViewMonthlyReportsWindow showViewMonthlyReportsWindow() {
		return branchManagerMonthlyReportsWindow;
	}

	/**
	 * showBranchManagerEditChoice
	 * 
	 * This method shows branch managers, edit choices window.
	 * @author Roman Milman
	 */
	public void showBranchManagerEditChoice() {

		branchManagerEditChoicesWindow.showWindow(personalInfo);
	}

	public BranchManagerUploadQuarterlyReportsWindow showBranchManagerUploadQuarterlyReportsWindow() {
		return branchManagerUploadQuarterlyReportsWindow;
	}

	/**
	 * loadRegistrationWindows
	 * 
	 * This method loads javaFX BranchManagerRegistration.fxml controller.
	 * @throws IOException
	 * @author Roman Milman
	 */
	private void loadRegistrationWindows() throws IOException {
		FXMLLoader loader = new FXMLLoader();

		// BranchManagerRegistration
		// ------------------------------------------------------------------------------
		loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/templates/BranchManagerRegistration.fxml"));
		registrationRoot = loader.load();
		branchManagerRegistrationWindow = loader.getController();
		branchManagerRegistrationWindow.init(registrationRoot, primaryStage, this);

		// log
		Logger.log(Level.DEBUG, "BranchManagerPortalView: BranchManagerRegistration initialized");
		System.out.println("BranchManagerPortalView: BranchManagerRegistration initialized");
		// ------------------------------------------------------------------------------
	}

	/**
	 * loadApproveEmployerWindow
	 * 
	 * This method loads javaFX BranchManagerApproveEmployerTemplate.fxml controller.
	 * @throws IOException
	 * @author Roman Milman
	 */
	private void loadApproveEmployerWindow(JSONObject descriptor) throws IOException {
		FXMLLoader loader = new FXMLLoader();

		// BranchManagerRegistration
		// ------------------------------------------------------------------------------
		loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/templates/BranchManagerApproveEmployerTemplate.fxml"));
		approveEmployerRoot = loader.load();
		branchManagerApproveEmployerWindow = loader.getController();
		branchManagerApproveEmployerWindow.init(approveEmployerRoot, primaryStage, this);

		// log
		Logger.log(Level.DEBUG, "BranchManagerPortalView: BranchManagerApproveEmployerWindow initialized");
		System.out.println("BranchManagerPortalView: BranchManagerApproveEmployerWindow initialized");
	}

	/**
	 * loadSupplierRegistrationWindow
	 * 
	 * This method loads javaFX BranchManagerSupplierRegistrationTemplate.fxml controller.
	 * @throws IOException
	 * @author Roman Milman
	 */
	private void loadSupplierRegistrationWindow(JSONObject descriptor) throws IOException {
		FXMLLoader loader = new FXMLLoader();

		// SupplierRegistrationWindow
		// ------------------------------------------------------------------------------
		loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/templates/BranchManagerSupplierRegistrationTemplate.fxml"));
		supplierRgstrRoot = loader.load();
		branchManagerSupplierRgstrWindow = loader.getController();
		branchManagerSupplierRgstrWindow.init(supplierRgstrRoot, primaryStage, this);

		// log
		Logger.log(Level.DEBUG, "BranchManagerPortalView: BranchManagerSupplierRgstrWindow initialized");
		System.out.println("BranchManagerPortalView: BranchManagerSupplierRgstrWindow initialized");
	}

	/**
	 * loadEditChoicesWindow
	 * 
	 * This method loads javaFX BranchManagerEditChoices.fxml controller.
	 * This method loads javaFX BranchManagerEditRole.fxml controller.
	 * This method loads javaFX BranchManagerEditStatus.fxml controller.
	 * 
	 * @throws IOException
	 * @author Roman Milman
	 */
	private void loadEditChoicesWindow(JSONObject descriptor) throws IOException {
		FXMLLoader loader = new FXMLLoader();

		// EditInformationWindow
		// ------------------------------------------------------------------------------
		loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/templates/BranchManagerEditChoices.fxml"));
		editChoicesRoot = loader.load();
		branchManagerEditChoicesWindow = loader.getController();
		branchManagerEditChoicesWindow.init(editChoicesRoot, primaryStage, this);

		// log
		Logger.log(Level.DEBUG, "BranchManagerPortalView: BranchManagerEditChoicesWindow initialized");
		System.out.println("BranchManagerPortalView: BranchManagerEditChoicesWindow initialized");

		// EditRoleWindow
		// ------------------------------------------------------------------------------
		loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/templates/BranchManagerEditRole.fxml"));
		editRoleRoot = loader.load();
		branchManagerEditRoleWindow = loader.getController();
		branchManagerEditRoleWindow.init(editRoleRoot, primaryStage, this);

		// log
		Logger.log(Level.DEBUG, "BranchManagerPortalView: BranchManagerEditChoicesWindow initialized");
		System.out.println("BranchManagerPortalView: BranchManagerEditChoicesWindow initialized");

		// EditStatusWindow
		// ------------------------------------------------------------------------------
		loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/templates/BranchManagerEditStatus.fxml"));
		editStatusRoot = loader.load();
		branchManagerEditStatusWindow = loader.getController();
		branchManagerEditStatusWindow.init(editStatusRoot, primaryStage, this);

		// log
		Logger.log(Level.DEBUG, "BranchManagerPortalView: BranchManagerEditStatusWindow initialized");
		System.out.println("BranchManagerPortalView: BranchManagerEditStatusWindow initialized");
	}
}
