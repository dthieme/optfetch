package uptick;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.*;


public abstract class BaseBinarySerializer<T> implements BinarySerializer<T>
{
    private static final Logger log = Logger.getLogger(BaseBinarySerializer.class);
    private static final byte NullByte = 0x00;

    @Override
    public T deserialize(final DataInput buffer) throws Exception
    {
        return readFields(buffer);
    }

    @Override
    public int serialize(final DataOutput buffer, final T data) throws Exception
    {
        return writeFields(buffer, data);

    }

    protected abstract int writeFields(final DataOutput dataOutput, final T data) throws Exception;
    protected abstract T readFields(final DataInput dataInput) throws Exception;

    @Override
    public List<T> listFromByteArray(final byte[] bytes) throws Exception
    {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        final DataInput dataInput = new DataInputStream(byteArrayInputStream);
        final int length = readInt(dataInput);
        final List<T> list = new ArrayList<>(length);
        for (int i = 0; i < length; i++)
        {
            list.add(readFields(dataInput));
        }
        return list;
    }

    @Override
    public byte[] listToByteArray(List<T> data) throws Exception
    {
        if (data == null)
            data = Collections.emptyList();
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final DataOutput dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        write(dataOutputStream, data.size());
        for (T d : data)
        {
            writeFields(dataOutputStream, d);
        }
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public byte[] toByteArray(final T data) throws Exception
    {

        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final DataOutput dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        final int len = serialize(dataOutputStream, data);
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public T fromByteArray(final byte[] bytes) throws Exception
    {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        final DataInput dataInputStream = new DataInputStream(byteArrayInputStream);
        return deserialize(dataInputStream);
    }

    @Override
    public List<T> deserializeList(final DataInput buffer) throws Exception {
        final int length = readInt(buffer);
        final List<T> list = new ArrayList<T>(length);
        for (int i = 0; i < length; i++)
        {
            final T data = deserialize(buffer);
            if (data != null)
                list.add(data);
        }
        return list;
    }

    @Override
    public int serializeList(final DataOutput buffer, final List<T> data) throws Exception
    {
        if (data == null)
        {
            return write(buffer, 0);
        }
        int len = write(buffer, data.size());
        for (T d : data)
        {
            len += writeFields(buffer, d);
        }
        return len;
    }


    public double[] readDoubleArr(DataInput buffer) throws Exception
    {
        int size = readInt(buffer);
        double[] arr = new double[size];
        for (int i = 0; i < size; i++)
        {
            arr[i] = readDouble(buffer);
        }

        return arr;
    }


    public int[] readIntArr(DataInput buffer) throws Exception
    {
        int size = readInt(buffer);
        int[] arr = new int[size];
        for (int i = 0; i < size; i++)
        {
            arr[i] = readInt(buffer);
        }

        return arr;
    }







    public int write(final DataOutput writer, final BigInteger bigInteger) throws Exception {
        try
        {
            int offset = 0;
            byte[] bytes = new byte[8];
            byte temp_byte[] = bigInteger.toByteArray();
            int array_count=temp_byte.length-1;
            for (int i = 7; i>=0; i--)
            {
                if(array_count>=0)
                {
                    bytes[offset]=temp_byte[array_count];
                }
                else
                {
                    bytes[offset]=(byte)00;
                }
                offset++;
                array_count--;
            }
            writer.write(bytes);
            return 8;
        }
        catch (Exception e)
        {
            log.error(e.getMessage(), e);
            throw new Exception(e);
        }
    }


    public BigInteger readUnsignedLong(DataInput reader) throws Exception {
        try
        {
            byte[] arr = new byte[8];
            reader.readFully(arr);
            return new BigInteger(1, arr);
        }
        catch (Exception e)
        {
            log.error(e.getMessage(), e);
            throw new Exception(e);
        }
    }

    public int writeFixedLengthBufer(final DataOutput buffer, final byte[] data, final int length) throws Exception {
        try
        {
            final int bufLen = data.length;
            for (int i = 0; i < length; i++)
            {
                if (i < bufLen)
                {
                    buffer.write(data[i]);
                }
                else
                {
                    buffer.write(NullByte);
                }
            }
            return length;
        }
        catch (Exception e)
        {
            log.error(e.getMessage(), e);
            throw new Exception(e);
        }
    }

    public int write(final DataOutput buffer, final byte[] data) throws Exception {
        try
        {
            int len = write(buffer, data.length);
            for (int i = 0; i < data.length; i++)
            {
                len += write(buffer, data[i]);
            }
            return len;
        }
        catch (Exception e)
        {
            log.error(e.getMessage(), e);
            throw new Exception(e);
        }
    }

    public byte[] readFixedLengthBytes(final DataInput buffer, final int numBytes) throws Exception {
        try
        {
            final byte[] bytes = new byte[numBytes];
            buffer.readFully(bytes);
            return bytes;
        }
        catch (Exception e)
        {
            log.error(e.getMessage(), e);
            throw new Exception(e);
        }
    }

    public byte[] readBytes(final DataInput buffer) throws Exception {
        try
        {
            int len = readInt(buffer);
            final byte[] data = new byte[len];
            for (int i = 0; i < len; i++)
            {
                data[i] = readByte(buffer);
            }
            return data;
        }
        catch (Exception e)
        {
            log.error(e.getMessage(), e);
            throw new Exception(e);
        }
    }

    public byte readByte(final DataInput buffer) throws Exception {
        try
        {
            return buffer.readByte();
        }
        catch (Exception e)
        {
            log.error(e.getMessage(), e);
            throw new Exception(e);
        }
    }

    public int write(final DataOutput buffer, final byte b) throws Exception {
        try
        {
            buffer.writeByte(b);
            return 1;
        }
        catch (Exception e)
        {
            log.error(e.getMessage(), e);
            throw new Exception(e);
        }
    }


    public char readChar(final DataInput buffer) throws Exception {
        try
        {
            return buffer.readChar();
        }
        catch (Exception e)
        {
            log.error(e.getMessage(), e);
            throw new Exception(e);
        }
    }



    public int write(final DataOutput buffer, final char c) throws Exception {
        try
        {
            buffer.writeChar(c);
            return 2;
        }
        catch (Exception e)
        {
            log.error(e.getMessage(), e);
            throw new Exception(e);
        }
    }




    protected int write(final DataOutput buffer, final boolean b) throws Exception
    {
        try
        {
            byte bt = (b) ? (byte)1 : (byte)0;
            buffer.writeByte(bt);
            return 1;
        }
        catch (Exception e)
        {
            log.error(e.getMessage(), e);
            throw new Exception(e);
        }
    }

    protected int write(final DataOutput buffer, final double d) throws Exception
    {
        try
        {
            buffer.writeDouble(d);
            return 8;
        }
        catch (Exception e)
        {
            log.error(e.getMessage(), e);
            throw new Exception(e);
        }
    }

    protected int write(final DataOutput buffer, final long l) throws Exception
    {
        try
        {
            buffer.writeLong(l);
            return 8;
        }
        catch (Exception e)
        {
            log.error(e.getMessage(), e);
            throw new Exception(e);
        }
    }

    protected int write(final DataOutput buffer, final int i) throws Exception
    {
        try
        {
            buffer.writeInt(i);
            return 4;
        }
        catch (Exception e)
        {
            log.error(e.getMessage(), e);
            throw new Exception(e);
        }
    }



    protected int write(final DataOutput buffer, final String s) throws Exception
    {
        try
        {
            if (s == null)
                return write(buffer, 0);
            final int length = s.length();
            write(buffer, length);
            final byte[] strBytes = s.getBytes(StandardCharsets.US_ASCII);
            buffer.write(strBytes);
            return 4 + strBytes.length;
        }
        catch (Exception e)
        {
            log.error(e.getMessage(), e);
            throw new Exception(e);
        }
    }

    protected int write(final DataOutput buffer, final short s) throws Exception
    {
        try
        {
            buffer.writeShort(s);
            return 2;
        }
        catch (Exception e)
        {
            log.error(e.getMessage(), e);
            throw new Exception(e);
        }
    }

    protected int write(final DataOutput buffer, final DateTime d) throws Exception
    {
        try
        {

            write(buffer, d.getMillis());
            return 8;
        }
        catch (Exception e)
        {
            log.error(e.getMessage(), e);
            throw new Exception(e);
        }
    }

    protected double readDouble(final DataInput buffer) throws Exception
    {
        try
        {
            return buffer.readDouble();
        }
        catch (Exception e)
        {
            log.error(e.getMessage(), e);
            throw new Exception(e);
        }
    }

    protected int readInt(final DataInput buffer) throws Exception
    {
        try
        {
            return buffer.readInt();
        }
        catch (Exception e)
        {
            log.error(e.getMessage(), e);
            throw new Exception(e);
        }

    }

    protected boolean readBool(final DataInput buffer) throws Exception
    {
        try
        {
            final byte i = buffer.readByte();
            return i == 1;
        }
        catch (Exception e)
        {
            log.error(e.getMessage(), e);
            throw new Exception(e);
        }

    }


    protected short readShort(final DataInput buffer) throws Exception
    {
        try
        {
            return buffer.readShort();
        }
        catch (Exception e)
        {
            log.error(e.getMessage(), e);
            throw new Exception(e);
        }
    }


    protected String readString(final DataInput buffer) throws Exception
    {
        try
        {
            final int strLen = buffer.readInt();
            if (strLen == 0)
                return "";
            final byte[] strBuffer = new byte[strLen];
            buffer.readFully(strBuffer, 0, strLen);
            return new String(strBuffer, StandardCharsets.US_ASCII);
        }
        catch (Exception e)
        {
            log.error(e.getMessage(), e);
            throw new Exception(e);
        }
    }

    protected long readLong(final DataInput buffer) throws Exception
    {
        try
        {
            return buffer.readLong();
        }
        catch (Exception e)
        {
            log.error(e.getMessage(), e);
            throw new Exception(e);
        }
    }

    protected DateTime readDate(final DataInput buffer) throws Exception
    {
        try
        {
            final long time = readLong(buffer);
            return new DateTime(time);
        }
        catch (Exception e)
        {
            log.error(e.getMessage(), e);
            throw new Exception(e);
        }
    }

    public int write(final DataOutput buffer, final double[] data) throws Exception {
        int length = write(buffer, data.length);
        for (int i = 0; i < data.length; i++)
            length += write(buffer, data[i]);
        return length;
    }

    public double[] readDoubleArray(final DataInput buffer) throws Exception {
        final int length = readInt(buffer);
        final double[] arr = new double[length];
        for (int i = 0; i < length; i++)
            arr[i] = readDouble(buffer);
        return arr;
    }

    public int write(final DataOutput buffer, final int[] data) throws Exception {
        int length = write(buffer, data.length);
        for (int i = 0; i < data.length; i++)
            length += write(buffer, data[i]);
        return length;
    }

    public int[] readIntArray(final DataInput buffer) throws Exception {
        final int length = readInt(buffer);
        final int[] arr = new int[length];
        for (int i = 0; i < length; i++)
            arr[i] = readInt(buffer);
        return arr;
    }

    public int write(final DataOutput buffer, final Map<String, String> data) throws Exception
    {
        if (data == null)
            return write(buffer, 0);
        int length = write(buffer, data.size());
        for (Map.Entry<String, String> entry : data.entrySet())
        {
            length += write(buffer, entry.getKey());
            length += write(buffer, entry.getValue());
        }
        return length;
    }

    public Map<String,String> readStringStringMap(final DataInput buffer) throws Exception
    {

        int length = readInt(buffer);
        final Map<String, String> map = new LinkedHashMap<>(length);
        for (int i = 0; i < length; i++)
        {
            final String key = readString(buffer);
            final String value = readString(buffer);
            map.put(key, value);
        }
        return map;
    }

    public int write(final DataOutput buffer, final List<String> data) throws Exception
    {
        if (data == null)
            return write(buffer, 0);
        int length = write(buffer, data.size());
        for (String d : data)
        {
            length += write(buffer, d);
        }
        return length;
    }

    public List<String> readStringList(final DataInput buffer) throws Exception
    {

        int length = readInt(buffer);
        final List<String> list = new ArrayList<>(length);
        for (int i = 0; i < length; i++)
        {
            final String data = readString(buffer);
            list.add(data);
        }
        return list;
    }


}
