package sample;

import javafx.beans.property.SimpleStringProperty;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class FileChecker {
    MessageDigest MD5;
    String dbPath;
    List<String> hashList;

    FileChecker(){
        try {
            this.MD5 = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException e){
            System.out.println("Algorithm Exception");
        }
    }
    public boolean check(Path filePath) throws IOException {
        String hash = getHash(filePath);

        if(this.hashList.contains(hash)){
            System.out.println("Найден вредоносный файл");
            System.out.println(filePath);
            return true;

        }
        else return false;

    }

    public void insertDBtoList(Path dbPath){ //выгрузка базы данных
        try {
            this.hashList = Files.readAllLines(dbPath);
        }
        catch (IOException e){
            System.out.println("Не получилось прочитать файл с базой данных");
        }
    }

    public FileData createFileData(Path filePath, Boolean status) throws IOException { //запись о сканировании
        FileData fileData = null;
        BasicFileAttributes attr = Files.readAttributes(filePath, BasicFileAttributes.class);

        String name = new File(String.valueOf(filePath)).getName();
        String path = filePath.toString();
        String dateCreated = attr.creationTime().toString();
        dateCreated = dateCreated.substring(0, 18);
        String dateModified = attr.lastModifiedTime().toString().toString();
        dateModified = dateModified.substring(0,18);
        String st;

        if (status){
            st = "         Да";
        }
        else {
            st = "        Нет";
        }
        fileData = new FileData(name,path, dateCreated, dateModified, st);

        return fileData;
    }

    public String getHash(Path filePath) throws IOException {
        byte[] fileBytes = Files.readAllBytes(filePath);
        BigInteger bigInteger;
        this.MD5.update(fileBytes);
        byte[] digest = MD5.digest();
        bigInteger = new BigInteger(1, digest);
        System.out.println(bigInteger.toString(16));
        return bigInteger.toString(16);

    }

}

