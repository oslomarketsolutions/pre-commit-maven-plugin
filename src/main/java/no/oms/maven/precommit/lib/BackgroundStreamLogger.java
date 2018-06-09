package no.oms.maven.precommit.lib;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Runs a thread which reads from a thread until completion and writes output to a logger.
 */
class BackgroundStreamLogger extends Thread {
    private static final Logger LOGGER = LoggerFactory.getLogger(BackgroundStreamLogger.class);
    private InputStream is;
    private String type;

    BackgroundStreamLogger(InputStream is, String type) {
        this.is = is;
        this.type = type;
    }

    public void run() {
        try {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                switch (type.toUpperCase()) {
                    case "ERROR":
                        LOGGER.error(line);
                        break;
                    case "INFO":
                        LOGGER.info(line);
                        break;
                    case "DEBUG":
                        LOGGER.debug(line);
                        break;
                }
            }
        } catch (IOException ioe) {
            LOGGER.error("Reading process stream faled", ioe);
        }
    }
}
