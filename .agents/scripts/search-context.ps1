[CmdletBinding()]
param(
    [Parameter(Position = 0)]
    [string] $Query,

    [int] $Top = 5,

    [switch] $Json,

    [switch] $List
)

Set-StrictMode -Version 3.0
$ErrorActionPreference = "Stop"

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$agentRoot = Resolve-Path (Join-Path $scriptDir "..")
$repoRoot = Resolve-Path (Join-Path $agentRoot "..")
$indexPath = Join-Path $agentRoot "context\index.json"

if (-not (Test-Path -LiteralPath $indexPath)) {
    throw "Context index not found: $indexPath"
}

$index = Get-Content -LiteralPath $indexPath -Raw | ConvertFrom-Json
$documents = @($index.documents)

function Join-Text {
    param([object] $Value)

    if ($null -eq $Value) {
        return ""
    }

    if ($Value -is [System.Array]) {
        return ($Value -join " ")
    }

    return [string] $Value
}

function Normalize-SearchText {
    param([string] $Text)

    if ([string]::IsNullOrWhiteSpace($Text)) {
        return ""
    }

    $normalized = $Text.Normalize([System.Text.NormalizationForm]::FormD)
    $builder = [System.Text.StringBuilder]::new()

    foreach ($character in $normalized.ToCharArray()) {
        $category = [System.Globalization.CharUnicodeInfo]::GetUnicodeCategory($character)
        if ($category -ne [System.Globalization.UnicodeCategory]::NonSpacingMark) {
            [void] $builder.Append($character)
        }
    }

    return $builder.ToString().ToLowerInvariant()
}

function Get-Tokens {
    param([string] $Text)

    if ([string]::IsNullOrWhiteSpace($Text)) {
        return @()
    }

    $stopWords = @(
        "the", "and", "for", "from", "with", "that", "this", "into",
        "uma", "para", "que", "por", "dos", "das", "com", "como",
        "onde", "qual", "quais", "sobre", "projeto", "project",
        "agente", "agent", "contexto", "context", "memoria", "memory"
    )

    $tokens = [regex]::Split((Normalize-SearchText $Text), "[^a-z0-9_.:/-]+") |
        Where-Object { $_.Length -ge 2 -and ($stopWords -notcontains $_) } |
        Select-Object -Unique

    return @($tokens)
}

function Expand-Tokens {
    param([string[]] $Tokens)

    $synonyms = @{
        "rota" = @("route", "routes")
        "rotas" = @("route", "routes")
        "autenticacao" = @("auth", "authentication", "jwt")
        "autorizacao" = @("auth", "authorization", "roles", "guards")
        "seguranca" = @("security", "jwt")
        "validacao" = @("validation", "validate")
        "teste" = @("test", "tests")
        "testes" = @("test", "tests")
        "servico" = @("service", "services")
        "servicos" = @("service", "services")
        "controlador" = @("controller", "controllers")
        "controladores" = @("controller", "controllers")
        "infraestrutura" = @("infra", "infrastructure")
        "busca" = @("search")
        "buscas" = @("search")
        "front" = @("frontend")
        "back" = @("backend")
    }

    $expanded = New-Object System.Collections.Generic.List[string]

    foreach ($token in $Tokens) {
        if (-not $expanded.Contains($token)) {
            $expanded.Add($token)
        }

        if ($synonyms.ContainsKey($token)) {
            foreach ($synonym in $synonyms[$token]) {
                if (-not $expanded.Contains($synonym)) {
                    $expanded.Add($synonym)
                }
            }
        }
    }

    return @($expanded)
}

