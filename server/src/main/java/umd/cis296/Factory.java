package umd.cis296;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Factory {

    private ExecutorService machines;

    private static Factory INSTANCE;

    static {
        INSTANCE = new Factory();
        INSTANCE.machines = Executors.newFixedThreadPool(Configuration.instance().threads);
    }

    public static Factory instance() {
        return INSTANCE;
    }

    public static void addMachine(Machine machine) {
        instance().machines.submit(machine);
    }
}
