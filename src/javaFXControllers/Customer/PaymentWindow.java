package javaFXControllers.Customer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import clientSide.CustomerPortalView;
import clientSide.SupplierPortalView;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import util.DateParser;
import util.Logger;
import util.Meal;
import util.Message;
import util.OptionalFeature;
import util.Order;
import util.Logger.Level;

/**
 *  PaymentWindow
 * 
 * This class is the javaFX controller for PaymentWindowTemplate.fxml This class
 * holds primaryStage, scene, view.
 * 
 * @author mosa
 * @version 3/1/2022
 */
@SuppressWarnings("unchecked")
public class PaymentWindow {
	private final static String FOLDER_NAME = "/images/";
	private Stage primaryStage;
	private Scene scene;
	private HBox paymentHBox;
	private CustomerPortalView view;
	private JSONObject order;
	int finalCost; //cost of the order after discount
	boolean businessCustomer;
	int refBalance; 
	int leftToPay; // how much the customer has to pay after using refund balance

	@FXML
	private Label userLabel;

	@FXML
	private VBox pathVBox;

	@FXML
	private ImageView foodStackIcon;

	@FXML
	private ImageView homeIcon;

	@FXML
	private ImageView restaurantIcon;

	@FXML
	private ImageView mealIcon;

	@FXML
	private ImageView deliveryIcon;

	@FXML
	private ImageView paymentIcon;

	@FXML
	private Button backButton;

	@FXML
	private CheckBox balanceCB;

	@FXML
	private Label titleLabel;

	@FXML
	private HBox mHbox;

	@FXML
	private RadioButton busRB;

	@FXML
	private RadioButton regRB;

	@FXML
	private VBox regularVBox;

	@FXML
	private HBox questionHBox;

	@FXML
	private ImageView regularImage;

	@FXML
	private TextField ownerTxt;

	@FXML
	private TextField cardTxt;

	@FXML
	private TextField emailTxt;

	@FXML
	private VBox businessVBox;

	@FXML
	private TextField codeTxt;

	@FXML
	private ImageView businessImage;

	@FXML
	private TextField w4cTxt;

	@FXML
	private TextField empNameTxt;

	@FXML
	private VBox orderDetails;

	@FXML
	private Button confirmButton;

	@FXML
	private Label refBalanceLbl;

	
	/**
	 * init
	 * 
	 * This method initializes the needed parameters for this controller.
	 * @param VBox paymentHBox
	 * @param Stage primaryStage
	 * @param CustomerPortalView view
	 */
	public void init(HBox paymentHBox, Stage primaryStage, CustomerPortalView view) {
		this.paymentHBox = paymentHBox;
		this.primaryStage = primaryStage;
		this.view = view;
		ToggleGroup group = new ToggleGroup();
		busRB.setToggleGroup(group);
		regRB.setToggleGroup(group);
		userLabel.setText("Welcome, "+view.getFirstName());

		/////// load icons
		Image img = new Image(FOLDER_NAME + "Foodstack.jpg");
		foodStackIcon.setImage(img);
		img = new Image(FOLDER_NAME + "Home.jpg");
		homeIcon.setImage(img);
		homeIcon.setBlendMode(BlendMode.LIGHTEN);
		img = new Image(FOLDER_NAME + "Restaurant.jpg");
		restaurantIcon.setImage(img);
		restaurantIcon.setBlendMode(BlendMode.LIGHTEN);
		img = new Image(FOLDER_NAME + "Meal.png");
		mealIcon.setImage(img);
		mealIcon.setBlendMode(BlendMode.LIGHTEN);
		img = new Image(FOLDER_NAME + "Delivery1.jpg");
		deliveryIcon.setImage(img);
		deliveryIcon.setBlendMode(BlendMode.LIGHTEN);
		img = new Image(FOLDER_NAME + "Payment.png");
		paymentIcon.setImage(img);
		paymentIcon.setBlendMode(BlendMode.LIGHTEN);
		img = new Image(FOLDER_NAME + "RegularAcc.jpg");
		regularImage.setImage(img);
		img = new Image(FOLDER_NAME + "BusinessAcc.jpg");
		businessImage.setImage(img);
		////////
	}


