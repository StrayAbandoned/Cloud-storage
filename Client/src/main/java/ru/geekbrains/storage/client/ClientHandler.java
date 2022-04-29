package ru.geekbrains.storage.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import ru.geekbrains.storage.*;


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
        if(response.getType().equals(ResponseType.REG_OK)||response.getType().equals(ResponseType.REG_NO)){
            if(response.getType().equals(ResponseType.REG_OK)){
                ClientService.getRegController().regInfo("Registration successful!");

            } else {
                ClientService.getRegController().regInfo("Registration failed!");
            };

        }
        if(response.getType().equals(ResponseType.AUTH_OK)||response.getType().equals(ResponseType.AUTH_NO)){
            if(response.getType().equals(ResponseType.AUTH_OK)){
                ClientService.getMainController().setAuthenticated(true);
            } else {
                ClientService.getMainController().setAuthenticated(false);
                ClientService.getMainController().failAuth("Wrong login/password");

            };

        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
