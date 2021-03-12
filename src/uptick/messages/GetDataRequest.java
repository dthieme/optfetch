package uptick.messages;


import org.apache.log4j.Logger;
import uptick.UptickMessageInfo;
import uptick.UptickMessageType;

public final class GetDataRequest extends BaseUptickMessage
{
    private static final Logger log = Logger.getLogger(GetDataRequest.class);


    public GetDataRequest(final UptickMessageInfo messageHeader)
    {
        super(UptickMessageType.GetDataRequest, messageHeader);

    }


}
