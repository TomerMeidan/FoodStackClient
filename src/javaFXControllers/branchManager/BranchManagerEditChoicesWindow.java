package javaFXControllers.branchManager;

import org.json.simple.JSONObject;

import clientSide.BranchManagerPortalView;
import common.Logger;
import common.Message;
import common.Logger.Level;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * BranchManagerEditChoicesWindow
 * 
 * This class is the javaFX controller for BranchManagerEditChoices.fxml
 * This class holds primaryStage, scene, editChoicesRoot, view variables.
 * @author Roman Milman
 */
public class BranchManagerEditChoicesWindow {

	private Stage primaryStage;
	private Scene scene;
	private HBox editChoicesRoot;
	private BranchManagerPortalView view;

	/**
	 * init
	 * 
	 * This method initializes the needed parameters for this controller.
	 * @param HBox editChoicesRoot
	 * @param Stage primaryStage
	 * @param BranchManagerPortalView view
	 * @author Roman Milman
	 */
	public void init(HBox editChoicesRoot, Stage primaryStage, BranchManagerPortalView view) {
		this.editChoicesRoot = editChoicesRoot;
		this.primaryStage = primaryStage;
		this.view = view;
	}

	/**
	 * showWindow
	 * 
	 * This method calls Platform.runLater() to add javaFX task.
	 * This method builds the scene and sets to primaryStage.
	 * This method announces to server "ready" after showing window.
	 * @param JSONObject descriptor - without use here, (for future flexibility).
	 * @author Roman Milman
	 */
	public void showWindow(JSONObject descriptor) {
		// log
		Logger.log(Level.INFO, "BranchManagerEditChoicesWindow: showing window");
		System.out.println("BranchManagerEditChoicesWindow: showing window");

		Platform.runLater(() -> {
			try {
				Scene scene = new Scene(editChoicesRoot);
				this.scene = scene;
			} catch (IllegalArgumentException e) {
				// log
				Logger.log(Level.WARNING, "BranchManagerEditChoicesWindow: exception in showWindow");
				System.out.println("BranchManagerEditChoicesWindow: exception in showWindow");
			}

			primaryStage.setScene(scene);
			primaryStage.show();

			JSONObject json = new JSONObject();
			json.put("command", "edit choices is ready");
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
	void onBackButton(ActionEvent event) {
		// log
		Logger.log(Level.INFO, "BranchManagerEditChoicesWindow: back button was pressed");
		System.out.println("BranchManagerEditChoicesWindow: back button was pressed");

		view.showBranchManagerHomePage();
	}

	/**
	 * onEditRoles
	 * 
	 * This method called when 'Event' occurred to 'Edit' button.
	 * This method sends to server JSONObject, with relevant message for this event.
	 * @param ActionEvent event.
	 * @author Roman Milman
	 */
	@SuppressWarnings("unchecked")
	@FXML
	void onEditRoles(ActionEvent event) {
		// log
		Logger.log(Level.INFO, "BranchManagerEditChoicesWindow: edit role button was pressed");
		System.out.println("BranchManagerEditChoicesWindow: edit role button was pressed");

		JSONObject json = new JSONObject();
		json.put("command", "edit roles was pressed");
		json.put("branch", view.getBranch());
		view.getComController().handleUserAction(json);
	}

	/**
	 * onEditStatus
	 * 
	 * This method called when 'Event' occurred to 'Edit' button.
	 * This method sends to server JSONObject, with relevant message for this event.
	 * @param ActionEvent event.
	 * @author Roman Milman
	 */
	@FXML
	void onEditStatus(ActionEvent event) {
		// log
		Logger.log(Level.INFO, "BranchManagerEditChoicesWindow: edit status button was pressed");
		System.out.println("BranchManagerEditChoicesWindow: edit status button was pressed");

		JSONObject json = new JSONObject();
		json.put("command", "edit status was pressed");
		json.put("branch", view.getBranch());
		view.getComController().handleUserAction(json);
	}
}