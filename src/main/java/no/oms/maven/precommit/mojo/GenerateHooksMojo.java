package no.oms.maven.precommit.mojo;


import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

/**
 * Goal which touches a timestamp file.
 */
@Mojo(name = "generate-hooks", defaultPhase = LifecyclePhase.INITIALIZE)
public class GenerateHooksMojo extends AbstractMojo {
    /**
     * Location of the file.
     */
    @Parameter( defaultValue = "${project.build.directory}", property = "outputDir", required = true )
    private File outputDirectory;

    public void execute() throws MojoExecutionException {
        /*File packageJson = new File(workingDirectory, "package.json");
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
