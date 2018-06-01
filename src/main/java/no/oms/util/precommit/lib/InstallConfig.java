package no.oms.util.precommit.lib;

import java.io.File;

public interface InstallConfig {
  File getInstallDirectory();
  File getWorkingDirectory();
  CacheResolver getCacheResolver();
}

final class DefaultInstallConfig implements InstallConfig {
  private final File installDirectory;
  private final File workingDirectory;
  private final CacheResolver cacheResolver;

  public DefaultInstallConfig(File installDirectory,
                              File workingDirectory,
                              CacheResolver cacheResolver) {
    this.installDirectory = installDirectory;
    this.workingDirectory = workingDirectory;
    this.cacheResolver = cacheResolver;
  }


  @Override
  public File getInstallDirectory() {
                                  return this.installDirectory;
                                                               }


  @Override
  public File getWorkingDirectory() {
                                  return this.workingDirectory;
                                                               }

  public CacheResolver getCacheResolver() {
                                        return cacheResolver;
                                                             }

}