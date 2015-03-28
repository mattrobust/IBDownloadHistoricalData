# IBDownloadHistoricalData

Downloads historical data from interactive brokers and builds a data file useful for subsequent back-test

By [RobustTechHouse](http://robusttechhouse.com) http://robusttechhouse.com

##Java Logic##
The Java class generates in desired path a series of data files in reverse chronological order eg
  	
  	1.txt => most recent data
  	
  	2.txt => next most recent data
  	
  	... etc

##Python Logic##
The Python script takes the Java class output files and creates one contiguous data file in chronological order


##Useful References##

Historical Data Limitations: https://www.interactivebrokers.com/en/software/api/apiguide/tables/historical_data_limitations.htm

List of IB currencies: https://www.interactivebrokers.com/en/?f=%2Fen%2Ftrading%2Fexchanges.php%3Fexch%3Dibfxpro%26amp%3Bshowcategories%3D%26amp%3Bib_entity%3Dllc#

Sample code to request data: http://www.elitetrader.com/et/index.php?threads/request-ib-tick-data-java-api.206254/

Sample code: http://stackoverflow.com/questions/10777885/error-getting-the-eur-usd-historical-data-using-r-on-ibrokers

##Sample IB Historical Request Format For Reference##

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
