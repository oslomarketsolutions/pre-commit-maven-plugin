package no.oms.maven.precommit.mojo;


import no.oms.maven.precommit.lib.BinaryInstaller;
import no.oms.maven.precommit.lib.InstallationException;
import no.oms.maven.precommit.lib.PluginFactory;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Goal which downloads and prepares a pre-commit binary for use later
 */
@Mojo(name = "download-binary", defaultPhase = LifecyclePhase.INITIALIZE)
public class DownloadBinaryMojo extends AbstractPrecommitMojo {

    /**
     * Where to download binary from. Defaults to https://github.com/pre-commit/pre-commit/archive/...
     */
    @Parameter(property = "downloadRoot", required = false, defaultValue = BinaryInstaller.DEFAULT_DOWNLOAD_ROOT)
    private String downloadRoot;

    /**
     * The precommitVersion of the pre-commit binary to install. IMPORTANT! Most precommitVersion names start with 'v', for example
     * 'v1.10.1'
     */
    @Parameter(property = "precommitVersion", required = true)
    private String precommitVersion;

    /**
     * Skips execution of this mojo.
     */
    @Parameter(property = "skip.downloadprecommit", alias = "skip.downloadprecommit", defaultValue = "${skip.downloadprecommit}")
    private boolean skip;

    @Override
    public void execute(PluginFactory pluginFactory) throws MojoExecutionException {
        try {
            pluginFactory.getBinaryInstaller()
                    .setDownloadRoot(downloadRoot)
                    .setVersion(precommitVersion)
                    .installBinary();
        } catch (InstallationException e) {
            throw new MojoExecutionException("Failed to install binary", e);
        }
    }

    @Override
    protected boolean skipExecution() {
        return skip;
    }
}
