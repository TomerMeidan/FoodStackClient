package javaFXControllers.HR;

import org.json.simple.JSONObject;

import clientSide.HRPortalView;
import common.Logger;
import common.Message;
import common.Logger.Level;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * HRHomePageWindow
 * 
 * This class is the javaFX controller for HRHomePageTemplate.fxml
 * This class holds primaryStage, scene, homePageRoot, view variables.
 * @author Roman Milman
 */
public class HRHomePageWindow {

	private Stage primaryStage;
	private Scene scene;
	private HBox homePageHBox;
	private HRPortalView view;

	@FXML
	private Label welcomeLabel;

	/**
	 * init
	 * 
	 * This method initializes the needed parameters for this controller.
	 * @param HBox homePageRoot
	 * @param Stage primaryStage
	 * @param BranchManagerPortalView view
	 * @author Roman Milman
	 */
	public void init(HBox homePageHBox, Stage primaryStage, HRPortalView view) {
		this.homePageHBox = homePageHBox;
		this.primaryStage = primaryStage;
		this.view = view;

	}

	/**
	 * showWindow
	 * 
	 * This method calls Platform.runLater() to add javaFX task.
	 * This method builds the scene and sets to primaryStage.
	 * This method announces to server "ready" after showing window.
	 * @param JSONObject descriptor - has 'FirstName','LastName' keys with accordingly values.
	 * @author Roman Milman
	 */
	public void showWindow(JSONObject descriptor) {
		// log
		Logger.log(Level.INFO, "HRHomePageWindow: showing window");
		System.out.println("HRHomePageWindow: showing window");

		Platform.runLater(() -> {
			try {
				Scene scene = new Scene(homePageHBox);
				this.scene = scene;
			} catch (IllegalArgumentException e) {
				// log
				Logger.log(Level.WARNING, "HRHomePageWindow: exception in showWindow");
				System.out.println("HRHomePageWindow: exception in showWindow");
			}
			welcomeLabel.setText("Welcome, " + Message.getValue(descriptor, "FirstName") + " "
					+ Message.getValue(descriptor, "LastName"));

			primaryStage.setScene(scene);
			primaryStage.show();

			JSONObject json = new JSONObject();
			json.put("command", "home page is ready");
			json.put("home page", "HR");
			view.ready(json);
		});
	}

	/**
	 * onLogoutButton
	 * 
	 * This method called when 'Event' occurred to 'Logout' button.
	 * This method sends to server event of 'Logout' happened.
	 * @param ActionEvent event.
	 * @author Roman Milman
	 */
	@FXML
	public void onLogoutButton(ActionEvent event) {
		// log
		Logger.log(Level.INFO, "HRHomePageWindow: logout button was pressed");
		System.out.println("HRHomePageWindow: logout button was pressed");

		JSONObject json = new JSONObject();
		json.put("command", "logout was pressed");
		view.getComController().handleUserAction(json);
	}

	/**
	 * onApproveBusinessAccount
	 * 
	 * This method called when 'Event' occurred to 'Approve business' button.
	 * This method sends to server event of 'approve business' happened, with 'employerID'.
	 * @param ActionEvent event.
	 * @author Roman Milman
	 */
	@SuppressWarnings("unchecked")
	@FXML
	void onApproveBusinessAccount(ActionEvent event) {
		// log
		Logger.log(Level.INFO, "HRHomePageWindow: approve business client button was pressed");
		System.out.println("HRHomePageWindow: approve business client button was pressed");

		JSONObject json = new JSONObject();
		json.put("command", "approve business client was pressed");
		json.put("employerID", view.getEmployerID());
		view.getComController().handleUserAction(json);

	}

	/**
	 * onRegisterEmployer
	 * 
	 * This method called when 'Event' occurred to 'register employer' button.
	 * This method sends to server event of 'register employer' happened.
	 * @param ActionEvent event.
	 * @author Roman Milman
	 */
	@FXML
	void onRegisterEmployer(ActionEvent event) {
		// log
		Logger.log(Level.INFO, "HRHomePageWindow: register employer button was pressed");
		System.out.println("HRHomePageWindow: register employer button was pressed");

		JSONObject json = new JSONObject();
		json.put("command", "register employer was pressed");
		view.getComController().handleUserAction(json);
	}

}
