package onassis.dto;

import java.math.BigDecimal;
import java.util.Date;

public class Pb extends P {

	public BigDecimal b;
	
	public Pb() {
		super();
	}
	
	public Pb(Long id, Date dc, Date d, BigDecimal i, Integer c, Integer a, Boolean s, String g, String descr, Boolean l, BigDecimal b) {
		super();
		this.id = id;
		this.dc = dc;
		this.d = d;
		this.i = i;
		this.c = c;
		this.a = a;
		this.s = s;
		this.g = g;
		this.descr = descr;
		this.l = l;
		this.b = b;
	}

	public BigDecimal getB() {
		return b;
	}
	public void setB(BigDecimal b) {
		this.b = b;
	}
}
