package com.andgatech.gtnhitemdocexporter.export;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class NbtSummaryFormatterTest {

    @Test
    public void emptyWhenNbtIsMissing() {
        assertEquals("", NbtSummaryFormatter.summarize(null, true, 20));
        assertEquals("", NbtSummaryFormatter.summarize("", true, 20));
    }

    @Test
    public void emptyWhenDisabled() {
        assertEquals("", NbtSummaryFormatter.summarize("{foo:1}", false, 20));
    }

    @Test
    public void truncatesLongText() {
        assertEquals("1234567890...", NbtSummaryFormatter.summarize("123456789012345", true, 13));
    }
}
