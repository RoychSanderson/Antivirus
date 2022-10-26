package sample;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.ResourceBundle;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


public class Controller {
    @FXML
    private ResourceBundle resources;

    @FXML
    private Button btnChooseDir;

    @FXML
    private TableView<FileData> tableView;

    @FXML
    private TableColumn<FileData, String> colName;

    @FXML
    private TableColumn<FileData, String> colPath;

    @FXML
    private TableColumn<FileData, String> colCre;

    @FXML
    private TableColumn<FileData, String> colMod;

    @FXML
    private TableColumn<FileData, String> colSts;

    @FXML
    private Button btnScanStop;

    @FXML
    private TextField fldTime;

    @FXML
    private Button btnAddDate;

    @FXML
    private TextArea txtDateList;

    @FXML
    private DatePicker fldDate;

    @FXML
    private ListView<String> listDate;

    @FXML
    private CheckBox checkMonitor;

    @FXML
    private Label lbStatus;

    @FXML
    private TextField fldPath;

    @FXML
    private Button btcChooseFile;

    @FXML
    private Button btnAddKarantin;

    @FXML
    private Button btnRemove;

    @FXML
    private ListView<FileData> listViewKarantin;

    @FXML
    private Button btnReturnFromKarantin;

    @FXML
    private CheckBox checkKarantin;

    @FXML
    private CheckBox checkDelete;

    RunnableScanner runnableScanner;
    Thread thread = null;
    RunnableTimeChecker runnableTimeChecker;
    ObservableList<FileData> listKarantin;

