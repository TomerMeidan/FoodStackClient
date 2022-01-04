package javaFXControllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import clientSide.BranchManagerPortalView;
import clientSide.CEOPortalView;
import clientSide.PortalViewInterface;
import common.Logger;
import common.Logger.Level;
import common.Message;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class ViewMonthlyReportsWindow {

	private Stage primaryStage;
	private Scene scene;
	private HBox homePageHBox;
	private JSONObject personalInfo;
	private PortalViewInterface view;

	// Start of main selected variables that the user is choosing
	private String selectedBranch = "";
	private String selectedReport = "";
	private String selectedMonthDate = null;
	private String selectedYearDate = null;
	// End of main selected variables

	@FXML
	private Button approveDateButton;
	@FXML
	private AnchorPane anchorPaneID;
	@FXML
	private Button backButton;
	@FXML
	private DatePicker dateID;
	@FXML
	private ComboBox<String> selectBranchCombo;
	@FXML
	private Label selectBranchLabel;
	@FXML
	private Label messageLabel;
	@FXML
	private HBox mainHBox;
	@FXML
	private Separator verticalSeperator;
	@FXML
	private ComboBox<String> selectReportCombo;
	@FXML
	private ComboBox<String> selectYearCombo;
	@FXML
	private ComboBox<String> selectMonthCombo;
	private ArrayList<String> arrayYear;
	// JSON holds all the information about restaurants reports from the database
	private JSONObject restaurantsReportData;

	// filtered all the none relevent restaurants, not on the same month\year and
	// not the same branch
	private JSONArray filteredRestaurantsData;

	private JSONObject maxOrderInformation;
	private JSONObject maxIncomeInformation;

	// Start of created stacked bar charts variables -------
	private StackedBarChart ordersStackedBarChart;
	private StackedBarChart incomeStackedBarChart;
	private VBox vboxStackedBars;
	// End of created stacked bar charts variables -------

	/** Init<p>
	 * This method initiates certain JAVAFX variables in order to present the monthly reports window.
	 * The method initiates the combo boxes for choosing years, branches and months accordingly.
	 * @param homePageHBox - Main HBOX for the monthly reports window
	 * @param primaryStage - Main stage.
	 * @param view - Communication Controller for the monthly reports window
	 * @param personalInfo - Personal info about the client.
	 * 
	 * */
	public void init(HBox homePageHBox, Stage primaryStage, PortalViewInterface view, JSONObject personalInfo) {
		this.homePageHBox = homePageHBox;
		this.primaryStage = primaryStage;
		this.view = view;
		this.personalInfo = personalInfo;
		scene = new Scene(homePageHBox);
		maxOrderInformation = new JSONObject();
		maxIncomeInformation = new JSONObject();
		approveDateButton.disableProperty().set(true);
		selectBranchCombo.getItems().addAll("North", "South", "Center");

		// ComboBox is set for report types
		selectReportCombo.getItems().addAll("Income", "Meals", "Performance");
		selectMonthCombo.getItems().addAll("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12");

	}

	public void showWindow() {
		// log
		Logger.log(Level.INFO, "ViewMonthlyReportsWindow: showing window");
		System.out.println("ViewMonthlyReportsWindow: showing window");

		Platform.runLater(() -> {
			primaryStage.setScene(scene);
			primaryStage.show();

			// ComboBox is set for branch types
			if (view instanceof BranchManagerPortalView) {
				selectBranchCombo.setVisible(false);
				selectBranchLabel.setVisible(false);
				selectedBranch = Message.getValueString(personalInfo, "branch");
			}

			JSONObject json = new JSONObject();
			json.put("command", "ready");
			view.ready(json);
		});
	}

	/**
	 * Set Year Range
	 * <p>
	 * This method will loop around all the restaurant orders that were imported and
	 * checks to see which years these orders were initiated.
	 * 
	 * @param report    - Information regarding the orders from different
	 *                  restaurants.
	 * @param arrayYear - displays an ArrayList that holds only years that orders
	 *                  were placed.
	 * @author Tomer Meidan
	 */
	public void setYearRange(JSONArray report, ArrayList<String> arrayYear) {

		for (int i = 0; i < report.size(); i++) {
			JSONObject restaurant = (JSONObject) report.get(i);
			String year = Message.getValueString(restaurant, "dateYear");
			if (!arrayYear.contains(year)) {
				selectYearCombo.getItems().add(year);
				arrayYear.add(year);
			}

		}

	}

	/**
	 * This method sets the JSONObject that holds all the reports given from the
	 * database report tables: income_report, items_report and performance_report.
	 * 
	 * @param restaurantsReportData - holds all the report related information
	 * @author Tomer Meidan
	 */
	public void setRestaurantsReportData(JSONObject restaurantsReportData) {

		this.restaurantsReportData = restaurantsReportData;

		JSONArray incomeReportsData = (JSONArray) restaurantsReportData.get("income reports data");
		JSONArray itemsReportsData = (JSONArray) restaurantsReportData.get("items reports data");
		JSONArray performanceReportsData = (JSONArray) restaurantsReportData.get("performance reports data");
		boolean flag = true;
		arrayYear = new ArrayList<>();

		if (incomeReportsData.equals(null)) {
			flag = false;
			Logger.log(Level.DEBUG,
					"ViewMonthlyReportsWindow: setRestaurantsReportData: income reports data was not transferred successfuly");
			System.out.println(
					"ViewMonthlyReportsWindow: setRestaurantsReportData: income reports data was not transferred successfuly");
		} else
			setYearRange(incomeReportsData, arrayYear);

		if (itemsReportsData.equals(null)) {
			flag = false;
			Logger.log(Level.DEBUG,
					"ViewMonthlyReportsWindow: setRestaurantsReportData: items reports data was not transferred successfuly");
			System.out.println(
					"ViewMonthlyReportsWindow: setRestaurantsReportData: items reports data was not transferred successfuly");
		} else
			setYearRange(itemsReportsData, arrayYear);

		if (performanceReportsData.equals(null)) {
			flag = false;
			Logger.log(Level.DEBUG,
					"ViewMonthlyReportsWindow: setRestaurantsReportData: performance reports data was not transferred successfuly");
			System.out.println(
					"ViewMonthlyReportsWindow: setRestaurantsReportData: performance reports data was not transferred successfuly");
		} else
			setYearRange(performanceReportsData, arrayYear);

		if (flag == true) {
			Logger.log(Level.DEBUG,
					"ViewMonthlyReportsWindow: setRestaurantsReportData: restaurants reports have been transferred successfuly");
			System.out.println(
					"ViewMonthlyReportsWindow: setRestaurantsReportData: restaurants reports have been transferred successfuly");
		}

	}

	/**
	 * On Select Report Option
	 * <p>
	 * 
	 * This is an FXML trigger on the button Select report type. The method will
	 * activate on user report select and save the selected option on a global
	 * variable.
	 * @author Tomer Meidan
	 */
	@FXML
	void onSelectReportOption(ActionEvent event) {

		selectedReport = selectReportCombo.getSelectionModel().getSelectedItem();

		Logger.log(Level.DEBUG,
				"ViewMonthlyReportsWindow: onSelectReportOption: user selected report " + selectedReport);
		System.out.println("ViewMonthlyReportsWindow: onSelectReportOption: user selected report " + selectedReport);

		if (!validateSelectedOptions())
			return;

		approveDateButton.disableProperty().set(false);
	}

	/**
	 * On Select Branch Option
	 * <p>
	 * 
	 * This is an FXML trigger on the button Select branch type. The method will
	 * activate on user branch select and save the selected option on a global
	 * variable.
	 * @author Tomer Meidan
	 */
	@FXML
	void onSelectBranchOption(ActionEvent event) {

		selectedBranch = selectBranchCombo.getSelectionModel().getSelectedItem();

		Logger.log(Level.DEBUG,
				"ViewMonthlyReportsWindow: onSelectBranchOption: user selected branch " + selectedBranch);
		System.out.println("ViewMonthlyReportsWindow: onSelectBranchOption: user selected branch " + selectedBranch);

		if (!validateSelectedOptions())
			return;

		approveDateButton.disableProperty().set(false);

	}

	/**
	 * On Select Month Combo
	 * <p>
	 * 
	 * This is an FXML trigger on the button Select month type. The method will
	 * activate on user month select and save the selected option on a global
	 * variable.
	 * @author Tomer Meidan
	 */
	@FXML
	void onSelectMonthCombo(ActionEvent event) {
		selectedMonthDate = selectMonthCombo.getSelectionModel().getSelectedItem();
		if (!validateSelectedOptions())
			return;

		approveDateButton.disableProperty().set(false);
	}

	/**
	 * On Select Year Combo
	 * <p>
	 * 
	 * This is an FXML trigger on the button Select year type. The method will
	 * activate on user year select and save the selected option on a global
	 * variable.
	 * @author Tomer Meidan
	 */
	@FXML
	void onSelectYearCombo(ActionEvent event) {
		selectedYearDate = selectYearCombo.getSelectionModel().getSelectedItem();
		if (!validateSelectedOptions())
			return;

		approveDateButton.disableProperty().set(false);
	}

	/**
	 * On Back Button
	 * <p>
	 * 
	 * This is an FXML trigger on the button back. The method will activate on user
	 * pressed back button, a destructor for all related variables to the class will
	 * initiate in order to reset their values and a method will return to the
	 * previous window whether it is a branch manager or a CEO.
	 * @author Tomer Meidan
	 */
	@FXML
	void onBackButton(ActionEvent event) {

		// Destructor ------------------
		anchorPaneID.getChildren().clear();
		messageLabel.setText("");

		selectBranchCombo.valueProperty().set("Choose");
		selectReportCombo.valueProperty().set("Choose");
		selectYearCombo.getItems().clear();
		selectMonthCombo.valueProperty().set("");
		selectedBranch = "";
		selectedReport = "";
		selectedMonthDate = null;
		selectedYearDate = null;
		approveDateButton.disableProperty().set(true);
		arrayYear = null;
		restaurantsReportData = null;
		HashMap<String, Integer> restaurantOrdersMap = null;
		filteredRestaurantsData = null;
		maxOrderInformation = null;
		maxIncomeInformation = null;

		maxOrderInformation = new JSONObject();
		maxIncomeInformation = new JSONObject();
		// -----------------------------

		Logger.log(Level.DEBUG, "ViewMonthlyReportsWindow: onBackButton: user clicked on Back button!");
		System.out.println("ViewMonthlyReportsWindow: onBackButton: user clicked on Back button!");

		showPrevWindow();

	}

	private void showPrevWindow() {
		if (view instanceof CEOPortalView)
			((CEOPortalView) view).getCeoWindow().showWindow();
		else if (view instanceof BranchManagerPortalView)
			((BranchManagerPortalView) view).showBranchManagerHomePage();
	}

	/**
	 * On Approve Date Button
	 * <p>
	 * 
	 * On clicking the Approve button in monthly reports screen, the method will
	 * filter from orders JSONOBject all unnecessary orders that don't match the
	 * correct chosen month\year\branch by the user.
	 * 
	 * @author Tomer Meidan
	 */
	@FXML
	void onApproveDateButton(ActionEvent event) {
		Platform.runLater(() -> {

			anchorPaneID.getChildren().clear();
			approveDateButton.disableProperty().set(true);
			preparingReportData();
			switch (selectedReport) {
			case "Income":
				getMostValuesFromReport(filteredRestaurantsData, maxIncomeInformation, maxOrderInformation);
				createIncomeReportGraph(filteredRestaurantsData, maxIncomeInformation, maxOrderInformation,
						anchorPaneID);
				break;
			case "Meals":
				createItemsReportGraph();
				Logger.log(Level.DEBUG, "ViewMonthlyReportsWindow: onIncomeReportsButton: displays Income Reports");
				System.out.println("ViewMonthlyReportsWindow: onIncomeReportsButton: displays Income Reports");
				break;
			case "Performance":
				createPerformanceReportGraph();
				break;
			default:

				break;
			}

		});

	}

	/** Preparing Report Data<p>
	 * This method takes the global object of all the related report information,
	 * and filter them by the request of the user. For example, if the user requested
	 * to see Income reports, then the method will update the object filteredRestaurantsData
	 * with all the related data about income reports on the month and year and branch the user chose to see.
	 * 
	 * @Note filteredRestaurantsData - holds related data for a specific time and branch.
	 * @Note restaurantsReportData - Holds all information about reports (Income, Items and Performance)
	 * 
	 * @author Tomer Meidan
	 * */
	@SuppressWarnings("unchecked")
	public void preparingReportData() {

		// Message appears near the APPROVE button
		messageLabel.setText("");

		filteredRestaurantsData = new JSONArray();

		Integer releventCount = 0;
		Integer size;

		switch (selectedReport) {
		case "Income":
			JSONArray incomeReportData = (JSONArray) restaurantsReportData.get("income reports data");
			size = incomeReportData.size();
			for (int i = 0; i < size; i++) {
				JSONObject restaurantOrder = (JSONObject) incomeReportData.get(i);
				if (relevantReportData(restaurantOrder)) {
					filteredRestaurantsData.add(restaurantOrder);
					releventCount++;
				}
			}
			break;
		case "Meals":

			JSONArray itemsReportData = (JSONArray) restaurantsReportData.get("items reports data");
			size = itemsReportData.size();

			JSONArray relevantRestaurantItems = new JSONArray();
			boolean isNewRestaurant = true;
			String currentRestaurantName = "";

			for (int i = 0; i < size; i++) {
				JSONObject singleRestaurantItem = (JSONObject) itemsReportData.get(i);
				String restaurantName = Message.getValueString(singleRestaurantItem, "restaurantName");
				if (isNewRestaurant) {
					currentRestaurantName = Message.getValueString(singleRestaurantItem, "restaurantName");
					isNewRestaurant = false;
				}

				boolean relevant = relevantReportData(singleRestaurantItem);
				if (relevant) {
					if (currentRestaurantName.equals(restaurantName)) {
						relevantRestaurantItems.add(singleRestaurantItem);
						releventCount++;
					} else {
						filteredRestaurantsData.add(relevantRestaurantItems);
						relevantRestaurantItems = new JSONArray();
						currentRestaurantName = Message.getValueString(singleRestaurantItem, "restaurantName");
						relevantRestaurantItems.add(singleRestaurantItem);
						releventCount++;
					}
				}
			}

			if (!relevantRestaurantItems.isEmpty()) {
				filteredRestaurantsData.add(relevantRestaurantItems);
			}
			break;
		case "Performance":
			JSONArray performanceReportData = (JSONArray) restaurantsReportData.get("performance reports data");
			size = performanceReportData.size();
			for (int i = 0; i < size; i++) {
				JSONObject restaurantPerformance = (JSONObject) performanceReportData.get(i);
				if (relevantReportData(restaurantPerformance)) {
					filteredRestaurantsData.add(restaurantPerformance);
					releventCount++;
				}
			}
			break;
		default:
			break;
		}

		Logger.log(Level.DEBUG, "ViewMonthlyReportsWindow: onApproveDateButton: user selected a date. Year = "
				+ selectedYearDate + ", Month = " + selectedMonthDate + ", Number of relevent data = " + releventCount);
		System.out.println("ViewMonthlyReportsWindow: onApproveDateButton: user selected a date: Year = "
				+ selectedYearDate + ", Month = " + selectedMonthDate + ", Number of relevent data = " + releventCount);
		releventCount = 0;
	}

	/** Relevant Report Data<p>
	 * 
	 * This method checks to see if the restaurant is relevant for this month, year
	 * and branch.
	 * @param restaurantInfo - JSON object holding related information about items, orders and performance 
	 * on a restaurant, the object also holds variables such as the month, year and branch of the order in the 
	 * restaurant.
	 * @author Tomer Meidan
	 * 
	 * */
	public boolean relevantReportData(JSONObject restaurantInfo) {

		String orderBranch = Message.getValueString(restaurantInfo, "branch");
		String year = Message.getValueString(restaurantInfo, "dateYear");
		String month = Message.getValueString(restaurantInfo, "dateMonth");

		if (year == null || month == null) {
			Logger.log(Level.DEBUG,
					"ViewMonthlyReportsWindow: onApproveDateButton: relevantReportData: orderDate is invalid");
			System.out
					.println("ViewMonthlyReportsWindow: onApproveDateButton: relevantReportData: orderDate is invalid");
			return false;
		}
		if (year.equals(selectedYearDate) && month.equals(selectedMonthDate) && orderBranch.equals(selectedBranch))
			return true;
		return false;

	}

	/** Get Most Values From Report<p>
	 * 
	 * This method loops on the all related restaurants in a certain branch, month and year and determines which restaurants
	 * has the most income or orders in that specific time frame.
	 * 
	 * @param filteredRestaurantsData - This JSON holds all information regarding a restaurant in a certain month, year and branch.
	 * @param maxOrderInformation     - Holds the most restaurant with the most orders.
	 * @param maxIncomeInformation    - Holds the most restaurant with the most income.
	 * @author Tomer Meidan
	 * */
	@SuppressWarnings("unchecked")
	public void getMostValuesFromReport(JSONArray filteredRestaurantsData, JSONObject maxIncomeInformation,
			JSONObject maxOrderInformation) {

		Integer size = filteredRestaurantsData.size();
		Integer totalIncome = 0, totalOrders = 0;
		Integer mostOrders = 0, mostIncome = 0;
		long tempLong;
		String restaurantWithMostOrders = "", restaurantWithMostIncome = "";
		String restaurantName = "";

		for (int i = 0; i < size; i++) {
			JSONObject restaurantInfo = (JSONObject) filteredRestaurantsData.get(i);

			restaurantName = Message.getValueString(restaurantInfo, "restaurantName");
			tempLong = (long) restaurantInfo.get("totalIncome");
			totalIncome = (int) tempLong;
			tempLong = (long) restaurantInfo.get("totalOrders");
			totalOrders = (int) tempLong;

			// Setting the current restaurant with the highest income
			if (totalIncome > mostIncome) {
				restaurantWithMostIncome = restaurantName;
				mostIncome = totalIncome;
			}

			// Setting the current restaurant with the most orders
			if (totalOrders > mostOrders) {
				mostOrders = totalOrders;
				restaurantWithMostOrders = restaurantName;
			}

		}

		maxOrderInformation.put("maxOrders", mostOrders);
		maxOrderInformation.put("restaurantWithMaxOrders", restaurantWithMostOrders);
		maxIncomeInformation.put("maxIncome", mostIncome);
		maxIncomeInformation.put("restaurantWithMaxIncome", restaurantWithMostIncome);

	}

	
	/** Get Total Value From All Restaurants<p>
	 * 
	 * This method takes all the restaurants from a certain month and branch and sums up all
	 * the related values into one total variable and returns it. For example: if the input String totalName is 
	 * totalOrders, then the method will sum up all the orders from restaurants and return it.
	 * 
	 * @param filteredRestaurantsData - This JSON holds all information regarding a restaurant in a certain month, year and branch.
	 * @param totalName - Specific field of data to return a certain total amount of the restaurants.
	 * @return Returns the total sum of certain value in the restaurants (income amount, order amount and so on)
	 * 
	 * @author Tomer Meidan
	 * */
	public int getTotalValueFromAllRestaurants(JSONArray filteredRestaurantsData, String totalName) {
		long total = 0;
		for (int i = 0; i < filteredRestaurantsData.size(); i++) {

			JSONObject restaurantValues = (JSONObject) filteredRestaurantsData.get(i);
			total += (long) restaurantValues.get(totalName);

		}
		return (int) total;

	}

	/**
	 * createIncomeReportGraph the method takes all the information from the object
	 * filteredRestaurantsData and creates two bar charts accordingly. one bar chart
	 * represents a histogram, xAxis is the income divided into classes according to
	 * the highest income value. and another bar chart is also a histogram which
	 * represents the amount orders of the restaurants branch.
	 * 
	 * @param maxOrderInformation     - Holds the most restaurant with the most orders.
	 * @param maxIncomeInformation    - Holds the most restaurant with the most income.
	 * @param filteredRestaurantsData - is filtered to hold only relevant data to
	 *       month,year and branch that was chosen
	 * @param anchorPaneID            - Represents the main built in FX on scene builder that holds the graph.
	 * @author Tomer Meidan
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void createIncomeReportGraph(JSONArray filteredRestaurantsData, JSONObject maxIncomeInformation,
			JSONObject maxOrderInformation, AnchorPane anchorPaneID) {

		Platform.runLater(() -> {

			HBox mainHBox = new HBox();
			anchorPaneID.getChildren().add(mainHBox);

			// Creation of Income Graph
			StackedBarChart incomeStackedBarChart = incomeGraph(filteredRestaurantsData, maxIncomeInformation);

			// Creation of Order Amount Graph
			StackedBarChart ordersStackedBarChart = orderCountGraph(filteredRestaurantsData, maxOrderInformation);

			// Creation of switch between graphs button
			switchBetweenGraphsButton(mainHBox, incomeStackedBarChart, ordersStackedBarChart);

			Separator verticalSeperator = new Separator();
			verticalSeperator.setOrientation(Orientation.VERTICAL);
			verticalSeperator.setMaxHeight(390);
			mainHBox.setSpacing(5);
			mainHBox.getChildren().add(verticalSeperator);

			// Updating the graph's information with the most orders and most income
			Integer mostOrders = (Integer) maxOrderInformation.get("maxOrders");
			String mostOrdersRestaurant = Message.getValueString(maxOrderInformation, "restaurantWithMaxOrders");

			Integer mostIncome = (Integer) maxIncomeInformation.get("maxIncome");
			String mostIncomeRestaurant = Message.getValueString(maxIncomeInformation, "restaurantWithMaxIncome");

			// Important facts about the orders will go into this vbox
			VBox sideIncomeInfo = new VBox();
			sideIncomeInfo.setPadding(new Insets(10, 0, 0, 0));
			mainHBox.getChildren().add(sideIncomeInfo);

			// Most income details
			Integer totalRestaurantsIncome = getTotalValueFromAllRestaurants(filteredRestaurantsData, "totalIncome");
			Label mostIncomeTitleLabel = new Label("Income Info");
			mostIncomeTitleLabel.setFont(Font.font(18));
			mostIncomeTitleLabel.setStyle("-fx-font-weight: bold");
			Label mostIncomeValueLabel = new Label(
					"Most income: " + mostIncomeRestaurant + " generated " + mostIncome + " (NIS)");
			Label totalRestaurantsIncomeLabel = new Label("Total income: " + totalRestaurantsIncome + " (NIS)");

			Separator horizontalSeperator2 = new Separator();
			horizontalSeperator2.setMaxWidth(Control.USE_COMPUTED_SIZE);
			horizontalSeperator2.setPadding(new Insets(5, 0, 5, 0));

			sideIncomeInfo.getChildren().addAll(mostIncomeTitleLabel, mostIncomeValueLabel, totalRestaurantsIncomeLabel,
					horizontalSeperator2);

			// Most orders details
			Integer totalRestaurantsOrders = getTotalValueFromAllRestaurants(filteredRestaurantsData, "totalOrders");
			Label mostOrdersTitleLabel = new Label("Orders Info");
			mostOrdersTitleLabel.setFont(Font.font(18));
			mostOrdersTitleLabel.setStyle("-fx-font-weight: bold");
			Label mostOrdersValueLabel = new Label(
					"Most orders: " + mostOrdersRestaurant + " with " + mostOrders + " (ORDERS)");
			Label totalRestaurantsOrdersLabel = new Label("Total orders: " + totalRestaurantsOrders + " (ORDERS)");

			Separator horizontalSeperator1 = new Separator();
			horizontalSeperator1.setMaxWidth(Control.USE_COMPUTED_SIZE);
			horizontalSeperator1.setPadding(new Insets(5, 0, 5, 0));

			sideIncomeInfo.getChildren().addAll(mostOrdersTitleLabel, mostOrdersValueLabel, totalRestaurantsOrdersLabel,
					horizontalSeperator1);

			TableView incomeTableView = new TableView();
			incomeTableView.setMaxHeight(220);
			TableColumn<Map, String> restaurantNameColumn = new TableColumn<>("Restaurant");
			restaurantNameColumn.setCellValueFactory(new MapValueFactory<>("Restaurant"));

			TableColumn<Map, String> incomeColumn = new TableColumn<>("Income");
			incomeColumn.setCellValueFactory(new MapValueFactory<>("Income"));

			TableColumn<Map, String> orderCountColumn = new TableColumn<>("Order Amount");
			orderCountColumn.setCellValueFactory(new MapValueFactory<>("Order Amount"));

			incomeTableView.getColumns().addAll(restaurantNameColumn, incomeColumn, orderCountColumn);
			ObservableList<Map<String, Object>> items = FXCollections.<Map<String, Object>>observableArrayList();

			for (int i = 0; i < filteredRestaurantsData.size(); i++) {
				Map<String, Object> singleRow = new HashMap<>();
				JSONObject restaurantValues = (JSONObject) filteredRestaurantsData.get(i);
				String restaurantName = Message.getValueString(restaurantValues, "restaurantName");
				long totalOrders = (long) restaurantValues.get("totalOrders");
				long totalIncome = (long) restaurantValues.get("totalIncome");

				singleRow.put("Restaurant", restaurantName);
				singleRow.put("Income", totalIncome);
				singleRow.put("Order Amount", totalOrders);

				items.add(singleRow);
			}

			incomeTableView.getItems().addAll(items);
			sideIncomeInfo.getChildren().add(incomeTableView);

		});

	}

	/**
	 * Switch Between Graphs Button
	 * <p>
	 * 
	 * This method is controlled by a dynamically allocated button in the monthly
	 * report graph and controlling which type of graph to show to the user. The
	 * default graph that shows is the income graph but upon pressing the Next graph
	 * button, this method will initiate and switch to a different graph upon press.
	 * 
	 * @param mainHBox              - This is the main Hbox that the graphs are
	 *                              inside of.
	 * @param incomeStackedBarChart - This stack bar chart represent the income bar
	 *                              graph.
	 * @param ordersStackedBarChart - This stack bar chart represent the orders bar
	 *                              graph.
	 * @author Tomer Meidan
	 */
	private void switchBetweenGraphsButton(HBox mainHBox, StackedBarChart incomeStackedBarChart,
			StackedBarChart ordersStackedBarChart) {

		VBox vboxStackedBars = new VBox();
		Button nextGraphButton = new Button("Show order count");
		Pane pane = new Pane();
		pane.setPadding(new Insets(0, 0, 0, 10));
		HBox hbox3 = new HBox();
		hbox3.getChildren().addAll(pane, nextGraphButton);
		vboxStackedBars.getChildren().add(hbox3);
		nextGraphButton.setOnAction(new EventHandler<ActionEvent>() {
			boolean flag = true;
			StackedBarChart ordersChart = ordersStackedBarChart;
			StackedBarChart incomeChart = incomeStackedBarChart;
			Button button = nextGraphButton;

			@Override
			public void handle(ActionEvent event) {
				if (flag == true) {
					vboxStackedBars.getChildren().remove(incomeChart);
					vboxStackedBars.getChildren().add(ordersChart);
					button.setText("Show income count");
					flag = false;
				} else {
					vboxStackedBars.getChildren().remove(ordersChart);
					vboxStackedBars.getChildren().add(incomeChart);
					button.setText("Show order count");
					flag = true;
				}
			}
		});

		vboxStackedBars.getChildren().add(incomeStackedBarChart);
		mainHBox.getChildren().add(vboxStackedBars);
	}

	/**
	 * Income Graph
	 * <p>
	 * 
	 * This method creates dynamically a bar chart for all the income from
	 * restaurants in a certain branch.
	 * 
	 * @param filteredRestaurantsData - Contains all related information from
	 *                                restaurants in a certain branch about income
	 *                                amount.
	 * @param maxOrderInformation     - Holds information about which restaurant has
	 *                                the most income.
	 * @return Returns a bar chart of all the income from restaurants in a certain
	 *         branch by histogram look.
	 * @author Tomer Meidan
	 */
	public StackedBarChart incomeGraph(JSONArray filteredRestaurantsData, JSONObject maxIncomeInformation) {
		// Defining the axes
		CategoryAxis xAxis = new CategoryAxis();
		xAxis.setLabel("Income in New Israeli Shekel (NIS)");
		int numOfRestaurantsInGraph = filteredRestaurantsData.size();
		NumberAxis yAxis = new NumberAxis(0, numOfRestaurantsInGraph, 1);
		yAxis.setLabel("Restaurants No.");

//		yAxis.setTickLabelFormatter();
		// Creating the Bar chart
		StackedBarChart incomeStackedBarChart = new StackedBarChart(xAxis, yAxis);
		incomeStackedBarChart.setMaxWidth(Control.USE_COMPUTED_SIZE);
		incomeStackedBarChart.setMaxHeight(Control.USE_COMPUTED_SIZE);
		// Setting animation of graph to false for graph bug reasons
		xAxis.setAnimated(false);

		// Initiating the graph X and Y columns
		XYChart.Series seriesIncome = new XYChart.Series<>();

		// Setting paramater names for the graph
		seriesIncome.setName("Income");

		Integer maxIncomeValue = (Integer) maxIncomeInformation.get("maxIncome");
		Integer classWidth = maxIncomeValue / 5;
		String columnName = "";

		// Setting columns for the income amount graph
		seriesIncome.getData().add(new XYChart.Data("0" + " - " + classWidth, 0));
		seriesIncome.getData().add(new XYChart.Data(classWidth + 1 + " - " + classWidth * 2, 0));
		seriesIncome.getData().add(new XYChart.Data(classWidth * 2 + 1 + " - " + classWidth * 3, 0));
		seriesIncome.getData().add(new XYChart.Data(classWidth * 3 + 1 + " - " + classWidth * 4, 0));
		seriesIncome.getData().add(new XYChart.Data(classWidth * 4 + 1 + " - " + maxIncomeValue, 0));

		// Creating all the histogram graph's columns
		for (int i = 0; i < filteredRestaurantsData.size(); i++) {
			JSONObject restaurantValues = (JSONObject) filteredRestaurantsData.get(i);
			long totalIncome = (long) restaurantValues.get("totalIncome");

			if (totalIncome >= classWidth * 4 && totalIncome <= maxIncomeValue)
				columnName = classWidth * 4 + 1 + " - " + maxIncomeValue;
			else if (totalIncome >= classWidth * 3 && totalIncome <= classWidth * 4)
				columnName = classWidth * 3 + 1 + " - " + classWidth * 4;
			else if (totalIncome >= classWidth * 2 && totalIncome <= classWidth * 3)
				columnName = classWidth * 2 + 1 + " - " + classWidth * 3;
			else if (totalIncome >= classWidth * 1 && totalIncome <= classWidth * 2)
				columnName = classWidth + 1 + " - " + classWidth * 2;
			else
				columnName = "0" + " - " + classWidth;

			seriesIncome.getData().add(new XYChart.Data(columnName, 1));
			columnName = "";
		}

		// Adding the columns as children of the stack bar chart
		incomeStackedBarChart.getData().add(seriesIncome);
		return incomeStackedBarChart;
	}

	/**
	 * Order Count Graph
	 * <p>
	 * 
	 * This method creates dynamically a bar chart for all the orders from
	 * restaurants in a certain branch.
	 * 
	 * @param filteredRestaurantsData - Contains all related information from
	 *                                restaurants in a certain branch about order
	 *                                count.
	 * @param maxOrderInformation     - Holds information about which restaurant has
	 *                                the most orders.
	 * @return Returns a bar chart of all the orders from restaurants in a certain
	 *         branch by histogram look.
	 * @author Tomer Meidan
	 */
	@SuppressWarnings("unchecked")
	public StackedBarChart orderCountGraph(JSONArray filteredRestaurantsData, JSONObject maxOrderInformation) {
		Integer classWidth;
		String columnName;
		CategoryAxis xAxis2 = new CategoryAxis();
		int numOfRestaurantsInGraph = filteredRestaurantsData.size();
		NumberAxis yAxis2 = new NumberAxis(0, numOfRestaurantsInGraph, 1);
		xAxis2.setLabel("Orders amount");
		yAxis2.setLabel("Restaurants No.");

		// Creating the second Bar chart
		StackedBarChart ordersStackedBarChart = new StackedBarChart(xAxis2, yAxis2);
		ordersStackedBarChart.setMaxWidth(Control.USE_COMPUTED_SIZE);
		ordersStackedBarChart.setMaxHeight(Control.USE_COMPUTED_SIZE);

		XYChart.Series seriesOrderCount = new XYChart.Series<>();
		seriesOrderCount.setName("Order Count");

		Integer maxOrderValue = (Integer) maxOrderInformation.get("maxOrders");
		classWidth = maxOrderValue / 5;
		columnName = "";

		// Setting columns for order amount graph
		seriesOrderCount.getData().add(new XYChart.Data("0" + " - " + classWidth, 0));
		seriesOrderCount.getData().add(new XYChart.Data(classWidth + 1 + " - " + classWidth * 2, 0));
		seriesOrderCount.getData().add(new XYChart.Data(classWidth * 2 + 1 + " - " + classWidth * 3, 0));
		seriesOrderCount.getData().add(new XYChart.Data(classWidth * 3 + 1 + " - " + classWidth * 4, 0));
		seriesOrderCount.getData().add(new XYChart.Data(classWidth * 4 + 1 + " - " + maxOrderValue, 0));

		// Creating all the histogram graph's columns
		for (int i = 0; i < filteredRestaurantsData.size(); i++) {
			JSONObject restaurantValues = (JSONObject) filteredRestaurantsData.get(i);
			long totalOrders = (long) restaurantValues.get("totalOrders");

			if (totalOrders >= classWidth * 4 + 1 && totalOrders <= maxOrderValue)
				columnName = classWidth * 4 + 1 + " - " + maxOrderValue;
			else if (totalOrders >= classWidth * 3 + 1 && totalOrders <= classWidth * 4)
				columnName = classWidth * 3 + 1 + " - " + classWidth * 4;
			else if (totalOrders >= classWidth * 2 + 1 && totalOrders <= classWidth * 3)
				columnName = classWidth * 2 + 1 + " - " + classWidth * 3;
			else if (totalOrders >= classWidth + 1 && totalOrders <= classWidth * 2)
				columnName = classWidth + 1 + " - " + classWidth * 2;
			else
				columnName = "0" + " - " + classWidth;

			seriesOrderCount.getData().add(new XYChart.Data(columnName, 1));
			columnName = "";
		}

		// Adding the columns as children of the stack bar chart
		ordersStackedBarChart.getData().add(seriesOrderCount);
		return ordersStackedBarChart;
	}

	/**
	 * createItemsReportGraph the method takes all the information from the object
	 * filteredRestaurantsData and creates a bar chart accordingly. xAxis represents
	 * all the items that the restaurants have (Main Dish, Drinks, Side Dish...) and
	 * yAxis is the amount of the item from all the restaurants combined.
	 * 
	 * @Note filteredRestaurantsData - is filtered to hold only relevant data to
	 *       month,year and branch that was chosen
	 *       @author Tomer Meidan
	 */
	private void createItemsReportGraph() {

		messageLabel.setText("");

		HBox mainHBox = new HBox();
		anchorPaneID.getChildren().add(mainHBox);

		// Setting columns
		HashMap<String, Integer> items = new HashMap<>();
		VBox restaurantRows = new VBox();
		restaurantRows.setPadding(new Insets(10, 0, 0, 30));

		// Item table related variables ////////////////////////////////////////////////
		TableView tableView = new TableView();
		tableView.setMaxHeight(300);
		ObservableList<Map<String, Object>> tableItems = FXCollections.<Map<String, Object>>observableArrayList();

		TableColumn<Map, String> restaurantNameColumn = new TableColumn<>("Restaurant");
		restaurantNameColumn.setCellValueFactory(new MapValueFactory<>("Restaurant"));
		TableColumn<Map, String> column;
		tableView.getColumns().add(restaurantNameColumn);

		/////////////////////////////////////////////////////////////////////////////////

		ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

		int numOfBranchRestaurants = filteredRestaurantsData.size();
		String currentItemType = "", itemTypeInCheck = "";
		long itemTypeAmount = 0;
		for (int i = 0; i < numOfBranchRestaurants; i++) {

			JSONArray restaurantItems = (JSONArray) filteredRestaurantsData.get(i);
			boolean firstEnterance = true;
			Map<String, Object> tableRow = new HashMap<>();

			// Creating all the histogram graph's columns ///////////////////////////////
			for (int j = 0; j < restaurantItems.size(); j++) {
				JSONObject restaurantSingleItemType = (JSONObject) restaurantItems.get(j);
				itemTypeInCheck = Message.getValueString(restaurantSingleItemType, "itemType");
				if (firstEnterance) {
					String restaurantName = Message.getValueString(restaurantSingleItemType, "restaurantName");
					tableRow.put("Restaurant", restaurantName);
					currentItemType = itemTypeInCheck;
					firstEnterance = false;
				}

				if (currentItemType.equals(itemTypeInCheck))
					itemTypeAmount += (long) restaurantSingleItemType.get("itemCount");
				else {
					tableRow.put(currentItemType, itemTypeAmount);

					addToTable(items, tableView, currentItemType, itemTypeAmount);
					currentItemType = itemTypeInCheck;
					itemTypeAmount = (long) restaurantSingleItemType.get("itemCount");

				}

			}

			tableRow.put(currentItemType, itemTypeAmount);
			addToTable(items, tableView, currentItemType, itemTypeAmount);
			tableItems.add(tableRow);
			itemTypeAmount = 0;
		}
		tableView.getItems().addAll(tableItems);

		PieChart itemsPieChart = new PieChart();

		itemsPieChart.setLabelLineLength(20);
		itemsPieChart.setLegendSide(Side.LEFT);
		itemsPieChart.setTitle("Restaurants Items Chart");

		Label caption = new Label("");
		caption.setTextFill(Color.DARKORANGE);
		caption.setStyle("-fx-font: 24 arial;");
		caption.setPadding(new Insets(0, 0, 0, 10));

		vboxStackedBars = new VBox();
		vboxStackedBars.setPadding(new Insets(0, 0, 10, 20));
		// anchor pane to hold the chart
		Button nextGraphButton = new Button("Show item distribution");
		Pane pane = new Pane();
		pane.setPadding(new Insets(2, 0, 5, 10));
		HBox nextGraphButtonHBox = new HBox();
		nextGraphButtonHBox.getChildren().addAll(pane, nextGraphButton, caption);
		vboxStackedBars.getChildren().add(nextGraphButtonHBox);
		vboxStackedBars.getChildren().add(pane);

		vboxStackedBars.getChildren().add(itemsPieChart);

		mainHBox.getChildren().add(vboxStackedBars);
		verticalSeperator = new Separator();
		verticalSeperator.setOrientation(Orientation.VERTICAL);
		verticalSeperator.setMaxHeight(390);
		mainHBox.setSpacing(5);
		mainHBox.getChildren().add(verticalSeperator);

		// Important facts about the orders will go into this vbox
		VBox relatedInfoVBox = new VBox();
		mainHBox.getChildren().add(relatedInfoVBox);

		Label itemTitleLabel = new Label("View item amount");
		itemTitleLabel.setFont(Font.font(18));
		itemTitleLabel.setStyle("-fx-font-weight: bold");

		Label itemAmountLabel = new Label("Press on a pie chart piece in order to\n see the amount of the item.");
		Separator horizontalSeperator1 = new Separator();
		horizontalSeperator1.setMaxWidth(Control.USE_COMPUTED_SIZE);
		horizontalSeperator1.setPadding(new Insets(5, 0, 5, 0));

		relatedInfoVBox.getChildren().addAll(itemTitleLabel, itemAmountLabel, horizontalSeperator1);

		// Amount of Items
		for (Map.Entry<String, Integer> item : items.entrySet()) {

			String itemType = item.getKey();
			Integer itemAmount = item.getValue();

			pieChartData.add(new PieChart.Data(itemType, itemAmount));
		}

		itemsPieChart.getData().addAll(pieChartData);

		for (PieChart.Data data : itemsPieChart.getData()) {
			data.getNode().addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent e) {
					caption.setText(String.valueOf(data.getName() + " : " + (int) data.getPieValue()));
				}
			});
		}

		nextGraphButton.setOnAction(new EventHandler<ActionEvent>() {
			boolean flag = true;
			VBox relatedInfoVBox1 = relatedInfoVBox;

			@Override
			public void handle(ActionEvent event) {
				if (flag == true) {
					caption.setText("");
					vboxStackedBars.getChildren().remove(itemsPieChart);
					vboxStackedBars.getChildren().add(tableView);
					mainHBox.getChildren().removeAll(relatedInfoVBox1, verticalSeperator);

					nextGraphButton.setText("Show items graph");
					flag = false;
				} else {
					caption.setText("");
					vboxStackedBars.getChildren().remove(tableView);
					vboxStackedBars.getChildren().add(itemsPieChart);
					mainHBox.getChildren().addAll(verticalSeperator, relatedInfoVBox1);
					nextGraphButton.setText("Show items distribution");
					flag = true;
				}
			}
		});

		Logger.log(Level.DEBUG, "ViewMonthlyReportsWindow: createItemsReportWindow: displays items reports");
		System.out.println("ViewMonthlyReportsWindow: createItemsReportWindow: displays items reports");
	}

	/**
	 * Add To Table
	 * <p>
	 * 
	 * This method takes an item from the list of items on a certain order and add
	 * it to the table of items. If the item already exists, then the item will be
	 * added to a certain column, else the table will create a new column for this
	 * specific item and put its value under it.
	 * 
	 * @param items           - HashMap that holds information on which item columns
	 *                        already exist in the table.
	 * @param tableView       - Dynamic FX table view that holds information
	 *                        regarding items from restaurants.
	 * @param currentItemType - A certain item type from a restaurant that will be
	 *                        added into the table.
	 * @param itemTypeAmount  - A certain item amount from a restaurant that will be
	 *                        added into the table.
	 *                        @author Tomer Meidan
	 * 
	 */
	private void addToTable(HashMap<String, Integer> items, TableView tableView, String currentItemType,
			long itemTypeAmount) {
		if (!items.containsKey(currentItemType)) {

			TableColumn<Map, String> column;
			items.put(currentItemType, (int) itemTypeAmount);
			column = new TableColumn<>(currentItemType);
			column.setCellValueFactory(new MapValueFactory<>(currentItemType));
			tableView.getColumns().add(column);
		} else {
			int previousItemAmount = items.get(currentItemType);
			int newItemAmount = (int) itemTypeAmount + previousItemAmount;
			items.put(currentItemType, newItemAmount);
		}
	}

	/**
	 * createPerformanceReportGraph the method takes all the information from the
	 * object filteredRestaurantsData and creates a bar chart accordingly.
	 * 
	 * @Note filteredRestaurantsData - is filtered to hold only relevant data to
	 *       month,year and branch that was chosen
	 *       @author Tomer Meidan
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void createPerformanceReportGraph() {

		messageLabel.setText("");

		HBox mainHBox = new HBox();
		anchorPaneID.getChildren().add(mainHBox);

		// Defining the axes
		CategoryAxis xAxis = new CategoryAxis();
		xAxis.setLabel("");
		NumberAxis yAxis = new NumberAxis();
		yAxis.setLabel("Delivery amount");

		// Creating the Bar chart
		StackedBarChart performanceStackedBarChart = new StackedBarChart(xAxis, yAxis);
		performanceStackedBarChart.setMaxWidth(Control.USE_COMPUTED_SIZE);
		performanceStackedBarChart.setMaxHeight(Control.USE_COMPUTED_SIZE);

		// Setting animation of graph to false for graph bug reasons
		xAxis.setAnimated(false);

		// Initiating the graph X and Y columns
		XYChart.Series seriesItem = new XYChart.Series<>();

		// Setting paramater names for the graph
		seriesItem.setName("Performance");

		// Setting columns

		// Creating all the histogram graph's columns
		int[] performanceValuesArray = createPerformanceValues();
		seriesItem.getData().add(new XYChart.Data("On Time", performanceValuesArray[0]));
		seriesItem.getData().add(new XYChart.Data("Late", performanceValuesArray[1]));

		performanceStackedBarChart.getData().add(seriesItem);
		mainHBox.getChildren().add(performanceStackedBarChart);

		Separator verticalSeperator = new Separator();
		verticalSeperator.setOrientation(Orientation.VERTICAL);
		verticalSeperator.setMaxHeight(390);
		mainHBox.setSpacing(5);
		mainHBox.getChildren().add(verticalSeperator);

		// Important facts about the orders will go into this vbox
		VBox vbox = new VBox();
		mainHBox.getChildren().add(vbox);

		// Time amounts and averages

		Label onTimeTitleLabel = new Label("On Time");
		onTimeTitleLabel.setFont(Font.font(18));
		onTimeTitleLabel.setStyle("-fx-font-weight: bold");
		if (performanceValuesArray[4] == 0)
			performanceValuesArray[4] = 1;
		Label onTimeAmountLabel = new Label("Amount: " + performanceValuesArray[0]);
		Label onTimeAverageLabel = new Label("Average Time: " + performanceValuesArray[2] / performanceValuesArray[4]);

		Separator horizontalSeperator1 = new Separator();
		horizontalSeperator1.setMaxWidth(Control.USE_COMPUTED_SIZE);
		horizontalSeperator1.setPadding(new Insets(5, 0, 5, 0));

		vbox.getChildren().addAll(onTimeTitleLabel, onTimeAmountLabel, onTimeAverageLabel, horizontalSeperator1);

		Label lateTimeTitleLabel = new Label("Late Time");
		lateTimeTitleLabel.setFont(Font.font(18));
		lateTimeTitleLabel.setStyle("-fx-font-weight: bold");
		if (performanceValuesArray[5] == 0)
			performanceValuesArray[5] = 1;
		Label lateTimeAmountLabel = new Label("Amount: " + performanceValuesArray[1]);
		Label lateTimeAverageLabel = new Label(
				"Average Time: " + performanceValuesArray[3] / performanceValuesArray[5]);
		Separator horizontalSeperator2 = new Separator();
		horizontalSeperator1.setMaxWidth(Control.USE_COMPUTED_SIZE);
		horizontalSeperator1.setPadding(new Insets(5, 0, 5, 0));

		vbox.getChildren().addAll(lateTimeTitleLabel, lateTimeAmountLabel, lateTimeAverageLabel, horizontalSeperator2);

		Logger.log(Level.DEBUG, "ViewMonthlyReportsWindow: createItemsReportWindow: displays items reports");
		System.out.println("ViewMonthlyReportsWindow: createItemsReportWindow: displays items reports");

	}

	/**
	 * createPerformanceValues Object of type int array which holds values regarding
	 * the performance of the restaurants.
	 * 
	 * @param performanceValuesArray
	 *                               <p>
	 *                               1) performanceValuesArray[0] = On Time count
	 *                               <br>
	 *                               2) performanceValuesArray[1] = Late Time count
	 *                               <br>
	 *                               3) performanceValuesArray[3] = Late Time
	 *                               Average <br>
	 *                               4) performanceValuesArray[4] = On Time Average
	 *                               count <br>
	 *                               5) performanceValuesArray[5] = Late Time
	 *                               Average count
	 *                               @author Tomer Meidan
	 */
	private int[] createPerformanceValues() {
		int[] performanceValuesArray = new int[6];
		for (int i = 0; i < filteredRestaurantsData.size(); i++) {

			JSONObject singleRestaurantPerformance = (JSONObject) filteredRestaurantsData.get(i);

			long onTimeCount = (long) singleRestaurantPerformance.get("onTimeCount");
			long lateTimeCount = (long) singleRestaurantPerformance.get("lateTimeCount");
			long onTimeAverage = (long) singleRestaurantPerformance.get("onTimeAverage");
			long lateTimeAverage = (long) singleRestaurantPerformance.get("lateTimeAverage");
			if (onTimeAverage > 0) {
				performanceValuesArray[0] += (int) onTimeCount;
				performanceValuesArray[2] += (int) onTimeAverage;
				performanceValuesArray[4]++;
			}
			if (lateTimeAverage > 0) {
				performanceValuesArray[1] += (int) lateTimeCount;
				performanceValuesArray[3] += (int) lateTimeAverage;
				performanceValuesArray[5]++;
			}
		}
		return performanceValuesArray;
	}

	/**
	 * validateSelectedOptions - Checks if the user chose Branch, Date and Report
	 * options.
	 * 
	 * @return true = Branch, Date and Report type were chosen, false = other.
	 * @author Tomer Meidan
	 */
	public boolean validateSelectedOptions() {

		if (selectedMonthDate == null || selectedYearDate == null)
			return false;
		else if (!selectedBranch.equals("North") && !selectedBranch.equals("South") && !selectedBranch.equals("Center"))
			return false;
		else if (!selectedReport.equals("Income") && !selectedReport.equals("Meals")
				&& !selectedReport.equals("Performance"))
			return false;
		return true;

	}
}
