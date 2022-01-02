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
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * BranchManagerEditStatusWindow
 * 
 * This class is the javaFX controller for BranchManagerEditStatus.fxml
 * This class holds primaryStage, scene, editStatusRoot, view, selectedUser, customers, employers, hrs, suppliers, switchedUser variables.
 * JSONObject selectedUser - contains selected user's info.
 * JSONArray customers - Array of customers with their info.
 * JSONArray employers - Array of employers with their info.
 * JSONArray hrs - Array of hrs with their info.
 * JSONArray suppliers - Array of suppliers with their info.
 * JSONArray switchedUser - Array of users which already been switched.
 * @author Roman Milman
 */
public class BranchManagerEditStatusWindow {

	private Stage primaryStage;
	private Scene scene;
	private HBox editStatusRoot;
	private BranchManagerPortalView view;

	private JSONObject selectedUser;

	private JSONArray customers;
	private JSONArray employers;
	private JSONArray hrs;
	private JSONArray suppliers;

	private JSONArray switchedUsers;

	private ObservableList<Map<String, Object>> usersRows;

	@FXML
	private AnchorPane anchorID;

	@FXML
	private Label nameLabel;

	@FXML
	private Label switchFromLabel;

	@FXML
	private Label switchToLabel;

	@FXML
	private Button switchButton;
	
	@FXML
    private Label statusLabel;

	/**
	 * init
	 * 
	 * This method initializes the needed parameters for this controller.
	 * @param HBox editStatusRoot
	 * @param Stage primaryStage
	 * @param BranchManagerPortalView view
	 * @author Roman Milman
	 */
	public void init(HBox editStatusRoot, Stage primaryStage, BranchManagerPortalView view) {
		this.editStatusRoot = editStatusRoot;
		this.primaryStage = primaryStage;
		this.view = view;
	}

	/**
	 * showWindow
	 * 
	 * This method sets customers,employers,hrs,suppliers by the array's received from server.
	 * This method calls Platform.runLater() to add javaFX task.
	 * This method builds the scene and sets to primaryStage.
	 * This method announces to server "ready" after showing window.
	 * @param JSONObject descriptor - holds "customers","employers","hrs","suppliers" keys to JSONArrays.
	 * @author Roman Milman
	 */
	public void showWindow(JSONObject descriptor) {
		// log
		Logger.log(Level.INFO, "BranchManagerEditStatusWindow: showing window");
		System.out.println("BranchManagerEditStatusWindow: showing window");

		customers = (JSONArray) descriptor.get("customers");
		employers = (JSONArray) descriptor.get("employers");
		hrs = (JSONArray) descriptor.get("hrs");
		suppliers = (JSONArray) descriptor.get("suppliers");

		switchedUsers = new JSONArray();

		Platform.runLater(() -> {
			try {
				Scene scene = new Scene(editStatusRoot);
				this.scene = scene;
			} catch (IllegalArgumentException e) {
				// log
				Logger.log(Level.WARNING, "BranchManagerEditStatusWindow: exception in showWindow");
				System.out.println("BranchManagerEditStatusWindow: exception in showWindow");
			}

			clearWindow();
			buildTableView();
			primaryStage.setScene(scene);
			primaryStage.show();

			JSONObject json = new JSONObject();
			json.put("command", "edit status is ready");
			view.ready(json);
		});
	}

