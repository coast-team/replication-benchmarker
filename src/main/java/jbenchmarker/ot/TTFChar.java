package jbenchmarker.ot;

/**
 *
 * @author oster
 */
public class TTFChar {

    private char character;
    private boolean visible;

    public TTFChar(char c) {
        this.character = c;
        this.visible = true;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void hide() {
        this.visible = false;
    }

    public char getChar() {
        return this.character;
    }

    @Override
    public String toString() {
        if (this.visible) {
            return "" + this.character;
        } else {
            return "{" + this.character + "}";
        }
    }
}
