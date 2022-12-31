package onassis.utils.payment.synchronizer.parsers;

import lombok.Getter;
import lombok.Setter;
import onassis.dto.C;
import onassis.dto.P;
import onassis.dto.PInfo;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static onassis.utils.payment.synchronizer.parsers.Parser.Target;

public class Receipt {

    @Getter
    private List<Line> lines = new ArrayList<>();

    @Getter
    @Setter
    public String url = "";

    @Getter
    @Setter
    Long chosenCategory;

    @Getter
    @Setter
    String description;

    @Override
    public String toString() {
        String indent = IOUtils.indent();
        return  indent + "Receipt {" +
                indent + "collectedValues=" + collectedValues +
                indent + "url=" + url +
                indent + "lines=" + lines +
                indent + "chosenC=" + chosenCategory +
                indent + "chosenDescription=" + description +
                indent + "} Receipt";
    }

    Map<Target, String> collectedValues = new HashMap<>();

    public Receipt() {
    }
    public boolean hasItAll() {
        return
        collectedValues.containsKey(Target.BEGIN) &&
                collectedValues.containsKey(Target.DAY) &&
                collectedValues.containsKey(Target.MONTH) &&
                collectedValues.containsKey(Target.YEAR) &&
                collectedValues.containsKey(Target.WHOLE) &&
                collectedValues.containsKey(Target.DECIMAL);
    }

    public PInfo getPseudoP() {
        if(!hasItAll()) {
            return null;
        }
        return new PInfo(null, getDate(), getDate(), getAmount(), null, RestIO.getAccount(), getDescription());
    //PInfo(Integer id, Date dc, Date d,       BigDecimal i, String c_descr, String a_descr, String descr) {
    }

    public P getP(RestIO restIO) {
        if(!hasItAll()) {
            return null;
        }
        return new P(null, getDate(), getDate(), getAmount(), Integer.parseInt("" + chosenCategory) , RestIO.getAccountId(), true, "TODO", description, true);
    }

    public void collect(String str) {
        Line newLine = new Line(str);
        lines.add(newLine);

        for (int i = 0; i < Parser.parsers.getMaxLength(); i++) {
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

    public Date getDate() {
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

