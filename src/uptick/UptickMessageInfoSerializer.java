package uptick;


import org.apache.log4j.Logger;

import java.io.DataInput;
import java.io.DataOutput;

public final class UptickMessageInfoSerializer extends BaseBinarySerializer<UptickMessageInfo>
{
    private static final Logger log = Logger.getLogger(UptickMessageInfoSerializer.class);


    @Override
    protected int writeFields(final DataOutput buffer, final UptickMessageInfo data) throws Exception
    {
        final boolean debug = UptickMessageType.fromCodeChecked(data.getMessageType()) != UptickMessageType.Heartbeat;
        if (debug && log.isDebugEnabled())
        {
            log.debug("Writing uptick info " + data);
        }
        int length = 0;
        length += write(buffer, data.getHeaderVersion());
        length += write(buffer, data.getHeaderSize());
        length += writeFixedLengthBufer(buffer, Utils.toUtf8(data.getHeaderKey()), 8);
        length += write(buffer, data.getTopicSize());
        length += writeFixedLengthBufer(buffer, Utils.toUtf8(data.getTopic()), data.getTopicSize());
        length += write(buffer, data.getMessageType());
        length += write(buffer, data.getMetadataCompressionType());
        if (debug && log.isDebugEnabled())
        {
            log.debug("Using metadata compression type " + data.getMetadataCompressionType());
        }
        final String json = RestUptickMetadata.toJson(data.getMetadata());
        if (debug && log.isDebugEnabled())
        {
            log.debug("Metadata json " + json);
        }
        byte[] metadataBytes = Utils.toUtf8(json);
        if (debug && log.isDebugEnabled())
        {
            log.debug("Metadata bytes before compression " + metadataBytes.length);
        }
        if (data.getMetadataCompressionType() == UptickMessageFactory.CompressionTypeGzip)
        {
            metadataBytes = UptickSerializer.compress(metadataBytes);
        }
        if (debug && log.isDebugEnabled())
        {
            log.debug("Metadata after before compression " + metadataBytes.length);
        }
        length += write(buffer, (short)metadataBytes.length);
        length += writeFixedLengthBufer(buffer, metadataBytes, metadataBytes.length);
        final UptickMessageType msgType = UptickMessageType.fromCodeChecked(data.getMessageType());
        if (msgType.includesBody())
        {

            length += write(buffer, data.getBodyType());
            length += write(buffer, data.getBodySerializationType());
            length += write(buffer, data.getBodyCompressionType());
            length += write(buffer, data.getBodyEncryptionType());
            length += write(buffer, data.getBodySize());

        }
        return length;
    }

/*

  final String bodyJson = UptickMessageBody.toJson(data);
        if (log.isDebugEnabled())
        {
            log.debug("Writing body json " + bodyJson);
        }
        byte[] bodyBytes = UptickMarketDataUtils.toUtf8(bodyJson);
        final byte compressionType = UptickMessageFactory.getInstance().getCompressionType(bodyJson);
        if (compressionType == UptickMessageFactory.CompressionTypeGzip)
        {
            bodyBytes = UptickSerializer.compress(bodyBytes);
        }
        int length = bodyBytes.length;
        writeFixedLengthBufer(dataOutput, bodyBytes, length);
        return length;
 */

    @Override
    protected UptickMessageInfo readFields(final DataInput buffer) throws Exception
    {

        final short headerVersion = readShort(buffer);
        if (log.isDebugEnabled())
        {
            log.info("Read header version " +headerVersion);
        }
        final short headerSize = readShort(buffer);
        if (log.isDebugEnabled())
        {
            log.info("Read header size  " + headerSize);
        }
        final String headerKey = Utils.fromUtf8(readFixedLengthBytes(buffer, 8));
        if (log.isDebugEnabled())
        {
            log.debug("Got header key " + headerKey);
        }
        final short topicLength = readShort(buffer);
        if (log.isDebugEnabled())
        {
            log.debug("Got topic length " + topicLength);
        }
        final String topic = Utils.fromUtf8(readFixedLengthBytes(buffer, topicLength));
        if (log.isDebugEnabled())
        {
            log.debug("Got topic " + topic);
        }
        final short messageType = readShort(buffer);
        if (log.isDebugEnabled())
        {
            log.debug("Got message type " + messageType);
        }
        final byte compressionType = readByte(buffer);
        if (log.isDebugEnabled())
        {
            log.debug("Got compression type " + compressionType);
        }

        final short metadataLength = readShort(buffer);
        if (log.isDebugEnabled())
        {
            log.debug("Got metadata length  " + metadataLength);
        }
        byte[] metadataBytes = readFixedLengthBytes(buffer, metadataLength);
        if (log.isDebugEnabled())
        {
            log.debug("Got metadata bytes " + metadataBytes.length);
        }
        if (compressionType == UptickMessageFactory.CompressionTypeGzip)
        {
            metadataBytes = UptickSerializer.decompress(metadataBytes);
            if (log.isDebugEnabled())
            {
                log.debug("Decompressed bytes to " + metadataBytes.length);
            }
        }

        final String metadataJson = Utils.fromUtf8(metadataBytes);
        if (log.isDebugEnabled())
        {
            log.debug("Got metadata json " + metadataJson);
        }
        final UptickMetadata metadata = RestUptickMetadata.toObj(metadataJson);
        if (log.isDebugEnabled())
        {
            log.debug("Got metadata " + metadataJson);
        }
        final UptickMessageType msgType = UptickMessageType.fromCodeChecked(messageType);
        if (msgType.includesBody())
        {
            if (log.isDebugEnabled())
            {
                log.debug("Reading body fields");
            }
            final short bodyType = readShort(buffer);
            if (log.isDebugEnabled())
            {
                log.debug("Got body type " + bodyType);
            }
            final byte bodySerializationType = readByte(buffer);
            if (log.isDebugEnabled())
            {
                log.debug("Got ser type " + bodySerializationType);
            }
            final byte bodyCompressionType = readByte(buffer);
            if (log.isDebugEnabled())
            {
                log.debug("Got body compression type " +bodyCompressionType);
            }
            final byte bodyEncryptionType = readByte(buffer);
            if (log.isDebugEnabled())
            {
                log.debug("Got encryption type  " + bodyEncryptionType);
            }
            final int bodySize = readInt(buffer);
            if (log.isDebugEnabled())
            {
                log.debug("Got body size " + bodySerializationType);
            }
            return new UptickMessageInfo(headerVersion,
                                         headerSize,
                                         headerKey,
                                         topicLength,
                                         topic,
                                         messageType,
                                         compressionType,
                                         metadataLength,
                                         metadata,
                                         bodyType,
                                         bodySerializationType,
                                         bodyCompressionType,
                                         bodyEncryptionType,
                                         bodySize);
        }
        else
        {
            return new UptickMessageInfo(headerVersion,
                                         headerSize,
                                         headerKey,
                                         topicLength,
                                         topic,
                                         messageType,
                                         compressionType,
                                         metadataLength,
                                         metadata);
        }


    }






}
