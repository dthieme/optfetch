package uptick.messages;


import org.apache.log4j.Logger;
import uptick.UptickMessageInfo;
import uptick.UptickMessageType;

public final class SubscribeRequest extends BaseUptickMessage
{
    private static final Logger log = Logger.getLogger(SubscribeRequest.class);


    public SubscribeRequest(final UptickMessageInfo messageHeader)
    {
        super(UptickMessageType.SubscribeRequest, messageHeader);
    }




}
