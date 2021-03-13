package uptick.messages;


import org.apache.log4j.Logger;
import uptick.UptickMessageInfo;
import uptick.UptickMessageType;

public final class Heartbeat extends BaseUptickMessage
{
    private static final Logger log = Logger.getLogger(Heartbeat.class);

    public Heartbeat(final UptickMessageInfo messageInfo)
    {
        super(UptickMessageType.Heartbeat, messageInfo);
    }
}
