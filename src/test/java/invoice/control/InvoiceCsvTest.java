package invoice.control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import invoice.model.InvoiceHeader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class InvoiceCsvTest {

    @TempDir
    Path folder;

    private Path write(String name, String content) throws IOException {
        return Files.writeString(folder.resolve(name), content);
    }

    private Map<Integer, InvoiceHeader> load(String headers, String lines) throws IOException {
        Map<Integer, InvoiceHeader> invoices = new LinkedHashMap<>();
        InvoiceCsv.readHeaders(write("headers.csv", headers), invoices);
        InvoiceCsv.readLines(write("lines.csv", lines), invoices);
        return invoices;
    }

    @Test
    void loadsInvoicesWithTheirLines() throws IOException {
        Map<Integer, InvoiceHeader> invoices = load("1,22-11-2020,Ali\n2,13-10-2021,Saleh\n",
                "1,Mobile,3200,1\n1,Cover,20,2\n2,Laptop,4000,1\n");

        assertEquals(2, invoices.size());
        assertEquals("Ali", invoices.get(1).getName());
        assertEquals("22-11-2020", invoices.get(1).getDate());
        assertEquals(2, invoices.get(1).getInvoice_Lines().size());
        invoices.get(1).TotalPrices();
        assertEquals(3240.0, invoices.get(1).getTotal(), 1e-9);
    }

    @Test
    void theSampleDataLoads() throws IOException {
        Map<Integer, InvoiceHeader> invoices = new LinkedHashMap<>();

        InvoiceCsv.readHeaders(Path.of("sample-data", "InvoiceHeader.csv"), invoices);
        InvoiceCsv.readLines(Path.of("sample-data", "InvoiceLine.csv"), invoices);

        assertEquals(3, invoices.size());
        assertEquals(3, invoices.get(1).getInvoice_Lines().size());
        invoices.get(3).TotalPrices();
        assertEquals(75000.0, invoices.get(3).getTotal(), 1e-9);
    }

    @Test
    void savingAndLoadingAgainGivesTheSameInvoices() throws IOException {
        Map<Integer, InvoiceHeader> original = load("1,22-11-2020,Ali\n2,13-10-2021,Saleh\n",
                "1,Mobile,3200,1\n1,Cover,20,2\n2,Laptop,4000,1\n");
        Path headerFile = folder.resolve("saved-headers.csv");
        Path lineFile = folder.resolve("saved-lines.csv");

        InvoiceCsv.write(original, headerFile, lineFile);
        Map<Integer, InvoiceHeader> reloaded = new LinkedHashMap<>();
        InvoiceCsv.readHeaders(headerFile, reloaded);
        InvoiceCsv.readLines(lineFile, reloaded);

        assertEquals(original.keySet(), reloaded.keySet());
        for (int number : original.keySet()) {
            assertEquals(original.get(number).getName(), reloaded.get(number).getName());
            assertEquals(original.get(number).getInvoice_Lines().size(), reloaded.get(number).getInvoice_Lines().size());
        }
    }

    @Test
    void aHeaderLineWithTheWrongNumberOfValuesIsRejected() {
        IOException error = assertThrows(IOException.class, () -> load("1,22-11-2020\n", ""));

        assertTrue(error.getMessage().contains("Line 1"));
    }

    @Test
    void aPriceThatIsNotANumberIsRejected() {
        IOException error = assertThrows(IOException.class, () -> load("1,22-11-2020,Ali\n", "1,Mobile,cheap,1\n"));

        assertTrue(error.getMessage().contains("line 1"));
    }

    @Test
    void aLineForAnInvoiceThatDoesNotExistIsRejected() {
        IOException error = assertThrows(IOException.class, () -> load("1,22-11-2020,Ali\n", "9,Mobile,100,1\n"));

        assertTrue(error.getMessage().contains("invoice 9 does not exist"));
    }
}