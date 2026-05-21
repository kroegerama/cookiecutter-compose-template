# cookiecutter-compose-template

Cookiecutter template for a modern Android app using Jetpack Compose.

## Prerequisites

- Git
- Python 3
- [Cookiecutter](https://cookiecutter.readthedocs.io/en/stable/installation.html) template engine

## Usage

```sh
cookiecutter gh:kroegerama/cookiecutter-compose-template
```

You will be prompted for:

| Prompt | Default | Notes |
|---|---|---|
| App name | `My App` | Display name of the app |
| Application ID | `com.example.myapp` | Lowercase, 3+ dot-separated segments |
| Kotlin namespace | `com.example.myapp` | Lowercase, 2+ dot-separated segments, no Kotlin keywords |
| Minimum SDK | `27` | |
| Target folder name | _(derived from app name)_ | Folder where the project is generated |

## What's Included

**UI**
- Jetpack Compose + Material3 Expressive
- Navigation3 with [Scene Decorators](https://developer.android.com/guide/navigation/navigation-3/recipes/navscenedecorator)

**Dependency Injection**
- Hilt

**Networking**
- [kmpgen](https://github.com/kroegerama/openapi-kmp-gen) - OpenAPI Kotlin client codegen
- [Ktor](https://ktor.io/)
- [Chucker](https://github.com/ChuckerTeam/chucker) (debug HTTP inspector, no-op in release)

**Utilities**
- [Arrow](https://arrow-kt.io/) (functional programming)
- DataStore (Preferences)
- kotlinx.serialization
- [logcat](https://github.com/square/logcat)

**Project structure**

```
<project_slug>/
├── app/          # Main application module
└── network/      # API client module incl. generated API client (kmpgen)
```

## Example

The [`example/`](https://github.com/kroegerama/cookiecutter-compose-template/tree/main/example) directory contains a pre-generated project showing what the template produces.
