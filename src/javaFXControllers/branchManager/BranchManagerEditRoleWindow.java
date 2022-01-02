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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * BranchManagerEditRoleWindow
 * 
 * This class is the javaFX controller for BranchManagerEditRole.fxml
 * This class holds primaryStage, scene, editRoleRoot, view, customers, selectedCustomer, switchedCustomers variables.
 * JSONObject selectedCustomer - contains selected customer's info.
 * JSONArray customers - Array of customers with their info.
 * JSONArray switchedCustomers - Array of customers which already been switched.
 * @author Roman Milman
 */
public class BranchManagerEditRoleWindow {

	private Stage primaryStage;
	private Scene scene;
	private HBox editRoleRoot;
	private BranchManagerPortalView view;

	private JSONObject selectedCustomer;

	private JSONArray customers;
	
	private JSONArray switchedCustomers;

	@FXML
	private AnchorPane anchorID;

	@FXML
	private Label nameLabel;

	@FXML
	private Label switchFromLabel;

	@FXML
	private Label switchToLabel;

	@FXML
	private Label employerLabel;

	@FXML
	private TextArea employerTextArea;

	@FXML
	private Button switchButton;
	
	@FXML
    private Label statusLabel;

	/**
	 * init
	 * 
	 * This method initializes the needed parameters for this controller.
	 * @param HBox editRoleRoot
	 * @param Stage primaryStage
	 * @param BranchManagerPortalView view
	 * @author Roman Milman
	 */
	public void init(HBox editRoleRoot, Stage primaryStage, BranchManagerPortalView view) {
		this.editRoleRoot = editRoleRoot;
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
		Logger.log(Level.INFO, "BranchManagerEditRoleWindow: showing window");
		System.out.println("BranchManagerEditRoleWindow: showing window");

		customers = (JSONArray) descriptor.get("customers");
		
		switchedCustomers = new JSONArray();

		Platform.runLater(() -> {
			try {
				Scene scene = new Scene(editRoleRoot);
				this.scene = scene;
			} catch (IllegalArgumentException e) {
				// log
				Logger.log(Level.WARNING, "BranchManagerEditRoleWindow: exception in showWindow");
				System.out.println("BranchManagerEditRoleWindow: exception in showWindow");
			}

			clearWindow();
			buildTableView();
			primaryStage.setScene(scene);
			primaryStage.show();

			JSONObject json = new JSONObject();
			json.put("command", "edit roles is ready");
			view.ready(json);
		});
	}

