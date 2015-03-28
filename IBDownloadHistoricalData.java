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
    private static final int NOT_AN_FA_ACCOUNT_ERROR = 321 ;
    private int faErrorCodes[] = { 503, 504, 505, 522, 1100, NOT_AN_FA_ACCOUNT_ERROR } ;
    private boolean faError ;

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
		onConnect();
		onHistoricalData();
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			br.readLine();
		} catch (Exception e) {
			System.err.println(e);
		} finally {
			onDisconnect();
			m_tickers.close();
			m_TWS.close();
			m_errors.close();
		}
		
	}
	
	void onConnect() {
		m_client.eConnect("", 7496, 0);
        if (m_client.isConnected()) {
            m_TWS.add("Connected to Tws server version " +
                       m_client.serverVersion() + " at " +
                       m_client.TwsConnectionTime());
        }
    }

    void onDisconnect() {
        m_client.eDisconnect();
    }

    void onCancelRealTimeBars() {
		/*
        // cancel market data
        m_client.cancelRealTimeBars( m_orderDlg.m_id );
		*/
    }

    //void onScanner() {
		/*
        m_scannerDlg.show();
        if (m_scannerDlg.m_userSelection == ScannerDlg.CANCEL_SELECTION) {
            m_client.cancelScannerSubscription(m_scannerDlg.m_id);
        }
        else if (m_scannerDlg.m_userSelection == ScannerDlg.SUBSCRIBE_SELECTION) {
            m_client.reqScannerSubscription(m_scannerDlg.m_id,
                                            m_scannerDlg.m_subscription);
        }
        else if (m_scannerDlg.m_userSelection == ScannerDlg.REQUEST_PARAMETERS_SELECTION) {
            m_client.reqScannerParameters();
        }
		*/
    //}

    void onReqCurrentTime() {
    	m_client.reqCurrentTime();
	}

    void onHistoricalData() {
		//open path, find newest file, read first line, take the last and request, write new file
		//how to find newest file
		//how to read first line
		
		//Duration String "1 D", "14400 S"
		m_client.reqHistoricalData( 0, cont,
									"20141225 12:00:00", "14400 S",
                                    "1 min", "BID",
                                    1, 1);
									
    }

    void onCancelHistoricalData() {
	/*
        // cancel historical data
        m_client.cancelHistoricalData( m_orderDlg.m_id );
	*/
    }

    void onReqContractData() {
	/*
        // req mkt data
        m_client.reqContractDetails( m_orderDlg.m_id, m_orderDlg.m_contract );
	*/
    }

    void onReqMktDepth() {
	/*
        final Integer dialogId = m_orderDlg.m_id;
		
        MktDepthDlg depthDialog = m_mapRequestToMktDepthDlg.get(dialogId);
        if ( depthDialog == null ) {
            depthDialog = new MktDepthDlg("Market Depth ID ["+dialogId+"]", this);
            m_mapRequestToMktDepthDlg.put(dialogId, depthDialog);

            // cleanup the map after depth dialog is closed so it does not linger or leak memory
            depthDialog.addWindowListener(new WindowAdapter() {
            	@Override public void windowClosed(WindowEvent e) {
            		m_mapRequestToMktDepthDlg.remove(dialogId);
            	}
			});
        }

        depthDialog.setParams( m_client, dialogId);

        // req mkt data
        m_client.reqMktDepth( dialogId, m_orderDlg.m_contract, m_orderDlg.m_marketDepthRows );
        depthDialog.setVisible(true);
	*/
    }

    void onCancelMktData() {
	/*
        // cancel market data
        m_client.cancelMktData( m_orderDlg.m_id );
	*/
    }

    void onCancelMktDepth() {
	/*
       // cancel market data
        m_client.cancelMktDepth( m_orderDlg.m_id );
	*/
    }

    void onReqOpenOrders() {
        m_client.reqOpenOrders();
    }

    void onWhatIfOrder() {
    	placeOrder(true);
    }

    void onPlaceOrder() {
    	placeOrder(false);
    }

    void placeOrder(boolean whatIf) {
	/*
        Order order = m_orderDlg.m_order;

        // save old and set new value of whatIf attribute
        boolean savedWhatIf = order.m_whatIf;
        order.m_whatIf = whatIf;

        // place order
        m_client.placeOrder( m_orderDlg.m_id, m_orderDlg.m_contract, order );

        // restore whatIf attribute
        order.m_whatIf = savedWhatIf;
	*/
    }

    void onExerciseOptions() {
	/*
        // cancel order
        m_client.exerciseOptions( m_orderDlg.m_id, m_orderDlg.m_contract,
                                  m_orderDlg.m_exerciseAction, m_orderDlg.m_exerciseQuantity,
                                  m_orderDlg.m_order.m_account, m_orderDlg.m_override);
	*/							 
    }

    void onCancelOrder() {
	/*
       // cancel order
        m_client.cancelOrder( m_orderDlg.m_id );
	*/
    }

    void onExtendedOrder() {
		/*
        // Copy over the extended order details
        copyExtendedOrderDetails( m_orderDlg.m_order, m_extOrdDlg.m_order);
		*/
    }

    void  onReqAcctData() {
		/*
        if ( dlg.m_subscribe) {
        	m_acctDlg.accountDownloadBegin(dlg.m_acctCode);
        }

        m_client.reqAccountUpdates( dlg.m_subscribe, dlg.m_acctCode);

        if ( m_client.isConnected() && dlg.m_subscribe) {
            m_acctDlg.reset();
            m_acctDlg.setVisible(true);
        }
		*/
    }

    void onFinancialAdvisor() {
	/*
      faGroupXML = faProfilesXML = faAliasesXML = null ;
      faError = false ;
      m_client.requestFA(EClientSocket.GROUPS) ;
      m_client.requestFA(EClientSocket.PROFILES) ;
      m_client.requestFA(EClientSocket.ALIASES) ;
	*/
    }

    void  onServerLogging() {
	/*
        // connect to TWS
        m_client.setServerLogLevel( dlg.m_serverLogLevel);
	*/
    }

    void  onReqAllOpenOrders() {
        // request list of all open orders
        m_client.reqAllOpenOrders();
    }

    void  onReqAutoOpenOrders() {
        // request to automatically bind any newly entered TWS orders
        // to this API client. NOTE: TWS orders can only be bound to
        // client's with clientId=0.
        m_client.reqAutoOpenOrders( true);
    }

    void  onReqManagedAccts() {
        // request the list of managed accounts
        m_client.reqManagedAccts();
    }

    void onClear() {
        m_tickers.clear();
        m_TWS.clear();
        m_errors.clear();
    }

    void onClose() {
        System.exit(1);
    }

    void onReqExecutions() {
		/*    
		m_client.reqExecutions( dlg.m_reqId, dlg.m_execFilter);
		*/
    }

    void onReqNewsBulletins() {
		/*
        if ( m_newsBulletinDlg.m_subscribe ) {
            m_client.reqNewsBulletins( m_newsBulletinDlg.m_allMsgs);
        }
        else {
            m_client.cancelNewsBulletins();
        }
		*/
    }

    void onCalculateImpliedVolatility() {
	/*
          m_client.calculateImpliedVolatility( m_orderDlg.m_id, m_orderDlg.m_contract,
                m_orderDlg.m_order.m_lmtPrice, m_orderDlg.m_order.m_auxPrice);
	*/
    }

    void onCancelCalculateImpliedVolatility() {
	/*
        m_client.cancelCalculateImpliedVolatility( m_orderDlg.m_id);
	*/
    }

    void onCalculateOptionPrice() {
	/*
        m_client.calculateOptionPrice( m_orderDlg.m_id, m_orderDlg.m_contract,
                m_orderDlg.m_order.m_lmtPrice, m_orderDlg.m_order.m_auxPrice);
	*/
    }

    void onCancelCalculateOptionPrice() {
	/*
        m_client.cancelCalculateOptionPrice( m_orderDlg.m_id);
	*/
    }

    void onGlobalCancel() {
        m_client.reqGlobalCancel();
    }

    void onReqMarketDataType() {
	/*
        // req mkt data type
        m_client.reqMarketDataType( m_orderDlg.m_marketDataType);
	*/
    }

    void onRequestPositions() {
        m_client.reqPositions();
    }

    void onCancelPositions() {
        m_client.cancelPositions();
    }

    void onRequestAccountSummary() {
	/*
       m_client.reqAccountSummary( dlg.m_reqId, dlg.m_groupName, dlg.m_tags);
	*/
    }

    void onCancelAccountSummary() {
	/*
		m_client.cancelAccountSummary( dlg.m_reqId);
	*/
    }

    public void tickPrice( int tickerId, int field, double price, int canAutoExecute) {
        // received price tick
    	String msg = EWrapperMsgGenerator.tickPrice( tickerId, field, price, canAutoExecute);
        m_tickers.add( msg );
    }

    public void tickOptionComputation( int tickerId, int field, double impliedVol, double delta, double optPrice, double pvDividend,
        double gamma, double vega, double theta, double undPrice) {
        // received computation tick
        String msg = EWrapperMsgGenerator.tickOptionComputation( tickerId, field, impliedVol, delta, optPrice, pvDividend,
            gamma, vega, theta, undPrice);
        m_tickers.add( msg );
    }

    public void tickSize( int tickerId, int field, int size) {
        // received size tick
    	String msg = EWrapperMsgGenerator.tickSize( tickerId, field, size);
        m_tickers.add( msg);
    }

    public void tickGeneric( int tickerId, int tickType, double value) {
        // received generic tick
    	String msg = EWrapperMsgGenerator.tickGeneric(tickerId, tickType, value);
        m_tickers.add( msg);
    }

    public void tickString( int tickerId, int tickType, String value) {
        // received String tick
    	String msg = EWrapperMsgGenerator.tickString(tickerId, tickType, value);
        m_tickers.add( msg);
    }

    public void tickSnapshotEnd(int tickerId) {
    	String msg = EWrapperMsgGenerator.tickSnapshotEnd(tickerId);
    	m_tickers.add( msg) ;
    }

    public void tickEFP(int tickerId, int tickType, double basisPoints, String formattedBasisPoints,
    					double impliedFuture, int holdDays, String futureExpiry, double dividendImpact,
    					double dividendsToExpiry) {
        // received EFP tick
    	String msg = EWrapperMsgGenerator.tickEFP(tickerId, tickType, basisPoints, formattedBasisPoints,
				impliedFuture, holdDays, futureExpiry, dividendImpact, dividendsToExpiry);
        m_tickers.add(msg);
    }

    public void orderStatus( int orderId, String status, int filled, int remaining,
    						 double avgFillPrice, int permId, int parentId,
    						 double lastFillPrice, int clientId, String whyHeld) {
        /*
		// received order status
    	String msg = EWrapperMsgGenerator.orderStatus( orderId, status, filled, remaining,
    	        avgFillPrice, permId, parentId, lastFillPrice, clientId, whyHeld);
        m_TWS.add(  msg);

        // make sure id for next order is at least orderId+1
        m_orderDlg.setIdAtLeast( orderId + 1);
		*/
    }

    public void openOrder( int orderId, Contract contract, Order order, OrderState orderState) {
        // received open order
    	String msg = EWrapperMsgGenerator.openOrder( orderId, contract, order, orderState);
        m_TWS.add( msg) ;
    }

    public void openOrderEnd() {
        // received open order end
    	String msg = EWrapperMsgGenerator.openOrderEnd();
        m_TWS.add( msg) ;
    }

    public void contractDetails(int reqId, ContractDetails contractDetails) {
    	String msg = EWrapperMsgGenerator.contractDetails( reqId, contractDetails);
    	m_TWS.add(msg);
    }

	public void contractDetailsEnd(int reqId) {
		String msg = EWrapperMsgGenerator.contractDetailsEnd(reqId);
		m_TWS.add(msg);
	}

    public void scannerData(int reqId, int rank, ContractDetails contractDetails,
                            String distance, String benchmark, String projection, String legsStr) {
    	String msg = EWrapperMsgGenerator.scannerData(reqId, rank, contractDetails, distance,
    			benchmark, projection, legsStr);
        m_tickers.add(msg);
    }

    public void scannerDataEnd(int reqId) {
    	String msg = EWrapperMsgGenerator.scannerDataEnd(reqId);
    	m_tickers.add(msg);
    }

    public void bondContractDetails(int reqId, ContractDetails contractDetails)
    {
    	String msg = EWrapperMsgGenerator.bondContractDetails( reqId, contractDetails);
    	m_TWS.add(msg);
    }

    public void execDetails(int reqId, Contract contract, Execution execution)
    {
    	String msg = EWrapperMsgGenerator.execDetails(reqId, contract, execution);
    	m_TWS.add(msg);
    }

    public void execDetailsEnd(int reqId)
    {
    	String msg = EWrapperMsgGenerator.execDetailsEnd(reqId);
    	m_TWS.add(msg);
    }

    public void updateMktDepth( int tickerId, int position, int operation,
                    int side, double price, int size) {
	/*
        MktDepthDlg depthDialog = m_mapRequestToMktDepthDlg.get(tickerId);
        if ( depthDialog != null ) {
            depthDialog.updateMktDepth( tickerId, position, "", operation, side, price, size);
        } else {
            System.err.println("cannot find dialog that corresponds to request id ["+tickerId+"]");
        }
	*/
    }

    public void updateMktDepthL2( int tickerId, int position, String marketMaker,
                    int operation, int side, double price, int size) {
    /*
		MktDepthDlg depthDialog = m_mapRequestToMktDepthDlg.get(tickerId);
        if ( depthDialog != null ) {
            depthDialog.updateMktDepth( tickerId, position, marketMaker, operation, side, price, size);
        } else {
            System.err.println("cannot find dialog that corresponds to request id ["+tickerId+"]");
        }
	*/
    }

    public void nextValidId( int orderId) {
        // received next valid order id
    	String msg = EWrapperMsgGenerator.nextValidId( orderId);
        m_TWS.add(msg) ;
        //m_orderDlg.setIdAtLeast( orderId);
    }

    public void error(Exception ex) {
        // do not report exceptions if we initiated disconnect
		/*
        if (!m_disconnectInProgress) {
            String msg = AnyWrapperMsgGenerator.error(ex);
            Main.inform( this, msg);
        }
		*/
    }

    public void error( String str) {
    	String msg = AnyWrapperMsgGenerator.error(str);
        m_errors.add( msg);
    }

    public void error( int id, int errorCode, String errorMsg) {
        // received error
    	String msg = AnyWrapperMsgGenerator.error(id, errorCode, errorMsg);
        m_errors.add( msg);
        for (int ctr=0; ctr < faErrorCodes.length; ctr++) {
            faError |= (errorCode == faErrorCodes[ctr]);
        }
		/*
        if (errorCode == MktDepthDlg.MKT_DEPTH_DATA_RESET) {
		
            MktDepthDlg depthDialog = m_mapRequestToMktDepthDlg.get(id);
            if ( depthDialog != null ) {
                depthDialog.reset();
            } else {
                System.err.println("cannot find dialog that corresponds to request id ["+id+"]");
            }
        }
		*/
    }

    public void connectionClosed() {
        String msg = AnyWrapperMsgGenerator.connectionClosed();
        //Main.inform( this, msg);
    }

    public void updateAccountValue(String key, String value,
                                   String currency, String accountName) {
    /*
		m_acctDlg.updateAccountValue(key, value, currency, accountName);
	*/
    }

    public void updatePortfolio(Contract contract, int position, double marketPrice,
        double marketValue, double averageCost, double unrealizedPNL, double realizedPNL,
        String accountName) {
	/*
        m_acctDlg.updatePortfolio(contract, position, marketPrice, marketValue,
            averageCost, unrealizedPNL, realizedPNL, accountName);
	*/
    }

    public void updateAccountTime(String timeStamp) {
	/*
        m_acctDlg.updateAccountTime(timeStamp);
	*/
    }

    public void accountDownloadEnd(String accountName) {
	/*
    	m_acctDlg.accountDownloadEnd( accountName);

    	String msg = EWrapperMsgGenerator.accountDownloadEnd( accountName);
        m_TWS.add( msg);
	*/
    }

    public void updateNewsBulletin( int msgId, int msgType, String message, String origExchange) {
	/*
        String msg = EWrapperMsgGenerator.updateNewsBulletin(msgId, msgType, message, origExchange);
        JOptionPane.showMessageDialog(this, msg, "IB News Bulletin", JOptionPane.INFORMATION_MESSAGE);
	*/
    }

    public void managedAccounts( String accountsList) {
    }

    public void historicalData(int reqId, String date, double open, double high, double low,
                               double close, int volume, int count, double WAP, boolean hasGaps) {
        String msg = EWrapperMsgGenerator.historicalData(reqId, date, open, high, low, close, volume, count, WAP, hasGaps);
    	m_tickers.add( msg );
    }
	
	public void realtimeBar(int reqId, long time, double open, double high, double low, double close, long volume, double wap, int count) {
		String msg = EWrapperMsgGenerator.realtimeBar(reqId, time, open, high, low, close, volume, wap, count);
        m_tickers.add( msg );
	}
	
    public void scannerParameters(String xml) {
        displayXML(EWrapperMsgGenerator.SCANNER_PARAMETERS, xml);
    }

	public void currentTime(long time) {
		String msg = EWrapperMsgGenerator.currentTime(time);
    	m_TWS.add(msg);
	}
	public void fundamentalData(int reqId, String data) {
		String msg = EWrapperMsgGenerator.fundamentalData(reqId, data);
		m_tickers.add(msg);
	}
	public void deltaNeutralValidation(int reqId, UnderComp underComp) {
		String msg = EWrapperMsgGenerator.deltaNeutralValidation(reqId, underComp);
		m_TWS.add(msg);
	}

    void displayXML(String title, String xml) {
        m_TWS.add(title);
        m_TWS.addText(xml);
    }

    public void receiveFA(int faDataType, String xml) {
    }

    public void marketDataType(int reqId, int marketDataType) {
        String msg = EWrapperMsgGenerator.marketDataType(reqId, marketDataType);
        m_tickers.add(msg);
    }

    public void commissionReport(CommissionReport commissionReport) {
        String msg = EWrapperMsgGenerator.commissionReport(commissionReport);
        m_TWS.add(msg);
    }

    public void position(String account, Contract contract, int pos, double avgCost) {
        String msg = EWrapperMsgGenerator.position(account, contract, pos, avgCost);
        m_TWS.add(msg);
    }

    public void positionEnd() {
        String msg = EWrapperMsgGenerator.positionEnd();
        m_TWS.add(msg);
    }

    public void accountSummary( int reqId, String account, String tag, String value, String currency) {
        String msg = EWrapperMsgGenerator.accountSummary(reqId, account, tag, value, currency);
        m_TWS.add(msg);
    }

    public void accountSummaryEnd( int reqId) {
        String msg = EWrapperMsgGenerator.accountSummaryEnd(reqId);
        m_TWS.add(msg);
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
