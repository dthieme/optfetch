package uptick;


import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


public final class Utils
{
    private static final Logger log = Logger.getLogger(Utils.class);
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().serializeSpecialFloatingPointValues().create();
    private static final DateTimeFormatter timestampFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private static final DateTimeFormatter txTimeFormatter = DateTimeFormat.forPattern("MM/dd/yyyy hh:mm:ss a");
    private static final Gson noPrettyPrintGson = new GsonBuilder().serializeSpecialFloatingPointValues().create();


    public static String toJson(final Object o)
    {
        return gson.toJson(o);
    }

    public static String formatTimestamp(final DateTime d)
    {
        return timestampFormatter.print(d);

    }

    public static <T> T fromJson(final String json, final Class<T> cls)
    {
        try
        {
            return gson.fromJson(json, cls);
        }
        catch (Throwable t)
        {
            try
            {
                log.error("Error deserializing json " + t.getMessage(), t);
            }
            catch (Throwable t2)
            {

            }
            throw t;
        }
    }

    public static String formatTxTime(final DateTime dateTime)
    {
        return txTimeFormatter.print(dateTime);
    }

    public static String toFlatJson(final Object o)
    {
        return noPrettyPrintGson.toJson(o);
    }

    public static byte[] toUtf8(final String str)
    {
        return str.getBytes(Charsets.UTF_8);
    }

    public static String fromUtf8(final byte[] bytes)
    {
        return new String(bytes, Charsets.UTF_8);
    }

    public static byte[] compress(final byte[] data) throws Exception
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);
        GZIPOutputStream gzip = new GZIPOutputStream(bos);
        gzip.write(data);
        gzip.close();
        final byte[] compressed = bos.toByteArray();
        bos.close();
        return compressed;
    }

    public static byte[] decompress(final byte[] compressed) throws Exception
    {

        final ByteArrayInputStream bis = new ByteArrayInputStream(compressed);
        final GZIPInputStream gis = new GZIPInputStream(bis);
        final List<Byte> bytes = new ArrayList<>();
        int data = gis.read();
        while(data != -1){
            //do something with data
            bytes.add((byte)data);
            data = gis.read();

        }
        final byte[] byteArr = new byte[bytes.size()];
        for (int i = 0; i < bytes.size(); i++) {
            byteArr[i] = bytes.get(i);
        }
        return byteArr;
    }

    public static void executeRunnableOnCachedAppThread(final String taskName, final Runnable runnable)
    {

        new Thread(runnable, taskName).start();
    }
}
