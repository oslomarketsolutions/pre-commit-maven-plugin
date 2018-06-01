package no.oms.util.precommit.mojo;


import no.oms.util.precommit.lib.BinaryInstaller;
import no.oms.util.precommit.lib.InstallationException;
import no.oms.util.precommit.lib.PluginFactory;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Goal which touches a timestamp file.
 */
@Mojo(name = "download-binary", defaultPhase = LifecyclePhase.INITIALIZE)
public class DownloadBinaryMojo extends AbstractPrecommitMojo {

    /**
     * Where to download binary from. Defaults to https://github.com/pre-commit/pre-commit/archive/...
     */
    @Parameter(property = "downloadRoot", required = false, defaultValue = BinaryInstaller.DEFAULT_DOWNLOAD_ROOT)
    private String downloadRoot;

    /**
     * The version of the pre-commit binary to install. IMPORTANT! Most version names start with 'v', for example
     * 'v1.10.1'
     */
    @Parameter(property = "version", required = true)
    private String version;

    /**
     * Skips execution of this mojo.
     */
    @Parameter(property = "skip.installprecommit", alias = "skip.installprecommit", defaultValue = "${skip.installprecommit}")
    private boolean skip;

    @Override
    public void execute(PluginFactory pluginFactory) throws MojoExecutionException {
        try {
            pluginFactory.getBinaryInstaller().installBinary();
        } catch (InstallationException e) {
            throw new MojoExecutionException("Failed to install binary", e);
        }
    }

    @Override
    protected boolean skipExecution() {
        return skip;
    }
}
