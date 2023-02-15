package ru.vtb.uasp.beg.lib.transportManagerClient;

public class TransportState {
    private String name;
    private String topicName;
    private String kafkaAddress;
    private long lastUpdateTimestamp;
    private boolean enabled;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getKafkaAddress() {
        return kafkaAddress;
    }

    public void setKafkaAddress(String kafkaAddress) {
        this.kafkaAddress = kafkaAddress;
    }

    public long getLastUpdateTimestamp() {
        return lastUpdateTimestamp;
    }

    public void setLastUpdateTimestamp(long lastUpdateTimestamp) {
        this.lastUpdateTimestamp = lastUpdateTimestamp;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "TransportState{" +
                "businessEventName='" + name + '\'' +
                ", topicName='" + topicName + '\'' +
                ", kafkaAddress='" + kafkaAddress + '\'' +
                ", lastUpdateTimestamp=" + lastUpdateTimestamp +
                ", enabled=" + enabled +
                '}';
    }
}