	/**
	 * Present an empty window of "Payment Window", and send a message to controller
	 * <p>
	 * Message sent as JSON, contains keys:<br>
	 * "command", value "Payment window is displayed"
	 * @param order
	 */
	public void showWindow(JSONObject order) {
		this.order = order;
		Platform.runLater(() -> {
			try {
				if (scene == null) {
					Scene scene = new Scene(paymentHBox);
					this.scene = scene;
				}
			} catch (IllegalArgumentException e) {
				// log
				Logger.log(Level.WARNING, "PaymentWindow: exception in showWindow");
				System.out.println("PaymentWindow: exception in showWindow");
			}
			refBalance = 0;
			titleLabel.setVisible(true);
			primaryStage.setScene(scene);
			primaryStage.show();
			w4cTxt.disableProperty().set(true);
			empNameTxt.disableProperty().set(true);
			ownerTxt.disableProperty().set(true);
			cardTxt.disableProperty().set(true);
			emailTxt.disableProperty().set(true);
			ownerTxt.setText(view.getID());
			cardTxt.setText(view.getCreditNumber());
			emailTxt.setText(view.getEmail());
			if (Message.getValueString(order, "employerID") == null) {
				businessVBox.disableProperty().set(true);
				businessVBox.setVisible(false);
				businessCustomer = false;
				regRB.setSelected(true);
			} else {
				w4cTxt.setText(Message.getValueString(order, "employerW4C"));
				empNameTxt.setText(Message.getValueString(order, "employerName"));
				businessCustomer = true;
				busRB.setSelected(true);
			}
			JSONObject json = new JSONObject();
			balanceCB.setSelected(false);
			json.put("supplierID", Message.getValueString(order, "supplierID"));
			json.put("command", "Payment window is displayed");
			view.ready(json);
			//Logger.log(Level.INFO, "PaymentWindow: showing window");
			System.out.println("PaymentWindow: showing window");
		});
		
	}

