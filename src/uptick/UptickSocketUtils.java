package uptick;


import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class UptickSocketUtils
{
    private static final Logger log = Logger.getLogger(UptickSocketUtils.class);


    public static List<List<String>> convertData(final Object[][] array)
    {

        final List<List<String>> ret = new ArrayList<>();
        for (int i = 0; i < array.length; i++)
        {
            List<String> data = new ArrayList<String>();
            for (int j = 0; j < array[i].length; j++)
            {
                final Object o = array[i][j];
                final String str = o == null ? "null" : o.toString();
                data.add(str);
            }
            ret.add(data);
        }
        return ret;
    }

    public static List<List<String>> convertLogData(final Object[][] array)
    {

        final List<List<String>> ret = new ArrayList<>();
        for (int i = 0; i < array.length; i++)
        {
            List<String> data = new ArrayList<String>();
            for (int j = 0; j < array[i].length; j++)
            {
                final String str;
                if (j == 0)
                {
                    str = Utils.formatTxTime(DateTime.now());
                }
                else
                {
                    final Object o = array[i][j];
                    str = o == null ? "null" : o.toString();
                }
                data.add(str);
            }
            ret.add(data);
        }
        return ret;
    }




}
