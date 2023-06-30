package sockyProxy;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.logging.Logging;
import burp.api.montoya.proxy.ProxyWebSocketHistoryFilter;
import burp.api.montoya.websocket.*;
import sockyProxy.GUI.sockyTableModel;
import burp.api.montoya.http.*;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.*;
import java.util.List;
import static burp.api.montoya.websocket.Direction.CLIENT_TO_SERVER;

public class wsMessageHandler implements MessageHandler {
	private final MontoyaApi api;
	private final sockyTableModel tbModel;
    public wsMessageHandler(MontoyaApi api, sockyTableModel tbModel) {
        this.api = api;
        this.tbModel = tbModel;
    }
    
    @Override
    public TextMessageAction handleTextMessage(TextMessage textMessage) {
        Logging logging = api.logging();
        //if (textMessage.direction() == CLIENT_TO_SERVER && textMessage.payload().contains("password")) {
         //   String base64EncodedPayload = api.utilities().base64Utils().encodeToString(textMessage.payload());

        //    return TextMessageAction.continueWith(base64EncodedPayload);
        //}
        if (textMessage.direction() == Direction.CLIENT_TO_SERVER) {
	        logging.logToOutput(textMessage.payload());
	        wSocketMessage wsm = new wSocketMessage();
	        wsm.setMessage(textMessage);
	        wsm.setTimestamp();
	        tbModel.add(wsm);
        }

        return TextMessageAction.continueWith(textMessage);
    }

    @Override
    public BinaryMessageAction handleBinaryMessage(BinaryMessage binaryMessage) {
        return BinaryMessageAction.continueWith(binaryMessage);
    }
}
