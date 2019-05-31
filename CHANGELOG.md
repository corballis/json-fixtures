# `Json-fixtures` Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.5] - 2019-05-31
### Added
- Reference handling: You can link fixtures to other fixtures. See the docs for more info.
- PropertyMatchers: To handle some properties differently during the assertion.
- `FixtureAssert.toMatchSnapshot*` methods.
- `referencePrefix` is added to `@Fixture` annotation to configure the reference separator. 
- `initFixtures` now accepts a `Settings.Builder` instance 
- `initFixtures` now validates auto generated fixtures

### Changed
- Jackson version upgraded from `2.2.3` to `2.9.9`
- Minimum java version is 8 for development
- `BeanFactory.init` does not throw any exceptions anymore
- Default ObjectMapper now prints json in custom format. See: `JsonFixturesPrettyPrinter`

### Fixed
- `ObjectMapperProvider` was a static class and it could be overridden in any test class. When multiple tests were running a test could modify the configured `ObjectMapper` instance which affected another tests. This led to failing test.

### Removed
- `ObjectMapperProvider` is replaced with `Settings.Builder`
- `FileSystemWriter` is replaced with `FixtureWriter`

### BREAKING CHANGES
- `FixtureAnnotations.initFixtures` must be executed in the same thread where the tests are running (only when you use snapshots).
- Custom `ObjectMapper` can only be configured with `Settings.Builder`
- Append mode is removed from `Generator`
