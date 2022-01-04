package javaFXControllers.HR;

import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import clientSide.HRPortalView;
import common.Logger;
import common.Logger.Level;
import common.Message;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import util.W4C;

/**
 * HRRegisterEmployerWindow
 * 
 * This class is the javaFX controller for HREmployerRegistrationTemplate.fxml
 * This class holds primaryStage, scene, registerEmployerRoot, view, registerButtonMap, employers.
 * JSONArray employers - Array of employers with their info.
 * @author Roman Milman
 */
public class HRRegisterEmployerWindow {

	private Stage primaryStage;
	private Scene scene;
	private HBox registerEmployerRoot;
	private HRPortalView view;
	private HashMap<String, Button> registerButtonMap;

	private JSONArray employers;
	private String w4c;

	@FXML
	private VBox registrationListVBox;

	@FXML
	private Label nameLabel;

	@FXML
	private Label creditLabel;

	@FXML
	private Label phoneNumberLabel;

	@FXML
	private Label emailLabel;

	@FXML
	private Label w4cLabel;

	@FXML
	private Label balanceLabel;

	@FXML
	private TextArea balanceTextArea;
	
	@FXML
    private Label statusLabel;

	@FXML
	private Button registerButton;

	/**
	 * init
	 * 
	 * This method initializes the needed parameters for this controller.
	 * @param HBox registerEmployerRoot
	 * @param Stage primaryStage
	 * @param BranchManagerPortalView view
	 * @author Roman Milman
	 */
	public void init(HBox registerEmployerRoot, Stage primaryStage, HRPortalView view) {
		this.registerEmployerRoot = registerEmployerRoot;
		this.primaryStage = primaryStage;
		this.view = view;
		registerButtonMap = new HashMap<String, Button>();
	}

	/**
	 * showWindow
	 * 
	 * This method sets employers by the array received from server.
	 * This method calls Platform.runLater() to add javaFX task.
	 * This method builds the scene and sets to primaryStage.
	 * This method announces to server "ready" after showing window.
	 * @param JSONObject descriptor - holds "employers" key to JSONArray.
	 * @author Roman Milman
	 */
	public void showWindow(JSONObject descriptor) {
		// log
		Logger.log(Level.INFO, "HRRegisterEmployerWindow: showing window");
		System.out.println("HRRegisterEmployerWindow: showing window");

		employers = (JSONArray) descriptor.get("employers");

		Platform.runLater(() -> {
			try {
				Scene scene = new Scene(registerEmployerRoot);
				this.scene = scene;
			} catch (IllegalArgumentException e) {
				// log
				Logger.log(Level.WARNING, "HRRegisterEmployerWindow: exception in showWindow");
				System.out.println("HRRegisterEmployerWindow: exception in showWindow");
			}

			setLabels();
			primaryStage.setScene(scene);
			primaryStage.show();

			JSONObject json = new JSONObject();
			json.put("command", "employer registration is ready");
			view.ready(json);
		});
	}

	/**
	 * setLabels
	 * 
	 * This method sets Labels, and creates new W4C code.
	 * @author Roman Milman
	 */
	private void setLabels() {
		
		statusLabel.setText("Status: ");
		statusLabel.setTextFill(Paint.valueOf("black"));
		
		if (employers.size() == 0) {
			registerButton.setDisable(true);

			nameLabel.setText("Employer name: ");
			creditLabel.setText("Credit: ");
			phoneNumberLabel.setText("Phone number: ");
			emailLabel.setText("Email: ");
			w4cLabel.setText("W4C: ");
			balanceLabel.setText("Enter daily balance: ");
		} else {
			JSONObject employer = (JSONObject) employers.get(0);

			registerButton.setDisable(false);

			w4c = W4C.createW4C();

			nameLabel.setText("Employer name: " + Message.getValueString(employer, "employer name"));
			creditLabel.setText("Credit: " + Message.getValueString(employer, "credit"));
			phoneNumberLabel.setText("Phone number: " + Message.getValueString(employer, "phone number"));
			emailLabel.setText("Email: " + Message.getValueString(employer, "email"));
			w4cLabel.setText("W4C: " + w4c);
			balanceLabel.setText("Enter daily balance: ");
		}
	}

	/**
	 * onRegisterButton
	 * 
	 * This method called when 'Event' occurred to 'register' button.
	 * This method does some sanity checks on needed Widgets.
	 * This method sends to server an event of 'registration' occurred, with employers info.
	 * @param ActionEvent event.
	 * @author Roman Milman
	 */
	@SuppressWarnings("unchecked")
	@FXML
	public void onRegisterButton(ActionEvent event) {
		JSONObject employer = (JSONObject) employers.get(0);
		String employerName = Message.getValueString(employer, "employer name");

		String balance = balanceTextArea.getText();

		if (!isBalanceValid(balance)) {
			showPopup("Illegal balance!");
			return;
		}

		System.out.println("BranchManagerSupplierRgstrWindow: " + employerName + " register button was pressed");

		JSONObject json = new JSONObject();
		json.put("command", "supplier register was pressed");
		json.put("employer name", employerName);
		json.put("w4c", w4c);
		json.put("balance", balance);
		view.getComController().handleUserAction(json);
	}

	/**
	 * isBalanceValid
	 * 
	 * This method return true if all sanity checks on balance pass.
	 * @param String balance
	 * @return boolean
	 * @author Roman Milman
	 */
	private boolean isBalanceValid(String balance) {
		if (balance.equals(""))
			return false;

		for (int i = 0; i < balance.length(); i++) {
			if (balance.charAt(i) < '0' || balance.charAt(i) > '9') {
				return false;
			}
		}
		return true;
	}

	/**
	 * showPopup
	 * 
	 * This method shows pop-up messages.
	 * This method disables registerButton, if 'register' was successful.
	 * @param JSONObject descriptor - holds: 'update' key with message from server.
	 * @author Roman Milman
	 */
	public void showPopup(JSONObject descriptor) {
		String msg = Message.getValueString(descriptor, "update");

		if (msg.equals("employer has been registered")) {
			registerButton.setDisable(true);
			statusLabel.setTextFill(Paint.valueOf("green"));
		}

		Platform.runLater(() -> {
			statusLabel.setText("Status: " + msg);
		});
	}

	/**
	 * showPopup
	 * 
	 * This method shows pop-up messages.
	 * @param String msg - contains the message we will display in the pop-up.
	 * @author Roman Milman
	 */
	public void showPopup(String msg) {
		Platform.runLater(() -> {
			statusLabel.setText("Status: " + msg);
			statusLabel.setTextFill(Paint.valueOf("red"));
		});
	}

	/**
	 * onBackButton
	 * 
	 * This method called when 'Event' occurred to 'Back' button.
	 * This method calls showHRHomePage method.
	 * Goes back to home page.
	 * @param ActionEvent event.
	 * @author Roman Milman
	 */
	@FXML
	public void onBackButton(ActionEvent event) {
		// log
		Logger.log(Level.DEBUG, "HRRegisterEmployerWindow: back button was pressed");
		System.out.println("HRRegisterEmployerWindow: back button was pressed");

		view.showHRHomePage();
	}
}
