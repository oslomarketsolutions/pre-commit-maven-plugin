package no.oms.util.precommit.lib;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

final class PythonException extends Exception {
    public PythonException(String message){
        super(message);
    }
    PythonException(String message, Throwable cause) {
        super(message, cause);
    }
}


interface PythonHandle {
    public VirtualEnv setupVirtualEnv(File directory, String envName) throws PythonException;
    public void activateVirtualEnv(VirtualEnv env) throws PythonException;
    public void installIntoVirtualEnv(VirtualEnv env, File setupFile) throws PythonException;
}

final class VirtualEnv {
    File directory;
    String name;

    VirtualEnv(File directory, String name) {
        this.directory = new File(directory + "/." + name);
        this.name = name;
    }
}

final class DefaultPythonHandle implements PythonHandle {
    private static final Logger LOGGER = LoggerFactory.getLogger(PythonHandle.class);

    @Override
    public VirtualEnv setupVirtualEnv(File directory, String envName) throws PythonException {
        LOGGER.info("About to setup virtual env {}", envName);

        VirtualEnv env = new VirtualEnv(directory, envName);
        String[] command = new String[]{ "python", "-m", "venv", env.directory.getAbsolutePath() };

        try {
            Process child = Runtime.getRuntime().exec(command);
            int result = child.waitFor();
            if (result != 0) {
                throw new PythonException(
                        "Could not create virtual env " + env.directory.getAbsolutePath() + ". return code " + result
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
    public void activateVirtualEnv(VirtualEnv env) throws PythonException {
        if (!env.directory.exists()) {
            throw new PythonException("VirtualEnv " + env.name + " does not exist");
        }


    }

    @Override
    public void installIntoVirtualEnv(VirtualEnv env, File setupFile) throws PythonException {
        LOGGER.info("About to install binary into virtual env {}", env.name);

        String[] command = new String[]{
                env.directory.getAbsolutePath() + "/bin/python",
                setupFile.getAbsolutePath(),
                "install"
        };
        String[] environment = new String[]{ "VIRTUAL_ENV=" + env.directory.getAbsolutePath() };

        try {
            Process child = Runtime.getRuntime().exec(command, environment);
            int result = child.waitFor();
            if (result != 0) {
                throw new PythonException(
                        "Failed to install into virtual env " + env.name + ". return code " + result
                );
            }
        } catch (IOException e) {
            throw new PythonException("Failed to execute python", e);
        } catch (InterruptedException e) {
            throw new PythonException("Unexpected interruption of while waiting for python virtualenv process", e);
        }

        LOGGER.info("Successfully installed into {}", env.name);
    }
}