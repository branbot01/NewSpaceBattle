package com.newspacebattle;

class EnemyAI {
    int team;

    Blackboard blackboard;

    EnemyAI(int team, Blackboard blackboard) {
        this.team = team;
        this.blackboard = blackboard;
        if (teamSize() == 0) {
            return;
        }

        new Thread(() -> {
            while (teamSize() > 0) {
                if (!GameScreen.paused) {
                    update();
                }
                Utilities.delay(16);
            }
        }).start();
    }

    void update() {

    }

    private int teamSize() {
        int teamSize = 0;
        for (int i = 0; i <= GameScreen.ships.size() - 1; i++) {
            if (GameScreen.ships.get(i).team == team) {
                teamSize++;
            }
        }
        return teamSize;
    }
}
