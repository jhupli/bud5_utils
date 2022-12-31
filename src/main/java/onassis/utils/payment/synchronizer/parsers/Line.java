package onassis.utils.payment.synchronizer.parsers;

import lombok.Getter;

import java.util.*;

import static onassis.utils.payment.synchronizer.parsers.Parser.Target;

public class Line {

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
            String indent = IOUtils.indent();
            return indent + "Meta {" +
                    indent + "target=" + target +
                    indent + "regexp=" + regexp +
                    indent + "regexp_index=" + regexp_index +
                    indent + "value='" + value + '\'' +
                    indent + "}";
        }
    }

    @Getter
    private String line = null;
    List<Meta> meta = new ArrayList<>();

    void collect(int i, String str, Map<Target, String> collectedValues) {
        for(Target target : Target.values()) {
            if(collectedValues.containsKey(target)) {
                continue;
            }
            if(i >= target.partialParser.length()) {
                continue;
            }
            String value = target.partialParser.match(i, str);
            if(null != value) {
                meta.add(new Meta(target, target.partialParser.rexps.get(i), i, null == value ? "" : value));
                collectedValues.put(target, value);
            }
        }
    };

    public Line(String line) {
        this.line = line;
    }

    @Override
    public String toString() {
        String indent = IOUtils.indent();
        return  indent + "Line {" +
                indent +  "line=" + line +
                indent + "meta=" + meta +
                indent +"} Line";
    }
}
