package onassis.utils.payment.importer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.freva.asciitable.AsciiTable;
import com.github.freva.asciitable.Column;
import com.github.freva.asciitable.HorizontalAlign;
import com.github.freva.asciitable.OverflowBehaviour;

import org.apache.commons.lang3.CharSet;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import lombok.SneakyThrows;
import onassis.dto.C;
import onassis.dto.PInfo;
import onassis.utils.payment.importer.Line;
//import onassis.utils.payment.synchronizer.parsers.Matchable;
import onassis.utils.payment.importer.Receipt.State;
import onassis.utils.payment.synchronizer.parsers.Parser;

import static java.lang.Runtime.getRuntime;

public class IOUtils {
    static public List<String> statementLines = new ArrayList();
    static public String statementFileName;
    @SneakyThrows
    public IOUtils(String statementFileName) {
        this.statementFileName = statementFileName;
        Thread shutdownThread = new Thread() {
            @Override
            public void run() {
                farewell();
            }
        };
        getRuntime().addShutdownHook(shutdownThread);

        Scanner scan;
        scan = new Scanner(new File(statementFileName));
        // check that there is not already logfile
        isOnassisFileReadOnly(statementFileName);
        if(!scan.hasNext()) {
            statementLines.add(scan.nextLine());
        }
        printOut(statementFileName + " read.\n");
    }

    static public boolean isSkipLine(String line) {
        return line.startsWith("***") || line.startsWith("*>*");
    }

    static private void isOnassisFileReadOnly(String basefileName) {
        File f = new File(basefileName + ".onassis");
        if(f.exists()) {
            printOut("ERROR: there is already " + basefileName + ".onassis - file!\n");
            printOut("Use the latest " + basefileName + ".onassis - file as input.\n");
            System.exit(3);
        }
    }

    static void lockLog() {
        File f = new File(statementFileName + ".onassis");
        if(!f.setReadOnly()) {
            printOut("WARN: could not set .onassis -file readOnly.");
        }
    }
    static public void muteLoggers() {
        Set<String> loggers = new HashSet<>(Arrays.asList("org.apache.http", "groovyx.net.http", "com.jayway.restassured.internal.RequestSpecificationImpl"));

        for(String log:loggers) {
            Logger logger = (Logger) LoggerFactory.getLogger(log);
            logger.setLevel(Level.WARN);
            logger.setAdditive(false);
        }
    }

    static public void writeRawLog(String s, String statementFileName) throws  Exception{
        BufferedWriter writer = new BufferedWriter(new FileWriter(statementFileName + ".onassis", true));
        writer.write(s +"\n");
        writer.close();
    }

    static public class StatementWriter{
        String statementFileName = null;
        @SneakyThrows
        public StatementWriter(String statementFileName) {
            this.statementFileName = statementFileName;

        }

