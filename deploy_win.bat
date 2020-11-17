ibmcloud login -a https://api.us-east.bluemix.net --apikey RK9yv5NIpJcooltObCgtcyR6NM0Yw52WO00IMqBp1dkE
ibmcloud cs region-set us-east
ibmcloud target -o sttorg -s sttspace
bx login -a https://api.us-east.bluemix.net -apikey RK9yv5NIpJcooltObCgtcyR6NM0Yw52WO00IMqBp1dkE
bx target -o sttorg -s sttspace
REM export KUBECONFIG=/Users/afaisman/.bluemix/plugins/container-service/clusters/stt_frontend_cluster/kube-config-wdc07-stt_frontend_cluster.yml
[Environment]::SetEnvironmentVariable("KUBECONFIG", "C:\Users\AlexanderFaisman\.bluemix\plugins\container-service\clusters\stt_frontend_cluster\kube-config-wdc07-stt_frontend_cluster.yml", [EnvironmentVariableTarget]::User)

bx dev build
bx dev deploy

