package clientSide;

import java.io.IOException;

import org.json.simple.JSONObject;

import common.Logger;
import common.Logger.Level;
import common.Message;
import javaFXControllers.Customer.CustomerWindow;
import javaFXControllers.Customer.OrderWindow;
import javaFXControllers.Customer.PaymentWindow;
import javaFXControllers.Customer.ViewOrderWindow;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
/**
 * Class used implement "PortalViewInterface" Loads the relevant templates and information that
 * the customer can see, also handles messages from/to server
 * @author Mosa Hadish
 * @version 3/1/2021
 */
public class CustomerPortalView implements PortalViewInterface {

	private ComController com;

	private String defaultBranch;
	private String firstName;
	private String phoneNumber;
	private String address;
	private String creditNumber;
	private String ID;
	private String email;

	// ----- LOGIN variables

	private Stage primaryStage;
	private CustomerWindow customerWindow;
	private OrderWindow orderWindow;
	private PaymentWindow paymentWindow;
	private ViewOrderWindow viewOrderWindow;
	private VBox homePageVBox;
	private HBox orderHBox;
	private HBox paymentHBox;
	private HBox viewOrderHBox;

	public CustomerPortalView(Stage primaryStage, ComController com) {
		this.primaryStage = primaryStage;
		this.com = com;
	}

	
	@Override
	public void init(JSONObject json) {
		defaultBranch = Message.getValueString(json, "branch");
		firstName = Message.getValueString(json, "FirstName");
		loadCustomerWindow();
		loadOrderWindow();
		loadViewOrderWindow();
		loadPaymentWindow();
		// log
		Logger.log(Level.DEBUG, "CustomerPortalView: Customer related windows initialized");
		System.out.println("CustomerPortalView: Customer related windows initialized");
		customerWindow.showWindow();
	}

	@Override
	public void handleMsg(JSONObject descriptor) {
		switch (Message.getValueString(descriptor, "command")) {
		case "update":
			// customerWindow
			if (Message.getValueString(descriptor, "update").equals("Show customer window"))
				customerWindow.showWindow();
			else if (Message.getValueString(descriptor, "update").equals("W4C not found"))
				customerWindow.scanFail();
			else if (Message.getValueString(descriptor, "update").equals("Order is ready"))
				customerWindow.orderReadyPopup(descriptor);
			else if (Message.getValueString(descriptor, "update").equals("W4C found")) {
				address = Message.getValueString(descriptor, "address");
				phoneNumber = Message.getValueString(descriptor, "phoneNumber");
				creditNumber = Message.getValueString(descriptor, "creditNumber");
				email = Message.getValueString(descriptor, "email");
				ID = Message.getValueString(descriptor, "ID");
				customerWindow.scanSuccess();
			}
			else if (Message.getValueString(descriptor, "update").equals("Go back to homepage")) {
				loadOrderWindow(); //reload because changing a lot
				loadPaymentWindow();//reload because changing a lot
				customerWindow.showWindow();
			}
			// orderWindow
			else if (Message.getValueString(descriptor, "update").equals("Show order window")) 
				orderWindow.showWindow();
			else if (Message.getValueString(descriptor, "update").equals("Show restaurant list"))
				orderWindow.showRestaurants(Message.getValueJArray(descriptor, "restaurantList"));
			else if (Message.getValueString(descriptor, "update").equals("Show item types list"))
				orderWindow.showTypesList(Message.getValueJObject(descriptor, "menu"));
			else if (Message.getValueString(descriptor, "update").equals("Show meals by type"))
				orderWindow.showMealsByType(descriptor);
			else if (Message.getValueString(descriptor, "update").equals("Show delivery window"))
				orderWindow.showDeliveryWindow();
			/// paymentWindow
			else if (Message.getValueString(descriptor, "update").equals("Show payment window"))
				paymentWindow.showWindow(Message.getValueJObject(descriptor, "order"));
			else if (Message.getValueString(descriptor, "update").equals("Show payment methods"))
				paymentWindow.showPaymentOptions(descriptor);
			else if (Message.getValueString(descriptor, "update").equals("Order was successfuly added"))
				paymentWindow.showSuccessWindow();
			else if (Message.getValueString(descriptor, "update").equals("Show pop up: failed order")) {
				if(Message.getValueString(descriptor, "reason").equals("Error in system"))
					paymentWindow.showPopup("There was a problem processing your order, try again later");
				if(Message.getValueString(descriptor, "reason").equals("Not enough in balance"))
					paymentWindow.orderFailPopUp(descriptor);
			}

		

			// viewOrderWindow
			else if (Message.getValueString(descriptor, "update").equals("Show View Order window")) 
				viewOrderWindow.showWindow();
			else if (Message.getValueString(descriptor, "update").equals("Show orders"))
				viewOrderWindow.showOrders(descriptor);
			else if (Message.getValueString(descriptor, "update").equals("Show pop up: order finished"))
				viewOrderWindow.showPopupSuccess("Thanks for using our services");
			else if (Message.getValueString(descriptor, "update").equals("Show order details"))
				viewOrderWindow.displayOrderInformation();
			else if (Message.getValueString(descriptor, "update").equals("Refresh View Window")) {
				viewOrderWindow.refreshWindowIfShowing(descriptor);
			}
		default:
			break;
		}
	}

