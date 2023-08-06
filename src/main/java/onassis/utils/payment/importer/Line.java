package onassis.utils.payment.importer;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import onassis.utils.payment.importer.Parsers.Target;
import static onassis.utils.payment.importer.Parsers.Target.DESCR;
import static onassis.utils.payment.importer.Parsers.Target.SKIP;


import onassis.utils.payment.importer.Parsers.Target;

import javax.naming.spi.StateFactory;

public class Line  {
    @Getter
    private String line = null;

    public Line(String line) {
        this.line = line;
    }

    List<Meta> meta = new ArrayList<>();
    static class Meta {
        Target target;
        String regexp;
        int regexp_index;
        String value;



        public Meta(Target target, String regexp, int regexp_index, String value) {
            this.target = target;
            this.regexp = regexp;
            this.regexp_index = regexp_index;
            this.value = value;
        }

        @Override
        public String toString() {
            String indent = onassis.utils.payment.synchronizer.parsers.IOUtils.indent();
            return indent + "Meta {" +
                    indent + "  target=" + target +
                    indent + "  regexp=" + regexp +
                    indent + "  regexp_index=" + regexp_index +
                    indent + "  value='" + value + '\'' +
                    indent + "}";
        }
    }

    void collect(int i, String statementLine, Map<Target, String> collectedValues) {
        for(Target target : Target.values()) {
            if(collectedValues.containsKey(target)) {
                continue;
            }
            if(i >= target.partialParser.length()) {
                continue;
            }
            String value = target.partialParser.match(i, statementLine);
            if(null != value) {
                meta.add(new Meta(target, target.partialParser.rexps.get(i), i, null == value ? "" : value));
                collectedValues.put(target, value);
            }
        }
    };

    @Override
    public String toString() {
        String indent = IOUtils.indent();
        return  indent + "Line {" +
                indent + "  line=" + line +
                indent + "  meta=" + meta +
                indent +"} Line";
    }
}
