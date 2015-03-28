/* Copyright (C) 2013 Interactive Brokers LLC. All rights reserved.  This code is subject to the terms
 * and conditions of the IB API Non-Commercial License or the IB API Commercial License, as applicable. */

package IBDownloadHistoricalData;

import com.ib.client.AnyWrapperMsgGenerator;
import com.ib.client.CommissionReport;
import com.ib.client.Contract;
import com.ib.client.ContractDetails;
import com.ib.client.EClientSocket;
import com.ib.client.EWrapper;
import com.ib.client.EWrapperMsgGenerator;
import com.ib.client.Execution;
import com.ib.client.Order;
import com.ib.client.OrderState;
import com.ib.client.UnderComp;
import com.ib.client.Util;
import com.ib.client.ComboLeg;
import java.util.Vector;
import java.io.*;

class IBDownloadHistoricalData  implements EWrapper {

    private EClientSocket   m_client = new EClientSocket( this);
    private FileLog     m_tickers; //= new FileLog("ServerData.txt");
    private FileLog     m_TWS = new FileLog("ServerResponses.txt");
    private FileLog     m_errors = new FileLog("ServerErrors.txt");
    
	private Contract cont =  new Contract(0, "USD", "CASH", "",
                    0, "", "",
                    "IDEALPRO", "CAD", "", "",
                    new Vector<ComboLeg>(), "IDEALPRO", false,
                    "", "");

	private String LastDateTime = "20150327 00:00:00";
					
	// This method is called to start the application
    public static void main (String args[]) {
		System.out.println("Starting IBDownloadHistoricalData");
		IBDownloadHistoricalData downloader = new IBDownloadHistoricalData();
		downloader.run();
    }

	void run() {
		connect();
		requestHistoricalData();
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			br.readLine();
		} catch (Exception e) {
			System.err.println(e);
		} finally {
			disconnect();
			m_tickers.close();
			m_TWS.close();
			m_errors.close();
		}
		
	}
	
	void connect() {
		//connect localhost port 7496
		m_client.eConnect("", 7496, 0);
        if (m_client.isConnected()) {
            m_TWS.add("Connected to Tws server version " +
                       m_client.serverVersion() + " at " +
                       m_client.TwsConnectionTime());
        }
    }

    void disconnect() {
        m_client.eDisconnect();
    }

    void requestHistoricalData() {
		//open path, find newest file, read first line, take the last and request, write new file
		//how to find newest file
		//how to read first line
		
		//Duration String "1 D", "14400 S"
		m_client.reqHistoricalData( 0, cont,
									"20141225 12:00:00", "14400 S",
                                    "1 min", "BID",
                                    1, 1);
									
    }

    public void tickPrice( int tickerId, int field, double price, int canAutoExecute) {
    }

    public void tickOptionComputation( int tickerId, int field, double impliedVol, double delta, double optPrice, double pvDividend,
        double gamma, double vega, double theta, double undPrice) {
    }

    public void tickSize( int tickerId, int field, int size) {
    }

    public void tickGeneric( int tickerId, int tickType, double value) {
    }

    public void tickString( int tickerId, int tickType, String value) {
    }

    public void tickSnapshotEnd(int tickerId) {
    }

    public void tickEFP(int tickerId, int tickType, double basisPoints, String formattedBasisPoints,
    					double impliedFuture, int holdDays, String futureExpiry, double dividendImpact,
    					double dividendsToExpiry) {
    }

    public void orderStatus( int orderId, String status, int filled, int remaining,
    						 double avgFillPrice, int permId, int parentId,
    						 double lastFillPrice, int clientId, String whyHeld) {
    }

    public void openOrder( int orderId, Contract contract, Order order, OrderState orderState) {
    }

    public void openOrderEnd() {
    }

    public void contractDetails(int reqId, ContractDetails contractDetails) {
    }

	public void contractDetailsEnd(int reqId) {
	}

    public void scannerData(int reqId, int rank, ContractDetails contractDetails,
                            String distance, String benchmark, String projection, String legsStr) {
    }

    public void scannerDataEnd(int reqId) {
    }

    public void bondContractDetails(int reqId, ContractDetails contractDetails)
    {   	
    }

    public void execDetails(int reqId, Contract contract, Execution execution)
    {
    }

    public void execDetailsEnd(int reqId)
    {
    }

    public void updateMktDepth( int tickerId, int position, int operation,
                    int side, double price, int size) {
    }

    public void updateMktDepthL2( int tickerId, int position, String marketMaker,
                    int operation, int side, double price, int size) {
    }

    public void nextValidId( int orderId) {
        // received next valid order id
    	String msg = EWrapperMsgGenerator.nextValidId( orderId);
        m_TWS.add(msg) ;
    }

    public void error(Exception ex) {
    }

    public void error( String str) {
    	String msg = AnyWrapperMsgGenerator.error(str);
        m_errors.add( msg);
    }

    public void error( int id, int errorCode, String errorMsg) {
    	String msg = AnyWrapperMsgGenerator.error(id, errorCode, errorMsg);
        m_errors.add( msg);
    }

    public void connectionClosed() {
        String msg = AnyWrapperMsgGenerator.connectionClosed();
    }

    public void updateAccountValue(String key, String value,
                                   String currency, String accountName) {
    }

    public void updatePortfolio(Contract contract, int position, double marketPrice,
        double marketValue, double averageCost, double unrealizedPNL, double realizedPNL,
        String accountName) {
    }

    public void updateAccountTime(String timeStamp) {
    }

    public void accountDownloadEnd(String accountName) {
    }

    public void updateNewsBulletin( int msgId, int msgType, String message, String origExchange) {
    }

    public void managedAccounts( String accountsList) {
    }

    public void historicalData(int reqId, String date, double open, double high, double low,
                               double close, int volume, int count, double WAP, boolean hasGaps) {
        String msg = EWrapperMsgGenerator.historicalData(reqId, date, open, high, low, close, volume, count, WAP, hasGaps);
    	m_tickers.add( msg );
    }
	
	public void realtimeBar(int reqId, long time, double open, double high, double low, double close, long volume, double wap, int count) {
	}
	
    public void scannerParameters(String xml) {
    }

	public void currentTime(long time) {
	}
	
	public void fundamentalData(int reqId, String data) {
	}
	
	public void deltaNeutralValidation(int reqId, UnderComp underComp) {
	}

    public void receiveFA(int faDataType, String xml) {
    }

    public void marketDataType(int reqId, int marketDataType) {
    }

    public void commissionReport(CommissionReport commissionReport) {
    }

    public void position(String account, Contract contract, int pos, double avgCost) {
    }

    public void positionEnd() {
    }

    public void accountSummary( int reqId, String account, String tag, String value, String currency) {
    }

    public void accountSummaryEnd( int reqId) {
    }
	
	class FileLog {
		PrintWriter writer = null;
	
		public FileLog(String filePath) {
			try {
				writer = new PrintWriter(new BufferedWriter(new FileWriter(filePath, true)));
			}catch (Exception e) {
				System.err.println(e);
			}
		}
		
		public void add(String msg) {
			writer.write(msg + "\n");
		}
		
		public void addText(String msg) {
			add(msg);
		}
		
		public void clear() {
		}
		
		public void close() {
			writer.flush();
			writer.close();
		}
	}
}
