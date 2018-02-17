$payload = @{
    checkin=[System.Convert]::ToBase64String([System.Text.Encoding]::UTF8.GetBytes("Hello"))
}
#Invoke-RestMethod 'http://chkn-chkn.1d35.starter-us-east-1.openshiftapps.com' -Method Post -Body ($payload | ConvertTo-Json) -ContentType 'application/json; encoding=utf-8'
Invoke-RestMethod 'http://localhost:8080/' -Method Post -Body ($payload | ConvertTo-Json) -ContentType 'application/json; encoding=utf-8'