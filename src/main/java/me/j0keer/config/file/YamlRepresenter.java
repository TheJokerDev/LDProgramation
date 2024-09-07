package me.j0keer.config.file;

import me.j0keer.config.ConfigurationSection;
import me.j0keer.config.serialization.ConfigurationSerializable;
import me.j0keer.config.serialization.ConfigurationSerialization;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.representer.Representer;

import java.util.LinkedHashMap;
import java.util.Map;

public class YamlRepresenter extends Representer {
    public YamlRepresenter() {
        super(new DumperOptions());
        this.multiRepresenters.put(ConfigurationSection.class, new RepresentConfigurationSection());
        this.multiRepresenters.put(ConfigurationSerializable.class, new RepresentConfigurationSerializable());
    }

    private class RepresentConfigurationSerializable extends RepresentMap {
        private RepresentConfigurationSerializable() {
            super();
        }

        public Node representData(Object data) {
            ConfigurationSerializable serializable = (ConfigurationSerializable)data;
            Map<String, Object> values = new LinkedHashMap<>();
            values.put("==", ConfigurationSerialization.getAlias(serializable.getClass()));
            values.putAll(serializable.serialize());
            return super.representData(values);
        }
    }

    private class RepresentConfigurationSection extends RepresentMap {
        private RepresentConfigurationSection() {
            super();
        }

        public Node representData(Object data) {
            return super.representData(((ConfigurationSection)data).getValues(false));
        }
    }
}
