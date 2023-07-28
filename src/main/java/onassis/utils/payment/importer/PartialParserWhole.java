package onassis.utils.payment.importer;

public class PartialParserWhole extends PartialParser {
    @Override
    public String format(String text) {
        return text.replaceAll("[^\\d-]", "");
    }
}
