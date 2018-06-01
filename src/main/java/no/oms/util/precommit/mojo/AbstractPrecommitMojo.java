package no.oms.util.precommit.mojo;

import no.oms.util.precommit.lib.PluginFactory;
import no.oms.util.precommit.lib.RepositoryCacheResolver;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.eclipse.aether.RepositorySystemSession;

import java.io.File;

public abstract class AbstractPrecommitMojo extends AbstractMojo {
    /**
     * The base directory for running all Node commands. (Usually the directory that contains package.json)
     */
    @Parameter(defaultValue = "${basedir}", property = "workingDirectory")
    protected File workingDirectory;

    /**
     * The base directory for installing the binary into
     */
    @Parameter(defaultValue = "${basedir}/precommit", property = "installDirectory")
    protected File installDirectory;

    @Parameter(defaultValue = "${repositorySystemSession}", readonly = true)
    private RepositorySystemSession repositorySystemSession;

    /**
     * Implemented by children to define an execution
     */
    protected abstract void execute(PluginFactory pluginFactory) throws MojoExecutionException;

    /**
     * Implemented by children to determine if this execution should be skipped.
     */
    protected abstract boolean skipExecution();

    @Override
    public void execute() throws MojoExecutionException {
        if (!skipExecution()) {
            if (installDirectory == null) {
                installDirectory = workingDirectory;
            }

            execute(new PluginFactory(workingDirectory, installDirectory,
                    new RepositoryCacheResolver(repositorySystemSession)));
        } else {
            getLog().info("Skipping execution.");
        }
    }
}
