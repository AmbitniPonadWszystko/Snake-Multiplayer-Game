package serverprime;

enum BarrierType {
    EMPTY(0),
    SNAKE(1),
    WALL(2);
    public final int value;

    /*  initializing Enum Types by value in brackets            */
    BarrierType(int value) {
        this.value = value;
    }
}