        public void writeLog(Receipt receipt) throws  Exception{
            BufferedWriter writer = new BufferedWriter(new FileWriter(statementFileName + ".onassis", true));
            String linePrefix = "";
            switch (receipt.getState()) {
                case CREATE:
                    linePrefix = "*>*";
                    break;
                case MATCH_FOUND_ALREADY_LOCKED:
                case MATCH_FOUND:
                    linePrefix = "***";
                    break;
                case ATTRS_NOT_FOUND:
                    linePrefix = "***";
                    break;
                default:
                    linePrefix = "";
            }
            for (Line l : receipt.getLines()) {
                writer.write( (l.getLine().startsWith("***") ||  l.getLine().startsWith("*>*") ? "" : linePrefix) + l.getLine() + "\n");
            }
            writer.write("----------------------------------------------------------------------");
            writer.close();
        }
    }


/*        public void writeLog(Matchable m) throws  Exception{
                BufferedWriter writer = new BufferedWriter(new FileWriter(statementFileName + ".onassis", true));
                String linePrefix = "";
                switch (m.getState()) {
                    case CREATE:
                        linePrefix = "*>*";
                        break;
                    case MATCH_FOUND_ALREADY_LOCKED:
                    case MATCH_FOUND:
                        linePrefix = "***";
                        break;
                    case ATTRS_NOT_FOUND:
                        linePrefix = "***";
                        break;
                    default:
                        linePrefix = "";
                }
                for (Line l : m.getReceipt().getLines()) {
                    writer.write( (l.getLine().startsWith("***") ||  l.getLine().startsWith("*>*") ? "" : linePrefix) + l.getLine() + "\n");
                }
                writer.close();
        }
    } '/




    // https://www.google.com/search?q=ascii+%E2%95%9F&rlz=1C1GCEU_deDE842DE842&oq=ascii+%E2%95%9F&aqs=chrome..69i57j0i22i30l9.2786j0j7&sourceid=chrome&ie=UTF-8#imgrc=Bw5m0affChzNHM

    /*
    Character[] borderStyles = "ABCDEFGHIJKLMNOPQRSTUVWXYZ123".chars().mapToObj(c -> (char)c).toArray(Character[]::new);
    Prints

ABBBCBBBBBBBBBCBBBBBBBBBBCBBBBBBCBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBD
E   F  Name   F Diameter F Mass F Atmosphere                      G
HIIIJIIIIIIIIIJIIIIIIIIIIJIIIIIIJIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIK
L 1 M Mercury M  0.382   M 0.06 M             minimal             N
OPPPQPPPPPPPPPQPPPPPPPPPPQPPPPPPQPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPR
L 2 M   Venus M  0.949   M 0.82 M    Carbon dioxide, Nitrogen     N
OPPPQPPPPPPPPPQPPPPPPPPPPQPPPPPPQPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPR
L 3 M   Earth M  1.000   M 1.00 M     Nitrogen, Oxygen, Argon     N
OPPPQPPPPPPPPPQPPPPPPPPPPQPPPPPPQPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPR
L 4 M    Mars M  0.532   M 0.11 M Carbon dioxide, Nitrogen, Argon N
STTTUTTTTTTTTTUTTTTTTTTTTUTTTTTTUTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTV
W   X Average X  0.716   X 0.50 X                                 Y
Z111211111111121111111111211111121111111111111111111111111111111113

kulmiin?
╭	╮
╰	╯
     */


    static private final Character[] TABLE_ASCII =                    new Character[]{'╭', '─', '┬', '╮', '│', '│', '│', '╞', '═', '╪', '╡', '│', '│', '│',  '├',  '─',  '┼',  '┤', '├', '─', '┤', '┤', '│', '│', '│', '╰', '─', '┴', '┘'};
    static private final Character[] TABLE_ASCII_NO_DATA_SEPARATORS = new Character[]{'╭', '─', '┬', '╮', '│', '│', '│', '╞', '═', '╪', '╡', '│', '│', '│', null, null, null, null, '├', '─', '┤', '┤', '│', '│', '│', '╰', '─', '┴', '╯'};

    static public int LINELENGTH = 80;
    static public void showLines(List<String> rows, String header) {
        System.out.println(
                AsciiTable.getTable(TABLE_ASCII_NO_DATA_SEPARATORS, rows,
                        Arrays.asList(
                                (new Column()).minWidth(LINELENGTH + 3).maxWidth(LINELENGTH + 3).headerAlign(HorizontalAlign.CENTER)
                                              .dataAlign(HorizontalAlign.LEFT)
                                              .header(header).with((r) -> { return r.replaceAll("\t", " "); })))
        );
    };

