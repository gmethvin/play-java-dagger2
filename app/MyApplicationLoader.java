import play.Application;
import play.ApplicationLoader;
import play.Environment;
import play.api.LoggerConfigurator$;
import scala.compat.java8.OptionConverters;

import java.util.Optional;

public class MyApplicationLoader implements ApplicationLoader {
    @Override
    public Application load(Context context) {
        final ClassLoader classLoader = context.environment().classLoader();
        final Optional<LoggerConfigurator> opt = LoggerConfigurator.fromClassLoader(classLoader);
        opt.ifPresent(lc -> lc.configure(context.environment()));

        MyComponentsFactory factory = DaggerMyComponentsFactory.builder()
                .applicationModule(new ApplicationModule(context))
                .build();

        return factory.componentsFromContext().javaApplication();
    }

}

class LoggerConfigurator {

    private final play.api.LoggerConfigurator delegate;

    public LoggerConfigurator(play.api.LoggerConfigurator delegate) {
        this.delegate = delegate;
    }

    public static Optional<LoggerConfigurator> fromClassLoader(ClassLoader classLoader) {
        return OptionConverters.toJava(LoggerConfigurator$.MODULE$.apply(classLoader)).map(LoggerConfigurator::new);
    }

    public void configure(Environment env) {
        delegate.configure(env.underlying());
    }

}
