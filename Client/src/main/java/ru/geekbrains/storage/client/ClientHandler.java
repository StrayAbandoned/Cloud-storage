package ru.geekbrains.storage.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import ru.geekbrains.storage.*;

public class ClientHandler extends ChannelInboundHandlerAdapter {

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
                ClientService.setLogin(((AuthResponse)response).getLogin());
                ctx.writeAndFlush(new PathRequest());
            }
            case AUTH_NO -> {
                ClientService.getMainController().setAuthenticated(false);

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
            case CHANGE_OK,CHANGE_NO ->{
                if(response.getType().equals(ResponseType.CHANGE_OK)){
                    ClientService.getSettingController().getResult().setText("Password changed!");

                    ctx.writeAndFlush(new GetFilesRequest());
                } else {
                    ClientService.getSettingController().getResult().setText("Password wasn't changed!");
                    ctx.writeAndFlush(new GetFilesRequest());

                }
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
