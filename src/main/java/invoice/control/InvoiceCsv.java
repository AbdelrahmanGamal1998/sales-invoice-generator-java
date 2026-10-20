package invoice.control;

import invoice.model.InvoiceHeader;
import invoice.model.InvoiceLine;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * Reads and writes the two CSV files that hold the invoices.
 *
 * <ul>
 * <li>Header file, one invoice per line: {@code number,date,customer name}</li>
 * <li>Line file, one item per line: {@code invoice number,item name,price,count}</li>
 * </ul>
 */
public final class InvoiceCsv {

    private InvoiceCsv() {
    }

    /** Reads the invoice headers into the map, keyed by invoice number. */
    public static void readHeaders(Path file, Map<Integer, InvoiceHeader> invoices) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) {
                    continue;
                }
                String[] row = split(line, 3, lineNumber);
                try {
                    int number = Integer.parseInt(row[0].trim());
                    invoices.put(number, new InvoiceHeader(number, row[1].trim(), row[2].trim()));
                } catch (NumberFormatException e) {
                    throw new IOException("Header file, line " + lineNumber + ": the invoice number is not a number");
                }
            }
        }
    }

    /** Reads the invoice lines and adds each one to its invoice. The headers must already be loaded. */
    public static void readLines(Path file, Map<Integer, InvoiceHeader> invoices) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) {
                    continue;
                }
                String[] row = split(line, 4, lineNumber);
                try {
                    int number = Integer.parseInt(row[0].trim());
                    double price = Double.parseDouble(row[2].trim());
                    int count = Integer.parseInt(row[3].trim());
                    InvoiceHeader invoice = invoices.get(number);
                    if (invoice == null) {
                        throw new IOException("Line file, line " + lineNumber + ": invoice " + number + " does not exist");
                    }
                    invoice.AddLine(new InvoiceLine(number, row[1].trim(), price, count));
                } catch (NumberFormatException e) {
                    throw new IOException(
                            "Line file, line " + lineNumber + ": the number, price or count is not a number");
                }
            }
        }
    }

    /** Writes every invoice to the header file and its lines to the line file, replacing their contents. */
    public static void write(Map<Integer, InvoiceHeader> invoices, Path headerFile, Path lineFile)
            throws IOException {
        try (BufferedWriter headers = Files.newBufferedWriter(headerFile, StandardCharsets.UTF_8);
                BufferedWriter lines = Files.newBufferedWriter(lineFile, StandardCharsets.UTF_8)) {
            for (InvoiceHeader invoice : invoices.values()) {
                headers.write(invoice.getInv_Num() + "," + invoice.getDate() + "," + invoice.getName() + "\n");
                for (InvoiceLine item : invoice.getInvoice_Lines()) {
                    lines.write(item.getInv_Num() + "," + item.getItem_Name() + "," + item.getPrice() + ","
                            + item.getItem_count() + "\n");
                }
            }
        }
    }

    private static String[] split(String line, int fields, int lineNumber) throws IOException {
        String[] row = line.split(",");
        if (row.length != fields) {
            throw new IOException(
                    "Line " + lineNumber + ": expected " + fields + " values separated by commas but found " + row.length);
        }
        return row;
    }
}