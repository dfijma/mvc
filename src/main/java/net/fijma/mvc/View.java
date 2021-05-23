package net.fijma.mvc;

public abstract class View<M> {

    protected final M model;

    public abstract void draw();
    public abstract boolean key(int k);

    protected View(M model) {
        this.model = model;
    }

    // http://www.lihaoyi.com/post/BuildyourownCommandLinewithANSIescapecodes.html
    protected void clear() {
        System.out.print("\u001b[2J");
        setRC(0,0);
    }

    // https://stackoverflow.com/questions/4842424/list-of-ansi-color-escape-sequences
    public static final int BLACK = 0;
    public static final int RED = 1;
    public static final int GREEN = 2;

    protected String green() {
        return "\u001b[32m";
    }

    protected String red() {
        return "\u001b[31m";
    }

    protected String reset() {
        return "\u001b[0m";
    }

    protected void setRC(int row, int col) {
        System.out.print("\u001b[" + row + ";" + col + "H");
    }

}
