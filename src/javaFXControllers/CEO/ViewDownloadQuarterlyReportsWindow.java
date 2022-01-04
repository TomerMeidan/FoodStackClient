package javaFXControllers.CEO;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.sun.org.apache.xml.internal.security.utils.Base64;

import clientSide.CEOPortalView;
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
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class ViewDownloadQuarterlyReportsWindow {

    
    @FXML
    private Button downloadReportsButton;

    @FXML
    private Label messageAfterUploadLabel;

    @FXML
    private AnchorPane anchorPaneID;
    
    @FXML
    private Button selectFileLocationButton;

    @FXML
    private Label titleLabel;
    
    @FXML	
    private Button BackButton;

	private Stage primaryStage;
	private Scene scene;
	private CEOPortalView view;
	private HBox homePageHBox;
	private JSONObject personalInfo;
	private JSONObject files;
	private JSONObject selectedFileFromTable;
	private File selectedDirectory;
	
	/** init<p>
	 * The method saves the main variables regarding the CEO main home page window.
	 * Also initiates the scene of the CEO window.
	 * @param homePageHBox - The main javafx object defined for the window.
	 * @param primaryStage - Primary stage for the CEO window.
	 * @param view - CEO portal client communication controller
	 * @param personalInfo - information regarding the client such as first name, last name and branch
	 * 
	 * */
	public void init(HBox homePageHBox, Stage primaryStage, CEOPortalView view, JSONObject personalInfo) {
		this.homePageHBox = homePageHBox;
		this.primaryStage = primaryStage;
		this.view = view;
		this.personalInfo = personalInfo;
		scene = new Scene(homePageHBox);
		downloadReportsButton.setVisible(false);
	}
	
	/** Show Window<p>
	 * 
	 * This method initiates the FXML main scene for the CEO download quarterly reports window.
	 * After showing the window, the method creates a JSON Object that will send a response
	 * to the server side confirming that the window is now showing for the client.
	 * 
	 * */
	public void showWindow() {
		// log
		Logger.log(Level.INFO, "ViewDownloadQuarterlyReportsWindow: showing window");
		System.out.println("ViewDownloadQuarterlyReportsWindow: showing window");

		Platform.runLater(() -> {
			primaryStage.setScene(scene);
			primaryStage.show();

			JSONObject json = new JSONObject();
			json.put("command", "download quarterly reports window is ready");
			view.ready(json);
		});
	}

	/** Set Files Table<p>
	 * 
	 * This method takes a batch of file rows from the data base and adds them
	 * to a table view for the client to choose a certain file to download to the computer.
	 * 
	 * @param files - This JSON Object is an imported file JSON from the server side. This JSON contains
	 * file rows with branch, quarter, year and an encoded byte array using Base64 function.
	 * 
	 * */
	public void setFilesTable(JSONObject files) {
		this.files = files;
		Platform.runLater(() -> {
			
		TableView tableView = new TableView();
		tableView.setMaxHeight(150);
		tableView.setMaxWidth(200);

		
		ObservableList<Map<String, Object>> fileRows =
			    FXCollections.<Map<String, Object>>observableArrayList();
		
		TableColumn<Map, String> branchColumn = new TableColumn<>("Branch");
		branchColumn.setCellValueFactory(new MapValueFactory<>("Branch"));
		
		TableColumn<Map, String> quarterColumn = new TableColumn<>("Quarter");
		quarterColumn.setCellValueFactory(new MapValueFactory<>("Quarter"));
		
		TableColumn<Map, String> yearColumn = new TableColumn<>("Year");
		yearColumn.setCellValueFactory(new MapValueFactory<>("Year"));
		
		tableView.getColumns().addAll(branchColumn,quarterColumn,yearColumn);
		JSONArray filesArray = (JSONArray) files.get("filesArray");
		
		int numberOfFiles = filesArray.size();
		
		for(int i = 0 ; i < numberOfFiles ; i++) {
			Map<String, Object> tableRow = new HashMap<>();
			JSONObject fileRow = (JSONObject) filesArray.get(i);
			String branch = Message.getValueString(fileRow, "branch");
			String year = Message.getValueString(fileRow, "year");
			String quarter = Message.getValueString(fileRow, "quarter");
			
			fileRow.put("Branch", branch);
			fileRow.put("Quarter", quarter);
			fileRow.put("Year", year);

			fileRows.add(fileRow);
		}
		
		tableView.getItems().addAll(fileRows);
		
		
		HBox simpleHbox = new HBox();
		simpleHbox.setPadding(new Insets(0,0,0,20));
		simpleHbox.getChildren().add(tableView);
		anchorPaneID.getChildren().add(simpleHbox);
		
		tableView.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				
				try {
				selectedFileFromTable = (JSONObject) tableView.getSelectionModel().getSelectedItem();
				}catch(Exception e) {
					Logger.log(Level.INFO, "ViewDownloadQuarterlyReportsWindow: tableView : setOnMouseClicked: Exception was thrown");
					System.out.println("ViewDownloadQuarterlyReportsWindow: tableView : setOnMouseClicked: Exception was thrown");
				}

			}

		});
		
	});
}
	

	/** On Back Button<p>
	 * 
	 * This method is a scene builder trigger defined to return to the previous window, which
	 * is the CEO view quarterly reports. This method contains a destructor to reset certain variables.
	 * 
	 * @param event - action event of clicking the "Back" button.
	 * 
	 * */
    @FXML
    void onBackButton(ActionEvent event) {
    	
		Logger.log(Level.INFO, "ViewDownloadQuarterlyReportsWindow: Back button was pressed, returning to CEO view quarterly reports");
		System.out.println("ViewDownloadQuarterlyReportsWindow: Back button was pressed, returning to CEO view quarterly reports");
		
		//DESTRUCTOR --------------------------------------
		anchorPaneID.getChildren().clear();
		files = null;
		personalInfo = null;
		selectedFileFromTable = new JSONObject();
		selectedDirectory = null;
		downloadReportsButton.setVisible(false);
		messageAfterUploadLabel.setText("");

		//-------------------------------------------------
		view.getViewQuarterlyReportsWindow().showWindow();
    }


    /** On Download Reports Button<p>
     * This is an FX trigger that initiates upon clicking the Download button on the GUI screen.
     * The purpose of this method is to give the user an option to download and store a file on the computer
     * and download to that location that was chosen by the client from a table view object on the screen.
     * 
     * @param event - action event of clicking the "Download Report" button.

     * */
    @FXML
    void onDownloadReportsButton(ActionEvent event) { 	
		try {
			
			if (selectedFileFromTable.isEmpty()) {
				messageAfterUploadLabel.setText("Please select a file from the table first!");
				return;
			}

			// Initialize a pointer
			// in file using OutputStream
			String filePath = selectedDirectory.getAbsolutePath()+ "\\" + Message.getValueString(selectedFileFromTable, "fileName");
			OutputStream os = new FileOutputStream(filePath);
			
			// Starts writing the bytes in it
			String base64String = (String) selectedFileFromTable.get("file");
			byte[] byteArray = Base64.decode(base64String);
			os.write(byteArray);
			
			Logger.log(Level.INFO, "ViewDownloadQuarterlyReportsWindow: onDownloadReportsButton: Successfully downloaded file in size of " + byteArray.length + " bytes");
			System.out.println("ViewDownloadQuarterlyReportsWindow: onDownloadReportsButton: Successfully downloaded file in size of " + byteArray.length + " bytes");

			// Close the file
			os.close();
			
			messageAfterUploadLabel.setText("The file was downloaded successfully!");
		}

		catch (Exception e) {
			Logger.log(Level.INFO, "ViewDownloadQuarterlyReportsWindow: onDownloadReportsButton: Exception was thrown.");
			System.out.println("ViewDownloadQuarterlyReportsWindow: onDownloadReportsButton: Exception was thrown.");
			}

    }


    /** On Download Reports Button<p>
     * This is an FX trigger that initiates upon clicking the Select fiel location button on the GUI screen.
     * The purpose of this method is to give the user an option to choose where to store the file on the computer
     * and download to that location a certain file he can choose from a table view object on the screen.
     * 
     * @param event - action event of clicking the "Download Report" button.

     * */
    @FXML
    void onSelectFileLocationButton(ActionEvent event) {
    	
		Logger.log(Level.INFO, "ViewDownloadQuarterlyReportsWindow: onSelectFileLocationButton: Select file location button was pressed");
		System.out.println("ViewDownloadQuarterlyReportsWindow: onSelectFileLocationButton:  Select file location button was pressed");
		
		// Choosing the directory to save the file on the computer
		DirectoryChooser directoryChooser = new DirectoryChooser();
		selectedDirectory = directoryChooser.showDialog(primaryStage);
		
		if(selectedDirectory == null){
			Logger.log(Level.INFO, "ViewDownloadQuarterlyReportsWindow: onSelectFileLocationButton: No directory was selected");
			System.out.println("ViewDownloadQuarterlyReportsWindow: onSelectFileLocationButton: No directory was selected");
			downloadReportsButton.setVisible(false);
			}
		else{
			Logger.log(Level.INFO, "ViewDownloadQuarterlyReportsWindow: onSelectFileLocationButton: Directory was selected " + selectedDirectory.getAbsolutePath());
			System.out.println("ViewDownloadQuarterlyReportsWindow: onSelectFileLocationButton: Directory was selected " + selectedDirectory.getAbsolutePath());
			downloadReportsButton.setVisible(true);

		}
		
    }

}
