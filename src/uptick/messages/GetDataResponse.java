package uptick.messages;


import org.apache.log4j.Logger;
import uptick.UptickMessageBody;
import uptick.UptickMessageInfo;
import uptick.UptickMessageType;

public final class GetDataResponse extends BaseUptickMessage
{
    private static final Logger log = Logger.getLogger(GetDataResponse.class);


    public GetDataResponse(final UptickMessageInfo messageHeader,
                           final UptickMessageBody body)
    {
        super(UptickMessageType.GetDataResponse, messageHeader, body);

    }




    @Override
    public String toString()
    {
        return reportToString();
    }
}
