package uptick.messages;


import org.apache.log4j.Logger;
import uptick.UptickMessageInfo;
import uptick.UptickMessageType;

public final class UnsubscribeRequest extends BaseUptickMessage
{
    private static final Logger log = Logger.getLogger(UnsubscribeRequest.class);


    public UnsubscribeRequest(final UptickMessageInfo messageHeader)
    {
        super(UptickMessageType.UnsubscribeRequest, messageHeader);
    }


}
