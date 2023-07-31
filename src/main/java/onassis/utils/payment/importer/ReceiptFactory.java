package onassis.utils.payment.importer;

import java.util.ArrayList;
import java.util.List;
import static onassis.utils.payment.importer.Parsers.Target.BEGIN;
import static onassis.utils.payment.importer.Parsers.Target.SKIP;

public class ReceiptFactory {

    public static class MyReceipt extends ArrayList<Receipt> {
        public Receipt getLast() {
            return this.get(this.size() -1);
        }
    }

    static List<Receipt> getReceipts(List<String> statementLines) {
        MyReceipt receipts = new MyReceipt();
        receipts.add(new Receipt());
        for(String statementLine : statementLines) {
            receipts.getLast().getLines().add(new Line(statementLine));
            if(Parsers.parsers.get(BEGIN).match(statementLine)
            && !IOUtils.isSkipLine(statementLine)
            && !Parsers.parsers.get(SKIP).match(statementLine)
            ) {
                receipts.add(new Receipt());
            }
        }
        return receipts;
    }
}
