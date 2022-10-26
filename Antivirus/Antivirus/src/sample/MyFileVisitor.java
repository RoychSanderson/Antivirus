package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.zip.*;

public class MyFileVisitor extends SimpleFileVisitor<Path> {
    private final FileChecker fileChecker;
    private final Path dbPath;
    private boolean karantin = false;
    private boolean delete = false;
    private final TableView<FileData> table;
    private ObservableList<FileData> list = FXCollections.observableArrayList();
    ListView<FileData> listViewKarantin;
    private static boolean zipstatus = false;

    public MyFileVisitor(Path dbPath,TableView<FileData> table,  ListView<FileData> listViewKarantin) throws IOException {
        this.fileChecker = new FileChecker();
        this.dbPath = dbPath;
        this.table = table;
        this.listViewKarantin = listViewKarantin;
        fileChecker.insertDBtoList(dbPath);
    }

    public ObservableList<FileData> getList(){
        return list;
    }

    public void setList(ObservableList<FileData> list){
        this.list = list;
    }
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        String hash = "";
        boolean status;

        if (!Files.isDirectory(file)) {
//            System.out.println(Files.probeContentType(file));
//            if(Files.probeContentType(file).equals("application/x-zip-compressed")){
//                try(var zf = new ZipFile(file.toString()))
//                {
//                    Enumeration<? extends ZipEntry> zipEntries = zf.entries();
//                    zipEntries.asIterator().forEachRemaining(entry -> {
//                        if(!entry.isDirectory()){
////                            try {
////                                MyFileVisitor myFileVisitor = new MyFileVisitor(dbPath, table, listViewKarantin);
////                                Files.walkFileTree(Paths.get(entry.getName()), myFileVisitor);
////                                //    System.out.println(myFileVisitor.getList());
////                            } catch (IOException e) {
////                                e.printStackTrace();
////                            }
//                            try {
//                                Obr(Paths.get(file.toString() + "\\" + entry.getName()));
//                            } catch(IOException ei) {
//                                ei.printStackTrace();
//                            }
//                        }
//                    });
//
//                }
//                catch(Exception ex){
//                    System.out.println(ex.getMessage());
//                }
//            } else {
            status = fileChecker.check(file);
            try {
                if (Files.probeContentType(file).equals("application/x-zip-compressed")) {
                    String string = Path.of("UnzippedContents").toString() + "\\" + file.getFileName();
                    string = string.substring(0, string.length() - 4);
                    var outputPath = Path.of(string);
//                    System.out.println(outputPath);
                    if (Files.exists(outputPath))
                        recursiveDelete(outputPath.toFile());
                    Files.createDirectory(outputPath);

                    try (var zf = new ZipFile(file.toString())) {
                        Enumeration<? extends ZipEntry> zipEntries = zf.entries();
                        zipEntries.asIterator().forEachRemaining(entry -> {
                            try {
                                if (entry.isDirectory()) {
                                    var dirToCreate = outputPath.resolve(entry.getName());
                                    Files.createDirectories(dirToCreate);
                                } else {
                                    var fileToCreate = outputPath.resolve(entry.getName());
                                    Files.copy(zf.getInputStream(entry), fileToCreate);
                                }
                            } catch (IOException ei) {
                                ei.printStackTrace();
                            }
                        });
                    }
                    catch (IOException ei){
                        ei.printStackTrace();
                    }
                    MyFileVisitor myFileVisitor = new MyFileVisitor(dbPath, table, listViewKarantin);
                    try {
                        Files.walkFileTree(outputPath, myFileVisitor);
                        ObservableList<FileData> fileList = myFileVisitor.getList();
                        if (Files.exists(outputPath))
                            recursiveDelete(outputPath.toFile());
                        list.addAll(fileList);

                    } catch (IOException e) {
                        System.out.println("Нет такой папки или файла");
                    }
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
                if (file.toString().contains("UnzippedContents") && status)
                    zipstatus = true;
                if (file.toString().contains(".zip")){
                    status = zipstatus;
                    zipstatus = false;
                }

                FileData fileData = fileChecker.createFileData(file, status);
                if (!list.contains(fileData)){
                    if(delete){
                            try {
                                if(fileData.getStatus().equals("         Да")) {
                                    File myFile = new File(fileData.getPath());
                                    myFile.delete();
                                    System.out.println("Удален " + myFile.getName());
                                    list.add(fileData);
                                    table.getItems().add(fileData);
                                }
                            }
                            catch (IndexOutOfBoundsException e){
                                System.out.println("ЧТО ТО ПОШЛО ПЛОХО");
                            }
                        }
                    if (karantin){
                            try {
                                if(fileData.getStatus().equals("         Да")) {
                                    System.out.println("В процессе");
                                    listViewKarantin.getItems().add(fileData);
                                    System.out.println(fileData.getPath());
                                    File myFile = new File(fileData.getPath());
                                    fileData.setPathKarantin("Quarantine\\" + myFile.getName());
                                    myFile.renameTo(new File("Quarantine\\" + myFile.getName()));
                                    myFile.delete();
                                }
                                else{
                                    list.add(fileData);
                                    table.getItems().add(fileData);
                                }
                            }
                            catch (IndexOutOfBoundsException e){
                                System.out.println("ЧТО ТО ПОШЛО ПЛОХО");
                            }
                        }
                    else {
                        list.add(fileData);
                        table.getItems().add(fileData);
                    }
                }

        }
      //  System.out.println(list.size());
        return FileVisitResult.CONTINUE;
    }


    public void turnOnKarantion(boolean karantin){ this.karantin = karantin; }

    public void  turnOnDelete(boolean delete){ this.delete = delete; }

    public static void recursiveDelete(File file) {
        // до конца рекурсивного цикла
        if (!file.exists())
            return;

        //если это папка, то идем внутрь этой папки и вызываем рекурсивное удаление всего, что там есть
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                // рекурсивный вызов
                recursiveDelete(f);
            }
        }
        // вызываем метод delete() для удаления файлов и пустых(!) папок
        file.delete();
        System.out.println("Удаленный файл или папка: " + file.getAbsolutePath());
    }

    public void clearList(){
         this.list = FXCollections.observableArrayList();
    }
}