package com.manamind.util.precommit;


import com.manamind.util.precommit.mojo.GenerateHooksMojo;
import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.plugin.testing.WithoutMojo;

import org.junit.Ignore;
import org.junit.Rule;
import static org.junit.Assert.*;
import org.junit.Test;
import java.io.File;

public class GenerateHooksMojoTest {

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

        GenerateHooksMojo generateHooksMojo = (GenerateHooksMojo) rule.lookupConfiguredMojo( pom, "touch" );
        assertNotNull(generateHooksMojo);
        generateHooksMojo.execute();

        File outputDirectory = ( File ) rule.getVariableValueFromObject(generateHooksMojo, "outputDirectory" );
        assertNotNull( outputDirectory );
        assertTrue( outputDirectory.exists() );

        File touch = new File( outputDirectory, "touch.txt" );
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

