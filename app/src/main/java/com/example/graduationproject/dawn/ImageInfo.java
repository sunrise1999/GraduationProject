package com.example.graduationproject.dawn;

import java.io.Serializable;

public class ImageInfo implements Serializable {
    private String file_name;
    private String file_path;
    private String file_date;

    public String getFileName() {
        return file_name;
    }

    public void setFileName(String file_name) {
        this.file_name = file_name;
    }

    public String getFilePath() {
        return file_path;
    }

    public void setFilePath(String file_path) {
        this.file_path = file_path;
    }

    public String getFileDate() {
        return file_date;
    }

    public void setFileDate(String file_date) {
        this.file_date = file_date;
    }
}
