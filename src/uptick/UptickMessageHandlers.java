package uptick;



import org.apache.log4j.Logger;
import uptick.messages.UptickMessage;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArraySet;

public final class UptickMessageHandlers implements UptickMessageHandler
{
    private static final Logger log = Logger.getLogger(UptickMessageHandlers.class);
    private final Collection<UptickMessageHandler> handlers = new CopyOnWriteArraySet<>();
    private static final UptickMessageHandlers instance = new UptickMessageHandlers();

    public static UptickMessageHandlers getInstance() {
        return instance;
    }

    private UptickMessageHandlers() {

    }



    public void addMessageHandler(final UptickMessageHandler handler)
    {
        handlers.add(handler);
    }

    public void removeMessageHandler(final UptickMessageHandler handler)
    {
        handlers.remove(handler);
    }

    @Override
    public void handleMessage(final UptickMessage message)
    {
        for (UptickMessageHandler handler : handlers)
        {
            handler.handleMessage(message);
        }
    }

}
