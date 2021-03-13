package uptick;

import org.apache.log4j.Logger;
import uptick.messages.Heartbeat;
import uptick.messages.UptickMessage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public final class UptickSocketClient
{
    private static final Logger log = Logger.getLogger(UptickSocketClient.class);
    private static final String ProdHost = "broyhill.uptick.tech";
    private static final int ProdPort = 40001;

    private static final long ConnectionCheckFrequency = 5000;
    private static final long ReconnectTryInterval = 1000;
    private static final UptickSerializer serializer = new UptickSerializer();
    private final AtomicBoolean reconnecting = new AtomicBoolean(false);
    private final String host;
    private final int port;
    private final UptickMessageHandler messageHandler;
    private final AtomicBoolean connected = new AtomicBoolean(false);
    private final UptickMessageFactory messageFactory = UptickMessageFactory.getInstance();
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private Socket socket;
    private ClientDataHandler clientDataHandler;
    private ConnectionCheckTask connectionCheckTask;
    private ReconnectThread reconnectTask;

    public static UptickSocketClient newProdClient() {
        return new UptickSocketClient(ProdHost, ProdPort);
    }



    public UptickSocketClient(final String host, final int port)
    {
        this(host, port, UptickMessageHandlers.getInstance());
    }

    public UptickSocketClient(final String host, final int port, final UptickMessageHandler messageHandler)
    {
        this.host = host;
        this.port = port;
        this.messageHandler = messageHandler;
    }


    private final /* inner */ class ConnectionCheckTask implements Runnable
    {
        private boolean running = true;
        private final DataOutputStream dataOutputStream;

        public ConnectionCheckTask(final DataOutputStream dataOutputStream)
        {
            this.dataOutputStream = dataOutputStream;
        }

        public void stop()
        {
            running = false;
        }

        @Override
        public void run()
        {
            while (running)
            {
                try
                {
                    /*
                    final Heartbeat heartbeat = messageFactory.newHeartbeat();
                    if (!running)
                        break;
                    send(heartbeat);
                    */
                    Thread.sleep(ConnectionCheckFrequency);

                }
                catch (Throwable t)
                {
                    log.error("Detected connection error in uptick " + host + ":" + port + ", starting reconnect process...");
                    running = false;
                    startReconnectLoop();
                    break;
                }
            }
            log.info("Uptick connection check task exiting");
        }
    }

    private final /* inner */ class ReconnectThread extends Thread
    {



        @Override
        public void run()
        {
            int numTries = 0;
            while (true)
            {
                try
                {
                    closeAll();
                    initConnection();
                    final Heartbeat heartbeat = messageFactory.newHeartbeat();
                    send(heartbeat, false);
                    connected.set(true);
                    startDataHandlerAncConnectionCheck();
                    reconnecting.set(false);
                    log.info("Uptick econnection to " + host + ":" + port + " successful");
                    break;
                }
                catch (InterruptedException ie)
                {
                    log.info("Reconnect task exiting");
                }
                catch (Throwable t)
                {
                    try
                    {
                        numTries++;
                        Thread.sleep(ReconnectTryInterval);

                        if ((numTries % 10) == 0)
                        {
                            log.info("Could not connect to uptick at " + host + ":" + port + "...retrying");

                        }
                    }
                    catch (InterruptedException ww) {

                    }
                }
            }

        }
    }

    private void closeAll()
    {
        if (connectionCheckTask != null)
        {
            try
            {
                connectionCheckTask.stop();
            }
            catch (Throwable t)
            {

            }

        }
        if (clientDataHandler != null)
        {
            try
            {
                clientDataHandler.stop();
            }
            catch (Throwable t)
            {

            }
        }
        if (inputStream != null)
        {
            try
            {
                inputStream.close();
            }
            catch (Throwable t)
            {

            }
        }
        if (outputStream != null)
        {
            try
            {
                outputStream.close();
            }
            catch (Throwable t)
            {

            }
        }


        if (socket != null)
        {
            try
            {
                socket.close();
            }
            catch (Throwable t)
            {

            }
        }
    }


    private void startReconnectLoop()
    {
        if (!reconnecting.getAndSet(true))
        {
            log.info("Starting uptick reconnect task...");
            reconnectTask = new ReconnectThread();
            reconnectTask.start();
        }
    }


    private void initConnection() throws Exception
    {
        socket = new Socket();
        socket.connect(new InetSocketAddress(host, port), 5000);
        outputStream = new DataOutputStream(socket.getOutputStream());
        inputStream = new DataInputStream(socket.getInputStream());
    }

    private void startDataHandlerAncConnectionCheck()
    {
        clientDataHandler = new ClientDataHandler(inputStream, messageHandler);
        connectionCheckTask = new ConnectionCheckTask(outputStream);
        Utils.executeRunnableOnCachedAppThread("UptickSocketClient", clientDataHandler);
        Utils.executeRunnableOnCachedAppThread("UptickConnectoinCheck", connectionCheckTask);

    }

    public void connect() throws Exception
    {
        if (reconnecting.get())
            return;
        if (! connected.getAndSet(true))
        {

            initConnection();
            startDataHandlerAncConnectionCheck();
            log.info("Connected to uptick server " + host + ":" + port);

        }
    }

    private void checkConnected() throws Exception {
        if (! connected.get())
            throw new Exception("Uptick socket " + host + ":" + port + " not connected");
        if (! socket.isConnected())
        {
            throw new Exception("Uptick socket " + host + ":" + port + " not connected...starting reconnect thread");
        }
    }

    public void send(final UptickMessage message) throws Exception
    {
        send(message, true);
    }



    private synchronized void send(final UptickMessage message, boolean checkConnected) throws Exception
    {
        if (checkConnected)
        {
            checkConnected();
        }
        if (log.isDebugEnabled())
        {
            log.debug("SEDING MESSAAGE->" + message.getMessageType() + " : " + message);
        }
        final byte[] data = serializer.toByteArray(message);
        if (log.isDebugEnabled())
        {
            log.info("Sending data of length " + data.length + " msg " + message + " to uptick");
        }
        outputStream.write(data);
        outputStream.flush();
    }

    private final /* inner */ class ClientDataHandler implements Runnable
    {
        private final DataInputStream inputStream;
        private final UptickMessageHandler messageHandler;
        private boolean running = true;

        ClientDataHandler(final DataInputStream inputStream, final UptickMessageHandler messageHandler)
        {
            this.inputStream = inputStream;
            this.messageHandler = messageHandler;
        }

        public void stop() {
            running = false;
        }

        @Override
        public void run()
        {
            while (running)
            {
                try
                {
                    if (log.isDebugEnabled())
                    {
                        log.debug("Uptick client reading message");
                    }
                    final UptickMessage message;
                    message = serializer.deserialize(inputStream);
                    if (log.isDebugEnabled())
                    {
                        log.debug("Got message " + message);
                    }
                    messageHandler.handleMessage(message);
                }

                catch (Throwable t)
                {
                    if (running)
                    {
                        log.error("Error in client data handler thread " + t.getMessage(), t);
                        running = false;
                        startReconnectLoop();
                        break;
                    }
                }
            }
            log.info("Client data handler for uptick server " + host + ":" + port + " exiting");
        }
    }
}


