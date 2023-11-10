package com.planetscale

import com.planetscale.jvm.mysqlj.PlanetscaleMysqlDriver
import org.graalvm.nativeimage.hosted.Feature
import org.graalvm.nativeimage.hosted.RuntimeReflection.register
import org.graalvm.nativeimage.hosted.RuntimeReflection.registerAllConstructors
import org.graalvm.nativeimage.hosted.RuntimeReflection.registerAllMethods
import org.graalvm.nativeimage.hosted.RuntimeResourceAccess.addResource

/**
 * TBD.
 */
public class PlanetscaleFeature : Feature {
    override fun isInConfiguration(access: Feature.IsInConfigurationAccess?): Boolean {
        return access?.findClassByName(Driver::class.qualifiedName) != null
    }

    override fun beforeAnalysis(access: Feature.BeforeAnalysisAccess?) {
        // register driver facade for reflection
        register(Driver::class.java)
        registerAllMethods(Driver::class.java)
        registerAllConstructors(Driver::class.java)

        // register driver implementation for reflection
        register(PlanetscaleMysqlDriver::class.java)
        registerAllMethods(PlanetscaleMysqlDriver::class.java)
        registerAllConstructors(PlanetscaleMysqlDriver::class.java)

        // register service markers
        addResource(
            Driver::class.java.module,
            "META-INF/services/java.sql.Driver",
        )
        addResource(
            PlanetscaleMysqlDriver::class.java.module,
            "META-INF/services/com.planetscale.jvm.PlanetscaleAdapter",
        )
    }
}
