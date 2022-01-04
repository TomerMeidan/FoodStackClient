package clientSide;
//
import java.io.IOException;

import org.json.simple.JSONObject;

import common.Logger;
import common.Logger.Level;
import common.Message;
import javaFXControllers.supplier.ReceiptsWindow;
import javaFXControllers.supplier.SupplierWindow;
import javaFXControllers.supplier.UpdateMenuWindow;
import javaFXControllers.supplier.UpdateStatusWindow;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * SupplierPortalView
 * 
 * This class is the portalView which handles Supplier behavior.
 * This class implements PortalViewInterface.
 * This class holds all the javaFX controllers which Supplier uses.
 * Holds ComController to send messages.
 * Holds JSONObject personalInfo,which holds: portalType,FirstName,LastName.
 * @author Daniel Ohayon
 */
public class SupplierPortalView implements PortalViewInterface {

	private ComController com;
	private Stage primaryStage;
	private SupplierWindow supplierWindow;
	private UpdateMenuWindow updateMenuWindow;
	private UpdateStatusWindow updateStatusWindow;
	private ReceiptsWindow receiptsWindow;
	private VBox homePageVBox, updateMenuVBox, updateStatusVBox, receiptsVBox;
	private String userID;
	private String firstName;

	public SupplierPortalView(Stage primaryStage, ComController com) {
		this.primaryStage = primaryStage;
		this.com = com;
	}

