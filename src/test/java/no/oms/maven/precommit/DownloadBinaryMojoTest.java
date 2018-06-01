package no.oms.maven.precommit;


import no.oms.maven.precommit.mojo.DownloadBinaryMojo;
import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.plugin.testing.WithoutMojo;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class DownloadBinaryMojoTest {

    @Rule
    public MojoRule rule = new MojoRule() {

        @Override
        protected void before() throws Throwable {
        }

        @Override
        protected void after() {
        }
    };

    /**
     * @throws Exception if any
     */
    @Ignore
    @Test
    public void testSomething() throws Exception {
        File pom = new File( "target/test-classes/project-to-test/" );
        assertNotNull( pom );
        assertTrue( pom.exists() );

        DownloadBinaryMojo downloadBinaryMojo = (DownloadBinaryMojo) rule.lookupConfiguredMojo(pom, "download-binary");
        assertNotNull(downloadBinaryMojo);
        downloadBinaryMojo.execute();

        File outputDirectory = (File) rule.getVariableValueFromObject(downloadBinaryMojo, "installDirectory" );
        assertNotNull(outputDirectory);
        assertTrue(outputDirectory.exists());

        File touch = new File(outputDirectory, "touch.txt");
        assertTrue( touch.exists() );

    }

    /** Do not need the MojoRule. */
    @WithoutMojo
    @Ignore
    @Test
    public void testSomethingWhichDoesNotNeedTheMojoAndProbablyShouldBeExtractedIntoANewClassOfItsOwn() {
        assertTrue( true );
    }

}

