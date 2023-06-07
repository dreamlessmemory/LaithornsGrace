package com.dreamless.laithorn.api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FragmentRarityTest {

    @Test
    void weightedDropChance() {
        FragmentRarity.initializeWeightsMap();

        assertEquals(100, FragmentRarity.DULL.weightedDropChance(0), "Dull should drop at 100% at level 0");
        assertEquals(0, FragmentRarity.DULL.weightedDropChance(6), "Dull should stop dropping at level 6");
        assertEquals(30, FragmentRarity.FAINT.weightedDropChance(3), "Faint peaks at level 3");
        assertEquals(30, FragmentRarity.PALE.weightedDropChance(4), "Pale peaks at level 4");

        assertEquals(10, FragmentRarity.GLOWING.weightedDropChance(3), "Glowing scale curve");
        assertEquals(20, FragmentRarity.GLOWING.weightedDropChance(4), "Glowing scale curve");
        assertEquals(30, FragmentRarity.GLOWING.weightedDropChance(5), "Glowing peaks at level 5");
        assertEquals(20, FragmentRarity.GLOWING.weightedDropChance(6), "Glowing scale curve");
        assertEquals(10, FragmentRarity.GLOWING.weightedDropChance(7), "Glowing scale curve");
        assertEquals(10, FragmentRarity.GLOWING.weightedDropChance(8), "Glowing scale curve");

        assertEquals(30, FragmentRarity.SPARKLING.weightedDropChance(6), "Sparkling peaks at level 6");
        assertEquals(30, FragmentRarity.BRIGHT.weightedDropChance(7), "Bright peaks at level 7");
        assertEquals(30, FragmentRarity.SHINING.weightedDropChance(8), "Shining peaks at level 8");
        assertEquals(30, FragmentRarity.RADIANT.weightedDropChance(9), "Radiant peaks at level 9");
        assertEquals(60, FragmentRarity.INCANDESCENT.weightedDropChance(10), "Incandescent peaks at level 10");
    }
}