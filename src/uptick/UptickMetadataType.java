package uptick;

import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.Map;

public enum UptickMetadataType
{
    String("String"),
    Date("DateTime_Iso8601"),
    Number("Number"),
    Boolean("Boolean"),
    Unknown("Unknown");

    private static final Map<String, UptickMetadataType> map = new HashMap<>(UptickMetadataType.values().length);
    static
    {
        for (UptickMetadataType metadataType : UptickMetadataType.values())
        {
            map.put(metadataType.name, metadataType);
        }
    }
    private final String name;

    UptickMetadataType(final String name)
    {
        this.name = name;
    }

    public java.lang.String getName()
    {
        return name;
    }

    public static UptickMetadataType fromData(final Object data)
    {
        if (data == null)
        {
            return UptickMetadataType.Unknown;
        }
        if (data instanceof String)
        {
            return UptickMetadataType.String;
        }
        else if (data instanceof Number)
        {
            return UptickMetadataType.Number;
        }
        else if (data instanceof DateTime)
        {
            return UptickMetadataType.Date;
        }
        else if (data instanceof Boolean)
        {
            return UptickMetadataType.Boolean;
        }
        else
        {
           return UptickMetadataType.Unknown;
        }
    }

    public static UptickMetadataType fromStringChecked(final String name)
    {
        final UptickMetadataType type = map.get(name);
        if (type == null)
            throw new IllegalArgumentException("Unknown uptick metadata type "+ name);
        return type;
    }
}
