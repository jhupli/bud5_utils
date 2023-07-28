package onassis.utils.payment.importer;

import lombok.SneakyThrows;
import onassis.utils.payment.synchronizer.parsers.Matchable;
import onassis.utils.payment.synchronizer.parsers.Parser;
import onassis.utils.payment.synchronizer.parsers.RestIO;

public class Main {

    @SneakyThrows
    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("Usage: java -jar OnassisUtils.jar 1.0 <bank-name> <file of account-statement>");
            System.exit(2);
        }

        IOUtils ioUtils= new IOUtils(args[1]);
        IOUtils.muteLoggers();

        System.exit(0);

        Parsers parsers = new Parsers(args[0]);
        PostProcessor postProcessor = new PostProcessor(args[0]);
        /*

        Parser parser = new Parser(args[0]);

        IOUtils.StatementReader statements = new IOUtils(args[1]);
        String line = null;

        IOUtils.printOut("Collecting ..");

        /* {
            line = statements.getLine();
            parser.collect(line);
            if(null == line)     break;
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
            IOUtils.printOut("\nDone.\n");
        }



        //System.out.println("result:");
        //System.out.println(parser);
        System.exit(0); */

    }
}
