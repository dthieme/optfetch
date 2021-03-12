package uptick.messages;


import org.apache.log4j.Logger;
import uptick.UptickMessageInfo;
import uptick.UptickMessageType;

public final class GetMetadataRequest extends BaseUptickMessage
{
    private static final Logger log = Logger.getLogger(GetMetadataRequest.class);


    public GetMetadataRequest(final UptickMessageInfo messageHeader)
    {
        super(UptickMessageType.GetMetadataRequest, messageHeader);
    }


}
