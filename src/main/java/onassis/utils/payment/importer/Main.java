package onassis.utils.payment.importer;

import lombok.SneakyThrows;
import onassis.utils.payment.synchronizer.parsers.Matchable;
import onassis.utils.payment.synchronizer.parsers.Parser;

import java.util.List;

public class Main {

    static public Parsers parsers = null;
    static public PostProcessor postProcessor = null;
    static public IOUtils ioUtils = null;
    static public RestIO restIO = null;

    @SneakyThrows
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java -jar OnassisUtils.jar <bank-name> <file of account-statement>");
            System.exit(2);
        }

        String bankName = args[0];
        String statementsFile = args[1];

        ioUtils = new IOUtils(statementsFile);
        IOUtils.muteLoggers();
        RestIO.init(bankName);
        RestIO.login();
        Parsers.init(bankName);
        PostProcessor.init(bankName);

        List<Receipt> receipts = ReceiptFactory.getReceipts(ioUtils.statementLines);
        IOUtils.dump(args[1]+".after_read", receipts);
        receipts.stream().forEach(receipt -> receipt.parse());
        IOUtils.dump(args[1]+".after_parse", receipts);
        System.exit(0);

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
