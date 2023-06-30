package sockyProxy;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.logging.Logging;
import burp.api.montoya.websocket.WebSocketCreated;
import burp.api.montoya.websocket.WebSocketCreatedHandler;
import sockyProxy.GUI.sockyTableModel;
class wsCreateHandler implements WebSocketCreatedHandler{
	private final MontoyaApi api;
	private final sockyTableModel tbModel;
    public wsCreateHandler(MontoyaApi api, sockyTableModel tbModel) {
        this.api = api;
        this.tbModel = tbModel;
    }
    
	    @Override
	    public void handleWebSocketCreated(WebSocketCreated webSocketCreated) {
	        // write a message to our output stream
	        api.logging().logToOutput(webSocketCreated.upgradeRequest().headers().toString());	    	
	        webSocketCreated.webSocket().registerMessageHandler(new wsMessageHandler(api,tbModel));
	    }
}
