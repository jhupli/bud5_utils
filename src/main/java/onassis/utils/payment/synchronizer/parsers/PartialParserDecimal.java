package onassis.utils.payment.synchronizer.parsers;

public class PartialParserDecimal extends  PartialParser{
    @Override
    public String format(String text) {
        return text.replaceAll("[^\\d-]", "");
    }
}
