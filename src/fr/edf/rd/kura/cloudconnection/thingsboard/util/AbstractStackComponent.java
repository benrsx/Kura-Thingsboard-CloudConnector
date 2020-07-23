package fr.edf.rd.kura.cloudconnection.thingsboard.util;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.kura.cloudconnection.CloudEndpoint;
import org.eclipse.kura.cloudconnection.listener.CloudConnectionListener;
import org.eclipse.kura.configuration.ConfigurableComponent;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;

import fr.edf.rd.kura.cloudconnection.thingsboard.cloud.ThingsboardCloudEndpoint;
import fr.edf.rd.kura.cloudconnection.thingsboard.util.StackComponentOptions.OptionsFactory;

public abstract class AbstractStackComponent<T> implements ConfigurableComponent, CloudConnectionListener {

    private final Set<CloudConnectionListener> cloudConnectionListeners = new CopyOnWriteArraySet<>();
    private final AtomicReference<StackComponentOptions<T>> options = new AtomicReference<>();
    private final AtomicReference<Optional<ThingsboardCloudEndpoint>> endpoint = new AtomicReference<>(
            Optional.empty());

    private ServiceTracker<CloudEndpoint, ThingsboardCloudEndpoint> tracker;
    private final BundleContext context = FrameworkUtil.getBundle(AbstractStackComponent.class).getBundleContext();

    protected abstract Logger getLogger();

    protected abstract OptionsFactory<T> getOptionsFactory();

    protected void setCloudEndpoint(final ThingsboardCloudEndpoint endpoint) {
        this.endpoint.set(Optional.of(endpoint));
        endpoint.registerCloudConnectionListener(this);
    }

    protected void unsetCloudEndpoint(final ThingsboardCloudEndpoint endpoint) {
        endpoint.unregisterCloudConnectionListener(this);
        this.endpoint.set(Optional.empty());
    }

    public void activated(final Map<String, Object> properties) {
        getLogger().info("activating...");

        updated(properties);

        getLogger().info("activating...done");
    }

    public void updated(final Map<String, Object> properties) {
        getLogger().info("updating...");

        this.options.set(new StackComponentOptions<>(properties, getOptionsFactory()));
        reopenTracker();

        getLogger().info("updating...done");
    }

    public void deactivated() {
        getLogger().info("deactivating...");

        shutdownTracker();

        getLogger().info("deactivating...done");
    }

    protected StackComponentOptions<T> getOptions() {
        return this.options.get();
    }

    protected Optional<ThingsboardCloudEndpoint> getEndpoint() {
        return this.endpoint.get();
    }

    private void shutdownTracker() {
        if (this.tracker != null) {
            this.tracker.close();
            this.tracker = null;
        }
    }

    private void reopenTracker() {
        shutdownTracker();

        final StackComponentOptions<?> currentOptions = this.options.get();

        final Optional<String> endpointPid = currentOptions.getCloudEndpointPid();

        if (!endpointPid.isPresent()) {
            return;
        }

        final Filter filter;
        try {
            filter = Utils.createFilter(CloudEndpoint.class, endpointPid.get());
        } catch (final Exception e) {
            getLogger().warn("invalid cloud endpoint pid", e);
            return;
        }

        this.tracker = new ServiceTracker<>(this.context, filter, new Customizer());
        this.tracker.open();
    }

    private class Customizer implements ServiceTrackerCustomizer<CloudEndpoint, ThingsboardCloudEndpoint> {

        @Override
        public ThingsboardCloudEndpoint addingService(final ServiceReference<CloudEndpoint> reference) {
            final CloudEndpoint service = AbstractStackComponent.this.context.getService(reference);

            if (service == null) {
                return null;
            }

            if (!(service instanceof ThingsboardCloudEndpoint)) {
                AbstractStackComponent.this.context.ungetService(reference);
                return null;
            }

            final ThingsboardCloudEndpoint trackedService = (ThingsboardCloudEndpoint) service;

            setCloudEndpoint(trackedService);

            return trackedService;
        }

        @Override
        public void modifiedService(final ServiceReference<CloudEndpoint> reference,
                final ThingsboardCloudEndpoint service) {
            // do nothing
        }

        @Override
        public void removedService(final ServiceReference<CloudEndpoint> reference,
                final ThingsboardCloudEndpoint service) {

            unsetCloudEndpoint(service);
            AbstractStackComponent.this.context.ungetService(reference);
        }

    }

    public void registerCloudConnectionListener(CloudConnectionListener cloudConnectionListener) {
        this.cloudConnectionListeners.add(cloudConnectionListener);
    }

    public void unregisterCloudConnectionListener(CloudConnectionListener cloudConnectionListener) {
        this.cloudConnectionListeners.remove(cloudConnectionListener);
    }

    @Override
    public void onDisconnected() {
        this.cloudConnectionListeners.forEach(Utils.catchAll(CloudConnectionListener::onDisconnected));
    }

    @Override
    public void onConnectionLost() {
        this.cloudConnectionListeners.forEach(Utils.catchAll(CloudConnectionListener::onConnectionLost));
    }

    @Override
    public void onConnectionEstablished() {
        this.cloudConnectionListeners.forEach(Utils.catchAll(CloudConnectionListener::onConnectionEstablished));
    }
}
