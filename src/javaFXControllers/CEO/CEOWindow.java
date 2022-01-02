package javaFXControllers.CEO;

import java.io.IOException;

import org.json.simple.JSONObject;

import clientSide.CEOPortalView;
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

public class CEOWindow {

	private Stage primaryStage;
	private HBox homePageHBox;
	private Scene scene;
	private CEOPortalView view;


	@FXML
	private Button logoutButton;

	@FXML
	private Button viewQuartelyReportsButton;
	
    @FXML
    private Button viewMonthlyReportsButton;

	@FXML
	private Label welcomeNameLabel;
	
	/** init<p>
	 * The method saves the main variables regarding the CEO main home page window.
	 * Also initiates the scene of the CEO window.
	 * @param homePageHBox - The main javafx object defined for the window.
	 * @param primaryStage - Primary stage for the CEO window.
	 * @param view - CEO portal client communication controller
	 * 
	 * */
	public void init(HBox homePageHBox, Stage primaryStage, CEOPortalView view) {
		this.homePageHBox = homePageHBox;
		this.primaryStage = primaryStage;
		this.view = view;
		scene = new Scene(homePageHBox);
	}
	
	/** On View Monthly Reports Button<p>
	 * 
	 * This method is a trigger defined in the SceneBuilder.
	 * On clicking the Monthly Reports button, the method will be initiated and send
	 * a JSON Object to the server side a request to switch the screen window to
	 * that of the Monthly Reports window. 
	 * 
	 * @param event - action event of clicking the "Monthly Reports" button.
	 * */
	@SuppressWarnings("unchecked")
	@FXML
	void onViewMonthlyReportsButton(ActionEvent event) {
		
		// log
		Logger.log(Level.INFO, "CEOWindow: onviewMonthlyReportsButton: monthly reports button was pressed!");
		System.out.println("CEOWindow: onviewMonthlyReportsButton: monthly reports button was pressed!");
		
		JSONObject json = new JSONObject();
		json.put("command", "monthly reports button is pressed");
		json.put("message", "Request to view the monthly reports from user: " + view.getComController().getInetAddress());
		view.getComController().handleUserAction(json);		
	}
	
	/** On View Quarterly Reports Button<p>
	 * 
	 * This method is a trigger defined in the SceneBuilder.
	 * On clicking the Quarterly Reports button, the method will be initiated and send
	 * a JSON Object to the server side a request to switch the screen window to
	 * that of the Quarterly Reports window. 
	 * 
	 * @param event - action event of clicking the "Quarterly Reports" button.
	 * */
	@FXML
	void onViewQuartelyReportsButton(ActionEvent event) {
		
		// log
		Logger.log(Level.INFO, "CEOWindow: onViewQuartelyReportsButton: quartely reports button was pressed!");
		System.out.println("CEOWindow: onViewQuartelyReportsButton: quartely reports button was pressed!");
		
		JSONObject json = new JSONObject();
		json.put("command", "view quarterly reports button is pressed");
		json.put("message", "Request to view the quarterly reports from user: " + view.getComController().getInetAddress());
		view.getComController().handleUserAction(json);
		
	}
	
	/** On Logout Button<p>
	 * 
	 * This method is a trigger defined in the SceneBuilder.
	 * On clicking the logout button, the method will be initiated and send
	 * a JSON Object to the server side a request to logout. 
	 * 
	 * @param event - action event of clicking the "Logout" button.
	 * */
	@FXML
	void onLogoutButton(ActionEvent event) {
		
		Logger.log(Level.INFO, "CEOWindow: logout button was pressed");
		System.out.println("CEOWindow: logout button was pressed");

		JSONObject json = new JSONObject();
		json.put("command", "logout was pressed");
		view.getComController().handleUserAction(json);
	}
	

	/** Show Window<p>
	 * 
	 * This method initiates the FXML main scene for the CEO home page.
	 * After showing the window, the method creates a JSON Object that will send a response,
	 * To the server side confirming that the window is now showing for the client.
	 * 
	 * */
	public void showWindow() {
		// log
		Logger.log(Level.INFO, "CEOWindow: showWindow: showing the ceo window");
		System.out.println("CEOWindow: showWindow: showing the ceo window");

		Platform.runLater(() -> {
			StringBuilder welcomeMessage = new StringBuilder();
			welcomeMessage.append("Welcome, ");
			welcomeMessage.append(view.getFirstName());
			welcomeMessage.append("!");
			welcomeNameLabel.setText(welcomeMessage.toString());
			primaryStage.setScene(scene);
			primaryStage.show();

			JSONObject json = new JSONObject();
			json.put("command", "home page is ready");
			view.ready(json);
		});
	}

}
