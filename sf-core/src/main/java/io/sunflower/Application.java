package io.sunflower;

import ch.qos.logback.classic.Level;
import com.google.common.base.Stopwatch;
import io.sunflower.cli.CheckCommand;
import io.sunflower.cli.Cli;
import io.sunflower.cli.ServerCommand;
import io.sunflower.logging.BootstrapLogging;
import io.sunflower.setup.Bootstrap;
import io.sunflower.setup.Environment;
import io.sunflower.util.Generics;
import io.sunflower.util.JarLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The base class for Sunflower applications.
 *
 * Because the default constructor will be inherited by all subclasses,
 * {BootstrapLogging.bootstrap()} will always be invoked. The log level used during the bootstrap
 * process can be configured by {Application} subclasses by overriding {#bootstrapLogLevel}.
 *
 * @param <T> the type of configuration class for this application
 */
public abstract class Application<T extends Configuration> {

  protected Logger logger = LoggerFactory.getLogger(getClass());

  private static final String LOGO = "\n"
      + " _____              __ _                        \n"
      + "/  ___|            / _| |                       \n"
      + "\\ `--. _   _ _ __ | |_| | _____      _____ _ __ \n"
      + " `--. \\ | | | '_ \\|  _| |/ _ \\ \\ /\\ / / _ \\ '__| \n"
      + "/\\__/ / |_| | | | | | | | (_) \\ V  V /  __/ |    http://www.sunflower.io\n"
      + "\\____/ \\__,_|_| |_|_| |_|\\___/ \\_/\\_/ \\___|_|    @sunflower({})\n"
      + "       framework                                                 \n";

  protected Application() {
    bootstrapLogging();
  }

  /**
   * The log level at which to bootstrap logging on application startup.
   */
  protected Level bootstrapLogLevel() {
    return Level.INFO;
  }

  protected void bootstrapLogging() {
    // make sure spinning up Hibernate Validator doesn't yell at us
    BootstrapLogging.bootstrap(bootstrapLogLevel());
  }

  /**
   * Returns the {@link Class} of the configuration class type parameter.
   *
   * @return the configuration class
   * @see Generics#getTypeParameter(Class, Class)
   */
  public Class<T> getConfigurationClass() {
    return Generics.getTypeParameter(getClass(), Configuration.class);
  }

  /**
   * Returns the name of the application.
   *
   * @return the application's name
   */
  public String getName() {
    return getClass().getSimpleName();
  }

  /**
   * Initializes the application bootstrap.
   *
   * @param bootstrap the application bootstrap
   */
  public void initialize(Bootstrap<T> bootstrap) {
  }

  /**
   * When the application runs, this is called after the {@link Bundle}s are run. Override it to add
   * providers, resources, etc. for your application.
   *
   * @param configuration the parsed {@link Configuration} object
   * @param environment the application's {@link Environment}
   * @throws Exception if something goes wrong
   */
  public abstract void run(T configuration, Environment environment) throws Exception;

  /**
   * Parses command-line arguments and runs the application. Call this method from a {@code public
   * static void main} entry point in your application.
   *
   * @param arguments the command-line arguments
   * @throws Exception if something goes wrong
   */
  public void run(String... arguments) throws Exception {
    Stopwatch sw = Stopwatch.createStarted();

    showSplashScreenViaLogger();

    final Bootstrap<T> bootstrap = new Bootstrap<>(this);
    addDefaultCommands(bootstrap);

    initialize(bootstrap);

    // Should be called after initialize to give an opportunity to set a custom metric register
    bootstrap.registerMetrics();

    final Cli cli = new Cli(new JarLocation(getClass()), bootstrap, System.out, System.err);
    if (!cli.run(arguments)) {
      // only exit if there's an error running the command
      onFatalError();
    }
    sw.stop();

    logger.info(getName() + " started in {}", sw);
  }

  /**
   * Called by {@link #run(String...)} to add the standard "server" and "check" commands
   *
   * @param bootstrap the bootstrap instance
   */
  protected void addDefaultCommands(Bootstrap<T> bootstrap) {
    bootstrap.addCommand(new CheckCommand<>(this));
    bootstrap.addCommand(new ServerCommand<>(this));
  }

  /**
   * Called by {@link #run(String...)} to indicate there was a fatal error running the requested
   * command.
   *
   * The default implementation calls {@link System#exit(int)} with a non-zero status code to
   * terminate the application.
   */
  protected void onFatalError() {
    System.exit(1);
  }

  private void showSplashScreenViaLogger() {

    String sunflowerVersion = Application.class.getPackage().getImplementationVersion();

    // log Sunflower splash screen
    logger.info(LOGO, sunflowerVersion);

  }
}
