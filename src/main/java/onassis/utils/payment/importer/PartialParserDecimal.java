package onassis.utils.payment.importer;

public class PartialParserDecimal extends PartialParser {
    @Override
    public String format(String text) {
        return text.replaceAll("[^\\d-]", "");
    }
}
