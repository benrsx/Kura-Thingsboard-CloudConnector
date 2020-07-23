package fr.edf.rd.kura.cloudconnection.thingsboard.subscriber;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.kura.cloudconnection.message.KuraMessage;
import org.eclipse.kura.cloudconnection.subscriber.CloudSubscriber;
import org.eclipse.kura.cloudconnection.subscriber.listener.CloudSubscriberListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.edf.rd.kura.cloudconnection.thingsboard.cloud.ThingsboardCloudEndpoint;
import fr.edf.rd.kura.cloudconnection.thingsboard.util.AbstractStackComponent;
import fr.edf.rd.kura.cloudconnection.thingsboard.util.StackComponentOptions;
import fr.edf.rd.kura.cloudconnection.thingsboard.util.StackComponentOptions.OptionsFactory;
import fr.edf.rd.kura.cloudconnection.thingsboard.util.Utils;

public class ThingsboardSubscriber extends AbstractStackComponent<SubscribeOptions>
        implements CloudSubscriber, CloudSubscriberListener {

    private static final Logger logger = LoggerFactory.getLogger(ThingsboardSubscriber.class);

    private final Set<CloudSubscriberListener> cloudSubscriberListeners = new CopyOnWriteArraySet<>();

    @Override
    protected void setCloudEndpoint(final ThingsboardCloudEndpoint endpoint) {
        super.setCloudEndpoint(endpoint);
        trySubscribe();
    }

    @Override
    protected void unsetCloudEndpoint(final ThingsboardCloudEndpoint endpoint) {
        tryUnsubscribe();
        super.unsetCloudEndpoint(endpoint);
    }

    @Override
    public void registerCloudSubscriberListener(final CloudSubscriberListener listener) {
        this.cloudSubscriberListeners.add(listener);
    }

    @Override
    public void unregisterCloudSubscriberListener(final CloudSubscriberListener listener) {
        this.cloudSubscriberListeners.remove(listener);
    }

    @Override
    public void onMessageArrived(final KuraMessage message) {
        this.cloudSubscriberListeners.forEach(Utils.catchAll(l -> l.onMessageArrived(message)));
    }

    private void trySubscribe() {

        final Optional<ThingsboardCloudEndpoint> endpoint = getEndpoint();

        if (!endpoint.isPresent()) {
            return;
        }

        final ThingsboardCloudEndpoint currentEndpoint = endpoint.get();

        currentEndpoint.unregisterSubscriber(this);

        final StackComponentOptions<SubscribeOptions> options = getOptions();

        final Optional<SubscribeOptions> subscribeOptions = options.getComponentOptions();

        if (subscribeOptions.isPresent()) {
            currentEndpoint.registerSubscriber(subscribeOptions.get(), this);
        }

    }

    private void tryUnsubscribe() {

        final Optional<ThingsboardCloudEndpoint> endpoint = getEndpoint();

        if (endpoint.isPresent()) {
            endpoint.get().unregisterSubscriber(this);
        }

    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    protected OptionsFactory<SubscribeOptions> getOptionsFactory() {
        return SubscribeOptions::new;
    }
}
