package ru.geekbrains.storage.client;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import ru.geekbrains.storage.BasicRequest;

public class Network {
    private final int PORT = 26894;
    private final String HOSTNAME = "localhost";
    private final int FILE_SIZE = Integer.MAX_VALUE;
    private Channel channel;
    private final Network network = this;

    public Network(){
        ClientService.setNetwork(network);

        new Thread(() -> {
            EventLoopGroup workerGroup = new NioEventLoopGroup();

            try {
                Bootstrap boot = new Bootstrap();
                boot.group(workerGroup)
                        .channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<SocketChannel>() {

                            @Override
                            protected void initChannel(SocketChannel socketChannel) {
                                //channel = socketChannel;
                                socketChannel.pipeline().addLast(
                                        new ObjectDecoder(FILE_SIZE, ClassResolvers.weakCachingResolver(Network.class.getClassLoader())),
                                        new ObjectEncoder(),
                                        new ClientHandler());
                            }
                        });
                ChannelFuture future = boot.connect(HOSTNAME,PORT);
                channel = future.channel();
                future.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                workerGroup.shutdownGracefully();
            }
        }).start();
    }

    public void sendFiles(BasicRequest b){
        try {
            channel.writeAndFlush(b).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    public void close(){
        channel.close();
    }

}
