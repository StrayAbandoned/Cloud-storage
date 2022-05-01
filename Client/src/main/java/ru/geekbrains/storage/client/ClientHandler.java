package ru.geekbrains.storage.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import ru.geekbrains.storage.*;

import java.nio.file.Path;


public class ClientHandler extends ChannelInboundHandlerAdapter{
    Network network;

    public ClientHandler(Network network) {
        this.network = network;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        BasicResponse response = (BasicResponse) msg;
        System.out.println(response.getType());
        if(response instanceof RegResponse){
            if(response.getType() == ResponseType.REG_OK){
                ClientService.getRegController().regInfo("Registration successful!");

            } else {
                ClientService.getRegController().regInfo("Registration failed!");
            }
        }
        if(response instanceof AuthResponse){
            if(response.getType() == ResponseType.AUTH_OK){
                ClientService.getMainController().setAuthenticated(true);
                ctx.writeAndFlush(new PathRequest());
     //           ctx.writeAndFlush(new GetFilesRequest());
            } else {
                ClientService.getMainController().setAuthenticated(false);
                ClientService.getMainController().failAuth("Wrong login/password");
            }

        }
        if(response instanceof PathResponse){
            ClientService.setPath(((PathResponse) response).getPath());
            ctx.writeAndFlush(new GetFilesRequest(ClientService.getPath()));
        }

        if(response instanceof GetFilesResponse){
            ClientService.getMainController().showRemoteFiles(((GetFilesResponse) response).getFiles(), Path.of(((GetFilesResponse) response).getPath()));
        }

        if (response instanceof UploadResponse){
            ctx.writeAndFlush(new GetFilesRequest(ClientService.getPath()));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
