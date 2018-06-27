package jake.yang.permission.library.core;

@SuppressWarnings("unused")
public class Chain {
    private boolean mIsOpen;

    public void open() {
        this.mIsOpen = true;
    }

    public void close() {
        this.mIsOpen = false;
    }

    public boolean getState() {
        return this.mIsOpen;
    }
}