function Get-Excerpt {
    param(
        [string] $Content,
        [string[]] $Tokens
    )

    $lines = $Content -split "`r?`n"
    $hits = New-Object System.Collections.Generic.List[object]

    for ($i = 0; $i -lt $lines.Count; $i++) {
        $line = $lines[$i]
        $lowerLine = Normalize-SearchText $line
        $matched = 0

        foreach ($token in $Tokens) {
            if ($lowerLine.Contains($token)) {
                $matched++
            }
        }

        if ($matched -gt 0) {
            $hits.Add([pscustomobject]@{
                lineNumber = $i + 1
                score = $matched
                text = $line.Trim()
            })
        }
    }

    if ($hits.Count -gt 0) {
        $selected = $hits |
            Sort-Object -Property score, lineNumber -Descending |
            Select-Object -First 4 |
            Sort-Object -Property lineNumber

        return (($selected | ForEach-Object { "{0}: {1}" -f $_.lineNumber, $_.text }) -join "`n")
    }

    return (($lines | Where-Object { -not [string]::IsNullOrWhiteSpace($_) } | Select-Object -First 4) -join "`n")
}

if ($List) {
    $listResults = $documents | ForEach-Object {
        [pscustomobject]@{
            id = $_.id
            title = $_.title
            path = $_.path
            tags = @($_.tags)
            summary = $_.summary
        }
    }

    if ($Json) {
        $listResults | ConvertTo-Json -Depth 6
    } else {
        foreach ($doc in $listResults) {
            Write-Output ("{0} - {1}" -f $doc.id, $doc.title)
            Write-Output ("  path: {0}" -f $doc.path)
            Write-Output ("  tags: {0}" -f (($doc.tags) -join ", "))
            Write-Output ("  summary: {0}" -f $doc.summary)
            Write-Output ""
        }
    }

    exit 0
}

if ([string]::IsNullOrWhiteSpace($Query)) {
    throw "Pass -Query `"search terms`" or use -List."
}

$queryTokens = Expand-Tokens (Get-Tokens $Query)

if ($queryTokens.Count -eq 0) {
    throw "Query did not contain searchable tokens."
}

$results = foreach ($doc in $documents) {
    $docPath = Join-Path $repoRoot $doc.path
    $content = ""

    if (Test-Path -LiteralPath $docPath) {
        $content = Get-Content -LiteralPath $docPath -Raw
    }

    $metadataText = @(
        $doc.id,
        $doc.title,
        $doc.summary,
        (Join-Text $doc.tags),
        (Join-Text $doc.sourcePaths)
    ) -join " "

    $metadataLower = Normalize-SearchText $metadataText
    $contentLower = Normalize-SearchText $content
    $score = 0

    foreach ($token in $queryTokens) {
        if ($metadataLower.Contains($token)) {
            $score += 6
        }

        $count = [regex]::Matches($contentLower, [regex]::Escape($token)).Count
        if ($count -gt 0) {
            $score += [Math]::Min($count, 8)
        }
    }

    if ($score -gt 0) {
        [pscustomobject]@{
            score = $score
            id = $doc.id
            title = $doc.title
            path = $doc.path
            tags = @($doc.tags)
            summary = $doc.summary
            excerpt = Get-Excerpt -Content $content -Tokens $queryTokens
            sourcePaths = @($doc.sourcePaths)
        }
    }
}

$topResults = @($results | Sort-Object -Property score -Descending | Select-Object -First $Top)

if ($Json) {
    [pscustomobject]@{
        query = $Query
        tokens = $queryTokens
        count = $topResults.Count
        results = $topResults
    } | ConvertTo-Json -Depth 8
    exit 0
}

if ($topResults.Count -eq 0) {
    Write-Output ("No context hits for: {0}" -f $Query)
    Write-Output "Fallback: use targeted repo exploration, then add reusable findings to .agents/context/."
    exit 0
}

Write-Output ("Context RAG results for: {0}" -f $Query)
Write-Output ("Tokens: {0}" -f ($queryTokens -join ", "))
Write-Output ""

foreach ($result in $topResults) {
    Write-Output ("[{0}] {1} ({2})" -f $result.score, $result.title, $result.id)
    Write-Output ("path: {0}" -f $result.path)
    Write-Output ("tags: {0}" -f (($result.tags) -join ", "))
    Write-Output ("summary: {0}" -f $result.summary)
    Write-Output "excerpt:"
    Write-Output $result.excerpt
    Write-Output ""
}