    static private String CREATE_KEY = "c";
    static public void showP(List<PInfo> pInfoList) {
        if(null == pInfoList) {
            return;
        }
        i = 0;
        System.out.println(AsciiTable.getTable(TABLE_ASCII_NO_DATA_SEPARATORS, pInfoList, Arrays.asList(
                (new Column()).minWidth(5).maxWidth(5).headerAlign(HorizontalAlign.CENTER)
                        .dataAlign(HorizontalAlign.CENTER)
                        .header("#").with((p) -> { return i != pInfoList.size() - 1 ? "" + (i+1) : CREATE_KEY; }),
                (new Column()).minWidth(12).maxWidth(12).headerAlign(HorizontalAlign.CENTER)
                        .dataAlign(HorizontalAlign.LEFT)
                        .header("Date").with((p) -> { return new SimpleDateFormat("dd.MM.yyyy").format(p.getDc());}),
                (new Column()).minWidth(15).maxWidth(15, OverflowBehaviour.ELLIPSIS_RIGHT).headerAlign(HorizontalAlign.CENTER)
                        .dataAlign(HorizontalAlign.CENTER)
                        .header("Category").with((p) -> { return p.getC_descr();  }),

/*                (new Column()).minWidth(15).maxWidth(15, OverflowBehaviour.ELLIPSIS_RIGHT).headerAlign(HorizontalAlign.CENTER)
                        .dataAlign(HorizontalAlign.CENTER)
                        .header("Category").with((p) -> { return i != pInfoList.size() - 1 ? p.getC_descr() : "-"; }), */

                (new Column()).minWidth(3).maxWidth(3, OverflowBehaviour.ELLIPSIS_RIGHT).headerAlign(HorizontalAlign.CENTER)
                        .dataAlign(HorizontalAlign.CENTER)
                        .header("L").with((p) -> { return i++ != pInfoList.size() - 1 ? (p.isLocked() ? "*" : "" ) : "*"; }),

                (new Column()).minWidth(LINELENGTH - 5 - 12 - 15 - 3).maxWidth(LINELENGTH - 5 -12 - 15, OverflowBehaviour.ELLIPSIS_RIGHT).headerAlign(HorizontalAlign.CENTER)
                        .dataAlign(HorizontalAlign.CENTER)
                        .header("Description").with((p) -> { return "" + p.getDescr(); })
        )));
    }
    static public boolean askYesNo() {
        return ask("Ready to update. Continue ?", "yYnN",null, null).equalsIgnoreCase("Y");
    }

    static private int i;
    static public C pickCategory() {
        List<C> rows = RestIO.getCategories();
        i=0;

        List<Pair<Pair, Pair >> cols = new ArrayList<>();
        for(int i=1; i <= rows.size(); i+=2) {
                 cols.add(Pair.of(
                        Pair.of("" + i, rows.get(i - 1).getDescr()),
                         (i + 1) > rows.size() ?
                                 Pair.of("", "") :
                                 Pair.of("" + (i + 1), rows.get(i).getDescr()))
                 );
        }

        System.out.println(
                AsciiTable.getTable(TABLE_ASCII_NO_DATA_SEPARATORS, cols,
                        Arrays.asList(
                                (new Column()).minWidth(5).maxWidth(5, OverflowBehaviour.ELLIPSIS_RIGHT).headerAlign(HorizontalAlign.CENTER)
                                        .dataAlign(HorizontalAlign.CENTER)
                                        .header("#").with((r) -> { return "" + r.getLeft().getLeft(); }),
                                (new Column()).minWidth(LINELENGTH/2 - 5).maxWidth(LINELENGTH - 5).headerAlign(HorizontalAlign.CENTER)
                                        .dataAlign(HorizontalAlign.LEFT)
                                        .header("Description").with((r) -> { return "" + r.getLeft().getRight();  }),
                                (new Column()).minWidth(5).maxWidth(5, OverflowBehaviour.ELLIPSIS_RIGHT).headerAlign(HorizontalAlign.CENTER)
                                        .dataAlign(HorizontalAlign.CENTER)
                                        .header("#").with((r) -> { return "" + r.getRight().getLeft(); }),
                                (new Column()).minWidth(LINELENGTH/2 - 5).maxWidth(LINELENGTH - 5).headerAlign(HorizontalAlign.CENTER)
                                        .dataAlign(HorizontalAlign.LEFT)
                                        .header("Description").with((r) -> { return "" + r.getRight().getRight();  })
                        )
                )
        );

        String answer =  ask("Pick a Category #", null, 1, rows.size());
        return null == answer ? null : rows.get(Integer.parseInt(answer) - 1);
    };

