package fr.edf.rd.kura.cloudconnection.thingsboard.publisher;

import java.util.Map;

import org.eclipse.kura.KuraException;

import fr.edf.rd.kura.cloudconnection.thingsboard.cloud.Qos;
import fr.edf.rd.kura.cloudconnection.thingsboard.util.Property;

public class PublishOptions {

    private static final Property<String> DEVICE_ID = new Property<>("device.id", "$assetName");
    public static final Property<Qos> QOS_PROP = new Property<>("qos", 0).map(Qos.class, Qos::valueOf);
    public static final Property<Boolean> RETAIN_PROP = new Property<>("retain", false);
    public static final Property<Integer> PRIORITY_PROP = new Property<>("priority", 4);

    private final String deviceId;
    private final Qos qos;
    private final boolean retain;
    private final int priority;

    public PublishOptions(final Map<String, Object> properties) throws KuraException {
        this.deviceId = DEVICE_ID.getOrDefault(properties);
        this.qos = QOS_PROP.getOrDefault(properties);
        this.retain = RETAIN_PROP.getOrDefault(properties);
        this.priority = PRIORITY_PROP.getOrDefault(properties);
    }

    public String getDeviceId() {
        return this.deviceId;
    }

    public Qos getQos() {
        return this.qos;
    }

    public boolean getRetain() {
        return this.retain;
    }

    public int getPriority() {
        return this.priority;
    }

}
