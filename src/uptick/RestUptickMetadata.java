package uptick;


import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class RestUptickMetadata
{
    private static final Logger log = Logger.getLogger(RestUptickMetadata.class);
    private List<List<String>> dataList;

    public static String toJson(final UptickMetadata metadata)
    {
        final List<List<Object>> dataList = new ArrayList<>(metadata.getMetadataEntries().size());
        for (UptickMetadataEntry entry : metadata.getMetadataEntries())
        {
            final List<Object> entryAsStringList = Arrays.asList(entry.getFieldName(),
                                                                 entry.getFieldValue().toString(),
                                                                 entry.getMetadataType().getName());
            dataList.add(entryAsStringList);
        }
        return Utils.toJson(dataList);
    }

    public static UptickMetadata toObj(final String json)
    {
        final String[][] dataArr = Utils.fromJson(json, String[][].class);
        final List<UptickMetadataEntry> entryList = new ArrayList<>(dataArr.length);

        for (String[] d : dataArr)
        {
            final String name = d[0];
            final String value = d[1];
            final UptickMetadataType type = UptickMetadataType.fromStringChecked(d[2]);
            entryList.add(new UptickMetadataEntry(name, value, type));
        }
        return new UptickMetadata(entryList);
    }

    public List<List<String>> getDataList()
    {
        return dataList;
    }

    public void setDataList(final List<List<String>> dataList)
    {
        this.dataList = dataList;
    }
}
