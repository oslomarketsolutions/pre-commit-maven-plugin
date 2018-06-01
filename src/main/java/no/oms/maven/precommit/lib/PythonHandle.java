package no.oms.maven.precommit.lib;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

final class PythonException extends Exception {
    PythonException(String message){
        super(message);
    }
    PythonException(String message, Throwable cause) {
        super(message, cause);
    }
}


interface PythonHandle {
    VirtualEnvDescriptor setupVirtualEnv(File directory, String envName) throws PythonException;
    void installIntoVirtualEnv(VirtualEnvDescriptor env, File setupFile) throws PythonException;
    void installGitHooks(VirtualEnvDescriptor env) throws PythonException;
}

final class VirtualEnvDescriptor {
    File directory;
    String name;

    VirtualEnvDescriptor(File directory, String name) {
        this.directory = new File(directory + "/." + name + "-virtualenv");
        this.name = name;
    }
}

final class DefaultPythonHandle implements PythonHandle {
    private static final Logger LOGGER = LoggerFactory.getLogger(PythonHandle.class);

    @Override
    public VirtualEnvDescriptor setupVirtualEnv(File directory, String envName) throws PythonException {
        LOGGER.info("About to setup virtual env {}", envName);

        VirtualEnvDescriptor env = new VirtualEnvDescriptor(directory, envName);

        if (env.directory.exists()) {
            LOGGER.info("Virtual env already exists, skipping");
            return env;
        }

        String[] command = new String[]{ "python", "-m", "venv", env.directory.getAbsolutePath() };
        LOGGER.debug("Running {}", (Object) command);

        try {
            Process child = Runtime.getRuntime().exec(command);
            int result = child.waitFor();
            String stdout = IOUtils.toString(child.getInputStream());

            if (result != 0) {
                throw new PythonException(
                        "Could not create virtual env " + env.directory.getAbsolutePath() + ". return code " + result +
                                "\nPython said: " + stdout
                );
            }
        } catch (IOException e) {
            throw new PythonException("Failed to execute python", e);
        } catch (InterruptedException e) {
            throw new PythonException("Unexpected interruption of while waiting for python virtualenv process", e);
        }

        return env;
    }

    @Override
    public void installIntoVirtualEnv(VirtualEnvDescriptor env, File setupFile) throws PythonException {
        LOGGER.info("About to install binary into virtual env {}", env.name);

        if (!env.directory.exists()) {
            throw new PythonException("Virtual env " + env.name + " does not exist");
        }

        String[] command = new String[]{
                env.directory.getAbsolutePath() + "/bin/python",
                setupFile.getAbsolutePath(),
                "install"
        };
        String[] environment = new String[]{ "VIRTUAL_ENV=" + env.directory.getAbsolutePath() };
        LOGGER.debug("Running {} {} in {}", environment, command, setupFile.getParentFile());

        try {
            Process child = Runtime.getRuntime().exec(command, environment, setupFile.getParentFile());
            int result = child.waitFor();
            String stdout = IOUtils.toString(child.getInputStream());

            if (result != 0) {
                throw new PythonException(
                        "Failed to install into virtual env " + env.name + ". return code " + result +
                                "\nPython said: " + stdout
                );
            }
        } catch (IOException e) {
            throw new PythonException("Failed to execute python", e);
        } catch (InterruptedException e) {
            throw new PythonException("Unexpected interruption of while waiting for python virtualenv process", e);
        }

        LOGGER.info("Successfully installed into {}", env.name);
    }

    @Override
    public void installGitHooks(VirtualEnvDescriptor env) throws PythonException {
        LOGGER.info("About to install commit hooks into virtual env {}", env.name);

        if (!env.directory.exists()) {
            throw new PythonException("Virtual env " + env.name + " does not exist");
        }

        String[] command = new String[]{
                env.directory.getAbsolutePath() + "/bin/pre-commit",
                "install",
                "--install-hooks"
        };
        String[] environment = new String[]{
                "VIRTUAL_ENV=" + env.directory.getAbsolutePath(),
                // PATH is not inherited when we explicitly set environment.
                // Set it to retain access to the git binary
                "PATH=" + System.getenv("PATH")
        };
        LOGGER.debug("Running {} {}", environment, command);

        try {
            Process child = Runtime.getRuntime().exec(command, environment);
            int result = child.waitFor();
            String stdout = IOUtils.toString(child.getInputStream());

            if (result != 0) {
                throw new PythonException(
                        "Failed to install git hooks. return code " + result + "\nPre-commit said: " + stdout
                );
            }
        } catch (IOException e) {
            throw new PythonException("Failed to execute python", e);
        } catch (InterruptedException e) {
            throw new PythonException("Unexpected interruption of while waiting for the pre-commit binary", e);
        }

        LOGGER.info("Successfully installed Git commit hooks");
    }
}