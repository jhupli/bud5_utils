package onassis.utils.payment.importer;

public class PartialParser00Num extends PartialParser {
    @Override
    public String format(String text) {
        return text.replaceAll("[^\\d-]", "");
    }
}
