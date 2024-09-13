package com.example.pillanalyser.test;

import com.example.pillanalyser.Pill;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class PillTest {

    private Pill pill;

    @BeforeEach
    void setUp() {
        pill = new Pill("Test Pill", 1, Color.RED);
    }

    @Test
    void testGettersAndSetters() {
        assertEquals("Test Pill", pill.getName());
        assertEquals(1, pill.getRoot());
        assertEquals(Color.RED, pill.getColour());

        pill.setName("New Name");
        assertEquals("New Name", pill.getName());

        pill.setRoot(2);
        assertEquals(2, pill.getRoot());

        pill.setColour(Color.BLUE);
        assertEquals(Color.BLUE, pill.getColour());
    }

    @Test
    void testTwoColours() {
        assertFalse(pill.isTwoColours());

        pill.setTwoColours(true);
        assertTrue(pill.isTwoColours());
    }

    @Test
    void testIndexes() {
        ArrayList<Integer> indexes = new ArrayList<>();
        indexes.add(1);
        indexes.add(2);

        pill.setIndexes(indexes);
        assertEquals(indexes, pill.getIndexes());
    }

    @Test
    void testToString() {
        String expected = "Pill{name='Test Pill', root=1, indexes=[], colour=0x0000ffff, isTwoColours=false}";
        assertEquals(expected, pill.toString());
    }
}
