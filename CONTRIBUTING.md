# Contributing

Thanks for your interest in improving JPlotX.

## Development setup

1. Clone the repository.
2. Use Java 17 or newer.
3. Compile locally:

```bash
javac -d out (Get-ChildItem -Recurse -Filter *.java | ForEach-Object { $_.FullName })
```

4. Run the sample generator:

```bash
java -cp out com.jplotx.JPlotXApplication
```

## Contribution guidelines

- Keep the library dynamic and schema-agnostic.
- Prefer builder/API improvements that help third-party developers consume the library cleanly.
- Preserve backward compatibility where practical.
- Update `README.md` when public API behavior changes.
- Add or update sample CSV data when introducing new chart workflows.
- Document release-facing changes in `CHANGELOG.md`.

## Pull requests

- Use clear, descriptive commit messages.
- Keep changes focused.
- Mention any API changes, chart behavior changes, or packaging changes in the PR description.
