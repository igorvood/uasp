package ru.vtb.uasp.beg.lib.transportManagerClient.impl;

import ru.vtb.uasp.beg.lib.transportManagerClient.impl.local.LocalTransportManagerClientBuilder;
import ru.vtb.uasp.beg.lib.transportManagerClient.impl.remote.RemoteTransportManagerClientBuilder;

public class TransportManagerClientBuilder {

    public static LocalTransportManagerClientBuilder local() {
        return new LocalTransportManagerClientBuilder();
    }

    public static RemoteTransportManagerClientBuilder remote() {
        return new RemoteTransportManagerClientBuilder();
    }
}
