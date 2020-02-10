
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebCrawler extends JFrame {

    //TableUrlWebPage tableUrlWebPage;
    //JTable urlWebPage;

    //int depth = 0;
    CreatorWorkers workers;
    Thread threadCreatorWorkers;

    public WebCrawler() throws IOException {

        super("Web Crawler");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        JPanel body = body();

        add(body, new GridBagConstraints(0, 0, 1, 1, 1 , 1, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(1,1,1,1),0,0));

    }

    JPanel body(){

        JPanel top = new JPanel();
        top.setLayout(new GridBagLayout());

        JLabel urlTextLabel = new JLabel("Start URL: ");

        JTextField UrlTextField = new JTextField();
        UrlTextField.setName("UrlTextField");

        JToggleButton runButton = new JToggleButton("Run");
        runButton.setName("RunButton");

        JLabel workersTextLabel = new JLabel("Workers: ");

        JTextField workersTextField = new JTextField();
        workersTextField.setName("WorkersTextField");

        JLabel depthTextLabel = new JLabel("Maximum depth: ");

        JTextField depthTextField = new JTextField();
        depthTextField.setName("DepthTextField");

        JCheckBox depthCheck = new JCheckBox();
        depthCheck.setSelected(true);
        depthCheck.setName("DepthCheckBox");

        JLabel depthEnableTextLabel = new JLabel("Enable");

        JLabel timeLimitTextLabel = new JLabel("Time limit: ");

        JTextField timeLimitTextField = new JTextField();
        timeLimitTextField.setName("WorkersTextField");

        JLabel timeLimitSecondsTextLabel = new JLabel("seconds");

        JCheckBox timeLimitCheck = new JCheckBox();
        timeLimitCheck.setName("TimeLimitCheckBox");

        JLabel timeLimitEnableTextLabel = new JLabel("Enable");

        JLabel elapsedTimeTextLabel = new JLabel("Elapsed time: ");

        JLabel timerTextLabel = new JLabel("0");

        JLabel parsedPagesTextLabel = new JLabel("Parsed pages: ");

        JLabel parsedCPagesTextLabel = new JLabel("0");
        parsedCPagesTextLabel.setName("ParsedLabel");

        JLabel exportTextLabel = new JLabel("Export: ");

        JTextField exportTextField = new JTextField();
        exportTextField.setName("ExportUrlTextField");

        JButton exportButton = new JButton("Save");
        exportButton.setName("ExportButton");

        UrlTextField.setPreferredSize(new Dimension(200,30));

        runButton.setSize(50,30);

        workersTextField.setPreferredSize(new Dimension(200,30));

        depthTextField.setPreferredSize(new Dimension(200,30));

        timeLimitTextField.setPreferredSize(new Dimension(200,30));

        exportTextField.setPreferredSize(new Dimension(200,30));

        exportButton.setSize(50,30);

        GridBagConstraints c = new GridBagConstraints();

        c.insets = new Insets(5,5,5,5);

        c.gridx = 0;
        c.gridy = 0;
        top.add(urlTextLabel, c);
        c.gridx = 1;
        top.add(UrlTextField, c);
        c.gridx = 2;
        top.add(runButton, c);
        c.gridx = 0;
        c.gridy = 1;
        top.add(workersTextLabel, c);
        c.gridx = 1;
        top.add(workersTextField, c);
        c.gridx = 0;
        c.gridy = 2;
        top.add(depthTextLabel, c);
        c.gridx = 1;
        top.add(depthTextField, c);
        c.gridx = 2;
        top.add(depthCheck, c);
        c.gridx = 3;
        top.add(depthEnableTextLabel, c);
        c.gridx = 0;
        c.gridy = 3;
        top.add(timeLimitTextLabel, c);
        c.gridx = 1;
        top.add(timeLimitTextField, c);
        c.gridx = 2;
        top.add(timeLimitSecondsTextLabel, c);
        c.gridx = 3;
        top.add(timeLimitCheck, c);
        c.gridx = 4;
        top.add(timeLimitEnableTextLabel, c);
        c.gridx = 0;
        c.gridy = 4;
        top.add(elapsedTimeTextLabel, c);
        c.gridx = 1;
        top.add(timerTextLabel, c);
        c.gridx = 0;
        c.gridy = 5;
        top.add(parsedPagesTextLabel, c);
        c.gridx = 1;
        top.add(parsedCPagesTextLabel, c);
        c.gridy = 6;
        c.gridx = 0;
        top.add(exportTextLabel, c);
        c.gridx = 1;
        top.add(exportTextField, c);
        c.gridx = 2;
        top.add(exportButton, c);

        runButton.addItemListener( new ItemListener(){

            @Override
            public void itemStateChanged(ItemEvent itemEvent) {

                int depth = 0;
                int timeLimit = 0;

                if(depthCheck.isSelected()){
                    depth = Integer.parseInt(depthTextField.getText());
                }
                if(timeLimitCheck.isSelected()){
                    timeLimit = Integer.parseInt(timeLimitTextField.getText());
                }

                int countWorkers =  Integer.parseInt(workersTextField.getText());

                if(itemEvent.getStateChange() == ItemEvent.SELECTED){
                    if(countWorkers > 1) {
                        try {
                            workers = new CreatorWorkers(UrlTextField.getText(), countWorkers);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }else{
                        try {
                            workers = new CreatorWorkers(UrlTextField.getText());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if(depth > 0 && workers != null){
                        workers.setMaxDepth(depth);
                    }
                    if(timeLimit > 0 && workers != null){
                        workers.setMaxTime(timeLimit);
                    }

                    if(workers != null){
                        workers.setTimerTextLabel(timerTextLabel);
                        workers.setParsedCPagesTextLabel(parsedCPagesTextLabel);
                        threadCreatorWorkers = new Thread(workers);
                        threadCreatorWorkers.start();
                    }

                    runButton.setText("Stop");
                }else {
                    if(workers != null){
                        workers.stopWork();
                        try {
                            threadCreatorWorkers.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    runButton.setText("Run");
                }
            }
        });

        exportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                System.out.println(exportTextField.getText());
                if(workers != null){
                  workers.exportFile(exportTextField.getText());
                }

            }
        });


        return top;
    }








}


