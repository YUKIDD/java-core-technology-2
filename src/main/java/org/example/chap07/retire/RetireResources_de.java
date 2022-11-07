package org.example.chap07.retire;

import java.awt.*;
import java.util.ListResourceBundle;

public class RetireResources_de extends ListResourceBundle {
    private static final Object[][] contents = {
            {"colorPre", Color.yellow},{"colorGain",Color.black},{"colorLoss",Color.red}
    };

    @Override
    protected Object[][] getContents() {
        return contents;
    }
}
