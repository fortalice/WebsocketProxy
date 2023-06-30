package sockyProxy;
import java.awt.event.InputEvent;
import java.util.List;
import java.util.Optional;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.core.Registration;
import burp.api.montoya.core.ToolType;
import burp.api.montoya.http.HttpService;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.intruder.HttpRequestTemplate;
import burp.api.montoya.intruder.Intruder;
import burp.api.montoya.intruder.PayloadGeneratorProvider;
import burp.api.montoya.intruder.PayloadProcessor;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.ContextMenuItemsProvider;
import burp.api.montoya.BurpExtension;
import burp.api.montoya.extension.ExtensionUnloadingHandler;
import burp.api.montoya.logging.Logging;
import burp.api.montoya.ui.contextmenu.WebSocketContextMenuEvent;
import burp.api.montoya.ui.contextmenu.WebSocketEditorEvent;
import burp.api.montoya.ui.contextmenu.WebSocketMessage;
import sockyProxy.GUI.sockyTableModel;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

public class sockyProxy implements BurpExtension {
	public MontoyaApi api;
	@Override
	public void initialize(MontoyaApi mapi) {
		// TODO Auto-generated method stub
		this.api = mapi;
        // set extension name
        api.extension().setName("sockyProxy");
        // write a message to our output stream
        api.logging().logToOutput("sockyProxy loaded.");
        api.logging().logToOutput("Is Python Installed: " + String.valueOf(Utils.isPythonInstalled()));
        if (!Utils.isPythonInstalled()) {
        	api.logging().logToOutput("You must install Python3 on your host system (with aiohttp and websockets library installed) and ensure python3 is in your PATH environment variable");
        	return;
        }
        //Register web socket handler with Burp.
        GUI UI;
        api.userInterface().registerSuiteTab("sockyProxy", UI = new GUI(api));
        wsCreateHandler exampleWebSocketCreationHandler = new wsCreateHandler(api,UI.stm);
        api.websockets().registerWebSocketCreatedHandler(exampleWebSocketCreationHandler);
        extUnload unload = new extUnload(api);
        api.extension().registerUnloadingHandler(unload);
        api.userInterface().registerContextMenuItemsProvider(new MyContextMenuItemsProvider(api));
        //Utils.LaunchProxy(api, UI.tbPort.getText(), UI.tbWebsocketURL.getText());
	}
	

	public class MyContextMenuItemsProvider implements ContextMenuItemsProvider
	{

	    private final MontoyaApi api;

	    public MyContextMenuItemsProvider(MontoyaApi api)
	    {

	        this.api = api;
	    }

	    @Override
	    public List<Component> provideMenuItems(ContextMenuEvent event)
	    {
	        if (event.isFromTool(ToolType.PROXY, ToolType.TARGET, ToolType.REPEATER))
	        {
	            List<Component> menuItemList = new ArrayList<>();

	            JMenuItem retrieveRequestItem = new JMenuItem("Print request");
	            JMenuItem retrieveResponseItem = new JMenuItem("Print response");

	            HttpRequestResponse requestResponse = event.messageEditorRequestResponse().isPresent() ? event.messageEditorRequestResponse().get().requestResponse() : event.selectedRequestResponses().get(0);

	            retrieveRequestItem.addActionListener(l -> api.logging().logToOutput("Request is:\r\n" + requestResponse.request().toString()));
	            menuItemList.add(retrieveRequestItem);

	            if (requestResponse.response() != null)
	            {
	                retrieveResponseItem.addActionListener(l -> api.logging().logToOutput("Response is:\r\n" + requestResponse.response().toString()));
	                menuItemList.add(retrieveResponseItem);
	            }

	            return menuItemList;
	        }

	        return null;
	    }
	}
	class wsContextMenu implements WebSocketContextMenuEvent {

		@Override
		public InputEvent inputEvent() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public ToolType toolType() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean isFromTool(ToolType... toolType) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Optional<WebSocketEditorEvent> messageEditorWebSocket() {
			// TODO Auto-generated method stub
			return Optional.empty();
		}

		@Override
		public List<WebSocketMessage> selectedWebSocketMessages() {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	class extUnload implements ExtensionUnloadingHandler {
		private MontoyaApi api;
		
		extUnload(MontoyaApi mapi) {
			this.api = mapi;
		}
		
		@Override
		public void extensionUnloaded() {
			if (Utils.ProcessLauncher.process != null) {
				this.api.logging().logToOutput("Websocket Proxy Server Stopped");
				Utils.ProcessLauncher.process.destroy();				
			}
		}
		
	}

}
