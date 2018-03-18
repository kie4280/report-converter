import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class FileWindow extends JDialog {
    private JPanel contentPane;
    private JTable table1;
    private JPanel panel1;

    private JButton deleteButton;
    private JButton addButton;
    private JButton changeButton;
    private JButton nextButton;
    DefaultTableModel tableModel = (DefaultTableModel) table1.getModel();
    private LinkedHashMap<String, ArrayList<String>> out = new LinkedHashMap<>();
    private ArrayList<String> reference = new ArrayList<>(10);


    public FileWindow() {

        setContentPane(contentPane);
        setModal(true);
        table1.setRowHeight(30);
        tableModel.addColumn("Name");
        tableModel.addColumn("Path");
        table1.getColumnModel().getColumn(0).setPreferredWidth(200);
        table1.getColumnModel().getColumn(1).setPreferredWidth(600);

        setSize(800, 500);
        int height = Toolkit.getDefaultToolkit().getScreenSize().height;
        int width = Toolkit.getDefaultToolkit().getScreenSize().width;
        setLocation(width / 2 - getWidth() / 2, height / 2 - getHeight() / 2);

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        changeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = table1.getSelectedRow();
                int column = table1.getSelectedColumn();
                if (row != -1 && column != -1) {
                    JFileChooser jFileChooser = new JFileChooser((String) table1.getValueAt(row, column));
                    int option = jFileChooser.showOpenDialog(null);
                    if (option == JFileChooser.APPROVE_OPTION) {
                        String filedes = jFileChooser.getSelectedFile().toString();
                        table1.setValueAt(filedes, row, 1);
                        reference.set(row * 2 + 1,filedes);
                    }
                }

            }
        });

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = table1.getSelectedRow();

                if (row != -1) {
                    String type = (String) table1.getValueAt(row, 0);
                    String dest = (String) table1.getValueAt(row, 1);
                    JFileChooser jFileChooser = new JFileChooser(dest);
                    int option = jFileChooser.showOpenDialog(null);
                    if (option == JFileChooser.APPROVE_OPTION) {
                        String filename = jFileChooser.getSelectedFile().toString();
                        tableModel.insertRow(row + 1, new Object[]{type, filename});
                        reference.add((row + 1) * 2, type);
                        reference.add((row + 1) * 2 + 1, filename);
                        //table1.setValueAt(filename, row, 1);
                    }
                }

            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = table1.getSelectedRow();
                int first = reference.indexOf(table1.getValueAt(row, 0));
                int last = reference.lastIndexOf(table1.getValueAt(row, 0));
                if (first == last) {
                    table1.setValueAt("?", row, 1);
                    reference.set(first + 1, "?");
                } else {
                    int selectindex = table1.getSelectedRow();
                    tableModel.removeRow(selectindex);
                    reference.remove(row * 2);
                    reference.remove(row * 2);
                }

            }
        });
        tableModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                switch (e.getType()) {
                    case TableModelEvent.DELETE:
                        break;
                    case TableModelEvent.UPDATE:
                        int row = e.getFirstRow();
                        int column = e.getColumn();
                        TableModel model = (TableModel) e.getSource();
                        String data = (String) model.getValueAt(row, column);
                        if (data.isEmpty()) {
                            Main.indicator.status("Cannot be empty!");
                            table1.setValueAt(reference.get(row + column), row, column);
                        } else {
                            reference.set(row * 2 + column, data);
                        }
                        //System.out.println("update");
                        break;
                    case TableModelEvent.INSERT:
                        //System.out.println("insert");
                        break;
                }
            }
        });
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                save();
                dispose();

            }
        });
    }

    public void load(LinkedHashMap<String, ArrayList<String>> files) {

        Iterator<Map.Entry<String, ArrayList<String>>> iterator = files.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ArrayList<String>> entry = iterator.next();
            Iterator<String> array = entry.getValue().iterator();
            String name = entry.getKey();

            while (array.hasNext()) {
                String[] arr = new String[2];
                arr[0] = name;
                String path = array.next();
                arr[1] = path;

                reference.add(name);
                reference.add(path);
                tableModel.addRow(arr);
            }
        }
    }

    private void save() {

        ListIterator<String> listIterator = reference.listIterator();
        while (listIterator.hasNext()) {
            String key = listIterator.next();
            String value = listIterator.next();
            if (out.containsKey(key)) {
                out.get(key).add(value);
            } else {
                ArrayList<String> arrayList = new ArrayList<>();
                arrayList.add(value);
                out.put(key, arrayList);
            }
        }
    }

    private void onCancel() {
        // add your code here if necessary
        Main.error = true;
        Main.gui.setEnterButtonEnable(true);
        dispose();
    }

    public LinkedHashMap<String, ArrayList<String>> getfile() {
        return out;

    }

    public static void main(String[] args) {
        FileWindow fileWindow = new FileWindow();
        Iterator<Map.Entry<String, ArrayList<String>>> iterator = fileWindow.getfile().entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ArrayList<String>> entry = iterator.next();
            Iterator<String> iterator1 = entry.getValue().iterator();
            while (iterator1.hasNext()) {
                String temp = iterator1.next();
                System.out.println(entry.getKey() + "  " + temp);

            }

        }

    }
}
