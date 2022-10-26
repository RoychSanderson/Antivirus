package sample;

import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RunnableScanner implements Runnable{
    private MyFileVisitor myFileVisitor;
    private String path;
    private boolean kill = true;
    private boolean monitor = false;

    private ObservableList<FileData> list;

    @Override
    public void run() {
        while(kill) {
            if (!monitor) {
                try {
                    Files.walkFileTree(Paths.get(path), myFileVisitor);
                    kill = false;
                //    System.out.println(myFileVisitor.getList());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                try {
                    Files.walkFileTree(Paths.get(path), myFileVisitor);
                    System.out.println(myFileVisitor.getList());
                    Thread.sleep(400);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("Просканировал");
    }

    public void turnOnMonitor(boolean status){ this.monitor = status; }
    public void turnOnDelete(boolean status){ myFileVisitor.turnOnDelete(status); }
    public void turnOnKarantin(boolean status){ myFileVisitor.turnOnKarantion(status); }

    public ObservableList<FileData> getList(){
        return myFileVisitor.getList();
    }

    public void stop()
    {
        this.kill = false;
    }

    public RunnableScanner(String pathDb, String path, TableView<FileData> table,  ListView<FileData> listViewKarantin) throws IOException {
        myFileVisitor = new MyFileVisitor(Path.of(pathDb), table, listViewKarantin);
        this.path = path;

    }

}
