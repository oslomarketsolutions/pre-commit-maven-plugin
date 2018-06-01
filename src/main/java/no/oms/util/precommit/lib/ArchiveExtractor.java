package no.oms.util.precommit.lib;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.AccessDeniedException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;
import org.codehaus.plexus.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ArchiveExtractionException extends Exception {
	ArchiveExtractionException(String message) {
		super(message);
	}

	ArchiveExtractionException(String message, Throwable cause) {
		super(message, cause);
	}
}

interface ArchiveExtractor {
	void extract(String archive, String destinationDirectory) throws ArchiveExtractionException;
}

final class DefaultArchiveExtractor implements ArchiveExtractor {
	private static final Logger LOG = LoggerFactory.getLogger(DefaultArchiveExtractor.class);

	private void prepDestination(File path, boolean directory) throws IOException {
		if (directory) {
			path.mkdirs();
		} else {
			if (!path.getParentFile().exists()) {
				path.getParentFile().mkdirs();
			}
			if (!path.getParentFile().canWrite()) {
				throw new AccessDeniedException(
						String.format("Could not get write permissions for '%s'", path.getParentFile().getAbsolutePath()));
			}
		}
	}


	@Override
	public void extract(String archive, String destinationDirectory) throws ArchiveExtractionException {
		final File archiveFile = new File(archive);

		try (FileInputStream fis = new FileInputStream(archiveFile)) {
			if ("zip".equals(FileUtils.getExtension(archiveFile.getAbsolutePath()))) {
				ZipFile zipFile = new ZipFile(archiveFile);
				try {
					Enumeration<? extends ZipEntry> entries = zipFile.entries();
					while (entries.hasMoreElements()) {
						ZipEntry entry = entries.nextElement();
						final File destPath = new File(destinationDirectory + File.separator + entry.getName());
						prepDestination(destPath, entry.isDirectory());
						if (!entry.isDirectory()){
							InputStream in = null;
							OutputStream out = null;
							try {
								in = zipFile.getInputStream(entry);
								out = new FileOutputStream(destPath);
								IOUtils.copy(in, out);
							} finally {
								IOUtils.closeQuietly(in);
								IOUtils.closeQuietly(out);
							}
						}
					}
				} finally {
					zipFile.close();
				}
			} else {
				// TarArchiveInputStream can be constructed with a normal FileInputStream if
				// we ever need to extract regular '.tar' files.
				TarArchiveInputStream tarIn = null;
				try {
					tarIn = new TarArchiveInputStream(new GzipCompressorInputStream(fis));


					TarArchiveEntry tarEntry = tarIn.getNextTarEntry();
					while (tarEntry != null) {
						// Create a file for this tarEntry
                        String name = strip(tarEntry.getName());
						final File destPath = new File(destinationDirectory + File.separator + name);

						prepDestination(destPath, tarEntry.isDirectory());
						if (!destPath.getCanonicalPath().startsWith(destinationDirectory)) {
							throw new IOException(
									"Expanding " + name + " would create file outside of " + destinationDirectory
							);
						}
						if (!tarEntry.isDirectory()) {
							destPath.createNewFile();
							boolean isExecutable = (tarEntry.getMode() & 0100) > 0;
							destPath.setExecutable(isExecutable);


							OutputStream out = null;
							try {
								out = new FileOutputStream(destPath);
								IOUtils.copy(tarIn, out);
							} finally {
								IOUtils.closeQuietly(out);
							}
						}
						tarEntry = tarIn.getNextTarEntry();
					}
				} finally {
					IOUtils.closeQuietly(tarIn);
				}
			}
		} catch (IOException e) {
			throw new ArchiveExtractionException("Could not extract archive: '"
					+ archive
					+ "'", e);
		}
	}

    private String strip(String input) {
        return input.replaceFirst("^[-\\w.]+/", "");
    }
}