package javaFXControllers.supplier;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

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
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Functionality of update status action. The class include functionality of
 * present order details and approve orders.
 * 
 * @author Daniel Ohayon
 * @version 12/25/2021
 */
@SuppressWarnings("unchecked")
public class UpdateStatusWindow {

	@FXML
	private Button backButton;

	@FXML
	private Label supplierLabel;

	@FXML
	private VBox supplierVBox;
	@FXML
	private Label labelSide;

	private VBox supplierVBoxLoaded;
	private Stage primaryStage;
	private SupplierPortalView view;
	private Scene scene;
	private String userID;
	private HashMap<String, JSONObject> orders;
	private HashMap<String, VBox> details;
	
	private HashMap<String, Button> approvesB;
	private HashMap<String, HashMap<String, JSONObject>> orderByGroup;
	private int businessSelect;
	private int totalSelect;

	/**
	 * Initialize method. The method initialize the parameters and the values of
	 * this class
	 * 
	 * @param VBox updateMenuVBox - object of the all screen, Stage primaryStage,
	 *             SupplierPortalView view - expression the communication between
	 *             client - server
	 */
	public void init(VBox updateMenuVBox, Stage primaryStage, SupplierPortalView view) {
		this.supplierVBoxLoaded = updateMenuVBox;
		this.primaryStage = primaryStage;
		this.view = view;
		approvesB = new HashMap<>();
		details = new HashMap<>();
		
		orderByGroup = new HashMap<>();
		businessSelect = 0;
		totalSelect = 0;
	}

	/**
	 * Present empty screen of "update status" and notify to sever that this window
	 * is ready
	 * 
	 * @param ID of the supplier
	 * @see tamplate of "update status"
	 */
	public void showWindow(String userID) {
		// log
		Logger.log(Level.INFO, "UpdateStatusWindow: showing window");
		System.out.println("UpdateStatusWindow: showing window");

		Platform.runLater(() -> {
			try {
				Scene scene = new Scene(supplierVBoxLoaded);
				this.scene = scene;
			} catch (IllegalArgumentException e) {
				// log
				Logger.log(Level.WARNING, "UpdateStatusWindow: exception in showWindow");
				System.out.println("UpdateStatusWindow: exception in showWindow");
			}

			primaryStage.setScene(scene);
			primaryStage.show();
			this.userID = userID;
			JSONObject json = new JSONObject();
			json.put("command", "Order list presented is ready");
			json.put("supplierID", userID);
			view.ready(json);
		});
	}

	/**
	 * Private method that design a button.
	 * @param Button button - button to be designed , int height - height of button , int width - width of button, String colorBack - color of button 
	 */
	private void designButton(Button button, int height, int width, String colorBack) {
		button.setPrefHeight(height);
		button.setPrefWidth(width);
		button.setStyle("-fx-background-color: " + colorBack + ";");
		button.setFont(new Font("verdana", 12));
		button.setTextFill(Color.WHITE);
	}
	
