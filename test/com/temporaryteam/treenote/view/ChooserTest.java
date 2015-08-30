package com.temporaryteam.treenote.view;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Naik
 */
public class ChooserTest extends Chooser {

    @Test
    public void testMatchExt() {
        assertTrue(matchExt(new File("/path/name.zip"), Chooser.ALL));
        assertTrue(matchExt(new File("/path/name.zip"), Chooser.ENC_ZIP));
        assertTrue(matchExt(new File("/path/name.zip"), Chooser.ZIP));
        assertFalse(matchExt(new File("/path/name.zip"), Chooser.JSON));
    }

}
