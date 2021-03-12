package uptick;


import org.apache.log4j.Logger;
import org.joda.time.DateTime;

public final class UptickMetadataEntry<T>
{
    private static final Logger log = Logger.getLogger(UptickMetadataEntry.class);
    private final String fieldName;
    private final T fieldValue;
    private final UptickMetadataType metadataType;

    public UptickMetadataEntry(final String fieldName,
                               final T fieldValue,
                               final UptickMetadataType metadataType)
    {
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        this.metadataType = metadataType;
    }

    public static UptickMetadataEntry<String> newEntry(final UptickMetadataField entry, final String data)
    {
        return new UptickMetadataEntry<>(entry.getFieldName(), data, UptickMetadataType.String);
    }

    public static UptickMetadataEntry<Number> newEntry(final UptickMetadataField entry, final Number number)
    {
        return new UptickMetadataEntry<>(entry.getFieldName(), number, UptickMetadataType.Number);
    }

    public static UptickMetadataEntry<DateTime> newEntry(final UptickMetadataField entry, final DateTime dateTime)
    {
        return new UptickMetadataEntry<>(entry.getFieldName(), dateTime, UptickMetadataType.Date);
    }

    public static UptickMetadataEntry<Boolean> newEntry(final UptickMetadataField entry, final boolean bool)
    {
        return new UptickMetadataEntry<>(entry.getFieldName(), bool, UptickMetadataType.Boolean);
    }

    public String getFieldValueAsString() {
        return fieldValue == null ? "null" : fieldValue.toString();
    }

    public String getFieldName()
    {
        return fieldName;
    }

    public T getFieldValue()
    {
        return fieldValue;
    }

    public UptickMetadataType getMetadataType()
    {
        return metadataType;
    }


    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder("UptickMetadataEntry{");
        sb.append("fieldName='").append(fieldName).append('\'');
        sb.append(", fieldValue='").append(fieldValue).append('\'');
        sb.append(", metadataType=").append(metadataType);
        sb.append('}');
        return sb.toString();
    }
}
