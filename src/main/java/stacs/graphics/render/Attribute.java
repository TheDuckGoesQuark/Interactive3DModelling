package stacs.graphics.render;

public enum Attribute {

    COORDINATES(0),
    COLOUR(1);

    private int index;

    Attribute(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
