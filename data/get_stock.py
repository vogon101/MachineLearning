import pandas_datareader.data as web
import datetime

STOCKS = ["III","ADN","ADM","AAL","ANTO","AHT","ABF","AZN","AV.","BAB","BA.","BARC","BDEV","BLT","BP.","BATS","BLND","BT.A","BNZL","BRBY","CPI","CCL","CNA","CCH","CPG","CRH","DCC","DGE","DLG","EZJ","EXPN","FRES","GKN","GSK","GLEN","HMSO","HL.","HIK","HSBA","IMB","ISAT","IHG","IAG","ITRK","INTU","ITV","JMAT","KGF","LAND","LGEN","LLOY","LSE","MKS","MERL","MNDI","NG.","NXT","OML","PSON","PSN","PFG","PRU","RRS","RB.","REL","RIO","RR.","RBS","RDSA","RMG","RSA","SGE","SBRY","SDR","SVT","SHP","SKY","SN.","SMIN","SPD","SSE","STAN","SL.","STJ","TW.","TSCO","TPK","TUI","ULVR","UU.","VOD","WTB","WOS","WPG","WPP","ATVI","ADBE","AKAM","ALXN","GOOG","GOOGL","AMZN","AAL","AMGN","ADI","AAPL","AMAT","ADSK","ADP","BIDU","BIIB","BMRN","AVGO","CA","CELG","CERN","CHTR","CHKP","CTAS","CSCO","CTXS","CTSH","CMCSA","COST","CSX","CTRP","XRAY","DISCA","DISCK","DISH","DLTR","EBAY","EA","EXPE","ESRX","FB","FAST","FISV","GILD","HAS","HSIC","HOLX","IDXX","ILMN","INCY","INTC","INTU","ISRG","JBHT","JD","KLAC","LRCX","LBTYA","LBTYK","LILA","LILAK","LVNTA","QVCA","MAT","MXIM","MCHP","MU","MSFT","MDLZ","MNST","MYL","NTES","NFLX","NCLH","NVDA","ORLY","PCAR","PAYX","PYPL","QCOM","REGN","ROST","SHPG","SIRI","SWKS","SBUX","SYMC","TMUS","TSLA","TXN","KHC","PCLN","TSCO","TRIP","FOXA","ULTA","VRSK","VRTX","VIAB","VOD","WBA","WDC","XLNX","YHOO"]

print("Total Stocks: " + str(len(STOCKS))

start = datetime.datetime(2016, 1, 1)
end = datetime.datetime.today()

for stock in STOCKS:
    print("Fetching " + stock)
    data = web.DataReader(stock, 'google', start, end)
    with open(stock + ".csv", "w") as file:
        file.write(data.to_csv())
