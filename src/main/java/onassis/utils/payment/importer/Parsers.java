package onassis.utils.payment.importer;

import lombok.SneakyThrows;

import java.io.FileReader;
import java.lang.reflect.Array;
import java.util.List;
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
        CATEGORY("", "Category", new PartialParser()),
        ;

        static public final Target [] parseableTargets = new Target[] { DAY, MONTH, YEAR, WHOLE, DECIMAL, SKIP, UNARY, DESCR, };
        public String regexpName;
        public String name;
        public PartialParser partialParser;


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
    }

    public static PostProcessor PostProcessors;
    public static final PartialParserMap parsers = new PartialParserMap();

    @SneakyThrows
    public static void init(String bank) {
        //read properties
        String propFileName = String.format("regexps/%s.properties", bank);
        Properties _properties = new Properties();
        _properties.load(new FileReader(propFileName));
        Target.stream().forEach(p -> parsers.put(p, p.partialParser.init(_properties.getStringArray(p.regexpName))));
        if (null == parsers.get(Target.BEGIN)) {
            throw new IllegalArgumentException("Empty regexps!");
        }
    }

    @Override
    public String toString() {
        String indent = IOUtils.indent();
        return indent;

    }
}
