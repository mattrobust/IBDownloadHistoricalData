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
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.lang.Thread;

/*
	Historical Data Limitations: https://www.interactivebrokers.com/en/software/api/apiguide/tables/historical_data_limitations.htm
	List of IB currencies: https://www.interactivebrokers.com/en/?f=%2Fen%2Ftrading%2Fexchanges.php%3Fexch%3Dibfxpro%26amp%3Bshowcategories%3D%26amp%3Bib_entity%3Dllc#
	Sample code to request data: http://www.elitetrader.com/et/index.php?threads/request-ib-tick-data-java-api.206254/
	Sample code: http://stackoverflow.com/questions/10777885/error-getting-the-eur-usd-historical-data-using-r-on-ibrokers
	
	Symbol: USD
	Security Type: CASH
	Exchange: IDEALPRO
	Primary Exchange: IDEALPRO
	Currency: JPY
	End Date/Time: 20150326 07:46:46 GMT
	Duration: 1 D, another eg could be 14400 S
	Bar Size Setting: 1 min
	What to Show: BID/ASK
	Regular Trading Hours: 1
	Date Format Style: 1
	
	Data files are generated in desired path in reverse chronological order eg
	1.txt => most recent data
	2.txt => next most recent data
	... etc
	
	
*/
class IBDownloadHistoricalData  implements EWrapper {

    private EClientSocket   mClient = new EClientSocket( this);
    private FileLog     mDataFile;
    private FileLog     mServerResponsesLog = new FileLog("ServerResponses.txt");
    private FileLog     mServerErrorsLog = new FileLog("ServerErrors.txt");
    
	//CHANGE this to the desired path
	private String 		mDataPath = "Data/CAD/BID/";
	//CHANGE this to the desired contract
	private Contract mContract =  new Contract(0, "USD", "CASH", "",
                    0, "", "",
                    "IDEALPRO", "CAD", "", "",
                    new Vector<ComboLeg>(), "IDEALPRO", false,
                    "", "");
	//CHANGE this between BID and ASK to get different fields
	private String mRequestField = "BID";
	//CHANGE this to tweak how long to wait for data to come in
	private int mDataWaitTimeSeconds = 60; 
	
	private boolean mIsThisRequestFinished = false;
	private Date mCurrRequestDateTime = null;
				
    public static void main (String args[]) {
		System.out.println("Starting IBDownloadHistoricalData");
		IBDownloadHistoricalData downloader = new IBDownloadHistoricalData();
		downloader.run();
    }

	private Date getLatestDownloadDate() {
		//the day before at 12midnight
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
		cal.add(Calendar.DATE, -1);
		Date date1DayBefore = cal.getTime();
		return date1DayBefore;
	}
	
	//CHANGE this to the first download date, which depends on how much data access you have in your IB account
	private Date getFirstDownloadDate() {
		Calendar cal = Calendar.getInstance();
		cal.set(2007, 6, 1, 0, 0);
		Date d = cal.getTime();
		return d;
	}
	
	void run() {
		connect();
		long firstDownloadDateSeconds = getFirstDownloadDate().getTime();
		
		//loop until we are done with all requests
		while (true) {
		
			//do one request, loop here until we are done or exceed time
			Date startTime = new Date();
			mIsThisRequestFinished = false;
			requestHistoricalData();
			while (!mIsThisRequestFinished) {
				try {
					Thread.sleep(5000);
				} catch (Exception e) {
					System.err.println(e);
				}
				Date currTime = new Date();
				long timediffSeconds = (currTime.getTime() - startTime.getTime())/1000L;
				
				//waited too long break out and try again
				if (timediffSeconds > mDataWaitTimeSeconds) {
					break;
				}
			}
			mDataFile.close();
			
			if (!mIsThisRequestFinished) {
				System.out.println ("Failed to finish current request " + mCurrRequestDateTime);
				mDataFile.delete();
			}
			
			if (firstDownloadDateSeconds > mCurrRequestDateTime.getTime()) {
				//this actually means we finished all the downloading
				break;
			}
			
			//force sleep 20 seconds to slow down requests to avoid IB pacing constraints
			try {
					Thread.sleep(20000);
				} catch (Exception e) {
					System.err.println(e);
			}
			
		}
		
		disconnect();
		if (mDataFile != null) {
			mDataFile.close();
		}
		mServerResponsesLog.close();
		mServerErrorsLog.close();
	}
	
