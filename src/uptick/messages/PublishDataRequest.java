package uptick.messages;


import org.apache.log4j.Logger;
import uptick.UptickMessageBody;
import uptick.UptickMessageInfo;
import uptick.UptickMessageType;

public final class PublishDataRequest extends BaseUptickMessage
{
    private static final Logger log = Logger.getLogger(PublishDataRequest.class);


    public PublishDataRequest(final UptickMessageInfo messageHeader,
                              final UptickMessageBody body)
    {
        super(UptickMessageType.PublishDataRequest, messageHeader, body);

    }


    @Override
    public String toString()
    {
        return reportToString();
    }
}
