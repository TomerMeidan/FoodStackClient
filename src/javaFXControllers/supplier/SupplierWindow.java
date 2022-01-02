package javaFXControllers.supplier;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.json.simple.JSONObject;

import clientSide.SupplierPortalView;

import javafx.application.Platform;
import javafx.event.ActionEvent;

import javafx.scene.Scene;

import javafx.stage.Stage;
import common.Logger;
import common.Logger.Level;

/**
 * Controller class that present the home page of supplier
 * 
 * @author Daniel Ohayon
 * @version 14/11/2021
 */
public class SupplierWindow {

	private Stage primaryStage;
	private Scene scene;
	private VBox supplierVBoxLoaded;
	private SupplierPortalView view;
	@FXML
	private VBox supplierVBox;
	@FXML
	private Button UpdateButton;
	@FXML
	private Button LogOutButton;
	@FXML
	private Label supplierLabel;
	@FXML
	private Button updateStatusButton;
	@FXML
    private Button receiptsButton;
	
	@SuppressWarnings("unchecked")
	public void init(VBox supplierVBoxTry, Stage primaryStage, SupplierPortalView view) {
		this.supplierVBoxLoaded = supplierVBoxTry;
		this.primaryStage = primaryStage;
		this.view = view;
		this.scene = new Scene(supplierVBoxLoaded);
	}

	public void showWindow() {
		// log
		Logger.log(Level.INFO, "SupplierWindow: showing window");
		System.out.println("SupplierWindow: showing window");

		Platform.runLater(() -> {
			StringBuilder welcomeMessage = new StringBuilder();
			welcomeMessage.append("Welcome, ");
			welcomeMessage.append(view.getFirstName());
			welcomeMessage.append("!");
			supplierLabel.setText(welcomeMessage.toString());
			primaryStage.setScene(scene);
			primaryStage.show();
			
			JSONObject json = new JSONObject();
			json.put("command", "home page is ready");
			view.ready(json);
		});

	}

	@FXML
	void clickOnLogOutButton(ActionEvent event) throws Exception {
		Logger.log(Level.INFO, "SupplierWindow: clickOnLogOutButton");
		System.out.println("SupplierWindow: clickOnLogOutButton");

		JSONObject json = new JSONObject();
		json.put("command", "clickOnLogOutButton");
		view.getComController().handleUserAction(json);
	}

	@FXML
	void clickOnUpdateButton(ActionEvent event) {
		Logger.log(Level.INFO, "SupplierWindow: clickOnUpdateButton");
		System.out.println("SupplierWindow: clickOnUpdateButton");
		JSONObject json = new JSONObject();
		json.put("command", "Update menu was clicked");
		view.ready(json);
	}

	@FXML
    void clickOnUpdateStatusButton(ActionEvent event) {
		Logger.log(Level.INFO, "SupplierWindow: clickOnUpdateStatusButton");
		System.out.println("SupplierWindow: clickOnUpdateStatusButton");
		JSONObject json = new JSONObject();
		json.put("command", "Update status was clicked");
		view.ready(json);
    }

	 @FXML
	    void clickOnReceiptsButton(ActionEvent event) {
		 Logger.log(Level.INFO, "SupplierWindow: clickOnReceiptsButton");
			System.out.println("SupplierWindow: clickOnReceiptsButton");
			JSONObject json = new JSONObject();
			json.put("command", "Receipts button was clicked");
			view.ready(json);
	    }
}
