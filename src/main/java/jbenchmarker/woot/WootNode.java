package jbenchmarker.woot;

/**
 *
 * @author urso
 */
public abstract class WootNode { 
    
    final private WootIdentifier id; // own identifier
    final private char content;
    private boolean visible;

    public WootNode(WootIdentifier id, char content, boolean visible) {
        this.id = id;
        this.content = content;
        this.visible = visible;
    }

    public WootIdentifier getId() {
        return id;
    }

    public char getContent() {
        return content;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
