$LayerCompRegExp = 'layercomp_\d+_([^-]*)-?(.*)'
$DirContent = @(Get-ChildItem -Path '.\*' -Include 'layercomp_*' -File | Foreach-Object {$_.Name})

for( $i = 0; $i -lt $DirContent.Count; $i++ ) {
	$v = $DirContent[$i]

	if( $v.Contains('-') ) {
		$dir = $v -replace $LayerCompRegExp, '.\$1\'
		
		if( !(Test-Path $dir -PathType 'Container') ) {
			New-Item -Path $dir -ItemType 'Directory' -ErrorAction 'Stop'
		}
		
		$v = $v, ($v -replace $LayerCompRegExp, "$dir`$2")
	} else {
		$v = $v, ($v -replace $LayerCompRegExp, ".\`$1")
	}
	
	Move-Item -Path $v[0] -Destination $v[1] -Force
}