	/**
	 * displays the order details in the window order needs to contain key:
	 * "refundBalance"
	 * 
	 * @param order
	 */
	public void showPaymentOptions(JSONObject json) {
		Order o = Order.fromJSONObject(order);
		Platform.runLater(() -> {
			if (!(orderDetails.getChildren().isEmpty()))
				orderDetails.getChildren().clear();
			String bal = Message.getValueString(json, "refundBalance");
			if (bal == null)
				refBalance = 0;
			else
				refBalance = Integer.valueOf(bal);
			refBalanceLbl.setText("Refund Balance: " + refBalance);
			int totalItemsPrice = 0;
			JSONArray mealsList = (JSONArray) order.get("mealsJArray");
			if (mealsList == null)
				return;
			Integer mealsListSize = mealsList.size();
			Label numberOfMeals = new Label("Your Order (" + mealsListSize.toString() + " items): ");
			boolean earlyBookingFlag = false;
			boolean deliveryFlag = false;
			if (Message.getValueString(order, "pickUpType").equals("Delivery"))
				deliveryFlag = true;
			orderDetails.getChildren().add(numberOfMeals);
			orderDetails.getChildren().add(createListViewOfOrder(o));
			totalItemsPrice = o.getMealsListCost();
			finalCost = totalItemsPrice;
			if (deliveryFlag)
				finalCost += 25;
			if (checkIfEarlyBooking(Message.getValueString(order, "dueDate"))) {
				finalCost = (int) (finalCost * 0.9);
				order.put("earlyBooking", "True");
				earlyBookingFlag = true;
			} else
				order.put("earlyBooking", "False");

			Label finalPriceLbl = new Label("Total cost: " + finalCost + " INS ");
			Label itemsPriceLbl = new Label("Items cost: " + totalItemsPrice + " INS");
			orderDetails.getChildren().add(itemsPriceLbl);
			if (deliveryFlag)
				orderDetails.getChildren().add(new Label("Delivery selected: +25 INS"));
			if (earlyBookingFlag)
				orderDetails.getChildren().add(new Label("Early booking discount: 10%"));
			leftToPay = finalCost;
			orderDetails.getChildren().add(finalPriceLbl);
			balanceCB.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					if (balanceCB.isSelected()) {
						if (refBalance - finalCost <= 0) {
							leftToPay = finalCost - refBalance;
							orderDetails.getChildren()
									.add(new Label("(Using Balance Refund: " + refBalance + ", you will pay "+leftToPay+")"));
							refBalance = 0;
						} else {
							leftToPay = 0;
							refBalance = refBalance - finalCost;
							orderDetails.getChildren().add(new Label(
									"(Using Balance Refund: " + (refBalance+finalCost) + ", " + (refBalance) + " will remain)"));
						}
					} else {
						leftToPay = finalCost;
						ObservableList<Node> l = orderDetails.getChildren();
						orderDetails.getChildren().remove(l.size() - 1);
					}
				}

			});
			busRB.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					confirmButton.disableProperty().set(false);
				}

			});
			regRB.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					confirmButton.disableProperty().set(false);
				}

			});
			sendToServer("Showing payment options");
		});

	}

	/**Method to build a list view of the selected meals 
	 * @param order
	 * @return List View of the selected meals in the order
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

	/**
	 * Method called when a payment method (radio button) is chosen to enable confirm button
	 */
	@FXML
	public void chosePaymentMethod() {
		confirmButton.disableProperty().set(false);
	}

	/**
	 * Method called when "Confirm" button is clicked<br>
	 * Send message to server side as JSONObject, keys:<br>
	 * "order", has value JSONObject containing all the necessary information about that the order
	 * "command", "Confirm button was clicked"
	 */
	@FXML
	public void clickConfirmButton() {
		JSONObject json = new JSONObject();
		Date date = Calendar.getInstance().getTime();
		boolean refundBalanceFlag = false;
		this.order.put("totalPrice", finalCost);
		order.put("orderTime", date.toString().substring(4));
//		need to check if payment is successful?
		if (balanceCB.isSelected()) {
			refundBalanceFlag = true;
			order.put("refundBalance", refBalance);
		}
		order.put("refundBalanceUsed", refundBalanceFlag);
		order.put("leftToPay", leftToPay);
		// payment type
		if (regRB.isSelected())
			order.put("paymentType", "Regular");
		else
			order.put("paymentType", "Business");

		json.put("order", order);
		json.put("command", "Confirm button was clicked");
		System.out.println("PaymentWindow: Confirm button was clicked");
		view.ready(json);

	}

	public boolean checkEmail() {
		String email = emailTxt.getText();
		Pattern pattern = Pattern.compile("^(.+)@(.+)$");
		Matcher matcher = pattern.matcher(email);
		if (!(matcher.matches()) || !(email.contains(".com"))) {
			System.out.println("Please enter a valid email address!");
			return false;
		}
		return true;
	}

	public boolean checkIfEarlyBooking(String dueDate) {
		Date date = Calendar.getInstance().getTime();
		String today = date.toString().substring(4);
		if (!checkIfDifferencePassed(today, dueDate, 2*60))
			return true;
		return false;
	}

	/**
	 * Method called when "Back" button is clicked<br>
	 * Shows Delivery Window
	 */
	@FXML
	public void onBackButton() {
		view.getOrderWindow().showWindow();
		view.getOrderWindow().showDeliveryWindow();
	}

	public void sendToServer(String cmd) {
		JSONObject json = new JSONObject();
		json.put("command", cmd);
		view.ready(json);
	}

	/**
	 * When order is successfuly processed, build a new GUI to display<br>
	 * Contain button to go back to homepage<br>
	 */
	public void showSuccessWindow() {
		Platform.runLater(() -> {
			backButton.disableProperty().set(true);
			mHbox.getChildren().clear();
			titleLabel.setVisible(false);
			Button homePageButton = new Button("Back to homepage");
			homePageButton.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					sendToServer("Back to homepage button was clicked");
				}

			});
			HBox h = new HBox(5);
			ImageView img = getIMG("images/v.png");
			Label l = new Label("Your order has been successfuly made");
			l.setFont(new Font("Arial", 20));
			h.getChildren().add(img);
			h.getChildren().add(l);
			homePageButton.setStyle("-fx-background-color:  #F24444; -fx-text-fill: white;");
			Label thankYou = new Label("Thanks for using our services!");
			thankYou.setFont(new Font("Arial", 24));
			order = null;
			mHbox.setAlignment(Pos.CENTER);
			VBox v = new VBox(10);
			v.getChildren().add(h);
			v.getChildren().add(thankYou);
			thankYou.setLayoutY(100);
			v.getChildren().add(homePageButton);
			mHbox.getChildren().add(v);
		});
	}

	/**
	 * load image by using name<br>
	 * if image is not found, load "not available" image
	 * 
	 * @param name (name of image)
	 * @return ImageView containing the respective image
	 */
	public ImageView getIMG(String name) {
		Image img;
		try {
			img = new Image(name);
		} catch (IllegalArgumentException e) {
			img = new Image("/images/not available.jpg");
		}
		ImageView imgView = new ImageView(img);
		imgView.setFitHeight(40);
		imgView.setFitWidth(40);
		imgView.setPreserveRatio(true);
		return imgView;
	}

	/**Method called by server side when payment fails<br>
	 * Pop up a dialog window asking if user would like to pay using his credit aswell<br>
	 * 2 buttons: "yes", "no"
	 * 
	 * @param json
	 */
	public void orderFailPopUp(JSONObject json) {
		Long currentBalance = Message.getValueLong(json, "currentBalance");
		Platform.runLater(() -> {
			Stage window = new Stage();
			window.initModality(Modality.APPLICATION_MODAL);
			window.setTitle("ERROR");
			window.setMinWidth(300);
			window.setMinHeight(20);

			Label label = new Label();
			label.setText(
					"You have "+ currentBalance+" INS in your balance. Use card: "+ view.getCreditNumber()+" to pay the remaining "+(leftToPay-currentBalance)+" INS and complete the order?");

			Button b1 = new Button("Yes");
			b1.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					window.hide();
					JSONObject toServer = new JSONObject();
					toServer.put("order", order);
					toServer.put("command", "Clicked yes after order failed");
					view.ready(toServer);
				}
			});
			Button b2 = new Button("No");
			b2.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					window.hide();
				}
			});
		
			VBox layout = new VBox(10);
			layout.getChildren().add(label);
			layout.setAlignment(Pos.CENTER);
			layout.getChildren().add(b1);
			layout.getChildren().add(b2);

			Scene scene = new Scene(layout);
			window.setScene(scene);
			window.showAndWait();
		});
	}

	/**general pop up message
	 * @param msg
	 */
	public void showPopup(String msg) {
		Platform.runLater(() -> {
			Stage window = new Stage();
			window.initModality(Modality.APPLICATION_MODAL);
			window.setTitle("ERROR");
			window.setMinWidth(300);
			window.setMinHeight(20);

			Label label = new Label();
			label.setText(msg);

			Button b = new Button("OK");
			b.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					window.hide();
				}

			});

			VBox layout = new VBox(10);
			layout.getChildren().add(label);
			layout.setAlignment(Pos.CENTER);
			layout.getChildren().add(b);

			Scene scene = new Scene(layout);
			window.setScene(scene);
			window.showAndWait();
		});
	}

	/**
	 * Method to check if (difference) time has passed from start to end
	 * @param start
	 * @param end
	 * @param difference (minutes)
	 * @return true if end - start > difference
	 */
	public boolean checkIfDifferencePassed(String start, String end, int difference) {
		int endHour = Integer.valueOf(DateParser.dateParser(end, "hours"));
		int endMinutes = Integer.valueOf(DateParser.dateParser(end, "minutes"));
		int startHour = Integer.valueOf(DateParser.dateParser(start, "hours"));
		int startMinutes = Integer.valueOf(DateParser.dateParser(start, "minutes"));
		if ((endHour * 60 + endMinutes) - (startHour * 60 + startMinutes) > difference )
			return true;
		else
			return false;
	}
}
