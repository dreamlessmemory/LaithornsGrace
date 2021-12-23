package com.dreamless.laithorn.player;

import static org.junit.jupiter.api.Assertions.*;

class PlayerDataHandlerTest {
    private final int LevelCap = 10;
    private final int StackExp = 64*10;
    private final int LevelOneStacks = 2;
    private final int LevelTenStacks = 100;

    @org.junit.jupiter.api.Test
    void getNewEXPRequirement() {
        PlayerDataHandler.setLevelingConfiguration(LevelOneStacks, LevelTenStacks, LevelCap, StackExp);
        assertEquals(PlayerDataHandler.getNewEXPRequirement(1), LevelOneStacks*StackExp, "Experience required for level 1 should be two stacks at 10xp");
        assertEquals(PlayerDataHandler.getNewEXPRequirement(10), LevelTenStacks*StackExp, "Experience required for level 10 should be 100 stacks at 10xp");
    }
}