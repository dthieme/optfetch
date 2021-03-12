package uptick;


import org.apache.log4j.Logger;
import uptick.messages.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class UptickMessageFactory
{
    private static final Logger log = Logger.getLogger(UptickMessageFactory.class);
    private static final String HeartbeatTopic = "DV/Heartbeat";
    private static final UptickMessageInfoSerializer MessageInfoSerializer = new UptickMessageInfoSerializer();;
    private static final UptickMessageFactory instance = new UptickMessageFactory();
    static final short HeaderVersion = 1;
    static final String HeaderKey = "#UpTick!";
    static final byte CompressionTypeNone = 0;
    static final byte CompressionTypeGzip = 2;
    static final short UptickXlBodyType = 3;
    static final byte BodySerializationTye = 3;
    static final byte BodyEncryptionTypeNone = 0;
    private static final int CompressionThreshold = 500;
    private static final List<UptickMetadataEntry> DefaultMetadataEntries = Collections.singletonList(
            UptickMetadataEntry.newEntry(UptickMetadataField.SenderUsername,
                                         (String) UptickMetadataField.SenderUsername.getDefaultValue()));


    public static UptickMessageFactory getInstance() {
        return instance;
    }

    public DeleteDataRequest newDeleteDataRequest(final String topic) throws Exception {
        final UptickMessageInfo header = getInfo(UptickMessageType.DeleteDataRequest, topic);
        return (DeleteDataRequest) fromMessageInfo(header);
    }

    public SubscribeRequest newSubscribeRequest(final String topic) throws Exception {
        final UptickMessageInfo header = getInfo(UptickMessageType.SubscribeRequest, topic);
        return (SubscribeRequest)fromMessageInfo(header);
    }

    public GetDataRequest newGetDataRequest(final String topic) throws Exception {
        final UptickMessageInfo header = getInfo(UptickMessageType.GetDataRequest, topic);
        return (GetDataRequest)fromMessageInfo(header);
    }

    public PublishDataRequest newPublishDataRequest(final String topic,
                                                    final List<UptickMetadataEntry> metadataEntries,
                                                    final UptickMessageBody body) throws Exception
    {
        return newPublishDataRequest(topic, metadataEntries, body, false);
    }

    public PublishDataRequest newPublishDataRequest(final String topic,
                                                    final List<UptickMetadataEntry> metadataEntries,
                                                    final UptickMessageBody body,
                                                    final boolean isMarginRequest) throws Exception
    {

        final boolean includeDefaultMetadata = true;
        final Pair<UptickMessageInfo, UptickMessageBody> data = getInfo(UptickMessageType.PublishDataRequest,
                                                                        topic,
                                                                        metadataEntries,
                                                                        body,
                                                                        includeDefaultMetadata);
        return new PublishDataRequest(data.getVal1(), data.getVal2());
    }


    public Heartbeat newHeartbeat() throws Exception {
        final UptickMessageInfo header = getInfo(UptickMessageType.Heartbeat, HeartbeatTopic);
        return (Heartbeat)fromMessageInfo(header);
    }

    public GetMetadataRequest newGetMetadataRequest(final String topic) throws Exception {
        final UptickMessageInfo header = getInfo(UptickMessageType.GetMetadataRequest, topic);
        return (GetMetadataRequest)fromMessageInfo(header);
    }

    public UnsubscribeRequest newUnsubscribeRequest(final String topic) throws Exception
    {
        final UptickMessageInfo header = getInfo(UptickMessageType.UnsubscribeRequest, topic);
        return (UnsubscribeRequest)fromMessageInfo(header);
    }

    UptickMessage fromMessageInfo(final UptickMessageInfo messageInfo) throws Exception
    {
        return fromMessageInfo(messageInfo, null);
    }

    UptickMessage fromMessageInfo(final UptickMessageInfo messageInfo, final UptickMessageBody body) throws Exception
    {
        final UptickMessageType uptickMessageType = UptickMessageType.fromCodeChecked(messageInfo.getMessageType());
        switch (uptickMessageType)
        {
            case DataUpdateNotification:
                return new DataUpdateNotification(messageInfo);
            case DeleteDataRequest:
                return new DeleteDataRequest(messageInfo);
            case GetDataRequest:
                return new GetDataRequest(messageInfo);
            case GetDataResponse:
                return new GetDataResponse(messageInfo, body);
            case GetMetadataRequest:
                return new GetMetadataRequest(messageInfo);
            case GetMetadataResponse:
                return new GetMetadataResponse(messageInfo);
            case Heartbeat:
                return new Heartbeat(messageInfo);
            case PublishDataRequest:
                return new PublishDataRequest(messageInfo, body);
            case SessionTerminateNotification:
                return new SessionTerminateNotification(messageInfo);
            case SubscribeRequest:
                return new SubscribeRequest(messageInfo);
            case SubscribeResponse:
                return new SubscribeResponse(messageInfo);
            case UnsubscribeRequest:
                return new UnsubscribeRequest(messageInfo);
            case UnsubscribeResponse:
                return new UnsubscribeResponse(messageInfo);
            default:
                throw new Exception("Unimplemented handling of uptick message type " + uptickMessageType);
        }
    }

    byte getCompressionType(final int len)
    {
        return CompressionTypeGzip; //return len > CompressionThreshold ? CompressionTypeGzip : CompressionTypeNone;
    }

    byte getCompressionType(final String json) {
        return getCompressionType(json.length());
    }

    private UptickMessageInfo getInfo(final UptickMessageType messageType, final String topicName) throws Exception
    {
        return getInfo(messageType, topicName, Collections.emptyList(), UptickMessageBody.NoBody).getVal1();
    }

    private UptickMessageInfo getInfo(final UptickMessageType messageType,
                                      final String topicName,
                                      final List<UptickMetadataEntry> metadataEntries) throws Exception
    {
        return getInfo(messageType, topicName, metadataEntries, UptickMessageBody.NoBody).getVal1();
    }

    private Pair<UptickMessageInfo, UptickMessageBody> getInfo(final UptickMessageType messageType,
                                                               final String topicName,
                                                               final List<UptickMetadataEntry> metadataEntries,
                                                               final UptickMessageBody body) throws Exception
    {
        return getInfo(messageType, topicName,metadataEntries, body, true);
    }

    private Pair<UptickMessageInfo, UptickMessageBody> getInfo(final UptickMessageType messageType,
                                                               final String topicName,
                                                               final List<UptickMetadataEntry> metadataEntries,
                                                               final UptickMessageBody body,
                                                               final boolean includeDefaultMetadata) throws Exception
    {
        final List<UptickMetadataEntry> entries = includeDefaultMetadata ? new ArrayList<>(DefaultMetadataEntries) : new ArrayList<>(metadataEntries.size());
        entries.addAll(metadataEntries);
        final UptickMetadata metadata = new UptickMetadata(entries);

        final short topicSize = (short)Utils.toUtf8(topicName).length;
        final String metadataJson = RestUptickMetadata.toJson(metadata);

        final byte compressionType = getCompressionType(metadataJson);
        byte[] metadataBytes = Utils.toUtf8(metadataJson);
        if (compressionType == CompressionTypeGzip)
            metadataBytes = UptickSerializer.compress(metadataBytes);

        final UptickMessageInfo tmpHeader;
        if (!messageType.includesBody())
        {
            tmpHeader = new UptickMessageInfo(HeaderVersion,
                                              (short) 0,
                                              HeaderKey,
                                              topicSize,
                                              topicName,
                                              messageType.getMessageType(),
                                              compressionType,
                                              (short) metadataBytes.length,
                                              metadata);
        }
        else
        {
            final UptickMessageBodySerializer bodySerializer = new UptickMessageBodySerializer(0, CompressionTypeGzip);
            byte[] bodyBytes = bodySerializer.toByteArray(body);
            tmpHeader = new UptickMessageInfo(HeaderVersion,
                                              (short) 0,
                                              HeaderKey,
                                              topicSize,
                                              topicName,
                                              messageType.getMessageType(),
                                              compressionType,
                                              (short) metadataBytes.length,
                                              metadata,
                                              UptickXlBodyType,
                                              BodySerializationTye,
                                              CompressionTypeGzip,
                                              BodyEncryptionTypeNone,
                                              bodyBytes.length);
        }
        final byte[] headerBytes = MessageInfoSerializer.toByteArray(tmpHeader);
        final UptickMessageInfo header = tmpHeader.cloneWithHeaderSize((short)headerBytes.length);
        return new ImmutablePair<>(header, body);

    }



}

