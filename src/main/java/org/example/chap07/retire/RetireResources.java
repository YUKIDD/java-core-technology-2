package org.example.chap07.retire;

import java.awt.*;
import java.util.ListResourceBundle;

public class RetireResources extends ListResourceBundle {

    private static final Object[][] contents = {
            { "colorPre", Color.blue}, {"colorGain",Color.white},{"colorLoss",Color.red}
    };
    @Override
    protected Object[][] getContents() {
        return contents;
    }
}
