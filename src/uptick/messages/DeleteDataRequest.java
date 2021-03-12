package uptick.messages;


import org.apache.log4j.Logger;
import uptick.UptickMessageInfo;
import uptick.UptickMessageType;

public final class DeleteDataRequest extends BaseUptickMessage
{
    private static final Logger log = Logger.getLogger(DeleteDataRequest.class);


    public DeleteDataRequest(final UptickMessageInfo messageHeader)
    {
        super(UptickMessageType.DeleteDataRequest, messageHeader);

    }


}
