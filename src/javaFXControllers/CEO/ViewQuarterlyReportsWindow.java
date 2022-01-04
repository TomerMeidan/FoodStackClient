package javaFXControllers.CEO;

import org.json.simple.JSONObject;

import clientSide.CEOPortalView;
import common.Logger;
import common.Logger.Level;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class ViewQuarterlyReportsWindow {

    @FXML
    private AnchorPane anchorPaneID;

    @FXML
    private Button backButton;

    @FXML
    private Button viewIncomeReportsButton;
    
    @FXML
    private Button downloadQuarterlyReportsButton;
    
    private HBox homePageHBox;
    private Stage primaryStage;
    private CEOPortalView view;
    private JSONObject personalInfo;
    private Scene scene;
    
	/** Init<p>
	 * This method initiates certain JAVAFX variables in order to present the quarterly reports window.
	 * @param homePageHBox - Main HBOX for the monthly reports window
	 * @param primaryStage - Main stage.
	 * @param view - Communication Controller for the quarterly reports window
	 * @param personalInfo - Personal info about the client.
	 * 
	 * */
	public void init(HBox homePageHBox, Stage primaryStage, CEOPortalView view, JSONObject personalInfo) {
		this.homePageHBox = homePageHBox;
		this.primaryStage = primaryStage;
		this.view = view;
		this.personalInfo = personalInfo;
		scene = new Scene(homePageHBox);
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
	public void showWindow() {
		// log
		Logger.log(Level.INFO, "ViewQuarterlyReportsWindow: showing window");
		System.out.println("ViewQuarterlyReportsWindow: showing window");

		Platform.runLater(() -> {
			primaryStage.setScene(scene);
			primaryStage.show();

			JSONObject json = new JSONObject();
			json.put("command", "quarterly reports page is ready");
			view.ready(json);
		});
	}
	/**
	 * On Back Button
	 * <p>
	 * 
	 * This is an FXML trigger on the button back. The method will activate on user
	 * pressed back button, a destructor for all related variables to the class will
	 * initiate in order to reset their values and a method will return to the
	 * previous of the CEO.
	 * 
	 * @param event - This object is an action event that represent the action on pressing the back button.

	 * @author Tomer Meidan
	 */
    @FXML
    void onBackButton(ActionEvent event) {
    	
		Logger.log(Level.INFO, "ViewQuarterlyReportsWindow: Back button was pressed, returning to CEO homepage");
		System.out.println("ViewQuarterlyReportsWindow: Back button was pressed, returning to CEO homepage");
		
		view.getCeoWindow().showWindow();

    }

    /** On View Income Reports Button<p>
     * This method will navigate to the View income report window
     * 
     * */
    @FXML
    void onViewIncomeReportsButton(ActionEvent event) {
		Logger.log(Level.INFO, "ViewQuarterlyReportsWindow: View Income Reports button was pressed!");
		System.out.println("ViewQuarterlyReportsWindow: View Income Reports button was pressed!");
		
		JSONObject json = new JSONObject();
		json.put("command", "view quarterly income reports button is pressed");
		json.put("message", "Request to view the quarterly income reports from user: " + view.getComController().getInetAddress());
		view.getComController().handleUserAction(json);
		view.getViewQuarterlyIncomeReports().showWindow();
    }
    /** On Download Quarterly Reports Button<p>
     * This method will navigate to the download quarterly report window
     * 
     * */
    @FXML
    void onDownloadQuarterlyReportsButton(ActionEvent event) {

		Logger.log(Level.INFO, "ViewQuarterlyReportsWindow: Upload Quarterly Reports button was pressed");
		System.out.println("ViewQuarterlyReportsWindow: Upload Quarterly Reports button was pressed");
		
		JSONObject json = new JSONObject();
		json.put("command", "download quarterly reports button is pressed");
		view.getComController().handleUserAction(json);


		

    }

}