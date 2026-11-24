param([Parameter(Mandatory=$true)][string]$Entrada, [Parameter(Mandatory=$true)][string]$Saida)
$ErrorActionPreference = 'Stop'
$wordRai = $null
$documentoRai = $null
$semSalvarRai = 0
try {
 $wordRai = New-Object -ComObject Word.Application
 $wordRai.Visible = $false
 $wordRai.DisplayAlerts = 0
 $wordRai.AutomationSecurity = 3
 $wordRai.Options.UpdateLinksAtOpen = $false
 $entradaRai = $Entrada
 $falsoRai = $false
 $verdadeiroRai = $true
 $documentoRai = $wordRai.Documents.Open($Entrada, $false, $true, $false)
 $documentoRai.ExportAsFixedFormat($Saida, 17)
} finally {
 if ($null -ne $documentoRai) { try { $documentoRai.Close() } catch { Write-Warning "Documento convertido; limpeza COM pendente." } finally { [void][Runtime.InteropServices.Marshal]::FinalReleaseComObject($documentoRai) } }
 if ($null -ne $wordRai) { try { $wordRai.Quit() } catch { Write-Warning "Conversão concluída; liberação do Word pendente." } finally { [void][Runtime.InteropServices.Marshal]::FinalReleaseComObject($wordRai) } }
}
