import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.ArrayList;

public class Settings extends JDialog {
    private JPanel contentPane;

    private JTextField textField1;
    private JTextField textField2;
    private JButton addButton;
    private JTable table1;
    private JButton deleteButton;
    ObjectOutputStream fileWriter = null;
    ObjectInputStream fileReader = null;
    boolean settingexist = false;
    ArrayList<ArrayList<String>> entry = new ArrayList<>(100);
    DefaultTableModel tableModel = (DefaultTableModel) table1.getModel();

    public Settings() {

        setTitle("Settings");
        tableModel.addColumn("Excel");
        tableModel.addColumn("Example");


        try {
            fileReader = new ObjectInputStream(new BufferedInputStream(new FileInputStream(System.getProperty("user.dir") + "\\settings.ser")));
            settingexist = true;

        } catch (FileNotFoundException e) {
            settingexist = false;
            close();
            e.printStackTrace();
        } catch (IOException e) {
            settingexist = false;
            Main.indicator.status("Error opening file!");
            close();
            e.printStackTrace();
        }

        if (settingexist) {

            try {
                entry = (ArrayList<ArrayList<String>>) fileReader.readObject();
                for (ArrayList<String> iterator : entry) {
                    tableModel.addRow(iterator.toArray());
                }
            } catch (IOException e) {
                Main.indicator.status("Read error!");

                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }

        setContentPane(contentPane);
        setModal(true);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        getRootPane().setDefaultButton(addButton);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                save();
                close();
                dispose();
            }
        });
        setSize(600, 500);
        int height = Toolkit.getDefaultToolkit().getScreenSize().height;
        int width = Toolkit.getDefaultToolkit().getScreenSize().width;
        setLocation(width / 2 - getWidth() / 2, height / 2 - getHeight() / 2);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addButton.setEnabled(false);
                String excel = textField1.getText();
                String word = textField2.getText();
                ArrayList<String> temp = new ArrayList<>(2);
                if (!excel.isEmpty() && !word.isEmpty()) {
                    temp.add(excel);
                    temp.add(word);
                    tableModel.addRow(temp.toArray());
                    textField1.setText("");
                    textField2.setText("");
                    entry.add(temp);
                }
                addButton.setEnabled(true);
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int count = table1.getSelectedRowCount();
                for (int i = 0; i < count; i++) {
                    int getselect = table1.getSelectedRow();
                    tableModel.removeRow(getselect);
                }
            }
        });

        tableModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                switch (e.getType()) {
                    case TableModelEvent.UPDATE:
                        int row = e.getFirstRow();
                        int column = e.getColumn();
                        TableModel model = (TableModel) e.getSource();
                        String data = (String) model.getValueAt(row, column);
                        if (data.isEmpty()) {
                            Main.indicator.status("Cannot be empty!");
                            table1.setValueAt(entry.get(row).get(column), row, column);
                        } else {
                            entry.get(row).set(column, data);
                        }
                        //System.out.println("update");
                        break;
                    case TableModelEvent.INSERT:
                        //System.out.println("insert");
                        break;

                }


            }
        });
    }

    public void save() {

        /*entry = new ArrayList<>(100);
        for (int a = 0; a < table1.getRowCount(); a++) {
            ArrayList<String> temp = new ArrayList<>(2);
            for (int b = 0; b < table1.getColumnCount(); b++) {
                temp.add((String) table1.getValueAt(a, b));
            }
            entry.add(temp);
        }*/

        try {
            fileWriter = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(System.getProperty("user.dir") + "\\settings.ser")));
            fileWriter.writeObject(entry);
        } catch (IOException e) {
            Main.indicator.status("Error writing to file!");
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (fileReader != null)
                fileReader.close();
            if (fileWriter != null)
                fileWriter.close();
        } catch (IOException e) {
            Main.indicator.status("Error closing file!");
            e.printStackTrace();
        }
    }

    public boolean read() {
        try{
            FileReader fileReader= new FileReader(System.getProperty("user.dir") + "\\settings.ser");
        } catch (FileNotFoundException e) {
            settingexist = false;
            e.printStackTrace();
        }
        Main.entry = entry;
        return settingexist;
    }

}