package onassis.utils.payment.synchronizer.parsers;

public class PartialParser00Num extends  PartialParser{
    @Override
    public String format(String text) {
        return text.replaceAll("[^\\d-]", "");
    }
}