	/**
	 * buildTableView
	 * 
	 * This method builds TableView with potential users to edit their status.
	 * @author Roman Milman
	 */
	@SuppressWarnings("unchecked")
	private void buildTableView() {
		// log
		Logger.log(Level.DEBUG, "BranchManagerEditStatusWindow: building customer table view");
		System.out.println("BranchManagerEditStatusWindow: building customer table view");

		TableView usersTableView = new TableView();
		

		usersRows = FXCollections.<Map<String, Object>>observableArrayList();

		TableColumn<Map, String> nameCol = new TableColumn<>("Name");
		nameCol.setCellValueFactory(new MapValueFactory<>("Name"));

		TableColumn<Map, String> phoneNumberCol = new TableColumn<>("Phone number");
		phoneNumberCol.setCellValueFactory(new MapValueFactory<>("Phone number"));

		TableColumn<Map, String> emailCol = new TableColumn<>("Email");
		emailCol.setCellValueFactory(new MapValueFactory<>("Email"));

		TableColumn<Map, String> statusCol = new TableColumn<>("Status");
		statusCol.setCellValueFactory(new MapValueFactory<>("Status"));

		TableColumn<Map, String> roleCol = new TableColumn<>("Role");
		roleCol.setCellValueFactory(new MapValueFactory<>("Role"));

		usersTableView.getColumns().addAll(nameCol, phoneNumberCol, emailCol, statusCol, roleCol);

		// build and add rows
		addUserToUsersTableView(usersTableView, customers);
		addUserToUsersTableView(usersTableView, employers);
		addUserToUsersTableView(usersTableView, hrs);
		addUserToUsersTableView(usersTableView, suppliers);

		usersTableView.getItems().addAll(usersRows);

		anchorID.getChildren().add(usersTableView);

		usersTableView.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {

				try {
					selectedUser = (JSONObject) usersTableView.getSelectionModel().getSelectedItem();
					
					if(selectedUser == null)
						return;
					
					statusLabel.setText("Status: ");
					statusLabel.setTextFill(Paint.valueOf("black"));	
					
					switchButton.setVisible(true);
					String status = Message.getValue(selectedUser, "Status");

					nameLabel.setText(Message.getValue(selectedUser, "Name"));

					switchFromLabel.setText("Switch from: " + status);
					if (status.equals("active")) {
						switchToLabel.setText("Switch to: freeze");
					} else {
						switchToLabel.setText("Switch to: active");
					}

				} catch (Exception e) {
					Logger.log(Level.WARNING,
							"BranchManagerEditRoleWindow: tableView : setOnMouseClicked: Exception was thrown");
					System.out.println(
							"BranchManagerEditRoleWindow: tableView : setOnMouseClicked: Exception was thrown");
				}
			}
		});
	}

	/**
	 * addUserToUsersTableView
	 * 
	 * This method adds a user to TableView.
	 * @param TableView usersTableView.
	 * @param JSONArray jsonArray - contains an array of JSONObject's as users.
	 * @author Roman Milman
	 */
	@SuppressWarnings("unchecked")
	private void addUserToUsersTableView(TableView usersTableView, JSONArray jsonArray) {

		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject user = (JSONObject) jsonArray.get(i);

			JSONObject tableRow = new JSONObject();

			String name = Message.getValue(user, "name");
			String phoneNumber = Message.getValue(user, "phone number");
			String email = Message.getValue(user, "email");
			String status = Message.getValue(user, "status");
			String role = Message.getValue(user, "role");
			String username = Message.getValue(user, "username");

			tableRow.put("Name", name);
			tableRow.put("Phone number", phoneNumber);
			tableRow.put("Email", email);
			tableRow.put("Status", status);
			tableRow.put("Role", role);
			tableRow.put("username", username);

			usersRows.add(tableRow);
		}
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
		Logger.log(Level.DEBUG, "BranchManagerEditStatusWindow: back button was pressed");
		System.out.println("BranchManagerEditStatusWindow: back button was pressed");

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
		statusLabel.setText("Status: ");
		statusLabel.setTextFill(Paint.valueOf("black"));
	}

	/**
	 * isSwitched
	 * 
	 * This method goes over switchedCustomers JSONArray.
	 * If this users 'key' wasn't switch already return true, otherwise return false.
	 * @param String key - users key is personal : "Phone number" + "Email" + "Name"
	 * @author Roman Milman
	 */
	private Boolean isSwitched(String key) {
		for (int i = 0; i < switchedUsers.size(); i++) {
			String switchedKey = (String) switchedUsers.get(i);
			if (switchedKey.equals(key))
				return true;
		}
		return false;
	}

	/**
	 * onSwitch
	 * 
	 * This method called when 'Event' occurred to 'Switch' button.
	 * This method checks if this user is able to be switched.
	 * This method does some sanity checks on input in needed Widgets.
	 * This method sends to server a 'Switch' event happened with users info.
	 * @param ActionEvent event.
	 * @author Roman Milman
	 */
	@SuppressWarnings("unchecked")
	@FXML
	void onSwitch(ActionEvent event) {
		
		String key = Message.getValue(selectedUser, "Phone number") + Message.getValue(selectedUser, "Email")
		+ Message.getValue(selectedUser, "Name");

		if (isSwitched(key)) {
			showPopup("This user has been switched allready");
			return;
		}

		JSONObject json = new JSONObject();

		json = selectedUser;
		json.put("command", "status switch has been pressed");

		view.getComController().handleUserAction(json);
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
	 * This method add's switched users key to switchedUsers, if 'Switch' was successful.
	 * This method creates keys as, key = "Phone number" + "Email" + "Name" (those are keys in descriptor).
	 * @param JSONObject descriptor - holds: 'update' key with message from server, 'phone number','email','name' keys with users info accordingly as values.
	 * @author Roman Milman
	 */
	public void showPopup(JSONObject descriptor) {
		String msg = Message.getValue(descriptor, "update");

		if (msg.equals("user status has been changed")) {
			String key = Message.getValue(descriptor, "phone number") + Message.getValue(descriptor, "email")
					+ Message.getValue(descriptor, "name");
			switchedUsers.add(key);
		}

		Platform.runLater(() -> {
			statusLabel.setText("Status: " + msg);
		});
	}
}