    static public void dump(String baseFileName, List<Receipt> receiptsList) throws IOException {
        printOut("Dumping model " + baseFileName + ".dump ..");
        BufferedWriter writer = new BufferedWriter(new FileWriter(baseFileName + ".dump"));
        for(Receipt receipt : receiptsList) {
            writer.write(receipt.toString());
        }
        writer.close();
        printOut(" Done.\n");
    }
    static public void dump(String baseFileName, Parsers model) throws IOException {
        printOut("Dumping model " + baseFileName + ".dump ..");
        BufferedWriter writer = new BufferedWriter(new FileWriter(baseFileName + ".dump"));
        writer.write(model.toString());
        writer.close();
        printOut(" Done.\n");
    }
    static int errorNro = 1;

       static public void dumpErrorFile(String baseFileName, Exception e, Receipt receipt) throws IOException {
        String fileName = baseFileName + ".error." + (errorNro++) + ".debug";
        printOut("Writing " + fileName + " ..");
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        writer.write(e + "\n");
        writer.write("-------------------------------------\n");
        for(Line l : receipt.getLines()) {
            writer.write(l.getLine() + "\n");
        }
        writer.write("-------------------------------------\n");
        writer.write(receipt + "\n");
        writer.close();
        printOut(" Done.\n");
    }


    /*static public void dumpErrorFile(String baseFileName, Exception e, Matchable m) throws IOException {
        String fileName = baseFileName + ".error." + (errorNro++) + ".debug";
        printOut("Writing " + fileName + " ..");
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        writer.write(e + "\n");
        writer.write("-------------------------------------\n");
        for(Line l : m.getReceipt().getLines()) {
            writer.write(l.getLine() + "\n");
        }
        writer.write("-------------------------------------\n");
        writer.write(m + "\n");
        writer.close();
        printOut(" Done.\n");
    }
    static public State pickMatch(Matchable m, int lineNr) {
        showLines(m.getReceipt().getLines().stream().map(l -> {return l.getLine(); }).collect(Collectors.toList()), ""+lineNr+":"+" Receipt " + m.getReceipt().getAmount());
        showP(m.getPInfo());
        String answer =  ask("Pick a Payment #, c to create new, s to skip, b to break ", "scb", 1, m.getPInfo().size());
        if(answer.equalsIgnoreCase("s")) {
            return State.SKIP;
        }
        if(answer.equalsIgnoreCase("b")) {
            showLines(Arrays.asList("NOTE IMPORTANT: If running another round, use latest <previous input-file>.onassis as input!"),
                    "*****IMPORTANT NOTE******");
            return State.BREAK;
        }
        if(answer.equals(CREATE_KEY)) {
            return State.CREATE;
        } else {
            PInfo pInfo = m.getPInfo().get(Integer.parseInt(answer) - 1);
            m.setTheChosenP(pInfo);

            if (pInfo.isLocked()) {
                return State.MATCH_FOUND_ALREADY_LOCKED;
            }
            return State.MATCH_FOUND;
        }
    } */

