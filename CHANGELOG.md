# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [0.2.2] - 2018-08-07
### Fixed
- Fixes an issue with detecting the correct python binary ([#1][issue-1])

## [0.2.1] - 2018-06-11
### Fixed
- Made version checking smarter to support systems where `python` is a symlink
  to python 2

## [0.2.0] - 2018-06-09
### Added
- New argument `hookTypes` for installing multiple different hook types
- Python output is now always written to maven output. `download-binary` is very
  verbose and is therefore written to DEBUG which requires `-X` to see.

### Changed
- Changed the `version` argument to `precommitVersion` to differentiate it from
  the plugin version

## [0.1.0] - 2018-06-06
### Added
- Initial release of the plugin

[Unreleased]: https://github.com/oslomarketsolutions/pre-commit-maven-plugin/compare/0.2.2...HEAD
[0.2.2]: https://github.com/oslomarketsolutions/pre-commit-maven-plugin/compare/0.2.1...0.2.2
[0.2.1]: https://github.com/oslomarketsolutions/pre-commit-maven-plugin/compare/0.2.0...0.2.1
[0.2.0]: https://github.com/oslomarketsolutions/pre-commit-maven-plugin/compare/0.1.0...0.2.0
[0.1.0]: https://github.com/oslomarketsolutions/pre-commit-maven-plugin/compare/e5dfac7097cb80b54dc3e802b453f40fd2f05fb6...0.1.0
[issue-1]: https://github.com/oslomarketsolutions/pre-commit-maven-plugin/issues/1

