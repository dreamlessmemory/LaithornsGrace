package com.dreamless.laithorn.player;

import static org.junit.jupiter.api.Assertions.*;

class PlayerDataHandlerTest {
    private final int LevelCap = 10;
    private final int StackExp = 64 * 10;
    private final int LevelOneStacks = 2;
    private final int LevelTenStacks = 100;

    @org.junit.jupiter.api.Test
    void getNewEXPRequirement() {
        PlayerDataHandler.setLevelingConfiguration(LevelOneStacks, LevelTenStacks, LevelCap, StackExp);
        assertEquals(LevelOneStacks * StackExp, PlayerDataHandler.getNewEXPRequirement(0), "Experience required from level 0-1 should be two stacks at 10xp");
        assertEquals(LevelTenStacks * StackExp, PlayerDataHandler.getNewEXPRequirement(LevelCap - 1), "Experience required from level 9-10 should be 100 stacks at 10xp");
        assertEquals(Integer.MAX_VALUE, PlayerDataHandler.getNewEXPRequirement(LevelCap), "Experience required at max level should be 'infinite'");
        assertEquals(Integer.MAX_VALUE, PlayerDataHandler.getNewEXPRequirement(99), "Experience required at max level should be 'infinite'");
    }
}