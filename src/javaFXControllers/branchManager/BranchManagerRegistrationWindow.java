package javaFXControllers.branchManager;

import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import clientSide.BranchManagerPortalView;
import common.Logger;
import common.Message;
import common.Logger.Level;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;
import util.W4C;

/**
 * BranchManagerRegistrationWindow
 * 
 * This class is the javaFX controller for BranchManagerRegistration.fxml
 * This class holds primaryStage, scene, registrationRoot, view, selectedCustomer, registeredCustomers, customers variables.
 * JSONObject selectedCustomer - contains selected customer's info.
 * JSONArray customers - Array of customers with their info.
 * JSONArray registeredCustomers - Array of customers which already been registerd.
 * @author Roman Milman
 */
public class BranchManagerRegistrationWindow {

	private Stage primaryStage;
	private Scene scene;
	private HBox registrationRoot;
	private BranchManagerPortalView view;

	private JSONObject selectedCustomer;

	private JSONArray registeredCustomers;

	private JSONArray customers;

	@FXML
	private AnchorPane anchorID;

	@FXML
	private Label nameLabel;

	@FXML
	private Label idLabel;

	@FXML
	private Label w4cLabel;

	@FXML
	private Label usernameLabel;

	@FXML
	private TextArea usernameTextArea;

	@FXML
	private HBox passwordLabel;

	@FXML
	private PasswordField passwordField;

	@FXML
	private CheckBox isBusinessCustomerCheckBox;

	@FXML
	private Label employerNameLabel;

	@FXML
	private TextArea employerNameTextArea;

	@FXML
	private Button registerButton;
	
	@FXML
    private Label mustField1;
	
	@FXML
    private Label mustField2;
	
	@FXML
    private Label statusLabel;

	/**
	 * init
	 * 
	 * This method initializes the needed parameters for this controller.
	 * @param HBox registrationRoot
	 * @param Stage primaryStage
	 * @param BranchManagerPortalView view
	 * @author Roman Milman
	 */
	public void init(HBox registrationRoot, Stage primaryStage, BranchManagerPortalView view) {
		this.registrationRoot = registrationRoot;
		this.primaryStage = primaryStage;
		this.view = view;
	}

	/**
	 * showWindow
	 * 
	 * This method sets customers by the array received from server.
	 * This method calls Platform.runLater() to add javaFX task.
	 * This method builds the scene and sets to primaryStage.
	 * This method announces to server "ready" after showing window.
	 * @param JSONObject descriptor - holds "customers" key to JSONArray.
	 * @author Roman Milman
	 */
	public void showWindow(JSONObject descriptor) {
		// log
		Logger.log(Level.INFO, "BranchManagerRegistrationWindow: showing window");
		System.out.println("BranchManagerRegistrationWindow: showing window");

		customers = (JSONArray) descriptor.get("customers");

		registeredCustomers = new JSONArray();

		Platform.runLater(() -> {
			try {
				Scene scene = new Scene(registrationRoot);
				this.scene = scene;
			} catch (IllegalArgumentException e) {
				// log
				Logger.log(Level.WARNING, "BranchManagerRegistrationWindow: exception in showWindow");
				System.out.println("BranchManagerRegistrationWindow: exception in showWindow");
			}

			clearWindow();
			buildTableView();
			setRules();

			primaryStage.setScene(scene);
			primaryStage.show();

			JSONObject json = new JSONObject();
			json.put("command", "registration window is ready");
			view.ready(json);
		});
	}

