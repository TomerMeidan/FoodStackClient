package javaFXControllers.supplier;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.mysql.cj.x.protobuf.MysqlxCrud.Update;

import clientSide.SupplierPortalView;
import common.Logger;
import common.Logger.Level;
import common.Message;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
////
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
/**
 * functionality of update menu action Add type, edit type, add meal (needs
 * enter to specified type), edit meal.
 * 
 * @author Daniel Ohayon
 * @version 12/25/21
 */
public class UpdateMenuWindow {

	private VBox supplierVBoxLoaded;
	private Stage primaryStage;
	private SupplierPortalView view;
	private Scene scene;
	private JSONObject showMenuDescriptor;
	private JSONArray editOpArr;
	private JSONArray editMustArr;
	private HashMap<String, VBox> feedEditFeatV;
	private HashMap<String, JSONArray> menu;
	private HashMap<String, HBox> editFeatH;
	private HashMap<String, VBox> forEditType;
	private HashMap<String, String> mustFeat;
	private HashMap<String, String> optionalFeat;
	private String userID;
	private HBox forAddType;
	private VBox forAddTypeV;
	private HBox forAddFeat;
	private VBox viewFeatures;
	private Label feedAddType;
	private Label feedAddOptional;
	private Label feedAddMust;

	@FXML
	private Button backButton;

	@FXML
	private Label supplierLabel;

	@FXML
	private VBox supplierVBox;
	@FXML
	private Label labelSide;
	@FXML
	private Pane sidePaneType;

	@FXML
	private Label paneType;


    @FXML
    private Label paneMeal1;

	@FXML
	private Pane sidePaneMeal;

	@FXML
    private ImageView imageType;

	 @FXML
	 private ImageView imageMeals;
	 
	/**
	 * Initialize method. The method initialize the parameters and the values of
	 * this class
	 * 
	 * @param VBox supplierVBoxTry - object of the all screen, Stage primaryStage,
	 *             SupplierPortalView view - expression the communication between
	 *             client - server
	 */
	@SuppressWarnings("unchecked")
	public void init(VBox supplierVBoxTry, Stage primaryStage, SupplierPortalView view) {
		this.supplierVBoxLoaded = supplierVBoxTry;
		this.primaryStage = primaryStage;
		this.view = view;

		editFeatH = new HashMap<>();
		feedEditFeatV = new HashMap<>();
		forEditType = new HashMap<>();
		mustFeat = new HashMap<>();
		optionalFeat = new HashMap<>();
		forAddType = new HBox();
		forAddTypeV = new VBox();
		forAddFeat = new HBox();
		viewFeatures = new VBox();
		feedAddType = new Label();
		feedAddOptional = new Label();
		feedAddMust = new Label();
		editMustArr = new JSONArray();
		editOpArr = new JSONArray();
	}

	/**
	 * The method responsible to indicate that the user on the meals update stage
	 */
	public void barToMeal() {
		sidePaneType.setStyle("-fx-background-color: #D93B48;");
		sidePaneMeal.setStyle("-fx-background-color:  #F2C12E;");
		paneType.setStyle("-fx-background-color: #D93B48;");
		paneMeal1.setStyle("-fx-background-color:   #F24444;");
//		Image img = new Image("templates/Icons/Types.jpeg");
//		imageType.setImage(img);
//		img = new Image("templates/Icons/MealsS.jpeg");
//		imageMeals.setImage(img);

	}
	
	/**
	 * The method responsible to indicate that the user on the types update stage
	 */
	public void barToType() {
		sidePaneMeal.setStyle("-fx-background-color: #D93B48;");
		sidePaneType.setStyle("-fx-background-color:  #F2C12E;");
		paneMeal1.setStyle("-fx-background-color: #D93B48;");
		paneType.setStyle("-fx-background-color:   #F24444;");
//		Image img = new Image("templates/Icons/TypesS.jpeg");
//		imageType.setImage(img);
//		img = new Image("templates/Icons/Meals.jpeg");
//		imageMeals.setImage(img);
	}

	/**
	 * Present empty screen of "update menu" and notify to sever that this window is
	 * ready
	 * 
	 * @param ID of the supplier
	 * @see tamplate of "update menu"
	 */
	@SuppressWarnings("unchecked")
	public void showWindow(String userID) {
		// log
		Logger.log(Level.INFO, "UpdateMenuWindow: showing window");
		System.out.println("UpdateMenuWindow: showing window");

		Platform.runLater(() -> {
			try {
				Scene scene = new Scene(supplierVBoxLoaded);
				this.scene = scene;
			} catch (IllegalArgumentException e) {
				// log
				Logger.log(Level.WARNING, "UpdateMenuWindow: exception in showWindow");
				System.out.println("UpdateMenuWindow: exception in showWindow");
			}
			StringBuilder welcomeMessage = new StringBuilder();
			welcomeMessage.append("Welcome, ");
			welcomeMessage.append(view.getFirstName());
			welcomeMessage.append("!");
			supplierLabel.setText(welcomeMessage.toString());
			primaryStage.setScene(scene);
			primaryStage.show();
			this.userID = userID;
			JSONObject json = new JSONObject();
			json.put("command", "Types presented is ready");
			json.put("userID", userID);
			view.ready(json);
		});
	}

	/**
	 * Private method that design a button.
	 * @param Button button - button to be designed , int height - height of button , int width - width of button, String colorBack - color of button 
	 */
	private void designButton(Button button, int height, int width, String colorBack) {
		button.setPrefHeight(height);
		button.setPrefWidth(width);
		button.setStyle("-fx-background-color: " + colorBack + ";");
		button.setFont(new Font("verdana", 12));
		button.setTextFill(Color.WHITE);
	}

