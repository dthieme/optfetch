package uptick.messages;


import org.joda.time.DateTime;
import uptick.UptickMessageBody;
import uptick.UptickMessageInfo;
import uptick.UptickMessageType;

public interface UptickMessage
{
    UptickMessageType getMessageType();
    boolean includesBody();
    DateTime getTimestamp();
    UptickMessageInfo getMessageInfo();
    UptickMessageBody getMessageBody();
    String getTopic();
}
