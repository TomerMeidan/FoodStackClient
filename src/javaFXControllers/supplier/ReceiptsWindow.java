package javaFXControllers.supplier;

import java.util.Calendar;
import java.util.Date;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import clientSide.SupplierPortalView;
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
import javafx.scene.control.ListView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
//
public class ReceiptsWindow {
	private Stage primaryStage;
	private Scene scene;
	private VBox supplierVBoxLoaded;
	private SupplierPortalView view;
	private String userID;
	@FXML
	private Button BackButton;

	@FXML
	private Label supplierLabel;

	@FXML
	private VBox supplierVBox;
	@FXML
	private Label labelSide;
	
	/**
	 * init
	 * 
	 * Initialize method. The method initialize the parameters and the values of
	 * this class
	 * 
	 * @param VBox supplierVBoxT - object of the all screen, Stage primaryStage,
	 *             SupplierPortalView view - expression the communication between
	 *             client - server
	 */
	@SuppressWarnings("unchecked")
	public void init(VBox supplierVBoxTry, Stage primaryStage, SupplierPortalView view) {
		this.supplierVBoxLoaded = supplierVBoxTry;
		this.primaryStage = primaryStage;
		this.view = view;

	}

	/**
	 * showWindow
	 * 
	 * Present empty screen of "Receipts" and notify to sever that this window
	 * is ready
	 * 
	 * @param ID of the supplier
	 * @see tamplate of "Receipts"
	 */
	public void showWindow(String userID) {
		// log
		Logger.log(Level.INFO, "ReceiptsWindow: showing window");
		System.out.println("ReceiptsWindow: showing window");

		Platform.runLater(() -> {
			try {
				Scene scene = new Scene(supplierVBoxLoaded);
				this.scene = scene;
			} catch (IllegalArgumentException e) {

				Logger.log(Level.WARNING, "ReceiptsWindow: exception in showWindow");
				System.out.println("ReceiptsWindow: exception in showWindow");
			}
			StringBuilder welcomeMessage = new StringBuilder();
			welcomeMessage.append("Welcome, ");
			welcomeMessage.append(view.getFirstName());
			welcomeMessage.append("!");
			labelSide.setText(welcomeMessage.toString());
			labelSide.setFont(new Font("verdana", 14));
			primaryStage.setScene(scene);
			primaryStage.show();
			this.userID = userID;
			JSONObject json = new JSONObject();
			json.put("command", "Receipts list presented is ready");
			json.put("supplierID", userID);
			view.ready(json);
		});

	}
	/**
	 * clickOnBackButton
	 * 
	 * This method called when 'Event' occurred to 'Back' button.
	 * This method sends to server event if 'Back' happened.
	 * @param ActionEvent event.
	 */
	@FXML
	void clickOnBackButton(ActionEvent event) {

		BackButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				view.getSupplierWindow().showWindow();
			}
		});
	}
	/**
	 * showReceipt 
	 * 
	 * Add receipt list of this month(default year) to the "Receipts". the
	 * list presented bills after fees.
	 * @param JSONObject descriptor - include JSONArray of receipts list.
	 * @see Receipts list of this month.
	 */
	public void showReceipt(JSONObject descriptor) {
		JSONArray receipts = (JSONArray) descriptor.get("receipts");
		Platform.runLater(() -> {
			int sum = 0;
			supplierVBox.getChildren().clear();
			Date date1 = Calendar.getInstance().getTime();
			String dateStr1 = date1.toString();
			String[] dateStrSplit1 = dateStr1.split(" ");
			Label title = new Label("Receipt of month - " + dateStrSplit1[1] + ":");
			title.setFont(Font.font("verdana", FontWeight.MEDIUM, FontPosture.REGULAR, 25));
			supplierVBox.getChildren().add(title);
			VBox.setMargin(title, new Insets(0, 0, 20, 0));
			ListView listView = new ListView();

			for (int i = 0; i < receipts.size(); i++) {
				VBox v = new VBox();
				JSONObject receipt = (JSONObject) receipts.get(i);
				String date = Message.getValueString(receipt, "date");
				Label tim = new Label(date);
				tim.setFont(new Font("verdana", 14));
				v.getChildren().add(tim);
				String price = Message.getValueString(receipt, "price");
				int priI = Integer.parseInt(price);
				priI = (int) (priI * 1.07);
				sum += priI;
				Label pri = new Label(price + " NIS and after fee from company " + priI + " NIS");
				pri.setFont(new Font("verdana", 14));
				v.getChildren().add(pri);
				v.setBorder(new Border(new BorderStroke(Color.PINK, BorderStrokeStyle.SOLID, null, null)));
				listView.getItems().add(v);

			}
			supplierVBox.getChildren().add(listView);
			if (receipts.size() > 0) {
				Label totalSum = new Label("Total Sum: " + sum);
				supplierVBox.getChildren().add(totalSum);
			}
		});

	}
}
