package ru.geekbrains.storage.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
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
    private String[] s;
    private File directoryForCopy, fileForCopy;

    private Path remoteRoot = Paths.get(System.getProperty("user.dir"));
    final private int level = remoteRoot.getNameCount() + 3;

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
        if (request instanceof PathRequest) {

            if (((PathRequest) request).getName() == null) {
                File dir = new File(String.valueOf(remoteRoot), login);
                if (dir.mkdirs() && !dir.exists()) {
                    Server.getLogger().info("Directory created");
                }
                remoteRoot = dir.toPath();
                s = String.valueOf(remoteRoot).split("\\\\", level);
                ctx.writeAndFlush(new PathResponse(s[s.length - 1]));
            } else if (((PathRequest) request).getName().equals("...BACK...")) {
                if (remoteRoot.getNameCount() > level - 2) {
                    remoteRoot = remoteRoot.getParent();
                    s = String.valueOf(remoteRoot).split("\\\\", level-1);
                    ctx.writeAndFlush(new PathResponse(s[s.length - 1]));
                }
            } else {
                remoteRoot = remoteRoot.resolve(((PathRequest) request).getName());
                s = String.valueOf(remoteRoot).split("\\\\", level - 1);
                ctx.writeAndFlush(new PathResponse(s[s.length - 1]));
            }
        }
        if (request instanceof GetFilesRequest) {
            Server.getLogger().info("List requested");
            GetFilesResponse response = new GetFilesResponse(showFiles(remoteRoot), s[s.length - 1]);
            ctx.writeAndFlush(response);
        }
        if (request instanceof UploadRequest) {
            Server.getLogger().info("Uploading...");
            String pathOfFile = String.format(remoteRoot + "\\%s", ((UploadRequest) request).getFilename());
            try (FileOutputStream fos = new FileOutputStream(pathOfFile)) {
                fos.write(((UploadRequest) request).getData());
            }
            if (new File(pathOfFile).exists()) {
                ctx.writeAndFlush(new UploadResponse(ResponseType.UPLOAD_OK));
            } else {
                ctx.writeAndFlush(new UploadResponse(ResponseType.UPLOAD_NO));
            }

        }
        if (request instanceof NewFolderRequest) {
            if (request.getType() == RequestType.NEW_REMOTE_FOLDER) {
                File dir = new File(String.valueOf(remoteRoot), ((NewFolderRequest) request).getName());
                if (dir.mkdirs() && !dir.exists()) {
                    Server.getLogger().info("Directory created");
                    ctx.writeAndFlush(new NewFolderResponse());
                }
                ctx.writeAndFlush(new GetFilesResponse(showFiles(remoteRoot), s[s.length - 1]));
            }
        }
        if (request instanceof CopyRequest) {
            if (request.getType() == RequestType.COPY_DIRECTORY) {
                copyDirectory(((CopyRequest) request).getName());
                ctx.writeAndFlush(new GetFilesResponse(showFiles(remoteRoot), s[s.length - 1]));

            } else {
                copyFile(((CopyRequest) request).getName());
                ctx.writeAndFlush(new GetFilesResponse(showFiles(remoteRoot), s[s.length - 1]));

            }
        }
        if (request instanceof PasteRequest){
            paste();
            ctx.writeAndFlush(new GetFilesResponse(showFiles(remoteRoot), s[s.length - 1]));
        }
        if (request instanceof DeleteRequest){
            delete(((DeleteRequest) request).getFileName());
            ctx.writeAndFlush(new GetFilesResponse(showFiles(remoteRoot), s[s.length - 1]));
        }

        if (request instanceof RenameRequest){
           rename(((RenameRequest) request).getOldName(), ((RenameRequest) request).getNewName());
            ctx.writeAndFlush(new GetFilesResponse(showFiles(remoteRoot), s[s.length - 1]));
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

    private void paste(){
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
