package uptick;


import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public enum UptickMetadataField
{
    SenderMachine("UPTICK::SENDER_MACHINE", "ROBOTRON8000"),
    SenderAppVersion("UPTICK::SENDER_APP_VERSION", "0.6.20.0"),
    SenderApp("UPTICK::SENDER_APP"),
    ReportNumPages("UPTICK::REPORT_NUM_PAGES"),
    Page1NumRows("UPTICK::PAGE_1_NUM_ROWS"),
    Page1NumCols("UPTICK::PAGE_1_NUM_COLUMNS"),
    Page2NumRows("UPTICK::PAGE_2_NUM_ROWS"),
    Page2NumCols("UPTICK::PAGE_2_NUM_COLUMNS"),
    Page3NumRows("UPTICK::PAGE_3_NUM_ROWS"),
    Page3NumCols("UPTICK::PAGE_3_NUM_COLUMNS"),
    Page4NumRows("UPTICK::PAGE_4_NUM_ROWS"),
    Page4NumCols("UPTICK::PAGE_4_NUM_COLUMNS"),
    Page5NumRows("UPTICK::PAGE_5_NUM_ROWS"),
    Page5NumCols("UPTICK::PAGE_5_NUM_COLUMNS"),
    Page6NumRows("UPTICK::PAGE_6_NUM_ROWS"),
    Page6NumCols("UPTICK::PAGE_6_NUM_COLUMNS"),
    SendTimeUtc("UPTICK::SEND_TIME_UTC"),
    SenderProcessId("UPTICK::SENDER_PROCESS_ID", "12345"),
    SenderUsername("UPTICK::SENDER_USERNAME", "svc_uptick"),
    ReportSecret("UPTICK::REPORT_SECRET");

    private final String fieldName;
    private final Object defaultValue;

    UptickMetadataField(final String fieldName)
    {
        this(fieldName, "");
    }

    UptickMetadataField(final String fieldName, final Object defaultValue)
    {
        this.fieldName = fieldName;
        this.defaultValue = defaultValue;
    }

    public String getFieldName()
    {
        return fieldName;
    }

    public Object getDefaultValue()
    {
        return defaultValue;
    }

    public static String getSendTime() {
        return Utils.formatTxTime(DateTime.now(DateTimeZone.UTC));
    }


}
