package javaFXControllers.CEO;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import clientSide.CEOPortalView;
import common.Logger;
import common.Logger.Level;
import common.Message;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class ViewQuarterlyIncomeReportsWindow {

	private HBox homePageHBox;
	private Stage primaryStage;
	private CEOPortalView view;
	private JSONObject personalInfo;
	private Scene scene;

	@FXML
	private AnchorPane anchorPaneID;

	@FXML
	private Button approveQuarterButton;

	@FXML
	private Button backButton;

	@FXML
	private ComboBox<String> selectBranchCombo1;

	@FXML
	private ComboBox<String> selectBranchCombo2;

	@FXML
	private ComboBox<String> selectQuarter1;

	@FXML
	private ComboBox<String> selectQuarter2;

	@FXML
	private ComboBox<String> selectYearCombo1;

	@FXML
	private ComboBox<String> selectYearCombo2;

	@FXML
	private Label selectBranchLabel;

	@FXML
	private Label selectBranchLabel1;
	
	private Button switchButton;
	private VBox firstBranchVBox;
	private VBox secondBranchVBox;

	@FXML
	private CheckBox compareCheckBox;

	@FXML
	private HBox secondQuarterHBox;
	@FXML
	private HBox selectHBox;
	
	private String firstChosenBranch = null, firstQuarter = null, firstChosenYear = null;
	private String secondChosenBranch = null, secondQuarter = null, secondChosenYear = null;

	private JSONObject allIncomeReportData;
	private JSONObject firstMaxOrderInformation, firstMaxIncomeInformation;
	private JSONObject secondMaxOrderInformation, secondMaxIncomeInformation;

	private JSONArray firstChoiceFilteredRestaurantsData, secondChoiceFilteredRestaurantsData;
	private ArrayList<String> firstChosenQuarter;
	private ArrayList<String> secondChosenQuarter;
	
	/** Init<p>
	 * This method initiates certain JAVAFX variables in order to present the income quarterly reports window.
	 * The method initiates the combo boxes for choosing years, branches and months accordingly.
	 * @param homePageHBox - Main HBOX for the monthly reports window
	 * @param primaryStage - Main stage.
	 * @param view - Communication Controller for the monthly reports window
	 * @param personalInfo - Personal info about the client.
	 * 
	 * */
	public void init(HBox homePageHBox, Stage primaryStage, CEOPortalView view, JSONObject personalInfo) {
		this.homePageHBox = homePageHBox;
		this.primaryStage = primaryStage;
		this.view = view;
		this.personalInfo = personalInfo;
		scene = new Scene(homePageHBox);
		secondQuarterHBox.setVisible(false);

		selectBranchCombo1.getItems().addAll("North", "Center", "South");
		selectBranchCombo2.getItems().addAll("North", "Center", "South");
		selectQuarter1.getItems().addAll("Jan - Mar", "Apr - Jun", "Jul - Sep", "Oct - Dec");
		selectQuarter2.getItems().addAll("Jan - Mar", "Apr - Jun", "Jul - Sep", "Oct - Dec");

		approveQuarterButton.disableProperty().set(true);

	}

	
	/**
	 * showWindow
	 * 
	 * This method calls Platform.runLater() to add javaFX task.
	 * This method builds the scene and sets to primaryStage.
	 * This method announces to server "ready" after showing window.
	 * @param JSONObject descriptor - has 'FirstName','LastName' keys with accordingly values.
	 * @author Roman Milman
	 */
	public void showWindow() {
		// log
		Logger.log(Level.INFO, "ViewQuarterlyReportsWindow: showing window");
		System.out.println("ViewQuarterlyReportsWindow: showing window");

		Platform.runLater(() -> {
			primaryStage.setScene(scene);
			primaryStage.show();

			JSONObject json = new JSONObject();
			json.put("command", "income quarterly reports window is ready");
			view.getComController().handleUserAction(json);;
		});
	}

	
	/** On Approve Quarter Button<p>
	 * This method is a FX trigger that is initiated when the client is pressing the 
	 * approve button.
	 * The purpose of this method is to determine which type of graph is to be shown, whether it is a compoare 
	 * between two quarterly reports or just one quarterly reports.

	 * */
	@FXML
	void onApproveQuarterButton(ActionEvent event) {
		anchorPaneID.getChildren().clear();
		approveQuarterButton.disableProperty().set(true);
		Logger.log(Level.INFO, "ViewQuarterlyReportsWindow: onApproveQuarterButton: approve button was pressed!");
		System.out.println("ViewQuarterlyReportsWindow: onApproveQuarterButton: approve button was pressed!");
		firstChosenQuarter = setChosenQuarter(firstQuarter);
		if (compareCheckBox.isSelected())
			secondChosenQuarter = setChosenQuarter(secondQuarter);

		firstChoiceFilteredRestaurantsData = new JSONArray();
		firstMaxOrderInformation = new JSONObject();
		firstMaxIncomeInformation = new JSONObject();

		preparingReportData(firstChoiceFilteredRestaurantsData, firstChosenYear, firstChosenBranch, firstChosenQuarter);
		view.getViewMonthlyReportsWindow().getMostValuesFromReport(firstChoiceFilteredRestaurantsData,
				firstMaxIncomeInformation, firstMaxOrderInformation);

		if (!compareCheckBox.isSelected()) {
			view.getViewMonthlyReportsWindow().createIncomeReportGraph(firstChoiceFilteredRestaurantsData,
					firstMaxIncomeInformation, firstMaxOrderInformation, anchorPaneID);
			Logger.log(Level.DEBUG, "ViewQuarterlyIncomeReportsWindow: onApproveQuarterButton: displays Income Report");
			System.out.println("ViewQuarterlyIncomeReportsWindow: onApproveQuarterButton: displays Income Report");
		} else {
			secondChoiceFilteredRestaurantsData = new JSONArray();
			secondMaxOrderInformation = new JSONObject();
			secondMaxIncomeInformation = new JSONObject();

			preparingReportData(secondChoiceFilteredRestaurantsData, secondChosenYear, secondChosenBranch,
					secondChosenQuarter);
			view.getViewMonthlyReportsWindow().getMostValuesFromReport(secondChoiceFilteredRestaurantsData,
					secondMaxIncomeInformation, secondMaxOrderInformation);

			createIncomeCompareReport(false);
			
			switchButton = new Button("Switch graph");
			switchButton.setOnAction(new EventHandler<ActionEvent>() {
				
				boolean isSwitched = true;

				@Override
				public void handle(ActionEvent event) {
					anchorPaneID.getChildren().clear();
					createIncomeCompareReport(isSwitched);
						if(isSwitched) isSwitched = false; else isSwitched = true;
				}
				
			});
			
			selectHBox.getChildren().add(switchButton);
		
		}

	}

	
	/** Create Income Compare Report<p>
	 * This method is responsible to create the two comparable income and amount of orders
	 * graphs.
	 * @param isSwitched - boolean object that indicates if a graph switch between orders and income was initiated. 
	 * 
	 * */
	public void createIncomeCompareReport(boolean isSwitched) {

		Platform.runLater(() -> {

			HBox mainHBox = new HBox();
			anchorPaneID.getChildren().add(mainHBox);

			// Creating the first chosen branch -------------------------------------			
			firstBranchVBox = createBarChart(mainHBox, firstChoiceFilteredRestaurantsData, firstMaxOrderInformation, firstMaxIncomeInformation, firstChosenBranch, firstChosenYear, firstQuarter, isSwitched);
			// Finished creating the first chosen branch ------------------------------
			
			// Creating the second chosen branch -------------------------------------			
			secondBranchVBox = createBarChart(mainHBox, secondChoiceFilteredRestaurantsData, secondMaxOrderInformation, secondMaxIncomeInformation, secondChosenBranch, secondChosenYear, secondQuarter, isSwitched);
			// Finished creating the second chosen branch ------------------------------

			Separator verticalSeperator = new Separator();
			verticalSeperator.setOrientation(Orientation.VERTICAL);
			verticalSeperator.setMaxHeight(390);
						
			mainHBox.getChildren().addAll(firstBranchVBox, verticalSeperator,secondBranchVBox);

		});
	}

	
	/** Create Bar Chart<p>
	 * This method will create a bar chart graph representation of the quarterly income and amount of orders
	 * from the chosen branches by the client, the method will create two graphs in order to compare with each other.
	 * @param mainHBox
	 * @param restaurantDataList
	 * @param mostOrderInformation
	 * @param mostIncomeInformation
	 * @param chosenBranch
	 * @param chosenYear
	 * @param quarter
	 * @param isSwitched
	 * 
	 * */
	private VBox createBarChart(HBox mainHBox, JSONArray restaurantDataList, JSONObject mostOrderInformation, JSONObject mostIncomeInformation, String chosenBranch, String chosenYear, String quarter, boolean isSwitched) {
		VBox firstBranchVBox = new VBox();
		
		StackedBarChart stackedBarChart;
		
		if(!isSwitched) {
		stackedBarChart = view.getViewMonthlyReportsWindow()
				.incomeGraph(restaurantDataList, mostIncomeInformation);
		} else {
			stackedBarChart = view.getViewMonthlyReportsWindow()
					.orderCountGraph(restaurantDataList, mostOrderInformation);
		}
		
		stackedBarChart.setTitle(chosenBranch + " branch on " + chosenYear + ", " + quarter);
		stackedBarChart.setMaxWidth(380);
		stackedBarChart.setMaxHeight(250);
		firstBranchVBox.getChildren().add(stackedBarChart);
		
		// Important facts about the first chosen branch, most income, most orders and such
		VBox sideIncomeInfo = new VBox();
		sideIncomeInfo.setPadding(new Insets(10, 0, 0, 10));
		mainHBox.getChildren().add(sideIncomeInfo);

		// Updating the graph's information with the most orders and most income
		Integer mostOrders = (Integer) mostOrderInformation.get("maxOrders");
		String mostOrdersRestaurant = Message.getValue(mostOrderInformation, "restaurantWithMaxOrders");
		Integer mostIncome = (Integer) mostIncomeInformation.get("maxIncome");
		String mostIncomeRestaurant = Message.getValue(mostIncomeInformation, "restaurantWithMaxIncome");
		
		int totalIncome = view.getViewMonthlyReportsWindow().getTotalValueFromAllRestaurants(restaurantDataList, "totalIncome");
		int totalOrders = view.getViewMonthlyReportsWindow().getTotalValueFromAllRestaurants(restaurantDataList, "totalOrders");
		
		setMostValues(sideIncomeInfo, mostIncome, mostIncomeRestaurant, "Income",totalIncome);
		setMostValues(sideIncomeInfo, mostOrders, mostOrdersRestaurant , "Orders",totalOrders);
		

		
		firstBranchVBox.getChildren().add(sideIncomeInfo);
		return firstBranchVBox;
	}

	
	/** Set Most Values<p>
	 * This method will set all the restaurants that have the most orders and income in their
	 * restaurant on a specific quarter of year.
	 * 
	 * @param sideIncomeInfo - FX Vbox object that will hold the two quarter graphs.
	 * @param value - most value of either income or amount of orders
	 * @param restaurantName - the restaurant name that has the most in income or orders
	 * @param type - either type of most income or type of most orders
	 * @param totalValue - the sum of all the restaurant in the quarter.
	 * 
	 * */
	private void setMostValues(VBox sideIncomeInfo, Integer value, String restaurantName, String type, int totalValue) {
		// Most values details
		
		VBox mostVBox = new VBox();

		Label mostTitleLabel = new Label(type + " Info:");
		mostTitleLabel.setFont(Font.font(18));
		mostTitleLabel.setStyle("-fx-font-weight: bold");
		Label mostRestaurantLabel = new Label("Restaurant " + restaurantName + " has the most " + type + " with total of " + value);
		Label spaceLabel = new Label(" ");
		
		Label totalValueLabel = new Label();		
		if(type.equals("Income")) totalValueLabel.setText("Total revenue from all restaurants is " + totalValue + " (NIS)");
		else totalValueLabel.setText("Total orders from all restaurants is " + totalValue + " (ORDERS)");
		
		Separator horizontalSeperator1 = new Separator();
		horizontalSeperator1.setMaxWidth(Control.USE_COMPUTED_SIZE);
		horizontalSeperator1.setPadding(new Insets(5, 0, 5, 0));
		sideIncomeInfo.getChildren().addAll(mostTitleLabel,mostRestaurantLabel,spaceLabel,totalValueLabel, horizontalSeperator1);
	}
	
	
	/** Set Chosen Quarter<p>
	 * 
	 * This method is setting an ArrayList object to hold a certain array of strings regarding
	 * the previous quarter months of this or last year. The purpose of this arrayList is 
	 * to help indicate which quarters are to be returned from the data base when pulling the reports
	 * information about the income, item per restaurants and performance. In this manner, we initiate a 
	 * Date object to learn which current month and year  we are at the moment and by this, enabling to determine
	 * which last quarter is needed.
	 * @author Tomer Meidan
	 * @param quarter - input is the current quarter (STRING ranges between the quarter months)
	 * @return ArrayList<String> - returns an array list consisting three months on the last quarter.
	 * */
	private ArrayList<String> setChosenQuarter(String quarter) {
		ArrayList<String> chosenQuarter = new ArrayList<>();

		switch (quarter) {
		case "Jan - Mar":
			chosenQuarter.add("01");
			chosenQuarter.add("02");
			chosenQuarter.add("03");
			break;
		case "Apr - Jun":
			chosenQuarter.add("04");
			chosenQuarter.add("05");
			chosenQuarter.add("06");
			break;
		case "Jul - Sep":
			chosenQuarter.add("07");
			chosenQuarter.add("08");
			chosenQuarter.add("09");
			break;
		case "Oct - Dec":
			chosenQuarter.add("10");
			chosenQuarter.add("11");
			chosenQuarter.add("12");
			break;
		}

		return chosenQuarter;

	}

	
	/** Preparing Report Data<p>
	 * This method takes the global object of all the related report information,
	 * and filter them by the request of the user. For example, if the user requested
	 * to see Income reports, then the method will update the object filteredRestaurantsData
	 * with all the related data about income reports on the month and year and branch the user chose to see.
	 * 
	 * @param filteredRestaurantsData - holds related data for a specific time and branch.
	 * @param chosenYear - Chosen year by the user on the GUI screen for a specific year on reports.
	 * @param chosenBranch - Chosen branch by the user on the GUI screen for a specific year on reports.
	 * @param chosenQuarter - Chosen quarter by the user on the GUI screen for a specific year on reports.
	 * @Note allIncomeReportData - Holds all information about reports (Income)
	 * 
	 * @author Tomer Meidan
	 * */
	@SuppressWarnings("unchecked")
	public void preparingReportData(JSONArray filteredRestaurantsData, String chosenYear, String chosenBranch,
			ArrayList<String> chosenQuarter) {

		Integer relevantCount = 0;
		Integer size;
		boolean isFirstEnterance = true, isEmpty = true;
		String restaurantName = "", branch = " ";
		long totalIncome = 0, totalOrders = 0;

		JSONArray incomeReportData = (JSONArray) allIncomeReportData.get("income reports data");
		size = incomeReportData.size();
		for (int i = 0; i < size; i++) {

			JSONArray singleRestaurantData = (JSONArray) incomeReportData.get(i);

			for (int j = 0; j < singleRestaurantData.size(); j++) {
				JSONObject restaurantRow = (JSONObject) singleRestaurantData.get(j);

				if (isFirstEnterance) {
					restaurantName = Message.getValue(restaurantRow, "restaurantName");
					branch = Message.getValue(restaurantRow, "branch");
					if (!branch.equals(chosenBranch))
						break;
					totalIncome = 0;
					totalOrders = 0;
					isFirstEnterance = false;
				}

				if (relevantReportData(restaurantRow, chosenYear, chosenBranch, chosenQuarter)) {
					totalIncome += (long) restaurantRow.get("totalIncome");
					totalOrders += (long) restaurantRow.get("totalOrders");
					isEmpty = false;
					relevantCount++;
				}

			}

			if (isEmpty == false) {
				JSONObject json = new JSONObject();
				json.put("restaurantName", restaurantName);
				json.put("branch", chosenBranch);
				json.put("totalIncome", totalIncome);
				json.put("totalOrders", totalOrders);
				filteredRestaurantsData.add(json);

			}

			isEmpty = true;
			isFirstEnterance = true;

		}

		System.out
				.println("ViewQuarterlyIncomeReportsWindow : preparingReportData : First quarter relevant data count: "
						+ relevantCount + " | the data: " + filteredRestaurantsData.toString());

		relevantCount = 0;
	}

	
	/**
	 * On Back Button
	 * <p>
	 * 
	 * This is an FXML trigger on the button back. The method will activate on user
	 * pressed back button, a destructor for all related variables to the class will
	 * initiate in order to reset their values and a method will return to the
	 * previous of the CEO.
	 * 
	 * @param event - This object is an action event that represent the action on pressing the back button.

	 * @author Tomer Meidan
	 */
	@FXML
	void onBackButton(ActionEvent event) {

		// Start of DESTRUCTOR -------------------------------------------
		anchorPaneID.getChildren().clear();
		selectBranchCombo1.getSelectionModel().clearSelection();
		selectBranchCombo2.getSelectionModel().clearSelection();
		compareCheckBox.setSelected(false);
		secondQuarterHBox.setVisible(false);
		selectQuarter1.getSelectionModel().clearSelection();
		selectQuarter2.getSelectionModel().clearSelection();
		selectYearCombo1.getSelectionModel().clearSelection();
		selectYearCombo2.getSelectionModel().clearSelection();
		selectYearCombo1.getItems().clear();
		selectYearCombo2.getItems().clear();
		approveQuarterButton.disableProperty().set(true);
		allIncomeReportData = null;
		firstChosenBranch = firstQuarter = firstChosenYear = null;
		secondChosenBranch = secondQuarter = secondChosenYear = null;
		firstChoiceFilteredRestaurantsData = null;
		firstChosenQuarter = null;
		secondChosenQuarter = null;
		selectHBox.getChildren().remove(switchButton);
		// End of DESTRUCTOR ----------------------------------------------

		Logger.log(Level.INFO,
				"ViewQuarterlyIncomeReports: Back button was pressed, returning to View Quarterly Rerorts page");
		System.out.println(
				"ViewQuarterlyIncomeReports: Back button was pressed, returning to View Quarterly Reports page");

		view.getViewQuarterlyReportsWindow().showWindow();
	}

	
	/** On Select First Branch<p>
	 * The method represents the users first Branch to compare.
	 * This method is an FX trigger that initiates upon clicking an option 
	 * for the branch combo box on the GUI screen.
	 * The method will save the users choice for the branch on a globally variable in the class.
	 * 
	 * @param event - This object is an action event that represent the action on the check box of branch.
	 * @author Tomer Meidan

	 * 
	 * */
	@FXML
	void onSelectFirstBranch(ActionEvent event) {

		firstChosenBranch = selectBranchCombo1.getSelectionModel().getSelectedItem();

		isUserInputValid();

		Logger.log(Level.DEBUG, "ViewQuarterlyIncomeReportsWindow: onSelectFirstBranch: user selected first branch "
				+ firstChosenBranch);
		System.out.println(
				"ViewMonthlyReportsWindow: onSelectFirstBranch: user selected first branch " + firstChosenBranch);
	}

	
	/** On Select First Quarter<p>
	 * The method represents the users first Quarter to compare.
	 * This method is an FX trigger that initiates upon clicking an option 
	 * for the quarter combo box on the GUI screen.
	 * The method will save the users choice for the quarter on a globally variable in the class.
	 * 
	 * @param event - This object is an action event that represent the action on the check box of quarter.
	 * @author Tomer Meidan
	 * 
	 * */
	@FXML
	void onSelectFirstQuarter(ActionEvent event) {

		firstQuarter = selectQuarter1.getSelectionModel().getSelectedItem();

		isUserInputValid();

		Logger.log(Level.DEBUG,
				"ViewQuarterlyIncomeReportsWindow: onSelectFirstQuarter: user selected first quarter " + firstQuarter);
		System.out
				.println("ViewMonthlyReportsWindow: onSelectFirstQuarter: user selected first quarter " + firstQuarter);
	}

	
	/** On Select First Year<p>
	 * The method represents the users first Year to compare.
	 * 
	 * This method is an FX trigger that initiates upon clicking an option 
	 * for the year combo box on the GUI screen.
	 * The method will save the users choice for the year on a globally variable in the class.
	 * 
	 * @param event - This object is an action event that represent the action on the check box of year.
	 * @author Tomer Meidan
	 * 
	 * */
	@FXML
	void onSelectFirstYear(ActionEvent event) {

		firstChosenYear = selectYearCombo1.getSelectionModel().getSelectedItem();

		isUserInputValid();

		Logger.log(Level.DEBUG,
				"ViewQuarterlyIncomeReportsWindow: onSelectFirstYear: user selected first year " + firstChosenYear);
		System.out.println("ViewMonthlyReportsWindow: onSelectFirstYear: user selected first year " + firstChosenYear);
	}

	
	/** On Select Second Branch<p>
	 * The method represents the users second Branch to compare.
	 * This method is an FX trigger that initiates upon clicking an option 
	 * for the branch combo box on the GUI screen.
	 * The method will save the users choice for the branch on a globally variable in the class.
	 * 
	 * @param event - This object is an action event that represent the action on the check box of branch.
	 * @author Tomer Meidan

	 * 
	 * */
	@FXML
	void onSelectSecondBranch(ActionEvent event) {

		secondChosenBranch = selectBranchCombo2.getSelectionModel().getSelectedItem();

		isUserInputValid();

		Logger.log(Level.DEBUG, "ViewQuarterlyIncomeReportsWindow: onSelectSecondBranch: user selected second branch "
				+ secondChosenBranch);
		System.out.println(
				"ViewMonthlyReportsWindow: onSelectSecondBranch: user selected second branch " + secondChosenBranch);

	}

	
	/** On Select Second Quarter<p>
	 * The method represents the users second Quarter to compare.
	 * This method is an FX trigger that initiates upon clicking an option 
	 * for the quarter combo box on the GUI screen.
	 * The method will save the users choice for the quarter on a globally variable in the class.
	 * 
	 * @param event - This object is an action event that represent the action on the check box of quarter.
	 * @author Tomer Meidan
	 * 
	 * */
	@FXML
	void onSelectSecondQuarter(ActionEvent event) {

		secondQuarter = selectQuarter2.getSelectionModel().getSelectedItem();

		isUserInputValid();

		Logger.log(Level.DEBUG, "ViewQuarterlyIncomeReportsWindow: onSelectSecondQuarter: user selected second quarter "
				+ secondQuarter);
		System.out.println(
				"ViewMonthlyReportsWindow: onSelectSecondQuarter: user selected second quarter " + secondQuarter);
	}

	
	/** On Select Second Year<p>
	 * The method represents the users second Year to compare.
	 * 
	 * This method is an FX trigger that initiates upon clicking an option 
	 * for the year combo box on the GUI screen.
	 * The method will save the users choice for the year on a globally variable in the class.
	 * 
	 * @param event - This object is an action event that represent the action on the check box of year.
	 * @author Tomer Meidan
	 * 
	 * */
	@FXML
	void onSelectSecondYear(ActionEvent event) {

		secondChosenYear = selectYearCombo2.getSelectionModel().getSelectedItem();

		isUserInputValid();

		Logger.log(Level.DEBUG,
				"ViewQuarterlyIncomeReportsWindow: onSelectSecondYear: user selected second year " + secondChosenYear);
		System.out
				.println("ViewMonthlyReportsWindow: onSelectSecondYear: user selected second year " + secondChosenYear);
	}

	
	/** On Compare Check Box<p>
	 * This method is an FX trigger that initiates after making an action on the compare
	 * check box on the GUI screen. This method initiates if the client checked or unchecked the compare box,
	 * in order to choose which quarters to compare against each other.
	 * @param event - This object is an action event that represent the action on the check box of compare.
	 * @author Tomer Meidan
	 * */
	@FXML
	void onCompareCheckBox(ActionEvent event) {
		selectHBox.getChildren().remove(switchButton);

		if (compareCheckBox.isSelected()) {
			isUserInputValid();
			secondQuarterHBox.setVisible(true);
			Logger.log(Level.DEBUG,
					"ViewQuarterlyIncomeReportsWindow: onCompareCheckBox: user checked compare reports");
			System.out.println("ViewQuarterlyIncomeReportsWindow: onCompareCheckBox: user checked compare reports");
		} else {
			isUserInputValid();
			selectYearCombo2.getSelectionModel().clearSelection();
			selectBranchCombo2.getSelectionModel().clearSelection();
			selectQuarter2.getSelectionModel().clearSelection();
			secondChosenBranch = secondQuarter = secondChosenYear = null;
			secondQuarterHBox.setVisible(false);
			Logger.log(Level.DEBUG,
					"ViewQuarterlyIncomeReportsWindow: onCompareCheckBox: user unchecked compare reports");
			System.out.println("ViewQuarterlyIncomeReportsWindow: onCompareCheckBox: user unchecked compare reports");
		}

	}

	 
	/** Set Restaurants Report Data<p>
	 * This method updates the global variable allIncomeReportData by the last quarter, year and current branch
	 * of the user. Also the method determines which years to show on the Year Combo Box on the GUI side, it will
	 * iterate through all the report information and find out which years are the reports related to, and save it
	 * in an arrayList.
	 * 
	 * @param descriptor - This JSON object holds all the information related to reports data on the quarter, year and branch
	 * of the user BM.
	 * @author Tomer Meidan

	 * */
	public void setRestaurantsReportData(JSONObject descriptor) {
		allIncomeReportData = descriptor;
		Platform.runLater(() -> {
			JSONArray reportData = (JSONArray) allIncomeReportData.get("income reports data");
			ArrayList<String> arrayYear = new ArrayList<>();

			for (int i = 0; i < reportData.size(); i++) {
				JSONArray restaurant = (JSONArray) reportData.get(i);
				for (int j = 0; j < restaurant.size(); j++) {
					JSONObject restaurantRow = (JSONObject) restaurant.get(j);
					String year = Message.getValue(restaurantRow, "dateYear");
					if (!arrayYear.contains(year)) {
						selectYearCombo1.getItems().add(year);
						selectYearCombo2.getItems().add(year);
						arrayYear.add(year);
					}
				}
			}
		});
	}

	
	/** Relevant Report Data<p>
	 * This method checks if the current checks data regarding a report from a restaurant is
	 * on the same month, year , quarter and branch are the same as chosen by the client.
	 * @param restaurantInfo - chosen restaurant by the client.
	 * @param chosenYear - chosen year by the client.
	 * @param chosenBranch - chosen branch by the client.
	 * @param chosenQuarter - chosen quarter by the client.
	 * 
	 * @return returns a boolean value indicating if the data is relevant for this report or not.
	 * @author Tomer Meidan
	 * */
	public boolean relevantReportData(JSONObject restaurantInfo, String chosenYear, String chosenBranch,
			ArrayList<String> chosenQuarter) {

		String orderBranch = Message.getValue(restaurantInfo, "branch");
		String year = Message.getValue(restaurantInfo, "dateYear");
		String month = Message.getValue(restaurantInfo, "dateMonth");

		if (year == null || month == null) {
			Logger.log(Level.DEBUG,
					"ViewMonthlyReportsWindow: onApproveDateButton: relevantReportData: orderDate is invalid");
			System.out
					.println("ViewMonthlyReportsWindow: onApproveDateButton: relevantReportData: orderDate is invalid");
			return false;
		}
		if (year.equals(chosenYear) && chosenQuarter.contains(month) && orderBranch.equals(chosenBranch))
			return true;
		return false;

	}

	
	/** Is User Input Valid<p>
	 * This method is a boolean valid function to allow the user to continue and see
	 * the report information only after he selected certain options first.
	 * Such as, the user must select a branch, certain quarter and year in order to continue and 
	 * see the result. Also if the client wishes to compare between reports, then he must fill in
	 * the second row of options aswell that consists of branch, quarter and year aswell.
	 * 
	 * NOTE: the option to submit will only be open if both rows are valid and options were chosen.
	 * @author Tomer Meidan
	 * 
	 * */
	public void isUserInputValid() {

		if (firstChosenBranch != null && firstQuarter != null && firstChosenYear != null) {
			if (!compareCheckBox.isSelected())
				approveQuarterButton.disableProperty().set(false);
			else if (compareCheckBox.isSelected()) {
				if (secondChosenBranch != null && secondQuarter != null && secondChosenYear != null)
					approveQuarterButton.disableProperty().set(false);
				else
					approveQuarterButton.disableProperty().set(true);
			}
		} else
			approveQuarterButton.disableProperty().set(true);

	}

}
