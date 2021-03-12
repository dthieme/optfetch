package uptick;


import org.apache.log4j.Logger;
import uptick.messages.UptickMessage;

public final class LogUptickMessageHandler implements UptickMessageHandler
{
    private static final Logger log = Logger.getLogger(LogUptickMessageHandler.class);

    @Override
    public void handleMessage(final UptickMessage message)
    {
        log.info("Got uptick message "  + message);
    }
}
