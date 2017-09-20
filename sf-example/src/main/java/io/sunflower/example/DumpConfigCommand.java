package io.sunflower.example;

import net.sourceforge.argparse4j.inf.Namespace;

import io.sunflower.Configuration;
import io.sunflower.cli.ConfiguredCommand;
import io.sunflower.setup.Bootstrap;

public class DumpConfigCommand extends ConfiguredCommand<ExampleConfiguration> {

    public DumpConfigCommand() {
        super("dump", "dump configuration.");
    }

    @Override
    protected void run(Bootstrap bootstrap, Namespace namespace, ExampleConfiguration configuration) throws Exception {
        System.out.println(String.format(configuration.getTemplate(), configuration.getDefaultName()));
    }
}
