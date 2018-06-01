package no.oms.maven.precommit.lib;

import java.io.File;

public interface CacheResolver {
  File resolve(CacheDescriptor cacheDescriptor);
}