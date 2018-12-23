package io.sunflower.setup;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.inject.Stage;

public class GuiceConfig {

    @JsonProperty
    private boolean scheduleEnabled = false;

    @JsonProperty
    private boolean eventEnabled = false;

    @JsonProperty
    private boolean adviseEnabled = false;

    @JsonProperty
    private boolean metricsEnabled = false;

    @JsonProperty
    private boolean lifecycleEnabled = false;

    @JsonProperty
    private Stage stage = Stage.DEVELOPMENT;

    public boolean isScheduleEnabled() {
        return scheduleEnabled;
    }

    public void setScheduleEnabled(boolean scheduleEnabled) {
        this.scheduleEnabled = scheduleEnabled;
    }

    public boolean isEventEnabled() {
        return eventEnabled;
    }

    public void setEventEnabled(boolean eventEnabled) {
        this.eventEnabled = eventEnabled;
    }

    public boolean isAdviseEnabled() {
        return adviseEnabled;
    }

    public void setAdviseEnabled(boolean adviseEnabled) {
        this.adviseEnabled = adviseEnabled;
    }

    public boolean isMetricsEnabled() {
        return metricsEnabled;
    }

    public void setMetricsEnabled(boolean metricsEnabled) {
        this.metricsEnabled = metricsEnabled;
    }

    public boolean isLifecycleEnabled() {
        return lifecycleEnabled;
    }

    public void setLifecycleEnabled(boolean lifecycleEnabled) {
        this.lifecycleEnabled = lifecycleEnabled;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
