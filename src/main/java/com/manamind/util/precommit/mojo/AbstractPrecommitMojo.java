package com.manamind.util.precommit.mojo;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;

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
}
