package uptick.messages;


import org.apache.log4j.Logger;
import uptick.UptickMessageInfo;
import uptick.UptickMessageType;

public final class UnsubscribeResponse extends BaseUptickMessage
{
    private static final Logger log = Logger.getLogger(UnsubscribeResponse.class);


    public UnsubscribeResponse(final UptickMessageInfo messageHeader)
    {
        super(UptickMessageType.SubscribeRequest, messageHeader);
    }


}
