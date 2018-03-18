import org.apache.poi.hssf.OldExcelFormatException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ExcelEditor {

    private ArrayList<ArrayList<String>> inData = null;
    private HashMap<String, Integer> title = new HashMap<>();
    private ArrayList<String> excel = new ArrayList<>(100);
    private ArrayList<String> word = new ArrayList<>(100);

    public ExcelEditor() {
        ArrayList<ArrayList<String>> entry = Main.entry;
        for (ArrayList<String> temp : entry) {
            excel.add(temp.get(0));
            word.add(temp.get(1));
        }
    }

    public void replace(String inaddress, String outaddress, People people) {

        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(inaddress));
            Workbook wb = WorkbookFactory.create(inputStream);
            Sheet st = wb.getSheetAt(0);
            for (int a = 0; a <= st.getLastRowNum(); a++) {
                Row row = st.getRow(a);
                if (row != null) {
                    for (int b = 0; b < row.getLastCellNum(); b++) {
                        Cell cell = row.getCell(b);
                        if (cell != null) {
                            switch (cell.getCellType()) {
                                case Cell.CELL_TYPE_STRING:
                                    String cellvalue = cell.getStringCellValue();
                                    if (cellvalue.contains("#")) {
                                        int index = cellvalue.indexOf("#");
                                        cellvalue = cellvalue.substring(index + 1, cellvalue.indexOf("#", index + 1));
                                        if (people.data.containsKey(cellvalue)) {
                                            cell.setCellValue(people.data.get(cellvalue));
                                        } else {
                                            cell.setCellValue("");
                                        }
                                    }
                                    break;
                                case Cell.CELL_TYPE_NUMERIC:
                                    break;
                            }
                        }
                    }
                }
            }

            outputStream = new BufferedOutputStream(new FileOutputStream(outaddress));
            wb.write(outputStream);

        } catch (OldExcelFormatException e) {
            e.printStackTrace();
            Main.error = true;
            Main.gui.setTextField2("Cannot read old format! Please try excel 2003 or later");
            Main.gui.setEnterButtonEnable(true);

        } catch (InvalidFormatException e) {
            Main.error = true;
            Main.gui.setTextField2("Invalid file format!");
            Main.gui.setEnterButtonEnable(true);

            e.printStackTrace();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Main.error = true;
            Main.gui.setTextField2("Excel file opened by other application");
            Main.gui.setEnterButtonEnable(true);


        } catch (IOException e) {
            Main.error = true;
            Main.indicator.status("Unknown error!");
            Main.gui.setEnterButtonEnable(true);
            e.printStackTrace();
        } finally {
            if (outputStream != null)
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            if (inputStream != null)
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    public ArrayList<ArrayList<String>> read(String address) {
        int lastrow;
        Row row;
        Cell cell;
        ArrayList<ArrayList<String>> array = null;
        try {
            Workbook wb = WorkbookFactory.create(new File(address));
            Sheet st = wb.getSheetAt(0);
            lastrow = st.getLastRowNum();
            array = new ArrayList<>(lastrow);
            for (int a = 0; a <= lastrow; a++) {
                row = st.getRow(a);
                if (row != null) {
                    ArrayList<String> cellarray = new ArrayList<>(row.getLastCellNum());
                    for (int b = 0; b < row.getLastCellNum(); b++) {
                        cell = row.getCell(b);
                        if (cell != null) {
                            switch (cell.getCellType()) {
                                case Cell.CELL_TYPE_STRING:
                                    String instring = cell.getStringCellValue();
                                    if (excel.contains(instring)) {
                                        title.put(word.get(excel.indexOf(instring)), b);
                                    } else {
                                        cellarray.add(instring);
                                    }
                                    break;

                                case Cell.CELL_TYPE_NUMERIC:
                                    String temp = Double.toString(cell.getNumericCellValue());
                                    if (temp.endsWith(".0")) {
                                        temp = temp.substring(0, temp.indexOf(".0"));
                                    }
                                    cellarray.add(temp);
                                    break;
                            }
                        } else {
                            cellarray.add(null);
                        }
                    }
                    cellarray.trimToSize();
                    if (cellarray.size() != 0)
                        array.add(cellarray);
                }
            }
            array.trimToSize();
        } catch (OldExcelFormatException e) {
            e.printStackTrace();
            Main.error = true;
            Main.gui.setTextField1("Cannot read old format! Please try excel 2003 or later");
            Main.gui.setEnterButtonEnable(true);

        } catch (InvalidFormatException e) {
            Main.error = true;
            Main.gui.setTextField1("Invalid file format!");
            Main.gui.setEnterButtonEnable(true);

            e.printStackTrace();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Main.error = true;
            Main.gui.setTextField1("Excel file opened by other application");
            Main.gui.setEnterButtonEnable(true);


        } catch (IOException e) {
            Main.error = true;
            Main.indicator.status("Unknown error!");
            Main.gui.setEnterButtonEnable(true);

            e.printStackTrace();
        }

        return array;
    }

    public ArrayList<ArrayList<String>> checkread(String address) {
        if (inData == null)
            inData = read(address);
        return inData;
    }

    public LinkedHashMap<String, HashMap> sort(String address) {

        LinkedHashMap<String, HashMap> foldername = new LinkedHashMap<>();
        ArrayList<ArrayList<String>> rows = checkread(address);
        for (int a = 0; a < rows.size(); a++) {
            ArrayList<String> row = rows.get(a);
            HashMap<String, People> peoplelist;
            try {
                String folder = row.get(title.get("type"));
                if (foldername.containsKey(folder)) {
                    peoplelist = foldername.get(folder);

                } else {
                    foldername.put(folder, new HashMap<>());
                    peoplelist = foldername.get(folder);
                }
                String cell = row.get(title.get("number"));
                if (peoplelist.containsKey(cell)) {

                    peoplelist.get(cell).name = row.get(title.get("name"));
                    peoplelist.get(cell).date = row.get(title.get("date"));
                    String index = row.get(title.get("index0"));
                    for (int b = 1; title.containsKey("index" + b); b++) {
                        index += "-" + row.get(title.get("index" + b));
                    }
                    peoplelist.get(cell).data.put(index, row.get(title.get("data")));
                    for (Map.Entry<String, Integer> entries : title.entrySet()) {
                        peoplelist.get(cell).data.put(entries.getKey(), row.get(entries.getValue()));
                    }

                } else {
                    People people = new People(cell);
                    people.name = row.get(title.get("name"));
                    people.date = row.get(title.get("date"));
                    String index = row.get(title.get("index0"));
                    for (int b = 1; title.containsKey("index" + b); b++) {
                        index += "-" + row.get(title.get("index" + b));
                    }
                    people.data.put(index, row.get(title.get("data")));
                    for (Map.Entry<String, Integer> entries : title.entrySet()) {
                        people.data.put(entries.getKey(), row.get(entries.getValue()));
                    }
                    peoplelist.put(cell, people);
                }
            } catch (NullPointerException e) {
                Main.error = true;
                Main.indicator.status("Setting error! Missing some entry");
                Main.gui.setEnterButtonEnable(true);
                e.printStackTrace();
            } catch (IndexOutOfBoundsException e) {

                e.printStackTrace();
            }

        }
        return foldername;
    }

    public static void main(String[] args) {

    }

}