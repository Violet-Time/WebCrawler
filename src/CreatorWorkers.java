
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Создатель рабочих
 */

public class CreatorWorkers implements Runnable{

    public static final String REQUEST_LINKS = "href=[\"\'][A-Za-z%\\d:\\/.]+";
    public static final String REQUEST_TITLE = "<title>.+</title>";

    //Максимальная глубина
    private int maxDepth;
    //Максимальное время в секундах
    private int maxTime;
    //Количество рабочих
    private int workersCount;
    //Статус работы
    private int status;

    private int beTime = 0;

    //Сохранение ссылки и заголовка страницы
    public ConcurrentMap<String, String> saveUrlAndTitle = new ConcurrentHashMap<>();
    //Ссылки на страниы для парсинга
    public Queue<MyData> dataForCrawler;

    public Queue<Worker> looseWorkers = new ConcurrentLinkedQueue<>();

    //AtomicInteger workersRunCount = new AtomicInteger(0);

    Thread[] threadWorkers;
    Worker[] workers;

    Timer timer;

    JLabel timerTextLabel;
    JLabel parsedCPagesTextLabel;

    CreatorWorkers(String startLink) throws InterruptedException {
        this(startLink, 1);
    }

    CreatorWorkers(String startLink, int workersCount) throws InterruptedException {
        this.dataForCrawler = new ConcurrentLinkedQueue<>();
        this.dataForCrawler.add(new MyData(startLink, 0));
        this.workersCount = workersCount;
        this.maxDepth = 0;
        this.maxTime = 0;
        this.status = 0;
    }


    public int getMaxDepth() {
        return maxDepth;
    }

    public int getMaxTime() {
        return maxTime;
    }

    public int getWorkersCount() {
        return workersCount;
    }

    public int getStatus() {
        return status;
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public void setMaxTime(int maxTime) {
        this.maxTime = maxTime;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setTimerTextLabel(JLabel timerTextLabel) {
        this.timerTextLabel = timerTextLabel;
        timer();
    }

    public void setParsedCPagesTextLabel(JLabel parsedCPagesTextLabel) {
        this.parsedCPagesTextLabel = parsedCPagesTextLabel;
    }

    public void timer(){

        this.timer = new Timer(1000, e -> {
            if(maxTime > 0 && maxTime < beTime){
                status = 0;
            }
            timerTextLabel.setText(String.valueOf(beTime));
            beTime++;

        });
    }

    private void createWorkers() throws InterruptedException {
        //System.out.println(urls.peek()[1]);
        System.out.println(1);
        threadWorkers = new Thread[workersCount];
        workers = new Worker[workersCount];
        for (int i = 0; i < threadWorkers.length; i++){
            workers[i] = new Worker(this);
            threadWorkers[i] = new Thread(workers[i]);
        }

    }

    void exportFile(String name){
        System.out.println(name);
        File file = new File(name);
        try(PrintWriter writer = new PrintWriter(file)){

            saveUrlAndTitle.forEach((k,v) -> writer.println(k + "\n" + v));

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    //распределение работы
    private void distribution() throws InterruptedException {
        System.out.println(3);
        while(true){
            if(status == 0){
                //parsedPages++;
                parsedCPagesTextLabel.setText(String.valueOf(saveUrlAndTitle.size()));
                for (Thread worker : threadWorkers){
                    worker.join();
                }
                break;
            }
            if(!dataForCrawler.isEmpty() || looseWorkers.size() < workersCount) {
                if (!dataForCrawler.isEmpty() && !looseWorkers.isEmpty()) {
                    parsedCPagesTextLabel.setText(String.valueOf(saveUrlAndTitle.size()));
                    looseWorkers.poll().setData(dataForCrawler.poll());
                }
            }else {
                status = 0;
            }
        }
    }

    public void stopWork(){
        status = 0;
    }
    public void startWork(){
        System.out.println(workersCount);
        status = 1;
        for (int i = 0; i < workersCount; i++) {
            if (!threadWorkers[i].isAlive()) {
                threadWorkers[i].start();
                looseWorkers.add(workers[i]);
            }
        }

    }

    @Override
    public void run() {
        try {
            createWorkers();
            startWork();
            timer.start();
            distribution();
            timer.stop();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