	/**
	 * Add order list of this day(default year) to the "update status" window. the
	 * list presented according to the time of order request The method include
	 * functionality of select orders, preset order details and approve orders. On
	 * pressed "Approve" button, notify to server that this button was pressed. On
	 * pressed "Order details" button, notify to server that this button was
	 * pressed.
	 * 
	 * @param JSONObject descriptor - include HashMap of orders that sorted by the
	 *                   time.
	 * @see Order list of this day, sorted by the time.
	 */
	public void showOrderList(JSONObject descriptor) {
		Logger.log(Level.INFO, "UpdateStatusWindow: show Order List");
		System.out.println("UpdateStatusWindow: show Order List");
		Platform.runLater(() -> {
			supplierVBox.getChildren().clear();
			Date date1 = Calendar.getInstance().getTime();
			String dateStr1 = date1.toString();
			String[] dateStrSplit1 = dateStr1.split(" ");
			Label title = new Label("Order list of today - " + dateStrSplit1[1] + " " + dateStrSplit1[2] + ":");
			title.setFont(Font.font("verdana", FontWeight.MEDIUM, FontPosture.REGULAR, 25));
			supplierVBox.getChildren().add(title);
			VBox.setMargin(title, new Insets(0, 0, 20, 0));
			ListView l = new ListView();
			StringBuilder welcomeMessage = new StringBuilder();
			welcomeMessage.append("Welcome, ");
			welcomeMessage.append(view.getFirstName());
			welcomeMessage.append("!");
			labelSide.setText(welcomeMessage.toString());
			labelSide.setFont(new Font("verdana", 14));
			labelSide.setPrefWidth(300);
			String restaurantName = Message.getValueString(descriptor, "restaurantName");
			orderByGroup = (HashMap<String, HashMap<String, JSONObject>>) descriptor.get("orders");
			for (String hour : orderByGroup.keySet()) {
				Button approveB = new Button("Approve");
				approveB.disableProperty().set(true);
				ListView listView = new ListView();
				Label timeLabel = new Label("Delivery will arrive at " + hour + ":");
				timeLabel.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 14));
				timeLabel.setPrefWidth(300);
				orders = orderByGroup.get(hour);
				listView.getItems().add(timeLabel);
				JSONArray ordersPerHour = new JSONArray();
				int cnt = 0;
				businessSelect = 0;
				totalSelect = 0;
				ArrayList<String> approvalCustomer = new ArrayList<>();
				for (String id : orders.keySet()) {

					JSONObject order = orders.get(id);
					String employerID = Message.getValueString(order, "employerID");
					String total = Message.getValueString(order, "total");
					HBox h = new HBox();
					VBox v = new VBox();
					String status = Message.getValueString(order, "status");
					String customerID = Message.getValueString(order, "clientID");
					String recieveTimeS = Message.getValueString(order, "recieveTime");
					String[] recieveTimeSplit = recieveTimeS.split(":");
					String recieveTime = recieveTimeSplit[0] + ":" + recieveTimeSplit[1];
					if (!status.equals("Ready")) {
						cnt++;
						Label orderIDTxt = new Label("Order ID: " + id);
						orderIDTxt.setFont(Font.font("verdana", FontWeight.MEDIUM, FontPosture.REGULAR, 12));
						orderIDTxt.setPrefWidth(120);
						v.getChildren().add(orderIDTxt);
						VBox.setMargin(orderIDTxt, new Insets(10, 0, 10, 10));
						Label statusTxt = new Label("Order Status: " + status);
						statusTxt.setFont(Font.font("verdana", FontWeight.MEDIUM, FontPosture.REGULAR, 12));
						statusTxt.setPrefWidth(220);
						v.getChildren().add(statusTxt);
						VBox.setMargin(statusTxt, new Insets(10, 0, 10, 10));
						Label recieveTimeTxt = new Label("Due Time: " + recieveTime);
						recieveTimeTxt.setFont(Font.font("verdana", FontWeight.MEDIUM, FontPosture.REGULAR, 12));
						recieveTimeTxt.setPrefWidth(220);
						v.getChildren().add(recieveTimeTxt);
						VBox.setMargin(recieveTimeTxt, new Insets(10, 0, 10, 10));

						Label business;
						if (employerID != null)
							business = new Label("Business Customer: " + "V");
						else
							business = new Label("Business Customer: " + "X");
						business.setFont(Font.font("verdana", FontWeight.MEDIUM, FontPosture.REGULAR, 12));
						business.setPrefWidth(220);
						v.getChildren().add(business);
						VBox.setMargin(business, new Insets(10, 0, 10, 10));

						VBox forDetail = new VBox();
						Button orderDetails = new Button("Order Details");
						orderDetails.setOnAction(new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent event) {
								details.put(id, forDetail);
								orderDetails.disableProperty().set(true);
								JSONObject json = new JSONObject();
								json.put("command", "Order details button was pressed");
								json.put("orderID", id);
								json.put("hour", hour);
								view.ready(json);
							}
						});
						designButton(orderDetails, 35, 140, "#D93B48");
						forDetail.getChildren().add(orderDetails);
						h.getChildren().add(v);
						h.getChildren().add(forDetail);
						HBox.setMargin(forDetail, new Insets(10, 0, 0, 150));

						CheckBox cb = new CheckBox();

						cb.setOnAction(new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent event) {
								for (int i = 0; i < ordersPerHour.size(); i++) {
									JSONObject ob = (JSONObject) ordersPerHour.get(i);
									if (ob.get("orderID") == id)
										ordersPerHour.remove(ob);
								}
								JSONObject selectedOrder = new JSONObject();
								selectedOrder.put("orderID", id);
								selectedOrder.put("total", total);
								selectedOrder.put("employerID", employerID);
								selectedOrder.put("customerID", customerID);

								if (!cb.isSelected()) {
									approvalCustomer.remove(customerID);
									totalSelect--;
									if (employerID != null)
										businessSelect--;
									selectedOrder.put("selected", false);
									if (totalSelect == 0)
										approveB.disableProperty().set(true);
								} else {
									if(!approvalCustomer.contains(customerID))
										approvalCustomer.add(customerID);
									approveB.disableProperty().set(false);
									selectedOrder.put("selected", true);
									selectedOrder.put("email", Message.getValueString(order, "email"));
									totalSelect++;
									if (employerID != null)
										businessSelect++;
								}
								ordersPerHour.add(selectedOrder);

							}
						});