    @FXML
    void initialize() throws IOException {
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        fldPath.setEditable(false);
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        final FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All Files", "*.*"));
        String dbPath = "C:\\Users\\RoychPrime\\IdeaProjects\\Antivirus\\Antivirus\\test.txt";
        MyFileVisitor myFileVisitor = new MyFileVisitor(Path.of(dbPath), tableView, listViewKarantin);
        lbStatus.setVisible(false);
        runnableTimeChecker = new RunnableTimeChecker(listDate, runnableScanner, thread, tableView, dbPath, fldPath, listViewKarantin);
        Thread timeThread = new Thread(runnableTimeChecker);
        timeThread.start();

        colName = new TableColumn<FileData, String>("Наименование");
        colName.setCellValueFactory(new PropertyValueFactory<FileData, String>("name"));
        colName.setMinWidth(180);
        colName.setReorderable(false);
        colName.setSortable(false);
        colName.setResizable(false);

        colPath = new TableColumn<FileData, String>("Расположение");
        colPath.setCellValueFactory(new PropertyValueFactory<FileData, String>("path"));
        colPath.setMinWidth(410);
        colPath.setResizable(false);
        colPath.setReorderable(false);
        colPath.setSortable(false);

        colCre = new TableColumn<FileData, String>("Создан");
        colCre.setCellValueFactory(new PropertyValueFactory<FileData, String>("dateCreated"));
        colCre.setMinWidth(130);
        colCre.setResizable(false);
        colCre.setReorderable(false);
        colCre.setSortable(false);

        colMod = new TableColumn<FileData, String>("Изменен");
        colMod.setCellValueFactory(new PropertyValueFactory<FileData, String>("dateModified"));
        colMod.setMinWidth(130);
        colMod.setResizable(false);
        colMod.setReorderable(false);
        colMod.setSortable(false);

        colSts = new TableColumn<FileData, String>("Заражение");
        colSts.setCellValueFactory(new PropertyValueFactory<FileData, String>("status"));
        colSts.setMinWidth(85);
        colSts.setResizable(false);
        colSts.setReorderable(false);
        colSts.setSortable(true);

        tableView.getColumns().add(colName);
        tableView.getColumns().add(colPath);
        tableView.getColumns().add(colCre);
        tableView.getColumns().add(colMod);
        tableView.getColumns().add(colSts);

        btnScanStop.setOnAction(actionEvent -> {
            if (runnableScanner != null){
                runnableScanner.stop();
            }
            runnableTimeChecker.stopScan();
        }); //остановка сканирования


       btnAddDate.setOnAction(actionEvent -> {
            System.out.println(fldDate.getValue());
            System.out.println(fldTime.getText());
            String strDate;
            try {
                strDate = fldDate.getValue().toString() + " " + fldTime.getText();
            }
            catch (NullPointerException e){
                System.out.println("Пустые поля");
                return;
            }
            System.out.println(strDate);
            Date date = null;
            try{
                date = formater.parse(strDate);
                System.out.println(date);
            }
            catch (ParseException e){
                System.out.println("Некорректная дата");
            }
            listDate.getItems().add(date.toString());
            Date dateCur = new Date();
            dateCur.setSeconds(0);
            System.out.println(dateCur);

            System.out.println(dateCur.toString().equals(date.toString()));
        }); //кнопка даты

       btnRemove.setOnAction(actionEvent -> {
           try {
               FileData fileData = tableView.getSelectionModel().getSelectedItem();
               File myFile = new File(fileData.getPath());
               myFile.delete();
               File myFile1 = new File(fileData.getPath());
               myFile1.delete();
               System.out.println("Удален файл " + myFile.getName());
               tableView.getItems().remove(fileData);
//               if(fileData.getStatus().equals("         Да")) {
//                   File myFile1 = new File(fileData.getPath());
//                   myFile1.delete();
//                   System.out.println("удален" + myFile.getName());
//                   tableView.getItems().remove(fileData);
//               }
//               else lbStatus.setText("Файл не заражен");
           }
           catch (NullPointerException e){
               System.out.println("Нечего удалять");
               lbStatus.setText("Выберите файл");
           }

           try {
               FileData selectedFile = listViewKarantin.getSelectionModel().getSelectedItem();
               System.out.println(selectedFile.getPathKarantin());
               File file = new File(selectedFile.getPathKarantin());
               listViewKarantin.getItems().remove(selectedFile);
               file.delete();
               lbStatus.setText("Файл успешно удален");
           }
           catch (NullPointerException e){
               System.out.println("Нечего удалять");
               lbStatus.setText("Выберите файл");
           }
       }); //удаление

       btcChooseFile.setOnAction(actionEvent -> {
           if (runnableScanner != null ){
               runnableScanner.stop();
           }
            fldPath.clear();
            tableView.getItems().clear();
            File file = fileChooser.showOpenDialog(Main.getPrimary());
            if (file != null) {
                fldPath.setText(file.getAbsolutePath());
            }
           String path1 = fldPath.getText();
           if (path1.length() == 0){
               System.out.println("Пустой путь");
               return;
           }

            try {
                Files.walkFileTree(Paths.get(path1), myFileVisitor);
                ObservableList<FileData> fileList = myFileVisitor.getList();
                tableView.setItems(fileList);
                myFileVisitor.clearList();
            } catch (IOException e) {
                System.out.println("Нет такой папки или файла");
            }
        }); //выбор файла

        btnChooseDir.setOnAction(actionEvent -> {
            fldPath.clear();
            tableView.getItems().clear();
            File dir = directoryChooser.showDialog(Main.getPrimary());
            if (dir != null) {
                fldPath.setText(dir.getAbsolutePath());
            }

            String path1 = fldPath.getText();
            if (path1.length() == 0){
                System.out.println("Пустой путь");
              //  lbStatus.setText("Нечего сканировать");
                return;
            }
            try {
                runnableScanner = new RunnableScanner(dbPath, path1, tableView, listViewKarantin);
                runnableScanner.turnOnMonitor(checkMonitor.isSelected());
                runnableScanner.turnOnDelete(checkDelete.isSelected());
                runnableScanner.turnOnKarantin(checkKarantin.isSelected());
                thread = new Thread(runnableScanner);
                thread.start();
                //tableView.setItems(runnableScanner.getList());


            } catch (IOException e) {
                System.out.println("Нет такой папки или файла");
            }

        }); //выбор папки

        btnAddKarantin.setOnAction(actionEvent -> {
//            System.out.println("я работаю!");
            FileData selectedFile = tableView.getSelectionModel().getSelectedItem();
            System.out.println(selectedFile);
            try{
                if(selectedFile.getStatus().equals("         Да")) {
                    System.out.println("В процессе");
                    tableView.getItems().remove(selectedFile);
                    listViewKarantin.getItems().add(selectedFile);
                    System.out.println(selectedFile.getPath());
                    File myFile = new File(selectedFile.getPath());
                    selectedFile.setPathKarantin("Quarantine\\" + myFile.getName());
                    myFile.renameTo(new File("Quarantine\\" + myFile.getName()));
                    myFile.delete();
                }
            }
            catch (NullPointerException e){
                System.out.println("btnAddKarantin - oshibka");
            }
        }); //в карантин

        btnReturnFromKarantin.setOnAction(actionEvent -> {
            FileData selectedFile = listViewKarantin.getSelectionModel().getSelectedItem();
            System.out.println(selectedFile);
            if (selectedFile != null) {
                try {
                    System.out.println("В процессе");
                    listViewKarantin.getItems().remove(selectedFile);
                    tableView.getItems().add(selectedFile);
                    System.out.println(selectedFile.getPathKarantin());
                    File myFile = new File(selectedFile.getPathKarantin());
                    System.out.println(myFile.renameTo(new File(selectedFile.getPath())));
                    myFile.delete();
                } catch (NullPointerException e) {
                    System.out.println("btnReturn - oshibka");
                }
            }

        }); //из карантина
    }

}
