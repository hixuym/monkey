package io.monkey.grpc.server;

import io.monkey.server.Server;
import io.monkey.setup.Environment;

public class GrpcServer extends Server {

    private io.grpc.Server grpcServer;

    public GrpcServer(Environment environment, io.grpc.Server grpcServer) {
        super(environment);
        this.grpcServer = grpcServer;
    }

    @Override
    protected void boot() throws Exception {

        grpcServer.start();

        logger.info("gRPC Server started, listening on " + grpcServer.getPort());

        grpcServer.awaitTermination();
    }

    @Override
    protected void shutdown() {
        grpcServer.shutdown();

        logger.info("gRPC Server stoped.");
    }
}
