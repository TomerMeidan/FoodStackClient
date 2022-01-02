package javaFXControllers.branchManager;

import org.json.simple.JSONObject;

import clientSide.BranchManagerPortalView;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import common.Logger;
import common.Logger.Level;
import common.Message;

/**
 * BranchManagerHomePageWindow
 * 
 * This class is the javaFX controller for BranchManagerHomePageTemplate.fxml
 * This class holds primaryStage, scene, homePageRoot, view variables.
 * @author Roman Milman
 */
public class BranchManagerHomePageWindow {

	private Stage primaryStage;
	private Scene scene;
	private HBox homePageRoot;
	private BranchManagerPortalView view;

	@FXML
	private Button clientRegisterButton;
	@FXML
	private Button logoutButton;
	@FXML
	private Label welcomeLabel;
	@FXML
	private Button uploadQuarterlyReportsButton;

	/**
	 * init
	 * 
	 * This method initializes the needed parameters for this controller.
	 * @param HBox homePageRoot
	 * @param Stage primaryStage
	 * @param BranchManagerPortalView view
	 * @author Roman Milman
	 */
	public void init(HBox homePageRoot, Stage primaryStage, BranchManagerPortalView view) {
		this.homePageRoot = homePageRoot;
		this.primaryStage = primaryStage;
		this.view = view;
	}

	/**
	 * showWindow
	 * 
	 * This method calls Platform.runLater() to add javaFX task.
	 * This method builds the scene and sets to primaryStage.
	 * This method announces to server "ready" after showing window.
	 * @param JSONObject descriptor - has 'FirstName','LastName' keys with accordingly values.
	 * @author Roman Milman
	 */
	public void showWindow(JSONObject descriptor) {
		// log
		Logger.log(Level.INFO, "BranchManagerHomePage: showing window");
		System.out.println("BranchManagerHomePage: showing window");

		Platform.runLater(() -> {
			try {
				Scene scene = new Scene(homePageRoot);
				this.scene = scene;
			} catch (IllegalArgumentException e) {
				// log
				Logger.log(Level.WARNING, "BranchManagerHomePage: exception in showWindow");
				System.out.println("BranchManagerHomePage: exception in showWindow");
			}
			try {
			welcomeLabel.setText("Welcome, " + Message.getValue(descriptor, "FirstName") + " "
					+ Message.getValue(descriptor, "LastName"));

			primaryStage.setScene(scene);
			primaryStage.show();

			JSONObject json = new JSONObject();
			json.put("command", "home page is ready");
			json.put("home page", "branch manager");
			view.ready(json);
			} catch(Exception e) {
				System.out.println("");
			}
		});
	}
	
	/**
	 * onViewMonthlyReportsButton
	 * 
	 * This method called when 'Event' occurred to 'View monthly reports' button.
	 * This method sends to server an event of 'monthly reports' happened.
	 * @param ActionEvent event.
	 * @author Roman Milman
	 */
	@SuppressWarnings("unchecked")
	@FXML
	void onViewMonthlyReportsButton(ActionEvent event) {
		
		// log
		Logger.log(Level.INFO, "BranchManagerHomePageWindow: onviewMonthlyReportsButton: monthly reports button was pressed!");
		System.out.println("BranchManagerHomePageWindow: onviewMonthlyReportsButton: monthly reports button was pressed!");
		
		JSONObject json = new JSONObject();
		json.put("command", "monthly reports button is pressed");
		json.put("message", "Request to view the monthly reports from user: " + view.getComController().getInetAddress());
		view.getComController().handleUserAction(json);
	}

	/**
	 * onLogoutButton
	 * 
	 * This method called when 'Event' occurred to 'Logout' button.
	 * This method sends to server event of 'Logout' happened.
	 * @param ActionEvent event.
	 * @author Roman Milman
	 */
	@FXML
	public void onLogoutButton(ActionEvent event) {
		// log
		Logger.log(Level.INFO, "BranchManagerHomePage: logout button was pressed");
		System.out.println("BranchManagerHomePage: logout button was pressed");

		JSONObject json = new JSONObject();
		json.put("command", "logout was pressed");
		view.getComController().handleUserAction(json);
	}

