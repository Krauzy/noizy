# RAG Maintenance Rules

## Purpose

The local context RAG reduces repeated broad searches by keeping a small curated index of stable, reusable project knowledge.

It is not a replacement for source verification. Always verify live files before editing behavior.

## Query policy

Before broad `rg` searches, run:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .agents/scripts/search-context.ps1 -Query "<task>" -Top 5
```

Then read or search only the modules indicated by the top results. Broaden the search when:

- The query returns no relevant result.
- The task is novel.
- A result looks stale.
- The change has cross-stack blast radius.

## What to index

Index reusable context such as:

- Module maps and ownership boundaries.
- Endpoint families and route maps.
- Runtime ports and local credentials.
- Validation commands and known reliable checks.
- Cross-stack coupling, such as JWT, CORS, S3 bucket names, Range streaming, and frontend mocks.
- Prior failure modes that should change future behavior.

## What not to index

Do not index:

- Real secrets or tokens.
- Generated folders such as `node_modules`, `dist`, `build`, `.terraform`, or coverage output.
- Large copied source files.
- One-off implementation details with no future retrieval value.

## Updating the index

When adding or changing a context file:

1. Update the markdown chunk in `.agents/context/`.
2. Update `.agents/context/index.json` with tags and summary.
3. Run a targeted query to confirm the new context is retrievable.
4. Keep summaries concise and fact-based.
