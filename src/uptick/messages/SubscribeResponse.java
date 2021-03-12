package uptick.messages;


import org.apache.log4j.Logger;
import uptick.UptickMessageInfo;
import uptick.UptickMessageType;

public final class SubscribeResponse extends BaseUptickMessage
{
    private static final Logger log = Logger.getLogger(SubscribeResponse.class);


    public SubscribeResponse(final UptickMessageInfo messageHeader)
    {
        super(UptickMessageType.SubscribeResponse, messageHeader);
    }



}