	@Override
	public void ready(JSONObject json) {
		com.handleUserAction(json);
	}

	@Override
	public ComController getComController() {
		return com;
	}

	public OrderWindow getOrderWindow() {
		return orderWindow;
	}

	public CustomerWindow getCustomerWindow() {
		return customerWindow;
	}

	public PaymentWindow getPaymentWindow() {
		return paymentWindow;
	}

	public void setBranch(String defaultBranch) {
		this.defaultBranch = defaultBranch;
	}

	public String getID() {
		return ID;
	}

	public String getEmail() {
		return email;
	}

	public String getBranch() {
		return defaultBranch;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public String getAddress() {
		return address;
	}

	public String getCreditNumber() {
		return creditNumber;
	}

	/**
	 * Load an FXML file for the Order Window and initialize it
	 */
	public void loadOrderWindow() {
		FXMLLoader loader2 = new FXMLLoader();
		loader2.setLocation(getClass().getResource("/templates/OrderWindowTemplate.fxml"));
		try {
			orderHBox = loader2.load();
		} catch (IOException e) {
			System.out.println("CustomerPortalView: IOException in loadOrderWindow "+e);
		}
		orderWindow = loader2.getController();
		orderWindow.init(orderHBox, primaryStage, this);
	}

	/**
	 * Load an FXML file for the Payment Window and initialize it
	 */
	public void loadPaymentWindow() {
		FXMLLoader loader4 = new FXMLLoader();
		loader4.setLocation(getClass().getResource("/templates/PaymentWindow.fxml"));
		try {
			paymentHBox = loader4.load();
		} catch (IOException e) {
			System.out.println("CustomerPortalView: IOException in loadPaymentWindow "+e);
		}
		paymentWindow = loader4.getController();
		paymentWindow.init(paymentHBox, primaryStage, this);
	}

	/**
	 * Load an FXML file for the Customer Window and initialize it
	 */
	public void loadCustomerWindow() {
		FXMLLoader loader1 = new FXMLLoader();
		loader1.setLocation(getClass().getResource("/templates/CustomerHomepageTemplate.fxml"));
		try {
			homePageVBox = loader1.load();
		} catch (IOException e) {
			System.out.println("CustomerPortalView: IOException in loadCustomerWindow "+e);
		}
		customerWindow = loader1.getController();
		customerWindow.init(homePageVBox, primaryStage, this);
	}

	/**
	 * Load an FXML file for the View Order Window and initialize it
	 */
	public void loadViewOrderWindow() {
		FXMLLoader loader3 = new FXMLLoader();
		loader3.setLocation(getClass().getResource("/templates/ViewOrderTemplate.fxml"));
		try {
			viewOrderHBox = loader3.load();
		} catch (IOException e) {
			System.out.println("CustomerPortalView: IOException in loadViewOrderWindow "+e);
		}
		viewOrderWindow = loader3.getController();
		viewOrderWindow.init(viewOrderHBox, primaryStage, this);
	}

}
