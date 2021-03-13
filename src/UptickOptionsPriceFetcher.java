import org.apache.log4j.Logger;
import uptick.UptickConnection;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;


public final class UptickOptionsPriceFetcher
{
    private static final Logger log = Logger.getLogger(UptickOptionsPriceFetcher.class);

    private static final String DefaultTopic = "BROYHILL/TEST/OPTIONS";
    private static final long DefaultPublishFreq = 5 * 1000;
    private final long publishFreq;
    private final String topic;
    private final AtomicBoolean started = new AtomicBoolean(false);
    private final String chromeDriver;
    private final List<String> symbols;

    public UptickOptionsPriceFetcher(final String chromeDriver, final List<String> symbols)
    {
        this(chromeDriver, symbols, DefaultPublishFreq, DefaultTopic);
    }

    public UptickOptionsPriceFetcher(final String chromeDriver, final List<String> symbols, final long publishFreq, final String topic)
    {
        this.chromeDriver = chromeDriver;
        this.publishFreq = publishFreq;
        this.symbols = symbols;
        this.topic = topic;
    }

    public void startPublish() throws Exception {
        if (! started.getAndSet(true))
        {
            log.info("Starting uptick publish to topic " + topic);
            final UptickConnection uptick = new UptickConnection("broyhill.uptick.tech", 40001, uptickMessage -> {
                log.info("Got uptick message " + uptickMessage);
            });
            uptick.connect();

            log.info("Scheduling uptick publish");
            final OptionsPriceFetcher priceFetcher = new OptionsPriceFetcher(chromeDriver, symbols);
            final UptickPriceFetch fetch = new UptickPriceFetch(priceFetcher, symbols, uptick);
            Executors.newSingleThreadScheduledExecutor()
                    .scheduleWithFixedDelay(fetch,
                                            0,
                                            publishFreq,
                                            TimeUnit.MILLISECONDS);



        }
    }

    private final /* inner */ class FetchTask implements Callable<Boolean>
    {
        private final String symbol;
        private final OptionsPriceFetcher priceFetcher;
        private final UptickConnection uptick;

        public FetchTask(final String symbol, final OptionsPriceFetcher priceFetcher, final UptickConnection connection)
        {
            this.symbol = symbol;
            this.priceFetcher = priceFetcher;
            this.uptick = connection;
        }

        @Override public Boolean call() throws Exception
        {
            try
            {
                final Consumer<List<OptionsPriceFetcher.OptionsInfo>> handler = optionsInfoList -> {

                    try
                    {
                        if (! optionsInfoList.isEmpty())
                        {
                            final List<List<String>> rows = new ArrayList<>();
                            rows.add(OptionsPriceFetcher.OptionsInfo.getHeaderRows());
                            for (OptionsPriceFetcher.OptionsInfo info : optionsInfoList)
                            {
                                rows.add(info.toRow());
                            }
                            uptick.sendReport(topic + "~" + symbol, rows);
                        }
                    }
                    catch (Throwable t)
                    {
                        log.error("Error sending report " + t.getMessage(), t);
                    }
                };
                log.info("Fetching " + symbol);
                priceFetcher.fetch(symbol, handler);

            }
            catch (Throwable t)
            {
                log.equals("Error fetching symbol " + symbol);
            }
            finally
            {
                return true;
            }
        }
    }


    private final /* inner */ class UptickPriceFetch implements Runnable
    {
        private final OptionsPriceFetcher priceFetcher;
        private final List<String> symbols;
        private final UptickConnection uptick;
        private final ExecutorService fetcher = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() / 2);

        public UptickPriceFetch(final OptionsPriceFetcher priceFetcher, final List<String> symbols, final UptickConnection uptick)
        {
            this.priceFetcher = priceFetcher;
            this.symbols = symbols;
            this.uptick = uptick;
        }

        @Override public void run()
        {

            try
            {
                final List<FetchTask> tasks = new ArrayList<>(symbols.size());
                for (String symbol : symbols)
                {
                    tasks.add(new FetchTask(symbol, priceFetcher, uptick));
                }
                final List<Future<Boolean>> results = fetcher.invokeAll(tasks);
                for (Future<Boolean> result : results) {
                    result.get();
                }
            }
            catch (Throwable t)
            {
                log.error(t.getMessage(), t);
            }
        }
    }


    private static void printUsage() {
        System.out.println("Usage java " + UptickOptionsPriceFetcher.class + " path_to_chromedriver symbolfile");
        System.exit(0);
    }


    public static void main(final String[] args) {
        try {


            if (args.length < 2)
                printUsage();
            final String chromeDriver = args[0];

            final List<String> symbols = new ArrayList<>();
            final String path = args[1];
            log.info("Reading symbols from " + path);
            final List<String> lines = Files.readAllLines(Paths.get(path));
            for (String line : lines) {
                line = line.trim().toUpperCase();
                if (!line.isEmpty()) {
                    log.info("Adding symbol " + line);
                    symbols.add(line);
                }
            }

            UptickOptionsPriceFetcher fetcher = new UptickOptionsPriceFetcher(chromeDriver, symbols);
            fetcher.startPublish();
            while (true) {
                Thread.sleep(1000);
            }
        }
        catch (Throwable t)
        {
            log.error("Error fetching options : " + t.getMessage(), t);
        }
    }

}
