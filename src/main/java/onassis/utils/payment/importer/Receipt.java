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

    public Receipt() {
    }
    public boolean hasItAll() {
        return
                collectedValues.containsKey(DAY) &&
                collectedValues.containsKey(MONTH) &&
                collectedValues.containsKey(YEAR) &&
                collectedValues.containsKey(WHOLE) &&
                collectedValues.containsKey(DECIMAL);
    }


    /*public PInfo getPseudoP(RestIO restIO) {
        if(!hasItAll()) {
            return null;
        }

        String descr = getDescription();
        String c_descr = null;
        PostProcessor postProcessor = PostProcessor.getMatch(this);
        if(null != postProcessor) {
            descr = postProcessor.descr + (StringUtils.isNotBlank(descr) ? ": " + descr : "");
            description = aakkosetPois(descr);
            Optional<C> c = restIO.getCategories().stream().distinct().filter(cc -> cc.id == postProcessor.category).findFirst();
            if(null != c.get()) {
                c_descr = c.get().getDescr();
                chosenCategory = c.get().getId();
            }
        }
        return new PInfo(null, getDate(), getDate(), getAmount(), c_descr, RestIO.getAccount(), descr, true);

    //PInfo(Integer id, Date dc, Date d,       BigDecimal i, String c_descr, String a_descr, String descr) {
    }*/

    private String aakkosetPois(String line) {
        return line.replaceAll("ä","ae")
                .replaceAll("Ä","Ae")
                .replaceAll("ö","oe")
                .replaceAll("Ö","Oe")
                .replaceAll("ü","ue")
                .replaceAll("Ü","Ue")
                .replaceAll("ß","ss");
    }
    public P getP(RestIO restIO) {
        if(!hasItAll()) {
            return null;
        }
        return new P(null,
                getDate(),
                getDate(),
                getAmount(),
                Integer.parseInt("" + chosenCategory),
                RestIO.getAccountId(),
                true,
                "TODO",
                description,
                true);
    }

    public void parse() {
        for(int lineNr=0 ; lineNr<lines.size() ; lineNr++) {
            Line line = lines.get(lineNr);
            String statementLine = line.getLine();
            if(IOUtils.isSkipLine(statementLine)) {
                continue;
            }

            String value = null;
            for(Target target : Target.values()) {
                if(BEGIN.equals(target) || CATEGORY.equals(target)) {
                    //BEGIN on jo parsittu
                    //CATEGORY haetaan DESCR yhteydessä, siis:
                    continue;
                }

                if(collectedValues.containsKey(target)) {
                    //The first match will overrule
                    continue;
                }
                for(int parserIx = 0 ; parserIx < PartialParserMap.maxLength ; parserIx++ ) {
                    value = target.match(parserIx, statementLine); //Is there a match?
                    if (null != value) {
                        line.meta.add(new Line.Meta(target, target.partialParser.rexps.get(parserIx), parserIx, null == value ? "" : value));
                        collectedValues.put(target, value);
                    }
                }
            }
            String description = collectedValues.get(DESCR);
            if(null == description) {
                description = "";
            }
            PostProcessor postProcessor = PostProcessor.getMatch(this);
            if (null != postProcessor) {
                value = null == value ? "" : ": " + value;
                collectedValues.put(DESCR, postProcessor.descr + value);
                collectedValues.put(CATEGORY, "" + postProcessor.category);
            }
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

    public String getDescription() {
        if(collectedValues.containsKey(Target.DESCR)) {
            return collectedValues.get(Target.DESCR).replaceAll("\t", " ").replaceAll("\n", " ");
        }
        return null;
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

    private BigDecimal getAmount() {
        long amount = Long.valueOf((collectedValues.containsKey(Target.UNARY) ?
                        collectedValues.get(Target.UNARY) : "") +
                        collectedValues.get(Target.WHOLE) +
                        collectedValues.get(Target.DECIMAL));

        return BigDecimal.valueOf(amount, 2);
    }


}