	/**
	 * buildTableView
	 * 
	 * This method builds TableView with potential customers to edit their role.
	 * @author Roman Milman
	 */
	@SuppressWarnings("unchecked")
	private void buildTableView() {
		// log
		Logger.log(Level.DEBUG, "BranchManagerEditRoleWindow: building customer table view");
		System.out.println("BranchManagerEditRoleWindow: building customer table view");

		TableView customersTableView = new TableView();

		ObservableList<Map<String, Object>> customerRows = FXCollections.<Map<String, Object>>observableArrayList();

		TableColumn<Map, String> firstNameCol = new TableColumn<>("First name");
		firstNameCol.setCellValueFactory(new MapValueFactory<>("First name"));

		TableColumn<Map, String> lastNameCol = new TableColumn<>("Last name");
		lastNameCol.setCellValueFactory(new MapValueFactory<>("Last name"));

		TableColumn<Map, String> idCol = new TableColumn<>("id");
		idCol.setCellValueFactory(new MapValueFactory<>("id"));

		TableColumn<Map, String> roleCol = new TableColumn<>("Role");
		roleCol.setCellValueFactory(new MapValueFactory<>("Role"));

		TableColumn<Map, String> employerNameCol = new TableColumn<>("employer name");
		employerNameCol.setCellValueFactory(new MapValueFactory<>("employer name"));

		customersTableView.getColumns().addAll(firstNameCol, lastNameCol, idCol, roleCol, employerNameCol);

		for (int i = 0; i < customers.size(); i++) {
			JSONObject customer = (JSONObject) customers.get(i);

			JSONObject tableRow = new JSONObject();

			String firstName = Message.getValue(customer, "first name");
			String lastName = Message.getValue(customer, "last name");
			String role = Message.getValue(customer, "role");
			String employerName = Message.getValue(customer, "employer name");
			String id = Message.getValue(customer, "id");

			tableRow.put("First name", firstName);
			tableRow.put("Last name", lastName);
			tableRow.put("id", id);
			tableRow.put("Role", role);
			tableRow.put("employer name", employerName);

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
					
					statusLabel.setText("Status: ");
					statusLabel.setTextFill(Paint.valueOf("black"));
					
					switchButton.setVisible(true);
					
					String role = Message.getValue(selectedCustomer, "Role");

					nameLabel.setText(Message.getValue(selectedCustomer, "First name"));

					if (role.equals("Customer")) {
						switchFromLabel.setText("Switch from: Customer");
						switchToLabel.setText("Switch to: Business Customer");
						employerLabel.setVisible(true);
						employerTextArea.setVisible(true);
						employerTextArea.clear();
					} else {
						switchFromLabel.setText("Switch from: Business Customer");
						switchToLabel.setText("Switch to: Customer");
						employerLabel.setVisible(false);
						employerTextArea.setVisible(false);
					}

				} catch (Exception e) {
					Logger.log(Level.INFO,
							"BranchManagerEditRoleWindow: tableView : setOnMouseClicked: Exception was thrown");
					System.out.println(
							"BranchManagerEditRoleWindow: tableView : setOnMouseClicked: Exception was thrown");
				}

			}

		});

	}

	/**
	 * onBackButton
	 * 
	 * This method called when 'Event' occurred to 'Back' button.
	 * This method calls showBranchManagerEditChoice method.
	 * Goes back to edit choices window.
	 * @param ActionEvent event.
	 * @author Roman Milman
	 */
	@FXML
	void onBackButton(ActionEvent event) {
		// log
		Logger.log(Level.DEBUG, "BranchManagerEditRoleWindow: back button was pressed");
		System.out.println("BranchManagerEditRoleWindow: back button was pressed");

		view.showBranchManagerEditChoice();
	}

	/**
	 * clearWindow
	 * 
	 * This method clears all the needed Widgets on scene.
	 * @author Roman Milman
	 */
	private void clearWindow() {
		switchFromLabel.setText("");
		switchToLabel.setText("");
		anchorID.getChildren().clear();
		nameLabel.setText("");
		switchButton.setVisible(false);
		employerTextArea.setVisible(false);
		employerLabel.setVisible(false);
		statusLabel.setText("Status: ");
		statusLabel.setTextFill(Paint.valueOf("black"));
	}
	
	/**
	 * isSwitched
	 * 
	 * This method goes over switchedCustomers JSONArray.
	 * If this customers id wasn't switch already return true, otherwise return false.
	 * @param String id - customers id.
	 * @return boolean
	 * @author Roman Milman
	 */
	private Boolean isSwitched(String id) {
		for (int i = 0; i < switchedCustomers.size(); i++) {
			String switchedID = (String) switchedCustomers.get(i);
			if(switchedID.equals(id))
				return true;
		}
		return false;
	}

	/**
	 * onSwitch
	 * 
	 * This method called when 'Event' occurred to 'Switch' button.
	 * This method checks if this customer is able to be switched.
	 * This method does some sanity checks on input in needed Widgets.
	 * This method sends to server a 'Switch' event happened with customers info.
	 * @param ActionEvent event.
	 * @author Roman Milman
	 */
	@SuppressWarnings("unchecked")
	@FXML
	void onSwitch(ActionEvent event) {
		
		if (isSwitched(Message.getValue(selectedCustomer, "id"))) {
			showPopup("This customer has been switched allready");
			return;
		}

		JSONObject json = new JSONObject();

		json.put("command", "role switch has been pressed");
		json.put("id", Message.getValue(selectedCustomer, "id"));

		if (employerLabel.isVisible()) {

			String employerName = employerTextArea.getText();
			if (!isEmployerLegal(employerName)) {
				showPopup("Invalid employer name");
				return;
			}

			json.put("new role", "Business Customer");
			json.put("employer name", employerName);
		} else {
			json.put("new role", "Customer");
		}

		view.getComController().handleUserAction(json);
	}

	/**
	 * isEmployerLegal
	 * 
	 * This method performers some sanity checks on employer.
	 * return true if all checks are passed.
	 * @param String employer.
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
	 * This method shows pop-up messages.
	 * This method add's switched customers id to switchedCustomers, if 'Switch' was successful.
	 * @param JSONObject descriptor - holds: 'update' key with message from server, 'id' key with customers id as value.
	 * @author Roman Milman
	 */
	public void showPopup(JSONObject descriptor) {
		String msg = Message.getValue(descriptor, "update");
		
		if(msg.equals("customer role has been switched"))
			switchedCustomers.add(Message.getValue(descriptor, "id"));

		Platform.runLater(() -> {
			statusLabel.setText("Status: " + msg);
		});
	}
}