package sample;

import javafx.beans.property.SimpleStringProperty;

import java.util.Objects;

public class FileData {
    private String name;
    private String path;
    private String dateCreated;
    private String dateModified;
    private String status;
    private String pathKarantin;

    public FileData(String name, String path, String dateCreated, String dateModified, String status) {
        this.name = name;
        this.path = path;
        this.dateCreated = dateCreated;
        this.dateModified = dateModified;
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileData fileData = (FileData) o;
        return name.equals(fileData.name) && path.equals(fileData.path) && dateCreated.equals(fileData.dateCreated) && dateModified.equals(fileData.dateModified) && status.equals(fileData.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, path, dateCreated, dateModified, status);
    }

    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public String getDateModified() {
        return dateModified;
    }

    public String getStatus() {
        return status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public void setDateModified(String dateModified) {
        this.dateModified = dateModified;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPathKarantin(String pathKarantin) { this.pathKarantin = pathKarantin; }

    public String getPathKarantin() { return pathKarantin; }



}
