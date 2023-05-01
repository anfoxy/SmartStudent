package org.example;

import grpc.main.GameGrpc;
import grpc.main.Move;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EchoServer extends GameGrpc.GameImplBase {
    private static List<StreamObserver<Move>> observers = new ArrayList<>();

    @Override
    public StreamObserver<Move> startGame(StreamObserver<Move> responseObserver) {
        observers.add(responseObserver);

        if (observers.size() == 2) {
            observers.get(0).onNext(Move.newBuilder().setPlayerNum(1).build());
            observers.get(1).onNext(Move.newBuilder().setPlayerNum(2).build());
            observers.get(0).onNext(Move.newBuilder().setX(0).setY(0).build());
            observers.get(1).onNext(Move.newBuilder().setX(0).setY(0).build());
            return new StreamObserver<Move>() {
                @Override
                public void onNext(Move move) {
                    sendMessageToAllClients(move);
                }
                @Override
                public void onError(Throwable throwable) {
                    observers.remove(responseObserver);
                }

                @Override
                public void onCompleted() {
                    observers.remove(responseObserver);
                }
            };
        } else {
            return new StreamObserver<Move>() {
                @Override
                public void onNext(Move move) {
                }

                @Override
                public void onError(Throwable throwable) {
                    observers.remove(responseObserver);
                }

                @Override
                public void onCompleted() {
                    observers.remove(responseObserver);
                }
            };
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        int port = 8080;
        Server server = ServerBuilder.forPort(port).addService(new EchoServer()).build();

        System.out.println("Starting server on port " + port);
        server.start();
        server.awaitTermination();
    }

    private static void sendMessageToAllClients(Move move) {
        for (StreamObserver<Move> observer : observers) {
            observer.onNext(move);
        }
    }
}