						h.getChildren().add(cb);
						HBox.setMargin(cb, new Insets(10, 0, 0, 150));
						approveB.setOnAction(new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent event) {
								String approvalNum = businessSelect + "";
								approvesB.put(hour, approveB);
								JSONObject json = new JSONObject();
								json.put("command", "Approve button was pressed");
								Date date = Calendar.getInstance().getTime();
								String dateStr = date.toString();
								String[] dateStrSplit = dateStr.split(" ");
								String dateS = dateStrSplit[1] + " " + dateStrSplit[2] + " " + dateStrSplit[3] + " "
										+ dateStrSplit[4] + " " + dateStrSplit[5];
								json.put("restaurantName", restaurantName);
								
							
								json.put("customersID", approvalCustomer);
								json.put("approveTime", dateS);
								json.put("supplierID", userID);
								json.put("ordersPerHour", ordersPerHour);
								json.put("approvalNum", approvalNum);
								checkApprove(json);
								
							}
						});
						approveB.setPrefHeight(30);
						approveB.setPrefWidth(120);
						h.setBorder(new Border(new BorderStroke(Color.PINK, BorderStrokeStyle.SOLID, null, null)));
						h.setStyle("-fx-background-color: #FFFAFA;");
						listView.getItems().add(h);
						listView.setPrefHeight(300);
					}
				}
				if (cnt != 0) {
					HBox forApprove = new HBox();
					forApprove.getChildren().add(approveB);
					HBox.setMargin(approveB, new Insets(3, 0, 0, 570));
					listView.getItems().add(forApprove);

					l.getItems().add(listView);
				}
				backButton.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						view.getSupplierWindow().showWindow();
					}
				});

			}
			supplierVBox.getChildren().add(l);
			backButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					view.getSupplierWindow().showWindow();
				}
			});
		});
	}
	/**
	 * Private method that build popUp that will contain VBox.The method include
	 * functionality of submit the approval On press submit button --> final
	 * approval
	 * 
	 * @param VBox approvalDetails - contain the details, JSONObject json - the json
	 *             that builded for send to server
	 * @see Window with approval details.
	 */
	public void showPopup(VBox approvalDetails, JSONObject json) {
		Platform.runLater(() -> {
			Stage window = new Stage();
			
			window.initModality(Modality.APPLICATION_MODAL);
			window.setTitle("Approval Order");
			window.setMinWidth(300);
			window.setMinHeight(40);

			VBox layout = new VBox();
			layout.getChildren().add(approvalDetails);
			layout.setAlignment(Pos.CENTER);
			Button submit = new Button("Submit");
			submit.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					window.hide();
					view.ready(json);
				}
			});
			layout.getChildren().add(submit);
			Scene scene = new Scene(layout);
			window.setScene(scene);
			window.show();
		});
	}

	/**
	 * Private method that build VBox that will contain approval details.
	 * 
	 * @param JSONObject json - the json that builded for send to server
	 */
	private void checkApprove(JSONObject json) {
		JSONArray ordersPerHour = (JSONArray) json.get("ordersPerHour");
		String approvalNumS = Message.getValueString(json, "approvalNum");
		int approvalNum = Integer.parseInt(approvalNumS);
		VBox approvalDetails = new VBox();
		for (int i = 0; i < ordersPerHour.size(); i++) {
			JSONObject order = (JSONObject) ordersPerHour.get(i);
			String orderID = Message.getValueString(order, "orderID");
			String employerID = Message.getValueString(order, "employerID");
			boolean selected = (boolean) order.get("selected");
			if (selected) {
				Label orderIDTxt = new Label("Order ID: " + orderID);
				orderIDTxt.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 12));
				orderIDTxt.setPrefWidth(120);
				approvalDetails.getChildren().add(orderIDTxt);
				Label deliveryType;
				if (approvalNum > 1 && employerID != null)
					deliveryType = new Label("Delivery Type: " + "Co - Delivery");
				else
					deliveryType = new Label("Delivery Type: " + "Re - Delivery");
				deliveryType.setFont(Font.font("verdana", FontWeight.MEDIUM, FontPosture.REGULAR, 12));
				deliveryType.setPrefWidth(300);
				approvalDetails.getChildren().add(deliveryType);
			}

		}

		showPopup(approvalDetails, json);
	}

	/**
	 * Remove approval orders from order list and show updated list.
	 * 
	 * @param JSONObject descriptor - include JSONObject to be parameter of
	 *                   "showOrderList" method
	 * @see Updated order list of this day, sorted by the time
	 */
	public void afterPressApproveButton(JSONObject descriptor) {
		Logger.log(Level.INFO, "UpdateStatusWindow: afterPressApproveButton");
		System.out.println("UpdateStatusWindow: afterPressApproveButton");

		JSONObject list = (JSONObject) descriptor.get("orders");
		showOrderList(list);

	}

	public void afterPressRemoveButton(JSONObject descriptor) {
		Logger.log(Level.INFO, "UpdateStatusWindow: afterPressRemoveButton");
		System.out.println("UpdateStatusWindow: afterPressRemoveButton");
		JSONObject list = (JSONObject) descriptor.get("orders");

		showOrderList(list);

	}

	/**
	 * Present order details of specified order. The method include functionality of
	 * exit order detail. On pressed "exit" button the window of order details will
	 * closed.
	 * 
	 * @param JSONObject descriptor - include ID of the order and the time(Key of
	 *                   hashMap).
	 * @see Order details in small window on "update status" screen.
	 */
	public void afterPressOrderDetails(JSONObject descriptor) {
		Logger.log(Level.INFO, "UpdateStatusWindow: afterPressOrderDetails");
		System.out.println("UpdateStatusWindow: afterPressOrderDetails");
		String id = Message.getValueString(descriptor, "orderID");
		String hour = Message.getValueString(descriptor, "hour");
		Platform.runLater(() -> {
			orders = orderByGroup.get(hour);
			JSONObject order = orders.get(id);
			JSONArray arrOrders = (JSONArray) order.get("arrOrders");
			VBox v = details.get(id);
			ScrollPane s = new ScrollPane();
			VBox sv = new VBox();
			for (int i = 0; i < arrOrders.size(); i++) {
				JSONObject json = (JSONObject) arrOrders.get(i);

				String itemType = Message.getValueString(json, "itemType");
				Label itemTypeL = new Label("Type: " + itemType);
				sv.getChildren().add(itemTypeL);
				String itemName = Message.getValueString(json, "itemName");
				Label itemNameL = new Label("Meal: " + itemName);
				sv.getChildren().add(itemNameL);
				if (Message.getValueString(json, "mustFeature") != null) {
					Label must = new Label("Must feature: " + Message.getValueString(json, "mustFeature"));
					sv.getChildren().add(must);
				}
				ArrayList<String> opName = (ArrayList<String>) json.get("optionalNames");
				if (opName != null) {
					Label optional = new Label("Optional features:");
					sv.getChildren().add(optional);
					for (String op : opName)
						sv.getChildren().add(new Label(op));

				}
				s.setContent(sv);
			}
			v.getChildren().add(s);
			VBox.setMargin(s, new Insets(5, 0, 0, 0));
			Button exit = new Button("exit");
			exit.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					v.getChildren().get(0).disableProperty().set(false);
					v.getChildren().remove(s);
					v.getChildren().remove(exit);
				}
			});
			v.getChildren().add(exit);
			VBox.setMargin(exit, new Insets(5, 0, 0, 20));
		});
	}

	/**
	 * This method called after the customer clicked on "confirm order".
	 * The method sends a notify to server to run the updated query of orders table .
	*/
	public void alertSupplier(JSONObject descriptor) {
		JSONObject json = new JSONObject();
		JSONObject order = (JSONObject) descriptor.get("orderDetails");
		json.put("command",  "Order list presented is ready");
		json.put("supplierID", userID);
		view.ready(json);
	}

}
