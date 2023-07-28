package onassis.utils.payment.importer;

import lombok.SneakyThrows;

import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Runtime.getRuntime;
import static onassis.utils.payment.synchronizer.parsers.Matchable.State.ALL_ATTRS_FOUND;
import static onassis.utils.payment.synchronizer.parsers.Matchable.State.SKIP;


public class Parsers {
    public enum Targets {
        BEGIN("begin_rexp", "", new PartialParser()),
        DAY("day_rexp", "Day", new PartialParser00Num()),
        MONTH("month_rexp", "Month", new PartialParser00Num()),
        YEAR("year_rexp", "Year", new PartialParser()),
        WHOLE("whole_rexp", "Whole", new PartialParserWhole()),
        DECIMAL("decim_rexp", "Decimal", new PartialParserDecimal()),
        SKIP("skip_rexp", "", new PartialParser()),
        UNARY("unary_rexp", "Unary", new PartialParser()),
        DESCR("descr_rexp", "Description", new PartialParser()),
        ;


        public String regexpName;
        public String name;
        public PartialParser partialParser;


        Targets(String regexpName, String name, PartialParser partialParser) {
            this.regexpName = regexpName;
            this.name = name;
            this.partialParser = partialParser;
        }

        public static Stream<Targets> stream() {
            return Stream.of(Targets.values());
        }
    }

    public static PostProcessor PostProcessors;
    public static final PartialParserMap parsers = new PartialParserMap();

    @SneakyThrows
    public Parsers(String bank) {
        //read properties
        String propFileName = String.format("regexps/%s.properties", bank);
        Properties _properties = new Properties();
        _properties.load(new FileReader(propFileName));
        Targets.stream().forEach(p -> parsers.put(p, p.partialParser.init(_properties.getStringArray(p.regexpName))));
        if (null == parsers.get(Targets.BEGIN)) {
            throw new IllegalArgumentException("Empty regexps!");
        }
    }


    @Override
    public String toString() {
        String indent = IOUtils.indent();
        return indent;

    }
}
