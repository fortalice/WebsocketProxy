package sockyProxy;

import javax.swing.JPopupMenu;

import burp.api.montoya.http.Http;
import burp.api.montoya.http.handler.*;
import burp.api.montoya.http.HttpService;
import burp.api.montoya.http.message.requests.*;
import burp.api.montoya.http.message.requests.HttpRequest.*;
import burp.api.montoya.scanner.*;
import burp.api.montoya.core.ByteArray;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SpringLayout;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.table.AbstractTableModel;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.core.ByteArray;

import javax.swing.JMenuBar;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JButton;
import sockyProxy.Data.*;
import sockyProxy.GUI.sockyTableModel;
import sockyProxy.Utils.*;

import javax.swing.ImageIcon;

public class GUI extends JPanel implements ActionListener, AncestorListener {
	private static final long serialVersionUID = 1L;
	public JTextField tbPort;
	public JTextField tbWebsocketURL;
	public MontoyaApi api;
	public sockyTableModel stm;
	/**
	 * Create the panel.
	 */
	public sockyTableModel getTableModel() {
		return stm;
	}
	
	public GUI(MontoyaApi api) {
		this.api = api;
		stm = new sockyTableModel();
		setLayout(new BorderLayout(0, 0));
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		add(tabbedPane, BorderLayout.CENTER);
		
		JPanel pnlInventory = new JPanel();
		tabbedPane.addTab("Inventory", null, pnlInventory, null);
		pnlInventory.setLayout(new BorderLayout(0, 0));
		
		JPanel pnlProxyServer = new JPanel();
		tabbedPane.addTab("Proxy Server", null, pnlProxyServer, null);
		pnlProxyServer.setLayout(null);
		
		tbPort = new JTextField();
		tbPort.setText("9060");
		tbPort.setColumns(10);
		tbPort.setBounds(143, 6, 67, 26);
		pnlProxyServer.add(tbPort);
		
		tbWebsocketURL = new JTextField();
		tbWebsocketURL.setText("ws://localhost");
		tbWebsocketURL.setColumns(10);
		tbWebsocketURL.setBounds(143, 34, 230, 26);
		pnlProxyServer.add(tbWebsocketURL);
		
		JLabel lblNewLabel_1 = new JLabel("Websocket URL");
		lblNewLabel_1.setBounds(16, 39, 134, 16);
		pnlProxyServer.add(lblNewLabel_1);
		
		JLabel lblNewLabel = new JLabel("Proxy Server Port");
		lblNewLabel.setBounds(6, 11, 144, 16);
		pnlProxyServer.add(lblNewLabel);
		
		JButton btnStartProxy = new JButton("Start Websocket Proxy");
		btnStartProxy.setIcon(new ImageIcon("/Users/pac/Downloads/rocket.png"));
		btnStartProxy.setBounds(64, 85, 268, 29);
		pnlProxyServer.add(btnStartProxy);
		
		JButton btnStopProxy = new JButton("Stop Websocket Proxy");
		btnStopProxy.setIcon(new ImageIcon("/Users/pac/Downloads/stop.png"));
		btnStopProxy.setBounds(64, 128, 268, 29);
		pnlProxyServer.add(btnStopProxy);
		btnStartProxy.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		Utils.LaunchProxy(api, tbPort.getText(), tbWebsocketURL.getText());
        		btnStopProxy.setEnabled(true);
        		btnStartProxy.setEnabled(false);
        	}
        });
        btnStopProxy.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		
        		if (Utils.ProcessLauncher.process != null) {
        			api.logging().logToOutput("Websocket Proxy Server Stopped");
        			Utils.ProcessLauncher.process.destroy();
        			btnStopProxy.setEnabled(false);
        			btnStartProxy.setEnabled(true);
        			
        		}
        	}
        });
		
		JTable table = new JTable(stm)
        {
            @Override
            public void changeSelection(int rowIndex, int columnIndex, boolean toggle, boolean extend)
            {
                // show the log entry for the selected row
            	wSocketMessage responseReceived = stm.get(rowIndex);
                super.changeSelection(rowIndex, columnIndex, toggle, extend);
            }
        };
        
        JPopupMenu ctxMenu = new JPopupMenu("Edit");
        JMenuItem item = new JMenuItem("Send to Intruder");
	    item.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		api.logging().logToOutput("Menu Click: " + e.getActionCommand() + "\n");
        		String val = (String) table.getModel().getValueAt(table.getSelectedRow(), 0);
        		String req = "POST / HTTP/1.1\nHost: 127.0.0.1:"+tbPort.getText()+"\nAccept: application/json\nUser-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.5672.127 Safari/537.36\nContent-Length: 0\n\n"+val+"\n\n";  
        		HttpService service = HttpService.httpService("127.0.0.1", Integer.parseInt(tbPort.getText()), false);
        		HttpRequest httpreq = HttpRequest.httpRequest(service,  req.toString());
        		api.intruder().sendToIntruder(httpreq);
        	}
        });
	    JMenuItem itmScan = new JMenuItem("Send to Scanner");
	    itmScan.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		api.logging().logToOutput("Menu Click: " + e.getActionCommand() + "\n");
        		//AuditConfiguration audit = new AuditConfiguration();
        		//api.scanner().startAudit();
        	}
        });
	    JMenuItem itmDecode = new JMenuItem("Send to Decoder");
	    itmDecode.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		api.logging().logToOutput("Menu Click: " + e.getActionCommand() + "\n");
        		String val = (String) table.getModel().getValueAt(table.getSelectedRow(), 0);
        		api.logging().logToOutput(val);
        		burp.api.montoya.core.ByteArray ba = null;
        		ba.setBytes(0, val.getBytes());
        		api.decoder().sendToDecoder(ba);
        	}
        });
        ctxMenu.add(item);
        ctxMenu.add(itmScan);
        ctxMenu.add(itmDecode);
        table.setInheritsPopupMenu(true);
        table.setComponentPopupMenu(ctxMenu);
		table.setBounds(25, 25, 472, 354);
		table.setFont(new Font("Cascadia Code", Font.PLAIN, 12));		
        pnlInventory.add(table);

	}
	
	 public class SendRequest implements Runnable {
		    private HttpRequest request;
		    public SendRequest(HttpRequest req) {
		        this.request = req;
		    }

		    public void run() {
		    	api.http().sendRequest(this.request);
		    	api.logging().logToOutput("Request sent: " + this.request.url());
		    }

		}
	
	public class sockyTableModel extends AbstractTableModel {
		 private static final long serialVersionUID = 1L;
		private final List<wSocketMessage> log;
		 private final List<String> payloads;
		    public sockyTableModel()
		    {
		        this.log = new ArrayList<>();
		        this.payloads = new ArrayList<>();
		    }

		    @Override
		    public synchronized int getRowCount()
		    {
		        return log.size();
		    }

		    @Override
		    public int getColumnCount()
		    {
		        return 2;
		    }

		    @Override
		    public String getColumnName(int column)
		    {
		        return switch (column)
		                {
		                    case 0 -> "Websocket Payload";
		                    case 1 -> "Timestamp";
		                    default -> "";
		                };
		    }

		    @Override
		    public synchronized Object getValueAt(int rowIndex, int columnIndex)
		    {
		        wSocketMessage response = log.get(rowIndex);
		        return switch (columnIndex)
		                {
		                    case 0 -> response.getMessage().payload();
		                    case 1 -> response.getTimestamp();
		                    default -> "";
		                };
		    }

		    public synchronized void add(wSocketMessage responseReceived)
		    {
		        int index = log.size();
		        if(payloads.indexOf(responseReceived.getMessage().payload()) < 0) {
		        	log.add(responseReceived);
		        	payloads.add(responseReceived.getMessage().payload());
		        	fireTableRowsInserted(index, index);	        	
		        }
		    }

		    public synchronized wSocketMessage get(int rowIndex)
		    {
		        return log.get(rowIndex);
		    }
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		api.logging().logToOutput("Menu Click: " + e.getActionCommand() + "\n");
	}
	
	private JMenuItem makeMenuItem(String label) {
	    JMenuItem item = new JMenuItem(label);
	    item.addActionListener(this);
	    return item;
	  }

	@Override
	public void ancestorAdded(AncestorEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ancestorRemoved(AncestorEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ancestorMoved(AncestorEvent event) {
		// TODO Auto-generated method stub
		
	}
}
