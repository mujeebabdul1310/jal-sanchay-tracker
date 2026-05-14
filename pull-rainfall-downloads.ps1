param(
    [string]$Destination = (Join-Path $env:USERPROFILE "Downloads")
)

$ErrorActionPreference = "Stop"

function Get-AdbPath {
    $localProperties = Join-Path $PSScriptRoot "local.properties"
    if (Test-Path $localProperties) {
        $sdkLine = Get-Content -LiteralPath $localProperties |
            Where-Object { $_ -match "^sdk\.dir=" } |
            Select-Object -First 1

        if ($sdkLine) {
            $sdkDir = ($sdkLine -replace "^sdk\.dir=", "").Replace("\\", "\")
            $adbFromSdk = Join-Path $sdkDir "platform-tools\adb.exe"
            if (Test-Path $adbFromSdk) {
                return $adbFromSdk
            }
        }
    }

    foreach ($envName in @("ANDROID_HOME", "ANDROID_SDK_ROOT")) {
        $sdkDir = [Environment]::GetEnvironmentVariable($envName)
        if ($sdkDir) {
            $adbFromEnv = Join-Path $sdkDir "platform-tools\adb.exe"
            if (Test-Path $adbFromEnv) {
                return $adbFromEnv
            }
        }
    }

    $adbFromPath = Get-Command adb -ErrorAction SilentlyContinue
    if ($adbFromPath) {
        return $adbFromPath.Source
    }

    throw "adb.exe was not found. Open Android Studio once or add Android SDK platform-tools to PATH."
}

$adb = Get-AdbPath
New-Item -ItemType Directory -Path $Destination -Force | Out-Null

$devices = & $adb devices |
    Select-Object -Skip 1 |
    Where-Object { $_ -match "\sdevice$" }

if (-not $devices) {
    throw "No Android emulator/device found. Start the emulator, export CSV/PDF from the app, then run this script again."
}

$deviceFiles = & $adb shell "ls -1 /sdcard/Download/jal_sanchay_recent_rainfall_* 2>/dev/null" |
    ForEach-Object { $_.Trim() } |
    Where-Object { $_ -and $_ -notmatch "No such file" }

if (-not $deviceFiles) {
    throw "No rainfall export files found in Android Download. First tap DOWNLOAD CSV or DOWNLOAD PDF in the app Report page."
}

foreach ($deviceFile in $deviceFiles) {
    & $adb pull $deviceFile $Destination | Out-Null
}

Write-Host "Copied $($deviceFiles.Count) rainfall export file(s) to $Destination"
