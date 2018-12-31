package io.monkey.grpc.server;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.inject.Binding;
import com.google.inject.Injector;
import io.grpc.BindableService;
import io.grpc.ServerBuilder;
import io.monkey.MonkeyException;
import io.monkey.server.Server;
import io.monkey.server.ServerFactory;
import io.monkey.setup.Environment;
import io.monkey.validation.PortRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@JsonTypeName("grpc")
public class GrpcServerFactory implements ServerFactory {

    private static Logger logger = LoggerFactory.getLogger(GrpcServerFactory.class);

    @JsonProperty
    @PortRange
    private int port = 50051;

    @Override
    public Server build(Environment environment) {

        ServerBuilder builder = ServerBuilder.forPort(port);

        scanServices(environment.getInjector(), builder);

        io.grpc.Server server = builder.build();

        if (server.getServices().isEmpty()) {
            throw new MonkeyException("no grpc service founded.");
        }

        return new GrpcServer(environment, server);
    }

    @Override
    public void configure(Environment environment) {
    }

    private void scanServices(Injector injector, ServerBuilder builder) {
        List<Binding<?>> rootResourceBindings = new ArrayList<>();
        for (final Binding<?> binding : injector.getBindings().values()) {
            final Type type = binding.getKey().getTypeLiteral().getRawType();
            if (type instanceof Class) {
                final Class<?> beanClass = (Class) type;

                if (BindableService.class.isAssignableFrom(beanClass)) {
                    logger.info("registering grpc instance for {}", beanClass.getName());
                    builder.addService((BindableService) binding.getProvider().get());
                }
            }
        }
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
