package javaFXControllers.Customer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import clientSide.CustomerPortalView;
import common.Logger;
import common.Logger.Level;
import common.Meal;
import common.Message;
import common.OptionalFeature;
import common.Order;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/** * ViewOrderWindow
 * 
 * This class is the javaFX controller for ViewOrderWindow.fxml
 * This class holds primaryStage, scene, view.
 *@author mosa
 *@version 3/1/2022
 */
@SuppressWarnings("unchecked")
public class ViewOrderWindow {

	private final static String FOLDER_NAME = "/images/";

	private Stage primaryStage;
	private Scene scene;
	private HBox viewOrderHBox;
	private CustomerPortalView view;
	JSONObject orders;

	@FXML
	private VBox pathVBox;

	@FXML
	private ImageView foodStackIcon;

	@FXML
	private Pane homePane;

	@FXML
	private ImageView homeIcon;

	@FXML
	private Pane restaurantsPane;

	@FXML
	private ImageView restaurantIcon;

	@FXML
	private Pane mealsPane;

	@FXML
	private ImageView restaurantIcon1;

	@FXML
	private Pane deliveryPane;

	@FXML
	private ImageView deliveryIcon;

	@FXML
	private Pane paymentPane;

	@FXML
	private ImageView paymentIcon;

	@FXML
	private Button backButton;

	@FXML
	private Button viewButton;

	@FXML
	private Button approveButton;

	@FXML
	private VBox restaurantVBoxList;

	@FXML
	private TableView<Order> tableView;

	@FXML
	private TableColumn<Order, Long> orderIDColumn;
	@FXML
	private TableColumn<Order, String> restaurantNameColumn;
	@FXML
	private TableColumn<Order, String> statusColumn;
	@FXML
	private TableColumn<Order, String> orderTimeColumn;
	@FXML
	private TableColumn<Order, String> orderDueColumn;
	@FXML
	private TableColumn<Order, Integer> totalColumn;

