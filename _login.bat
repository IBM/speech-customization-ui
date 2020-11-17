ibmcloud login -a https://api.us-east.bluemix.net --apikey RK9yv5NIpJcooltObCgtcyR6NM0Yw52WO00IMqBp1dkE
ibmcloud cs region-set us-east
ibmcloud target -o sttorg -s sttspace
bx login -a https://api.us-east.bluemix.net -apikey RK9yv5NIpJcooltObCgtcyR6NM0Yw52WO00IMqBp1dkE
bx target -o sttorg -s sttspace
REM export KUBECONFIG=/Users/afaisman/.bluemix/plugins/container-service/clusters/stt_frontend_cluster/kube-config-wdc07-stt_frontend_cluster.yml
REM [Environment]::SetEnvironmentVariable("KUBECONFIG", "C:\Users\AlexanderFaisman\.bluemix\plugins\container-service\clusters\stt_frontend_cluster\kube-config-wdc07-stt_frontend_cluster.yml", [EnvironmentVariableTarget]::User)


REM SET KUBECONFIG=C:\Users\AlexanderFaisman\.bluemix\plugins\container-service\clusters\stt_frontend_cluster\kube-config-wdc07-stt_frontend_cluster.yml
SET KUBECONFIG=C:\dev\kubeConfig562493499\kube-config-wdc07-stt_frontend_cluster.yml


bluemix app push sttcustomization
