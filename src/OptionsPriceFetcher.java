import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


public class OptionsPriceFetcher
{
    private static final Logger log = Logger.getLogger(OptionsPriceFetcher.class);
    private static final String UrlBase = "https://www.barchart.com/stocks/quotes/~SYM~/options";
    private static final String UrlExpiry = UrlBase + "?expiration=~EXP~";
    private final String chromeDriverPath;
    private final String outputCsv;
    private final List<String> symbols;
    private final ChromeDriver driver;

    public OptionsPriceFetcher(final String chromeDriver,
                               final String outputCsv,
                               final List<String> symbols) {
        this.chromeDriverPath = chromeDriver;
        this.outputCsv = outputCsv;
        this.symbols = symbols;
        driver = initDriver(chromeDriverPath);
    }

    private void startFetch() {
        int count = 1;
        log.info("Outputting info to " + outputCsv);
        final StringBuilder buf = new StringBuilder();
        buf.append(OptionsInfo.getHeader());
        int numLines = 0;
        for (String symbol : symbols) {
            log.info("Fetching symbol " + symbol + " (" + count + "/" + symbols.size() + ")");
            final String basePage = getBaseWebpage(symbol);
            final List<String> expirys = extractExpirations(basePage);
            log.info("Got expirations for " + symbol + " :\n");
            expirys.forEach(System.out::println);
            int expCount = 1;
            for (String expiry : expirys) {
                log.info("Fetching " + symbol + " expiry " + expiry + " (" + expCount + "/" + expirys.size() + ")");
                final String expiryPage = getExpiryWebpage(symbol, expiry);
                final List<OptionsInfo> optionsInfoList = extractDataForExpiration(expiryPage, symbol, expiry);
                optionsInfoList.forEach(o -> buf.append(o.toCsvRow()));
                numLines += optionsInfoList.size();
                expCount++;
            }
        }
        log.info("Writing " + numLines + " to " + outputCsv);
        try
        {
            Files.write(Paths.get(outputCsv), buf.toString().getBytes(Charset.defaultCharset()));
        }
        catch (Throwable t)
        {
            final String tmpCsv = outputCsv + ".tmp";
            log.error("Error writing " + outputCsv + ", could it perhaps be open in Excel, outputting temp file to " + tmpCsv);
            try
            {
                Files.write(Paths.get(tmpCsv), buf.toString().getBytes(Charset.defaultCharset()));
            }
            catch (Throwable t2)
            {
                log.error("Error writing temp file, giving up");
            }
        }
    }

