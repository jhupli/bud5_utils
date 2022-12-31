package onassis.utils.payment.synchronizer.parsers;

public class PartialParserWhole extends  PartialParser{
    @Override
    public String format(String text) {
        return text.replaceAll("[^\\d-]", "");
    }
}
