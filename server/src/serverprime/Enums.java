/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverprime;

enum BarrierType {
    EMPTY(0),
    BLUE_SNAKE(1),
    RED_SNAKE(2),
    GREEN_SNAKE(3),
    YELLOW_SNAKE (4),
    WALL(9);
    public final int value;

    /*  initializing Enum Types by value in brackets            */
    BarrierType(int value) {
        this.value = value;
    }
}