	/**
	 * onClientRegisterButton
	 * 
	 * This method called when 'Event' occurred to 'client registration' button.
	 * This method sends to server event of 'client registration' happened, with 'branch'.
	 * @param ActionEvent event.
	 * @author Roman Milman
	 */
	@FXML
	public void onClientRegisterButton(ActionEvent event) {
		// log
		Logger.log(Level.INFO, "BranchManagerHomePage: client register button was pressed");
		System.out.println("BranchManagerHomePage: client register button was pressed");
		
		JSONObject json = new JSONObject();
		json.put("command", "client register was pressed");
		json.put("branch", view.getBranch());
		view.getComController().handleUserAction(json);
	}
	
	/**
	 * onApproveEmployerButton
	 * 
	 * This method called when 'Event' occurred to 'approve employer' button.
	 * This method sends to server event of 'approve employer' happened, with 'branch'.
	 * @param ActionEvent event.
	 * @author Roman Milman
	 */
	@FXML
	public void onApproveEmployerButton(ActionEvent event) {
		// log
		Logger.log(Level.INFO, "BranchManagerHomePage: approve employer button was pressed");
		System.out.println("BranchManagerHomePage: approve employer button was pressed");
		
		JSONObject json = new JSONObject();
		json.put("command", "approve employer was pressed");
		json.put("branch", view.getBranch());
		view.getComController().handleUserAction(json);

	}
	
	/**
	 * onRegisterSupplierButton
	 * 
	 * This method called when 'Event' occurred to 'register supplier' button.
	 * This method sends to server event of 'register supplier' happened, with 'branch'.
	 * @param ActionEvent event.
	 * @author Roman Milman
	 */
	@SuppressWarnings("unchecked")
	@FXML
	public void onRegisterSupplierButton(ActionEvent event) {
		// log
		Logger.log(Level.INFO, "BranchManagerHomePage: supplier registration button was pressed");
		System.out.println("BranchManagerHomePage: supplier registration button was pressed");
		
		JSONObject json = new JSONObject();
		json.put("command", "supplier registration was pressed");
		json.put("branch", view.getBranch());
		view.getComController().handleUserAction(json);

	}
	
	/**
	 * onUploadQuarterlyReportsButton
	 * 
	 * This method called when 'Event' occurred to 'upload quarterly reports' button.
	 * This method sends to server event of 'upload quarterly reports'.
	 * @param ActionEvent event.
	 * @author Roman Milman
	 */
	@FXML
	public void onUploadQuarterlyReportsButton(ActionEvent event) {
		// log
		Logger.log(Level.INFO, "BranchManagerHomePageWindow: onUploadQuarterlyReportsButton: upload quarterly reports button was pressed!");
		System.out.println("BranchManagerHomePageWindow: onUploadQuarterlyReportsButton: upload quarterly reports button was pressed!");
		view.showBranchManagerUploadQuarterlyReportsWindow().setLabels();
		JSONObject json = view.showBranchManagerUploadQuarterlyReportsWindow().getRelevantBranchReportInfo();
		json.put("command", "upload quarterly reports button was pressed");
		view.getComController().handleUserAction(json);
		
	}
	
	/**
	 * onEditInformationButton
	 * 
	 * This method called when 'Event' occurred to 'edit information' button.
	 * This method sends to server event of 'edit information' happened.
	 * @param ActionEvent event.
	 * @author Roman Milman
	 */
	@SuppressWarnings("unchecked")
	@FXML
	public void onEditInformationButton(ActionEvent event) {
		// log
		Logger.log(Level.INFO, "BranchManagerHomePage: edit information button was pressed");
		System.out.println("BranchManagerHomePage: edit information button was pressed");
		
		JSONObject json = new JSONObject();
		json.put("command", "edit information was pressed");
		view.getComController().handleUserAction(json);
	}
}
