package no.oms.maven.precommit.mojo;


import no.oms.maven.precommit.lib.InstallationException;
import no.oms.maven.precommit.lib.PluginFactory;
import org.apache.maven.plugin.MojoExecutionException;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

/**
 * Goal which runs an installed pre-commit binary to activate the hooks
 */
@Mojo(name = "generate-hooks", defaultPhase = LifecyclePhase.INITIALIZE)
public class GenerateHooksMojo extends AbstractPrecommitMojo {
    /**
     * Location of the file.
     */
    @Parameter( defaultValue = "${project.build.directory}", property = "outputDir", required = true )
    private File outputDirectory;

    /**
     * Skips execution of this mojo.
     */
    @Parameter(property = "skip.installprecommit", alias = "skip.installprecommit", defaultValue = "${skip.installprecommit}")
    private boolean skip;

    @Override
    public void execute(PluginFactory pluginFactory) throws MojoExecutionException {
        try {
            pluginFactory.getBinaryRunner()
                    .installHooks();
        } catch (InstallationException e) {
            throw new MojoExecutionException("Failed to install git hooks", e);
        }
    }

    @Override
    protected boolean skipExecution() {
        return skip;
    }
}