    private static final /* inner */ class OptionsInfo
    {
        private final String symbol;
        private final String contract;
        private final String expiry;
        private final String putCall;
        private String strike;
        private String moneyness;
        private String bid;
        private String mid;
        private String ask;
        private String last;
        private String change;
        private String pctChange;
        private String volume;
        private String openInterest;
        private String volumeOpenInterestRatio;
        private String impliedVol;
        private String lastTrade;

        public OptionsInfo(final String symbol, final String contract, final String expiry)
        {
            this.symbol = symbol;
            this.contract = contract;
            this.expiry = expiry;
            this.putCall = Character.toString(symbol.charAt(symbol.length()-1));
        }

        public void setField(final int idx, final String data)
        {
            switch (idx) {
                case 0:
                    strike = data;
                    break;
                case 1:
                    moneyness = data;
                    break;
                case 2:
                    bid = data;
                    break;
                case 3:
                    mid = data;
                    break;
                case 4:
                    ask = data;
                    break;
                case 5:
                    last = data;
                    break;
                case 6:
                    change = data;
                    break;
                case 7:
                    pctChange = data;
                    break;
                case 8:
                    volume = data;
                    break;
                case 9:
                    openInterest = data;
                    break;
                case 10:
                    volumeOpenInterestRatio = data;
                    break;
                case 11:
                    impliedVol = data;
                    break;
                case 12:
                    lastTrade = data;
                    break;
            }
        }

        public static String getHeader() {
            final StringBuilder sb = new StringBuilder();
            sb.append("symbol,");
            sb.append("contract,");
            sb.append("expiry,");
            sb.append("put/call,");
            sb.append("strike,");
            sb.append("moneyness,");
            sb.append("bid,");
            sb.append("mid,");
            sb.append("ask,");
            sb.append("last,");
            sb.append("change,");
            sb.append("pctChange=,");
            sb.append("volume=,");
            sb.append("openInterest,");
            sb.append("volumeOpenInterestRatio,");
            sb.append("impliedVol,");
            sb.append("lastTrade\n");
            return sb.toString();
        }

        public String toCsvRow() {
            final StringBuilder sb = new StringBuilder();
            sb.append(symbol).append(',');
            sb.append(contract).append(",");
            sb.append(expiry).append(",");
            sb.append(putCall).append(',');
            sb.append(strike).append(',');
            sb.append(moneyness).append(',');
            sb.append(bid).append(',');
            sb.append(mid).append(',');
            sb.append(ask).append(',');
            sb.append(last).append(',');
            sb.append(change).append(',');
            sb.append(pctChange).append(',');
            sb.append(volume).append(',');
            sb.append(openInterest).append(',');
            sb.append(volumeOpenInterestRatio).append(',');
            sb.append(impliedVol).append(',');
            sb.append(lastTrade).append('\n');
            return sb.toString();
        }

        @Override public String toString()
        {
            final StringBuilder sb = new StringBuilder("OptionsInfo{");
            sb.append("symbol='").append(symbol).append('\'');
            sb.append(", putCall='").append(putCall).append('\'');
            sb.append(", strike='").append(strike).append('\'');
            sb.append(", moneyness='").append(moneyness).append('\'');
            sb.append(", bid='").append(bid).append('\'');
            sb.append(", mid='").append(mid).append('\'');
            sb.append(", ask='").append(ask).append('\'');
            sb.append(", last='").append(last).append('\'');
            sb.append(", change='").append(change).append('\'');
            sb.append(", pctChange='").append(pctChange).append('\'');
            sb.append(", volume='").append(volume).append('\'');
            sb.append(", openInterest='").append(openInterest).append('\'');
            sb.append(", volumeOpenInterestRatio='").append(volumeOpenInterestRatio).append('\'');
            sb.append(", impliedVol='").append(impliedVol).append('\'');
            sb.append(", lastTrade='").append(lastTrade).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }


    private List<OptionsInfo> extractDataForExpiration(final String basePage, final String symbol, final String expiry)
    {
        final List<OptionsInfo> list = new ArrayList<>();
        final String symbolMarker = "data-current-symbol=\"";
        final String dataStart = "<span data-ng-bind=\"cell\">";
        final String[] lines = basePage.split("\n");
        String curSymbol = null;

        int dataIdx = 0;
        OptionsInfo curOptionsInfo = null;
        for (String line : lines) {
            if (line.contains(symbolMarker)) {
                final int startIdx = line.indexOf(symbolMarker) + symbolMarker.length();
                curSymbol = line.substring(startIdx, line.indexOf('"', startIdx));
                //log.info("Cur symbol " + curSymbol);
                if (curOptionsInfo != null)
                    list.add(curOptionsInfo);
                curOptionsInfo = new OptionsInfo(curSymbol, symbol, expiry);
                dataIdx = 0;
            }
            else if (curSymbol != null) {
                if (line.contains(dataStart)) {
                    final int startIdx = line.indexOf(dataStart) + dataStart.length();
                    //log.info("Parsing " + line);
                    final int endIdx = line.indexOf("</span>");
                    final String data = line.substring(startIdx, endIdx);
                    //log.info("Got data point for " + curSymbol + ": " + data);
                    curOptionsInfo.setField(dataIdx++, data);
                }
            }
        }
        return list;
    }


    private List<String> extractExpirations(final String basePage) {
        final Collection<String> expirys = new LinkedHashSet<>();
        final String valMarker = "value=\"";
        final String[] lines = basePage.split("\n");
        int lineNum = 0;
        for (String line : lines)
        {
            lineNum++;
            //log.info("On line num " + lineNum);
            if (line.contains("<!-- ngRepeat: (key, expiration) in expirations track by $index -->"))
            {
                //log.info("Found expiry line: " + line);
                int curIdx = 0;
                while (true)
                {
                    curIdx = line.indexOf(valMarker, curIdx);
                    final int startIdx = curIdx + valMarker.length();
                    final int endIdx = line.indexOf('"', startIdx);
                    final String expiry = line.substring(startIdx, endIdx);
                    //log.info("Got expiry " + expiry);
                    if (expiry.contains("<!--"))
                    {
                        break;
                    }
                    else
                    {
                        expirys.add(expiry);
                    }
                    curIdx = endIdx+1;

                }

            }
        }
        return new ArrayList<>(expirys);
    }


    private String getExpiryWebpage(final String symbol, final String expiry) {
        final String basePage = UrlExpiry.replace("~SYM~", symbol).replace("~EXP~", expiry);
        return getWebpageSource(basePage);
    }

    private String getBaseWebpage(final String symbol) {
        final String basePage = UrlBase.replace("~SYM~", symbol);
        return getWebpageSource(basePage);
    }

    private String getWebpageSource(final String url) {
        log.info("Obtaining webpage " + url);
        driver.get(url);
        return driver.getPageSource();
    }


    private static void printUsage() {
        System.out.println("Usage java " + OptionsPriceFetcher.class + " path_to_chromedriver output_csv symbol1 [symbol2...]");
        System.exit(0);
    }

    private ChromeDriver initDriver(final String driverPath)
    {
        log.info("Loading chrome driver");
        System.setProperty("webdriver.chrome.driver", driverPath);
        final ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--headless");
        return new ChromeDriver(chromeOptions);
    }


    public static void main(final String[] args) {
        try {


            if (args.length < 3)
                printUsage();
            final String chromeDriver = args[0];
            final String outputCsv = args[1];
            final List<String> symbols = new ArrayList<>();
            // if third arg starts with file:, read from a list
            if (args[2].startsWith("file:"))  {
                final String path = args[2].substring("file:".length());
                log.info("Reading symbols from " + path);
                final List<String> lines = Files.readAllLines(Paths.get(path));
                for (String line : lines) {
                    line = line.trim().toUpperCase();
                    if (!line.isEmpty()) {
                        log.info("Adding symbol " + line);
                        symbols.add(line);
                    }
                }
            }
            else
            {
                for (int i = 2; i < args.length; i++)
                    symbols.add(args[i]);
            }
            final OptionsPriceFetcher priceFetcher = new OptionsPriceFetcher(chromeDriver, outputCsv, symbols);
            priceFetcher.startFetch();

        }
        catch (Throwable t)
        {
            log.error("Error fetching options : " + t.getMessage(), t);
        }
    }
}
