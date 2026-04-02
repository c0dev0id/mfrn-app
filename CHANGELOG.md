# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Login screen (Phase 1): username/password form authenticating against the MFRN WoltLab forum
- `MfrnClient`: HTTP client handling WoltLab's double-submit CSRF login flow (XSRF-TOKEN cookie + `t` form field)
- Initial project scaffold: Gradle build, CI/CD workflow, launcher icons
