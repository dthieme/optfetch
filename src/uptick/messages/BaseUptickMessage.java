package uptick.messages;


import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import uptick.*;

public abstract class BaseUptickMessage implements UptickMessage
{
    private static final Logger log = Logger.getLogger(BaseUptickMessage.class);
    protected static final boolean OnlyPrintFirsPage = false;
    protected final DateTime timestamp;
    protected final UptickMessageType messageType;
    protected final boolean includesBody;
    protected final UptickMessageInfo messageInfo;
    protected final UptickMessageBody body;

    public BaseUptickMessage(final UptickMessageType messageType,
                             final UptickMessageInfo messageInfo)
    {
        this(messageType, messageInfo, UptickMessageBody.NoBody);
    }

    public BaseUptickMessage(final UptickMessageType messageType,
                             final UptickMessageInfo messageInfo,
                             final UptickMessageBody body)
    {
        this.messageType = messageType;
        this.messageInfo = messageInfo;
        this.timestamp = DateTime.now();
        this.includesBody = messageType.includesBody();
        this.body = body;
    }


    public String getTopic()
    {
        return messageInfo.getTopic();
    }


    @Override
    public UptickMessageInfo getMessageInfo()
    {
        return messageInfo;
    }

    @Override
    public UptickMessageBody getMessageBody()
    {
        return body;
    }

    @Override
    public UptickMessageType getMessageType()
    {
        return messageType;
    }

    @Override
    public boolean includesBody()
    {
        return includesBody;
    }

    @Override
    public DateTime getTimestamp()
    {
        return timestamp;
    }






    protected String reportToString()
    {
        final StringBuilder buf = new StringBuilder();

        buf.append("Report topic ").append(messageInfo.getTopic()).append(" timestamp ").append(Utils.formatTimestamp(timestamp));
        buf.append(" metadata:\n");
        for (UptickMetadataEntry entry : messageInfo.getMetadata().getMetadataEntries())
        {
            buf.append("\t").append(entry).append("\n");
        }
        if (includesBody)
        {
            /*
            buf.append("Report, total pages ").append(body.).size()).append("\n");
            int pageNum = 1;
            for (UptickPage page : body.getDataPages())
            {
                buf.append("\tData page ").append(pageNum).append("\n");
                int rowNum = 1;
                for (Object[] row : page.getData())
                {
                    buf.append("\t\tRow ").append(rowNum).append(" : " ).append(Arrays.toString(row)).append("\n");

                    if (OnlyPrintFirsPage)
                    {
                        buf.append("Omitting " + (row.length - 1) + " additional pages\n");
                        break;
                    }

                }
                if (OnlyPrintFirsPage)
                    break;

            }

             */
            buf.append(Utils.toJson(body));
        }
        return buf.toString();
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder("BaseUptickMessage{");
        sb.append("timestamp=").append(timestamp);
        sb.append(", messageType=").append(messageType);
        sb.append(", includesBody=").append(includesBody);
        sb.append(", messageInfo=").append(messageInfo);
        sb.append(", body=").append(body);
        sb.append('}');
        return sb.toString();
    }
}
