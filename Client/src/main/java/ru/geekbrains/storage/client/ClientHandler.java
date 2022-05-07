package ru.geekbrains.storage.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import ru.geekbrains.storage.*;

public class ClientHandler extends ChannelInboundHandlerAdapter {

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
        ClientService.getLogger().info(String.valueOf(response.getType()));
        switch (response.getType()) {
            case REG_OK -> ClientService.getRegController().regInfo("Registration successful!");
            case REG_NO -> ClientService.getRegController().regInfo("Registration failed!");
            case AUTH_OK -> {
                ClientService.getMainController().setAuthenticated(true);
                ctx.writeAndFlush(new PathRequest());
            }
            case AUTH_NO -> {
                ClientService.getMainController().setAuthenticated(false);
                ClientService.getMainController().failAuth();
            }
            case PATH, UPLOAD_NO, UPLOAD_OK -> ctx.writeAndFlush(new GetFilesRequest());
            case GET_FILES -> ClientService.getRemoteController().showRemoteFiles(((GetFilesResponse) response).getFiles(), ((GetFilesResponse) response).getPath());
            case NEW_FOLDER -> {
                ClientService.getRemoteController().getNameStage().close();
                ClientService.setServerMarker(false);
                ctx.writeAndFlush(new GetFilesRequest());
            }
            case DOWNLOAD -> {
                ClientService.getLocalController().saveDownloadedFile(((DownloadResponse) response).getFilename(), ((DownloadResponse) response).getData());
                ctx.writeAndFlush(new GetFilesRequest());
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
