package uptick;


import org.apache.log4j.Logger;

import java.util.*;

public final class UptickMetadata
{
    private static final Logger log = Logger.getLogger(UptickMetadata.class);
    private final List<UptickMetadataEntry> metadataEntries;
    private final Map<String, UptickMetadataEntry> entryMap;

    public UptickMetadata(final UptickMetadataEntry... entries)
    {
        this(Arrays.asList(entries));
    }

    public UptickMetadata(final List<UptickMetadataEntry> metadataEntries)
    {
        this.metadataEntries = metadataEntries;
        this.entryMap = new HashMap<>(metadataEntries.size());
        for (UptickMetadataEntry entry : metadataEntries)
        {
            final String name = entry.getFieldName();
            entryMap.put(name, entry);
        }
    }

    public String toJson()
    {
        return toJson(false);
    }

    public String toJson(final boolean prettyPrint)
    {
        final List<List<Object>> metadata = new ArrayList<>(metadataEntries.size());
        for (UptickMetadataEntry entry : metadataEntries)
        {
            final List<Object> list = Arrays.asList(entry.getFieldName(), entry.getFieldValue(), entry.getMetadataType().getName());
            metadata.add(list);
        }
        if (prettyPrint)
        {
            return Utils.toJson(metadata);
        }
        else
        {
            final String json = Utils.toFlatJson(metadata);
            return json;
        }
    }

    public UptickMetadataEntry getEntryValueByName(final String name)
    {
        return entryMap.get(name);
    }

    public List<UptickMetadataEntry> getMetadataEntries()
    {
        return metadataEntries;
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder("UptickMetadata{");
        sb.append("metadataEntries=\n");
        metadataEntries.forEach(s -> log.info(s));
        sb.append('}');
        return sb.toString();
    }
}
