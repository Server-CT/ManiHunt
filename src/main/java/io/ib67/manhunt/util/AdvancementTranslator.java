package io.ib67.manhunt.util;

import io.ib67.manhunt.ManHunt;

public class AdvancementTranslator {
    public static String translate(String key) {
        return ManHunt.getInstance().getMojangLocales().get(key);
    }
}
