package com.manamind.util.precommit.mojo;


import com.manamind.util.precommit.lib.BinaryInstaller;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Goal which touches a timestamp file.
 */
@Mojo(name = "download-binary", defaultPhase = LifecyclePhase.INITIALIZE)
public class DownloadBinaryMojo extends AbstractPrecommitMojo {

    /**
     * Where to download binary from. Defaults to https://github.com/pre-commit/pre-commit/archive/...
     */
    @Parameter(property = "downloadRoot", required = false, defaultValue = BinaryInstaller.DEFAULT_DOWNLOAD_ROOT)
    private String downloadRoot;

    /**
     * The version of the pre-commit binary to install. IMPORTANT! Most version names start with 'v', for example
     * 'v1.10.1'
     */
    @Parameter(property = "version", required = true)
    private String version;

    /**
     * Skips execution of this mojo.
     */
    @Parameter(property = "skip.installprecommit", alias = "skip.installprecommit", defaultValue = "${skip.installprecommit}")
    private boolean skip;

    public void execute() throws MojoExecutionException {
        BinaryInstaller
        /*File packageJson = new File(this.workingDirectory, "package.json");
        System.out.println("Fetching it");
        File f = outputDirectory;

        if ( !f.exists() ) {
            f.mkdirs();
        }

        File touch = new File( f, "touch.txt" );

        FileWriter w = null;
        try {
            w = new FileWriter( touch );

            w.write( "touch.txt" );
        } catch ( IOException e ) {
            throw new MojoExecutionException( "Error creating file " + touch, e );
        } finally {
            if ( w != null ) {
                try {
                    w.close();
                } catch ( IOException e ) {
                    // ignore
                }
            }
        }
        */
    }
}
