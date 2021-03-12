package uptick;


import uptick.messages.UptickMessage;

public interface UptickMessageHandler
{
    void handleMessage(final UptickMessage message);
}
