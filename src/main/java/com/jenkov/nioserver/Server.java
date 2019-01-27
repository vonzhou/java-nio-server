package com.jenkov.nioserver;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by jjenkov on 24-10-2015.
 */
public class Server {

    private SocketAcceptor socketAcceptor = null;
    private SocketProcessor socketProcessor = null;

    private int tcpPort = 0;
    private IMessageReaderFactory messageReaderFactory = null;
    private IMessageProcessor messageProcessor = null;

    public Server(int tcpPort, IMessageReaderFactory messageReaderFactory, IMessageProcessor messageProcessor) {
        this.tcpPort = tcpPort;
        this.messageReaderFactory = messageReaderFactory;
        this.messageProcessor = messageProcessor;
    }

    public void start() throws IOException {
        // TODO 可配置
        Queue socketQueue = new ArrayBlockingQueue(1024); //move 1024 to ServerConfig

        this.socketAcceptor = new SocketAcceptor(tcpPort, socketQueue);

        // 消息缓冲区
        MessageBuffer readBuffer = new MessageBuffer();
        MessageBuffer writeBuffer = new MessageBuffer();

        this.socketProcessor = new SocketProcessor(socketQueue, readBuffer, writeBuffer, this.messageReaderFactory, this.messageProcessor);

        Thread acceptorThread = new Thread(this.socketAcceptor);
        Thread processorThread = new Thread(this.socketProcessor);

        // 开启acceptor线程,processor线程
        acceptorThread.start();
        processorThread.start();
    }


}