	void connect() {
		//connect localhost port 7496
		mClient.eConnect("", 7496, 0);
        if (mClient.isConnected()) {
            mServerResponsesLog.add("Connected to Tws server version " +
                       mClient.serverVersion() + " at " +
                       mClient.TwsConnectionTime());
        }
    }

    void disconnect() {
        mClient.eDisconnect();
    }

    void requestHistoricalData() {
		File latestFile = lastFileModified(mDataPath);
		int num = 0;
		if (latestFile != null) {
			mCurrRequestDateTime = getFirstDateTime(latestFile);
		
			String latestFileName = latestFile.getName();
			
			int index = latestFileName.indexOf(".");
			String fileNameSubstring = latestFileName.substring(0, index);
			num = Integer.parseInt(fileNameSubstring);
		} else {
			mCurrRequestDateTime = getLatestDownloadDate();
		}

		mDataFile = new FileLog(mDataPath + String.format("%d.txt", num+1));
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd hh:mm:ss");
		String requestDateTimeStr = formatter.format(mCurrRequestDateTime);
		
		System.out.println(String.format("Send Historical Data Request For contract=%s requestDateTimeStr=%s requestField=%s", mContract.m_currency, requestDateTimeStr, mRequestField));
		mClient.reqHistoricalData( 0, mContract,
									requestDateTimeStr, "1 D", //request in 4 hrs blocks
                                    "1 min", mRequestField,
                                    1, 1);
											
    }

	public static Date getFirstDateTime(File file) {
		try {		
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = br.readLine();
			int index = line.indexOf("date = ");
			
			if (index < 1) {
				System.err.println("Failed to parse out date from first line of " + file.toPath());
				return null;
			}

			String dateTimeString = line.substring(index+7, index + 25); //between these indices are the datetime numbers eg 20141225 01:00:00
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd hh:mm:ss");
			Date parsedDate = formatter.parse(dateTimeString);
			
			return parsedDate;
			
		} catch (Exception e) {
			System.err.println(e);
			return null;
		}
	}
	
	public static File lastFileModified(String dir) {
		File fl = new File(dir);
		File[] files = fl.listFiles(new FileFilter() {          
			public boolean accept(File file) {
				return file.isFile();
			}
			});
		if (files.length == 0) {
			return null;
		}
		
		long lastMod = Long.MIN_VALUE;
		File choice = null;
		for (File file : files) {
			if (file.lastModified() > lastMod) {
				choice = file;
				lastMod = file.lastModified();
			}
		}
		return choice;
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
        mServerResponsesLog.add(msg) ;
		mServerResponsesLog.flush();
    }

    public void error(Exception ex) {
    }

    public void error( String str) {
    	String msg = AnyWrapperMsgGenerator.error(str);
        mServerErrorsLog.add( msg);
		mServerErrorsLog.flush();
    }

    public void error( int id, int errorCode, String errorMsg) {
    	String msg = AnyWrapperMsgGenerator.error(id, errorCode, errorMsg);
        mServerErrorsLog.add( msg);
		mServerErrorsLog.flush();
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
		if (msg.toUpperCase().contains("FINISHED")) {
			mIsThisRequestFinished = true;
		}
		else {
			mDataFile.add( msg );
		}
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
		public String mFilePath;
	
		public FileLog(String filePath) {
			mFilePath = filePath;
			try {
				writer = new PrintWriter(new BufferedWriter(new FileWriter(filePath, true)));
			}catch (Exception e) {
				System.err.println(e);
			}
		}
		
		public void add(String msg) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd hh:mm:ss");
			String nowDateTimeStr = formatter.format(new Date());
			writer.write(nowDateTimeStr + " " + msg + "\n");
		}
		
		public void close() {
			writer.flush();
			writer.close();
		}
		
		public void flush() {
			writer.flush();
		}
		
		public void delete() {
			try {
				File file = new File(mFilePath);
				file.delete();
			} catch (Exception e) {
				System.err.println("Failed to delete file: " + mFilePath);
			}
			
		}
	}
}
