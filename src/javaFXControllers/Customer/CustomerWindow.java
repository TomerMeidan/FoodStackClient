package javaFXControllers.Customer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import clientSide.CustomerPortalView;
import common.Logger;
import common.Logger.Level;
import common.Message;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

/** * CustomerWindow
 * 
 * This class is the javaFX controller for CustomerWindowTemplate.fxml
 * This class holds primaryStage, scene, view.
 *@author mosa
 *@version 3/1/2022
 */
@SuppressWarnings("unchecked")
public class CustomerWindow {

	private final static String FOLDER_NAME = "/images/";

	private Stage primaryStage;
	private Scene scene;
	private VBox homePageVBox;
	private CustomerPortalView view;

	private boolean w4cScanned;
	@FXML
	private Label userLabel;
	@FXML
	private Label welcomeLabel;
	@FXML
	private Label orderLabel;
	@FXML
	private Label w4cLabel;
	@FXML
	private Button orderButton;
	@FXML
	private Button viewOrderButton;
	@FXML
	private Button scanButton;
	@FXML
	private Button backButton;
	@FXML
	private ImageView homeIcon;
	@FXML
	private ImageView foodstackIcon;

	/**
	 * init
	 * 
	 * This method initializes the needed parameters for this controller.
	 * @param VBox homePageVBox
	 * @param Stage primaryStage
	 * @param CustomerPortalView view
	 */
	public void init(VBox homePageVBox, Stage primaryStage, CustomerPortalView view) {
		this.homePageVBox = homePageVBox;
		this.primaryStage = primaryStage;
		this.view = view;
		orderLabel.setVisible(false);
		w4cLabel.setVisible(false);
		w4cScanned = false;
		Image img = new Image(FOLDER_NAME + "Foodstack.jpg");
		foodstackIcon.setImage(img);
		Image img1 = new Image(FOLDER_NAME + "Home.jpg");
		homeIcon.setImage(img1);
		homeIcon.setBlendMode(BlendMode.LIGHTEN);
		scanButton.disableProperty().set(false);
	}

	/**
	 * Present an empty window of "Customer Window", and send a message to server side
	 * <p>
	 * Message sent as JSON, contains keys:<br>
	 * "command", value "home page is ready"
	 * "home page", "Customer"
	 */
	public void showWindow() {
		// log
		Logger.log(Level.INFO, "CustomerWindow: showing window");
		System.out.println("CustomerWindow: showing window");
		Platform.runLater(() -> {
			try {
				Scene scene = new Scene(homePageVBox);
				this.scene = scene;
			} catch (IllegalArgumentException e) {
				// log
				Logger.log(Level.WARNING, "CustomerWindow: exception in showWindow");
				System.out.println("CustomerWindow: exception in showWindow");
			}
			StringBuilder welcomeMessage = new StringBuilder();
			welcomeMessage.append("Welcome, ");
			welcomeMessage.append(view.getFirstName());
			welcomeMessage.append("!");
			welcomeLabel.setText(welcomeMessage.toString());
			primaryStage.setScene(scene);
			primaryStage.show();

			JSONObject json = new JSONObject();
			json.put("command", "home page is ready");
			json.put("home page", "Customer");
			view.ready(json);
		});
	}

	/**
	 * This method is called when clicking on "Order" button<br>
	 * If w4cScanned == true, it sends a message to server side  "Order button was clicked",
	 * else display feedback on the screen
	 * @param event
	 */
	@FXML
	public void onOrderButton(ActionEvent event) {
		if (w4cScanned) {
			sendToServer("Order button was clicked");
			// log
			Logger.log(Level.INFO, "CustomerWindow: Order button was clicked");
			System.out.println("CustomerWindow: Order button was clicked");
		} else
			orderLabel.setVisible(true);
	}

