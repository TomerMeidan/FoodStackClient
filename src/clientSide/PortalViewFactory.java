package clientSide;

import common.Logger;
import common.Logger.Level;
import javafx.stage.Stage;

public class PortalViewFactory {

	private Stage primaryStage;
	private ComController com;

	public PortalViewFactory(Stage primaryStage, ComController com) {
		this.primaryStage = primaryStage;
		this.com = com;
	}

	public PortalViewInterface createPortalView(String portalType) {

		if (portalType == null)
			return null;

		switch (portalType) {
		case "login":
			return new LoginPortalView(primaryStage, com);
		case "Branch Manager":
			return new BranchManagerPortalView(primaryStage, com);

		case "Supplier":
			return new SupplierPortalView(primaryStage,com);

		case "CEO":
			return new CEOPortalView(primaryStage,com);
		case "HR":
			return new HRPortalView(primaryStage, com);
		case "Customer":
			return new CustomerPortalView(primaryStage, com);
		case "Business Customer":
			return new CustomerPortalView(primaryStage, com);

		default:
			// log
			Logger.log(Level.WARNING, "PortalFactory: unknown portal type");
			System.out.println("PortalFactory: unknown portal type");
			return null;
		}
	}
}
