package org.zeroBzeroT.antiillegals.helpers.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class AreaCacheHelper {

    private AreaCacheHelper() {

    }

    private static final int GRID_SIZE = 4096; // this must be larger than 2000 for area codes to be properly calculated

    /**
     * the plugin caches reverted items based on their location in the world, to avoid complications with
     * hashcode collisions. players will not be able to remotely steal items from other players by having the item
     * overriden from another source, because the cache does not work on a global level. with this solution, possible
     * crash / lag exploits are still handled using a cache.
     */
    @NotNull
    private static final Cache<Integer, Cache<Integer, CachedState>> REVERTION_CACHES = CacheBuilder.newBuilder()
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .build();

    /**
     * gets the designated area cache that corresponds to the given location, or creates a new one if none was found
     * @param location the location to calculate the area code for
     * @return the existing or newly created cache
     */
    @NotNull
    public static Cache<Integer, CachedState> getOrCreateAreaCache(@NotNull final Location location) {
        final int areaCode = areaCode(location);

        try {
            return REVERTION_CACHES.get(areaCode, () -> CacheBuilder.newBuilder()
                    .expireAfterAccess(1, TimeUnit.MINUTES)
                    .build());
        } catch (final ExecutionException e) {
            // exception should never be thrown, because RevertionCache::new never throws any exception
            // still handled anyway however, just in case
            throw new RuntimeException(e);
        }
    }

    /**
     * this method is used to generate an area-based cache, avoiding complications with hashcode collisions
     * @param worldX the x coordinate of the location
     * @param worldZ the z coordinate of the location
     * @return the area code, using both the x and z coordinates to generate one unique value
     */
    public static int areaCode(final int worldX, final int worldZ) {
        final short gridX = (short) Math.floor(worldX / (double) GRID_SIZE);
        final short gridZ = (short) Math.floor(worldZ / (double) GRID_SIZE);

        return (gridX << 16) | (gridZ & 0xFFFF);
    }

    /**
     * this method is used to generate an area-based cache, avoiding complications with hashcode collisions
     * @param location the location to use for calculating the area code
     * @return the area code, using both the x and z coordinates of the location to generate one unique value
     */
    public static int areaCode(@NotNull final Location location) {
        return areaCode(location.getBlockX(), location.getBlockZ());
    }

}
