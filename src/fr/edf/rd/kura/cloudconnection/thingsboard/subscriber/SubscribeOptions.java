package fr.edf.rd.kura.cloudconnection.thingsboard.subscriber;

import java.util.Map;

import org.eclipse.kura.KuraException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.edf.rd.kura.cloudconnection.thingsboard.cloud.Qos;
import fr.edf.rd.kura.cloudconnection.thingsboard.util.Property;

public class SubscribeOptions {

    private static final Logger logger = LoggerFactory.getLogger(SubscribeOptions.class);

    public static final Property<Qos> QOS_PROP = new Property<>("qos", 0).map(Qos.class, Qos::valueOf);

    private final Qos qos;

    public SubscribeOptions(final Map<String, Object> properties) throws KuraException {
        this.qos = QOS_PROP.get(properties);
    }

    public Qos getQos() {
        return this.qos;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (this.qos == null ? 0 : this.qos.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SubscribeOptions other = (SubscribeOptions) obj;
        if (this.qos != other.qos) {
            return false;
        }
        return true;
    }

}