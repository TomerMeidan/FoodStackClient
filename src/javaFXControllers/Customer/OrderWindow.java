package javaFXControllers.Customer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import clientSide.CustomerPortalView;

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
import common.Logger;
import util.Message;
import common.Logger.Level;
import util.MyGrid;
import util.OptionalFeature;
import util.DateParser;
import util.Meal;

@SuppressWarnings("unchecked")
public class OrderWindow {

	private final static String FOLDER_NAME = "/images/";
	private Stage primaryStage;
	private Scene scene;
	private HBox orderHBox;
	private CustomerPortalView view;

	private JSONArray restaurantList;
	private JSONObject menu;
	private JSONArray selectedOptionals;
	private String selectedBranch = null;
	private JSONObject selectedRestaurant;
	private JSONObject order;
	private TableView<Meal> shoppingCart;
	private ObservableList<Meal> mealList;
	@FXML
	private Label userLabel;
	@FXML
	private VBox mainVBox; // TODO change name

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

	public void init(HBox orderHBox, Stage primaryStage, CustomerPortalView view) {
		this.orderHBox = orderHBox;
		this.primaryStage = primaryStage;
		this.view = view;
		selectedOptionals = new JSONArray();
		menu = new JSONObject();
		nextButton.disableProperty().set(true);
		checkOutButton.disableProperty().set(true);
		order = new JSONObject();
		// mealsJArray = new JSONArray();
		choices.getItems().addAll("North", "South", "Center");
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
	 * Present an empty window of "Order Window", and send a message to controller
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
				sendToController("Order window is displayed");
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
			sendToController("Restaurant list is displayed");
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
	 * @see Menu class for keys in jsonMenu
	 */
	public void showTypesList(JSONObject jsonMenu) {
		if (jsonMenu != null)
			menu = jsonMenu;
		Platform.runLater(() -> {
			restaurantsPane.setStyle("-fx-background-color: #D93B48;");
			restaurantHBox.setStyle("-fx-background-color: #D93B48;");
			mealsPane.setStyle("-fx-background-color:  #F2C12E;");
			mealsHBox.setStyle("-fx-background-color:   #F24444;");
			backButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					restaurantsPane.setStyle("-fx-background-color: #F2C12E;");
					restaurantHBox.setStyle("-fx-background-color: #F24444;");
					mealsPane.setStyle("-fx-background-color:  #D93B48;");
					mealsHBox.setStyle("-fx-background-color:   #D93B48;");
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
			sendToController("Item types list is displayed");
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
			sendToController("Meals by type are displayed");
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
						addOptionalFeatures(optionalFeatures, hboxForMeal, meal);
						addAddMealButton(meal, hboxForMeal);
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
	 * adds a button "Add Meal" with an event handler
	 * 
	 * @param meal
	 * @param vboxForMeal
	 * @param hboxForMeal
	 */
	public void addAddMealButton(JSONObject meal, HBox hboxForMeal) {
		Button addMeal = new Button("Add Meal");
		addMeal.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (meal.containsKey("mustFeatureID")) {
					clickOnAddMeal(selectedOptionals, meal);
					selectedOptionals = new JSONArray();
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
	 * @param optionalFeatures
	 * @param hboxForMeal
	 * @param meal
	 */
	public void addOptionalFeatures(JSONArray optionalFeatures, HBox hboxForMeal, JSONObject meal) {
		if (optionalFeatures == null)
			return;
		VBox vboxForOptional = new VBox(5);
		Label title = new Label("Optional Features:");
		ListView<CheckBox> lvForOptional = new ListView<>();
		lvForOptional.setMaxHeight(100);
		lvForOptional.setPrefHeight(100);
		vboxForOptional.getChildren().add(title);
		vboxForOptional.getChildren().add(lvForOptional);
		hboxForMeal.getChildren().add(vboxForOptional);
		for (int j = 0; j < optionalFeatures.size(); j++) {
			JSONObject optionalFeature = (JSONObject) optionalFeatures.get(j);
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
	 * @param mustFeatures
	 * @param hboxForMeal
	 * @param meal
	 */
	public void addMustFeatures(JSONArray mustFeatures, HBox hboxForMeal, JSONObject meal) {
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
			// lvForMust.getItems().add(title);
			hboxForMeal.getChildren().add(vboxForMust);
			// hboxForMeal.getChildren().add(lvForMust);
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
	 * when selecting a branch in the combo box, send command to go back to
	 * restaurant list
	 * 
	 * @param event
	 */
	@FXML // TODO optimize later
	public void onComboBox(ActionEvent event) {
		Platform.runLater(() -> {
			// mealsJArray.clear();
			restaurantsPane.setStyle("-fx-background-color: #F2C12E;");
			restaurantHBox.setStyle("-fx-background-color: #F24444;");
			mealsPane.setStyle("-fx-background-color:  #D93B48;");
			mealsHBox.setStyle("-fx-background-color:   #D93B48;");
			deliveryPane.setStyle("-fx-background-color:  #D93B48;");
			deliveryHBox.setStyle("-fx-background-color:   #D93B48;");
			mealList.clear();
			questionHBox.getChildren().clear();
			checkOutButton.setVisible(true);
			selectedBranch = choices.getSelectionModel().getSelectedItem();
			sendToController("Combo box option was selected");
		});
	}

	/**
	 * save meal into mealsList
	 * 
	 * @param selectedOptionals
	 * @param meal
	 */
	public void clickOnAddMeal(JSONArray selectedOptionals, JSONObject meal) {
		nextButton.disableProperty().set(true);
		checkOutButton.disableProperty().set(false);
		meal.put("optionalJArray", selectedOptionals);
		mealList.add(Meal.fromJSONObject(meal));
		shoppingCart.setItems(mealList);
		Logger.log(Level.INFO, "OrderWindow: clickOnAddMeal: Meal added");
		System.out.println("OrderWindow: clickOnAddMeal: Meal added");
		sendToController("Add meal button was clicked");
	}

	@FXML
	public void showCart() {

		Platform.runLater(() -> {
			Stage window = new Stage();
			VBox layout = new VBox(10);
			window.initModality(Modality.APPLICATION_MODAL);
			window.setTitle("Shopping Cart");
			window.setMinWidth(300);
			window.setMinHeight(20);
			Button b1 = new Button("Exit");
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
							addOptionalFeatures(optionalFeatures, hboxForMeal, meal);
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
										selectedOptionals = new JSONArray();
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
	 * save all the relevant information about the order
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
		sendToController("Check out button was clicked");
	}

	/**
	 * dynamically build the GUI for DeliveryWindow and display it to the user<br>
	 * show the delivery types available for the restaurant chosen
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

		addTextLimiter(timeHours, 2, 1);
		addTextLimiter(timeMinutes, 2, 1);
		addTextLimiter(phone, 10, 1);
		addTextLimiter(recipient, 20, 2);

		VBox self = new VBox(10);
		VBox robot = new VBox(10);
		VBox delivery = new VBox(10);
		self.setAlignment(Pos.CENTER);
		robot.setAlignment(Pos.CENTER);
		delivery.setAlignment(Pos.CENTER);

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

		RadioButton rb1 = new RadioButton();
		RadioButton rb2 = new RadioButton();
		RadioButton rb3 = new RadioButton();

		HBox selfH = new HBox(5);
		HBox robotH = new HBox(5);
		HBox deliveryH = new HBox(5);

		selfH.getChildren().addAll(rb1, qIMG1);
		robotH.getChildren().addAll(rb2, qIMG2);
		deliveryH.getChildren().addAll(rb3, qIMG3);

		ToggleGroup group = new ToggleGroup();
		rb1.setToggleGroup(group);
		rb2.setToggleGroup(group);
		rb3.setToggleGroup(group);

		rb1.setGraphic(selfIMG);
		rb2.setGraphic(robotIMG);
		rb3.setGraphic(deliveryIMG);

		rb1.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				nextButton.disableProperty().set(false);
				String orderType = selfLabel.getText();
				order.put("pickUpType", orderType);
				if (delivery.getChildren().size() > 2)
					for (int i = 1; i <= 3; i++) { // remove the labels and textfields under Delivery
						delivery.getChildren().remove(2);
					}
			}
		});
		rb2.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				nextButton.disableProperty().set(false);
				String orderType = robotLabel.getText();
				order.put("pickUpType", orderType);
				if (delivery.getChildren().size() > 2)
					for (int i = 1; i <= 3; i++) {// remove the labels and textfields under Delivery
						delivery.getChildren().remove(2);
					}
			}
		});
		rb3.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				nextButton.disableProperty().set(false);
				String orderType = deliveryLabel.getText();
				order.put("pickUpType", orderType);
				HBox h1 = new HBox(5);
				HBox h2 = new HBox(5);
				HBox h3 = new HBox(5);

				h1.getChildren().add(new Label("*Address:"));
				h1.getChildren().add(address);
				h2.getChildren().add(new Label("*Recipient:"));
				h2.getChildren().add(recipient);
				h3.getChildren().add(new Label("*Phone:"));
				h3.getChildren().add(phone);
				delivery.getChildren().addAll(h1, h2, h3);
			}
		});

		self.getChildren().addAll(selfLabel, selfH);
		robot.getChildren().addAll(robotLabel, robotH);
		delivery.getChildren().addAll(deliveryLabel, deliveryH);

		if ((deliveryTypes.charAt(0) == 'Y')) // 0 is self pickup
			hbox.getChildren().add(self);

		if ((deliveryTypes.charAt(1) == 'Y')) // 1 is delivery
			hbox.getChildren().add(delivery);

		if ((deliveryTypes.charAt(2) == 'Y')) // 2 is robot
			hbox.getChildren().add(robot);

		////////// remove if robot gets implemented ever
		// robot.disableProperty().set(true);
		rb2.disableProperty().set(true);
		String message = "*Robot option is yet to be implemented";
		Label l = new Label(message);
		l.setTextFill(Color.color(1, 0, 0));
		robot.getChildren().add(l);
		///////////////////////////////////////////////////////////

		backButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				mealsPane.setStyle("-fx-background-color:  #F2C12E;");
				mealsHBox.setStyle("-fx-background-color:   #F24444;");

				deliveryPane.setStyle("-fx-background-color:  #D93B48;");
				deliveryHBox.setStyle("-fx-background-color:   #D93B48;");

				questionHBox.getChildren().clear();
				nextButton.disableProperty().set(true);
				checkOutButton.disableProperty().set(false);
				checkOutButton.setVisible(true);
				cart.setVisible(true);
				showTypesList(null);
			}
		});

		Platform.runLater(() -> {
			mealsPane.setStyle("-fx-background-color:  #D93B48;");
			mealsHBox.setStyle("-fx-background-color:   #D93B48;");

			deliveryPane.setStyle("-fx-background-color:  #F2C12E;");
			deliveryHBox.setStyle("-fx-background-color:   #F24444;");
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
					String inputTime = getTimeFromUser(timeHours.getText(), timeMinutes.getText());
					checksFlag = checkDueTime(inputTime)
							& checkDueDate(DateParser.toSQLStyle(datePicker.getEditor().getText(), inputTime));
					if (!checksFlag) {
						errorMinutesLbl.setVisible(true);
						errorHoursLbl.setVisible(true);
					}
					if (rb3.isSelected()) {
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

	public String getTimeFromUser(String hours, String minutes) {
		if (hours.isEmpty() || minutes.isEmpty())
			return "";
		return hours + ":" + minutes;
	}

	public boolean checkPhoneNumber(String phoneNumber) {
		if ((phoneNumber.isEmpty()) || phoneNumber.length() != 10) {
			System.out.println("Please enter a valid phone number");
			return false;
		}
		return true;
	}

	public boolean checkRecipient(String recipient) {
		if ((recipient.isEmpty())) {
			System.out.println("Please enter a recipient name");
			return false;
		}
		return true;
	}

	public boolean checkAddress(String address) {
		if (address == null || (address.isEmpty())) {
			System.out.println("Please enter a valid address");
			return false;
		}
		return true;
	}

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

		if (Integer.valueOf(dueHour) > 23)
			return false;
		if (Integer.valueOf(dueMinutes) >= 60)
			return false;
		if (!(dueYear.equals(currentYear))) {
			System.out.println("Please choose a date from current year");
			return false;
		}
		if (Integer.valueOf(dueDay) == Integer.valueOf(currentDay)) {
			if (Integer.valueOf(dueHour) < Integer.valueOf(currentHour)) {
				feedBackLabel.setText("That time is not available!");
				feedBackLabel.setVisible(true);
				System.out.println("That time is not available");
				return false;
			}
			if (Integer.valueOf(dueHour) == Integer.valueOf(currentHour))
				if (Integer.valueOf(dueMinutes) < Integer.valueOf(currentMinutes)) {
					feedBackLabel.setText("That time is not available!");
					feedBackLabel.setVisible(true);
					System.out.println("That time is not available");
					return false;
				}
		}
		return flag;
	}

	/**
	 * Method to check if (difference) time has passed from start to end
	 * 
	 * @param start
	 * @param end
	 * @param difference
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

	public boolean checkDueTime(String dueTime) {
		if ((dueTime.isEmpty())) {
			System.out.println("Please enter a time");
			return false;
		}
		return true;
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

	/**
	 * send a json {"command", cmd} to controller
	 * 
	 * @param cmd
	 */
	// @Override
	public void sendToController(String cmd) {
		JSONObject json = new JSONObject();
		json.put("command", cmd);
		view.ready(json);
	}
}
