package ru.geekbrains.storage.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import ru.geekbrains.storage.*;

import java.util.logging.LogManager;
import java.util.logging.Logger;

public class MainHandler extends ChannelInboundHandlerAdapter {
    Server server;
    MainHandler(Server server){
        this.server = server;

    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        BasicRequest request = (BasicRequest) msg;
        if(request.getType().equals(RequestType.REG)){
            if(server.getAuthentication().registration((RegRequest) msg)){
               ctx.writeAndFlush(new RegResponse(ResponseType.REG_OK));
               Server.getLogger().info("Registration successful");
            } else {
                ctx.writeAndFlush(new RegResponse(ResponseType.REG_NO));
                Server.getLogger().info("Registration failed");
            };

        }
        if(request.getType().equals(RequestType.AUTH)){
            if(server.getAuthentication().login((AuthRequest) msg)){
                ctx.writeAndFlush(new RegResponse(ResponseType.AUTH_OK));
                Server.getLogger().info("Client logged in");
            } else {
                ctx.writeAndFlush(new RegResponse(ResponseType.AUTH_NO));
                Server.getLogger().info("Login failed");
            };

        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Клиент подключился");;
    }



    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
