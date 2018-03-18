import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

public class Gui extends JFrame {

    private JButton enterButton;
    private JPanel panel1;
    private JTextField textField1;
    private JButton browseButton1;
    private JTextField textField2;
    private JButton browseButton2;
    private JButton settingsButton;

    FileDialog fileDialog = new FileDialog(this);
    JFileChooser folderchooser = new JFileChooser();

    Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
    public int sheight = dimension.height;
    public int swidth = dimension.width;
    public static String outputfolder = "output";

    public Gui() {
        super("Excel to excel converter");
        Container c = getContentPane();
        c.add(panel1);
        setSize(600, 200);
        c.setVisible(true);
        setResizable(false);
        setLocationRelativeTo(null);
        int height = Toolkit.getDefaultToolkit().getScreenSize().height;
        int width = Toolkit.getDefaultToolkit().getScreenSize().width;
        setLocation(width / 2 - getWidth() / 2, height / 2 - getHeight() / 2);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        Main.gui = this;
        Main.indicator = new Indicator();
        Settings settings = new Settings();
        fileDialog.setMode(FileDialog.LOAD);
        fileDialog.setDirectory(System.getProperty("user.dir"));

//        textField1.setText("C:\\Users\\kieChang\\Desktop\\健檢數據.xlsx");
//        textField2.setText("C:\\Users\\kieChang\\Desktop");

        browseButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileDialog.setVisible(true);
                String file = fileDialog.getDirectory() + fileDialog.getFile();
                textField1.setText(file);

            }
        });
        browseButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                folderchooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                if (folderchooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    String folder = folderchooser.getSelectedFile().toString();
                    textField2.setText(folder);
                }

            }
        });


        enterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text1 = textField1.getText();
                String text2 = textField2.getText();


                if ((text1.endsWith(".xls") || text1.endsWith(".xlsx")) && !text2.isEmpty()) {

                    Main.error = false;
                    setEnterButtonEnable(false);
                    boolean read = settings.read();
                    if (!read) {
                        Main.error = true;
                        setEnterButtonEnable(true);
                        Main.indicator.status("Setting file not found!");
                    }


                    Thread t1 = new Thread(new Runnable() {
                        @Override
                        public void run() {

                            ExcelEditor excelEditor = new ExcelEditor();
                            WordEditor wordEditor = new WordEditor();
                            LinkedHashMap<String, HashMap> foldernames = excelEditor.sort(text1);
                            Iterator<String> folderiterator = foldernames.keySet().iterator();
                            LinkedHashMap<String, ArrayList<String>> files = new LinkedHashMap<>();
                            String[] containfile = new File(text2).list();
                            while (folderiterator.hasNext()) {
                                String search = folderiterator.next();
                                boolean found = false;
                                for (String temp : containfile) {
                                    if (temp.contains(search)) {

                                        found = true;
                                        if (files.containsKey(search)) {
                                            files.get(search).add(text2 + "\\" + temp);
                                        } else {
                                            ArrayList<String> insert = new ArrayList<>();
                                            insert.add(text2 + "\\" + temp);
                                            files.put(search, insert);
                                        }
                                    }
                                }
                                if (!found) {
                                    ArrayList<String> insert = new ArrayList<>();
                                    insert.add("?");
                                    files.put(search, insert);
                                }
                            }

                            FileWindow fileWindow = new FileWindow();
                            fileWindow.load(files);
                            fileWindow.setVisible(true);
                            LinkedHashMap<String, ArrayList<String>> filein = fileWindow.getfile();

                            File dir = new File(System.getProperty("user.dir") + "\\" + outputfolder);

                            if (dir.exists()) {
                                deleteDir(dir);

                            }
                            try {
                                Files.createDirectory(dir.toPath());
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }

                            int a = 0;
                            Iterator<String> folderiterator2 = foldernames.keySet().iterator();
                            while (folderiterator2.hasNext() && !Main.error) {
                                boolean pass = false;
                                String folder = folderiterator2.next();
                                ArrayList<String> filename = filein.get(folder);
                                if (filename.contains("?")) {     //speed up process
                                    pass = true;

                                } else {
                                    File outdir = new File(System.getProperty("user.dir") + "\\" + outputfolder + "\\" + folder);
                                    if (!outdir.exists()) {
                                        outdir.mkdir();
                                    }
                                }
                                HashMap<String, People> peoplelist = foldernames.get(folder);
                                Iterator<String> peopleiterator = peoplelist.keySet().iterator();

                                int num1 = foldernames.size();
                                int num2 = peoplelist.size();
                                int b = 0;
                                while (peopleiterator.hasNext() && !pass) {
                                    String key = peopleiterator.next();
                                    People people = peoplelist.get(key);
                                    people.name = people.name.replaceAll("[-+.^:?/,]", "X");

                                    String write = System.getProperty("user.dir") + "\\" + outputfolder + "\\" + folder + "\\" + people.number + people.name;
                                    if (filename != null) {
                                        Iterator<String> fileiterator = filename.iterator();
                                        while (fileiterator.hasNext()) {
                                            String read = fileiterator.next();
                                            if (read.endsWith(".xlsx") || read.endsWith(".xls")) {
                                                excelEditor.replace(read, write + ".xlsx", people);

                                            } else if (read.endsWith(".xls")) {
                                                excelEditor.replace(read, write + ".xls", people);
                                            } else if (read.endsWith(".docx")) {
                                                wordEditor.replace(read, write + ".docx", people);
                                            }
                                        }
                                    }
                                    b++;

                                    Main.indicator.progress(a, b, num1, num2);

                                }
                                a++;
                            }

                            if (!Main.error)
                                Main.indicator.done();//end
                        }
                    }, "WorkThread");
                    if (!Main.error) {
                        t1.start();
                        setEnterButtonEnable(false);
                    } else {
                        setEnterButtonEnable(true);
                    }
                }
            }
        });
        settingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                settings.setVisible(true);
            }
        });

    }

    public void setTextField1(String text) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                String temp = textField1.getText();
                textField1.setText(text);
                delay(4000, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        textField1.setText(temp);
                    }
                });
            }
        });

    }

    public void setTextField2(String text) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                String temp = textField2.getText();
                textField2.setText(text);
                delay(4000, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        textField2.setText(temp);
                    }
                });
            }
        });

    }

    public void setEnterButtonEnable(boolean enable) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (enable) {
                    enterButton.setText("Enter");
                    enterButton.setEnabled(true);
//                    pack();
//                    setLocation(swidth / 2 - getWidth() / 2, sheight / 2 - getHeight() / 2);
                } else {
                    enterButton.setText("Processing.....");
                    enterButton.setEnabled(false);
//                    pack();
//                    setLocation(swidth / 2 - getWidth() / 2, sheight / 2 - getHeight() / 2);
                }
            }
        });

    }

    public Timer delay(int time, ActionListener actionListener) {
        Timer temptimer = new Timer(time, actionListener);
        temptimer.setRepeats(false);
        temptimer.start();
        return temptimer;
    }

    public static void deleteDir(File element) {
        if (element.isDirectory()) {
            for (File sub : element.listFiles()) {
                deleteDir(sub);
            }
        }
        element.delete();
    }

    public static void main(String[] args) {
        Gui gui = new Gui();
    }
}
