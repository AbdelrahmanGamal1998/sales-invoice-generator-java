# Sales Invoice Generator (Java Swing)

[![Build](https://github.com/AbdelrahmanGamal1998/sales-invoice-generator-java/actions/workflows/build.yml/badge.svg)](https://github.com/AbdelrahmanGamal1998/sales-invoice-generator-java/actions/workflows/build.yml)

A desktop application (Java Swing) to create, edit, delete, load and save sales invoices. Invoices and their items
are shown in tables, and the data is stored in two CSV files. It was my first Java project in an online
programming course (2022) and was rebuilt with an MVC structure (`model`, `view`, `control`).

## Run it

You need JDK 17 or newer and Maven.

```bash
git clone https://github.com/AbdelrahmanGamal1998/sales-invoice-generator-java.git
cd sales-invoice-generator-java
mvn compile exec:java      # opens the window; use File > Load and pick the two files in sample-data/
mvn test                   # 10 tests
```

CSV format: the header file has one invoice per line (`number,date,customer`), the line file has one item per line
(`invoice number,item name,price,count`). Examples are in [`sample-data/`](sample-data).

## Tests

The tests cover the model (line and invoice totals, invoice numbering) and the CSV reading and writing
(`InvoiceCsv`): loading the sample data, a save-and-load round trip, and clear errors for a wrong number of values, a
price that is not a number, and a line for an invoice that does not exist. The window itself is not tested.

## Notes

- Changes made when it was cleaned up: Maven build and lower-case packages; the CSV code moved out of the file
  chooser code into `InvoiceCsv` so it can be tested and reports the exact line of a mistake; cancelling the file
  chooser no longer loops forever. The NetBeans files and compiled classes are not included.

## License

[MIT](LICENSE)