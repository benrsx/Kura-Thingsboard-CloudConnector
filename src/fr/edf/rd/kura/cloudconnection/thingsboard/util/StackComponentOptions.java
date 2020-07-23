package fr.edf.rd.kura.cloudconnection.thingsboard.util;

import java.util.Map;
import java.util.Optional;

import org.eclipse.kura.KuraException;
import org.eclipse.kura.cloudconnection.CloudConnectionConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StackComponentOptions<T> {

    private static final Logger logger = LoggerFactory.getLogger(StackComponentOptions.class);

    private static final Property<String> CLOUD_ENDPOINT_SERVICE_PID = new Property<>(
            CloudConnectionConstants.CLOUD_ENDPOINT_SERVICE_PID_PROP_NAME.value(), String.class);

    private final Optional<String> cloudEndpointPid;
    private final Optional<T> componentOptions;

    public StackComponentOptions(final Map<String, Object> properties, final OptionsFactory<T> factory) {
        this.cloudEndpointPid = extractCloudEndpointPid(properties);
        this.componentOptions = factory.tryBuild(properties);
    }

    private static Optional<String> extractCloudEndpointPid(final Map<String, Object> properties) {
        try {
            return Optional.of(CLOUD_ENDPOINT_SERVICE_PID.get(properties));
        } catch (final Exception e) {
            logger.warn("cloud endpoint pid not set");
            return Optional.empty();
        }
    }

    public Optional<String> getCloudEndpointPid() {
        return this.cloudEndpointPid;
    }

    public Optional<T> getComponentOptions() {
        return this.componentOptions;
    }

    public interface OptionsFactory<T> {

        public T build(final Map<String, Object> properties) throws KuraException;

        public default Optional<T> tryBuild(final Map<String, Object> properties) {
            try {
                return Optional.of(this.build(properties));
            } catch (final Exception e) {
                logger.warn("invalid publishing properties", e);
                return Optional.empty();
            }
        }
    }
}
