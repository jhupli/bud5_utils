package onassis.utils.payment.importer;

public class PartialParser2DigitYear extends PartialParser {
    @Override
    public String format(String text) {
        if(null == text) return null;
        return text.length() == 2 ? "20" + text : text;
    }
}
