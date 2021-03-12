package uptick.messages;


import org.apache.log4j.Logger;
import uptick.UptickMessageInfo;
import uptick.UptickMessageType;

public final class GetMetadataResponse extends BaseUptickMessage
{
    private static final Logger log = Logger.getLogger(GetMetadataResponse.class);

    public GetMetadataResponse(final UptickMessageInfo messageInfo)
    {
        super(UptickMessageType.GetMetadataResponse, messageInfo);
    }
}
