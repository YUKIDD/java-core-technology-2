package org.example.chap07.retire;

import java.awt.*;
import java.util.ListResourceBundle;

public class RetireResources_zh extends ListResourceBundle {
    private static final Object[][] contents = {
            {"colorPre", Color.red},{"colorGain",Color.blue},{"colorLoss",Color.yellow}
    };

    @Override
    protected Object[][] getContents() {
        return contents;
    }
}
