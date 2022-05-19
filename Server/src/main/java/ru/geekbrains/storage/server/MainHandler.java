package ru.geekbrains.storage.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.commons.io.FileUtils;
import ru.geekbrains.storage.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;


public class MainHandler extends ChannelInboundHandlerAdapter {

    private final Server server;
    private String login;
    private File directoryForCopy, fileForCopy;
    private String path;
    private Path remoteRoot = Paths.get(System.getProperty("user.dir"));
    private long quota;
    private long totalSize;

    MainHandler(Server server) {
        this.server = server;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        BasicRequest request = (BasicRequest) msg;
        switch (request.getType()) {
            case REG -> {
                if (server.getAuthentication().registration((RegRequest) msg)) {
                    ctx.writeAndFlush(new RegResponse(ResponseType.REG_OK));
                    Server.getLogger().info("Registration successful");
                } else {
                    ctx.writeAndFlush(new RegResponse(ResponseType.REG_NO));
                    Server.getLogger().info("Registration failed");
                }
            }
            case AUTH -> {
                if (server.getAuthentication().login((AuthRequest) msg)) {
                    login = server.getAuthentication().getLogin((AuthRequest) msg);
                    path = login;

                    ctx.writeAndFlush(new AuthResponse(ResponseType.AUTH_OK, login));
                    Server.getLogger().info("Client logged in");
                } else {
                    ctx.writeAndFlush(new AuthResponse(ResponseType.AUTH_NO));
                    Server.getLogger().info("Login failed");
                }
            }
            case PATH -> {
                if(login!=null){

                    if (((PathRequest) request).getName() == null) {
                        File dir = new File(String.valueOf(remoteRoot), login);
                        if (dir.mkdirs() && !dir.exists()) {
                            Server.getLogger().info("Directory created");
                        }
                        remoteRoot = dir.toPath();
                        totalSize = FileUtils.sizeOfDirectory(new File(System.getProperty("user.dir"), login));
                        quota = Long.parseLong(server.getAuthentication().getQuota(login)) - totalSize;
                        ctx.writeAndFlush(new PathResponse(path));
                    } else if (((PathRequest) request).getName().equals("BACK")) {
                        Path p = Paths.get(path);
                        if (p.getParent()!=null){
                            path = String.valueOf(p.getParent());
                            remoteRoot = remoteRoot.getParent();
                            ctx.writeAndFlush(new PathResponse(path));
                        }
                    } else {
                        remoteRoot = remoteRoot.resolve(((PathRequest) request).getName());
                        Path p = Paths.get(path).resolve(((PathRequest) request).getName());
                        path = String.valueOf(p);
                        ctx.writeAndFlush(new PathResponse(path));
                    }
                }

            }
            case GET_FILES -> {
                if(login!=null){
                    Server.getLogger().info("List requested");
                    GetFilesResponse response = new GetFilesResponse(showFiles(remoteRoot), path);
                    ctx.writeAndFlush(response);
                }


            }
            case DOWNLOAD -> {
                if (login!=null){
                    ctx.writeAndFlush(new DownloadResponse(new File(String.valueOf(remoteRoot), ((DownloadRequest) request).getName())));
                }


            }
            case COPY_FILE -> {
                if(login!=null){
                    copyFile(((CopyRequest) request).getName());
                    ctx.writeAndFlush(new GetFilesResponse(showFiles(remoteRoot), path));
                }


            }
            case COPY_DIRECTORY -> {
                if(login!=null){
                    copyDirectory(((CopyRequest) request).getName());
                    ctx.writeAndFlush(new GetFilesResponse(showFiles(remoteRoot), path));
                }


            }
            case PASTE -> {
                if(login!=null){
                    paste();
                    ctx.writeAndFlush(new GetFilesResponse(showFiles(remoteRoot), path));
                }


            }
            case DELETE -> {
                if(login!=null){
                    delete(((DeleteRequest) request).getFileName());
                    ctx.writeAndFlush(new GetFilesResponse(showFiles(remoteRoot), path));
                    totalSize = FileUtils.sizeOfDirectory(new File(System.getProperty("user.dir"), login));
                    quota = Long.parseLong(server.getAuthentication().getQuota(login)) - totalSize;
                }


            }
            case RENAME -> {
                if(login!=null){
                    rename(((RenameRequest) request).getOldName(), ((RenameRequest) request).getNewName());
                    ctx.writeAndFlush(new GetFilesResponse(showFiles(remoteRoot), path));
                }


            }
            case UPLOAD -> {
                if(login!=null){
                    String pathOfFile = String.format(remoteRoot + "\\%s", ((UploadRequest) request).getFilename());
                    if(((UploadRequest) request).getSize()<=quota){
                        try (FileOutputStream fos = new FileOutputStream(pathOfFile)) {
                            fos.write(((UploadRequest) request).getData());
                        }
                        totalSize = FileUtils.sizeOfDirectory(new File(System.getProperty("user.dir"), login));
                        quota = Long.parseLong(server.getAuthentication().getQuota(login)) - totalSize;
                    }

                    if (new File(pathOfFile).exists()) {
                        Server.getLogger().info("File uploaded");
                        ctx.writeAndFlush(new UploadResponse(ResponseType.UPLOAD_OK));
                    } else {
                        Server.getLogger().info("Uploading denied");
                        ctx.writeAndFlush(new UploadResponse(ResponseType.UPLOAD_NO));
                    }
                }



            }
            case NEW_REMOTE_FOLDER -> {
                if(login!=null){
                    File dir = new File(String.valueOf(remoteRoot), ((NewFolderRequest) request).getName());
                    if (dir.mkdirs() && !dir.exists()) {
                        Server.getLogger().info("Directory created");
                        ctx.writeAndFlush(new NewFolderResponse());
                    }
                    ctx.writeAndFlush(new GetFilesResponse(showFiles(remoteRoot), path));
                }


            }
            case CHANGE_PASSWORD -> {
                if(login!=null){
                    if(server.getAuthentication().changePassword((ChangePasswordRequest) msg)){
                        ctx.writeAndFlush(new ChangePasswordResponse(ResponseType.CHANGE_OK));
                    } else{
                        ctx.writeAndFlush(new ChangePasswordResponse(ResponseType.CHANGE_NO));
                    }
                }


            }
            case LOGOUT -> {
                login = null;
                path = null;
                directoryForCopy = null;
                fileForCopy = null;
                quota = 0L;
                totalSize = 0L;
                remoteRoot = Paths.get(System.getProperty("user.dir"));
                Server.getLogger().info("Log out");

            }
        }
    }

