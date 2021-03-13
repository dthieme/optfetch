package uptick;


import org.apache.log4j.Logger;
import uptick.messages.UptickMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;


public final class UptickConnection
{
    private static final Logger log = Logger.getLogger(UptickConnection.class);
    private final String host;
    private final int port;
    private final Consumer<UptickMessage> handler;
    private final UptickSocketClient client;
    private final AtomicBoolean connected = new AtomicBoolean(false);

    public UptickConnection(final String host, final int port, final Consumer<UptickMessage> handler)
    {
        this.host = host;
        this.port = port;
        this.handler = handler;
        this.client = new UptickSocketClient(host, port, handler::accept);
    }

    public void connect() throws Exception {
        if (! connected.getAndSet(true)) {
            log.info("Connecting to uptick " + host + ":" + port);
            client.connect();
        }
    }

    public <T> void sendReport(final String topic, final List<List<T>> data) throws Exception {
        log.info("Sending report to uptick topic " + topic);
        final List<List<Object>> objList = new ArrayList<>(data.size());
        for (List<T> row : data) {
            final List<Object> rowObj = new ArrayList<>(row.size());
            rowObj.addAll(row);
            objList.add(rowObj);
        }
        final UptickMessage message = UptickMessageFactory.getInstance().newPublishDataRequest(topic,
                                                                                               Collections.emptyList(),
                                                                                               UptickMessageBody.newOnePageBody(objList));
        client.send(message);
    }
}
