package clientSide;

import org.json.simple.JSONObject;

public interface PortalViewInterface {
	public void handleMsg(JSONObject descriptor);

	public void init(JSONObject json);

	public void ready(JSONObject json);

	public ComController getComController();
}
