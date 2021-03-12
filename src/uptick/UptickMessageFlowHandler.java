package uptick;


import org.apache.log4j.Logger;
import uptick.messages.GetDataRequest;
import uptick.messages.UptickMessage;

public final class UptickMessageFlowHandler implements UptickMessageHandler
{
    private static final Logger log = Logger.getLogger(UptickMessageFlowHandler.class);
    private final UptickSocketClient client;

    public UptickMessageFlowHandler(final UptickSocketClient client)
    {
        this.client = client;
    }

    @Override
    public void handleMessage(final UptickMessage message)
    {
        final UptickMessageType messageType = message.getMessageType();
        if (messageType == UptickMessageType.DataUpdateNotification) // || messageType == UptickMessageType.SubscribeResponse)
        {
            try
            {
                final String topic = message.getTopic();
                //if (log.isDebugEnabled())
                //{
                    log.info("Got response message " + message + " on topic  " + topic + ", sending get data request");
                //}
                final GetDataRequest getDataRequest = UptickMessageFactory.getInstance().newGetDataRequest(topic);
                client.send(getDataRequest);
            }
            catch (Throwable t)
            {
                log.error("Error responding to get data request notification " + message + t.getMessage(), t);
            }
        }

    }
}
