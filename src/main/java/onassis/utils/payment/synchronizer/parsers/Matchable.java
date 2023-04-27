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
        ATTRS_NOT_FOUND,
        ALL_ATTRS_FOUND,
        SKIP,
        BREAK,
        MATCH_FOUND,

        MATCH_FOUND_ALREADY_LOCKED,
        CREATE,
        ERROR,
    }

    @Getter
    @Setter
    private State state = State.ATTRS_NOT_FOUND;
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
        if(state.equals(State.MATCH_FOUND) || state.equals(State.MATCH_FOUND_ALREADY_LOCKED)) {
            blackList.add(theChosenP.getId());
        } else if(state.equals(State.CREATE)) {
            PostProcessor postProcessor = PostProcessor.getMatch(receipt);
            if(postProcessor != null) {
                IOUtils.printOut("Category found ("+postProcessor.category+"). ");
                receipt.chosenCategory = postProcessor.category;
            } else {
                receipt.chosenCategory = IOUtils.pickCategory(restIO.getCategories()).getId();
            }
            if(postProcessor != null && postProcessor.descr != null ) {
                IOUtils.printOut("Description found ("+postProcessor.descr+").");
            } else {
                receipt.description = IOUtils.pickDescription(receipt.getDescription());
            }
            if(postProcessor != null && postProcessor.descr != null ) IOUtils.printOut("\n");
        }
    }

    public void collect(String str) {
        receipt.collect(str);
        if(receipt.hasItAll() && state.equals(State.ATTRS_NOT_FOUND)) {
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
