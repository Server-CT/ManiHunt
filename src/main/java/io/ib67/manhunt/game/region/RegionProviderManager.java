package io.ib67.manhunt.game.region;

import java.util.HashMap;
import java.util.Map;

public class RegionProviderManager {
    private final Map<String, RegionProvider<?>> regionProviders = new HashMap<>();

    public boolean registerRegionProvider(String name, RegionProvider<?> provider) {
        if (regionProviders.containsKey(name.toLowerCase())) {
            return false;
        }
        regionProviders.put(name, provider);
        return true;
    }

    public RegionProvider<?> getProvider(String name) {
        return regionProviders.get(name);
    }
}
