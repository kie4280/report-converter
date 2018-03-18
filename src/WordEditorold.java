import org.apache.poi.xwpf.usermodel.*;

import java.io.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.ListIterator;

public class WordEditorold {

    public void replace(String inaddress, String outaddress, People people) {

        try {
            InputStream inputStream = new BufferedInputStream(new FileInputStream(inaddress));
            XWPFDocument docx = new XWPFDocument(inputStream);
            inputStream.close();
            Iterator<XWPFTable> tableiterator = docx.getTablesIterator();
            while (tableiterator.hasNext()) {
                ListIterator<XWPFTableRow> rowiterator = tableiterator.next().getRows().listIterator();
                while (rowiterator.hasNext()) {
                    ListIterator<XWPFTableCell> celliterator = rowiterator.next().getTableCells().listIterator();
                    while (celliterator.hasNext()) {
                        XWPFTableCell tableCell = celliterator.next();
                        ListIterator<XWPFParagraph> paragraphiterator = tableCell.getParagraphs().listIterator();

                        while (paragraphiterator.hasNext()) {
                            XWPFParagraph paragraph = paragraphiterator.next();

                            if (paragraph.getText().contains("#")) {
                                ListIterator<XWPFRun> run = paragraph.getRuns().listIterator();
                                String searchText = "";
                                boolean end = true;

                                while (run.hasNext()) {
                                    XWPFRun searchrun = run.next();
                                    String runText = searchrun.text();

                                    if (runText.contains("#")) {

                                        int index = runText.indexOf("#");
                                        int nextindex = runText.indexOf("#", index + 1);
                                        String tempText = "";

                                        while (index != -1) {
                                            end = !end;
                                            if (end) {
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

                                            } else {
                                                tempText += runText.substring(0, index);
                                                if (nextindex == -1) {
                                                    searchText += runText.substring(index + 1);
                                                } else {
                                                    searchText += runText.substring(index + 1, nextindex);
                                                    runText = runText.substring(nextindex + 1);
                                                }
                                            }
                                            index = nextindex;
                                            nextindex = runText.indexOf("#", nextindex + 1);

                                        }

                                        searchrun.setText(tempText, 0);
                                    } else if (!end) {
                                        searchText += runText;
                                        searchrun.setText("", 0);
                                    }

                                }

                            }
                        }
                    }
                }
            }
            ListIterator<XWPFHeader> headerListIterator = docx.getHeaderList().listIterator();
            while (headerListIterator.hasNext()) {
                XWPFHeader header = headerListIterator.next();
                ListIterator<XWPFParagraph> paragraphiterator = header.getParagraphs().listIterator();
                while (paragraphiterator.hasNext()) {
                    XWPFParagraph paragraph = paragraphiterator.next();
                    if (paragraph.getText().contains("#")) {
                        ListIterator<XWPFRun> runiterator = paragraph.getRuns().listIterator();
                        String searchText = "";
                        boolean end = true;
                        while (runiterator.hasNext()) {
                            XWPFRun run = runiterator.next();
                            String runText = run.text();

                            if (runText.contains("#")) {
                                int index = runText.indexOf("#");
                                int nextindex = runText.indexOf("#", index + 1);
                                String tempText = "";

                                while (index != -1) {
                                    end = !end;
                                    if (end) {
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

                                    } else {
                                        tempText += runText.substring(0, index);
                                        if (nextindex == -1) {
                                            searchText += runText.substring(index + 1);
                                        } else {
                                            searchText += runText.substring(index + 1, nextindex);
                                            runText = runText.substring(nextindex + 1);
                                        }
                                    }
                                    index = nextindex;
                                    nextindex = runText.indexOf("#", nextindex + 1);
                                }
                                run.setText(tempText, 0);

                            } else if (!end) {
                                searchText += runText;
                                run.setText("", 0);
                            }

                        }
                    } else if (paragraph.getText().isEmpty()) {
                        //header.
                    }
                }
            }
            File dir = new File(outaddress);
            dir = dir.toPath().getParent().toFile();
            if (!dir.exists()) {
                dir.mkdir();
            }
            OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outaddress));
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
        }

    }

    public static void main(String[] args) {
        try {

            InputStream inputStream = new BufferedInputStream(new FileInputStream("C:\\Users\\kieChang\\Desktop\\公務員報告範本.docx"));
            XWPFDocument docx = new XWPFDocument(inputStream);
            inputStream.close();
            Iterator<XWPFHeader> headers = docx.getHeaderList().iterator();
            while (headers.hasNext()) {

                ListIterator<XWPFParagraph> paragraph = headers.next().getParagraphs().listIterator();
                while (paragraph.hasNext()) {
                    ListIterator<XWPFRun> run = paragraph.next().getRuns().listIterator();
                    while (run.hasNext()) {
                        XWPFRun search = run.next();
                        System.out.println(search.toString() + "run");
                    }
                    System.out.println("paragraph");
                }
                System.out.println("header" +
                        "");

            }

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
        }


    }

}

