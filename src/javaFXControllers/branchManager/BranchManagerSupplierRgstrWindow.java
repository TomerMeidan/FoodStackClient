package javaFXControllers.branchManager;

import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import clientSide.BranchManagerPortalView;
import common.Logger;
import common.Message;
import common.Logger.Level;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * BranchManagerSupplierRgstrWindow
 * 
 * This class is the javaFX controller for
 * BranchManagerSupplierRegistrationTemplate.fxml This class holds primaryStage,
 * scene, supplierRgstrRoot, view, registerButtonMap, suppliers variables.
 * JSONArray suppliers - Array of suppliers with their info. HashMap<String,
 * Button> registerButtonMap - Hash map of 'register' buttons.
 * 
 * @author Roman Milman
 */
public class BranchManagerSupplierRgstrWindow {
	private Stage primaryStage;
	private Scene scene;
	private HBox supplierRgstrRoot;
	private BranchManagerPortalView view;
	private HashMap<String, Button> registerButtonMap;

	private JSONArray suppliers;

	@FXML
	private VBox registrationListVBox;

	@FXML
	private Label statusLabel;

	/**
	 * init
	 * 
	 * This method initializes the needed parameters for this controller.
	 * 
	 * @param HBox                    supplierRgstrRoot
	 * @param Stage                   primaryStage
	 * @param BranchManagerPortalView view
	 * @author Roman Milman
	 */
	public void init(HBox supplierRgstrRoot, Stage primaryStage, BranchManagerPortalView view) {
		this.supplierRgstrRoot = supplierRgstrRoot;
		this.primaryStage = primaryStage;
		this.view = view;
		registerButtonMap = new HashMap<String, Button>();
	}

	/**
	 * showWindow
	 * 
	 * This method sets suppliers by the array received from server. This method
	 * calls Platform.runLater() to add javaFX task. This method builds the scene
	 * and sets to primaryStage. This method announces to server "ready" after
	 * showing window.
	 * 
	 * @param JSONObject descriptor - holds "suppliers" key to JSONArray.
	 * @author Roman Milman
	 */
	@SuppressWarnings("unchecked")
	public void showWindow(JSONObject descriptor) {
		// log
		Logger.log(Level.INFO, "BranchManagerSupplierRgstrWindow: showing window");
		System.out.println("BranchManagerSupplierRgstrWindow: showing window");

		suppliers = (JSONArray) descriptor.get("suppliers");

		Platform.runLater(() -> {
			try {
				Scene scene = new Scene(supplierRgstrRoot);
				this.scene = scene;
			} catch (IllegalArgumentException e) {
				// log
				Logger.log(Level.WARNING, "BranchManagerSupplierRgstrWindow: exception in showWindow");
				System.out.println("BranchManagerSupplierRgstrWindow: exception in showWindow");
			}

			buildEmployersScrollPane();
			primaryStage.setScene(scene);
			primaryStage.show();

			JSONObject json = new JSONObject();
			json.put("command", "supplier registration window is ready");
			view.ready(json);
		});
	}

	/**
	 * buildEmployersScrollPane
	 * 
	 * This method builds ScrollPane with potential suppliers to register.
	 * 
	 * @author Roman Milman
	 */
	private void buildEmployersScrollPane() {
		registrationListVBox.getChildren().clear();
		registerButtonMap.clear();
		statusLabel.setText("Status: ");
		statusLabel.setTextFill(Paint.valueOf("black"));

		for (int i = 0; i < suppliers.size(); i++) {
			JSONObject supplier = (JSONObject) suppliers.get(i);

			VBox rowVBox = buildSupplierRow(supplier);

			registrationListVBox.getChildren().add(rowVBox);
		}
	}

