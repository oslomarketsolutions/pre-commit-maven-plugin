package no.oms.maven.precommit.lib;
	
import java.io.File;

public final class PluginFactory {
    private static final String DEFAULT_CACHE_PATH = "cache";

    private final File workingDirectory;
    private final File installDirectory;
    private final CacheResolver cacheResolver;

    public PluginFactory(File workingDirectory, File installDirectory) {
        this(workingDirectory, installDirectory, getDefaultCacheResolver(installDirectory));
    }

    public PluginFactory(File workingDirectory, File installDirectory, CacheResolver cacheResolver) {
        this.workingDirectory = workingDirectory;
        this.installDirectory = installDirectory;
        this.cacheResolver = cacheResolver;
    }


    public BinaryInstaller getBinaryInstaller() {
        return new BinaryInstaller(getInstallConfig(), new DefaultArchiveExtractor(), new DefaultFileDownloader(), new DefaultPythonHandle());
    }

    /*public YarnRunner getYarnRunner(ProxyConfig proxy, String npmRegistryURL) {
        return new DefaultYarnRunner(new InstallYarnExecutorConfig(getInstallConfig()), proxy, npmRegistryURL);
    }

    private NodeExecutorConfig getExecutorConfig() {
                                                 return new InstallNodeExecutorConfig(getInstallConfig());
                                                                                                          }
    */

    private InstallConfig getInstallConfig() {
        return new DefaultInstallConfig(installDirectory, workingDirectory, cacheResolver);
    }


    private static final CacheResolver getDefaultCacheResolver(File root) {
        return new DirectoryCacheResolver(new File(root, DEFAULT_CACHE_PATH));
    }
}