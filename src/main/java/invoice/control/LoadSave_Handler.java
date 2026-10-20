package invoice.control;

import invoice.model.InvoiceHeader;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import javax.swing.JFileChooser;

/** Asks the user for the two CSV files, then loads and saves the invoices through {@link InvoiceCsv}. */
public class LoadSave_Handler {

    private static File InvoiceHeader_file;
    private static File InvoiceLine_file;
    private static MessageDialog_Handler dialogHandler = new MessageDialog_Handler();

    public static void loadFiles(Map<Integer, InvoiceHeader> invoices) {
        dialogHandler.showDialog("Choose the Header File First", "info");
        File headerFile = chooseCsvFile();
        if (headerFile == null) {
            return;
        }
        try {
            InvoiceCsv.readHeaders(headerFile.toPath(), invoices);
        } catch (IOException e) {
            dialogHandler.showDialog(e.getMessage(), "error");
            return;
        }

        dialogHandler.showDialog("Choose Line File", "info");
        File lineFile = chooseCsvFile();
        if (lineFile == null) {
            return;
        }
        try {
            InvoiceCsv.readLines(lineFile.toPath(), invoices);
        } catch (IOException e) {
            dialogHandler.showDialog(e.getMessage(), "error");
            return;
        }

        InvoiceHeader_file = headerFile;
        InvoiceLine_file = lineFile;
    }

    public static void Save_File(Map<Integer, InvoiceHeader> All_Invoices) {
        if (InvoiceHeader_file == null || InvoiceLine_file == null) {
            dialogHandler.showDialog("This File is not found", "error");
            return;
        }
        try {
            InvoiceCsv.write(All_Invoices, InvoiceHeader_file.toPath(), InvoiceLine_file.toPath());
        } catch (IOException ex) {
            dialogHandler.showDialog("Try Again", "error");
        }
    }

    /** Lets the user pick a .csv file. Returns null when the user cancels. */
    private static File chooseCsvFile() {
        JFileChooser chooser = new JFileChooser();
        while (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File selected = chooser.getSelectedFile();
            if (selected.getName().endsWith(".csv")) {
                return selected;
            }
            dialogHandler.showDialog("Please Select .CSV File!", "error");
        }
        return null;
    }
}