	/**
	 * buildSupplierRow
	 * 
	 * This method builds VBox row with supplier info.
	 * 
	 * @param JSONObject supplier - holds 'first name','last name','supplier
	 *                   name','phone number','email' keys for accordingly values.
	 * @return VBox
	 * @author Roman Milman
	 */
	private VBox buildSupplierRow(JSONObject supplier) {

		String firstName = Message.getValue(supplier, "first name");
		String lastName = Message.getValue(supplier, "last name");
		String supplierName = Message.getValue(supplier, "supplier name");
		String phoneNumber = Message.getValue(supplier, "phone number");
		String email = Message.getValue(supplier, "email");

		VBox rowVBox = new VBox();
		HBox rowHBox = new HBox();

		VBox personalInfoVBox = new VBox();
		VBox newPersonalInformationVBox = new VBox();
		Label firstNameLabel = new Label("First Name: " + firstName);
		Label lastNameLabel = new Label("Last Name: " + lastName);
		Label supplierNameLabel = new Label("Supplier Name: " + supplierName);
		Label phoneNumberLabel = new Label("phone number: " + phoneNumber);
		Label emailLabel = new Label("email: " + email);

		firstNameLabel.setFont(Font.font("verdana", FontPosture.REGULAR, 12));
		lastNameLabel.setFont(Font.font("verdana", FontPosture.REGULAR, 12));
		supplierNameLabel.setFont(Font.font("verdana", FontPosture.REGULAR, 12));
		phoneNumberLabel.setFont(Font.font("verdana", FontPosture.REGULAR, 12));
		emailLabel.setFont(Font.font("verdana", FontPosture.REGULAR, 12));

		HBox usernameHBox = new HBox();
		HBox passwordHBox = new HBox();
		HBox deliveryTypesHBox = new HBox();

		Label usernameLabel = new Label("Username:");
		Label mustField1 = new Label("*");
		mustField1.setTextFill(Paint.valueOf("red"));
		TextArea usernameTextArea = new TextArea();

		usernameLabel.setFont(Font.font("verdana", FontPosture.REGULAR, 12));
		usernameTextArea.setFont(Font.font("verdana", FontPosture.REGULAR, 12));

		usernameTextArea.setMinSize(0, 10);
		usernameTextArea.setPrefSize(100, 25);

		Label passwordLabel = new Label("Password:");
		Label mustField2 = new Label("*");
		mustField2.setTextFill(Paint.valueOf("red"));
		PasswordField passwordField = new PasswordField();
		passwordField.setPrefHeight(10);
		passwordField.setPrefWidth(100);

		passwordLabel.setFont(Font.font("verdana", FontPosture.REGULAR, 12));

		Label deliveryTypesLabel = new Label("delivery types:");
		deliveryTypesLabel.setUnderline(true);
		CheckBox selfCheck = new CheckBox();
		CheckBox bikeCheck = new CheckBox();
		CheckBox robotCheck = new CheckBox();

		deliveryTypesLabel.setFont(Font.font("verdana", FontPosture.REGULAR, 12));

		usernameHBox.getChildren().addAll(usernameLabel, mustField1);
		usernameHBox.getChildren().add(usernameTextArea);
		usernameHBox.setSpacing(5);

		passwordHBox.getChildren().addAll(passwordLabel, mustField2);
		passwordHBox.getChildren().add(passwordField);
		passwordHBox.setSpacing(5);
		passwordHBox.setMargin(passwordField, new Insets(0, 0, 0, 3));

		deliveryTypesHBox.getChildren().addAll(deliveryTypesLabel, new Label("Self pick up:"), selfCheck,
				new Label("Bike delivery:"), bikeCheck, new Label("Robot delivery:"), robotCheck);
		deliveryTypesHBox.setSpacing(5);

		newPersonalInformationVBox.getChildren().addAll(usernameHBox, passwordHBox, deliveryTypesHBox);
		newPersonalInformationVBox.setSpacing(5);

		rowHBox.getChildren().add(personalInfoVBox);
		rowHBox.getChildren().add(newPersonalInformationVBox);
		Pane whitespacePane = new Pane();
		rowHBox.setHgrow(whitespacePane, Priority.ALWAYS);
		rowHBox.setSpacing(50);

		personalInfoVBox.getChildren().addAll(firstNameLabel, lastNameLabel, supplierNameLabel, phoneNumberLabel,
				emailLabel);

		Button registerButton = new Button("Register");
		registerButton.setStyle("-fx-background-color: #F24444; -fx-text-fill: white");
		registerButton.setMaxWidth(Double.MAX_VALUE);
		registerButton.setMaxHeight(50);
		registerButton.setOnAction(new EventHandler<ActionEvent>() {
			@SuppressWarnings("unchecked")
			@Override
			public void handle(ActionEvent event) {
				System.out.println("BranchManagerSupplierRgstrWindow: " + firstName + " " + lastName
						+ " register button was pressed");

				if (!isInputLegal(usernameTextArea, passwordField)) {
					showPopup("Error: username or password invalid");
					return;
				}

				JSONObject json = new JSONObject();
				json.put("command", "supplier register was pressed");
				json.put("first name", firstName);
				json.put("last name", lastName);
				json.put("username", usernameTextArea.getText());
				json.put("password", passwordField.getText());
				json.put("register type", "supplier");
				json.put("delivery types", getDeliveryTypes(selfCheck, bikeCheck, robotCheck));
				json.put("supplier name", supplierName);
				view.getComController().handleUserAction(json);
			}
		});
		registerButtonMap.put(supplierName, registerButton);

		rowHBox.getChildren().add(registerButton);
		rowHBox.setMargin(registerButton, new Insets(5, 0, 0, 0));

		Separator separator = new Separator();

		rowVBox.getChildren().add(rowHBox);
		rowVBox.getChildren().add(separator);

		return rowVBox;
	}

