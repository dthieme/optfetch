package uptick;

import java.io.DataInput;
import java.io.DataOutput;
import java.util.List;

public interface BinarySerializer<T>
{
    T deserialize(final DataInput buffer) throws Exception;
    int serialize(final DataOutput buffer, final T data) throws Exception;
    List<T> deserializeList(final DataInput buffer) throws Exception;
    int serializeList(final DataOutput buffer, final List<T> data) throws Exception;
    T fromByteArray(final byte[] bytes) throws Exception;
    byte[] toByteArray(T data) throws Exception;
    List<T> listFromByteArray(final byte[] bytes) throws Exception;
    byte[] listToByteArray(List<T> data) throws Exception;
}
