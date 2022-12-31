/*
  ------------------------------------------------------------------------------
        (c) by data experts gmbh
              Postfach 1130
              Woldegker Str. 12
              17001 Neubrandenburg

  Dieses Dokument und die hierin enthaltenen Informationen unterliegen
  dem Urheberrecht und duerfen ohne die schriftliche Genehmigung des
  Herausgebers weder als ganzes noch in Teilen dupliziert oder reproduziert
  noch manipuliert werden.
*/

package onassis.dto;

import java.io.Serializable;

public class Constant implements Serializable {
    public long getValue() {
        return value;
    }
    public void setValue(long value) {
        this.value = value;
    }
    public String getLabel() {
        return label;
    }
    public void setLabel(String label) {
        this.label = label;
    }
    public String getColor() {
        return color;
    }
    public void setColor(String color) {
        this.color = color;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public boolean isValid() {
        return valid;
    }
    public void setValid(boolean valid) {
        this.valid = valid;
    }
    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    private static final long serialVersionUID = 1L;
    long value;
    String label;
    String color;
    String icon;
    boolean valid;
    public Constant(long value, String label, String color, boolean valid) {
        super();
        this.value = value;
        this.label = label;
        this.color = color;
        this.valid = valid;
    }

    public Constant(long value, String label, String color, boolean valid, String icon) {
        super();
        this.value = value;
        this.label = label;
        this.color = color;
        this.valid = valid;
        this.icon = icon;
    }
}
