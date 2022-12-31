package onassis.utils.payment.synchronizer.parsers;

import lombok.Getter;
import lombok.Setter;
import onassis.dto.C;
import onassis.dto.P;
import onassis.dto.PInfo;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Matchable {
    public enum State {
        NEW,
        ATTRS_NOT_FOUND,
        ALL_ATTRS_FOUND,
        SKIP,
        BREAK,
        MATCH_FOUND,
        CREATE,
        ERROR,
    }

    @Getter
    private State state = State.NEW;
    @Getter
    private List<PInfo> pInfo;
    @Getter
    private Receipt receipt = new Receipt();

    @Getter
    @Setter
    public PInfo theChosenP = null;

    @Getter
    @Setter
    public P PtoCreate = null;

    private final RestIO restIO;

    public Matchable(RestIO restIO) {
        this.restIO = restIO;
    }

    private static int ix = 1;
    private static boolean breaked = false;

    public void pickMatch(Set<Integer> blackList) {
        pInfo = getPInfo().stream().filter(p -> {
            return p.getId() == null || !(blackList.contains(p.getId()));
        }).collect(Collectors.toList());
        if(breaked) {
            state = State.BREAK;
            return;
        }
        state = IOUtils.pickMatch(this, ix++);
        if(state.equals(State.BREAK)){
            breaked = true;
        }
        if(state.equals(State.MATCH_FOUND)) {
            blackList.add(theChosenP.getId());
        } else if(state.equals(State.CREATE)) {
            receipt.chosenCategory = IOUtils.pickCategory(restIO.getCategories()).getId();
            receipt.description = IOUtils.pickDescription(receipt.getDescription());
        }
    }

    public void collect(String str) {
        state = State.ATTRS_NOT_FOUND;
        receipt.collect(str);
        if(receipt.hasItAll()) {
            state = State.ALL_ATTRS_FOUND;
            pInfo = restIO.getPCandidates(receipt);
            pInfo.add(receipt.getPseudoP());
        }
    }

    @Override
    public String toString() {
        String indent = IOUtils.indent();

        return  indent + "Matchable { " +
                indent + "state=" + state +
                indent + "receipt=" + receipt +
                indent + "theChosenP=" + theChosenP +
                indent + "pInfos=" + pInfo +
                indent +  "} Matchable";
    }
}
