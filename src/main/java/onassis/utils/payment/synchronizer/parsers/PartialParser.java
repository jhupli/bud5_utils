//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package onassis.utils.payment.synchronizer.parsers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PartialParser {

    private List<Pattern> patterns;
    public List<String> rexps;

    protected PartialParser() {
        super();
    }

    public PartialParser init(String[] regexps) {
        this.patterns = new ArrayList();
        this.rexps = new ArrayList();
        Arrays.stream(regexps).forEach((r) -> {
            this.patterns.add(Pattern.compile(r));
            this.rexps.add(r);
        });
        return this;
    }
    protected int length() {
        return patterns.size();
    }

    public boolean match(String text) {
        for(int row = 0; row < patterns.size(); row++) {
            if(null != match(row,text)) {
                return true;
            }
        }
        return false;
    }

    public String match(int row, String text) {
        if(row >= this.patterns.size()) {
            return null;
        }
        Pattern p = this.patterns.get(row);
        Matcher matcher = p.matcher(text);
        if (matcher.find()) {
            return format(matcher.group(1));
        }
        return null;
    }

    public String format(String text) {
        return text;
    }
}
