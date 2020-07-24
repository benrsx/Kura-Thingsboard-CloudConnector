package fr.edf.rd.kura.cloudconnection.thingsboard.cloud;

import java.util.regex.Pattern;

public final class Constants {

    public static final String TOPIC_PROP_NAME = "topic";
    public static final String CONNECT_TOPIC = "v1/gateway/connect";
    public static final String TELEMETRY_TOPIC = "v1/gateway/telemetry";
    public static final String SUBSCRIBE_TOPIC = "v1/gateway/rpc";
    private static final String DEVICE_ID_PATTERN_STRING = "\\$([^\\s/]+)";
    public static final Pattern DEVICE_ID_PATTERN = Pattern.compile(DEVICE_ID_PATTERN_STRING);

    private Constants() {
    }

}
