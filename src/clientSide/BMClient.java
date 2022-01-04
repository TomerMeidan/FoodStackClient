package clientSide;

import common.Logger;
import common.Logger.Level;
import javafx.application.Application;
import javafx.stage.Stage;

public class BMClient extends Application {

	private static String DEFAULT_IP;
	private final static int DEFAULT_PORT = 5555;
	private PortalViewFactory factory;
	private static ComController com;

	public static void main(String[] args) {

		Logger.init();
		Logger.setLevel(Level.DEBUG);

		// log
		System.out.println("BMClient: Logger initialized");

		// Launch() is blocking
	//	DEFAULT_IP = args[0];
		launch();
		
		// log
		Logger.log(Level.DEBUG, "BMClient: javaFX window application stoped");
		System.out.println("BMClient: javaFX window application stoped");

		com.stop();
		
		System.exit(0);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		com = new ComController(DEFAULT_IP, DEFAULT_PORT);
		factory = new PortalViewFactory(primaryStage, com);
		com.setPortalFactory(factory);
		// log
		Logger.log(Level.DEBUG, "BMClient: ComController initialized");
		System.out.println("BMClient: ComController initialized");

		com.start();
	}

}
