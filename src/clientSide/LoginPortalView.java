package clientSide;

import java.io.IOException;

import org.json.simple.JSONObject;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import common.Logger;
import common.Logger.Level;
import javaFXControllers.LoginWindow;
import common.Message;

public class LoginPortalView implements PortalViewInterface {

	private ComController com;

	// ----- LOGIN variables

	private Stage primaryStage;
	private LoginWindow loginWindow;
	private VBox loginVBox;

	// ----- CONSTRUCTOR

	public LoginPortalView(Stage primaryStage, ComController com) {
		this.primaryStage = primaryStage;
		this.com = com;
	}

	@Override
	public void init(JSONObject json) {

		try {
			// LOGIN
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/templates/LoginTemplate.fxml"));
			loginVBox = loader.load();
			loginWindow = loader.getController();
			loginWindow.init(loginVBox, primaryStage, this);

			// log
			Logger.log(Level.DEBUG, "LoginPortalView: loginWindow initialized");
			System.out.println("LoginPortalView: loginWindow initialized");

			// displays login window
			loginWindow.showWindow();

			if (json != null && Message.getValue(json, "portalType").equals("login")) {
				loginWindow.onStatusConnected();
				
				// log
				Logger.log(Level.WARNING, "LoginPortalView: Logged out");
				System.out.println("LoginPortalView: Logged out");
			}

		} catch (IOException e) {
			Logger.log(Level.WARNING, "LoginPortalView: init: IOException");
			System.out.println("LoginPortalView: init: IOException");
		}
	}

	/** Handle Msg<p>
	 * 
	 * This method handles a certain login portal view related events sent by the communication
	 * controller using a JSONObject message containing the message keys (mostly using keys such as "command")
	 * 
	 * @param descriptor - Holding a certain "command" key message for the login portal view.
	 * 
	 * */
	@Override
	public void handleMsg(JSONObject descriptor) {

		switch ((String) descriptor.get("command")) {
		case "online":
			loginWindow.onStatusConnected();
			break;
		case "handshake":
			
			switch((String) descriptor.get("notOk")) {
			
			case "inactive":
				loginWindow.showPopup("User status is inactive.");
				break;
			case "freeze":
				loginWindow.showPopup("User status is frozen.");
				break;
			default:
				loginWindow.showPopup("User does not exist in the system.");
				break;
			}
		break;

		default:
			break;
		}
	}

	// ----- Functions for loginWindow
	/** Get Com Controller<p>
	 * This method return an object of ComController class.
	 * */
	@Override
	public ComController getComController() {
		return com;
	}
	
	/** Ready<p>
	 * This method receives a certain JSON message containing a task and sends it to the communication
	 * controller class using the handle user action method.
	 * @param json - Message contains a certain task or update.
	 * */
	@Override
	public void ready(JSONObject json) {
		com.handleUserAction(json);
	}

	/** Get Login Portal View<p>
	 * This method return an object of this class.
	 * */
	public LoginPortalView getLoginPortalView() {
		return this;
	}
	
	
}