    private void delete(String fileName) {
        FileUtils.deleteQuietly(new File(String.valueOf(remoteRoot), fileName));
    }

    private void copyFile(String name) {
        fileForCopy = new File(String.valueOf(remoteRoot), name);
        directoryForCopy = null;
    }

    private void copyDirectory(String name) {
        directoryForCopy = new File(String.valueOf(remoteRoot), name);
        fileForCopy = null;
    }

    public List<FileInfo> showFiles(Path rootPath) {
        List<FileInfo> out = new CopyOnWriteArrayList<>();
        try {
            List<Path> paths = null;
            paths = Files.list(rootPath).collect(Collectors.toList());
            for (Path p : paths) {
                out.add(new FileInfo(p));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return out;

    }

    private void paste() {
        if (fileForCopy != null) {
            try {
                FileUtils.copyFile(fileForCopy, new File(String.valueOf(remoteRoot), fileForCopy.getName()));
                fileForCopy = null;
                directoryForCopy = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (directoryForCopy != null) {
            try {
                FileUtils.copyDirectory(directoryForCopy, new File(String.valueOf(remoteRoot), directoryForCopy.getName()));
                fileForCopy = null;
                directoryForCopy = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void rename(String oldName, String newName) {
        File dir = new File(String.valueOf(remoteRoot), oldName);
        dir.renameTo(new File(String.valueOf(remoteRoot), newName));

    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Клиент подключился");

    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
