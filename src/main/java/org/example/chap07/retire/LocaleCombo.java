package org.example.chap07.retire;

import javax.swing.*;
import javax.swing.event.ListDataListener;
import java.text.Collator;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class LocaleCombo extends JComboBox<Locale> {
    private int selected;
    private Locale[] locales;
    private ListCellRenderer<Locale> renderer;

    public LocaleCombo(Locale[] locales) {
        this.locales = (Locale[]) locales.clone();
        sort();
        setSelectedItem(getLocale());
    }

    public void setLocale(Locale newValue) {
        super.setLocale(newValue);
        sort();
    }

    private void sort() {
        Locale loc = getLocale();
        Collator collator = Collator.getInstance(loc);
        Comparator<Locale> comp = (a,b) -> collator.compare(a.getDisplayName(loc),b.getDisplayName(loc));
        Arrays.sort(locales,comp);
        setModel(new ComboBoxModel<Locale>() {
            @Override
            public void setSelectedItem(Object anItem) {
                if (anItem == null) {
                    selected = -1;
                } else {
                    selected = Arrays.binarySearch(locales,(Locale) anItem,comp);
                }
            }

            @Override
            public Object getSelectedItem() {
                return selected >= 0 ? locales[selected] : null;
            }

            @Override
            public int getSize() {
                return locales.length;
            }

            @Override
            public Locale getElementAt(int index) {
                return locales[index];
            }

            @Override
            public void addListDataListener(ListDataListener l) {

            }

            @Override
            public void removeListDataListener(ListDataListener l) {

            }
        });
        setSelectedItem(selected);
    }

    public ListCellRenderer<Locale> getRender() {
        if (renderer == null) {
            @SuppressWarnings("unchecked")
            ListCellRenderer<Object> originalRenderer = (ListCellRenderer<Object>) super.getRenderer();
            if (originalRenderer == null) {
                return null;
            }
            renderer = (list,value,index,isSelected,cellHasFocus) ->
                    originalRenderer.getListCellRendererComponent(list,
                            value.getDisplayName(getLocale()),index,isSelected,cellHasFocus);
        }
        return renderer;
    }

    public void setRenderer(ListCellRenderer<? super Locale> newValue) {
        renderer = null;
        super.setRenderer(newValue);
    }
}