	/**
	 * init
	 * 
	 * This method initializing all the javaFX controllers.
	 * Sets personalInfo to JSONObject descriptor, received from server.
	 * Calls showWindow to show branch Manager's home page.
	 * @param JSONObject descriptor
	 */
	@Override
	public void init(JSONObject json) {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/templates/SupplierHomepage.fxml"));
			userID = Message.getValueString(json, "userID");
			firstName = Message.getValueString(json, "FirstName");
			homePageVBox = loader.load();
			supplierWindow = loader.getController();
			supplierWindow.init(homePageVBox, primaryStage, this);

			loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/templates/UpdateMenuTemplate.fxml"));
			updateMenuVBox = loader.load();
			updateMenuWindow = loader.getController();
			updateMenuWindow.init(updateMenuVBox, primaryStage, this);

			loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/templates/UpdateStatusTemplate.fxml"));
			updateStatusVBox = loader.load();
			updateStatusWindow = loader.getController();
			updateStatusWindow.init(updateStatusVBox, primaryStage, this);

			loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/templates/ReceiptsTemplate.fxml"));
			receiptsVBox = loader.load();
			receiptsWindow = loader.getController();
			receiptsWindow.init(receiptsVBox, primaryStage, this);

			// log
			Logger.log(Level.DEBUG, "SupplierPortalView: supplierWindow initialized");
			System.out.println("SupplierPortalView: supplierWindow initialized");

			supplierWindow.showWindow();

		} catch (IOException e) {
			Logger.log(Level.WARNING, "SupplierPortalView: init: IOException");
			System.out.println("SupplierPortalView: init: IOException");
		}

	}
	/**
	 * handleMsg
	 * 
	 * This method handles Supplier's messages received from server.
	 * @param descriptor - read's 'command' key to define which handleCommand method to call.
	 */
	@Override
	public void handleMsg(JSONObject descriptor) {
		switch (Message.getValueString(descriptor, "command")) {
		case "update":

			// --------update menu---------------//
			if (Message.getValueString(descriptor, "update").equals("showUpdateWindow"))
				updateMenuWindow.showWindow(userID);
			else if (Message.getValueString(descriptor, "update").equals("menuList"))
				updateMenuWindow.showTypes(descriptor);
			else if (Message.getValueString(descriptor, "update").equals("itemNames"))
				updateMenuWindow.showItemNames(Message.getValueString(descriptor, "itemType"));
			else if (Message.getValueString(descriptor, "update").equals("showEditTypeDetails"))
				updateMenuWindow.showEditTypeDetails(descriptor);
			else if (Message.getValueString(descriptor, "update").equals("saveType"))
				updateMenuWindow.afterPressedSaveEditType(descriptor);
			else if (Message.getValueString(descriptor, "update").equals("showAddTypeDetails"))
				updateMenuWindow.showAddTypeDetails();
			else if (Message.getValueString(descriptor, "update").equals("addNewType"))
				updateMenuWindow.afterPressedSaveAddType(descriptor);
			else if (Message.getValueString(descriptor, "update").equals("deleteType"))
				updateMenuWindow.responseForDeleteType(descriptor);
			else if (Message.getValueString(descriptor, "update").equals("exitNewType"))
				updateMenuWindow.responseForExitAddType(descriptor);
			else if (Message.getValueString(descriptor, "update").equals("showEditMealDetails"))
				updateMenuWindow.showEditDishDetails(descriptor);
			else if (Message.getValueString(descriptor, "update").equals("editDish"))
				updateMenuWindow.afterPressedSaveEditDish(descriptor);
			else if (Message.getValueString(descriptor, "update").equals("exitEditType"))
				updateMenuWindow.responseForExitEditType();
			else if (Message.getValueString(descriptor, "update").equals("exitAdd/EditMeal"))
				updateMenuWindow.responseForExitAddMeal(Message.getValueString(descriptor, "itemType"));

			else if (Message.getValueString(descriptor, "update").equals("showAddMealDetails"))
				updateMenuWindow.showAddMealDetails(descriptor);

			else if (Message.getValueString(descriptor, "update").equals("deleteDish"))
				updateMenuWindow.responseForDeleteDish(descriptor);
			else if (Message.getValueString(descriptor, "update").equals("checkAddFeature"))
				updateMenuWindow.responseForAddFeat(descriptor);
			else if (Message.getValueString(descriptor, "update").equals("checkEditFeature"))
				updateMenuWindow.responseForEditFeat(descriptor);

			// --------update status---------------//
			if (Message.getValueString(descriptor, "update").equals("showUpdateStatusWindow"))
				updateStatusWindow.showWindow(userID);
			else if (Message.getValueString(descriptor, "update").equals("ordersList"))
				updateStatusWindow.showOrderList(descriptor);
			else if (Message.getValueString(descriptor, "update").equals("Order is ready"))
				updateStatusWindow.afterPressApproveButton(descriptor);
			else if (Message.getValueString(descriptor, "update").equals("Order was successfuly added"))
				updateStatusWindow.alertSupplier(descriptor);
			else if (Message.getValueString(descriptor, "update").equals("removeOrder"))
				updateStatusWindow.afterPressRemoveButton(descriptor);
			else if (Message.getValueString(descriptor, "update").equals("showOrderDetails"))
				updateStatusWindow.afterPressOrderDetails(descriptor);

			// ----------Receipts ------------//
			if (Message.getValueString(descriptor, "update").equals("showReceiptsWindow"))
				receiptsWindow.showWindow(userID);
			else if (Message.getValueString(descriptor, "update").equals("receiptList"))
				receiptsWindow.showReceipt(descriptor);

		

		default:
			break;
		}

	}
	
	/**
	 * ready
	 * 
	 * This method sends the server (Window name here) is ready.
	 * @param descriptor - 'command' key specifies which window is ready.
	 */
	@Override
	public void ready(JSONObject json) {
		com.handleUserAction(json);
	}

	/**
	 * getComController
	 * 
	 * This method returns ComController instance.
	 * @return ComController
	 */
	@Override
	public ComController getComController() {
		return com;
	}


	/**
	 * getUpdateMenuWindow
	 * 
	 * This method shows update menu.
	 */
	public UpdateMenuWindow getUpdateMenuWindow() {
		return updateMenuWindow;
	}
	/**
	 * getUpdateStatusWindow
	 * 
	 * This method shows update status.
	 */
	public UpdateStatusWindow getUpdateStatusWindow() {
		return updateStatusWindow;
	}

	/**
	 * getSupplierWindow
	 * 
	 * This method shows supplier home page.
	 */
	public SupplierWindow getSupplierWindow() {
		return supplierWindow;
	}

	/**
	 * getFirstname
	 * 
	 * This method returns this users first name.
	 * @return String
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * getUserID
	 * 
	 * This method returns this users ID.
	 * @return String
	 */
	public String getUserID() {
		return userID;
	}

}
