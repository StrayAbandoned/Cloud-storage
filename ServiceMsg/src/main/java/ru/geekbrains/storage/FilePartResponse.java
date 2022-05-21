package ru.geekbrains.storage;



public class FilePartResponse implements BasicResponse{
    private String fileName;
    private long fileLength;
    private byte[] partBytes;
    private int partBytesLen;
    private String pathToStr;
    private ResponseType type;

    public FilePartResponse(String fileName, long fileLength, byte[] partBytes, int partBytesLen) {
        this.fileName = fileName;
        this.fileLength = fileLength;
        this.partBytes = partBytes;
        this.partBytesLen = partBytesLen;
        type = ResponseType.FILE_PART;
    }
    @Override
    public ResponseType getType() {
        return type;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileLength() {
        return fileLength;
    }

    public void setFileLength(long fileLength) {
        this.fileLength = fileLength;
    }

    public byte[] getPartBytes() {
        return partBytes;
    }

    public void setPartBytes(byte[] partBytes) {
        this.partBytes = partBytes;
    }

    public int getPartBytesLen() {
        return partBytesLen;
    }

    public void setPartBytesLen(int partBytesLen) {
        this.partBytesLen = partBytesLen;
    }

    public String getPathToStr() {
        return pathToStr;
    }

    public void setPathToStr(String pathToStr) {
        this.pathToStr = pathToStr;
    }
}
