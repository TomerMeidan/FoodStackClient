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
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * BranchManagerApproveEmployerWindow
 * 
 * This class is the javaFX controller for BranchManagerApproveEmployerTemplate.fxml
 * This class holds primaryStage, scene, approveEmployerRoot, view, approveButtonMap, employers variables.
 * JSONArray employers - array of all potential employers to be approved.
 * @author Roman Milman
 */
public class BranchManagerApproveEmployerWindow {
	private Stage primaryStage;
	private Scene scene;
	private HBox approveEmployerRoot;
	private BranchManagerPortalView view;
	private HashMap<String, Button> approveButtonMap;

	private JSONArray employers;

	@FXML
	private VBox employerVBox;

	/**
	 * init
	 * 
	 * This method initializes the needed parameters for this controller.
	 * @param HBox approveEmployerRoot
	 * @param Stage primaryStage
	 * @param BranchManagerPortalView view
	 * @author Roman Milman
	 */
	public void init(HBox approveEmployerRoot, Stage primaryStage, BranchManagerPortalView view) {
		this.approveEmployerRoot = approveEmployerRoot;
		this.primaryStage = primaryStage;
		this.view = view;
		approveButtonMap = new HashMap<String, Button>();
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
		Logger.log(Level.INFO, "BranchManagerApproveEmployerWindow: showing window");
		System.out.println("BranchManagerApproveEmployerWindow: showing window");

		employers = (JSONArray) descriptor.get("employers");

		Platform.runLater(() -> {
			try {
				Scene scene = new Scene(approveEmployerRoot);
				this.scene = scene;
			} catch (IllegalArgumentException e) {
				// log
				Logger.log(Level.WARNING, "BranchManagerApproveEmployerWindow: exception in showWindow");
				System.out.println("BranchManagerApproveEmployerWindow: exception in showWindow");
			}

			buildEmployersScrollPane();
			primaryStage.setScene(scene);
			primaryStage.show();

			JSONObject json = new JSONObject();
			json.put("command", "approve employer window is ready");
			view.ready(json);
		});
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
	public void onBackButton(ActionEvent event) {
		// log
		Logger.log(Level.DEBUG, "BranchManagerApproveEmployerWindow: back button was pressed");
		System.out.println("BranchManagerApproveEmployerWindow: back button was pressed");

		view.showBranchManagerHomePage();
	}

	/**
	 * buildEmployersScrollPane
	 * 
	 * This method builds scroll pane with employers info.
	 * Info taken from employers JSONArray.
	 * @author Roman Milman
	 */
	private void buildEmployersScrollPane() {
		// log
		Logger.log(Level.DEBUG, "BranchManagerApproveEmployerWindow: building employer scroll pane");
		System.out.println("BranchManagerApproveEmployerWindow: building employer scroll pane");

		employerVBox.getChildren().clear();
		approveButtonMap.clear();

		for (int i = 0; i < employers.size(); i++) {
			JSONObject employer = (JSONObject) employers.get(i);
			String name = Message.getValue(employer, "name");
			String phone = Message.getValue(employer, "number");
			String email = Message.getValue(employer, "email");
			String credit = Message.getValue(employer, "credit");

			VBox rowVBox = buildEmployerRow(name, phone, email, credit);

			employerVBox.getChildren().add(rowVBox);
		}
	}

	/**
	 * buildEmployerRow
	 * 
	 * This method builds VBox row with employer info.
	 * @param String name
	 * @param String phone
	 * @param String email
	 * @param String credit
	 * @return VBox
	 * @author Roman Milman
	 */
	private VBox buildEmployerRow(String name, String phone, String email, String credit) {
		VBox rowVBox = new VBox();
		HBox rowHBox = new HBox();

		VBox personalInfoVBox = new VBox();
		Label nameLabel = new Label("Name: " + name);
		Label phoneLabel = new Label("Phone number: " + phone);
		Label emailLabel = new Label("Email: " + email);
		Label creditLabel = new Label("Credit number: " + credit);
		
		nameLabel.setFont(Font.font("verdana", FontPosture.REGULAR, 12));
		phoneLabel.setFont(Font.font("verdana", FontPosture.REGULAR, 12));
		emailLabel.setFont(Font.font("verdana", FontPosture.REGULAR, 12));
		creditLabel.setFont(Font.font("verdana", FontPosture.REGULAR, 12));

		rowHBox.getChildren().add(personalInfoVBox);
		rowHBox.setHgrow(personalInfoVBox, Priority.ALWAYS);

		personalInfoVBox.getChildren().add(nameLabel);
		personalInfoVBox.getChildren().add(phoneLabel);
		personalInfoVBox.getChildren().add(emailLabel);
		personalInfoVBox.getChildren().add(creditLabel);

		Button approveButton = new Button("APPROVE");
		approveButton.setStyle("-fx-background-color: #F24444; -fx-text-fill: white");
		approveButton.setMaxWidth(Double.MAX_VALUE);
		approveButton.setMaxHeight(50);
		approveButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("BranchManagerApproveEmployerWindow: " + name + " approve button was pressed");

				JSONObject json = new JSONObject();
				json.put("command", "approve was pressed");
				json.put("name", name);
				view.getComController().handleUserAction(json);
			}
		});
		approveButtonMap.put(name, approveButton);

		rowHBox.getChildren().add(approveButton);

		Separator separator = new Separator();

		rowVBox.getChildren().add(rowHBox);
		rowVBox.getChildren().add(separator);

		return rowVBox;
	}

	/**
	 * showPopup
	 * 
	 * This method shows pop-up messages.
	 * This method disables 'Approve' buttons if approving employer succeeded.
	 * @param JSONObject descriptor - holds: 'update' key with message from server, 'employer' key with employers name as value.
	 * @author Roman Milman
	 */
	public void showPopup(JSONObject descriptor) {
		String msg = Message.getValue(descriptor, "update");

		if (msg.equals("employer has been activated")) {
			approveButtonMap.get(Message.getValue(descriptor, "employer")).setDisable(true);
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
}
