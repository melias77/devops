package dk.kmd.devops.plugin.environment.configuration;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;

import org.yaml.snakeyaml.Yaml;

public class PropertyFileGeneratorImpl implements Processor<Path> {

    private final PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:.yml");

    @Override
    public void process(ProcessContext context, Path target) {

        if (matcher.matches(target)) {
            generateEnvironmentSpecificPropertyFile(target);
        }
    }

    protected void generateEnvironmentSpecificPropertyFile(Path ymlFile) {

        Yaml yaml = new Yaml();

        // System.out.println(yaml.dump(yaml.load(new FileInputStream(new
        // File("hello_world.yaml")))));
        //
        // Map<String, Map<String, String>> values = (Map<String, Map<String,
        // String>>) yaml
        // .load(new FileInputStream(new File("hello_world.yaml")));
        //
        // for (String key : values.keySet()) {
        // Map<String, String> subValues = values.get(key);
        // System.out.println(key);
        //
        // for (String subValueKey : subValues.keySet()) {
        // System.out.println(String.format("\t%s = %s", subValueKey,
        // subValues.get(subValueKey)));
        // }
        // }

    }

}
