package edu.kit.informatik.connectfour.model.board;

class StandardBoard extends Board {

    @Override
    Position transform(Position pos) {
        return pos;
    }

    @Override
    int getBoundsOffset() {
        return 0;
    }
}
