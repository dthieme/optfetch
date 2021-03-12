package uptick;


import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.List;

public final class UptickMessageBody
{
    private static final Logger log = Logger.getLogger(UptickMessageBody.class);
    public static final UptickMessageBody NoBody = new UptickMessageBody(Collections.emptyList());
    private List<Object[][]> Data;

    public UptickMessageBody() {

    }

    public List<Object[][]> getData()
    {
        return Data;
    }

    public Object[][] getFirstPage() {
        return Data.size() > 0 ? Data.get(0) : new Object[0][0];
    }

    public static UptickMessageBody newOnePageBody(final List<List<Object>> rows)
    {
        final Object[][] arr = new Object[rows.size()][];
        for (int i = 0; i < rows.size(); i++)
        {
            final List<?> rowData = rows.get(i);
            final Object[] row = new Object[rowData.size()];
            for (int j = 0; j < rowData.size(); j++)
            {
                row[j] = rowData.get(j);
            }
            arr[i] = row;
        }
        final List<Object[][]> dataList = Collections.singletonList(arr);
        return new UptickMessageBody(dataList);
    }

    public static String toJson(final UptickMessageBody body)
    {
        return Utils.toFlatJson(body);
    }


    public UptickMessageBody(final List<Object[][]> data)
    {
        Data = data;
    }

    public String toFullStr()
    {
        final StringBuilder buf = new StringBuilder();
        int pageNum = 1;
        for (Object[][] page : Data)
        {
            buf.append("\n----Page ").append(pageNum).append("----------\n");
            for (Object[] row : page)
            {
                for (int i = 0; i < row.length; i++)
                {
                    buf.append(row[i]);
                    if (i != (row.length - 1))
                        buf.append(",");
                }
                buf.append("\n");
            }
            pageNum++;
        }
        return buf.toString();
    }

    @Override
    public String toString()
    {
        /*
        final StringBuilder sb = new StringBuilder("UptickMessageBody{");
        final int numPages = Data == null ? 0 : Data.size();
        sb.append("Data Num Pages=").append(numPages);
        sb.append('}');
        return sb.toString();
        */
        return toFullStr();
    }

}