	/**
	 * Private method that build popUp that will contain VBox.The method include
	 * functionality of submit the approval On press submit button --> final
	 * approval
	 * 
	 * @param VBox approvalDetails - contain the details, JSONObject json - the json
	 *             that builded for send to server
	 * @see Window with approval details.
	 */
	@SuppressWarnings("unchecked")
	public void showDeletePopup(JSONObject json) {
		Platform.runLater(() -> {
			Stage window = new Stage();
			
			window.initModality(Modality.APPLICATION_MODAL);
			window.setTitle("Delete");
			window.setMinWidth(300);
			window.setMinHeight(40);

			VBox layout = new VBox();
			Label check = new Label("Are you sure you want to delete?");
			check.setFont(Font.font("verdana", FontWeight.NORMAL, FontPosture.REGULAR, 14));
			layout.setAlignment(Pos.CENTER);
			layout.getChildren().add(check);
			HBox h = new HBox();
			Button Yes = new Button("Yes");
			Yes.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					window.hide();
					view.ready(json);
				}
			});
			designButton(Yes, 25, 40, "#20B2AA");
			h.getChildren().add(Yes);
			HBox.setMargin(Yes, new Insets(0, 0, 0, 80));
			Button No = new Button("No");
			No.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					window.hide();

				}
			});
			No.setPrefHeight(25);
			No.setPrefWidth(40);
			h.getChildren().add(No);
			HBox.setMargin(No, new Insets(0, 0, 0, 30));
			layout.getChildren().add(h);
			layout.setStyle("-fx-background-color: #FFFFF0;");
			VBox.setMargin(h, new Insets(5, 0, 5, 0));
			Scene scene = new Scene(layout);

			window.setScene(scene);
			window.show();
		});
	}

	/**
	 * Adds Types menu of the restaurant to the "update menu" window. The method
	 * include functionality of add new type, edit type, delete type and enter to
	 * their meal. On pressed "Add new type" button, notify to server that this
	 * button was pressed and "showAddTypeDetails" method will be called. On pressed
	 * "Edit type" button, notify to server that this button was pressed and
	 * "showEditTypeDetails" method will be called. On pressed "Delete type" button,
	 * notify to server that this button was pressed and "responseForDeleteType"
	 * method will be called. * On pressed "Enter" button, notify to server that
	 * this button was pressed and "showEditDishDetails" method will be called.
	 * 
	 * @param JSONObject descriptor - include JSONObject of the menu that include
	 *                   all items.
	 * @see Types menu window with relevant functionality
	 */
	public void showTypes(JSONObject descriptor) {
		showMenuDescriptor = descriptor;
		Platform.runLater(() -> {
			barToType();

			supplierVBox.getChildren().clear();
			ListView listView = new ListView();

			backButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					view.getSupplierWindow().showWindow();
				}
			});
			supplierLabel.setText("Type Of Meals");
			StringBuilder welcomeMessage = new StringBuilder();
			welcomeMessage.append("Welcome, ");
			welcomeMessage.append(view.getFirstName());
			welcomeMessage.append("!");
			labelSide.setText(welcomeMessage.toString());
			labelSide.setFont(new Font("verdana", 14));
			Logger.log(Level.INFO, "UpdateMenuWindow: showMenu");
			System.out.println("UpdateMenuWindow: showMenu");
			menu = (HashMap<String, JSONArray>) descriptor.get("menuList");
			Insets margin = new Insets(0, 10, 0, 10);
			Button addTypeButton = new Button("Add New Type");

			designButton(addTypeButton, 35, 120, "#D93B48");
			addTypeButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					JSONObject json = new JSONObject();
					addTypeButton.disableProperty().set(true);
					json.put("command", "Add Type button was pressed");
					json.put("imgType", "typeImg/noImg.jpg");
					view.ready(json);
				}
			});
			forAddType.getChildren().clear();
			forAddType.getChildren().add(addTypeButton);
			supplierVBox.getChildren().add(forAddType);
			VBox.setMargin(forAddType, new Insets(0, 0, 10, 0));
			for (String i : menu.keySet()) {
				JSONArray items = menu.get(i);
				JSONObject item = (JSONObject) items.get(0);
				String pathImg = (String) item.get("imgType");
				VBox v = new VBox();
				HBox h = new HBox();
				Label typeLabel = new Label(i);
				typeLabel.setPrefWidth(120);
				typeLabel.setFont(new Font("verdana", 16));
				h.getChildren().add(typeLabel);
				Image img = new Image(pathImg);
				ImageView imgView = new ImageView(img);
				imgView.setFitHeight(65);
				imgView.setFitWidth(65);

				h.getChildren().add(imgView);
				HBox.setMargin(typeLabel, margin);
				HBox.setMargin(imgView, margin);
				Button enterButton = new Button("Enter");
				designButton(enterButton, 35, 95, "#D93B48");
				enterButton.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						JSONObject json = new JSONObject();
						json.put("command", "Enter meals button was pressed");
						json.put("itemType", i);
						view.ready(json);
					}
				});
				h.getChildren().add(enterButton);
				HBox.setMargin(enterButton, new Insets(0, 0, 0, 60));
				Button editButton = new Button("Edit type");
				designButton(editButton, 35, 95, "#D93B48");
				editButton.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						JSONObject json = new JSONObject();
						json.put("command", "Edit type button was pressed");
						json.put("imgType", pathImg);
						json.put("itemType", i);
						view.ready(json);
					}
				});

				h.getChildren().add(editButton);
				HBox.setMargin(editButton, new Insets(0, 0, 0, 60));

				Button deleteButton = new Button("Delete type");
				designButton(deleteButton, 35, 95, "#D93B48");
				deleteButton.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						JSONObject json = new JSONObject();
						json.put("command", "Delete type Button was pressed");
						json.put("itemType", i);
						json.put("userID", userID);
						showDeletePopup(json);

					}
				});

				h.getChildren().add(deleteButton);
				HBox.setMargin(deleteButton, new Insets(0, 0, 0, 60));
				v.getChildren().add(h);
				forEditType.put(i, v);
				listView.getItems().add(v);
			}
			supplierVBox.getChildren().add(listView);
		});
	}

	/**
	 * Replace type menu in meal menu of the specified selected type. The method
	 * include functionality of add new meal, edit meal, and delete meal. On pressed
	 * "Add new meal" button, notify to server that this button was pressed and
	 * "showAddMealDetails" method will be called. On pressed "Edit meal" button,
	 * notify to server that this button was pressed and "showEditMealDetails"
	 * method will be called. On pressed "Delete meal" button, notify to server that
	 * this button was pressed and "responseForDeleteDish" method will be called.
	 * 
	 * @param String itemType - the selected type.
	 * @seeTypes meal menu window with relevant functionality
	 */
	public void showItemNames(String itemType) {
		
		Logger.log(Level.INFO, "UpdateMenuWindow: show item");
		System.out.println("UpdateMenuWindow: show item");
		Platform.runLater(() -> {
			supplierVBox.getChildren().clear();
			supplierLabel.setText(itemType + " Meals");
			backButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					showTypes(showMenuDescriptor);
				}
			});

			Button addMealButton = new Button("Add New Meal");
			designButton(addMealButton, 35, 120, "#D93B48");
			barToMeal();
			addMealButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					JSONObject json = new JSONObject();
					json.put("itemType", itemType);
					json.put("command", "Add Meal button was pressed");
					view.ready(json);
				}
			});
			supplierVBox.getChildren().add(addMealButton);
			VBox.setMargin(addMealButton, new Insets(0, 0, 10, 0));

			Insets margin = new Insets(0, 10, 0, 10);

			JSONArray items = menu.get(itemType);
			ListView listView = new ListView();
			int numOfItems = items.size();
			for (int i = 0; i < items.size(); i++) {
				HBox h = new HBox();
				JSONObject item = (JSONObject) items.get(i);
				String itemName = Message.getValueString(item, "itemName");
				if (itemName != null) {
					Label itemLabel = new Label(itemName);
					itemLabel.setPrefWidth(120);
					itemLabel.setFont(new Font("verdana", 16));
					h.getChildren().add(itemLabel);
					Image img = new Image(Message.getValueString(item, "imgMeal"));
					ImageView imgView = new ImageView(img);
					imgView.setFitHeight(65);
					imgView.setFitWidth(65);
					HBox.setMargin(itemLabel, margin);
					HBox.setMargin(imgView, margin);
					h.getChildren().add(imgView);
					Button editButton = new Button("Edit meal");
					designButton(editButton, 35, 95, "#D93B48");
					editButton.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) {
							JSONObject json = new JSONObject();
							json.put("command", "Edit meal button was pressed");
							json.put("itemName", Message.getValueString(item, "itemName"));
							json.put("itemType", itemType);
							json.put("item", item);
							json.put("imgMeal", Message.getValueString(item, "imgMeal"));
							view.ready(json);
						}
					});
					h.getChildren().add(editButton);
					HBox.setMargin(editButton, new Insets(0, 0, 0, 100));
					Button deleteButton = new Button("Delete meal");
					designButton(deleteButton, 35, 95, "#D93B48");
					deleteButton.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) {
							JSONObject json = new JSONObject();
							json.put("command", "Delete Meal Button was pressed");
							json.put("itemID", Message.getValueString(item, "itemID"));
							json.put("itemType", itemType);
							json.put("itemName", Message.getValueString(item, "itemName"));
							json.put("userID", userID);
							json.put("numOfItems", numOfItems);
							json.put("items", items);
							showDeletePopup(json);
						}
					});
					deleteButton.setPrefHeight(35);
					deleteButton.setPrefWidth(95);
					h.getChildren().add(deleteButton);
					HBox.setMargin(deleteButton, new Insets(0, 0, 0, 100));
					listView.getItems().add(h);

				}
			}

			supplierVBox.getChildren().add(listView);
			Button next = new Button("Go to home page");
			designButton(next, 35, 150, "#66CDAA");
			next.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					view.getSupplierWindow().showWindow();
				}
			});
			supplierVBox.getChildren().add(next);
			VBox.setMargin(next, new Insets(5, 0, 0, 650));

		});

	}

	/**
	 * Build popUp that will contain gridPame.
	 * 
	 * @param GridPane grid - contain image of types/meals.
	 * @see Window with image grid.
	 */
	public void showPopup(GridPane grid) {
		Platform.runLater(() -> {
			Stage window = new Stage();
			window.initModality(Modality.APPLICATION_MODAL);
			window.setTitle("Image");
			window.setMinWidth(250);
			window.setMinHeight(20);

			VBox layout = new VBox();
			layout.getChildren().add(grid);
			layout.setAlignment(Pos.CENTER);
			Button submit = new Button("Submit");
			submit.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					window.hide();
				}
			});
			designButton(submit, 25, 60, "#20B2AA");
			layout.getChildren().add(submit);
			VBox.setMargin(layout, new Insets(5, 0, 0, 0));
			Scene scene = new Scene(layout);
			window.setScene(scene);
			window.show();
		});
	}

	/**
	 * Build grid 3X3 that will contain type/meal images.
	 * 
	 * @param JSONObject json - the json that will returned(notify which image was
	 *                   chosen), ArrayList<String> imgName - contain image path,
	 *                   String key - image type/image meal ,String pathDefault -
	 *                   the path of image that was before(in new type/meal = "No
	 *                   image").
	 */
	public Button buildGridImg(JSONObject json, ArrayList<String> imgName, String key, String pathDefault) {
		GridPane imagesGrid = new GridPane();
		Button chooseImg = new Button();
		Image imgD = new Image(pathDefault);
		ImageView imgViewD = new ImageView(imgD);
		json.put(key, pathDefault);
		imgViewD.setFitHeight(50);
		imgViewD.setFitWidth(50);
		chooseImg.setGraphic(imgViewD);
		VBox vb = new VBox();
		vb.getChildren().add(chooseImg);
		ToggleGroup group = new ToggleGroup();
		imagesGrid.setVgap(10);
		imagesGrid.setHgap(10);
		int forImg = 0;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				String path = imgName.get(forImg);
				Image img = new Image(path);
				ImageView imgView = new ImageView(img);
				imgView.setFitHeight(50);
				imgView.setFitWidth(50);
				RadioButton imgButton = new RadioButton();
				imgButton.setToggleGroup(group);
				imgButton.setGraphic(imgView);

				imgButton.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						chooseImg.setGraphic(imgView);

						ImageView imgView1 = new ImageView(img);
						imgView1.setFitHeight(50);
						imgView1.setFitWidth(50);
						imgButton.setGraphic(imgView1);
						json.put(key, path);
					}
				});
				forImg++;
				imagesGrid.add(imgButton, i, j);

			}
		}

		chooseImg.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				showPopup(imagesGrid);
			}
		});
		return chooseImg;

	}

	public ArrayList<String> buildArrayForImages(String[] path) {
		ArrayList<String> imgName = new ArrayList<>();
		for (int i = 0; i < path.length; i++)
			imgName.add(path[i]);
		return imgName;
	}

	/**
	 * /** Adds to type menu window a small window that contain fields of edit. The
	 * method include functionality of enter type name , select image and save
	 * changes. On pressed "save" button, notify to server that this button was
	 * pressed and "afterPressedSaveEditType" method will be called.
	 * 
	 * @param JSONObject descriptor - include name exist type and image path of
	 *                   selected picture.
	 * @see Small window that contain fields of add on Update Type window
	 */
	public void showEditTypeDetails(JSONObject descriptor) {
		Logger.log(Level.WARNING, "UpdateMenuWindow: showEditTypeDetails");
		System.out.println("UpdateMenuWindow: showEditTypeDetails");
		JSONObject json = new JSONObject();
		String itemType = Message.getValueString(descriptor, "itemType");
		String imgType = Message.getValueString(descriptor, "imgType");
		VBox v = forEditType.get(itemType);

		json.put("imgType", "typeImg/noImg.jpg");
		Platform.runLater(() -> {
			forAddTypeV.getChildren().clear();
			Text typeTxt = new Text(itemType);
			typeTxt.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 20));
			forAddTypeV.getChildren().add(typeTxt);
			VBox.setMargin(typeTxt, new Insets(0, 0, 20, 300));
			HBox forEditHBox = new HBox();
			String[] path = { "typeImg/dessert.png", "typeImg/drinks.png", "typeImg/fish.png", "typeImg/pizzaDish.png",
					"typeImg/hamburger.png", "typeImg/meat.png", "typeImg/pasta.png", "typeImg/saladDish.png",
					"typeImg/sushi.png" };
			ArrayList<String> imgName = buildArrayForImages(path);
			Button imgGrid = buildGridImg(json, imgName, "imgType", imgType);
			Tooltip.install(imgGrid, new Tooltip("Click here to select image"));
			forEditHBox.getChildren().add(imgGrid);
			HBox.setMargin(imgGrid, new Insets(0, 0, 0, 70));
			Label name = new Label("Name:	");
			name.setFont(Font.font("verdana", FontWeight.NORMAL, FontPosture.REGULAR, 16));
			forEditHBox.getChildren().add(name);
			HBox.setMargin(name, new Insets(20, 0, 0, 140));
			TextField nameField = new TextField();
			nameField.setText(itemType);
			HBox hb = new HBox();
			VBox forFeedback = new VBox();

			feedAddType.setText("");
			forFeedback.getChildren().add(feedAddType);
			forFeedback.getChildren().add(nameField);
			forEditHBox.getChildren().add(forFeedback);

			Button saveButton = new Button("Save");
			Button cancelButton = new Button("Cancel");
			saveButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					saveButton.disableProperty().set(true);
					cancelButton.disableProperty().set(true);
					json.put("nameType", nameField.getText());
					json.put("userID", userID);
					json.put("itemType", itemType);
					Button exit = new Button("exit");
					exit.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) {
							json.put("command", "Exit edit type was pressed");
							view.ready(json);
						}
					});
					exit.setPrefWidth(60);
					exit.setPrefHeight(25);
					exit.setFont(new Font("verdana", 12));
					hb.getChildren().add(exit);
					exit.setPrefWidth(60);
					HBox.setMargin(exit, new Insets(0, 0, 0, 40));
					json.put("command", "Save edit type was pressed");

					view.ready(json);
				}
			});

			cancelButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					showTypes(showMenuDescriptor);
				}
			});
			cancelButton.setPrefWidth(60);
			cancelButton.setPrefHeight(25);
			cancelButton.setFont(new Font("verdana", 12));
			designButton(saveButton, 25, 60, "#20B2AA");
			hb.getChildren().add(saveButton);
			hb.getChildren().add(cancelButton);
			HBox.setMargin(cancelButton, new Insets(0, 0, 0, 40));

			forAddTypeV.getChildren().add(forEditHBox);
			forAddTypeV.getChildren().add(hb);
			VBox.setMargin(hb, new Insets(20, 20, 0, 370));
			v.getChildren().add(forAddTypeV);
		});
	}

	/**
	 * Adds feedback to the small "edit type" window. The method include
	 * functionality that present feedback with relevant color(red/green).
	 * 
	 * @param JSONObject descriptor - include JSONObject of the menu that include
	 *                   all items.
	 * @see The same small window that contain fields of edit with feedback on
	 *      Update Type window
	 */
	public void afterPressedSaveEditType(JSONObject descriptor) {
		Logger.log(Level.WARNING, "UpdateMenuWindow: afterPressedSaveEditType");
		System.out.println("UpdateMenuWindow: afterPressedSaveEditType");

		String feedback = Message.getValueString(descriptor, "feedback");

		boolean feedOK = (boolean) descriptor.get("feedOK");
		Platform.runLater(() -> {
			feedAddType.setText(feedback);
			feedAddType.setFont(new Font("verdana", 12));
			if (feedOK)
				feedAddType.setTextFill(Color.GREEN);
			else
				feedAddType.setTextFill(Color.RED);
		});
		JSONObject menuOb = (JSONObject) descriptor.get("menu");

		showMenuDescriptor = menuOb;

	}

	/**
	 * Adds to type menu window a small window that contain fields of edit. The
	 * method include functionality of enter type name , select image and save
	 * changes. On pressed "save" button, notify to server that this button was
	 * pressed and "afterPressedSaveAddType" method will be called.
	 * 
	 * @see Small window that contain fields of edit on Update Type window
	 */
	public void showAddTypeDetails() {
		Logger.log(Level.WARNING, "UpdateMenuWindow: showAddTypeDetails");
		System.out.println("UpdateMenuWindow: showAddTypeDetails");

		JSONObject json = new JSONObject();
		Platform.runLater(() -> {

			json.put("imgType", "typeImg/noImg.jpg");
			feedAddType.setText("");
			forAddTypeV.getChildren().clear();
			Text itemTxt = new Text("New Type");
			itemTxt.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 20));
			forAddTypeV.getChildren().add(itemTxt);
			VBox.setMargin(itemTxt, new Insets(0, 0, 20, 300));
			HBox forEditHBox = new HBox();
			VBox forFeedback = new VBox();
			String[] path = { "typeImg/dessert.png", "typeImg/drinks.png", "typeImg/fish.png", "typeImg/pizzaDish.png",
					"typeImg/hamburger.png", "typeImg/meat.png", "typeImg/pasta.png", "typeImg/saladDish.png",
					"typeImg/sushi.png" };
			ArrayList<String> imgName = buildArrayForImages(path);
			Button imgGrid = buildGridImg(json, imgName, "imgType", "typeImg/noImg.jpg");
			Tooltip.install(imgGrid, new Tooltip("Click here to select image"));
			forEditHBox.getChildren().add(imgGrid);
			HBox.setMargin(imgGrid, new Insets(0, 0, 0, 70));
			Label name = new Label("Name:	");
			name.setFont(Font.font("verdana", FontWeight.NORMAL, FontPosture.REGULAR, 16));
			forEditHBox.getChildren().add(name);
			HBox.setMargin(name, new Insets(20, 0, 0, 140));
			TextField nameField = new TextField();
			forFeedback.getChildren().add(feedAddType);
			forFeedback.getChildren().add(nameField);
			forEditHBox.getChildren().add(forFeedback);

			Button cancelButton = new Button("Cancel");
			Button saveButton = new Button("Save");
			HBox hb = new HBox();
			saveButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					saveButton.disableProperty().set(true);
					json.put("nameType", nameField.getText());
					json.put("userID", userID);
					Button exit = new Button("exit");
					exit.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) {
							json.put("command", "Exit add type was pressed");
							view.ready(json);
						}
					});
					exit.setPrefWidth(60);
					exit.setPrefHeight(25);
					exit.setFont(new Font("verdana", 12));
					hb.getChildren().add(exit);

					HBox.setMargin(exit, new Insets(0, 0, 0, 40));
					cancelButton.disableProperty().set(true);
					json.put("command", "Save add type was pressed");

					view.ready(json);
				}
			});

			cancelButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					forAddType.getChildren().get(0).disableProperty().set(false);
					forAddType.getChildren().remove(forAddTypeV);
				}
			});
			cancelButton.setPrefWidth(60);
			cancelButton.setPrefHeight(25);
			cancelButton.setFont(new Font("verdana", 12));
			designButton(saveButton, 25, 60, "#20B2AA");

			hb.getChildren().add(saveButton);
			hb.getChildren().add(cancelButton);
			HBox.setMargin(cancelButton, new Insets(0, 0, 0, 40));
			forAddTypeV.getChildren().add(forEditHBox);

			forAddTypeV.getChildren().add(hb);
			VBox.setMargin(hb, new Insets(20, 20, 0, 370));
			forAddType.getChildren().add(forAddTypeV);
		});
	}

	/**
	 * Adds feedback to the small "add new type" window. The method include
	 * functionality that present feedback with relevant color(red/green).
	 * 
	 * @param JSONObject descriptor - include JSONObject of the menu that include
	 *                   all items.
	 * @see The same small window that contain fields of add with feedback on Update
	 *      Type window
	 */
	public void afterPressedSaveAddType(JSONObject descriptor) {
		Logger.log(Level.WARNING, "UpdateMenuWindow: afterPressedSaveAddType");
		System.out.println("UpdateMenuWindow: afterPressedSaveAddType");
		String feedback = Message.getValueString(descriptor, "feedback");
		boolean feedOK = (boolean) descriptor.get("feedOK");
		Platform.runLater(() -> {
			feedAddType.setText(feedback);
			feedAddType.setFont(new Font("verdana", 12));
			if (feedOK)
				feedAddType.setTextFill(Color.GREEN);
			else
				feedAddType.setTextFill(Color.RED);
		});

		JSONObject menuOb = (JSONObject) descriptor.get("menu");
		showMenuDescriptor = menuOb;

	}

	/**
	 * Back to Update Type window after changes
	 * 
	 * @param JSONObject descriptor - include JSONObject of the menu that include
	 *                   all items.
	 * @see Update Type window after changes
	 */
	public void responseForExitAddType(JSONObject descriptor) {
		Logger.log(Level.WARNING, "UpdateMenuWindow: responseForExitType");
		System.out.println("UpdateMenuWindow: responseForExitType");
		Platform.runLater(() -> {
			showTypes(showMenuDescriptor);
		});
	}

	/**
	 * Back to Update Type window after changes
	 * 
	 * @see Update Type window after changes
	 */
	public void responseForExitEditType() {
		Logger.log(Level.WARNING, "UpdateMenuWindow: responseForExitEditType");
		System.out.println("UpdateMenuWindow: responseForExitEditType");
		Platform.runLater(() -> {
			showTypes(showMenuDescriptor);
		});

	}

	/**
	 * Stay in Update Type window after remove specified type
	 * 
	 * @param JSONObject descriptor - include JSONObject of the menu that include
	 *                   all items.
	 * @see Update Type window after remove specified type
	 */
	public void responseForDeleteType(JSONObject descriptor) {
		Logger.log(Level.WARNING, "UpdateMenuWindow: responseForDeleteType");
		System.out.println("UpdateMenuWindow: responseForDeleteType");
		JSONObject menuOb = (JSONObject) descriptor.get("menu");
		showMenuDescriptor = menuOb;
		Platform.runLater(() -> {
			showTypes(showMenuDescriptor);
		});

	}

	/**
	 *
	 * The method include functionality of save changes and delete feature. On press
	 * save, notify to server that this button was pressed and "responseForEditFeat"
	 * method will be called. On press delete, remember it and wait for pressing
	 * save.
	 * 
	 * @param JSONObject feature - include the details of the feature(name/price),
	 *                   String key1 - Optional/Must, String key2 - optional/must,
	 *                   String ID - feature's ID in sql, RadioButton featureRb -
	 *                   radio button of edit must/optional feature ,VBox vb -
	 *                   include the fields of edit, String oldName - the real name
	 *                   of feature(before chenged)
	 * @see Name and price fields of specified feature on edit meal window.
	 */
	private void showEditFeature(JSONObject feature, String key1, String key2, String ID, RadioButton featureRb,
			VBox vb, String oldName) {
		JSONObject json = new JSONObject();

		json.put("delete", false);

		json.put("oldName", oldName);
		HBox forEdHBox = editFeatH.get(oldName);
		Label feed = new Label("");
		forEdHBox.getChildren().add(feed);
		Label name = new Label("Name: ");
		name.setFont(new Font("verdana", 12));
		forEdHBox.getChildren().add(name);
		HBox.setMargin(name, new Insets(0, 0, 0, 0));
		TextField nameField = new TextField();
		nameField.setMaxWidth(80);
		nameField.setText((String) feature.get(key2));
		forEdHBox.getChildren().add(nameField);
		Label price = new Label("	Price: ");
		price.setFont(new Font("verdana", 12));
		forEdHBox.getChildren().add(price);
		TextField priceField = new TextField();
		priceField.setMaxWidth(80);
		priceField.setText(Message.getValueString(feature, "price"));
		forEdHBox.getChildren().add(priceField);
		JSONObject forEditFeat = new JSONObject();
		forEditFeat.put("delete", false);
		forEditFeat.put("edit", false);
		Button delete = new Button("delete");
		Button saveButton = new Button("Save");
		saveButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				saveButton.disableProperty().set(true);
				delete.disableProperty().set(true);

				json.put("command", "Save edit feature was pressed");
				json.put("feature", nameField.getText());
				json.put("price", priceField.getText());
				json.put("key", key1);

				forEditFeat.put("newName", nameField.getText());
				forEditFeat.put("newPrice", priceField.getText());
				forEditFeat.put("ID", ID);
				forEditFeat.put("edit", true);
				json.put("forEditFeat", forEditFeat);

				if (key1.equals("Optional")) {
					json.put("map", optionalFeat);
					json.put("editFeat", editOpArr);
				}
				if (key1.equals("Must")) {
					json.put("map", mustFeat);
					json.put("editFeat", editMustArr);
				}
				view.ready(json);

			}
		});
		designButton(saveButton, 25, 50, "#20B2AA");

		forEdHBox.getChildren().add(saveButton);
		HBox.setMargin(saveButton, new Insets(0, 0, 0, 5));

		delete.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				forEditFeat.put("delete", true);
				json.put("delete", true);
				saveButton.disableProperty().set(false);
				delete.disableProperty().set(true);
				featureRb.disableProperty().set(true);
				Label feed = new Label("Please enter save");
				feed.setTextFill(Color.BLUE);
				vb.getChildren().add(0, feed);
			}
		});
		delete.setPrefHeight(25);
		delete.setPrefWidth(50);
		forEdHBox.getChildren().add(delete);
		HBox.setMargin(delete, new Insets(0, 0, 0, 5));
		vb.getChildren().add(forEdHBox);

	}

	/**
	 *
	 * The method include functionality of save changes. On press save, notify to
	 * server that this button was pressed and "responseForAddFeat" method will be
	 * called.
	 * 
	 * @param String key - Must/Optional
	 * @see Name and price fields of new feature on edit meal window.
	 */
	public VBox addFeature(String key) {
		VBox vb = new VBox();
		Button addFeature = new Button("Add " + key + " feature");
		addFeature.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				addFeature.disableProperty().set(true);
				HBox hb = new HBox();
				Label nameTxt = new Label("Name: ");

				hb.getChildren().add(nameTxt);
				HBox.setMargin(nameTxt, new Insets(5, 0, 0, 0));
				TextField fieldName = new TextField();
				fieldName.setMaxWidth(80);
				hb.getChildren().add(fieldName);
				HBox.setMargin(fieldName, new Insets(5, 0, 0, 0));
				Label priceTxt = new Label("	Price: ");

				hb.getChildren().add(priceTxt);
				HBox.setMargin(priceTxt, new Insets(5, 0, 0, 0));
				TextField priceField = new TextField();
				priceField.setMaxWidth(80);
				hb.getChildren().add(priceField);
				HBox.setMargin(priceField, new Insets(5, 0, 0, 0));
				Button saveButton = new Button("V");
				saveButton.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {

						JSONObject json = new JSONObject();
						saveButton.disableProperty().set(true);

						json.put("command", "Save feature was pressed");
						json.put("feature", fieldName.getText());
						json.put("price", priceField.getText());
						json.put("key", key);

						if (key.equals("Optional")) {
							json.put("map", optionalFeat);

						}
						if (key.equals("Must")) {
							json.put("map", mustFeat);

						}
						view.ready(json);
					}
				});
				designButton(saveButton, 25, 25, "#20B2AA");
				hb.getChildren().add(saveButton);
				HBox.setMargin(saveButton, new Insets(0, 0, 0, 5));
				Button exitButton = new Button("X");
				exitButton.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						addFeature.disableProperty().set(false);
						vb.getChildren().remove(hb);
						feedAddOptional.setText("");
						feedAddMust.setText("");
						exitButton.setBorder(new Border(new BorderStroke(Color.BLUE, BorderStrokeStyle.SOLID,
								CornerRadii.EMPTY, BorderWidths.DEFAULT)));

					}
				});

				hb.getChildren().add(exitButton);
				HBox.setMargin(exitButton, new Insets(0, 0, 0, 5));
				vb.getChildren().add(hb);

			}
		});
		designButton(addFeature, 30, 160, "#D93B48");
		vb.getChildren().add(addFeature);
		if (key.equals("Optional"))
			vb.getChildren().add(feedAddOptional);
		else
			vb.getChildren().add(feedAddMust);
		VBox.setMargin(addFeature, new Insets(10, 0, 0, 0));
		return vb;

	}

	/**
	 * /** New window that contain fields of edit meal. The window is built
	 * dynamically and include functionality of enter meal name and price , select
	 * image, add and edit features and save changes. On pressed "save" button,
	 * notify to server that this button was pressed and "afterPressedSaveEditDish"
	 * method will be called. On pressed add/edit feature button, add to the window
	 * edit features field.
	 * 
	 * @param JSONObject descriptor - include relevant details about the meal, like
	 *                   image path, name and price.
	 * @see Window that contain fields of edit meal.
	 */
	public void showEditDishDetails(JSONObject descriptor) {
		JSONObject json = new JSONObject();
		json.put("command", "Save edit meal was pressed");
		ListView listView = new ListView();
		Logger.log(Level.WARNING, "UpdateMenuWindow: showEditDishDetails");
		System.out.println("UpdateMenuWindow: showEditDishDetails");
		String itemName = Message.getValueString(descriptor, "itemName");
		JSONObject item = (JSONObject) descriptor.get("item");
		json.put("imgMeal", Message.getValueString(item, "imgMeal")); // default
		JSONArray mustEdit = new JSONArray();
		JSONArray optionalEdit = new JSONArray();
		String itemType = Message.getValueString(descriptor, "itemType");
		json.put("dishName", itemName);
		Platform.runLater(() -> {
			backButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					showItemNames(itemType);
				}
			});
			mustFeat.clear();
			optionalFeat.clear();
			feedAddOptional.setText("");
			feedAddMust.setText("");
			supplierVBox.getChildren().clear();
			forAddFeat.getChildren().clear();
			Text itemTxt = new Text(itemName);
			itemTxt.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 20));
			supplierVBox.getChildren().add(itemTxt);
			VBox.setMargin(itemTxt, new Insets(0, 0, 20, 300));
			VBox forEdFeat = new VBox();
			HBox forEditHBox = new HBox();
			String[] path = { "mealImg/cake.png", "mealImg/chiken.png", "mealImg/cookie.png", "mealImg/empanadas.png",
					"mealImg/hotdog.png", "mealImg/meat.png", "mealImg/pizza.png", "mealImg/rise.png",
					"mealImg/vegetable.png" };
			ArrayList<String> imgName = buildArrayForImages(path);
			Button imageGridB = buildGridImg(json, imgName, "imgMeal", Message.getValueString(descriptor, "imgMeal"));
			Tooltip.install(imageGridB, new Tooltip("Click here to select image"));
			forEditHBox.getChildren().add(imageGridB);
			HBox.setMargin(imageGridB, new Insets(0, 0, 0, 50));
			Label name = new Label("Name:	");
			name.setFont(new Font("verdana", 14));
			forEditHBox.getChildren().add(name);
			HBox.setMargin(name, new Insets(0, 0, 0, 20));
			TextField nameField = new TextField();
			nameField.setMaxWidth(80);
			nameField.setText(itemName);
			forEditHBox.getChildren().add(nameField);

			Label price = new Label("	Price:	");
			price.setFont(new Font("verdana", 14));
			forEditHBox.getChildren().add(price);
			TextField priceField = new TextField();
			priceField.setMaxWidth(80);
			priceField.setText(Message.getValueString(item, "itemPrice"));
			forEditHBox.getChildren().add(priceField);
			viewFeatures.getChildren().clear();

			forEditHBox.getChildren().add(viewFeatures);
			HBox.setMargin(viewFeatures, new Insets(0, 0, 0, 12));
			forEdFeat.getChildren().add(forEditHBox);
			supplierVBox.getChildren().add(forEdFeat);
			VBox rbOp = new VBox();
			JSONArray optionalFeatures = (JSONArray) item.get("optionalFeatures");
			if (!optionalFeatures.isEmpty()) {
				Label optionalLabel = new Label("Optionals Features:");
				rbOp.getChildren().add(optionalLabel);
				VBox.setMargin(optionalLabel, new Insets(5, 0, 0, 280));
			}
			VBox featO = addFeature("Optional");
			forAddFeat.getChildren().add(featO);
			HBox.setMargin(featO, new Insets(20, 0, 0, 120));
			HBox rbHbox = new HBox();

			VBox rbMu = new VBox();
			for (int j = 0; j < optionalFeatures.size(); j++) {
				JSONObject option = (JSONObject) optionalFeatures.get(j);
				VBox opVb = new VBox();
				Label feedOp = new Label();
				opVb.getChildren().add(feedOp);
				String nameOp = Message.getValueString(option, "optional");
				String priceOp = Message.getValueString(option, "price");
				String IDOp = Message.getValueString(option, "opID");
				RadioButton optionRb = new RadioButton(nameOp);
				optionRb.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						HBox featH = new HBox();
						if (!optionRb.isSelected()) {
							HBox hide = editFeatH.get(nameOp);
							opVb.getChildren().remove(hide);
						} else {
							editFeatH.put(nameOp, featH);
							feedEditFeatV.put(nameOp, opVb);
							showEditFeature(option, "Optional", "optional", IDOp, optionRb, opVb, nameOp);

						}
					}
				});

				JSONObject forEditFeat = new JSONObject();
				forEditFeat.put("newName", nameOp);
				forEditFeat.put("newPrice", priceOp);
				forEditFeat.put("ID", IDOp);
				forEditFeat.put("delete", false);
				forEditFeat.put("edit", false);
				editOpArr.add(forEditFeat);

				optionalFeat.put(nameOp, priceOp);
				opVb.getChildren().add(optionRb);
				VBox.setMargin(optionRb, new Insets(5, 0, 0, 300));
				rbOp.getChildren().add(opVb);

			}
			rbHbox.getChildren().add(rbOp);
			JSONArray mustFeature = (JSONArray) item.get("mustFeatures");
			if (!mustFeature.isEmpty()) {
				Label mustLabel = new Label("Must Features:");
				rbMu.getChildren().add(mustLabel);
				VBox.setMargin(mustLabel, new Insets(5, 0, 0, 180));
			}
			VBox featM = addFeature("Must");
			forAddFeat.getChildren().add(featM);
			HBox.setMargin(featM, new Insets(20, 0, 0, 50));

			for (int j = 0; j < mustFeature.size(); j++) {
				JSONObject must = (JSONObject) mustFeature.get(j);
				VBox muVb = new VBox();
				Label feedMu = new Label();
				muVb.getChildren().add(feedMu);
				String nameMu = Message.getValueString(must, "must");
				String priceMu = Message.getValueString(must, "price");
				String IDMu = Message.getValueString(must, "muID");
				RadioButton mustRb = new RadioButton((String) must.get("must"));
				mustRb.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						HBox featH = new HBox();
						if (!mustRb.isSelected()) {
							HBox hide = editFeatH.get(nameMu);
							muVb.getChildren().remove(hide);
						} else {
							editFeatH.put(nameMu, featH);
							feedEditFeatV.put(nameMu, muVb);
							showEditFeature(must, "Must", "must", IDMu, mustRb, muVb, nameMu);

						}

					}
				});

				JSONObject forEditFeat = new JSONObject();
				forEditFeat.put("newName", nameMu);
				forEditFeat.put("newPrice", priceMu);
				forEditFeat.put("ID", IDMu);
				forEditFeat.put("delete", false);
				forEditFeat.put("edit", false);
				editMustArr.add(forEditFeat);

				mustFeat.put(nameMu, priceMu);
				muVb.getChildren().add(mustRb);
				VBox.setMargin(mustRb, new Insets(5, 0, 0, 180));
				rbMu.getChildren().add(muVb);

			}
			rbHbox.getChildren().add(rbMu);

			supplierVBox.getChildren().add(rbHbox);

			HBox hb = new HBox();
			Button saveButton = new Button("Save");
			saveButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					saveButton.disableProperty().set(true);

					Button exit = new Button("exit");
					exit.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) {
							json.put("command", "Exit edit/add mael was pressed");
							json.put("itemType", itemType);
							view.ready(json);
						}
					});
					exit.setPrefWidth(60);
					exit.setPrefHeight(25);
					exit.setFont(new Font("verdana", 12));
					hb.getChildren().add(exit);

					HBox.setMargin(exit, new Insets(5, 0, 0, 40));

					json.put("newDishName", nameField.getText());
					json.put("newDishPrice", priceField.getText());
					json.put("userID", userID);
					json.put("itemID", Message.getValueString(item, "itemID"));
					json.put("itemType", Message.getValueString(descriptor, "itemType"));
					json.put("menu", menu);
					json.put("selectedItem", item);
					json.put("optionalEdit", editOpArr);
					json.put("mustEdit", editMustArr);
					json.put("optionalFeat", optionalFeat);
					json.put("mustFeat", mustFeat);
					view.ready(json);
				}
			});

			designButton(saveButton, 25, 60, "#20B2AA");
			hb.getChildren().add(saveButton);
			HBox.setMargin(saveButton, new Insets(5, 0, 0, 650));
			supplierVBox.getChildren().add(forAddFeat);
			supplierVBox.getChildren().add(hb);

		});
	}

	/**
	 * Adds feedback to edit/add meal window. The method include functionality that
	 * present feedback with relevant color(red/green).
	 * 
	 * @param JSONObject descriptor - include JSONObject of the menu that include
	 *                   all items and relevant feedback.
	 * @see Edit/Add mael window with feedback.
	 */
	public void afterPressedSaveEditDish(JSONObject descriptor) {
		Logger.log(Level.WARNING, "UpdateMenuWindow: afterPressedSaveEditDish");
		System.out.println("UpdateMenuWindow: afterPressedSaveEditDish");
		JSONObject menuOb = (JSONObject) descriptor.get("menu");
		showMenuDescriptor = menuOb;
		menu = (HashMap<String, JSONArray>) showMenuDescriptor.get("menuList");
		String feedback = Message.getValueString(descriptor, "feedback");
		Label feedbackShow = new Label(feedback);
		feedbackShow.setFont(Font.font("verdana", FontWeight.MEDIUM, FontPosture.REGULAR, 14));
		boolean feedOK = (boolean) descriptor.get("feedOK");
		Platform.runLater(() -> {
			if (feedOK)
				feedbackShow.setTextFill(Color.GREEN);
			else
				feedbackShow.setTextFill(Color.RED);

			supplierVBox.getChildren().add(feedbackShow);
		});
	}

	/**
	 * /** New window that contain fields of add meal. The window is built
	 * dynamically and include functionality of enter meal name and price , select
	 * image, add features and save new details. On pressed "save" button, notify to
	 * server that this button was pressed and "afterPressedSaveEditDish" method
	 * will be called. On pressed add feature button, add to the window add features
	 * field.
	 * 
	 * @param JSONObject descriptor - include relevant the type of new meal.
	 * @see Window that contain fields of add meal.
	 */
	public void showAddMealDetails(JSONObject descriptor) {
		Logger.log(Level.WARNING, "UpdateMenuWindow: showAddMealDetails");
		System.out.println("UpdateMenuWindow: showAddMealDetails");
		ListView listView = new ListView();
		String itemType = Message.getValueString(descriptor, "itemType");
		String imgType = (String) ((JSONObject) menu.get(itemType).get(0)).get("imgType");
		JSONObject json = new JSONObject();
		mustFeat.clear();
		optionalFeat.clear();

		json.put("imgMeal", "typeImg/noImg.jpg"); // default
		json.put("command", "Save Meal was pressed");
		Platform.runLater(() -> {
			backButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					showItemNames(itemType);
				}
			});
			supplierVBox.getChildren().clear();
			forAddFeat.getChildren().clear();
			feedAddOptional.setText("");
			feedAddMust.setText("");
			Text itemTxt = new Text("New Meal");
			itemTxt.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 20));
			supplierVBox.getChildren().add(itemTxt);
			VBox.setMargin(itemTxt, new Insets(0, 0, 20, 300));
			HBox forAddHBox = new HBox();
			String[] path = { "mealImg/cake.png", "mealImg/chiken.png", "mealImg/cookie.png", "mealImg/empanadas.png",
					"mealImg/hotdog.png", "mealImg/meat.png", "mealImg/pizza.png", "mealImg/rise.png",
					"mealImg/vegetable.png" };
			ArrayList<String> imgName = buildArrayForImages(path);
			Button imageGridB = buildGridImg(json, imgName, "imgMeal", "typeImg/noImg.jpg");
			Tooltip.install(imageGridB, new Tooltip("Click here to select image"));

			forAddHBox.getChildren().add(imageGridB);
			HBox.setMargin(imageGridB, new Insets(0, 0, 0, 50));
			Label nameTxt = new Label("Meal Name:	");
			nameTxt.setFont(new Font("verdana", 14));
			forAddHBox.getChildren().add(nameTxt);
			HBox.setMargin(nameTxt, new Insets(0, 0, 0, 20));
			TextField nameField = new TextField();
			nameField.setPrefWidth(80);
			forAddHBox.getChildren().add(nameField);
			Label priceTxt = new Label("	Meal Price:	  ");
			priceTxt.setFont(new Font("verdana", 14));
			forAddHBox.getChildren().add(priceTxt);
			TextField priceField = new TextField();
			priceField.setPrefWidth(80);
			forAddHBox.getChildren().add(priceField);
			viewFeatures.getChildren().clear();

			forAddHBox.getChildren().add(viewFeatures);
			HBox.setMargin(viewFeatures, new Insets(0, 0, 0, 12));
			supplierVBox.getChildren().add(forAddHBox);
			JSONArray mustAdd = new JSONArray();
			JSONArray optionalAdd = new JSONArray();
			VBox featO = addFeature("Optional");
			forAddFeat.getChildren().add(featO);
			HBox.setMargin(featO, new Insets(5, 0, 0, 120));

			VBox featM = addFeature("Must");
			forAddFeat.getChildren().add(featM);
			HBox.setMargin(featM, new Insets(5, 0, 0, 50));

			supplierVBox.getChildren().add(forAddFeat);
			HBox hb = new HBox();
			Button saveButton = new Button("Save");
			saveButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					saveButton.disableProperty().set(true);

					Button exit = new Button("exit");
					exit.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) {
							json.put("command", "Exit edit/add mael was pressed");
							json.put("itemType", itemType);
							view.ready(json);
						}
					});
					exit.setPrefWidth(60);
					exit.setPrefHeight(25);
					exit.setFont(new Font("verdana", 12));
					hb.getChildren().add(exit);

					HBox.setMargin(exit, new Insets(0, 0, 0, 40));
					json.put("newDishName", nameField.getText());
					json.put("newDishPrice", priceField.getText());
					json.put("userID", userID);
					json.put("itemType", itemType);
					json.put("optionalAdd", optionalAdd);
					json.put("mustAdd", mustAdd);
					json.put("imgType", imgType);

					json.put("optionalFeat", optionalFeat);
					json.put("mustFeat", mustFeat);
					view.ready(json);
				}
			});

			designButton(saveButton, 25, 60, "#20B2AA");
			hb.getChildren().add(saveButton);
			HBox.setMargin(saveButton, new Insets(0, 0, 0, 5));
			supplierVBox.getChildren().add(hb);
			VBox.setMargin(hb, new Insets(100, 0, 0, 600));

		});
	}

	/**
	 * Stay in Meals window after remove specified meal
	 * 
	 * @param JSONObject descriptor - include JSONObject of the menu that include
	 *                   all items.
	 * @see Meals window after remove specified meal
	 */
	public void responseForDeleteDish(JSONObject descriptor) {
		Logger.log(Level.WARNING, "UpdateMenuWindow: responseForDeleteDish");
		System.out.println("UpdateMenuWindow: responseForDeleteDish");
		Platform.runLater(() -> {

			showItemNames(Message.getValueString(descriptor, "itemType"));

		});
		JSONObject menuOb = (JSONObject) descriptor.get("menu");
		showMenuDescriptor = menuOb;
		menu = (HashMap<String, JSONArray>) showMenuDescriptor.get("menuList");

	}

	/**
	 * Stay in Edit/Add meal window and show relevant feedback about adding mew
	 * feature
	 * 
	 * @param JSONObject descriptor - include JSONObject feedback, the feature and
	 *                   price feature, and the list of feature from his
	 *                   type(optional/must)
	 * @see Edit/Add meal window with commant above the feature.
	 */
	public void responseForAddFeat(JSONObject descriptor) {
		Logger.log(Level.WARNING, "UpdateMenuWindow: responseForAddFeat");
		System.out.println("UpdateMenuWindow: responseForAddFeat");
		String feedback = Message.getValueString(descriptor, "feedback");
		String feature = Message.getValueString(descriptor, "feature");
		String price = Message.getValueString(descriptor, "price");
		HashMap<String, String> map = (HashMap<String, String>) descriptor.get("map");

		String key = Message.getValueString(descriptor, "key");
		if (key.equals("Must"))
			mustFeat = map;
		if (key.equals("Optional"))
			optionalFeat = map;
		boolean feedOK = (boolean) descriptor.get("feedOK");
		Platform.runLater(() -> {
			if (key.equals("Optional")) {
				feedAddOptional.setText(feedback);
				feedAddOptional.setFont(Font.font("verdana", FontWeight.MEDIUM, FontPosture.REGULAR, 12));
				if (feedOK) {
					feedAddOptional.setTextFill(Color.GREEN);
					Label l = new Label(key + ": " + feature + " " + price + " NIS");
					viewFeatures.getChildren().add(l);
				} else
					feedAddOptional.setTextFill(Color.RED);

			} else {
				feedAddMust.setText(feedback);
				feedAddOptional.setFont(Font.font("verdana", FontWeight.MEDIUM, FontPosture.REGULAR, 12));
				if (feedOK) {
					feedAddMust.setTextFill(Color.GREEN);
					Label l = new Label(key + ": " + feature + " " + price + " NIS");
					viewFeatures.getChildren().add(l);
				} else
					feedAddMust.setTextFill(Color.RED);
			}
		});
	}

	/**
	 * Stay in Edit meal window and show relevant feedback about adding mew feature
	 * 
	 * @param JSONObject descriptor - include JSONObject feedback, the feature and
	 *                   price feature, and the list of feature from his
	 *                   type(optional/must)
	 * @see Edit meal window with commant above the feature.
	 */
	public void responseForEditFeat(JSONObject descriptor) {
		Logger.log(Level.WARNING, "UpdateMenuWindow: responseForEditFeat");
		System.out.println("UpdateMenuWindow: responseForEditFeat");
		JSONArray editFeat = (JSONArray) descriptor.get("editFeat");
		String key = Message.getValueString(descriptor, "key");
		HashMap<String, String> map = (HashMap<String, String>) descriptor.get("map");
		String feedback = Message.getValueString(descriptor, "feedback");
		String feature = Message.getValueString(descriptor, "feature");
		String oldName = Message.getValueString(descriptor, "oldName");
		String price = Message.getValueString(descriptor, "price");
		Platform.runLater(() -> {

			VBox viewfeed = feedEditFeatV.get(oldName);
			Label feed = new Label();

			viewfeed.getChildren().remove(0);
			viewfeed.getChildren().add(0, feed);
			if (key.equals("Must")) {
				editMustArr = editFeat;
				mustFeat = map;
			}
			if (key.equals("Optional")) {
				optionalFeat = map;
				editOpArr = editFeat;
			}
			boolean feedOK = (boolean) descriptor.get("feedOK");

			feed.setText(feedback);
			feed.setFont(Font.font("verdana", FontWeight.MEDIUM, FontPosture.REGULAR, 12));
			if (feedOK) {
				feed.setTextFill(Color.GREEN);
				Label l = new Label(key + ": " + feature + " " + price + " NIS");
				viewFeatures.getChildren().add(l);
				viewfeed.getChildren().get(1).disableProperty().set(true);
			} else
				feed.setTextFill(Color.RED);

		});

	}

	/**
	 * Back to Meals window after changes
	 * 
	 * @see Meals window after changes
	 */
	public void responseForExitAddMeal(String itemType) {
		Logger.log(Level.WARNING, "UpdateMenuWindow: responseForExitAddMeal");
		System.out.println("UpdateMenuWindow: responseForExitAddMeal");
		Platform.runLater(() -> {
			showItemNames(itemType);
		});

	}

}
