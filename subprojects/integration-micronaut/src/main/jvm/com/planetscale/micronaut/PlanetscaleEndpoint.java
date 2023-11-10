package com.planetscale.micronaut;

/**
 * TBD.
 *
 * @param endpoint
 */
public record PlanetscaleEndpoint(String endpoint) {
    /**
     * TBD.
     *
     * @param token
     * @return
     */
    public static PlanetscaleEndpoint of(String token) {
        return new PlanetscaleEndpoint(token);
    }
}
