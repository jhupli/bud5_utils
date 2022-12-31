package onassis.utils.payment.synchronizer.parsers;

import java.util.HashMap;

public class PartialParserMap extends HashMap<Parser.Target, PartialParser> {
    private int maxLength = 0;
    public PartialParserMap() {
        super();
    }

    @Override
    public PartialParser put(Parser.Target target, PartialParser partialParser) {
        if(partialParser.length() > maxLength) {
            maxLength = partialParser.length();
        }
        return super.put(target, partialParser);
    }

    public int getMaxLength() {
        return maxLength;
    }
}
