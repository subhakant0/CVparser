package com.cv.parser.factorymethod;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ExtractFiles {
    private Logger logger = LoggerFactory.getLogger(ExtractFiles.class);

    private Button btnExtractContents;
    private List<String> superList;
    private Table tableExtractedContent;
    private Button btnSaveInJSONfile;
    private Button btnSaveInCSVfile;

    private Shell mainShell;
    private Label status;

    private ParserFactory parserFactory = new ParserFactory();

    public ExtractFiles(Shell shell, Button btnExtractContents, List<String> superList, Table tableExtractedContent,
                        Button btnSaveInJSONfile, Button btnSaveInCSVfile) {
        this.mainShell = shell;
        this.btnExtractContents = btnExtractContents;
        this.superList = superList;
        this.tableExtractedContent = tableExtractedContent;
        this.btnSaveInJSONfile = btnSaveInJSONfile;
        this.btnSaveInCSVfile = btnSaveInCSVfile;
    }

    public void handleButtonClick() {
        btnExtractContents.addListener(SWT.Selection, new Listener() {
            public void handleEvent(org.eclipse.swt.widgets.Event arg0) {

                Shell doneShell = new Shell(mainShell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
                RowLayout rowLayout = new RowLayout();
                rowLayout.pack = false;
                rowLayout.wrap = true;
                rowLayout.marginLeft = 15;
                rowLayout.marginTop = 15;
                rowLayout.marginRight = 55;
                rowLayout.marginBottom = 15;
                doneShell.setLayout(rowLayout);

                status = new Label(doneShell, SWT.NONE);

                try {
                    parsePDF(parserFactory);
                    parseDOC(parserFactory);
                    parseDOCX(parserFactory);
                    parseTXT(parserFactory);
                } catch (UnsupportedFileExtension e) {
                    logger.error(e.getMessage());
                }

                displayDocumentsInTable();
                status.setText("Finished!");

                btnExtractContents.setEnabled(false);
                btnSaveInJSONfile.setEnabled(true);
                btnSaveInCSVfile.setEnabled(true);

                doneShell.pack();
                doneShell.open();

                // Move the dialog to the center of the top level shell.
                Rectangle shellBounds = mainShell.getBounds();
                Point dialogSize = doneShell.getSize();

                doneShell.setLocation(shellBounds.x + (shellBounds.width - dialogSize.x) / 2,
                        shellBounds.y + (shellBounds.height - dialogSize.y) / 2);
            }

        });
    }

    // display extracted documents in table
    private void displayDocumentsInTable() {
        for (int i = 0; i < superList.size(); i++) {
            TableItem item = new TableItem(tableExtractedContent, SWT.NONE);
            item.setText(new String[]{superList.get(i)});
        }
    }

    private void doParse(ParserInterface parserInterface) {
        // based on the given file type
        // get all the files of that type
        // then save it to a temporary list
        parserInterface.setFiles();
        parserInterface.extractFiles();

        // superList is a super List that contain all
        superList.addAll(parserInterface.getContents());
    }

    private void parsePDF(ParserFactory factory) throws UnsupportedFileExtension {
        doParse(factory.getContent("pdf"));
    }

    private void parseDOC(ParserFactory factory) throws UnsupportedFileExtension {
        doParse(factory.getContent("doc"));
    }

    private void parseDOCX(ParserFactory factory) throws UnsupportedFileExtension {
        doParse(factory.getContent("docx"));
    }

    private void parseTXT(ParserFactory factory) throws UnsupportedFileExtension {
        doParse(factory.getContent("txt"));
    }
}
