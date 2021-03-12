package uptick;


import org.apache.log4j.Logger;

import java.io.DataInput;
import java.io.DataOutput;

public final class UptickMessageBodySerializer extends BaseBinarySerializer<UptickMessageBody>
{
    private static final Logger log = Logger.getLogger(UptickMessageBodySerializer.class);

    private final int bodySize;
    private final byte compressionType;


    public UptickMessageBodySerializer(final int bodySize, final byte compressionType)
    {
        this.bodySize = bodySize;
        this.compressionType = compressionType;
    }

    @Override
    protected int writeFields(final DataOutput dataOutput, final UptickMessageBody data) throws Exception
    {

        final String bodyJson = UptickMessageBody.toJson(data);
        if (log.isDebugEnabled())
        {
            log.debug("Writing body json " + bodyJson);
        }
        byte[] bodyBytes = Utils.toUtf8(bodyJson);
        if (compressionType == UptickMessageFactory.CompressionTypeGzip)
        {
            bodyBytes = UptickSerializer.compress(bodyBytes);
        }
        int length = bodyBytes.length;
        writeFixedLengthBufer(dataOutput, bodyBytes, length);
        return length;

    }

    @Override
    protected UptickMessageBody readFields(final DataInput dataInput) throws Exception
    {
        if (bodySize == 0)
            throw new IllegalArgumentException("Unspecified body size");
        byte[] bodyBytes = readFixedLengthBytes(dataInput, bodySize);
        if (compressionType == UptickMessageFactory.CompressionTypeGzip)
        {
            bodyBytes = UptickSerializer.decompress(bodyBytes);
        }
        final String json = Utils.fromUtf8(bodyBytes);
        if (log.isDebugEnabled())
        {
            log.debug("Deser json " + json);
        }
        return Utils.fromJson(json, UptickMessageBody.class);
    }
}
