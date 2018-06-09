# pre-commit-maven-plugin

> A maven plugin for [the pre-commit framework][precommit]

[pre-commit][precommit] is a framework for managing commit hooks in a
Git repository. It makes working with commit hooks easier, but it has an
issue. It requires that developers install a binary onto their system
and manually installing the the hooks into their repo by running
`pre-commit install --install-hooks`. We can do better than that!

When compiling a maven project, this plugin will install the git hooks
automatically in the developer's Git project. One less thing to
remember!

## Usage

First, follow the [setup instructions][setup] on pre-commit.com. A
`.pre-commit-config.yaml` file in the root of the project is required
for the plugin to work.

Add the following to your `pom.xml` under build plugins:

```xml
<plugin>
    <groupId>no.oms.maven</groupId>
    <artifactId>pre-commit-maven-plugin</artifactId>
    <version>LATEST_VERSION</version>
    <inherited>false</inherited>
    <configuration>
        <!-- The version of pre-commit you would like to use -->
        <version>v1.10.1</version>
    </configuration>
    <executions>
        <execution>
            <goals>
                <goal>download-binary</goal>
                <goal>generate-hooks</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

Remember to replace `LATEST_VERSION` with the version of the plugin you
would like to use, i.e. [the latest version][releases]. This will
register the plugin

### Other hook types

If you're using other types of hooks than just precommit hooks, then you need to
pass an argument specifying which hooks you would like the plugin to install.
This is done by setting the `hookTypes` argument:

```xml
<hookTypes>
    <hookType>preCommit</hookType>
    <hookType>prePush</hookType>
    <hookType>commitMsg</hookType>
</hookTypes>
```

### Options

> TODO

### Skipping execution

If you need to skip parts of the plugin, you may do so using the
following system properties:

|Goal    |Property                  |
|--------|--------------------------|
|download| -Dskip.downloadprecommit |
|install | -Dskip.installprecommit  |

### Gitignore

After running the plugin, you will see a `precommit_files` directory in
your project. This is where the plugin stores the pre-commit files so
that they can be executed when generating the Git hooks.

You probably want to add this directory to your gitignore.

## Developing

### Releasing new versions
Ensure you have access to [OSSRH][ossrh] and that your `~/.m2/settings.xml`
includes the following

```
<server>
  <id>ossrh</id>
  <username>your_ossrh_username</username>
  <password>your_ossrh_password</password>
</server>
```

Run `mvn clean deploy -P release`

[precommit]: https://pre-commit.com
[setup]: https://pre-commit.com/#plugins
[releases]: https://github.com/oslomarketsolutions.com/pre-commit-maven-plugin/releases
[ossrh]: https://central.sonatype.org
