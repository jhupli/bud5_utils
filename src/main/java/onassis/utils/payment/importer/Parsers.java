package onassis.utils.payment.importer;

import lombok.Getter;
import lombok.SneakyThrows;


import java.io.FileReader;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;


public class Parsers {
    public enum Target {
        BEGIN("begin", "", new PartialParser(), true),
        DAY("day", "Day", new PartialParser00Num(), true),
        MONTH("month", "Month", new PartialParser00Num(), true),
        YEAR("year", "Year", new PartialParser2DigitYear(), true),
        WHOLE("whole", "Whole", new PartialParserWhole(), true),
        DECIMAL("decim", "Decimal", new PartialParserDecimal(), true),
        DESCR("descr", "Description", new PartialParser(), true),

        SKIP("skip", "", new PartialParser(), false),
        UNARY("unary", "Unary", new PartialParser(), false),
        CATEGORY("", "Category id", new PartialParser(), false),
        CATEGORY_NAME("", "Category", new PartialParser(), false),
        ;

        static public final Target [] parseableTargets = new Target[] { DAY, MONTH, YEAR, WHOLE, DECIMAL, SKIP, UNARY, DESCR, };
        public String regexpName;
        public String name;
        public PartialParser partialParser;

        public boolean mandatory;
        @Getter
        public static String groupId;
        @Getter
        public static String groupName;

        Target(String regexpName, String name, PartialParser partialParser, boolean mandatory) {
            this.regexpName = regexpName;
            this.name = name;
            this.partialParser = partialParser;
            this.mandatory = mandatory;
        }

        public String match(int ix, String line) {
            return partialParser.match(ix, line);
        }

        public static Stream<Target> stream() {
            return Stream.of(Target.values());
        }
        public static Stream<Target> parseablesStream() {
            return Stream.of(parseableTargets);
        }
    }

    public static PostProcessor PostProcessors;
    public static final PartialParserMap parsers = new PartialParserMap();
    public static final Map<Target, String> defaultValues = new HashMap<>();
    @Getter
    public static String groupId;

    @SneakyThrows
    public static void init(String bank) {
        //read properties
        String propFileName = String.format("regexps/%s.properties", bank);
        Properties _properties = new Properties();
        _properties.load(new FileReader(propFileName));
        Parsers.Target.stream().forEach(p -> parsers.put(p, p.partialParser.init(_properties.getStringArray(p.regexpName+ "_rexp", p.mandatory))));

        if (null == parsers.get(Target.BEGIN)) {
            throw new IllegalArgumentException("Empty regexps!");
        }

        //Default values
        Target.parseablesStream().forEach(t -> {
            String defaultKey = t.regexpName + "_default";
            String defaultValue = _properties.getString(defaultKey, false);
            if(null != defaultValue) {
                defaultValues.put(t, defaultValue);
            }
        });
    }

    @Override
    public String toString() {
        String indent = IOUtils.indent();
        return indent;

    }
}
