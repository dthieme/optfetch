package uptick;


import java.util.HashMap;
import java.util.Map;

public enum UptickMessageType
{
    Heartbeat(1),
    SessionTerminateNotification(999),
    GetDataRequest(120),
    GetMetadataRequest(130),
    SubscribeRequest(140),
    UnsubscribeRequest(150),
    DeleteDataRequest(160),
    PublishDataRequest(170, true),
    AuthenticationRequest(300),
    GetDataResponse(220, true),
    GetMetadataResponse(230),
    SubscribeResponse(240),
    UnsubscribeResponse(250),
    DeleteDataResponse(260),
    PublishDataResponse(270),
    DataUpdateNotification(280),
    AuthenticationResponse(400);

    private static final Map<Short, UptickMessageType> map = new HashMap<>(UptickMessageType.values().length);
    static
    {
        for (UptickMessageType t : UptickMessageType.values())
        {
            map.put(t.messageType, t);
        }
    }

    private final short messageType;
    private final boolean includesBody;

    UptickMessageType(final int t)
    {
        this((short)t, false);
    }
    UptickMessageType(final int t, final boolean includesBody)
    {
        this((short)t, includesBody);
    }


    public short getMessageType()
    {
        return messageType;
    }

    UptickMessageType(final short messageType, final boolean includesBody)
    {
        this.messageType = messageType;
        this.includesBody = includesBody;
    }

    public boolean includesBody()
    {
        return includesBody;
    }

    public static UptickMessageType fromCodeChecked(final short type)
    {
        final UptickMessageType t = map.get(type);
        if (t == null)
            throw new IllegalArgumentException("Invalid message type " + type);
        return t;
    }
}
