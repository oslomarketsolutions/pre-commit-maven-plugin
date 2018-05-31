package com.manamind.util.precommit.lib;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;

public class BinaryInstaller {
    public static final String INSTALL_PATH = "/pre-commit";

    public static final String DEFAULT_DOWNLOAD_ROOT =
            "https://github.com/yarnpkg/yarn/releases/download/";

    private String version, downloadRoot;

    private final Logger logger;

    private final InstallConfig config;

    private final ArchiveExtractor archiveExtractor;

    private final FileDownloader fileDownloader;

    private final PythonHandle pythonHandle;

    public BinaryInstaller(Logger logger, InstallConfig config, ArchiveExtractor archiveExtractor, FileDownloader fileDownloader, PythonHandle pythonHandle) {
        this.logger = logger;
        this.config = config;
        this.archiveExtractor = archiveExtractor;
        this.fileDownloader = fileDownloader;
        this.pythonHandle = pythonHandle;
    }

    /*
            python3 -m venv .pre-commit-env
            . .pre-commit-env/bin/activate
            tar -xzf pre-commit-1.10.1.tar.gz --strip-components 1
            python setup.py install
         */
    private void installBinary() throws InstallationException {
        try {
            logger.info("Installing pre-commit version {}", version);

            String downloadUrl = downloadRoot + version;
            String extension = "tar.gz";
            String fileEnding = "/" + version + "." + extension;
            downloadUrl += fileEnding;

            CacheDescriptor cacheDescriptor = new CacheDescriptor("pre-commit", version, extension);

            File archive = config.getCacheResolver().resolve(cacheDescriptor);

            downloadFileIfMissing(downloadUrl, archive);

            File installDirectory = getInstallDirectory();

            // We need to delete the existing yarn directory first so we clean out any old files, and
            // so we can rename the package directory below.
            try {
                if (installDirectory.isDirectory()) {
                    FileUtils.deleteDirectory(installDirectory);
                }
            } catch (IOException e) {
                logger.warn("Failed to delete existing Yarn installation.");
            }

            extractFile(archive, installDirectory);

            File setupFile = new File(installDirectory + "setup.py");

            if (!setupFile.exists()) {
                throw new InstallationException("Could not find setup.py in extracted archive");
            }

            VirtualEnv env = pythonHandle.setupVirtualEnv(installDirectory, "pre-commit-" + version);
            //pythonHandle.activateVirtualenv();
            pythonHandle.installIntoVirtualEnv(env, setupFile);

            //ensureCorrectYarnRootDirectory(installDirectory, yarnVersion);

            logger.info("Installed Yarn locally.");
        } catch (PythonException e) {
            throw new InstallationException("Could not download Yarn", e);
        } catch (ArchiveExtractionException e) {
            throw new InstallationException("Could not extract the Yarn archive", e);
        }
    }

    private File getInstallDirectory() {
        File installDirectory = new File(config.getInstallDirectory(), INSTALL_PATH);
        if (!installDirectory.exists()) {
            logger.debug("Creating install directory {}", installDirectory);
            installDirectory.mkdirs();
        }
        return installDirectory;
    }

    private void extractFile(File archive, File destinationDirectory) throws ArchiveExtractionException {
        logger.info("Unpacking {} into {}", archive, destinationDirectory);
        archiveExtractor.extract(archive.getPath(), destinationDirectory.getPath());
    }

    private void downloadFileIfMissing(String downloadUrl, File destination) throws PythonException {
        if (!destination.exists()) {
            downloadFile(downloadUrl, destination);
        }
    }

    private void downloadFile(String downloadUrl, File destination) throws PythonException {
        logger.info("Downloading {} to {}", downloadUrl, destination);
        fileDownloader.download(downloadUrl, destination.getPath());
    }
}
