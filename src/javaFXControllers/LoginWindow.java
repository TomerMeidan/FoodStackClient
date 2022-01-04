package javaFXControllers;

import org.json.simple.JSONObject;

import clientSide.LoginPortalView;
import common.Logger;
import common.Logger.Level;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class LoginWindow {

	private Stage primaryStage;
	private Scene scene;
	private VBox loginVBox;
	private LoginPortalView view;

	private final int MAX_INPUT = 30;

	@FXML
	private Label loginStatusLabel;
	@FXML
	private Button loginButton;
	@FXML
	private TextArea usernameTextArea;
	@FXML
	private PasswordField passwordField;

	public void init(VBox loginVBox, Stage primaryStage, LoginPortalView view) {
		this.loginVBox = loginVBox;
		this.primaryStage = primaryStage;
		this.view = view;
	}

	/** Show Window
	 * This method will initiate the Login window set by the FXML Login template.
	 * Using the primary stage that is set by the init() method, the login window will be shown
	 * accordingly.
	 * 
	 * */
	public void showWindow() {
		// log
		Logger.log(Level.INFO, "LoginWindow: showing window");
		System.out.println("LoginWindow: showing window");

		Platform.runLater(() -> {
			try {
				Scene scene = new Scene(loginVBox);
				this.scene = scene;
			} catch (IllegalArgumentException e) {
				// log
				Logger.log(Level.WARNING, "LoginWindow: exception in showWindow");
				System.out.println("LoginWindow: exception in showWindow");
			}

			usernameTextArea.setTextFormatter(new TextFormatter<String>(
					change -> change.getControlNewText().length() <= MAX_INPUT ? change : null));
			passwordField.setTextFormatter(new TextFormatter<String>(
					change -> change.getControlNewText().length() <= MAX_INPUT ? change : null));

			primaryStage.setScene(scene);
			primaryStage.show();

		});
	}

	/** On Login Button<p>
	 * This method is an FX trigger initiated by clicking the login button, once button is pressed
	 * a JSON object with a message to login will be sent to the server side.
	 * 
	 * */
	@SuppressWarnings("unchecked")
	@FXML
	public void onLoginButton(ActionEvent event) {
		// log
		Logger.log(Level.INFO, "LoginWindow: login button was pressed");
		System.out.println("LoginWindow: login button was pressed");

		JSONObject json = new JSONObject();
		json.put("command", "login was pressed");
		json.put("username", usernameTextArea.getText());
		json.put("password", passwordField.getText());

		view.getComController().handleUserAction(json);
	}

	/** On StatusConnected<p>
	 * This method will initiate a certain text FX to change depending on the connection
	 * between the client and the server. In this case it will turn GREEN if the connection is
	 * enabled.
	 * 
	 * */
	public void onStatusConnected() {
		Platform.runLater(() -> {
			// log
			Logger.log(Level.DEBUG, "LoginWindow: updating status to: connected");
			System.out.println("LoginWindow: updating status to: connected");

			loginStatusLabel.setText("ONLINE");
			loginStatusLabel.setTextFill(Paint.valueOf("GREEN"));
			loginButton.setDisable(false);

		});
	}

	/** On Status Disconnected<p>
	 * This method will initiate a certain text FX to change depending on the connection
	 * between the client and the server. In this case it will turn RED if the connection is
	 * disabled.
	 * 
	 * */
	public void onStatusDisconnected() {
		Platform.runLater(() -> {
			// log
			Logger.log(Level.DEBUG, "LoginWindow: updating status to: disconnected");
			System.out.println("LoginWindow: updating status to: disconnected");

			loginStatusLabel.setText("OFFLINE");
			loginStatusLabel.setTextFill(Paint.valueOf("RED"));
			loginButton.setDisable(true);
		});
	}

	/** Show Popup<p>
	 * This method is using some dynamic FX features to display a certain popup message
	 * that displays an ERROR of some kind (For example: User doesn't exist, or frozen...)
	 * 
	 * @param msg - This message will hold a certain string with an error message type.
	 * */
	public void showPopup(String msg) {
		Platform.runLater(() -> {
			Stage window = new Stage();
			window.initModality(Modality.APPLICATION_MODAL);
			window.setTitle("ERROR");
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
