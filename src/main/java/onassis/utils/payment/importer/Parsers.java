package onassis.utils.payment.importer;

import lombok.Getter;
import lombok.SneakyThrows;
import onassis.utils.payment.synchronizer.parsers.Parser;

import java.io.FileReader;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;


public class Parsers {
    public enum Target {
        BEGIN("begin_rexp", "", new PartialParser()),
        DAY("day_rexp", "Day", new PartialParser00Num()),
        MONTH("month_rexp", "Month", new PartialParser00Num()),
        YEAR("year_rexp", "Year", new PartialParser()),
        WHOLE("whole_rexp", "Whole", new PartialParserWhole()),
        DECIMAL("decim_rexp", "Decimal", new PartialParserDecimal()),
        SKIP("skip_rexp", "", new PartialParser()),
        UNARY("unary_rexp", "Unary", new PartialParser()),
        DESCR("descr_rexp", "Description", new PartialParser()),
        CATEGORY("", "Category id", new PartialParser()),
        CATEGORY_NAME("", "Category", new PartialParser()),
        ;

        static public final Target [] parseableTargets = new Target[] { DAY, MONTH, YEAR, WHOLE, DECIMAL, SKIP, UNARY, DESCR, };
        public String regexpName;
        public String name;
        public PartialParser partialParser;

        @Getter
        public static String groupId;
        @Getter
        public static String groupName;

        Target(String regexpName, String name, PartialParser partialParser) {
            this.regexpName = regexpName;
            this.name = name;
            this.partialParser = partialParser;
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
        Target.stream()
              .forEach(t -> parsers.put(t, t.partialParser.init(_properties.getStringArray(t.regexpName))));
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
