package onassis.utils.payment.importer;

import java.util.HashMap;

public class PartialParserMap extends HashMap<Parsers.Target, PartialParser> {
    public static int maxLength = 0;
    public PartialParserMap() {
        super();
    }

    @Override
    public PartialParser put(Parsers.Target target, PartialParser partialParser) {
        if(partialParser.length() > maxLength) {
            maxLength = partialParser.length();
        }
        return super.put(target, partialParser);
    }

    public int getMaxLength() {
        return maxLength;
    }
}
