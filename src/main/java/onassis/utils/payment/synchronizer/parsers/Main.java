package onassis.utils.payment.synchronizer.parsers;

import lombok.SneakyThrows;
public class Main {

    @SneakyThrows
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java -jar OnassisUtils.jar <bank-name> <file of account-statement>");
            System.exit(2);
        }

        IOUtils.muteLoggers();
        Parser parser = new Parser(args[0]);

        IOUtils.StatementReader statements = new IOUtils.StatementReader(args[1]);
        String line = null;
        IOUtils.printOut("Collecting ..");
        do {
            line = statements.getLine();
            if(null == line)     break;
            if(line.startsWith("***") || line.startsWith("*>*")) {
                IOUtils.writeRawLog(line, args[1]);
            } else {
                parser.collect(line);
            }
        } while(null != line);
        IOUtils.printOut(" Done\n");
        IOUtils.dump(args[1]+".after_collect", parser);
        long size = parser.matchables.stream().filter(p -> p.getState().equals(Matchable.State.ALL_ATTRS_FOUND)).count();
        IOUtils.printOut("Preparing " + size + " receipt(s) :\n");
        parser.prepare();
        IOUtils.printOut("Prepare done. \n");
        IOUtils.dump(args[1]+".after_prepare", parser);

        if (IOUtils.askYesNo()) {
            IOUtils.printOut("Updating Onassis: ");
            RestIO.setNow();
            parser.update(args[1]);
            IOUtils.printOut("Done.\n");
        }



        //System.out.println("result:");
        //System.out.println(parser);
        System.exit(0);

    }
}
