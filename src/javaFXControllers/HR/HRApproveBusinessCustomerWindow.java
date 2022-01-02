package javaFXControllers.HR;

import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import clientSide.HRPortalView;
import common.Logger;
import common.Message;
import common.Logger.Level;
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

public class HRApproveBusinessCustomerWindow {

	private Stage primaryStage;
	private Scene scene;
	private HBox homePageHBox;
	private HRPortalView view;
	private HashMap<String, Button> approveButtonMap;

	private JSONArray customers;

	@FXML
	private VBox businessClientVBox;

	public void init(HBox homePageHBox, Stage primaryStage, HRPortalView view) {
		this.homePageHBox = homePageHBox;
		this.primaryStage = primaryStage;
		this.view = view;
		approveButtonMap = new HashMap<String, Button>();
	}

	public void showWindow(JSONObject descriptor) {
		// log
		Logger.log(Level.INFO, "HRApproveBusinessClientWindow: showing window");
		System.out.println("HRApproveBusinessClientWindow: showing window");

		customers = (JSONArray) descriptor.get("customers");

		Platform.runLater(() -> {
			try {
				Scene scene = new Scene(homePageHBox);
				this.scene = scene;
			} catch (IllegalArgumentException e) {
				// log
				Logger.log(Level.WARNING, "HRApproveBusinessClientWindow: exception in showWindow");
				System.out.println("HRApproveBusinessClientWindow: exception in showWindow");
			}

			buildEmployersScrollPane();
			primaryStage.setScene(scene);
			primaryStage.show();

			JSONObject json = new JSONObject();
			json.put("command", "approve business client window is ready");
			view.ready(json);
		});
	}

	private void buildEmployersScrollPane() {
		businessClientVBox.getChildren().clear();
		approveButtonMap.clear();

		for (int i = 0; i < customers.size(); i++) {
			JSONObject employer = (JSONObject) customers.get(i);
			String id = Message.getValue(employer, "id");
			String phone = Message.getValue(employer, "number");
			String email = Message.getValue(employer, "email");
			String credit = Message.getValue(employer, "credit");

			VBox rowVBox = buildEmployerRow(id, phone, email, credit);

			businessClientVBox.getChildren().add(rowVBox);
		}
	}

	private VBox buildEmployerRow(String id, String phone, String email, String credit) {
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

	public void disableApproveButton(JSONObject descriptor) {
		String id = Message.getValue(descriptor, "id");

		Button disableButton = approveButtonMap.get(id);
		disableButton.setDisable(true);
	}

	@FXML
	public void onBackButton(ActionEvent event) {
		// log
		Logger.log(Level.DEBUG, "HRApproveBusinessClientWindow: back button was pressed");
		System.out.println("HRApproveBusinessClientWindow: back button was pressed");

		view.showHRHomePage();
	}

}
