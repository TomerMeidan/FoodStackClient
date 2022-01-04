package javaFXControllers.Customer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import clientSide.CustomerPortalView;
import common.DateParser;
import common.Logger;
import common.Logger.Level;
import common.Meal;
import common.Message;
import common.OptionalFeature;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import util.MyGrid;

/**
 * OrderWindow
 * 
 * This class is the javaFX controller for OrderWindowTemplate.fxml This class
 * holds primaryStage, scene, view.
 * 
 * @author mosa
 * @version 3/1/2022
 */
@SuppressWarnings("unchecked")
public class OrderWindow {

	private final static String FOLDER_NAME = "/images/";
	private Stage primaryStage;
	private Scene scene;
	private HBox orderHBox;
	private CustomerPortalView view;

	private JSONArray restaurantList;
	private JSONObject menu;
	private String selectedBranch = null;
	private JSONObject selectedRestaurant;
	private JSONObject order;
	private TableView<Meal> shoppingCart;
	private ObservableList<Meal> mealList;
	@FXML
	private Label userLabel;
	@FXML
	private VBox mainVBox;
	@FXML
	private Label feedBackLabel;
	@FXML
	private Button cart;
	@FXML
	private VBox pathVBox;

	@FXML
	private HBox questionHBox;

	@FXML
	private ComboBox<String> choices;

	@FXML
	private Button nextButton;

	@FXML
	private Button checkOutButton;

	@FXML
	private Button backButton;

	@FXML
	private Label titleLabel;

	@FXML
	private ImageView foodStackIcon;

	@FXML
	private ImageView homeIcon;

	@FXML
	private ImageView restaurantIcon;

	@FXML
	private ImageView mealsIcon;

	@FXML
	private ImageView deliveryIcon;
	@FXML
	private ImageView paymentIcon;

	@FXML
	private Pane mealsPane;

	@FXML
	private Pane restaurantsPane;

	@FXML
	private Pane deliveryPane;

	@FXML
	private HBox restaurantHBox;

	@FXML
	private HBox mealsHBox;

	@FXML
	private HBox deliveryHBox;

	/**
	 * init
	 * 
	 * This method initializes the needed parameters for this controller.
	 * 
	 * @param HBox               orderHBox
	 * @param Stage              primaryStage
	 * @param CustomerPortalView view
	 */
	public void init(HBox orderHBox, Stage primaryStage, CustomerPortalView view) {
		this.orderHBox = orderHBox;
		this.primaryStage = primaryStage;
		this.view = view;
		menu = new JSONObject();
		nextButton.disableProperty().set(true);
		checkOutButton.disableProperty().set(true);
		order = new JSONObject();
		choices.getItems().addAll("North", "South", "Center");
		userLabel.setText("Welcome, " + view.getFirstName());

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
		mealsIcon.setImage(img);
		mealsIcon.setBlendMode(BlendMode.LIGHTEN);
		img = new Image(FOLDER_NAME + "Delivery1.jpg");
		deliveryIcon.setImage(img);
		deliveryIcon.setBlendMode(BlendMode.LIGHTEN);
		img = new Image(FOLDER_NAME + "Payment.png");
		paymentIcon.setImage(img);
		paymentIcon.setBlendMode(BlendMode.LIGHTEN);
		img = new Image(FOLDER_NAME + "RegularAcc.jpg");
		feedBackLabel.setVisible(false);
		shoppingCart = new TableView<>();
		mealList = FXCollections.observableArrayList();
		initCart();
		////////

	}

	/**
	 * Present an empty window of "Order Window", and send a message to server side
	 * <p>
	 * Message sent as JSON, contains keys:<br>
	 * "command", value "Order window is displayed"
	 */
	public void showWindow() {
		Platform.runLater(() -> {
			try {
				if (scene == null) {
					Scene scene = new Scene(orderHBox);
					this.scene = scene;
				}
			} catch (IllegalArgumentException e) {
				// log
				System.out.println(e);
				Logger.log(Level.WARNING, "OrderWindow: exception in showWindow");
				System.out.println("OrderWindow: exception in showWindow");
			}
			if (selectedBranch == null)
				choices.getSelectionModel().select(view.getBranch());
			primaryStage.setScene(scene);
			checkOutButton.disableProperty().set(true);
			primaryStage.show();
			Logger.log(Level.INFO, "OrderWindow: showing window");
			System.out.println("OrderWindow: showing window");
			if (selectedBranch == null)
				sendToServer("Order window is displayed");
		});
	}

	public HBox getHBox() {
		return orderHBox;
	}

