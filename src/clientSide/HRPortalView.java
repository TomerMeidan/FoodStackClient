package clientSide;

import java.io.IOException;

import org.json.simple.JSONObject;

import common.Logger;
import common.Message;
import common.Logger.Level;
import javaFXControllers.HR.HRApproveBusinessCustomerWindow;
import javaFXControllers.HR.HRHomePageWindow;
import javaFXControllers.HR.HRRegisterEmployerWindow;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * HRPortalView
 * 
 * This class is the portalView which handles HR behavior.
 * This class implements PortalViewInterface.
 * This class holds all the javaFX controllers which HR uses.
 * Holds ComController to send messages.
 * Holds JSONObject personalInfo,which holds: portalType,FirstName,LastName,branch.
 * @author Roman Milman
 */
public class HRPortalView implements PortalViewInterface {

	private ComController com;

	private JSONObject personalInfo;

	// ----- GUI variables

	private Stage primaryStage;
	private HRHomePageWindow hrHomePageWindow;
	private HRApproveBusinessCustomerWindow hrApproveBusinessCustomerWindow;
	private HRRegisterEmployerWindow hrRegisterEmployerWindow;
	private HBox homePageRoot, approveRoot, registerEmployerRoot;

	public HRPortalView(Stage primaryStage, ComController com) {
		this.primaryStage = primaryStage;
		this.com = com;
	}

	/**
	 * init
	 * 
	 * This method initializing all the javaFX controllers.
	 * Sets personalInfo to JSONObject descriptor, received from server.
	 * Calls showWindow to show HR's home page.
	 * @param JSONObject descriptor
	 * @author Roman Milman
	 */
	@Override
	public void init(JSONObject descriptor) {

		try {
			// HRHomePage
			// ------------------------------------------------------------------------------
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/templates/HRHomePageTemplate.fxml"));
			homePageRoot = loader.load();
			hrHomePageWindow = loader.getController();
			hrHomePageWindow.init(homePageRoot, primaryStage, this);

			// log
			Logger.log(Level.DEBUG, "HRPortalView: HRHomePage initialized");
			System.out.println("HRPortalView: HRHomePage initialized");
			// -------------------------------------------------------------------------------

			// HRApproveBusinessCustomerWindow
			// ------------------------------------------------------------------------------
			loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/templates/HRApproveBusinessClient.fxml"));
			approveRoot = loader.load();
			hrApproveBusinessCustomerWindow = loader.getController();
			hrApproveBusinessCustomerWindow.init(approveRoot, primaryStage, this);

			// log
			Logger.log(Level.DEBUG, "HRPortalView: HRApproveBusinessCustomerWindow initialized");
			System.out.println("HRPortalView: HRApproveBusinessCustomerWindow initialized");
			// -------------------------------------------------------------------------------

			// HRRegisterEmployerWindow
			// ------------------------------------------------------------------------------
			loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/templates/HREmployerRegistrationTemplate.fxml"));
			registerEmployerRoot = loader.load();
			hrRegisterEmployerWindow = loader.getController();
			hrRegisterEmployerWindow.init(registerEmployerRoot, primaryStage, this);

			// log
			Logger.log(Level.DEBUG, "HRPortalView: HRRegisterEmployerWindow initialized");
			System.out.println("HRPortalView: HRRegisterEmployerWindow initialized");
			// -------------------------------------------------------------------------------

			// displays branchManagerHomePage window
			hrHomePageWindow.showWindow(descriptor);

			personalInfo = descriptor;

		} catch (IOException e) {
			Logger.log(Level.WARNING, "HRPortalView: init: IOException");
			System.out.println("HRPortalView: init: IOException");
		}

	}

	/**
	 * handleMsg
	 * 
	 * This method handles HR's messages received from server.
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
	 * This method handles HR's update messages received from server.
	 * @param descriptor - read's 'update' key to define what to do.
	 * @author Roman Milman
	 */
	private void handleUpdateCommand(JSONObject descriptor) {
		String updateType = Message.getValue(descriptor, "update");

		switch (updateType) {

		case "show approve business client window":
			hrApproveBusinessCustomerWindow.showWindow(descriptor);
			break;
		case "customer has been activated":
			hrApproveBusinessCustomerWindow.disableApproveButton(descriptor);
			break;
		case "show employer registration window":
			hrRegisterEmployerWindow.showWindow(descriptor);
			break;
		case "employer has been registered":
			hrRegisterEmployerWindow.showPopup(descriptor);
			break;
		case "could not registered employer":
			hrRegisterEmployerWindow.showPopup(descriptor);
			break;

		default:
			// log
			Logger.log(Level.DEBUG, "BranchManagerPortalView: handleUpdateCommand: unknown msg");
			System.out.println("BranchManagerPortalView: handleUpdateCommand: unknown msg");
			break;
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
	 * showHRHomePage
	 * 
	 * This method shows HRs home page.
	 * @author Roman Milman
	 */
	public void showHRHomePage() {
		hrHomePageWindow.showWindow(personalInfo);

	}

	/**
	 * getEmployerID
	 * 
	 * This method returns this HRs employerID from personalInfo
	 * @return String
	 * @author Roman Milman
	 */
	public String getEmployerID() {
		return Message.getValue(personalInfo, "employerID");
	}
	
	/**
	 * getBranch
	 * 
	 * This method returns this HRs branch from personalInfo
	 * @return String
	 * @author Roman Milman
	 */
	public String getBranch() {
		return Message.getValue(personalInfo, "branch");
	}

}
