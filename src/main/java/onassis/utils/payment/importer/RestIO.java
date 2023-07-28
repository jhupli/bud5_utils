package onassis.utils.payment.importer;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;
import lombok.Getter;
import lombok.SneakyThrows;
import onassis.OnassisController;
import onassis.dto.A;
import onassis.dto.C;
import onassis.dto.P;
import onassis.dto.PInfo;
import onassis.utils.payment.synchronizer.parsers.IOUtils;
import onassis.utils.payment.synchronizer.parsers.Parser;
import onassis.utils.payment.synchronizer.parsers.Receipt;
import org.apache.commons.lang3.StringUtils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import static com.jayway.restassured.RestAssured.given;

public class RestIO {
    private String host;
    private String user;
    @Getter
    private static String account;
    @Getter
    private static Integer accountId = null;
    private String pw;
    private List<C> categories = null;
    private List<A> accounts = null;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static Date now = null;
    private static String nowStr = null;

    public static void setNow() {
        now = new Date();
        nowStr = dateFormat.format(now);
    }

    public static Date getNow() {
        return now;
    }

    public static String getNowString() {
        return nowStr;
    }
    @SneakyThrows
    public RestIO(String bankName) {
        String fileName = String.format("regexps/%s.properties", bankName);
        InputStream in = new FileInputStream(fileName);
        Properties props = new Properties();
        props.load(in);
        in.close();
        user = props.getProperty("user");
        host = props.getProperty("host");
        account = props.getProperty("account");

        if (StringUtils.isEmpty(user) || StringUtils.isEmpty(host) || StringUtils.isEmpty(account)) {
            throw new RuntimeException("account, user or host missing.");
        }
    }


    void lock(int pId, Date date) {
        String lockUrl = String.format("http://%s/lock?id=%s&l=true&d=%s", host, pId, dateFormat.format(date));
        String updateResponse = null, lockResponse = null;
        try {

            lockResponse = ((Response) RestAssured.given().auth().basic(user, pw).header("Content-Type","application/json;charset=UTF-8").when().get(lockUrl, new Object[0])).asString();
            if(lockResponse.length()>0) {
                throw new RuntimeException(lockResponse);
            }
        } catch (Exception e) {

            onassis.utils.payment.synchronizer.parsers.IOUtils.printOut("ERROR: Failed to lock: " + pId + "\n");
        }
    }

    void create(P p) {
        OnassisController.Updates u = new OnassisController.Updates();
        List<P> pList = new ArrayList<>();
        //p.setD(now);
        p.setG(Parser.gId);
        pList.add(p);
        u.setCreated(pList);
        u.setModified(new ArrayList());
        u.setDeleted(new ArrayList<>());
        try {
            update(u);
        } catch (Exception e) {
            onassis.utils.payment.synchronizer.parsers.IOUtils.printOut("ERROR: Failed to create: " + p.toString() + "\n");
            throw e;
        }
    }

    private void update(OnassisController.Updates upd) {
        String createUrl = String.format("http://%s/payments/update", host);
        String responseJson = ((Response) RestAssured.given().auth().basic(user, pw).header("Content-Type","application/json;charset=UTF-8").contentType("application/json").body(upd).when().post(createUrl)).asString();
        if(responseJson.length()>0) {
            throw new RuntimeException(responseJson);
        }
    }
    boolean login() {
        pw = onassis.utils.payment.synchronizer.parsers.IOUtils.login();
        String catUrl = "http://" + host + "/cat/list";
        String accUrl = "http://" + host + "/acc/list";
        onassis.utils.payment.synchronizer.parsers.IOUtils.printOut("Loggin in ...");

        try {
            String responseJson =
                    given().auth().basic(user, pw).header("Content-Type","application/json;charset=UTF-8").when().get(catUrl).asString();
            List<C> categories_ = (new Gson()).fromJson(responseJson, new TypeToken<List<C>>() {
            }.getType());
            categories = categories_.stream().filter(c -> c.getActive()).collect(Collectors.toList());
        } catch (Exception e) {
            System.out.println(e);
            onassis.utils.payment.synchronizer.parsers.IOUtils.printOut(" Failed. Category not found.\n");
            System.exit(3);
        }

        try {
            String responseJson =
                    given().auth().basic(user, pw).header("Content-Type","application/json;charset=UTF-8").when().get(accUrl).asString();
            accounts = (new Gson()).fromJson(responseJson, new TypeToken<List<A>>() {
            }.getType());

            accountId = accounts.stream().filter(a -> {
                return account.equals(a.descr);
            }).findFirst().get().getId().intValue();
        } catch (Exception e) {
            onassis.utils.payment.synchronizer.parsers.IOUtils.printOut(" Failed. Account not found.\n");
            System.exit(2);
        }

        onassis.utils.payment.synchronizer.parsers.IOUtils.printOut(" Done.\n");
        return categories != null;
    }

    String newGroupId() {
        String url = "http://" + host + "/group/newid";
        try {
            String groupId =
                    given().auth().basic(user, pw).header("Content-Type","application/json;charset=UTF-8").when().get(url).asString();
            return groupId;
        } catch (Exception e) {
            IOUtils.printOut("Unable to create new group id.\n");
            throw new RuntimeException(e);
        }
    }

    List<PInfo> getPCandidates(Receipt receipt) {
        List<PInfo> pInfos = null;
        String url = String.format("http://%s/info?d=%s&i=%s&a=%s", this.host, receipt.getDateString(), receipt.getAmount(), this.accountId.intValue());
        receipt.setUrl(url);
        String responseJson = ((Response) RestAssured.given().auth().basic(this.user, this.pw).when().get(url, new Object[0])).asString();
        List<PInfo> Infos = (new Gson()).fromJson(responseJson, new TypeToken<List<PInfo>>() {
        }.getType());
        return Infos;
    }

    List<C> getCategories() {
        return categories;
    }
}
