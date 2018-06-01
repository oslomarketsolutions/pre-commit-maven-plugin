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

> NOTE: Not published to maven central yet!

Add the following to your `pom.xml`:

```xml
<plugin>
    <groupId>no.oms.maven</groupId>
    <artifactId>pre-commit-maven-plugin</artifactId>
    <version>LATEST_VERSION</version>
    <configuration>
        <!-- The version of pre-commit you would like to use -->
        <version>v1.10.1</version>
    </configuration>
</plugin>
```

Remember to replace `LATEST_VERSION` with the version of the plugin you
would like to use, i.e. [the latest version][releases]. This will
register the plugin

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

[precommit]: https://pre-commit.com
[setup]: https://pre-commit.com/#plugins
[releases]: https://github.com/oslomarketsolutions.com/pre-commit-maven-plugin/releases
