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

	public ComController(String ip, int port) {
		super(ip, port);
		this.ip = ip;
		this.port = port;
	}

	public void setPortalFactory(PortalViewFactory factory) {
		this.factory = factory;
	}

	public void start() {
		view = factory.createPortalView("login");
		view.init(null);

		// log
		Logger.log(Level.DEBUG, "ComController: PortalView initialized");
		System.out.println("ComController: PortalView initialized");

		tryToConnect();
	}

	public void stop() {
		try {
			closeConnection();
		} catch (IOException e) {
			// log
			Logger.log(Level.WARNING, "ComController: IOException in stop()");
			System.out.println("ComController: IOException in stop()");
		}
	}

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

	public void handleUserAction(JSONObject msg) {
		try {
			sendToServer(Parser.encode(msg));
		} catch (IOException e) {
			String warning = createMessage(msg, "ComController: IOException in handleUserAction: ", "command");
			Logger.log(Level.WARNING, warning);
			System.out.println(warning);
		}
	}

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
