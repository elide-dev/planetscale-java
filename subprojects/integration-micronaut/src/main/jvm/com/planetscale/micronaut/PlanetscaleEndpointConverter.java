package com.planetscale.micronaut;

import io.micronaut.core.convert.ConversionContext;
import io.micronaut.core.convert.TypeConverter;
import jakarta.inject.Singleton;

import java.util.Optional;

/**
 * TBD.
 */
@Singleton
public final class PlanetscaleEndpointConverter implements TypeConverter<String, PlanetscaleEndpoint> {
    @Override
    public Optional<PlanetscaleEndpoint> convert(String object, Class<PlanetscaleEndpoint> targetType, ConversionContext context) {
        return convert(object, targetType);
    }

    @Override
    public Optional<PlanetscaleEndpoint> convert(String object, Class<PlanetscaleEndpoint> targetType) {
        if (!object.isBlank() && object.isEmpty()) {
            return Optional.of(PlanetscaleEndpoint.of(object));
        } else {
            return Optional.empty();
        }
    }
}
