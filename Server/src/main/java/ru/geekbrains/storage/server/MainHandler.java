package ru.geekbrains.storage.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import ru.geekbrains.storage.*;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;


public class MainHandler extends ChannelInboundHandlerAdapter {
    private final Path remote = Paths.get(System.getProperty("user.dir"));
    private Path personalRemote;
    Server server;
    String login;
    String currentDirectory;

    MainHandler(Server server) {
        this.server = server;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        BasicRequest request = (BasicRequest) msg;
        if (request instanceof RegRequest) {
            if (server.getAuthentication().registration((RegRequest) msg)) {
                ctx.writeAndFlush(new RegResponse(ResponseType.REG_OK));
                Server.getLogger().info("Registration successful");
            } else {
                ctx.writeAndFlush(new RegResponse(ResponseType.REG_NO));
                Server.getLogger().info("Registration failed");
            }
            ;

        }
        if (request instanceof AuthRequest) {
            if (server.getAuthentication().login((AuthRequest) msg)) {
                login = server.getAuthentication().getLogin((AuthRequest) msg);
                ctx.writeAndFlush(new AuthResponse(ResponseType.AUTH_OK));
                Server.getLogger().info("Client logged in");
            } else {
                ctx.writeAndFlush(new AuthResponse(ResponseType.AUTH_NO));
                Server.getLogger().info("Login failed");
            }

        }
        if (request instanceof PathRequest){
            File dir = new File(String.valueOf(remote), login);
            if (dir.mkdirs() && !dir.exists()) {
                Server.getLogger().info("Directory created");
            }
            personalRemote = dir.toPath();
            currentDirectory = String.valueOf(personalRemote);
            ctx.writeAndFlush(new PathResponse(currentDirectory));
        }
        if (request instanceof GetFilesRequest) {
            Server.getLogger().info("List requested");
            File dir = new File(String.valueOf(remote), login);
            if (dir.mkdirs() && !dir.exists()) {
                Server.getLogger().info("Directory created");
            }
            currentDirectory = ((GetFilesRequest) request).getPath();
            GetFilesResponse response = new GetFilesResponse(server.showFiles(Path.of(currentDirectory)), currentDirectory);
            ctx.writeAndFlush(response);
        }
        if(request instanceof UploadRequest){
            Server.getLogger().info("Uploading...");
            String pathOfFile = String.format(((UploadRequest) request).getRemPath()+"\\%s",((UploadRequest)request).getFilename());
            System.out.println(pathOfFile);

            try (FileOutputStream fos = new FileOutputStream(pathOfFile)) {
                fos.write(((UploadRequest) request).getData());
            }
            if(new File(pathOfFile).exists()){
                ctx.writeAndFlush(new UploadResponse(ResponseType.UPLOAD_OK));
            } else {
                ctx.writeAndFlush(new UploadResponse(ResponseType.UPLOAD_NO));
            }

        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Клиент подключился");
        ;
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
