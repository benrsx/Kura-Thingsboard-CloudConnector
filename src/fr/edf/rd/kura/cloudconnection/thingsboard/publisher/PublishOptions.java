package fr.edf.rd.kura.cloudconnection.thingsboard.publisher;

import java.util.Map;

import org.eclipse.kura.KuraException;

import fr.edf.rd.kura.cloudconnection.thingsboard.cloud.Qos;
import fr.edf.rd.kura.cloudconnection.thingsboard.util.Property;

public class PublishOptions {

    public static final Property<String> TOPIC_PROP = new Property<>("topic", String.class);
    public static final Property<Qos> QOS_PROP = new Property<>("qos", 0).map(Qos.class, Qos::valueOf);
    public static final Property<Boolean> RETAIN_PROP = new Property<>("retain", false);
    public static final Property<Integer> PRIORITY_PROP = new Property<>("priority", 4);

    private final String topic;
    private final Qos qos;
    private final boolean retain;
    private final int priority;

    public PublishOptions(final Map<String, Object> properties) throws KuraException {
        this.topic = TOPIC_PROP.get(properties);
        this.qos = QOS_PROP.getOrDefault(properties);
        this.retain = RETAIN_PROP.getOrDefault(properties);
        this.priority = PRIORITY_PROP.getOrDefault(properties);
    }

    public String getTopic() {
        return this.topic;
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
