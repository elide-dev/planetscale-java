package com.planetscale

import org.graalvm.nativeimage.hosted.Feature

/**
 * TBD.
 */
public class PlanetscaleFeature : Feature {
    override fun isInConfiguration(access: Feature.IsInConfigurationAccess?): Boolean {
        return true
    }

    override fun beforeAnalysis(access: Feature.BeforeAnalysisAccess?) {
        // nothing yet
    }
}
