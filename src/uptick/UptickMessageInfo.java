package uptick;


import org.apache.log4j.Logger;

public final class UptickMessageInfo
{

    private static final Logger log = Logger.getLogger(UptickMessageInfo.class);
    private final short headerVersion;
    private final short headerSize;
    private final String headerKey;
    private final short topicSize;
    private final String topic;
    private final short messageType;
    private final byte metadataCompressionType;
    private final short metadataSize;
    private final UptickMetadata metadata;
    private final short bodyType;
    private final byte bodySerializationType;
    private final byte bodyCompressionType;
    private final byte bodyEncryptionType;
    private final int bodySize;


    public UptickMessageInfo(final short headerVersion,
                             final short headerSize,
                             final String headerKey,
                             final short topicSize,
                             final String topic,
                             final short messageType,
                             final byte metadataCompressionType,
                             final short metadataSize,
                             final UptickMetadata metadata)
    {
        this(headerVersion,
             headerSize,
             headerKey,
             topicSize,
             topic,
             messageType,
             metadataCompressionType,
             metadataSize,
             metadata,
             (short)0,
             (byte)0,
             (byte)0,
             (byte)0,
             0);
    }


    public UptickMessageInfo(final short headerVersion,
                             final short headerSize,
                             final String headerKey,
                             final short topicSize,
                             final String topic,
                             final short messageType,
                             final byte metadataCompressionType,
                             final short metadataSize,
                             final UptickMetadata metadata,
                             final short bodyType,
                             final byte bodySerializationType,
                             final byte bodyCompressionType,
                             final byte bodyEncryptionType,
                             final int bodySize)
    {
        this.headerVersion = headerVersion;
        this.headerSize = headerSize;
        this.headerKey = headerKey;
        this.topicSize = topicSize;
        this.topic = topic;
        this.messageType = messageType;
        this.metadataCompressionType = metadataCompressionType;
        this.metadataSize = metadataSize;
        this.metadata = metadata;
        this.bodyType = bodyType;
        this.bodySerializationType = bodySerializationType;
        this.bodyCompressionType = bodyCompressionType;
        this.bodyEncryptionType = bodyEncryptionType;
        this.bodySize = bodySize;

    }

    public UptickMessageInfo cloneWithHeaderSize(final short newHeaderSize)
    {
        return new UptickMessageInfo(headerVersion,
                                     newHeaderSize,
                                     headerKey,
                                     topicSize,
                                     topic,
                                     messageType,
                                     metadataCompressionType,
                                     metadataSize,
                                     metadata,
                                     bodyType,
                                     bodySerializationType,
                                     bodyCompressionType,
                                     bodyEncryptionType,
                                     bodySize);
    }


    public short getMetadataSize()
    {
        return metadataSize;
    }

    public short getTopicSize()
    {
        return topicSize;
    }

    public short getHeaderVersion()
    {
        return headerVersion;
    }

    public short getHeaderSize()
    {
        return headerSize;
    }

    public String getHeaderKey()
    {
        return headerKey;
    }

    public String getTopic()
    {
        return topic;
    }

    public short getMessageType()
    {
        return messageType;
    }

    public byte getMetadataCompressionType()
    {
        return metadataCompressionType;
    }

    public UptickMetadata getMetadata()
    {
        return metadata;
    }

    public short getBodyType()
    {
        return bodyType;
    }

    public byte getBodySerializationType()
    {
        return bodySerializationType;
    }

    public byte getBodyCompressionType()
    {
        return bodyCompressionType;
    }

    public byte getBodyEncryptionType()
    {
        return bodyEncryptionType;
    }

    public int getBodySize()
    {
        return bodySize;
    }



    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder("UptickMessageInfo{");
        sb.append("headerVersion=").append(headerVersion);
        sb.append(", headerSize=").append(headerSize);
        sb.append(", headerKey='").append(headerKey).append('\'');
        sb.append(", topicSize=").append(topicSize);
        sb.append(", topic='").append(topic).append('\'');
        sb.append(", messageType=").append(messageType);
        sb.append(", metadataCompressionType=").append(metadataCompressionType);
        sb.append(", metadataSize=").append(metadataSize);
        sb.append(", metadata=").append(metadata);
        sb.append(", bodyType=").append(bodyType);
        sb.append(", bodySerializationType=").append(bodySerializationType);
        sb.append(", bodyCompressionType=").append(bodyCompressionType);
        sb.append(", bodyEncryptionType=").append(bodyEncryptionType);
        sb.append(", bodySize=").append(bodySize);
        sb.append('}');
        return sb.toString();
    }
}
