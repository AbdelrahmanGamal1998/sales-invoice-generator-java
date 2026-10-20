package invoice.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class InvoiceModelTest {

    @Test
    void aLineTotalIsPriceTimesCount() {
        InvoiceLine line = new InvoiceLine(1, "Cover", 20, 2);

        assertEquals(40.0, line.getTotalPrice(), 1e-9);
    }

    @Test
    void anInvoiceTotalIsTheSumOfItsLines() {
        InvoiceHeader invoice = new InvoiceHeader(1, "22-11-2020", "Ali");
        invoice.AddLine(new InvoiceLine(1, "Mobile", 3200, 1));
        invoice.AddLine(new InvoiceLine(1, "Cover", 20, 2));
        invoice.AddLine(new InvoiceLine(1, "Headphone", 130, 1));

        invoice.TotalPrices();

        assertEquals(3370.0, invoice.getTotal(), 1e-9);
    }

    @Test
    void newInvoicesGetTheNextNumber() {
        InvoiceHeader.counter = 1;

        InvoiceHeader first = new InvoiceHeader("01-01-2024", "Mona");
        InvoiceHeader second = new InvoiceHeader("02-01-2024", "Omar");

        assertEquals(1, first.getInv_Num());
        assertEquals(2, second.getInv_Num());
    }

    @Test
    void loadingAnInvoiceNumberMovesTheCounterPastIt() {
        new InvoiceHeader(7, "01-01-2024", "Mona");

        assertEquals(8, new InvoiceHeader("02-01-2024", "Omar").getInv_Num());
    }
}