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

public class HRHomePageWindow {

	private Stage primaryStage;
	private Scene scene;
	private HBox homePageHBox;
	private HRPortalView view;

	@FXML
	private Label welcomeLabel;

	public void init(HBox homePageHBox, Stage primaryStage, HRPortalView view) {
		this.homePageHBox = homePageHBox;
		this.primaryStage = primaryStage;
		this.view = view;

	}

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

	@FXML
	public void onLogoutButton(ActionEvent event) {
		// log
		Logger.log(Level.INFO, "HRHomePageWindow: logout button was pressed");
		System.out.println("HRHomePageWindow: logout button was pressed");

		JSONObject json = new JSONObject();
		json.put("command", "logout was pressed");
		view.getComController().handleUserAction(json);
	}

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
