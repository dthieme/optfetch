package uptick.messages;


import org.apache.log4j.Logger;
import uptick.UptickMessageInfo;
import uptick.UptickMessageType;

public final class DataUpdateNotification extends BaseUptickMessage
{
    private static final Logger log = Logger.getLogger(DataUpdateNotification.class);

    public DataUpdateNotification(final UptickMessageInfo messageInfo)
    {
        super(UptickMessageType.DataUpdateNotification, messageInfo);
    }
}
