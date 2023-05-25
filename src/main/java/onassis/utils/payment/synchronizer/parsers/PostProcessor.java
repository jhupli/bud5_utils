package onassis.utils.payment.synchronizer.parsers;

import onassis.dto.P;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PostProcessor {
    long category;
    List<String> tags = null;
    String descr = null;

    private static List<PostProcessor> postProcessors = new ArrayList<>();

    PostProcessor(String line) {
        if(line.isEmpty()) return;
        String [] parts = line.split(";");
        category = Integer.parseInt(parts[1]);
        tags = Arrays.asList(parts[0].split("#"));
        if(parts.length > 2) {
            descr = parts[2];
        }
        postProcessors.add(this);
    }

    private PostProcessor match(Receipt receipt) {
        for(Line line : receipt.getLines()) {
            if(!line.getLine().startsWith("***") && !line.getLine().startsWith("*>*")) {
                for (String tag : tags) {
                    if (line.getLine().toUpperCase().contains(tag.toUpperCase())) {
                        return this;
                    }
                }
            }
        }
        return null;
    }

    public static PostProcessor getMatch(Receipt receipt) {
        for(PostProcessor p : postProcessors) {
            PostProcessor result = p.match(receipt);
            if( null != result ) return result;
        }
        return null;
    }
}
