package se.tain;

/**
 * Created by ruel on 9/26/16.
 */
public class ExternalData {
    private String configDependantMessage;

    public ExternalData() {
    }

    public ExternalData(String configDependantMessage) {
        this.configDependantMessage = configDependantMessage;
    }

    public String getConfigDependantMessage() {
        return configDependantMessage;
    }

    public void setConfigDependantMessage(String configDependantMessage) {
        this.configDependantMessage = configDependantMessage;
    }
}
