import org.apache.poi.openxml4j.exceptions.OLE2NotOfficeXmlFileException;
import org.apache.poi.xwpf.usermodel.*;

import java.io.*;
import java.util.Arrays;

public class WordEditor {

    public void replace(String inaddress, String outaddress, People people) {

        try {
            InputStream inputStream = new BufferedInputStream(new FileInputStream(inaddress));
            XWPFDocument docx = new XWPFDocument(inputStream);
            inputStream.close();

            for (XWPFParagraph paragraph : docx.getParagraphs()) {
                if (paragraph.getText().contains("#")) {
                    String searchText = "";
                    boolean end = true;

                    for (XWPFRun searchrun : paragraph.getRuns()) {
                        String runText = searchrun.text();
                        int index = runText.indexOf("#");
                        String tempText = "";

                        while (index != -1) {

                            if (end) {
                                tempText += runText.substring(0, index);
                                runText = runText.substring(index + 1);
                                index = runText.indexOf("#");
                                if (index != -1) {
                                    searchText += runText.substring(0, index);
                                    if (people.data.containsKey(searchText)) {
                                        tempText += people.data.get(searchText);

                                    } else {
                                        int length = searchText.length() + 2;
                                        char[] spacechar = new char[length];
                                        Arrays.fill(spacechar, ' ');
                                        String space = String.copyValueOf(spacechar);
                                        tempText += space;
                                    }
                                    runText = runText.substring(index + 1);
                                    index = runText.indexOf("#", index + 1);
                                    searchText = "";

                                } else {
                                    searchText += runText;
                                    runText = "";
                                    end = false;
                                }

                            } else {
                                searchText += runText.substring(0, index);
                                runText = runText.substring(index + 1);
                                index = runText.indexOf("#");

                                if (people.data.containsKey(searchText)) {
                                    tempText += people.data.get(searchText);

                                } else {
                                    int length = searchText.length() + 2;
                                    char[] spacechar = new char[length];
                                    Arrays.fill(spacechar, ' ');
                                    String space = String.copyValueOf(spacechar);
                                    tempText += space;
                                }
                                searchText = "";
                                end = true;
                            }
                        }

                        if (end) {
                            tempText += runText;
                            searchrun.setText(tempText, 0);
                        } else {
                            searchText += runText;
                            searchrun.setText("", 0);
                        }
                    }
                }
            }


            for (XWPFTable tables : docx.getTables()) {
                for (XWPFTableRow xwpfTableRow : tables.getRows()) {
                    for (XWPFTableCell tableCell : xwpfTableRow.getTableCells()) {

                        for (XWPFParagraph paragraph : tableCell.getParagraphs()) {
                            if (paragraph.getText().contains("#")) {
                                String searchText = "";
                                boolean end = true;

                                for (XWPFRun searchrun : paragraph.getRuns()) {
                                    String runText = searchrun.text();
                                    int index = runText.indexOf("#");
                                    String tempText = "";

                                    while (index != -1) {

                                        if (end) {
                                            tempText += runText.substring(0, index);
                                            runText = runText.substring(index + 1);
                                            index = runText.indexOf("#");
                                            if (index != -1) {
                                                searchText += runText.substring(0, index);
                                                if (people.data.containsKey(searchText)) {
                                                    tempText += people.data.get(searchText);

                                                } else {
                                                    int length = searchText.length() + 2;
                                                    char[] spacechar = new char[length];
                                                    Arrays.fill(spacechar, ' ');
                                                    String space = String.copyValueOf(spacechar);
                                                    tempText += space;
                                                }
                                                runText = runText.substring(index + 1);
                                                index = runText.indexOf("#", index + 1);
                                                searchText = "";

                                            } else {
                                                searchText += runText;
                                                runText = "";
                                                end = false;
                                            }

                                        } else {
                                            searchText += runText.substring(0, index);
                                            runText = runText.substring(index + 1);
                                            index = runText.indexOf("#");

                                            if (people.data.containsKey(searchText)) {
                                                tempText += people.data.get(searchText);

                                            } else {
                                                int length = searchText.length() + 2;
                                                char[] spacechar = new char[length];
                                                Arrays.fill(spacechar, ' ');
                                                String space = String.copyValueOf(spacechar);
                                                tempText += space;
                                            }
                                            searchText = "";
                                            end = true;
                                        }
                                    }

                                    if (end) {
                                        tempText += runText;
                                        searchrun.setText(tempText, 0);
                                    } else {
                                        searchText += runText;
                                        searchrun.setText("", 0);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            for (XWPFHeader header : docx.getHeaderList()) {
                for (XWPFParagraph paragraph : header.getParagraphs()) {

                    String searchText = "";
                    boolean end = true;
                    for (XWPFRun searchrun : paragraph.getRuns()) {
                        String runText = searchrun.toString();
                        int index = runText.indexOf("#");
                        String tempText = "";

                        while (index != -1) {

                            if (end) {
                                tempText += runText.substring(0, index);
                                runText = runText.substring(index + 1);
                                index = runText.indexOf("#");
                                if (index != -1) {
                                    searchText += runText.substring(0, index);
                                    if (people.data.containsKey(searchText)) {
                                        tempText += people.data.get(searchText);

                                    } else {
                                        int length = searchText.length() + 2;
                                        char[] spacechar = new char[length];
                                        Arrays.fill(spacechar, ' ');
                                        String space = String.copyValueOf(spacechar);
                                        tempText += space;
                                    }
                                    runText = runText.substring(index + 1);
                                    index = runText.indexOf("#", index + 1);
                                    searchText = "";

                                } else {
                                    searchText += runText;
                                    runText = "";
                                    end = false;
                                }

                            } else {
                                searchText += runText.substring(0, index);
                                runText = runText.substring(index + 1);
                                index = runText.indexOf("#");

                                if (people.data.containsKey(searchText)) {
                                    tempText += people.data.get(searchText);

                                } else {
                                    int length = searchText.length() + 2;
                                    char[] spacechar = new char[length];
                                    Arrays.fill(spacechar, ' ');
                                    String space = String.copyValueOf(spacechar);
                                    tempText += space;
                                }
                                searchText = "";
                                end = true;
                            }
                        }

                        if (end) {
                            tempText += runText;
                            searchrun.setText(tempText, 0);
                        } else {
                            searchText += runText;
                            searchrun.setText("", 0);
                        }
                    }
                }
            }

            OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outaddress));
            docx.write(outputStream);
            outputStream.close();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Main.error = true;
            Main.gui.setTextField2("Word example file not found!");
            Main.gui.setEnterButtonEnable(true);

        } catch (IOException e) {
            Main.error = true;
            Main.gui.setTextField2("word error!");
            Main.gui.setEnterButtonEnable(true);
            e.printStackTrace();
        } catch (OLE2NotOfficeXmlFileException e) {
            Main.error = true;
            Main.gui.setTextField2("Need .docx file!");
            Main.gui.setEnterButtonEnable(true);
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        /*try {

            InputStream inputStream = new BufferedInputStream(new FileInputStream("C:\\Users\\kieChang\\Desktop\\公務員報告範本.docx"));
            XWPFDocument docx = new XWPFDocument(inputStream);
            inputStream.close();
            for (XWPFHeader header : docx.getHeaderList()) {

                for (XWPFParagraph xwpfParagraph : header.getParagraphs()) {
                    for (XWPFRun search : xwpfParagraph.getRuns()) {

                        System.out.println(search.toString() + "run");
                        search.setText("",0);
                    }
                    System.out.println("paragraph");
                }
                System.out.println("header");

            }
            OutputStream outputStream = new BufferedOutputStream(new FileOutputStream("C:\\Users\\kieChang\\Desktop\\公務員報告範本1.docx"));
            docx.write(outputStream);
            outputStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Main.error = true;
            Main.indicator.status("Word example file not found!");
            Main.gui.setEnterButtonEnable(true);

        } catch (IOException e) {
            Main.error = true;
            Main.gui.setEnterButtonEnable(true);
            Main.indicator.status("word error");
            e.printStackTrace();
        }*/


    }

}

