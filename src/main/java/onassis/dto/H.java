package onassis.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

public class H {
    Timestamp hd;
    String op;
    Integer rownr;
    Integer id;
	Date dc;
    Date d;
    BigDecimal i;
    Integer c;
    String c_descr;
    Integer a;
    String a_descr;
    Boolean s;
	String g;
	String descr;
       
    public H(Timestamp hd, String op, Integer rownr, Integer id, Date dc, Date d, BigDecimal i, Integer c, String c_descr, Integer a, String a_descr, Boolean s,
			String g, String descr) {
		super();
		this.hd = hd;
		this.op = op;
		this.rownr = rownr;
		this.id = id;
		this.dc = dc;
		this.d = d;
		this.i = i;
		this.c = c;
		this.c_descr = c_descr;
		this.a = a;
		this.a_descr = a_descr;
		this.s = s;
		this.g = g;
		this.descr = descr;
	}

	public Timestamp getHd() {
		return hd;
	}

	public void setHd(Timestamp hd) {
		this.hd = hd;
	}

	public String getOp() {
		return op;
	}

	public void setOp(String op) {
		this.op = op;
	}

	public Integer getRownr() {
		return rownr;
	}

	public void setRownr(Integer rownr) {
		this.rownr = rownr;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getDc() {
		return dc;
	}

	public void setDc(Date dc) {
		this.dc = dc;
	}

	public Date getD() {
		return d;
	}

	public void setD(Date d) {
		this.d = d;
	}

	public BigDecimal getI() {
		return i;
	}

	public void setI(BigDecimal i) {
		this.i = i;
	}

	public Integer getC() {
		return c;
	}

	public void setC(Integer c) {
		this.c = c;
	}

	public String getC_descr() {
		return c_descr;
	}

	public void setC_descr(String c_descr) {
		this.c_descr = c_descr;
	}

	public Integer getA() {
		return a;
	}

	public void setA(Integer a) {
		this.a = a;
	}

	public String getA_descr() {
		return a_descr;
	}

	public void setA_descr(String a_descr) {
		this.a_descr = a_descr;
	}

	public Boolean getS() {
		return s;
	}

	public void setS(Boolean s) {
		this.s = s;
	}

	public String getG() {
		return g;
	}

	public void setG(String g) {
		this.g = g;
	}

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}
}
