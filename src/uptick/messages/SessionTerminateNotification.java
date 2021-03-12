package uptick.messages;


import org.apache.log4j.Logger;
import uptick.UptickMessageInfo;
import uptick.UptickMessageType;

public final class SessionTerminateNotification extends BaseUptickMessage
{
    private static final Logger log = Logger.getLogger(SessionTerminateNotification.class);

    public SessionTerminateNotification(final UptickMessageInfo messageInfo)
    {
        super(UptickMessageType.SessionTerminateNotification, messageInfo);
    }
}
