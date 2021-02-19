# `Json-fixtures` Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [3.1.0] - 2021-02-19
### Added
- `PropertyMatchers` can be initialized with a Map of matchers

## [3.0.0] - 2020-04-21
### BREAKING CHANGES
- `FixtureAssert` has a built-in validation which verifies that all the overridden properties exists in the current object. 
With this strictness, developers can be sure that all the matchers they provided will be matching valid properties of the original object, 
so they can't make no-op matchers believing that it works as expected.
### Fixed
- `FixtureAssert` now compares expected and actual values excluding the overridden properties.

## [2.2.0] - 2020-04-17
### Added
- `PropertyMatchers` can be used to match not just properties with simple types but nested objects as well.
- Some built-in `PropertyMatchers` are provided by the library:
    - `UuidMatcher`: Can detect that a property is a valid UUID string.
    - `NestedObjectMatcher`: Can be used to wrap other matchers for checking nested objects (see more in readme).
    - `ZonedDateTimeEqualsInUtcMatcher`: A useful matcher to compare `java.time.ZonedDateTime` instances without taking care of the zones. It can be useful when the tests are executed in different time zones (e.g.: on a CI machine at different location). 

## [2.1.4] - 2019-10-16
### Fixed
- Every instantiation of `Reflections` class scans the full java classpath to initialize its internal cache. 
In the previous versions this happened every time when `FixtureAnnotations.initFixtures` have been called, 
which required extra overhead and made the test execution much slower.
To prevent this, `ClassPathFixtureScanner` classes can be initialized with a pre-loaded `Reflections` instance. 
If you use the the default settings, no more actions needed. 
In this case, the default `Reflections` instance is created along with the `Settings` class.

## [2.1.3] - 2019-09-24
### Fixed
- FixtureAssert updated to handle lists properly when property matchers are used.

## [2.1.2] - 2019-08-01
### Changed
- Reflections library upgraded to 0.9.11.

## [2.1.1] - 2019-07-12
### Changed
- `BeanFactory` and `SnapshotGenerator` instances are created for every testcase instead of static initialization.
- These instances are accessible from `Settings` object.

## [2.1.0] - 2019-07-03
### Added
- More flexible fixture scanning mechanisms added. See the [docs](https://github.com/corballis/json-fixtures#fixture-scanners) for more details.

## [2.0.0] - 2019-05-31
### Added
- Reference handling: You can link fixtures to other fixtures. See the docs for more info.
- PropertyMatchers: To handle some properties differently during the assertion.
- Snapshot matching: `FixtureAssert.toMatchSnapshot*` methods.
- `referencePrefix` is added to `@Fixture` annotation to configure the reference separator. 
- `initFixtures` now accepts a `Settings.Builder` instance 
- `initFixtures` now validates auto generated fixtures

### Changed
- Jackson version upgraded from `2.2.3` to `2.9.9`
- Minimum Java version is 8 for development
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
```Java
ObjectMapperProvider.setObjectMapper(...)
```
calls in your tests need to be updated to
```Java
ObjectMapper objectMapper = new ObjectMapper();
Settings.Builder settings = new Settings.Builder().setObjectMapper(objectMapper);

FixtureAnnotations.initFixtures(this, settings);
```
- Append mode was removed from `Generator`
