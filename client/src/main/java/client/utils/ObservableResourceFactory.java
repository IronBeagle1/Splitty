package client.utils;

import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import org.apache.commons.text.StringSubstitutor;

import java.util.Map;

// Adapted from https://stackoverflow.com/questions/32464974/javafx-change-application-language-on-the-run
public class ObservableResourceFactory {

    private final SimpleMapProperty<String, String> resources =
            new SimpleMapProperty<>(FXCollections.observableHashMap());

    /***
     * Returns the Observable SMP, used for binding
     * @return the SMP holding all resources
     */
    public final SimpleMapProperty<String, String> resourcesProperty() {
        return resources;
    }

    /***
     * Live-replaces the language resources used with the provided Map
     * @param map - the Map containing key-value pairs of localized strings
     */
    public final void setResources(Map<String, String> map) {
        resourcesProperty().clear();
        resourcesProperty().putAll(map);
    }

    /***
     * Provides a binding for the translated value corresponding to a text key
     * @param key the text key (e.g. "startup.label.create")
     * @return a live-updating binding for the translated value
     */
    public StringBinding getStringBinding(String key) {
        return new StringBinding() {
            { bind(resourcesProperty()); }
            @Override
            public String computeValue() {
                return resourcesProperty().get(key);
            }
        };
    }

    private static final String subPrefix = "{{";
    private static final String subSuffix = "}}";

    /***
     * Provides a binding for the translated value corresponding to a text key, with values substituted
     * @param key the text key (e.g. "startup.label.create")
     * @param values the values to substitute in for placeholders
     * @return - a live-updating binding for the translated value, with values substituted (values are NOT live-updated)
     */
    public StringBinding getStringSubstitutionBinding(String key, Map<String, String> values) {
        return new StringBinding() {
            { bind(resourcesProperty()); }
            @Override
            public String computeValue() {
                String rawString = resourcesProperty().get(key);
                StringSubstitutor substitutor = new StringSubstitutor(values, subPrefix, subSuffix);
                return substitutor.replace(rawString);
            }
        };
    }
}
