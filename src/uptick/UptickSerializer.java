package uptick;


import org.apache.log4j.Logger;
import uptick.messages.UptickMessage;

import java.io.DataInput;
import java.io.DataOutput;

public final class UptickSerializer extends BaseBinarySerializer<UptickMessage>
{
    private static final Logger log = Logger.getLogger(UptickSerializer.class);
    private static final UptickMessageInfoSerializer Serializer = new UptickMessageInfoSerializer();


    @Override
    protected int writeFields(final DataOutput buffer, final UptickMessage data) throws Exception {
        if (log.isDebugEnabled())
        {
            log.debug("Writing uptick message " + data.getMessageType() + " with header " + data.getMessageInfo() + " and body " + data
                    .getMessageBody());
        }
        int len = Serializer.writeFields(buffer, data.getMessageInfo());
        final int headerLen = len;
        final UptickMessageType messageType = UptickMessageType.fromCodeChecked(data.getMessageInfo().getMessageType());
        int bodyLen = 0;
        if (messageType.includesBody())
        {
            final UptickMessageBodySerializer bodySerializer = new UptickMessageBodySerializer(data.getMessageInfo().getBodySize(), data.getMessageInfo().getBodyCompressionType());
            if (log.isDebugEnabled())
            {
                log.debug("Serialising body of length " + data.getMessageInfo().getBodySize());
            }
            bodyLen = bodySerializer.serialize(buffer, data.getMessageBody());
            if (log.isDebugEnabled())
            {
                log.debug("Body len total " + bodyLen);
            }
            len += bodyLen;
        }
        if (log.isDebugEnabled())
        {
            log.debug("Wrote message of type " + data.getMessageType() + " with header len " + headerLen + " body len " + bodyLen);
        }
        return len;
    }

    @Override
    protected UptickMessage readFields(final DataInput buffer) throws Exception
    {
        final UptickMessageInfo messageInfo = Serializer.readFields(buffer);
        if (log.isDebugEnabled())
        {
            log.debug("Got incoming message fields " + messageInfo);
        }
        final UptickMessageType messageType = UptickMessageType.fromCodeChecked(messageInfo.getMessageType());
        UptickMessageBody body = null;
        if (messageType.includesBody())
        {
            final UptickMessageBodySerializer bodySerializer = new UptickMessageBodySerializer(messageInfo.getBodySize(), messageInfo.getBodyCompressionType());
            if (log.isDebugEnabled())
            {
                log.debug("Reading body " + messageInfo);
            }
            body = bodySerializer.deserialize(buffer);
        }
        return UptickMessageFactory.getInstance().fromMessageInfo(messageInfo, body);
    }




    public static byte[] compress(final byte[] input) throws Exception
    {
        return Utils.compress(input);
    }

    public static byte[] decompress(final byte[] input) throws Exception
    {
        //return ZipUtils.gunzipData(input);
        return Utils.decompress(input);
    }
}
