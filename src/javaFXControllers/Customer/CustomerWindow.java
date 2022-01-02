package javaFXControllers.Customer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import clientSide.CustomerPortalView;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import util.Order;
import common.Logger;
import common.Message;
import common.Logger.Level;

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

	@FXML
	public void onOrderButton(ActionEvent event) {
		if (w4cScanned) {
			sendToController("Order button was clicked");
			// log
			Logger.log(Level.INFO, "CustomerWindow: Order button was clicked");
			System.out.println("CustomerWindow: Order button was clicked");
		} else
			orderLabel.setVisible(true);
	}

	@FXML
	public void onViewOrderButton() {
		sendToController("View Order button was clicked");
		// log
		Logger.log(Level.INFO, "CustomerWindow: View Order button was clicked");
		System.out.println("CustomerWindow:View Order button was clicked");
	}

	@FXML
	public void onScanButton() {
		sendToController("Scan button was clicked");
	}

	public void scanSuccess() {
		w4cScanned = true;
		orderLabel.setVisible(false);
		w4cLabel.setVisible(false);
		scanButton.disableProperty().set(true);
	}

	public void scanFail() {
		w4cLabel.setVisible(true);
	}

	@FXML
	public void onLogoutButton(ActionEvent event) {
		sendToController("Log out");
		// log
		Logger.log(Level.WARNING, "CustomerWindow: Logout button was clicked");
		System.out.println("CustomerWindow: Logout button was clicked");

	}

	public void sendToController(String cmd) {
		JSONObject json = new JSONObject();
		json.put("command", cmd);
		view.ready(json);
	}

	public void orderReadyPopup(JSONObject descriptor) {
		Logger.log(Level.INFO, "CustomerWindow: orderReadyPopup: Showing popup");
		System.out.println("CustomerWindow: orderReadyPopup: Showing popup");
		Platform.runLater(() -> {
			sendToController("Order is ready");
			Stage window = new Stage();
			window.initModality(Modality.APPLICATION_MODAL);
			window.setTitle("Message");
			window.setMinWidth(300);
			window.setMinHeight(100);
			String restaurantName = Message.getValue(descriptor, "restaurantName");
			String customerID1 = Message.getValue(descriptor, "customerID");
			VBox v = new VBox(5);
			int ordersNum = 0;
			JSONArray approvalOrders = (JSONArray) descriptor.get("approvalOrders");
			for (int i = 0; i < approvalOrders.size(); i++) {
				JSONObject json = (JSONObject) approvalOrders.get(i);

				String email = Message.getValue(json, "email");
				String orderID = Message.getValue(json, "orderID");
				String customerID2 = Message.getValue(json, "customerID");
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
