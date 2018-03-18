import org.apache.poi.hssf.OldExcelFormatException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class ExcelEditorold {

    private ArrayList<ArrayList<String>> inData = null;
    private HashMap<String, Integer> title = new HashMap<>();

    public void write(ArrayList<ArrayList> array, String address, boolean newFormat) {
        Workbook wb;
        if (newFormat) {
            wb = new XSSFWorkbook();
            address += ".xlsx";
        } else {
            wb = new HSSFWorkbook();
            address += ".xls";
        }
        Sheet st = wb.createSheet();
        Row row;
        Cell cell;

        for (int a = 0; a < array.size(); a++) {

            ArrayList cellarray = array.get(a);
            if (cellarray != null) {
                row = st.createRow(a);
                for (int b = 0; b < cellarray.size(); b++) {
                    if (cellarray.get(b) != null) {
                        cell = row.createCell(b);
                        if (cellarray.get(b).getClass().equals(String.class)) {
                            cell.setCellType(Cell.CELL_TYPE_STRING);
                            cell.setCellValue((String) cellarray.get(b));
                        } else {
                            cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                            cell.setCellValue((int) cellarray.get(b));
                        }
                    }
                }
            }
        }
        try {
            OutputStream out = new BufferedOutputStream(new FileOutputStream(address));
            wb.write(out);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
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
            for (int a = 0; a < lastrow; a++) {
                row = st.getRow(a);
                if (row != null) {
                    ArrayList<String> cellarray = new ArrayList<>(row.getLastCellNum());
                    for (int b = 0; b < row.getLastCellNum(); b++) {
                        cell = row.getCell(b);
                        if (cell != null) {
                            switch (cell.getCellType()) {
                                case Cell.CELL_TYPE_STRING:
                                    String instring = cell.getStringCellValue();
                                    switch (instring) {

                                        case "#病歷號":
                                            title.put("number", b);
                                            break;
                                        case "#姓名":
                                            title.put("name", b);
                                            break;
                                        case "#就診日":
                                            title.put("date", b);
                                            break;
                                        case "#檢驗代碼":
                                            title.put("index0", b);
                                            break;
                                        case "#檢驗檢查序號":
                                            title.put("index1", b);
                                            break;
                                        case "#身份證號":
                                            title.put("id", b);
                                            break;
                                        case "#出生日期":
                                            title.put("birth", b);
                                            break;
                                        case "#檢驗檢查報告":
                                            title.put("data0", b);
                                            break;
                                        case "#性別":
                                            title.put("gender", b);
                                            break;
                                        case "#體檢類別":
                                            title.put("category", b);
                                            break;
                                        default:
                                            if(!instring.contains("#")) {
                                                cellarray.add(instring);
                                            }
                                            break;
                                    }
                                    break;
                                case Cell.CELL_TYPE_NUMERIC:
                                    cellarray.add(Double.toString(cell.getNumericCellValue()));
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
        }catch (OldExcelFormatException e) {
            e.printStackTrace();
            Main.error = true;
            Main.gui.setTextField1("Cannot read old format! Please try excel 2003 or later");
            Main.gui.setEnterButtonEnable(true);

        }
        catch (InvalidFormatException e) {
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

    public HashMap<String, People> sort(String address) {
        HashMap<String, People> peoplelist = new HashMap<>();
        ArrayList<ArrayList<String>> rows = checkread(address);
        for (int a = 0; a < rows.size(); a++) {
            ArrayList<String> row = rows.get(a);
            String cell = "" + (long) Double.parseDouble(row.get(title.get("number")));
            if (peoplelist.containsKey(cell)) {
                peoplelist.get(cell).name = row.get(title.get("name"));
                peoplelist.get(cell).date = "" + (long) Double.parseDouble(row.get(title.get("date")));
                String index = (long) Double.parseDouble(row.get(title.get("index0"))) + "-" + (long) Double.parseDouble(row.get(title.get("index1")));
                peoplelist.get(cell).data.put(index, row.get(title.get("data0")));
                peoplelist.get(cell).data.put("name", peoplelist.get(cell).name);
                peoplelist.get(cell).data.put("date", peoplelist.get(cell).date);
                peoplelist.get(cell).data.put("gender", row.get(title.get("gender")));
                peoplelist.get(cell).data.put("id", row.get(title.get("id")));
                peoplelist.get(cell).data.put("birth", "" + (long) Double.parseDouble(row.get(title.get("birth"))));
            } else {
                People people = new People(cell);
                people.name = row.get(title.get("name"));
                people.date = "" + (long) Double.parseDouble(row.get(title.get("date")));
                String index = (long) Double.parseDouble(row.get(title.get("index0"))) + "-" + (long) Double.parseDouble(row.get(title.get("index1")));
                people.data.put(index, row.get(title.get("data0")));
                people.data.put("name", people.name);
                people.data.put("date", people.date);
                people.data.put("gender", row.get(title.get("gender")));
                people.data.put("id", row.get(title.get("id")));
                people.data.put("birth", "" + (long) Double.parseDouble(row.get(title.get("birth"))));
                peoplelist.put(cell, people);
            }
        }
        return peoplelist;
    }

    public static void main(String[] args) {

    }

}