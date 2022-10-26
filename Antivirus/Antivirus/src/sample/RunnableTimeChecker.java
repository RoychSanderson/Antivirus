package sample;

import javafx.application.Platform;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.Date;

public class RunnableTimeChecker implements Runnable {
    private boolean kill = true;
    private String currentDate;
    ListView<String> listDate;
    private RunnableScanner runnableScanner = null;
    private Thread thread;
    private String dbPath;
    private String path;
    private TextField textField;
    private TableView<FileData> tableView;
    private ListView<FileData> listViewKarantin;

    @Override
    public void run(){
        while (kill){
            Date dateCur = new Date();
            dateCur.setSeconds(0);
            //System.out.println(dateCur);
            currentDate = dateCur.toString();

            if(listDate.getItems().contains(currentDate)){
                try {
                    tableView.getItems().clear();
                    path = textField.getText();
                    runnableScanner = new RunnableScanner(dbPath, path, tableView,listViewKarantin);
                    thread = new Thread(runnableScanner);
                    thread.start();
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            listDate.getItems().remove(currentDate);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    RunnableTimeChecker(ListView<String> listDate, RunnableScanner runnableScanner, Thread thread, TableView tableView, String dbPath, TextField textField, ListView<FileData> listViewKarantin ){
        this.listDate = listDate;
        this.runnableScanner = runnableScanner;
        this.thread = thread;
        this.dbPath = dbPath;
        this.textField = textField;
        this.tableView = tableView;
    }

    public void stopScan(){
        if(runnableScanner != null){
            runnableScanner.stop();
        }
    }

}
