package onassis.utils.payment.importer;

import lombok.SneakyThrows;
import onassis.utils.payment.synchronizer.parsers.Line;
import onassis.utils.payment.synchronizer.parsers.Receipt;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PostProcessor {
    public long category;
    private List<String> tags = null;
    public String descr = null;

    public static List<PostProcessor> postProcessors = new ArrayList<>();

    @SneakyThrows
    PostProcessor(String bank) {
        // read postprocessors
        String postProcessorsFileName = String.format("regexps/%s.postprocess", bank);
        List<String> content;
        try (Stream<String> lines = Files.lines(Paths.get(postProcessorsFileName))) {
            content = lines.collect(Collectors.toList());
        }
        for(String line : content) {
            if(line.isEmpty()) return;
            String [] parts = line.split(";");
            category = Integer.parseInt(parts[1]);
            tags = Arrays.asList(parts[0].split("#"));
            if(parts.length > 2) {
                descr = parts[2];
            }
            postProcessors.add(this);
        }
    }

    private PostProcessor match(Receipt receipt) {
        for(Line line : receipt.getLines()) {
            if(!IOUtils.isSkipLine(line.getLine())) {
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
