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
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * HRApproveBusinessCustomerWindow
 * 
 * This class is the javaFX controller for HRApproveBusinessClient.fxml
 * This class holds primaryStage, scene, approveRoot, view, approveButtonMap, customers variables.
 * JSONArray customers - array of all potential business customers to be approved.
 * @author Roman Milman
 */
public class HRApproveBusinessCustomerWindow {

	private Stage primaryStage;
	private Scene scene;
	private HBox approveRoot;
	private HRPortalView view;
	private HashMap<String, Button> approveButtonMap;

	private JSONArray customers;

	@FXML
	private VBox businessClientVBox;

	/**
	 * init
	 * 
	 * This method initializes the needed parameters for this controller.
	 * @param HBox approveRoot
	 * @param Stage primaryStage
	 * @param BranchManagerPortalView view
	 * @author Roman Milman
	 */
	public void init(HBox approveRoot, Stage primaryStage, HRPortalView view) {
		this.approveRoot = approveRoot;
		this.primaryStage = primaryStage;
		this.view = view;
		approveButtonMap = new HashMap<String, Button>();
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
		Logger.log(Level.INFO, "HRApproveBusinessClientWindow: showing window");
		System.out.println("HRApproveBusinessClientWindow: showing window");

		customers = (JSONArray) descriptor.get("customers");

		Platform.runLater(() -> {
			try {
				Scene scene = new Scene(approveRoot);
				this.scene = scene;
			} catch (IllegalArgumentException e) {
				// log
				Logger.log(Level.WARNING, "HRApproveBusinessClientWindow: exception in showWindow");
				System.out.println("HRApproveBusinessClientWindow: exception in showWindow");
			}

			buildBusinessCustomersScrollPane();
			primaryStage.setScene(scene);
			primaryStage.show();

			JSONObject json = new JSONObject();
			json.put("command", "approve business client window is ready");
			view.ready(json);
		});
	}

	/**
	 * buildBusinessCustomersScrollPane
	 * 
	 * This method builds scroll pane with customers info.
	 * Info taken from customers JSONArray.
	 * @author Roman Milman
	 */
	private void buildBusinessCustomersScrollPane() {
		businessClientVBox.getChildren().clear();
		approveButtonMap.clear();

		for (int i = 0; i < customers.size(); i++) {
			JSONObject employer = (JSONObject) customers.get(i);
			String id = Message.getValueString(employer, "id");
			String phone = Message.getValueString(employer, "number");
			String email = Message.getValueString(employer, "email");
			String credit = Message.getValueString(employer, "credit");

			VBox rowVBox = buildBusinessCustomerRow(id, phone, email, credit);

			businessClientVBox.getChildren().add(rowVBox);
		}
	}

	/**
	 * buildBusinessCustomerRow
	 * 
	 * This method builds VBox row with business customers info.
	 * @param String id
	 * @param String phone
	 * @param String email
	 * @param String credit
	 * @return VBox
	 * @author Roman Milman
	 */
	private VBox buildBusinessCustomerRow(String id, String phone, String email, String credit) {
		VBox rowVBox = new VBox();
		HBox rowHBox = new HBox();

		VBox personalInfoVBox = new VBox();
		Label nameLabel = new Label("ID: " + id);
		Label phoneLabel = new Label("Phone number: " + phone);
		Label emailLabel = new Label("Email: " + email);
		Label creditLabel = new Label("Credit number: " + credit);

		rowHBox.getChildren().add(personalInfoVBox);
		rowHBox.setHgrow(personalInfoVBox, Priority.ALWAYS);

		personalInfoVBox.getChildren().add(nameLabel);
		personalInfoVBox.getChildren().add(phoneLabel);
		personalInfoVBox.getChildren().add(emailLabel);
		personalInfoVBox.getChildren().add(creditLabel);

		Button approveButton = new Button("APPROVE");
		approveButton.setMaxWidth(Double.MAX_VALUE);
		approveButton.setMaxHeight(50);
		approveButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("HRApproveBusinessClientWindow: " + id + " approve button was pressed");

				JSONObject json = new JSONObject();
				json.put("command", "approve was pressed");
				json.put("id", id);
				view.getComController().handleUserAction(json);
			}
		});
		approveButtonMap.put(id, approveButton);

		rowHBox.getChildren().add(approveButton);

		Separator separator = new Separator();

		rowVBox.getChildren().add(rowHBox);
		rowVBox.getChildren().add(separator);

		return rowVBox;
	}

	/**
	 * disableApproveButton
	 * 
	 * This method disables a button in approveButtonMap by the given id as input.
	 * @param JSONObject descriptor - includes "id" as key to customers id accordingly.
	 * @author Roman Milman
	 */
	public void disableApproveButton(JSONObject descriptor) {
		String id = Message.getValueString(descriptor, "id");

		Button disableButton = approveButtonMap.get(id);
		disableButton.setDisable(true);
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
		Logger.log(Level.DEBUG, "HRApproveBusinessClientWindow: back button was pressed");
		System.out.println("HRApproveBusinessClientWindow: back button was pressed");

		view.showHRHomePage();
	}

}
