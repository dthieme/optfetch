import org.apache.log4j.Logger;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;


public class OptionsPriceFetcher
{
    private static final Logger log = Logger.getLogger(OptionsPriceFetcher.class);
    private static final String UrlBase = "https://www.barchart.com/stocks/quotes/~SYM~/options";
    private static final String UrlExpiry = UrlBase + "?expiration=~EXP~";
    private final String chromeDriverPath;
    private final List<String> symbols;
    private final ChromeDriver driver;
    private final ExecutorService webpageFetchExecutor = Executors.newSingleThreadExecutor();

    public OptionsPriceFetcher(final String chromeDriver,
                               final List<String> symbols) {
        this.chromeDriverPath = chromeDriver;
        this.symbols = symbols;
        driver = initDriver(chromeDriverPath);
    }

    public void startFetch(final String outputCsv) {
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



    public void fetch(final String symbol, final Consumer<List<OptionsInfo>> handler) {
        int count = 1;
        final List<OptionsInfo> list = new ArrayList<>();
        log.info("Fetching symbol " + symbol + " (" + count + "/" + symbols.size() + ")");
        final String basePage = getBaseWebpage(symbol);
        log.info("Got base page, extracting expirys");
        final List<String> expirys = extractExpirations(basePage);
        log.info("Got expirations for " + symbol + " :\n");
        expirys.forEach(System.out::println);
        int expCount = 1;
        for (String expiry : expirys) {
            log.info("Fetching expiry " + expiry + " : (" + expCount + "/" + expirys.size() + ")");
            final String expiryPage = getExpiryWebpage(symbol, expiry);
            final List<OptionsInfo> optionsInfoList = extractDataForExpiration(expiryPage, symbol, expiry);
            list.addAll(optionsInfoList);
            expCount++;
        }
        log.info("Fetched " + list.size() + " options info");
        handler.accept(list);
    }

    public static final /* inner */ class OptionsInfo
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

        public static List<String> getHeaderRows() {
            final List<String> sb = new ArrayList<>();
            sb.add("symbol");
            sb.add("contract");
            sb.add("expiry");
            sb.add("put/call");
            sb.add("strike");
            sb.add("moneyness");
            sb.add("bid");
            sb.add("mid");
            sb.add("ask");
            sb.add("last");
            sb.add("change");
            sb.add("pctChange=");
            sb.add("volume=");
            sb.add("openInterest");
            sb.add("volumeOpenInterestRatio");
            sb.add("impliedVol");
            sb.add("lastTrade\n");
            return sb;
        }

        public static String getHeader() {
            final StringBuilder sb = new StringBuilder();
            getHeaderRows().forEach(r -> sb.append(r).append(","));
            return sb.toString();
        }

        public List<String> toRow() {
            final List<String> sb = new ArrayList<>();
            sb.add(symbol);
            sb.add(contract);
            sb.add(expiry);
            sb.add(putCall);
            sb.add(strike);
            sb.add(moneyness);
            sb.add(bid);
            sb.add(mid);
            sb.add(ask);
            sb.add(last);
            sb.add(change);
            sb.add(pctChange);
            sb.add(volume);
            sb.add(openInterest);
            sb.add(volumeOpenInterestRatio);
            sb.add(impliedVol);
            sb.add(lastTrade);
            return sb;
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
        //log.info("Here 1");
        final String valMarker = "value=\"";
        //log.info("Here 2");
        final String[] lines = basePage.split("\n");
        //log.info("Here 3");
        int lineNum = 0;
        for (String line : lines)
        {
            //log.info("Here 4");
            lineNum++;
            //log.info("On line num " + lineNum);
            if (line.contains("<!-- ngRepeat: (key, expiration) in expirations track by $index -->"))
            {
                //log.info("Here 5");
                //log.info("Found expiry line: " + line);
                int curIdx = 0;
                while (true)
                {
                    //log.info("Here 6");
                    curIdx = line.indexOf(valMarker, curIdx);
                    //log.info("Here 7");
                    final int startIdx = curIdx + valMarker.length();
                    //log.info("Here 8");
                    final int endIdx = line.indexOf('"', startIdx);
                    if (endIdx == -1)
                        break;
                    //log.info("Here 9 with start " + startIdx + " end idx " + endIdx);
                    final String expiry = line.substring(startIdx, endIdx);
                    //log.info("Got expiry " + expiry);
                    //log.info("Here 10");
                    if (expiry.contains("<!--"))
                    {
                        break;
                    }
                    else
                    {
                        expirys.add(expiry);
                    }
                    //log.info("Here 11");
                    curIdx = endIdx+1;
                    //log.info("CUR IDX NOW " + curIdx);
                }
                //log.info("Here 12");
            }
        }
        //log.info("Here 13");
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
        final WebpageFetcher fetcher = new WebpageFetcher(url);
        final Future<String> webpageResult = webpageFetchExecutor.submit(fetcher);
        try
        {
            long t1 = System.currentTimeMillis();
            log.info("Waiting for future result");
            final String data = webpageResult.get(60, TimeUnit.SECONDS);
            long t2 = System.currentTimeMillis();
            log.info("Webpage data of length " + data.length() + " obtained in " + (t2- t1) + " ms");
            return data;
        }
        catch (Exception e)
        {
            webpageResult.cancel(true);
            log.info("Fetch of website " + url + " failed...retrying " + e.getMessage(), e);
            return getWebpageSource(url);
        }
    }


    private final /* inner */ class WebpageFetcher implements Callable<String>
    {
        private final String url;

        public WebpageFetcher(final String url)
        {
            this.url = url;
        }

        @Override public String call() throws Exception
        {
            log.info("Obtaining webpage " + url);
            driver.get(url);
            return driver.getPageSource();
        }
    }




    private ChromeDriver initDriver(final String driverPath)
    {
        log.info("Loading chrome driver");
        System.setProperty("webdriver.chrome.driver", driverPath);
        final ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--headless");
        return new ChromeDriver(chromeOptions);
    }

    private static void printUsage() {
        System.out.println("Usage java " + OptionsPriceFetcher.class + " path_to_chromedriver output_csv symbol1 [symbol2...]");
        System.exit(0);
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
            final OptionsPriceFetcher priceFetcher = new OptionsPriceFetcher(chromeDriver, symbols);
            priceFetcher.startFetch(outputCsv);

        }
        catch (Throwable t)
        {
            log.error("Error fetching options : " + t.getMessage(), t);
        }
    }
}
