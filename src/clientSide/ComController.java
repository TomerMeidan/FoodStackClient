package clientSide;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import org.json.simple.JSONObject;

import ocsf.client.AbstractClient;
import common.*;
import common.Logger.Level;
import common.Logger;

import static common.Message.*;

public class ComController extends AbstractClient {

	protected PortalViewInterface view;
	private String ip;
	private int port;
	private PortalViewFactory factory;
	private Timer timer;

	/**
	 * Communication Controller
	 * <p>
	 * This method sets the current IP and port address of the server side
	 * implemented with EchoServer, contains the main method for the server program
	 * and performs the work of listening to the port, establishing connections, and
	 * reading from and writing to the socket.
	 */
	public ComController(String ip, int port) {
		super(ip, port);
		this.ip = ip;
		this.port = port;
	}

	/**
	 * Communication Controller
	 * <p>
	 * The server side is using a certain factory method to establish which
	 * communication control each user is using at the time they are connected via
	 * TCP\IP link between the client and the server side.
	 * 
	 * @param factory - If the associated connection factory is configured for
	 *                single- use connections, a new connection is immediately
	 *                created for each new request. Otherwise, if the connection is
	 *                in use, the calling thread blocks on the connection until
	 *                either a response is received or a timeout or I/O error
	 *                occurs.
	 */
	public void setPortalFactory(PortalViewFactory factory) {
		this.factory = factory;
	}

	/**
	 * Start
	 * <p>
	 * The method upon starting the server will create and initiate the starting
	 * portal view window as the log in portal screen, the method will then attempt
	 * to connect to a certain server on the same IP\PORT using the tryToConnect
	 * method.
	 */
	public void start() {
		view = factory.createPortalView("login");
		view.init(null);

		// log
		Logger.log(Level.DEBUG, "ComController: PortalView initialized");
		System.out.println("ComController: PortalView initialized");

		tryToConnect();
	}

	/**
	 * Stop
	 * <p>
	 * The method will initiate the close connection method defined by the abstract
	 * client to close the connection with the server from the client side, if the
	 * closing connection isn't successful then an IO exception will be thrown
	 * instead.
	 * 
	 */
	public void stop() {
		try {
			closeConnection();
		} catch (IOException e) {
			// log
			Logger.log(Level.WARNING, "ComController: IOException in stop()");
			System.out.println("ComController: IOException in stop()");
		}
	}

	/** Try To Connect<p>
	 * This method is sending a connection request to a certain port\ip, every time period using a Date
	 * object, the method will try and connect again and again each 5 seconds (5000 miliseconds as defined) with the server
	 * side.
	 * 
	 * */
	private void tryToConnect() {

		try {
			openConnection();
		} catch (IOException e) {
			System.out.println(e.toString());
			Logger.log(Level.WARNING, e.toString());

			timer = new Timer();
			TimerTask task = new TimerTask() {

				public void run() {
					// log
					Logger.log(Level.DEBUG, "trying to connect...");
					System.out.println("trying to connect...");

					tryToConnect();
				};
			};

			timer.schedule(task, 5000);

		}
	}

	/**
	 * Connection Established
	 * <p>
	 * 
	 * This method is a built in overridable method extended from AbstractClient.
	 * The purpose of this method is once a connection between the server and the
	 * client has been made, it will send a message to the Log In portal view
	 * notifying the status of the connection between a client and the server.
	 */
	@Override
	public void connectionEstablished() {
		// log
		Logger.log(Level.DEBUG, "ComController: connection established");
		System.out.println("ComController: connection established");

		JSONObject json = new JSONObject();
		json.put("command", "online");

		view.handleMsg(json);

		if (timer != null) {
			timer.cancel();
			Logger.log(Level.DEBUG, "ComMngr: timer closed");
			System.out.println("ComMngr: timer closed");
		}
	}

	/**
	 * Handle Message From Server
	 * <p>
	 * 
	 * This method is a built in overridable method extended from AbstractClient.
	 * The purpose of this method is to receive and handle all the messages that are
	 * sent by the server through an Object message. This message will usually have
	 * a JSON including a command status (such as: ""update) indicating on which
	 * task there is to perform.
	 * 
	 * @param msg - Message type send by the server to the client holding a certain
	 *            task to perform or an update message.
	 */
	@Override
	public void handleMessageFromServer(Object msg) {

		if (msg == null) {
			// log
			Logger.log(Level.WARNING, "ComController: Received null from server");
			System.out.println("ComController: Received null from server");
			return;
		}

		// log
		Logger.log(Level.DEBUG, "ComController: Received message from server");
		System.out.println("ComController: Received message from server");

		JSONObject json = Parser.decode(msg);

		// log
		Logger.log(Level.DEBUG, "ComController: message : " + json.toString());
		System.out.println("ComController: message : " + json.toString());

		// TODO create Message class to handle all the converts
		switch (getValue(json, "command")) {
		case "handshake":
			// log
			Logger.log(Level.INFO, "ComController: messageType: handshake");
			System.out.println("ComController: messageType: handshake");

			if (json.get("status").equals("notOk")) {
				view.handleMsg(json);
			} else {
				view = factory.createPortalView((String) json.get("portalType"));
				view.init(json);
			}
			break;
		case "update":
			view.handleMsg(json);
			break; // add
		default:
			// log
			Logger.log(Level.INFO, "ComController: messageType: undefined");
			System.out.println("ComController: messageType: undefined");
			break;
		}
	}

	/**
	 * Handle Message From Server
	 * <p>
	 * 
	 * This method's purpose is to take a certain message from the client side and
	 * send it to the client's portal view on the server side. Usually the messages
	 * consist of update status regarding a state in a certain FX window, or an
	 * update message or a permission to move from one window to another.
	 * 
	 * @param msg - Message type send by the client to the server holding a certain
	 *            task to perform or an update message.
	 */
	public void handleUserAction(JSONObject msg) {
		try {
			sendToServer(Parser.encode(msg));
		} catch (IOException e) {
			String warning = createMessage(msg, "ComController: IOException in handleUserAction: ", "command");
			Logger.log(Level.WARNING, warning);
			System.out.println(warning);
		}
	}

	/**
	 * Connection Closed
	 * <p>
	 * 
	 * This method's purpose is to take a certain message from the client side and
	 * send it to the client's portal view on the server side. Usually the messages
	 * consist of update status regarding a state in a certain FX window, or an
	 * update message or a permission to move from one window to another.
	 * 
	 * @param msg - Message type send by the client to the server holding a certain
	 *            task to perform or an update message.
	 */
	@Override
	protected void connectionClosed() {
		// log
		Logger.log(Level.DEBUG, "ComController: connection closed");
		System.out.println("ComController: connection closed");

		closeAll();

	}

	@Override
	protected void connectionException(Exception exception) {
		// log
		Logger.log(Level.DEBUG, "ComController: connection exception");
		System.out.println("ComController: connection exception");
		System.out.println(exception);
		/*
		 * if connection falls - back to login window and try to reconnect. a delay is
		 * needed because of the sequence in AbstractClient.run() connectionException()
		 * is called before clientReader = null;
		 */
		timer = new Timer();
		TimerTask task = new TimerTask() {

			public void run() {
				// log
				Logger.log(Level.DEBUG, "trying to connect...");
				System.out.println("trying to connect...");

				start();
			};
		};

		timer.schedule(task, 1000);
	}

	public void closeAll() {

		if (timer != null) {
			timer.cancel();
			Logger.log(Level.DEBUG, "ComMngr: timer closed");
			System.out.println("ComMngr: timer closed");
		}
	}

}
