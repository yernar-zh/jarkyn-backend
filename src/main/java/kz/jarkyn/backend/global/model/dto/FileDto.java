package kz.jarkyn.backend.global.model.dto;

import java.io.InputStream;

public class FileDto {
    private final InputStream inputStream;
    private final String contentDisposition;
    private final String contentType;
    private final long size;

    public FileDto(InputStream inputStream, String contentDisposition, String contentType, long size) {
        this.inputStream = inputStream;
        this.contentDisposition = contentDisposition;
        this.contentType = contentType;
        this.size = size;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public String getContentDisposition() {
        return contentDisposition;
    }

    public String getContentType() {
        return contentType;
    }

    public long getSize() {
        return size;
    }
}