	/**
	 * This method is called when clicking on "View Order" button<br>
	 * Send a message to server side "View Order button was clicked"
	 */
	@FXML
	public void onViewOrderButton() {
		sendToServer("View Order button was clicked");
		// log
		Logger.log(Level.INFO, "CustomerWindow: View Order button was clicked");
		System.out.println("CustomerWindow:View Order button was clicked");
	}

	/**
	 * This method is called when clicking on "Scan W4C" button<br>
	 * Send a message to server side "Scan button was clicked"
	 */
	@FXML
	public void onScanButton() {
		sendToServer("Scan button was clicked");
	}

	/**
	 * This method is called from server side if scanning W4C code has succeeded.<br>
	 * disables the functionality of the button
	 */
	public void scanSuccess() {
		w4cScanned = true;
		orderLabel.setVisible(false);
		w4cLabel.setVisible(false);
		scanButton.disableProperty().set(true);
	}

	/**
	 * This method is called from server side if scanning W4C code has failed.<br>
	 * Show feedback to user to know that it failed using a label
	 */
	public void scanFail() {
		w4cLabel.setVisible(true);
	}

	/**
	 * This method is called when clicking on "Logout" button<br>
	 * Send a message to server side "Log out"
	 * @param event
	 */
	@FXML
	public void onLogoutButton(ActionEvent event) {
		sendToServer("Log out");
		// log
		Logger.log(Level.WARNING, "CustomerWindow: Logout button was clicked");
		System.out.println("CustomerWindow: Logout button was clicked");

	}

	/**Method to avoid repeating the same 3 lines of code whenever sending a message to server
	 * @param cmd
	 */
	public void sendToServer(String cmd) {
		JSONObject json = new JSONObject();
		json.put("command", cmd);
		view.ready(json);
	}

	/**This method is called from server side if an order is ready for the customer
	 * @param descriptor
	 * @author danielle
	 */
	public void orderReadyPopup(JSONObject descriptor) {
		Logger.log(Level.INFO, "CustomerWindow: orderReadyPopup: Showing popup");
		System.out.println("CustomerWindow: orderReadyPopup: Showing popup");
		Platform.runLater(() -> {
			sendToServer("Order is ready");
			Stage window = new Stage();
			window.initModality(Modality.APPLICATION_MODAL);
			window.setTitle("Message");
			window.setMinWidth(300);
			window.setMinHeight(100);
			String restaurantName = Message.getValueString(descriptor, "restaurantName");
			String customerID1 = Message.getValueString(descriptor, "customerID");
			VBox v = new VBox(5);
			int ordersNum = 0;
			JSONArray approvalOrders = (JSONArray) descriptor.get("approvalOrders");
			for (int i = 0; i < approvalOrders.size(); i++) {
				JSONObject json = (JSONObject) approvalOrders.get(i);

				String email = Message.getValueString(json, "email");
				String orderID = Message.getValueString(json, "orderID");
				String customerID2 = Message.getValueString(json, "customerID");
				if (customerID1.equals(customerID2)) {
					ordersNum++;
					if (ordersNum == 1) {
						Label to = new Label("To: " + email);
						
						to.setFont(Font.font("verdana", FontWeight.MEDIUM, FontPosture.REGULAR, 14));
						
						v.getChildren().add(to);

					}
					Label detiles = new Label("Order #" + orderID + " is ready ");
					
					detiles.setFont(Font.font("verdana", FontWeight.MEDIUM, FontPosture.REGULAR, 14));
					v.getChildren().add(detiles);
				

				}
			}
			Label from = new Label("From: " + restaurantName);
			from.setFont(Font.font("verdana", FontWeight.MEDIUM, FontPosture.REGULAR, 14));
			v.getChildren().add(from);
			Button exit = new Button("exit");
			exit.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					window.hide();
				}
			});
			v.getChildren().add(exit);
			VBox.setMargin(exit, new Insets(5, 0, 0, 270));
			Scene scene = new Scene(v);
			window.setScene(scene);
			window.showAndWait();
		});
	}

}
