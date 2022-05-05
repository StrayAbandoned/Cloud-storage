package ru.geekbrains.storage.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import ru.geekbrains.storage.BasicRequest;
import ru.geekbrains.storage.FileInfo;
import ru.geekbrains.storage.UploadRequest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Server {
    private final static int PORT = 26894;
    private final static int FILE_SIZE = 10_485_760;
    private Authentication authentication;

    private LogManager logManager = LogManager.getLogManager();
    private static Logger logger = Logger.getLogger(Server.class.getName());
    private Server ser = this;



    public Server getSer() {
        return ser;
    }

    Server() throws FileNotFoundException, IOException {
        logManager.readConfiguration(new FileInputStream("Server/logging.properties"));
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        authentication = new Authentication();
        try {

            ServerBootstrap server = new ServerBootstrap();
            server.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(
                                    new ObjectDecoder(FILE_SIZE, ClassResolvers.weakCachingConcurrentResolver(Server.class.getClassLoader())),
                                    new ObjectEncoder(),
                                    new MainHandler(getSer())
                                    );

                        }
                    });
            ChannelFuture future = server.bind(PORT).sync();
            Server.getLogger().info("Server started");
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public Authentication getAuthentication() {
        return authentication;
    }

    public static Logger getLogger() {
       return logger;
    }




}