	/**
	 * Initialize "shopping cart" (TableView) with proper columns and cell data
	 */
	private void initCart() {
		TableColumn<Meal, String> mealNameColumn = new TableColumn<>("name");
		TableColumn<Meal, String> mustFeatureColumn = new TableColumn<>("must feature");
		mealNameColumn.setCellValueFactory(new PropertyValueFactory<Meal, String>("name"));
		mustFeatureColumn.setCellValueFactory(new Callback<CellDataFeatures<Meal, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<Meal, String> m) {
				return new ReadOnlyObjectWrapper<String>(m.getValue().getMustFeature().getName());
			}
		});
		shoppingCart.getColumns().add(mealNameColumn);
		shoppingCart.getColumns().add(mustFeatureColumn);
	}

	/**
	 * Load the different active restaurants in the selected branch in the ComboBox
	 * <p>
	 * Send message to controller as a JSONObject with keys:<br>
	 * "command", with value "Restaurant list is displayed"
	 * <p>
	 * Set action for backButton to show Customer Window
	 * 
	 * @param restaurants contains keys:<br>
	 *                    "branch", value String<br>
	 *                    "restaurantName", value String.
	 */
	public void showRestaurants(JSONArray restaurants) {
		if (restaurants != null)
			restaurantList = restaurants;
		Logger.log(Level.INFO, "OrderWindow: Showing restaurants");
		System.out.println("OrderWindow: Showing restaurants");
		backButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				view.getCustomerWindow().showWindow();
			}
		});
		Platform.runLater(() -> {
			cart.setVisible(false);
			restaurantsPane.setStyle("-fx-background-color: #F2C12E;");
			restaurantHBox.setStyle("-fx-background-color: #F24444;");
			mealsPane.setStyle("-fx-background-color:  #D93B48;");
			mealsHBox.setStyle("-fx-background-color:   #D93B48;");
			if (!(mainVBox.getChildren().isEmpty()) && mainVBox.getChildren() != null)
				mainVBox.getChildren().clear();
			titleLabel.setText("Choose a restaurant:");
			nextButton.disableProperty().set(true);
			if (selectedBranch == null) // if it's the first time, get the branch of the user
				selectedBranch = view.getBranch();
			else
				selectedBranch = choices.getValue();
			MyGrid grid = new MyGrid();
			for (int i = 0; i < restaurantList.size(); i++) {
				JSONObject restaurant = (JSONObject) restaurantList.get(i); // get single restaurant from the JSON array
				String restaurantBranch = Message.getValueString(restaurant, "branch");
				String restaurantName = Message.getValueString(restaurant, "restaurantName");
				if (restaurantBranch.equals(selectedBranch))
					grid.addToGrid(restaurantName, createEventForRestaurant(restaurant),
							FOLDER_NAME + restaurantName + ".jpg");
			}
			mainVBox.getChildren().add(grid.getGrid());
			sendToServer("Restaurant list is displayed");
		});
	}

	/**
	 * Load the different types in the selected restaurant
	 * <p>
	 * Send message to controller as a JSONObject with keys:<br>
	 * "command", with value "Item types list is displayed"
	 * <p>
	 * Set action for backButton to showRestaurants method
	 * 
	 * @param jsonMenu
	 */
	public void showTypesList(JSONObject jsonMenu) {
		if (jsonMenu != null)
			menu = jsonMenu;
		Platform.runLater(() -> {
			setPathToMealFromRestaurants();
			backButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					setPathToRestaurants();
					showRestaurants(null);
				}
			});
			cart.setVisible(true);
			Logger.log(Level.INFO, "OrderWindow: Showing types");
			System.out.println("OrderWindow: Showing types");
			if (!(mainVBox.getChildren().isEmpty()) && mainVBox.getChildren() != null)
				mainVBox.getChildren().clear();
			titleLabel.setText("Choose the type:");
			MyGrid grid = new MyGrid();
			for (Object o : menu.keySet()) {
				String itemType = o.toString();
				JSONArray t = (JSONArray) menu.get(itemType);
				JSONObject temp = (JSONObject) t.get(0);
				grid.addToGrid(itemType, createEventForType(itemType), Message.getValueString(temp, "imgType"));
			}
			mainVBox.getChildren().add(grid.getGrid());
			sendToServer("Item types list is displayed");
		});
	}

	/**
	 * Load the meals of a given type in the selected restaurant, using private
	 * method loadItemsIntoWindow()
	 * <p>
	 * Send message to controller as a JSONObject with keys:<br>
	 * "command", with value "Meals by type are displayed"
	 * <p>
	 * Set action for backButton to showTypesList method
	 * 
	 * @param type with key: "itemType", value String
	 */
	public void showMealsByType(JSONObject type) {
		Platform.runLater(() -> {
			backButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					nextButton.disableProperty().set(true);
					showTypesList(null);
				}
			});

			if (!(mainVBox.getChildren().isEmpty()) && mainVBox.getChildren() != null)
				mainVBox.getChildren().clear();
			titleLabel.setText("Select meals:");
			mainVBox.setSpacing(10);
			mainVBox.getChildren().add(new Label("  *Please click on the image of the meal you want"));
			String itemType = Message.getValueString(type, "itemType");
			loadItemsToWindow(itemType);
			Logger.log(Level.INFO, "OrderWindow: Showing meals");
			System.out.println("OrderWindow: Showing meals");
			sendToServer("Meals by type are displayed");
		});
	}

	/**
	 * This method loads a ListView of the meals with HBoxes with checkboxes,
	 * containing proper image, must features and optional features<br>
	 * Using the help of methods:<br>
	 * addMustFeatures, addOptionalFeatures, addAddMealButton
	 * 
	 * @param itemType
	 */
	private void loadItemsToWindow(String itemType) {
		JSONArray itemArr = Message.getValueJArray(menu, itemType);
		ListView<HBox> lv = new ListView<HBox>();
		mainVBox.getChildren().add(lv);
		for (int i = 1; i < itemArr.size(); i++) {
			JSONObject item = (JSONObject) itemArr.get(i);
			String itemName = Message.getValueString(item, "mealName");
			JSONArray selectedOptionals = new JSONArray();
			if (itemName == null || itemName.isEmpty())
				continue;
			String itemID = Message.getValueString(item, "mealID");
			String itemPrice = Message.getValueString(item, "mealPrice");
			String imgMeal = Message.getValueString(item, "imgMeal");
			JSONArray mustFeatures = Message.getValueJArray(item, "mustFeatureJArray");
			JSONArray optionalFeatures = Message.getValueJArray(item, "optionalFeatureJArray");

			VBox vboxForMeal = new VBox(5);
			HBox hboxForMeal = new HBox(10);

			vboxForMeal.setAlignment(Pos.CENTER);
			hboxForMeal.setAlignment(Pos.BASELINE_LEFT);

			Label l = new Label(itemName);
			Label priceLabel = new Label(itemPrice + " INS");
			l.setPadding(new Insets(5));
			l.setAlignment(Pos.CENTER);
			vboxForMeal.getChildren().add(l);
			CheckBox cb = new CheckBox();
			cb.getStyleClass().remove("check-box");
			cb.setGraphic(getIMG(imgMeal));
			cb.setPadding(new Insets(5));
			vboxForMeal.getChildren().add(cb);
			hboxForMeal.getChildren().addAll(vboxForMeal, priceLabel);
			cb.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					if (cb.isSelected()) {
						JSONObject meal = new JSONObject();
						meal.put("mealName", itemName);
						meal.put("mealID", itemID);
						meal.put("mealPrice", itemPrice);
						meal.put("mealType", itemType);
						addMustFeatures(mustFeatures, hboxForMeal, meal);
						addOptionalFeatures(optionalFeatures, hboxForMeal, selectedOptionals);
						addAddMealButton(meal, hboxForMeal, selectedOptionals);
					} else {
						hboxForMeal.getChildren().clear();
						hboxForMeal.getChildren().addAll(vboxForMeal, priceLabel);

					}
				}
			});
			lv.getItems().add(hboxForMeal);
			lv.setStyle(".list-cell:filled:selected:focused,\n" + ".list-cell:filled:selected,\n" + ".list-cell:even,\n"
					+ ".list-cell:odd {\n" + "  -fx-background-color: transparent;\n" + "}");
		}
		if (lv.getItems().isEmpty())
			lv.setPlaceholder(new Label("No such items"));
	}

	/**
	 * Adds a button "Add Meal" with an event handler
	 * 
	 * @param meal        (the meal that will be added to shopping cart through the
	 *                    event handler
	 * @param hboxForMeal (the hbox to which the button is added)
	 */
	private void addAddMealButton(JSONObject meal, HBox hboxForMeal, JSONArray selectedOptionals) {
		Button addMeal = new Button("Add Meal");
		addMeal.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (meal.containsKey("mustFeatureID")) {
					nextButton.disableProperty().set(true);
					checkOutButton.disableProperty().set(false);
					meal.put("optionalJArray", selectedOptionals);
					mealList.add(Meal.fromJSONObject(meal));
					shoppingCart.setItems(mealList);
					Logger.log(Level.INFO, "OrderWindow: clickOnAddMeal: Meal added");
					System.out.println("OrderWindow: clickOnAddMeal: Meal added");
					sendToServer("Add meal button was clicked");
				} else {
					System.out.println("Please choose a Must Feature!");
					showPopup("Please choose a Must Feature!");
				}
			}
		});
		hboxForMeal.getChildren().add(addMeal);
	}

	/**
	 * general popup for general information
	 * 
	 * @param msg (the message you want to show to user)
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
	 * Add a ListView of optional features checkboxes with eventhandlers
	 * 
	 * @param optionalFeaturesJArray (all the optional features array of a specific
	 *                               meal)
	 * @param hboxForMeal            (the hbox to which the listview is added)
	 * @param meal
	 */
	private void addOptionalFeatures(JSONArray optionalFeaturesJArray, HBox hboxForMeal, JSONArray selectedOptionals) {
		if (optionalFeaturesJArray == null)
			return;
		VBox vboxForOptional = new VBox(5);
		Label title = new Label("Optional Features:");
		ListView<CheckBox> lvForOptional = new ListView<>();
		lvForOptional.setMaxHeight(100);
		lvForOptional.setPrefHeight(100);
		vboxForOptional.getChildren().add(title);
		vboxForOptional.getChildren().add(lvForOptional);
		hboxForMeal.getChildren().add(vboxForOptional);
		for (int j = 0; j < optionalFeaturesJArray.size(); j++) {
			JSONObject optionalFeature = (JSONObject) optionalFeaturesJArray.get(j);
			String optionalFeatureName = Message.getValueString(optionalFeature, "optionalFeatureName");
			String optionalFeaturePrice = " (+";
			optionalFeaturePrice += Message.getValueLong(optionalFeature, "optionalFeaturePrice");
			optionalFeaturePrice += "INS)";
			CheckBox cb = new CheckBox(optionalFeatureName + optionalFeaturePrice);
			cb.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					if (cb.isSelected())
						selectedOptionals.add(optionalFeature);
					else
						selectedOptionals.remove(optionalFeature);
				}
			});
			lvForOptional.getItems().add(cb);
			title.setLabelFor(lvForOptional);
		}
	}

	/**
	 * Add must feature radio boxes, and set event handler
	 * 
	 * @param mustFeatures (all the must features of a specific meal)
	 * @param hboxForMeal  (the hbox to which the listview is added)
	 * @param meal
	 */
	private void addMustFeatures(JSONArray mustFeatures, HBox hboxForMeal, JSONObject meal) {
		if (mustFeatures != null) {
			ToggleGroup group = new ToggleGroup();
			VBox vboxForMust = new VBox(5);
			ListView<RadioButton> lvForMust = new ListView<>();
			lvForMust.setPrefHeight(100);
			lvForMust.setMaxHeight(100);
			Label title = new Label("Must Features:");
			vboxForMust.getChildren().add(title);
			vboxForMust.getChildren().add(lvForMust);
			title.setLayoutY(2);
			hboxForMeal.getChildren().add(vboxForMust);
			for (int j = 0; j < mustFeatures.size(); j++) {
				JSONObject mustFeature = (JSONObject) mustFeatures.get(j);
				String mustFeatureName = Message.getValueString(mustFeature, "mustFeatureName");
				String mustFeatureID = Message.getValueString(mustFeature, "mustFeatureID");
				String mustFeaturePrice = " (+";
				mustFeaturePrice += Message.getValueLong(mustFeature, "mustFeaturePrice");
				mustFeaturePrice += "INS)";
				RadioButton rb = new RadioButton(mustFeatureName + mustFeaturePrice);
				rb.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						meal.put("mustFeatureName", mustFeatureName);
						meal.put("mustFeatureID", mustFeatureID);
						meal.put("mustFeaturePrice", Message.getValueLong(mustFeature, "mustFeaturePrice"));
					}
				});
				rb.setToggleGroup(group);
				lvForMust.getItems().add(rb);
			}
		} else {
			meal.put("mustFeatureID", null);
		}
	}

	/**
	 * load image by using name<br>
	 *  if image is not found, load "not available" image 
	 * @param name (name of image)
	 * @return ImageView containing the respective image
	 */
	public ImageView getIMG(String name) {
		Image img;
		if (name != null) {
			try {
				img = new Image(name);
			} catch (IllegalArgumentException e) {
				img = new Image("/images/not available.jpg");
			}
		} else
			img = new Image("/images/not available.jpg");
		ImageView imgView = new ImageView(img);
		imgView.setFitHeight(100);
		imgView.setFitWidth(100);
		imgView.setPreserveRatio(true);
		return imgView;
	}

	/**
	 * Go back to restaurants list, display restaurants in the branch selected Sends
	 * a message to the server side "Combo box option was selected"
	 * 
	 * @param event
	 */
	@FXML
	public void onComboBox(ActionEvent event) {
		Platform.runLater(() -> {
			resetPath();
			mealList.clear();
			questionHBox.getChildren().clear();
			checkOutButton.setVisible(true);
			selectedBranch = choices.getSelectionModel().getSelectedItem();
			sendToServer("Combo box option was selected");
		});
	}

	/**
	 * Handle the GUI (left hand side of the screen showing the different windows)
	 */
	public void setPathToRestaurants() {
		restaurantsPane.setStyle("-fx-background-color: #F2C12E;");
		restaurantHBox.setStyle("-fx-background-color: #F24444;");
		mealsPane.setStyle("-fx-background-color:  #D93B48;");
		mealsHBox.setStyle("-fx-background-color:   #D93B48;");
	}

	public void setPathToMealFromRestaurants() {
		restaurantsPane.setStyle("-fx-background-color: #D93B48;");
		restaurantHBox.setStyle("-fx-background-color: #D93B48;");
		mealsPane.setStyle("-fx-background-color:  #F2C12E;");
		mealsHBox.setStyle("-fx-background-color:   #F24444;");
	}

	public void setPathToDelivery() {
		mealsPane.setStyle("-fx-background-color:  #D93B48;");
		mealsHBox.setStyle("-fx-background-color:   #D93B48;");
		deliveryPane.setStyle("-fx-background-color:  #F2C12E;");
		deliveryHBox.setStyle("-fx-background-color:   #F24444;");
	}

	public void setPathToMealFromDelivery() {
		mealsPane.setStyle("-fx-background-color:  #F2C12E;");
		mealsHBox.setStyle("-fx-background-color:   #F24444;");
		deliveryPane.setStyle("-fx-background-color:  #D93B48;");
		deliveryHBox.setStyle("-fx-background-color:   #D93B48;");
	}

	public void resetPath() {
		restaurantsPane.setStyle("-fx-background-color: #F2C12E;");
		restaurantHBox.setStyle("-fx-background-color: #F24444;");
		mealsPane.setStyle("-fx-background-color:  #D93B48;");
		mealsHBox.setStyle("-fx-background-color:   #D93B48;");
		deliveryPane.setStyle("-fx-background-color:  #D93B48;");
		deliveryHBox.setStyle("-fx-background-color:   #D93B48;");
	}
	/////

	/**
	 * Display the shopping cart with the selected meals<br>
	 * Add buttons to allow deletion of selected meals, or edit them or close the
	 * shopping cart<br>
	 */
	@FXML
	public void showCart() {
		Platform.runLater(() -> {
			Stage window = new Stage();
			VBox layout = new VBox(10);
			window.initModality(Modality.APPLICATION_MODAL);
			window.setTitle("Shopping Cart");
			window.setMinWidth(300);
			window.setMinHeight(20);
			Button b1 = new Button("Close");
			b1.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					shoppingCart.getSelectionModel().clearSelection();
					window.hide();
				}

			});
			Button b2 = new Button("Edit");
			b2.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					Meal m = shoppingCart.getSelectionModel().getSelectedItem();
					String nameOfMeal = m.getName();
					String mealType = m.getType();
					JSONArray typesArray = Message.getValueJArray(menu, mealType);
					layout.getChildren().clear();
					for (int i = 1; i < typesArray.size(); i++) {
						JSONObject item = (JSONObject) typesArray.get(i);
						String itemName = Message.getValueString(item, "mealName");
						if (itemName.equals(nameOfMeal)) {
							String itemID = Message.getValueString(item, "mealID");
							String itemPrice = Message.getValueString(item, "mealPrice");
							JSONArray mustFeatures = Message.getValueJArray(item, "mustFeatureJArray");
							JSONArray optionalFeatures = Message.getValueJArray(item, "optionalFeatureJArray");
							JSONArray selectedOptionals = new JSONArray();
							HBox hBoxForButtons = new HBox(5);
							VBox vboxForMeal = new VBox(5);
							HBox hboxForMeal = new HBox(10);

							vboxForMeal.setAlignment(Pos.CENTER);
							hboxForMeal.setAlignment(Pos.BASELINE_LEFT);

							StringBuilder sb = new StringBuilder();
							sb.append(itemName);
							sb.append(" : ");
							String mustName = m.getMustFeature().getName();
							if (mustName != null && !mustName.isEmpty()) {
								sb.append(mustName);
							}
							ArrayList<OptionalFeature> optionalList = m.getOptionalFeatureList();
							if (optionalList != null) {
								for (OptionalFeature o : optionalList) {
									String optionalName = o.getName();
									if (optionalName != null && !optionalName.isEmpty()) {
										sb.append(" , ");
										sb.append(optionalName);
									}
								}
							}
							Label l = new Label(sb.toString());
							layout.getChildren().add(l);
							l.setPadding(new Insets(5));
							l.setAlignment(Pos.CENTER);

							JSONObject meal = new JSONObject();
							meal.put("mealName", itemName);
							meal.put("mealID", itemID);
							meal.put("mealPrice", itemPrice);
							meal.put("mealType", mealType);
							addMustFeatures(mustFeatures, hboxForMeal, meal);
							addOptionalFeatures(optionalFeatures, hboxForMeal, selectedOptionals);
							hboxForMeal.getChildren().add(vboxForMeal);
							layout.getChildren().add(hboxForMeal);
							Button backButtonForCart = new Button("Back");
							backButtonForCart.setOnAction(new EventHandler<ActionEvent>() {

								@Override
								public void handle(ActionEvent event) {
									shoppingCart.getSelectionModel().clearSelection();
									window.hide();
									showCart();
								}

							});
							Button saveDetails = new Button("Save");
							saveDetails.setOnAction(new EventHandler<ActionEvent>() {

								@Override
								public void handle(ActionEvent event) {
									if (meal.containsKey("mustFeatureID")) {
										mealList.remove(m);
										meal.put("optionalJArray", selectedOptionals);
										mealList.add(Meal.fromJSONObject(meal));
										shoppingCart.getSelectionModel().clearSelection();
										window.hide();
										showCart();
									} else {
										System.out.println("Please choose a Must Feature!");
										showPopup("Please choose a Must Feature!");
									}
								}
							});
							hBoxForButtons.setAlignment(Pos.CENTER);
							hBoxForButtons.getChildren().add(backButtonForCart);
							hBoxForButtons.getChildren().add(saveDetails);
							layout.getChildren().add(hBoxForButtons);
							break;
						}
					}
				}
			});
			Button b3 = new Button("Delete");
			b3.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					Meal mealToDel = shoppingCart.getSelectionModel().getSelectedItem();
					if (mealList.contains(mealToDel))
						mealList.remove(mealToDel);
					if (mealList.isEmpty())
						checkOutButton.disableProperty().set(true);
				}

			});
			b2.disableProperty().set(true);
			b3.disableProperty().set(true);
			// add a listener to each row that enables clicking on the button "Edit",
			// "Delete" when a meal is selected
			shoppingCart.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
				b2.disableProperty().set(false);
				b3.disableProperty().set(false);
			});
			HBox hBoxForButtons = new HBox(10);
			hBoxForButtons.setAlignment(Pos.CENTER);
			hBoxForButtons.getChildren().addAll(b2, b3);

			layout.getChildren().add(shoppingCart);
			layout.setAlignment(Pos.CENTER);
			layout.getChildren().add(hBoxForButtons);
			layout.getChildren().add(b1);

			Scene scene = new Scene(layout);
			window.setScene(scene);
			window.showAndWait();
		});
	}

	/**
	 * Save the needed information of the selected restaurant<br>
	 * Send a message to server side "Check out button was clicked"
	 */
	@FXML
	public void onCheckOutButton() {
		Logger.log(Level.INFO, "OrderWindow: clickOnCheckOut: Clicked CheckOut");
		System.out.println("OrderWindow: clickOnCheckOut: Clicked CheckOut");
		restaurantsPane.setStyle("-fx-background-color: #D93B48;");
		restaurantHBox.setStyle("-fx-background-color: #D93B48;");
		mainVBox.getChildren().clear();
		order.put("restaurantName", Message.getValueString(selectedRestaurant, "restaurantName"));
		order.put("supplierID", Message.getValueString(selectedRestaurant, "supplierID"));
		order.put("restaurantBranch", selectedBranch);
		JSONArray mealsJArray = new JSONArray();
		for (Meal m : mealList) {
			mealsJArray.add(m.toJSONObject());
		}
		order.put("mealsJArray", mealsJArray);
		sendToServer("Check out button was clicked");
	}

	/**
	 * dynamically build the GUI for DeliveryWindow and display it to the user<br>
	 * show the delivery types (radio boxes) available for the restaurant chosen and
	 * set proper ActionEvents<br>
	 */
	public void showDeliveryWindow() {
		if (!(questionHBox.getChildren().isEmpty()))
			questionHBox.getChildren().clear();
		String deliveryTypes = Message.getValueString(selectedRestaurant, "deliveryTypes");
		checkOutButton.disableProperty().set(true);
		checkOutButton.setVisible(false);
		cart.setVisible(false);
		HBox hbox = new HBox(20);
		hbox.setAlignment(Pos.CENTER);
		TextField address = new TextField();
		TextField recipient = new TextField();
		TextField phone = new TextField();

		recipient.setText(view.getFirstName());
		phone.setText(view.getPhoneNumber());
		address.setText(view.getAddress());
		questionHBox.setPadding(new Insets(10));
		Label timeAndDate = new Label("Please enter the order's due date:");
		DatePicker datePicker = new DatePicker();
		datePicker.setValue(LocalDate.now());

		// user can't select past dates in the DatePicker
		datePicker.setDayCellFactory(picker -> new DateCell() {
			public void updateItem(LocalDate date, boolean empty) {
				super.updateItem(date, empty);
				LocalDate today = LocalDate.now();
				setDisable(empty || date.compareTo(today) < 0);
			}
		});

		TextField timeHours = new TextField();
		TextField timeMinutes = new TextField();
		timeHours.setPromptText("hour (2 digits)");
		timeMinutes.setPromptText("minutes (2 digits)");
		timeHours.setPrefWidth(110);
		timeMinutes.setPrefWidth(110);

		addTextLimiter(timeHours, 2, 1); // limit hours input to 2 digits and only numbers
		addTextLimiter(timeMinutes, 2, 1); // limit minutes input to 2 digits and only numbers
		addTextLimiter(phone, 10, 1); // limit phone input to 10 digits and only numbers
		addTextLimiter(recipient, 30, 2); // limit recipient name to 30 letters and only letters

		VBox self = new VBox(10);
		VBox robot = new VBox(10);
		VBox delivery = new VBox(10);
		self.setAlignment(Pos.CENTER);
		robot.setAlignment(Pos.CENTER);
		delivery.setAlignment(Pos.CENTER);
		// hboxes for address recipient and phone necessary for deliveries
		HBox hboxForAddress = new HBox(5);
		HBox hboxForRecipient = new HBox(5);
		HBox hboxForPhone = new HBox(5);

		Label selfLabel = new Label("Self Pickup");
		Label robotLabel = new Label("By a Robot");
		Label deliveryLabel = new Label("Delivery");

		ImageView selfIMG = getIMG("images/Self.jpg");
		ImageView robotIMG = getIMG("images/Robot.jpg");
		ImageView deliveryIMG = getIMG("images/Delivery.jpg");
		ImageView qIMG1 = getIMG("images/excl.jpg");
		ImageView qIMG2 = getIMG("images/excl.jpg");
		ImageView qIMG3 = getIMG("images/excl.jpg");

		Tooltip.install(qIMG1, new Tooltip("Pick up from the restaurant"));
		Tooltip.install(qIMG2, new Tooltip("Delivery with a robot"));
		Tooltip.install(qIMG3, new Tooltip("Delivery with a courier"));
		selfIMG.setFitHeight(200);
		selfIMG.setFitWidth(200);
		robotIMG.setFitHeight(167.5);
		robotIMG.setFitWidth(200);
		deliveryIMG.setFitHeight(200);
		deliveryIMG.setFitWidth(200);
		qIMG1.setFitHeight(15);
		qIMG1.setFitWidth(15);
		qIMG2.setFitHeight(15);
		qIMG2.setFitWidth(15);
		qIMG3.setFitHeight(15);
		qIMG3.setFitWidth(15);

		RadioButton rbForSelf = new RadioButton();
		RadioButton rbForRobot = new RadioButton();
		RadioButton rbForDelivery = new RadioButton();

		HBox selfH = new HBox(5);
		HBox robotH = new HBox(5);
		HBox deliveryH = new HBox(5);

		selfH.getChildren().addAll(rbForSelf, qIMG1);
		robotH.getChildren().addAll(rbForRobot, qIMG2);
		deliveryH.getChildren().addAll(rbForDelivery, qIMG3);

		ToggleGroup group = new ToggleGroup();
		rbForSelf.setToggleGroup(group);
		rbForRobot.setToggleGroup(group);
		rbForDelivery.setToggleGroup(group);

		rbForSelf.setGraphic(selfIMG);
		rbForRobot.setGraphic(robotIMG);
		rbForDelivery.setGraphic(deliveryIMG);

		rbForSelf.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				nextButton.disableProperty().set(false);
				String orderType = selfLabel.getText();
				order.put("pickUpType", orderType);
				hboxForAddress.setVisible(false);
				hboxForRecipient.setVisible(false);
				hboxForPhone.setVisible(false);
			}
		});
		rbForRobot.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				nextButton.disableProperty().set(false);
				String orderType = robotLabel.getText();
				order.put("pickUpType", orderType);
				hboxForAddress.setVisible(false);
				hboxForRecipient.setVisible(false);
				hboxForPhone.setVisible(false);
			}
		});
		rbForDelivery.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				nextButton.disableProperty().set(false);
				String orderType = deliveryLabel.getText();
				order.put("pickUpType", orderType);
				hboxForAddress.setVisible(true);
				hboxForRecipient.setVisible(true);
				hboxForPhone.setVisible(true);
			}
		});

		self.getChildren().addAll(selfLabel, selfH);
		robot.getChildren().addAll(robotLabel, robotH);
		delivery.getChildren().addAll(deliveryLabel, deliveryH);

		hboxForAddress.getChildren().add(new Label("*Address:"));
		hboxForAddress.getChildren().add(address);
		hboxForRecipient.getChildren().add(new Label("*Recipient:"));
		hboxForRecipient.getChildren().add(recipient);
		hboxForPhone.getChildren().add(new Label("*Phone:"));
		hboxForPhone.getChildren().add(phone);
		// hide until Delivery radio button is selected
		hboxForAddress.setVisible(false);
		hboxForRecipient.setVisible(false);
		hboxForPhone.setVisible(false);
		//
		delivery.getChildren().addAll(hboxForAddress, hboxForRecipient, hboxForPhone);

		if ((deliveryTypes.charAt(0) == 'Y')) // 0 is self pickup
			hbox.getChildren().add(self);

		if ((deliveryTypes.charAt(1) == 'Y')) // 1 is delivery
			hbox.getChildren().add(delivery);

		if ((deliveryTypes.charAt(2) == 'Y')) // 2 is robot
			hbox.getChildren().add(robot);

		////////// remove if robot gets implemented ever
		rbForRobot.disableProperty().set(true);
		String message = "*Robot option is yet to be implemented";
		Label l = new Label(message);
		l.setTextFill(Color.color(1, 0, 0));
		robot.getChildren().add(l);
		///////////////////////////////////////////////////////////

		backButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				setPathToMealFromDelivery();

				questionHBox.getChildren().clear();
				nextButton.disableProperty().set(true);
				checkOutButton.disableProperty().set(false);
				checkOutButton.setVisible(true);
				cart.setVisible(true);
				showTypesList(null);
			}
		});

		Platform.runLater(() -> {
			setPathToDelivery();
			titleLabel.setText("Choose a delivery option:");
			Label errorHoursLbl = new Label("*");
			Label errorMinutesLbl = new Label("*");
			errorHoursLbl.setStyle("-fx-text-fill: red");
			errorMinutesLbl.setStyle("-fx-text-fill: red");
			errorMinutesLbl.setVisible(false);
			errorHoursLbl.setVisible(false);
			mainVBox.getChildren().clear();
			mainVBox.getChildren().add(hbox);
			nextButton.disableProperty().set(true);
			questionHBox.getChildren().add(timeAndDate);
			questionHBox.getChildren().add(datePicker);
			questionHBox.getChildren().add(timeHours);
			questionHBox.getChildren().add(errorHoursLbl);
			questionHBox.getChildren().add(new Label(": "));
			questionHBox.getChildren().add(timeMinutes);
			questionHBox.getChildren().add(errorMinutesLbl);

			nextButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					JSONObject json = new JSONObject();
					boolean checksFlag = false;
					feedBackLabel.setVisible(false);
					json.put("command", "Delivery method was selected");
					json.put("order", order);
					String inputTime = appendHoursAndMinutes(timeHours.getText(), timeMinutes.getText());
					checksFlag = checkDueTime(inputTime)
							& checkDueDate(DateParser.toSQLStyle(datePicker.getEditor().getText(), inputTime));
					if (!checksFlag) {
						errorMinutesLbl.setVisible(true);
						errorHoursLbl.setVisible(true);
					}
					if (rbForDelivery.isSelected()) {
						if ((checkRecipient(recipient.getText()) & checkPhoneNumber(phone.getText())
								& checkAddress(address.getText()))) {
							order.put("address", address.getText());
							order.put("phoneNumber", phone.getText());
						} else
							checksFlag = false;
					}
					if (checksFlag) {
						order.put("dueDate", DateParser.toSQLStyle(datePicker.getEditor().getText(), inputTime));
						view.ready(json);
						Logger.log(Level.INFO, "OrderWindow: showDeliveryWindow: Showing delivery window");
						System.out.println("OrderWindow: showDeliveryWindow: Showing delivery window");
					}
				}
			});
		});

	}

	/**
	 * Method to append the hours from textfield on delivery window with the minutes
	 * 
	 * @param hours   (03)
	 * @param minutes (20)
	 * @return hours:minutes (from example 03:20)
	 */
	public String appendHoursAndMinutes(String hours, String minutes) {
		if (hours.isEmpty() || minutes.isEmpty())
			return "";
		return hours + ":" + minutes;
	}

	/**
	 * Check that input phone is not empty, and is 10 characters long
	 * 
	 * @param phoneNumber
	 * @return true if not empty and is 10 characters, else false
	 */
	public boolean checkPhoneNumber(String phoneNumber) {
		if ((phoneNumber.isEmpty()) || phoneNumber.length() != 10) {
			System.out.println("Please enter a valid phone number");
			return false;
		}
		return true;
	}

	/**
	 * Check that input recipient is not empty
	 * 
	 * @param recipient
	 * @return true if not empty, else false
	 */
	public boolean checkRecipient(String recipient) {
		if ((recipient.isEmpty())) {
			System.out.println("Please enter a recipient name");
			return false;
		}
		return true;
	}

	/**
	 * Check that input address is not empty
	 * 
	 * @param address
	 * @return true if not empty, else false
	 */
	public boolean checkAddress(String address) {
		if ((address.isEmpty())) {
			System.out.println("Please enter a valid address");
			return false;
		}
		return true;
	}

	/**
	 * Check that input dueDate is correct
	 * 
	 * @param dueDate
	 * @return
	 */
	public boolean checkDueDate(String dueDate) {
		if (dueDate.isEmpty()) {
			System.out.println("Please choose a valid date");
			return false;
		}

		boolean flag = true;
		Date dateNow = Calendar.getInstance().getTime();
		String currentYear = DateParser.dateParser(dateNow.toString().substring(4), "year");
		String currentHour = DateParser.dateParser(dateNow.toString().substring(4), "hours");
		String currentMinutes = DateParser.dateParser(dateNow.toString().substring(4), "minutes");
		String currentDay = DateParser.dateParser(dateNow.toString().substring(4), "day");

		String dueYear = DateParser.dateParser(dueDate, "year");
		String dueHour = DateParser.dateParser(dueDate, "hours");
		String dueMinutes = DateParser.dateParser(dueDate, "minutes");
		String dueDay = DateParser.dateParser(dueDate, "day");

		if (dueHour.length() < 2 || dueMinutes.length() < 2) {
			feedBackLabel.setText("Please enter 2 digits in hours and minutes!");
			feedBackLabel.setVisible(true);
			return false;
		}
		if (Integer.valueOf(dueHour) > 23 || Integer.valueOf(dueHour) < 0) {
			feedBackLabel.setText("Hours must be between 0-23!");
			feedBackLabel.setVisible(true);
			return false;
		}
		if (Integer.valueOf(dueMinutes) >= 60 || Integer.valueOf(dueMinutes) < 0) {
			feedBackLabel.setText("Minutes must be between 0-60!");
			feedBackLabel.setVisible(true);
			return false;
		}
		if (!(dueYear.equals(currentYear))) {
			System.out.println("Please choose a date from current year");
			feedBackLabel.setText("Please choose a date from current year!");
			feedBackLabel.setVisible(true);
			return false;
		}

		if (Integer.valueOf(dueDay) == Integer.valueOf(currentDay)) {
			if (Integer.valueOf(dueHour) < Integer.valueOf(currentHour)) {
				feedBackLabel.setText("Time can't be in the past!");
				feedBackLabel.setVisible(true);
				System.out.println("That time is not available");
				return false;
			}
			if (Integer.valueOf(dueHour) == Integer.valueOf(currentHour))
				if (Integer.valueOf(dueMinutes) < Integer.valueOf(currentMinutes)) {
					feedBackLabel.setText("Time can't be in the past!");
					feedBackLabel.setVisible(true);
					System.out.println("That time is not available");
					return false;
				}
		}
		return flag;
	}

	public boolean checkDueTime(String dueTime) {
		if ((dueTime.isEmpty())) {
			System.out.println("Please enter a time");
			return false;
		}
		return true;
	}

	/**
	 * Method to check if (difference) time has passed from start to end
	 * 
	 * @param start
	 * @param end
	 * @param difference (in minutes)
	 * @return true if end - start > difference
	 */
	public boolean checkIfDifferencePassed(String start, String end, int difference) {
		int endHour = Integer.valueOf(DateParser.dateParser(end, "hours"));
		int endMinutes = Integer.valueOf(DateParser.dateParser(end, "minutes"));
		int startHour = Integer.valueOf(DateParser.dateParser(start, "hours"));
		int startMinutes = Integer.valueOf(DateParser.dateParser(start, "minutes"));
		if ((endHour * 60 + endMinutes) - (startHour * 60 + startMinutes) > difference)
			return true;
		else
			return false;
	}

	/**
	 * add a listener to textfield (tf)<br>
	 * type = 1 - allow only numbers<br>
	 * type = 2 - allow only letters <br>
	 * type = 3 - allow both numbers and letters<br>
	 * type = any other input - allow everything
	 * 
	 * @param tf        (textfield that you want to limit)
	 * @param maxLength (max length of input of textfield)
	 * @param type      (1,2,3,4)
	 */
	public static void addTextLimiter(final TextField tf, final int maxLength, int type) {
		tf.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> ov, final String oldValue,
					final String newValue) {
				if (tf.getText().length() > maxLength) {
					String s = tf.getText().substring(0, maxLength);
					tf.setText(s);
				}
				if (tf.getText().isEmpty())
					;
				else if (type == 1) { // numbers only
					if (!(tf.getText().matches("[0-9]+"))) {
						String s = tf.getText().substring(0, tf.getText().length() - 1);
						tf.setText(s);
					}
				} else if (type == 2) { // letters only
					if (!(tf.getText().matches("[a-zA-Z]+"))) {
						String s = tf.getText().substring(0, tf.getText().length() - 1);
						tf.setText(s);
					}
				} else if (type == 3) { // both numbers and letters
					if (!(tf.getText().matches("[0-9]+")) && !(tf.getText().matches("[a-zA-Z]+"))) {
						String s = tf.getText().substring(0, tf.getText().length() - 1);
						tf.setText(s);
					}
				}
			}
		});
	}

	/////////////// EVENT HANDLERS/////////

	/**
	 * create an event handler for a specific restaurant button
	 * 
	 * @param restaurant
	 * @return EventHandler that clears the restaurant list page and shows the
	 *         selected restaurant's meal type list by sending a JSON
	 *         {"command","Get restaurant menu"}
	 */
	public EventHandler<ActionEvent> createEventForRestaurant(JSONObject restaurant) {
		EventHandler<ActionEvent> ev = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				nextButton.disableProperty().set(false);
				nextButton.setVisible(true);
				nextButton.setOnAction(new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent event) {
						order = new JSONObject();
						nextButton.disableProperty().set(true);
						Logger.log(Level.WARNING, "OrderWindow: Restaurant was selected");
						System.out.println("OrderWindow: Restaurant was selected");
						JSONObject json = new JSONObject();
						selectedRestaurant = restaurant;
						mealList.clear();
						json.put("restaurantInfo", selectedRestaurant);
						json.put("command", "Restaurant was selected");
						view.ready(json);
					}
				});
				;
			}
		};
		return ev;
	}

	/**
	 * create an event handler for a specific item type button
	 * 
	 * @param restaurant
	 * @return EventHandler that clears the item type page and shows the selected
	 *         type's meal list by sending a JSON with keys:<br>
	 *         "command", with value "Item type was selecteed" "itemType", with
	 *         value String (name of itemType)
	 */
	public EventHandler<ActionEvent> createEventForType(String itemType) {
		EventHandler<ActionEvent> ev = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				nextButton.disableProperty().set(false);
				nextButton.setVisible(true);
				nextButton.setOnAction(new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent event) {
						nextButton.disableProperty().set(true);
						JSONObject json = new JSONObject();
						json.put("command", "Item type was selected");
						json.put("itemType", itemType);
						view.ready(json);
					}
				});
			}
		};
		return ev;
	}

	public void sendToServer(String cmd) {
		JSONObject json = new JSONObject();
		json.put("command", cmd);
		view.ready(json);
	}
}