	/**
	 * isInputLegal
	 * 
	 * This method return true if all checks pass, otherwise return false.
	 * 
	 * @param TextArea      usernameTextArea
	 * @param PasswordField passwordField
	 * @return boolean
	 * @author Roman Milman
	 */
	private boolean isInputLegal(TextArea usernameTextArea, PasswordField passwordField) {
		return isUsernameLegal(usernameTextArea.getText()) && isPasswordLegal(passwordField.getText());
	}

	/**
	 * onBackButton
	 * 
	 * This method called when 'Event' occurred to 'Back' button. This method calls
	 * showBranchManagerHomePage method. Goes back to home page.
	 * 
	 * @param ActionEvent event.
	 * @author Roman Milman
	 */
	@FXML
	public void onBackButton(ActionEvent event) {
		// log
		Logger.log(Level.INFO, "BranchManagerSupplierRgstrWindow: back button was pressed");
		System.out.println("BranchManagerSupplierRgstrWindow: back button was pressed");

		view.showBranchManagerHomePage();
	}

	/**
	 * showPopup
	 * 
	 * This method shows pop-up messages.
	 * 
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
	 * showPopup
	 * 
	 * This method shows pop-up messages. This method disables 'register' button, if
	 * 'register' was successful.
	 * 
	 * @param JSONObject descriptor - holds: 'update' key with message from server,
	 *                   'supplier name' key with customers name as value.
	 * @author Roman Milman
	 */
	public void showPopup(JSONObject descriptor) {
		String msg = Message.getValue(descriptor, "update");

		if (msg.equals("supplier has been registered")) {
			registerButtonMap.get(Message.getValue(descriptor, "supplier name")).setDisable(true);
			statusLabel.setTextFill(Paint.valueOf("green"));
		}

		Platform.runLater(() -> {
			statusLabel.setText("Status: " + msg);
			statusLabel.setTextFill(Paint.valueOf("black"));
		});
	}

	/**
	 * isUsernameLegal
	 * 
	 * This method return true if all sanity checks on username pass.
	 * 
	 * @param String username
	 * @return boolean
	 * @author Roman Milman
	 */
	private boolean isUsernameLegal(String username) {

		if (username.length() == 0)
			return false;

		for (int i = 0; i < username.length(); i++) {
			char letter = username.charAt(i);
			if (letter < 'A') {
				if (letter < '0' || letter > '9')
					return false;
			} else if (letter > 'Z') {
				if (letter < 'a' || letter > 'z')
					return false;
			}
		}
		return true;
	}

	/**
	 * isPasswordLegal
	 * 
	 * This method return true if all sanity checks on password pass.
	 * 
	 * @param String password
	 * @return boolean
	 * @author Roman Milman
	 */
	private boolean isPasswordLegal(String password) {

		if (password.length() == 0)
			return false;

		for (int i = 0; i < password.length(); i++) {
			char letter = password.charAt(i);
			if (letter < '!' || letter > '~')
				return false;
		}
		return true;
	}

	/**
	 * getDeliveryTypes
	 * 
	 * This method return String accordingly to CheckBox values. example : selfCheck
	 * is selected, bikeCheck isn't selected, robotCheck is selected expected String
	 * : 'YNY'
	 * 
	 * @param CheckBox selfCheck
	 * @param CheckBox bikeCheck
	 * @param CheckBox robotCheck
	 * @return String
	 * @author Roman Milman
	 */
	private String getDeliveryTypes(CheckBox selfCheck, CheckBox bikeCheck, CheckBox robotCheck) {
		String deliveryType = "";

		if (selfCheck.isSelected())
			deliveryType += "Y";
		else
			deliveryType += "N";

		if (bikeCheck.isSelected())
			deliveryType += "Y";
		else
			deliveryType += "N";

		if (robotCheck.isSelected())
			deliveryType += "Y";
		else
			deliveryType += "N";

		return deliveryType;

	}
}
