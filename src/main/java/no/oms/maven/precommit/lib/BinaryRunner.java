package no.oms.maven.precommit.lib;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class BinaryRunner {
    public static final String INSTALL_PATH = "/pre-commit";
    private final Logger logger;
    private final InstallConfig config;
    private final PythonHandle pythonHandle;

    public BinaryRunner(InstallConfig config, PythonHandle pythonHandle) {
        logger = LoggerFactory.getLogger(getClass());
        this.config = config;
        this.pythonHandle = pythonHandle;
    }

    public void installHooks() throws InstallationException {
        try {
            logger.info("Installing git commit hooks");
            File installDirectory = getInstallDirectory();

            VirtualEnvDescriptor env = pythonHandle.setupVirtualEnv(installDirectory, "pre-commit");
            pythonHandle.installGitHooks(env);

            logger.info("Installed Git commit hooks");
        } catch (PythonException e) {
            throw new InstallationException("Python encountered an issue when installing the pre-commit binary", e);
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
}
