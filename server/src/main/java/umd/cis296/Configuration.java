package umd.cis296;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.tinylog.Logger;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

public class Configuration implements Serializable{

    private static final Path FILE_PATH = Path.of("./config.yml");
    
    private static Configuration INSTANCE = null;

    private static void createDefaultConfig(Path path) {
        Logger.info("Creating Default Config File");

        try {
            Files.deleteIfExists(path);
        }
        catch(IOException ex) {
            Logger.warn(ex);
        }

        Configuration config = new Configuration();

        DumperOptions yamlOptions = new DumperOptions();
        yamlOptions.setIndent(2);
        yamlOptions.setPrettyFlow(true);
        yamlOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        
        Representer representer = new Representer(yamlOptions);
        representer.addClassTag(Configuration.class, Tag.MAP);

        Yaml configYaml = new Yaml(representer);

        try (FileWriter configWriter = new FileWriter(path.toString())) {
            String yamlContent = configYaml.dumpAs(config, Tag.MAP, DumperOptions.FlowStyle.BLOCK);
            configWriter.write(yamlContent);
            Logger.info("Config File Created");
        } catch(IOException ex) {
            Logger.error(ex);
        }
    }

    private static Configuration readConfig(Path path) {
        if (!Files.exists(path)) {
            Logger.info("Configuration File Not Found");
            createDefaultConfig(path);
        }

        Logger.info("Reading Configuration File `{}`", path);

        try (FileInputStream configFin = new FileInputStream(path.toString())) {
            LoaderOptions loaderOptions = new LoaderOptions();
            Constructor constructor = new Constructor(loaderOptions);
            Yaml configYaml = new Yaml(constructor);
            Configuration config = configYaml.loadAs(configFin, Configuration.class);

            if (config == null) {
                throw new NullPointerException();
            }

            return config;
        }
        catch (Exception ex) {
            Logger.error(ex);
            return null;
        }
    }

    public static Configuration instance() {
        if (INSTANCE == null) {
            if ((INSTANCE = readConfig(FILE_PATH)) == null) {
                Logger.error("Unable To Read Config");
            }
        }
        return INSTANCE;
    }

    //
    //
    //

    public String name;
    public int port;
    public int threads;
    public List<String> channels;

    public Configuration() {
        this("A CIS-296 Final Project Server");
    }

    public Configuration(String name) {
        this.name = name;
        this.port = 5005;
        this.threads = 10;
        this.channels = new ArrayList<>() {{
            add("main");
        }};
    }
}
