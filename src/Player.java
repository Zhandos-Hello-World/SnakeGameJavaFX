public interface Player {
    void setSnake(int width, int height);
    void setMap(Map map);
    void moveRight();
    void moveLeft();
    void moveDown();
    void moveUp();
    Position getPosition();
}
