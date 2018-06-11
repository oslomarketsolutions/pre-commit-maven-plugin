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
    void installGitHooks(VirtualEnvDescriptor env, HookType[] hookTypes) throws PythonException;
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

        String[] command = new String[]{ getPython3Executable(), "-m", "venv", env.directory.getAbsolutePath() };
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

            // Write messages to output
            BackgroundStreamLogger errorGobbler = new BackgroundStreamLogger(child.getErrorStream(), "ERROR");
            BackgroundStreamLogger outputGobbler = new BackgroundStreamLogger(child.getInputStream(), "DEBUG");
            errorGobbler.start();
            outputGobbler.start();

            int result = child.waitFor();

            if (result != 0) {
                throw new PythonException("Failed to install into virtual env " + env.name + ". return code " + result);
            }
        } catch (IOException e) {
            throw new PythonException("Failed to execute python", e);
        } catch (InterruptedException e) {
            throw new PythonException("Unexpected interruption of while waiting for python virtualenv process", e);
        }

        LOGGER.info("Successfully installed into {}", env.name);
    }

    @Override
    public void installGitHooks(VirtualEnvDescriptor env, HookType[] hookTypes) throws PythonException {
        LOGGER.info("About to install commit hooks into virtual env {}", env.name);

        if (!env.directory.exists()) {
            throw new PythonException("Virtual env " + env.name + " does not exist");
        }

        if (hookTypes == null || hookTypes.length == 0) {
            throw new PythonException("Providing the hook types to install are required");
        }

        // There is seemingly no way to install all hooks at once
        // Thus we run pre-commit as many times as necessary
        for (HookType type : hookTypes) {
            String[] command = new String[]{
                    env.directory.getAbsolutePath() + "/bin/pre-commit",
                    "install",
                    "--install-hooks",
                    "--overwrite",
                    "--hook-type",
                    type.getValue()
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

                // Write messages to output
                BackgroundStreamLogger errorGobbler = new BackgroundStreamLogger(child.getErrorStream(), "ERROR");
                BackgroundStreamLogger outputGobbler = new BackgroundStreamLogger(child.getInputStream(), "INFO");
                errorGobbler.start();
                outputGobbler.start();

                int result = child.waitFor();

                if (result != 0) {
                    throw new PythonException("Failed to install git hooks. return code " + result);
                }
            } catch (IOException e) {
                throw new PythonException("Failed to execute python", e);
            } catch (InterruptedException e) {
                throw new PythonException("Unexpected interruption of while waiting for the pre-commit binary", e);
            }
        }

        LOGGER.info("Successfully installed Git commit hooks");
    }

    private String getPython3Executable() throws PythonException {
        Runtime runtime = Runtime.getRuntime();

        try {
            Process proc = runtime.exec(new String[]{"python3", "--version"});
            String output = IOUtils.toString(proc.getInputStream());

            if (proc.waitFor() == 0 && checkVersion(output)) {
                return "python3";
            }

            proc = runtime.exec(new String[]{"python", "--version"});
            output = IOUtils.toString(proc.getInputStream());

            if (proc.waitFor() == 0 && checkVersion(output)) {
                return "python";
            }
        } catch (InterruptedException | IOException exception) {
            throw new PythonException("Exception when fetching python version from binary: ", exception);
        }

        throw new PythonException(
                "Could not find a compatible python 3 version on your system. 3.3 is the minimum supported python version"
        );
    }

    private boolean checkVersion(String pythonOutput) throws PythonException {
        try {
            String versionString = pythonOutput.split(" ")[1];
            int majorVersion = Integer.parseInt(versionString.split("\\.")[0]);
            int minorVersion = Integer.parseInt(versionString.split("\\.")[1]);

            return majorVersion >= 3 && minorVersion >= 3;
        } catch (Exception exception) {
            throw new PythonException("Unexpected python version output: " + pythonOutput, exception);
        }
    }
}