	/**
	 * buildTableView
	 * 
	 * This method builds TableView with potential customers to register.
	 * @author Roman Milman
	 */
	@SuppressWarnings("unchecked")
	private void buildTableView() {
		// log
		Logger.log(Level.DEBUG, "BranchManagerRegistrationWindow: building customer table view");
		System.out.println("BranchManagerRegistrationWindow: building customer table view");

		TableView customersTableView = new TableView();

		ObservableList<Map<String, Object>> customerRows = FXCollections.<Map<String, Object>>observableArrayList();

		TableColumn<Map, String> firstNameCol = new TableColumn<>("First name");
		firstNameCol.setCellValueFactory(new MapValueFactory<>("First name"));

		TableColumn<Map, String> lastNameCol = new TableColumn<>("Last name");
		lastNameCol.setCellValueFactory(new MapValueFactory<>("Last name"));

		TableColumn<Map, String> idCol = new TableColumn<>("id");
		idCol.setCellValueFactory(new MapValueFactory<>("id"));

		customersTableView.getColumns().addAll(firstNameCol, lastNameCol, idCol);

		for (int i = 0; i < customers.size(); i++) {
			JSONObject customer = (JSONObject) customers.get(i);

			JSONObject tableRow = new JSONObject();

			String firstName = Message.getValue(customer, "first name");
			String lastName = Message.getValue(customer, "last name");
			String id = Message.getValue(customer, "id");
			String w4c = W4C.createW4C();

			tableRow.put("First name", firstName);
			tableRow.put("Last name", lastName);
			tableRow.put("id", id);
			tableRow.put("w4c", w4c);

			customerRows.add(tableRow);
		}

		customersTableView.getItems().addAll(customerRows);

		anchorID.getChildren().add(customersTableView);

		customersTableView.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {

				try {

					selectedCustomer = (JSONObject) customersTableView.getSelectionModel().getSelectedItem();

					if (selectedCustomer == null)
						return;

					registerButton.setVisible(true);
					isBusinessCustomerCheckBox.setVisible(true);
					isBusinessCustomerCheckBox.setSelected(false);
					usernameLabel.setVisible(true);
					employerNameLabel.setText("");
					employerNameLabel.setVisible(false);
					employerNameTextArea.setText("");
					employerNameTextArea.setVisible(false);
					usernameTextArea.setVisible(true);
					usernameTextArea.setText("");
					passwordLabel.setVisible(true);
					passwordField.setVisible(true);
					passwordField.setText("");
					mustField1.setVisible(true);
					mustField2.setVisible(true);
					statusLabel.setText("Status: ");
					statusLabel.setTextFill(Paint.valueOf("black"));


					String name = Message.getValue(selectedCustomer, "First name") + " "
							+ Message.getValue(selectedCustomer, "Last name");
					String id = "id: " + Message.getValue(selectedCustomer, "id");
					String w4c = Message.getValue(selectedCustomer, "w4c");

					nameLabel.setText(name);
					idLabel.setText(id);
					w4cLabel.setText("W4C: " + w4c);

				} catch (Exception e) {
					Logger.log(Level.INFO,
							"BranchManagerRegistrationWindow: tableView : setOnMouseClicked: Exception was thrown");
					System.out.println(
							"BranchManagerRegistrationWindow: tableView : setOnMouseClicked: Exception was thrown");
				}

			}

		});

	}

	/**
	 * clearWindow
	 * 
	 * This method clears and hides all the needed Widgets on scene.
	 * @author Roman Milman
	 */
	private void clearWindow() {
		nameLabel.setText("");
		idLabel.setText("");
		w4cLabel.setText("");
		anchorID.getChildren().clear();
		isBusinessCustomerCheckBox.setSelected(false);
		isBusinessCustomerCheckBox.setVisible(false);
		employerNameLabel.setVisible(false);
		employerNameLabel.setText("");
		employerNameTextArea.setVisible(false);
		employerNameTextArea.setText("");
		registerButton.setVisible(false);
		usernameLabel.setVisible(false);
		usernameTextArea.setVisible(false);
		passwordLabel.setVisible(false);
		passwordField.setVisible(false);
		mustField1.setVisible(false);
		mustField2.setVisible(false);
		statusLabel.setText("Status: ");
		statusLabel.setTextFill(Paint.valueOf("black"));
	}

	/**
	 * isBusinessCustomerOnAction
	 * 
	 * This method called when 'Event' occurred to 'isBusinessCustomerCheckBox' CheckBox.
	 * This method changes visual properties of Widgets base on business check box.
	 * @param ActionEvent event.
	 * @author Roman Milman
	 */
	@FXML
	void isBusinessCustomerOnAction(ActionEvent event) {
		if (isBusinessCustomerCheckBox.isSelected()) {
			employerNameLabel.setVisible(true);
			employerNameLabel.setText("Enter Employer name:");
			employerNameTextArea.setVisible(true);
		} else {
			employerNameLabel.setVisible(false);
			employerNameTextArea.setVisible(false);
		}
	}

	/**
	 * onBackButton
	 * 
	 * This method called when 'Event' occurred to 'Back' button.
	 * This method calls showBranchManagerHomePage method.
	 * Goes back to home page.
	 * @param ActionEvent event.
	 * @author Roman Milman
	 */
	@FXML
	void onBackButton(ActionEvent event) {
		// log
		Logger.log(Level.INFO, "BranchManagerRegistrationWindow: back button was pressed");
		System.out.println("BranchManagerRegistrationWindow: back button was pressed");

		view.showBranchManagerHomePage();
	}

	/**
	 * onRegisterButton
	 * 
	 * This method called when 'Event' occurred to 'register' button.
	 * This method does some sanity checks on needed Widgets.
	 * This method sends to server an event of 'registration' occurred, with customers info.
	 * @param ActionEvent event.
	 * @author Roman Milman
	 */
	@SuppressWarnings("unchecked")
	@FXML
	void onRegisterButton(ActionEvent event) {

		String name = Message.getValue(selectedCustomer, "First name") + " "
				+ Message.getValue(selectedCustomer, "Last name");
		// log
		System.out.println("BranchManagerRegistrationWindow: " + name + " register button was pressed");
		Logger.log(Level.DEBUG, "BranchManagerRegistrationWindow: \" + name + \" register button was pressed");

		if (isRegistered(Message.getValue(selectedCustomer, "w4c"))) {
			showPopup("This customer has been switched allready");
			return;
		}

		if (!isInputLegal(usernameTextArea, passwordField)) {
			showPopup("Error: username or password invalid");
			return;
		}

		JSONObject json = new JSONObject();
		json.put("command", "customer register was pressed");
		json.put("firstname", Message.getValue(selectedCustomer, "First name"));
		json.put("lastname", Message.getValue(selectedCustomer, "Last name"));
		json.put("id", Message.getValue(selectedCustomer, "id"));
		json.put("username", usernameTextArea.getText());
		json.put("password", passwordField.getText());
		json.put("w4c", Message.getValue(selectedCustomer, "w4c"));
		json.put("branch", view.getBranch());

		if (isBusinessCustomerCheckBox.isSelected()) {
			String employerName = employerNameTextArea.getText();

			if (!isEmployerLegal(employerName)) {
				showPopup("Error: employer name invalid");
				return;
			}

			json.put("register type", "business");
			json.put("employer name", employerName);
		} else
			json.put("register type", "regular");
		view.getComController().handleUserAction(json);

	}

	/**
	 * isInputLegal
	 * 
	 * This method return true if all checks pass, otherwise return false.
	 * @param TextArea usernameTextArea
	 * @param PasswordField passwordField
	 * @return boolean
	 * @author Roman Milman
	 */
	private boolean isInputLegal(TextArea usernameTextArea, PasswordField passwordField) {
		return isUsernameLegal(usernameTextArea.getText()) && isPasswordLegal(passwordField.getText());
	}

	/**
	 * isUsernameLegal
	 * 
	 * This method return true if all sanity checks on username pass.
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
	 * isEmployerLegal
	 * 
	 * This method return true if all sanity checks on employer pass.
	 * @param String employer
	 * @return boolean
	 * @author Roman Milman
	 */
	private boolean isEmployerLegal(String employer) {

		if (employer.length() == 0)
			return false;

		for (int i = 0; i < employer.length(); i++) {
			char letter = employer.charAt(i);
			if (letter < 'A' && letter != '.') {
				return false;
			} else if (letter > 'Z') {
				if (letter < 'a' || letter > 'z')
					return false;
			}
		}
		return true;
	}

	/**
	 * showPopup
	 * 
	 * This method shows pop-up messages.
	 * This method add's registered customers w4c to registeredCustomers, if 'register' was successful.
	 * @param JSONObject descriptor - holds: 'update' key with message from server, 'w4c' key with customers w4c as value.
	 * @author Roman Milman
	 */
	public void showPopup(JSONObject descriptor) {
		String msg = Message.getValue(descriptor, "update");

		if (msg.equals("business customer has been registered")) {
			registeredCustomers.add(Message.getValue(descriptor, "w4c"));
			statusLabel.setTextFill(Paint.valueOf("green"));
		}

		if (msg.equals("regular customer has been registered")) {
			registeredCustomers.add(Message.getValue(descriptor, "w4c"));
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
	 * isRegistered
	 * 
	 * This method goes over registeredCustomers JSONArray.
	 * If this customers w4c wasn't registered already return true, otherwise return false.
	 * @param String w4c
	 * @return boolean
	 * @author Roman Milman
	 */
	private Boolean isRegistered(String w4c) {
		for (int i = 0; i < registeredCustomers.size(); i++) {
			String registeredW4C = (String) registeredCustomers.get(i);
			if (registeredW4C.equals(w4c))
				return true;
		}
		return false;
	}

	/**
	 * setRules
	 * 
	 * This method sets rules to Widgets.
	 * @author Roman Milman
	 */
	private void setRules() {
		employerNameTextArea.setTextFormatter(
				new TextFormatter<String>(change -> change.getControlNewText().length() <= 45 ? change : null));
		usernameTextArea.setTextFormatter(
				new TextFormatter<String>(change -> change.getControlNewText().length() <= 45 ? change : null));
		passwordField.setTextFormatter(
				new TextFormatter<String>(change -> change.getControlNewText().length() <= 45 ? change : null));
	}

}
