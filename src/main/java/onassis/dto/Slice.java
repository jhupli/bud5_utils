/**
 * 
 */
package onassis.dto;

import java.math.BigDecimal;

/**
 * @author Janne Hupli
 * @version 1.0 Aug 2017
 */
public class Slice {
    public BigDecimal sl;
    public int c;
    
    public Slice(BigDecimal sl, int c) {
        super();
        this.sl = sl;
        this.c = c;
    }
    public BigDecimal getSl() {
        return sl;
    }
    public void setS(BigDecimal sl) {
        this.sl = sl;
    }
    public int getC() {
        return c;
    }
    public void setC(int c) {
        this.c = c;
    }
}
