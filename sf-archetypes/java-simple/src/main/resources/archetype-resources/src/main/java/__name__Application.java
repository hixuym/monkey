package ${package};

import io.sunflower.Application;
import io.sunflower.setup.Bootstrap;
import io.sunflower.setup.Environment;

public class ${name}Application extends Application<${name}Configuration> {

    public static void main(final String[] args)throws Exception {
        new ${name}Application().run(args);
    }

    @Override
    public String getName() {
        return"${name}";
    }

    @Override
    public void initialize(final Bootstrap<${name}Configuration> bootstrap) {
        // TODO: application initialization
    }

    @Override
    public void run(final ${name}Configuration configuration,final Environment environment) {
        // TODO: implement application
    }

}
