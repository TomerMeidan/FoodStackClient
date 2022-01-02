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

	@Override
	public void ready(JSONObject json) {
		com.handleUserAction(json);
	}

	@Override
	public ComController getComController() {
		return com;
	}

	public void showHRHomePage() {
		hrHomePageWindow.showWindow(personalInfo);

	}

	public Object getEmployerID() {
		return Message.getValue(personalInfo, "employerID");
	}

	public Object getBranch() {
		return Message.getValue(personalInfo, "branch");
	}

}
