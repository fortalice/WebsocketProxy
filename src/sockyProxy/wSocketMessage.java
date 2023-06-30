package sockyProxy;
import java.time.LocalDateTime;
import burp.api.montoya.websocket.TextMessage;

public class wSocketMessage {
	private TextMessage _txtMsg;
	private	LocalDateTime _timestamp;
	
	public TextMessage getMessage() {
		return _txtMsg;
	}
	
	public void setMessage(TextMessage message) {
		this._txtMsg = message;
	}
	
	public LocalDateTime getTimestamp() {
		return _timestamp;
	}
	
	public void setTimestamp() {
		LocalDateTime ts = LocalDateTime.now();
		this._timestamp = ts;
	}
}