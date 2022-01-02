package clientSide;
//
import java.io.IOException;
import org.json.simple.JSONObject;
import clientSide.LoginPortalView;
import common.Logger;
import common.Logger.Level;
import common.Message;
import javaFXControllers.supplier.ReceiptsWindow;
import javaFXControllers.supplier.SupplierWindow;
import javaFXControllers.supplier.UpdateMenuWindow;
import javaFXControllers.supplier.UpdateStatusWindow;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Class used implement "PortalViewInterface" Loads the relevant templates that
 * the supplier can see
 * 
 * @author Daniel Ohayon
 * @version 12/11/2021
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

	@Override
	public void init(JSONObject json) {
		try {
			// LOGIN
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/templates/SupplierHomepage.fxml"));
			userID = Message.getValue(json, "userID");
			firstName = Message.getValue(json, "FirstName");
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

	@Override
	public void handleMsg(JSONObject descriptor) {
		switch (Message.getValue(descriptor, "command")) {
		case "update":

			// --------update menu---------------//
			if (Message.getValue(descriptor, "update").equals("showUpdateWindow"))
				updateMenuWindow.showWindow(userID);
			else if (Message.getValue(descriptor, "update").equals("menuList"))
				updateMenuWindow.showTypes(descriptor);
			else if (Message.getValue(descriptor, "update").equals("itemNames"))
				updateMenuWindow.showItemNames(Message.getValue(descriptor, "itemType"));
			else if (Message.getValue(descriptor, "update").equals("showEditTypeDetails"))
				updateMenuWindow.showEditTypeDetails(descriptor);
			else if (Message.getValue(descriptor, "update").equals("saveType"))
				updateMenuWindow.afterPressedSaveEditType(descriptor);
			else if (Message.getValue(descriptor, "update").equals("showAddTypeDetails"))
				updateMenuWindow.showAddTypeDetails();
			else if (Message.getValue(descriptor, "update").equals("addNewType"))
				updateMenuWindow.afterPressedSaveAddType(descriptor);
			else if (Message.getValue(descriptor, "update").equals("deleteType"))
				updateMenuWindow.responseForDeleteType(descriptor);
			else if (Message.getValue(descriptor, "update").equals("exitNewType"))
				updateMenuWindow.responseForExitAddType(descriptor);
			else if (Message.getValue(descriptor, "update").equals("showEditMealDetails"))
				updateMenuWindow.showEditDishDetails(descriptor);
			else if (Message.getValue(descriptor, "update").equals("editDish"))
				updateMenuWindow.afterPressedSaveEditDish(descriptor);
			else if (Message.getValue(descriptor, "update").equals("exitEditType"))
				updateMenuWindow.responseForExitEditType();
			else if (Message.getValue(descriptor, "update").equals("exitAdd/EditMeal"))
				updateMenuWindow.responseForExitAddMeal(Message.getValue(descriptor, "itemType"));

			else if (Message.getValue(descriptor, "update").equals("showAddMealDetails"))
				updateMenuWindow.showAddMealDetails(descriptor);

			else if (Message.getValue(descriptor, "update").equals("deleteDish"))
				updateMenuWindow.responseForDeleteDish(descriptor);
			else if (Message.getValue(descriptor, "update").equals("checkAddFeature"))
				updateMenuWindow.responseForAddFeat(descriptor);
			else if (Message.getValue(descriptor, "update").equals("checkEditFeature"))
				updateMenuWindow.responseForEditFeat(descriptor);

			// --------update status---------------//
			if (Message.getValue(descriptor, "update").equals("showUpdateStatusWindow"))
				updateStatusWindow.showWindow(userID);
			else if (Message.getValue(descriptor, "update").equals("ordersList"))
				updateStatusWindow.showOrderList(descriptor);
			else if (Message.getValue(descriptor, "update").equals("Order is ready"))
				updateStatusWindow.afterPressApproveButton(descriptor);
			else if (Message.getValue(descriptor, "update").equals("Order was successfuly added"))
				updateStatusWindow.alertSupplier(descriptor);
			else if (Message.getValue(descriptor, "update").equals("removeOrder"))
				updateStatusWindow.afterPressRemoveButton(descriptor);
			else if (Message.getValue(descriptor, "update").equals("showOrderDetails"))
				updateStatusWindow.afterPressOrderDetails(descriptor);

			// ----------Receipts ------------//
			if (Message.getValue(descriptor, "update").equals("showReceiptsWindow"))
				receiptsWindow.showWindow(userID);
			else if (Message.getValue(descriptor, "update").equals("receiptList"))
				receiptsWindow.showReceipt(descriptor);

		case "notify":

		default:
			break;
		}

	}

	private Object EditMeal(JSONObject descriptor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void ready(JSONObject json) {
		com.handleUserAction(json);
	}

	@Override
	public ComController getComController() {
		return com;
	}

	public UpdateMenuWindow getUpdateMenuWindow() {
		return updateMenuWindow;
	}

	public UpdateStatusWindow getUpdateStatusWindow() {
		return updateStatusWindow;
	}

	public SupplierWindow getSupplierWindow() {
		return supplierWindow;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getUserID() {
		return userID;
	}

}