	/**
	 * init
	 * 
	 * This method initializes the needed parameters for this controller.
	 * @param HBox homePageHBox
	 * @param Stage primaryStage
	 * @param CustomerPortalView view
	 */
	public void init(HBox homePageHBox, Stage primaryStage, CustomerPortalView view) {
		this.viewOrderHBox = homePageHBox;
		this.primaryStage = primaryStage;
		this.view = view;
		Image img = new Image(FOLDER_NAME + "Foodstack.jpg");
		foodStackIcon.setImage(img);
		Image img1 = new Image(FOLDER_NAME + "Home.jpg");
		homeIcon.setImage(img1);
		homeIcon.setBlendMode(BlendMode.LIGHTEN);
		orders = new JSONObject();
		approveButton.disableProperty().set(true);

		// set what each column is supposed to show in the TableView
		orderIDColumn.setCellValueFactory(new PropertyValueFactory<Order, Long>("orderID"));
		restaurantNameColumn.setCellValueFactory(new PropertyValueFactory<Order, String>("restaurantName"));
		statusColumn.setCellValueFactory(new PropertyValueFactory<Order, String>("status"));
		orderTimeColumn.setCellValueFactory(new PropertyValueFactory<Order, String>("orderTime"));
		orderDueColumn.setCellValueFactory(new PropertyValueFactory<Order, String>("dueTime"));
		totalColumn.setCellValueFactory(new PropertyValueFactory<Order, Integer>("total"));
		//

		// add a listener to each row that enables clicking on the button "Approve
		// Reception" and "View Order Details",  or
		// disables it depending on the status of the order
		tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			Order order = tableView.getSelectionModel().getSelectedItem();
			viewButton.disableProperty().set(false); //add this
			String status;
			if (order != null) {
				status = order.getStatus();
				if (status.equals("Ready") || status.equals("Delivered")) {
					approveButton.disableProperty().set(false);
				} else {
					approveButton.disableProperty().set(true);
				}
			}

		});
		//
	}

	/**
	 * Present an empty window of "Customer Window", and send a message to server side
	 * <p>
	 * Message sent as JSON, contains keys:<br>
	 * "command", value "View Order window is displayed"
	 */
	public void showWindow() {
		// log
		Logger.log(Level.INFO, "ViewOrderWindow: showing window");
		System.out.println("ViewOrderWindow: showing window");
		Platform.runLater(() -> {
			try {
				if(scene == null) {
				Scene scene = new Scene(viewOrderHBox);
				this.scene = scene;
				}
			} catch (IllegalArgumentException e) {
				// log
				Logger.log(Level.WARNING, "ViewOrderWindow: exception in showWindow");
				System.out.println("ViewOrderWindow: exception in showWindow");
			}
			tableView.getSelectionModel().clearSelection(); //add this
			viewButton.disableProperty().set(true); //add this
			primaryStage.setScene(scene);
			primaryStage.show();
			
			sendToServer("View Order window is displayed");
		});
	}
	
	public void refreshWindowIfShowing(JSONObject orders) {
		if(primaryStage.isShowing())
			showOrders(orders);
	}

	/**
	 * load the orders into the TableView
	 * 
	 * @param orders
	 */
	public void showOrders(JSONObject orders) {
		Platform.runLater(() -> {
			if (orders == null) {
				tableView.setPlaceholder(new Label("No orders to display"));
				Logger.log(Level.WARNING, "ViewOrderWindow: No orders to display");
				System.out.println("ViewOrderWindow: No orders to display");
			} else {
				tableView.setItems(getOrders(orders));
				Logger.log(Level.WARNING, "ViewOrderWindow: Showing orders");
				System.out.println("ViewOrderWindow: Showing orders");
			}
		});

	}

	/**
	 * get the orders from the JSON and build an ObservableList so we can add it to
	 * the TableView
	 * 
	 * @param orders
	 * @return ObservableList<Order> containing all the information about the orders
	 */
	private ObservableList<Order> getOrders(JSONObject orders) {
		ObservableList<Order> orderList = FXCollections.observableArrayList();
		JSONArray ordersJArray = Message.getValueJArray(orders, "orders");
		if (ordersJArray != null) {
			int size = ordersJArray.size();
			for (int i = 0; i < size; i++) {
				JSONObject orderJ = (JSONObject) ordersJArray.get(i);
				Order order = Order.fromJSONObject(orderJ);
				orderList.add(order);
			}
		}
		return orderList;
	}

	/**
	 * Method called when View Order Details button clicked<br>
	 * Send message to server "View button clicked"
	 */
	@FXML
	public void onViewButton() {
		Order order = tableView.getSelectionModel().getSelectedItem();
		Long orderID = order.getOrderID();
		JSONObject json = new JSONObject();
		Logger.log(Level.WARNING, "ViewOrderWindow: View button clicked");
		System.out.println("ViewOrderWindow: View button clicked");
		json.put("orderID", orderID);
		json.put("command", "View button clicked");
		view.ready(json);
	}

	/**
	 * popup window for displaying a selected order's information (meals, delivery
	 * type etc)
	 */
	public void displayOrderInformation() {
		Order order = tableView.getSelectionModel().getSelectedItem();
		Platform.runLater(() -> {
			boolean discountAvailable = false;
			Stage window = new Stage();
			window.initModality(Modality.APPLICATION_MODAL);
			window.setTitle("Order Details");
			window.setMinWidth(300);
			window.setMinHeight(200);
			window.setWidth(500);
			Label label = new Label("Order " + order.getOrderID());
			Button b = new Button("Back");
			b.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					window.hide();
				}

			});
			VBox layout = new VBox(5);
			layout.getChildren().add(label);
			layout.setAlignment(Pos.CENTER);
			layout.getChildren().add(createListViewOfOrder(order));
			int mealsListCost = order.getMealsListCost();
			layout.getChildren().add(new Label("Cost of items: " + mealsListCost + " INS"));
	
			layout.getChildren()
					.add(new Label("Pick up type: " + order.getPickUpType() + " +" + order.getPickUpCost() + " INS"));
			if (order.checkEarlyBooking()) {
				Label earlyBookingLabel = new Label("Early booking discount: 10%");
				layout.getChildren().add(earlyBookingLabel);
				discountAvailable = true;
			}
			if (!discountAvailable)
				layout.getChildren().add(new Label("No discount or coupon used"));
			layout.getChildren().add(new Label("Total cost: " + order.getTotal() + " INS"));
			layout.getChildren().add(b);

			Scene scene = new Scene(layout);
			window.setScene(scene);
			Logger.log(Level.WARNING, "ViewOrderWindow: Displaying order information");
			System.out.println("ViewOrderWindow: Displaying order information");
			window.showAndWait();
		});
	}

	/**create ListView using labels for the order
	 * @param order
	 * @return
	 */
	public ListView<Label> createListViewOfOrder(Order order) {
		ListView<Label> lv = new ListView<>();
		ArrayList<Meal> mealList = order.getMeals();
		for (Meal meal : mealList) {
			StringBuilder sb = new StringBuilder();
			sb.append(meal.getName());
			sb.append(" (+");
			sb.append(meal.getPrice());
			sb.append(" INS), ");
			if (meal.getMustFeature() != null && meal.getMustFeature().getName() != null) {
				if (meal.getMustFeature().getPrice() != -1) {
					sb.append(meal.getMustFeature().getName());
					sb.append(" (+");
					sb.append(meal.getMustFeature().getPrice());
					sb.append(" INS), ");
				}	
			}
			if (meal.getOptionalFeatureList() != null)
				for (OptionalFeature o : meal.getOptionalFeatureList()) {
					sb.append(o.getName());
					sb.append(" (+");
					sb.append(o.getPrice());
					sb.append(" INS), ");
				}
			sb.delete(sb.length() - 2, 100);
			sb.append(". Total: ");
			sb.append(order.getMealCost(meal));
			sb.append(" INS");
			Label l = new Label(sb.toString());
			lv.getItems().add(l);
		}
		return lv;
	}

	@FXML
	public void onApproveButton() {
		Order order = tableView.getSelectionModel().getSelectedItem();
		Date date = Calendar.getInstance().getTime();
		JSONObject json = new JSONObject();
		Logger.log(Level.WARNING, "ViewOrderWindow: Approve Reception button clicked");
		System.out.println("ViewOrderWindow: Approve Reception button clicked");
		json.put("orderID", order.getOrderID());
		json.put("deliverDate", date.toString().substring(4));
		json.put("totalCost", order.getTotal());
		json.put("dueDate", order.getDueTime());
		json.put("supplierID", order.getSupplierID());
		json.put("earlyBooking", order.checkEarlyBooking());
		json.put("command", "Approve Reception button clicked");
		view.ready(json);
	}

	/**
	 * Show the customer window
	 */
	@FXML
	public void onBackButton() {
		Logger.log(Level.WARNING, "ViewOrderWindow: Back button was clicked");
		System.out.println("ViewOrderWindow: Back button was clicked");
		view.getCustomerWindow().showWindow();
	}

	/**
	 * show a pop up that user successfuly approved order delivery
	 * @param msg
	 */
	public void showPopupSuccess(String msg) {
		Platform.runLater(() -> {
			Stage window = new Stage();
			window.initModality(Modality.APPLICATION_MODAL);
			window.setTitle("Success Window");
			window.setMinWidth(300);
			window.setMinHeight(100);

			Label label = new Label(msg);
			Button b = new Button("OK");
			b.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					window.hide();
				}

			});
			VBox layout = new VBox(10);
			layout.getChildren().addAll(label, b);
			layout.setAlignment(Pos.CENTER);

			Scene scene = new Scene(layout);
			window.setScene(scene);
			window.showAndWait();
			Order order = tableView.getSelectionModel().getSelectedItem();
			order.setStatus("Delivered");
			int index = tableView.getSelectionModel().getSelectedIndex();	
			tableView.getItems().remove(index);
			if(tableView.getItems().isEmpty()) {
				tableView.getSelectionModel().clearSelection();
				viewButton.setDisable(true);
				approveButton.setDisable(true);
			}
		});
	}
	
	/**Method to avoid repeating the same 3 lines of code whenever sending a message to server
	 * @param cmd
	 */
	public void sendToServer(String cmd) {
		JSONObject json = new JSONObject();
		json.put("command", cmd);
		view.ready(json);
	}
}
