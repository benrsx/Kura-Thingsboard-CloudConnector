package fr.edf.rd.kura.cloudconnection.thingsboard.publisher;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.kura.KuraErrorCode;
import org.eclipse.kura.KuraException;
import org.eclipse.kura.cloudconnection.listener.CloudDeliveryListener;
import org.eclipse.kura.cloudconnection.message.KuraMessage;
import org.eclipse.kura.cloudconnection.publisher.CloudPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.edf.rd.kura.cloudconnection.thingsboard.cloud.ThingsboardCloudEndpoint;
import fr.edf.rd.kura.cloudconnection.thingsboard.util.AbstractStackComponent;
import fr.edf.rd.kura.cloudconnection.thingsboard.util.StackComponentOptions;
import fr.edf.rd.kura.cloudconnection.thingsboard.util.StackComponentOptions.OptionsFactory;
import fr.edf.rd.kura.cloudconnection.thingsboard.util.Utils;

public class ThingsboardPublisher extends AbstractStackComponent<PublishOptions>
        implements CloudPublisher, CloudDeliveryListener {

    private static final Logger logger = LoggerFactory.getLogger(ThingsboardPublisher.class);

    private final Set<CloudDeliveryListener> cloudDeliveryListeners = new CopyOnWriteArraySet<>();

    @Override
    protected void setCloudEndpoint(final ThingsboardCloudEndpoint endpoint) {
        super.setCloudEndpoint(endpoint);
        endpoint.registerCloudDeliveryListener(this);
    }

    @Override
    protected void unsetCloudEndpoint(final ThingsboardCloudEndpoint endpoint) {
        endpoint.unregisterCloudConnectionListener(this);
        super.unsetCloudEndpoint(endpoint);
    }

    @Override
    public String publish(final KuraMessage message) throws KuraException {
        final StackComponentOptions<PublishOptions> currentOptions = getOptions();

        final Optional<PublishOptions> publishOptions = currentOptions.getComponentOptions();

        if (!publishOptions.isPresent()) {
            throw new KuraException(KuraErrorCode.CONFIGURATION_ERROR, null, null, "invalid publish configuration");
        }

        final Optional<ThingsboardCloudEndpoint> currentEndpoint = getEndpoint();

        if (!currentEndpoint.isPresent()) {
            throw new KuraException(KuraErrorCode.NOT_FOUND, null, null, "cloud endpoint not bound");
        }

        return currentEndpoint.get().publish(publishOptions.get(), message.getPayload());
    }

    @Override
    public void registerCloudDeliveryListener(CloudDeliveryListener cloudDeliveryListener) {
        this.cloudDeliveryListeners.add(cloudDeliveryListener);
    }

    @Override
    public void unregisterCloudDeliveryListener(CloudDeliveryListener cloudDeliveryListener) {
        this.cloudDeliveryListeners.remove(cloudDeliveryListener);
    }

    @Override
    public void onMessageConfirmed(String messageId) {
        this.cloudDeliveryListeners.forEach(Utils.catchAll(l -> l.onMessageConfirmed(messageId)));
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    protected OptionsFactory<PublishOptions> getOptionsFactory() {
        return PublishOptions::new;
    }

}