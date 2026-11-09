package net.bitbylogic.utils.hologram;

import net.bitbylogic.utils.hologram.line.HologramTextLine;
import org.bukkit.util.Transformation;
import org.joml.Vector3f;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HologramDefinitionTest {

    @Test
    void linesAreASnapshot() {
        Hologram hologram = new Hologram(HologramLine.of("one").id("first"));
        List<HologramLine<?, ?>> snapshot = hologram.getLines();
        hologram.addLine(HologramLine.of("two").id("second"));

        assertEquals(1, snapshot.size());
        assertEquals(2, hologram.getLines().size());
        assertThrows(UnsupportedOperationException.class, () -> snapshot.add(HologramLine.of("three")));
    }

    @Test
    void linesAreFoundByIdIgnoringCase() {
        Hologram hologram = new Hologram(HologramLine.of("one").id("First"));

        assertTrue(hologram.getLine("first").isPresent());
        assertFalse(hologram.getLine("missing").isPresent());
    }

    @Test
    void settingsAreStoredOnTheDefinition() {
        Hologram hologram = new Hologram(HologramLine.of("one"))
                .global(false)
                .persistent(true)
                .lineSpacing(0.5f);

        assertFalse(hologram.isGlobal());
        assertTrue(hologram.isPersistent());
        assertEquals(0.5f, hologram.getLineSpacing());
    }

    @Test
    void lineTransformationIsACopy() {
        HologramTextLine line = HologramLine.of("one").translation(new Vector3f(1, 2, 3));
        Transformation copy = line.getTransformation();
        copy.getTranslation().set(9, 9, 9);

        assertEquals(new Vector3f(1, 2, 3), line.getTransformation().getTranslation());
    }

    @Test
    void lineMetadataIsACopy() {
        HologramTextLine line = HologramLine.of("one").metadata("key", "value");
        Map<String, Object> metadata = line.getMetadata();

        assertEquals("value", line.getMetadata("key"));
        assertThrows(UnsupportedOperationException.class, () -> metadata.put("other", 1));
    }

    @Test
    void plainDataReplacesTheSupplier() {
        HologramTextLine line = HologramLine.of("one").data(() -> "supplied");
        assertEquals("supplied", line.getData().orElseThrow());

        line.data("plain");
        assertEquals("plain", line.getData().orElseThrow());
    }

    @Test
    void supplierIsReadOncePerLookup() {
        int[] calls = {0};
        HologramTextLine line = HologramLine.of("one").data(() -> {
            calls[0]++;
            return "value";
        });

        line.getData();

        assertEquals(1, calls[0]);
    }

}
