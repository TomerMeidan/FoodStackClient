package javaFXControllers.HR;

import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import clientSide.HRPortalView;
import clientSide.W4C;
import common.Logger;
import common.Message;
import common.Logger.Level;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class HRRegisterEmployerWindow {

	private Stage primaryStage;
	private Scene scene;
	private HBox employerRgstrHBox;
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
	private Button registerButton;

	public void init(HBox employerRgstrHBox, Stage primaryStage, HRPortalView view) {
		this.employerRgstrHBox = employerRgstrHBox;
		this.primaryStage = primaryStage;
		this.view = view;
		registerButtonMap = new HashMap<String, Button>();
	}

	public void showWindow(JSONObject descriptor) {
		// log
		Logger.log(Level.INFO, "HRRegisterEmployerWindow: showing window");
		System.out.println("HRRegisterEmployerWindow: showing window");

		employers = (JSONArray) descriptor.get("employers");

		Platform.runLater(() -> {
			try {
				Scene scene = new Scene(employerRgstrHBox);
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

	private void setLabels() {
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

			nameLabel.setText("Employer name: " + Message.getValue(employer, "employer name"));
			creditLabel.setText("Credit: " + Message.getValue(employer, "credit"));
			phoneNumberLabel.setText("Phone number: " + Message.getValue(employer, "phone number"));
			emailLabel.setText("Email: " + Message.getValue(employer, "email"));
			w4cLabel.setText("W4C: " + w4c);
			balanceLabel.setText("Enter daily balance: ");
		}
	}

	@SuppressWarnings("unchecked")
	@FXML
	public void onRegisterButton(ActionEvent event) {
		JSONObject employer = (JSONObject) employers.get(0);
		String employerName = Message.getValue(employer, "employer name");

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

	public void showPopup(JSONObject descriptor) {
		String msg = Message.getValue(descriptor, "update");

		if (msg.equals("employer has been registered")) {
			registerButton.setDisable(true);
		}

		Platform.runLater(() -> {
			Stage window = new Stage();
			window.initModality(Modality.APPLICATION_MODAL);
			window.setTitle("");
			window.setMinWidth(300);
			window.setMinHeight(20);

			Label label = new Label();
			label.setText(msg);

			VBox layout = new VBox(10);
			layout.getChildren().add(label);
			layout.setAlignment(Pos.CENTER);

			Scene scene = new Scene(layout);
			window.setScene(scene);
			window.showAndWait();

		});
	}

	public void showPopup(String msg) {
		Platform.runLater(() -> {
			Stage window = new Stage();
			window.initModality(Modality.APPLICATION_MODAL);
			window.setTitle("");
			window.setMinWidth(300);
			window.setMinHeight(20);

			Label label = new Label();
			label.setText(msg);

			VBox layout = new VBox(10);
			layout.getChildren().add(label);
			layout.setAlignment(Pos.CENTER);

			Scene scene = new Scene(layout);
			window.setScene(scene);
			window.showAndWait();

		});
	}

	@FXML
	public void onBackButton(ActionEvent event) {
		// log
		Logger.log(Level.DEBUG, "HRRegisterEmployerWindow: back button was pressed");
		System.out.println("HRRegisterEmployerWindow: back button was pressed");

		view.showHRHomePage();
	}
}