    static public State pickMatch(Receipt receipt) {
        showLines(receipt.getLines().stream().map(l -> {return l.getLine(); }).collect(Collectors.toList()), ""+receipt.lineNr+":"+" Receipt " + receipt.getAmount());
        List<PInfo> rows = new ArrayList<>(receipt.getCandidates());
        rows.add(receipt.asPinfo()); // or shall we create a new brave transaction?
        showP(rows);
        String answer =  ask("Pick a Payment #, c to create new, s to skip, b to break ", "scb", 1,receipt.collectedValues.size());
        if(answer.equalsIgnoreCase("s")) {
            return State.SKIP;
        }
        if(answer.equalsIgnoreCase("b")) {
            showLines(Arrays.asList("NOTE IMPORTANT: If running another round, use latest <previous input-file>.onassis as input!"),
                    "*****IMPORTANT NOTE******");
            return State.BREAK;
        }
        if(answer.equals(CREATE_KEY)) {
            return State.CREATE;
        } else {
            //clear otherCandidates and return only the chosen one
            PInfo chosenCandidate = rows.get(Integer.parseInt(answer) - 1);
            receipt.getCandidates().clear();
            receipt.getCandidates().add(chosenCandidate);

            if (chosenCandidate.isLocked()) {
                return State.MATCH_FOUND_ALREADY_LOCKED;
            }
            return State.MATCH_FOUND;
        }
    }
    static String pickDescription() {
        String answer = ask("Give description or 'x' to accept :", "x", null,null, true);
        /*if(!answer.equalsIgnoreCase("x")) {
            return answer.replaceAll("\n", " ").replaceAll("\t", " ");
        }*/
        return answer;
    }
    /*
    public void printC(List<C> categories) {
        System.out.println(AsciiTable.getTable(borderStyles, planets, Arrays.asList(
                new Column().with(planet -> Integer.toString(planet.num)),
                new Column().header("Name").footer("Average").headerAlign(CENTER).dataAlign(RIGHT).with(planet -> planet.name),
                new Column().header("Diameter").headerAlign(RIGHT).dataAlign(CENTER).footerAlign(CENTER)
                        .footer(String.format("%.03f", planets.stream().mapToDouble(planet -> planet.diameter).average().orElse(0)))
                        .with(planet -> String.format("%.03f", planet.diameter)),
                new Column().header("Mass").headerAlign(RIGHT).dataAlign(LEFT)
                        .footer(String.format("%.02f", planets.stream().mapToDouble(planet -> planet.mass).average().orElse(0)))
                        .with(planet -> String.format("%.02f", planet.mass)),
                new Column().header("Atmosphere").headerAlign(LEFT).dataAlign(CENTER).with(planet -> planet.atmosphere))));


                new Column().header(p.getA_descr()).maxWidth(12, OverflowBehaviour.NEWLINE).with(planet -> planet.atmosphere),
                new Column().header(p.getC_descr()).maxWidth(12, OverflowBehaviour.NEWLINE).with(planet -> planet.atmosphere),
                new Column().header(p.getC_descr())).maxWidth(12, OverflowBehaviour.CLIP_LEFT).with(planet -> planet.atmosphere),
                new Column().header(p.getDescr())"Atmosphere Composition").maxWidth(12, OverflowBehaviour.CLIP_RIGHT).with(planet -> planet.atmosphere),
                new Column().header("Atmosphere Composition").maxWidth(12, OverflowBehaviour.ELLIPSIS_LEFT).with(planet -> planet.atmosphere),
                new Column().header("Atmosphere Composition").maxWidth(12, OverflowBehaviour.ELLIPSIS_RIGHT).with(planet -> planet.atmosphere))));
    } */

    static private String ask(String question, String possibleSingleAnswers, Integer start, Integer end) {
        return ask(question, possibleSingleAnswers, start, end, false);
    }
    static private String ask(String question, String possibleSingleAnswers, Integer start, Integer end, boolean allowFreeAnswer) {
        while(true) {
            System.out.print(question +
                    (null != possibleSingleAnswers ?  "[" + possibleSingleAnswers + "]" : "") +
                    " (or q to quit) : ");
            Scanner sc = new Scanner(System.in, "UTF-8"); //System.in is a standard input stream.
            String choice = sc.nextLine();

            if(StringUtils.isEmpty(choice)) {
                continue;
            }

            switch (choice) {
                case "q" :  System.exit(1);
            }

            if(null == possibleSingleAnswers && null == start && null == end) {
                return choice; //anything goes
            }
            try {
                int ix = Integer.parseInt(choice);
                if( null != start && ix < start) {
                    continue;
                }
                if( null != end && ix > end) {
                    continue;
                }
                return ""+ix;
            } catch (Exception toIgnore) {
                //keep on going
            }

            if(null != possibleSingleAnswers && choice.length() == 1 && possibleSingleAnswers.contains(choice)) {
                return choice;
            }
            if(allowFreeAnswer && choice.length() > 1) {
                return choice;
            }
        }
    }
    static public String indent() {
        return "\n" + StringUtils.repeat(' ', Thread.currentThread().getStackTrace().length - 5);
    }
    static public void printOut(String str) {
        System.out.print(str);
    }
    static public String login() {
        return ask("Onassis password", null, null, null);
    }
    static public void farewell() {
        printOut("Exiting ... Goodbye.\n");
    }
}
