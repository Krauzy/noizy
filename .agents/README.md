# Noizy Agent Configuration

This directory stores project-local agent context and a lightweight retrieval script.

## Files

- `noizy.agent.json`: machine-readable project agent configuration.
- `context/index.json`: context RAG manifest.
- `context/*.md`: curated context chunks built from the project and shared memory.
- `scripts/search-context.ps1`: dependency-free retriever for Windows/PowerShell.

## Usage

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .agents/scripts/search-context.ps1 -Query "jwt upload tracks" -Top 5
```

Use `-Json` when another tool needs structured output:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .agents/scripts/search-context.ps1 -Query "localstack buckets" -Top 3 -Json
```

Use `-List` to inspect indexed documents:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .agents/scripts/search-context.ps1 -List
```

## Maintenance

Update the matching `context/*.md` file and `context/index.json` whenever a change adds a reusable convention, validation path, module map, endpoint family, local runtime detail, or cross-stack coupling.
