package onassis.utils.payment.importer;

import lombok.Getter;
import lombok.Setter;
import onassis.dto.C;
import onassis.dto.P;
import onassis.dto.PInfo;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import onassis.utils.payment.importer.Parsers.Target;

import static onassis.utils.payment.importer.Parsers.Target.*;
public class Receipt {
    @Getter
    private List<Line> lines = new ArrayList<>();

    @Getter
    @Setter
    public String url = "";

    @Getter
    @Setter
    Long chosenCategory = null;

    @Getter
    @Setter
    String description = "";

    @Getter
    @Setter
    List<PInfo> candidates = null;
    @Override
    public String toString() {
        String indent = IOUtils.indent();
        return  indent + "Receipt {" +
                indent + "collectedValues=" + collectedValues +
                indent + "url=" + url +
                indent + "lines=" + lines +
                indent + "chosenC=" + chosenCategory +
                indent + "description=" + description +
                indent + "} Receipt";
    }

    Map<Target, String> collectedValues = new HashMap<>();
    public int lineNr = 0;
    private static AtomicInteger lineCount = new AtomicInteger(0);

    public Receipt() {
        lineNr = lineCount.incrementAndGet();
    }
    public boolean hasItAll() {
        return
                collectedValues.containsKey(DAY) && collectedValues.containsKey(MONTH) && collectedValues.containsKey(YEAR)
                && collectedValues.containsKey(WHOLE) && collectedValues.containsKey(DECIMAL);
    }




    private String aakkosetPois(String line) {
        return line.replaceAll("ä","ae")
                .replaceAll("Ä","Ae")
                .replaceAll("ö","oe")
                .replaceAll("Ö","Oe")
                .replaceAll("ü","ue")
                .replaceAll("Ü","Ue")
                .replaceAll("ß","ss");
    }

    public PInfo asPinfo() {
        return new PInfo(null, getDate(), getDate(), getAmount(), getCategoryDescription(), RestIO.getAccount(), getDescription(), true);
    }

    public P asP(RestIO restIO) {
        if(!hasItAll()) {
            return null;
        }
        return new P(null, getDate(), getDate(), getAmount(), getCategory(), RestIO.getAccountId(), true,"", getDescription(), true);
    }

    public void interact() {
        if(!collectedValues.containsKey(CATEGORY)) {
            C c = IOUtils.pickCategory();
            collectedValues.put(CATEGORY,"" + c.getId());
            collectedValues.put(CATEGORY_NAME,"" + c.getDescr());
        }
        candidates = RestIO.getPCandidates(this);



    }


    public void parse() {
        for(int lineNr=0 ; lineNr<lines.size() ; lineNr++) {
            Line line = lines.get(lineNr);
            String statementLine = line.getLine();
            if(IOUtils.isSkipLine(statementLine)) {
                continue;
            }

            String value = null;
            for(int parserIx = 0 ; parserIx < PartialParserMap.maxLength ; parserIx++ ) {
                for (Target target : Target.parseableTargets) {
                    if (collectedValues.containsKey(target)) {
                        //The first match will overrule
                        continue;
                    }

                    value = target.match(parserIx, statementLine); //Is there a match with this index?
                    if (null != value) {
                        line.meta.add(new Line.Meta(target, target.partialParser.rexps.get(parserIx), parserIx, null == value ? "" : value));
                        collectedValues.put(target, value);
                    }
                }
            }
        }


        PostProcessor postProcessor = PostProcessor.getMatch(this);
        if (null != postProcessor) {
            String originalDescription = collectedValues.get(DESCR); // "sample"
            description = (null == originalDescription ? "" : ": " + originalDescription);
            collectedValues.put(DESCR, postProcessor.descr + originalDescription); // "sample" -> "Tag: sample"
            collectedValues.put(CATEGORY, "" + postProcessor.category);
            collectedValues.put(CATEGORY_NAME, "" + RestIO.getCategoryName(postProcessor.category));
        }

        //Last, the default Values, if any
        for(Target t : parseableTargets) {
            if(collectedValues.containsKey(t) || !Parsers.defaultValues.containsKey(t)) {
                continue;
            }
            collectedValues.put(t, Parsers.defaultValues.get(t));
        }
    }

    public void collect(String str) {
        Line newLine = new Line(str);
        lines.add(newLine);
        String value = SKIP.partialParser.anymatch(str);
        if(null != value) {
            collectedValues.put(SKIP, value);
            newLine.meta.add(new Line.Meta(SKIP, "*" , -1, value));
            return;
        }
        for (int i = 0; i < /*Parser.parsers.getMaxLength()*/ Parsers.parsers.size(); i++) {
            newLine.collect(i, str, collectedValues);
        }
    }

    public String getDateString() {
        if(! collectedValues.containsKey(Target.YEAR) ||
                ! collectedValues.containsKey(Target.MONTH) ||
                ! collectedValues.containsKey(Target.DAY)) {
            throw new RuntimeException("Missing YEAR, MONTH or DAY to form a date.");
        }
        String dateStr = collectedValues.get(Target.YEAR) + "-" +
                collectedValues.get(Target.MONTH) + "-" +
                collectedValues.get(Target.DAY);
        return dateStr;
    }

    final private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public String getCategoryDescription() {
        return collectedValues.get(CATEGORY_NAME);
    }

    public int getCategory() {
        return Integer.parseInt(collectedValues.get(CATEGORY_NAME));
    }
    public String getDescription() {
         return collectedValues.get(DESCR);
        /*if(collectedValues.containsKey(Target.DESCR)) {
            return collectedValues.get(Target.DESCR).replaceAll("\t", " ").replaceAll("\n", " ");
        }
        return null;*/
    }

    private Date getDate() {
        Date date = null;
        try {
            date = dateFormat.parse(getDateString());
        } catch (ParseException e) {
            throw new RuntimeException("Internal error: Error parsing date.");
        }
        return date;
    }

    public BigDecimal getAmount() {
        long amount = Long.valueOf((collectedValues.containsKey(Target.UNARY) ?
                        collectedValues.get(Target.UNARY) : "") +
                        collectedValues.get(Target.WHOLE) +
                        collectedValues.get(Target.DECIMAL));

        return BigDecimal.valueOf(amount, 2);
    }


}

