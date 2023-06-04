package onassis.utils.payment.synchronizer.parsers;

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


public class Parser {
    public enum Target {
        BEGIN("begin_rexp", "", new PartialParser(), true),
        DAY("day_rexp", "Day", new PartialParser00Num(), true),
        MONTH("month_rexp", "Month", new PartialParser00Num(), true),
        YEAR("year_rexp", "Year", new PartialParser(), true),
        WHOLE("whole_rexp", "Whole", new PartialParserWhole(), true),
        DECIMAL("decim_rexp", "Decimal", new PartialParserDecimal(), true),

        SKIP("skip_rexp", "", new PartialParser(), false),
        UNARY("unary_rexp", "Unary", new PartialParser(), false),
        DESCR("descr_rexp", "Description", new PartialParser(), true),
        ;


        public String regexpName;
        public String name;
        public PartialParser partialParser;
        public boolean mandatory;

        Target(String regexpName, String name, PartialParser partialParser, boolean mandatory) {
            this.regexpName = regexpName;
            this.name = name;
            this.partialParser = partialParser;
            this.mandatory = mandatory;
        }

        public static int nrOfMandatories() {
            return 6; //TODO
        }

        public static Stream<Target> stream() {
            return Stream.of(Target.values());
        }
    }

    public static String gId;
    public static final PartialParserMap parsers = new PartialParserMap();
    private RestIO restIO;
    //private OnassisController.Updates<P> updates;
    //private List<Integer> toLock = new ArrayList<>();


    @SneakyThrows
    public Parser(String bank) {

        //read properties
        String propFileName = String.format("regexps/%s.properties", bank);
        PropertiesExt _properties = new PropertiesExt();
        _properties.load(new FileReader(propFileName));

        Target.stream().forEach(p -> parsers.put(p, p.partialParser.init(_properties.getStringArray(p.regexpName, p.mandatory))));

        if (null == parsers.get(Target.BEGIN)) {
            throw new IllegalArgumentException("Empty regexps!");
        }
        // read postprocessors
        String postProcessorsFileName = String.format("regexps/%s.postprocess", bank);
        List<String> content;
        try (Stream<String> lines = Files.lines(Paths.get(postProcessorsFileName))) {
            content = lines.collect(Collectors.toList());
        }

        for(String line : content) {
            new PostProcessor(line);
        }

        Thread shutdownThread = new Thread() {
            @Override
            public void run() {
                IOUtils.farewell();
            }
        };

        getRuntime().addShutdownHook(shutdownThread);

        restIO = new RestIO(bank);
        if (!restIO.login()) {
            throw new RuntimeException("Login failed.");
        }
        m = new Matchable(restIO);
    }

    Matchable m = null;
    List<Matchable> matchables = new ArrayList<>();

    private Matchable getLastMatchable() {
        return matchables.get(matchables.size() - 1);
    }

    private Set<Integer> blackList = new HashSet<>(); //of p-ids

    public void collect(String str) {
        if(str.startsWith("***") || str.startsWith("*>*")) {
            m.getReceipt().getLines().add(new Line(str));
        } else {
            if (parsers.get(Target.BEGIN).match(str) && m.getReceipt().collectedValues.containsKey(Target.BEGIN)) {
                matchables.add(m);
                m = new Matchable(restIO);
                IOUtils.printOut("."); //one receipt parsed
            }
            m.collect(str);
        }
    }

    final public static SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    public void prepare() {

        for (Matchable m : matchables) {
            if (m.getState().equals(ALL_ATTRS_FOUND)) {
                m.pickMatch(blackList);
            }
        }
        IOUtils.printOut("Fetching new groupid ...");
        gId = "_" +  restIO.newGroupId() + "_"  + dateFormat.format(new Date());
        IOUtils.printOut(" Done.\nNewly created will have group-id : '" + gId + "'\n");
    }
    int errorNr = 1;
    @SneakyThrows
    public void update(String baseFileName) {
        IOUtils.StatementWriter writer = new IOUtils.StatementWriter(baseFileName);

        for(Matchable m : matchables) {
            try {
                switch(m.getState())  {
                    case CREATE :       restIO.create(m.getReceipt().getP(restIO));
                                        IOUtils.printOut("c");
                                        break;
                    case MATCH_FOUND:   restIO.lock(m.theChosenP.getId(), m.getReceipt().getDate());
                                        IOUtils.printOut("l");
                                        break;
                    case SKIP:          IOUtils.printOut("s");
                }
            } catch (Exception e) {
                IOUtils.printOut("ERROR: something went wrong updating!\n");
                IOUtils.dumpErrorFile(baseFileName,e,m);
                IOUtils.printOut("Skipping this update. Continuing ..\n");
                m.setState(SKIP);
            }
            writer.writeLog(m);
        }
        return;
    }

    @Override
    public String toString() {
        String indent = IOUtils.indent();
        return  indent + "Parser {" +
                indent + "matchables=" + matchables +
                indent + "blackList=" + blackList +
                indent + "} Parser";
    }
}
