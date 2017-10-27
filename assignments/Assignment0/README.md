# BigDebug/BigSift Google Cloud Deployment for CS239

### Step 1:
Create a Google cloud account [here](https://cloud.google.com/).

### Step 2:
Download Google Cloud SDK [here](https://cloud.google.com/sdk/). Follow the installation [instruction](https://cloud.google.com/sdk/docs/) to install the SDK. Make sure you have all the path variables/ environment variables set up in your terminals.

For example, you must update your PATH environment in '~/.bashrc' so that 'gcloud' command can be used. During the installation process, you will also be asked to authorize your Google Cloud account. 

### Step 3:
Now install Docker in you local machine (Follow instructions [here](https://docs.docker.com/engine/installation/)). After the installation is done and once you launch the 'Docker' application, you will have a Docker (e.g., Whale-like icon on your Mac status bar) running. If this step is successful, you should be able to type 'docker' on your command line console.  

### Step 4:
Create a project in your Google Cloud account from your [Google Cloud Console](https://console.cloud.google.com) and note down the name of the project. For example, I created a project with the name 'Assignment0' and I can check that this project is assigned with Project ID, 'assignment0-184120'. On your Google Cloud account on the web, you can see all your projects listed under the option "Select projects and folders" and see the corresponding ID. 

### Step 5:
Now that you have installed Docker and created a project on GCP, check out the content of the assignment 0 from [the course assignment repository on the github] (https://github.com/maligulzar/cs239-deployment).  After checking out the repository to your local hard disk, move to the root directory of './assignments/Assignment0'. You should be able to see the "DockerFile" under this directory. This command creates a docker image by running "Docker" file under the current directory ('.') and give the following name. 

```bash
docker build -t gcr.io/<Your-Project-Name>/bigdebug:v1 .
```
This step will take several minutes to build the docker container from the recipe. You should see the messages similar to the following on your screen. It will then pull the required packages, run each command, etc. This process will take a **very very long** time, as it downloads Spark, Scala, and other tools required to do your subsequent assignments. You need to ensure that your machine has enough hard disk space (several GBs, mine is about ~2.17GB) and memory to finish this step. 
 
```bash 
bash-3.2$ docker build -t gcr.io/assignment0-184120/bigdebug:v1 .
Sending build context to Docker daemon  56.32kB
Step 1/18 : FROM java:openjdk-8-jdk
openjdk-8-jdk: Pulling from library/java
5040bd298390: Downloading  4.194MB/51.36MB
fce5728aad85: Downloading  4.308MB/18.54MB
76610ec20bf5: Downloading  6.389MB/42.5MB
...
```
After this is done, you can test this image by running 

```bash
docker run -it gcr.io/<Your-Project-Name>/bigdebug:v1
```
After you run the above command, it automatically attaches to the container created by the docker image. Therefore you can use CTRL+C or "exit" to detach from the container. 

To list all the images along with their status. Run 
```bash
docker ps -a
```

Use the following command to attach

```bash
docker attach <Container-ID>
```
This 'attach' command allows you to type commands in this container environment that you created based on the docker image. 

### Step 6:
We have a running version of our docker container and now we'll push this container to our Google Contianer Registry. 

You must visit your Google Cloud management web page and enable Google Cloud Container Registry (GCR) for the corresponding project. You can do this by searching for the corresponding project and finding an option to enable GCR. For example, my corresponding URL is 'https://console.cloud.google.com/apis/api/containerregistry.googleapis.com/overview?project=assignment0-184120' where I was able to enable GCR. 

```bash
gcloud docker -- push gcr.io/<project-name>/bigdebug
```
This command will generate a message like the following in the beginning. This may take several minutes. Once the registry is uploaded, it costs $$ for storage. 
  
```bash 
Miryungs-MacBook-Air:Assignment0 miryung$ gcloud docker -- push gcr.io/assignment0-184120/bigdebug
The push refers to a repository [gcr.io/assignment0-184120/bigdebug]
c76a39f98d2b: Pushed 
23a466b38dba: Pushed 
...
```

### Step 7:
Set up a Google Cloud Cluster with this command. (You also need to set the cluster size) 
```bash
gcloud container clusters create bigdebug-cluster
```
Note: If you see an error saying that resource could not be found. It is probably because of the project config not being set. Use the following command to set the project config property. 
```bash
gcloud config set project <Project-Name>
```
Note: If you see an error saying that the zone must be specified, please set the zone for creating a cluster. You may consult this [gcloud command] (https://cloud.google.com/sdk/gcloud/reference/container/clusters/create) for more options. 

For example, my command is the following 
```bash 
Miryungs-MacBook-Air:Assignment0 miryung$ gcloud container clusters create bigdebug-cluster --zone us-central1-f
```
This may take **several minutes** as well. After the cluster is created, I get the following message. 

```bash 
Creating cluster bigdebug-cluster...done.                                      
Created [https://container.googleapis.com/v1/projects/assignment0-184120/zones/us-central1-f/clusters/bigdebug-cluster].
kubeconfig entry generated for bigdebug-cluster.
NAME              ZONE           MASTER_VERSION  MASTER_IP      MACHINE_TYPE   NODE_VERSION  NUM_NODES  STATUS
bigdebug-cluster  us-central1-f  1.7.6-gke.1     146.148.80.67  n1-standard-1  1.7.6         3          RUNNING
```

After it is finished, get the configurations to set the kubernetes configuration file.

```bash
gcloud container clusters get-credentials bigdebug-cluster --project <Project-Name>
```
You must first check that 'kubectl' is enabled in your Google SDK installation. If you have not done so, run the following command to ensure that 'kubectl' is installed.  
```bash 
gcloud components install kubectl
``` 
For example, the following is my command and console message. 
```bash 
Miryungs-MacBook-Air:Assignment0 miryung$ gcloud container clusters get-credentials bigdebug-cluster --project assignment0-184120 --zone us-central1-f
```
### Step 8 (Optional):
To check if kubernetes is configured propperly, run 
```bash
kubectl proxy
```
### Step 9:
Use kubernetes VM orchestration technology to deploy our cluster. Kubernetes YAML files will be used to deploy a master and several workers as well as the Zeppelin notebook.

```bash
kubectl create -f kubernetes/examples/spark
```

To check Kubernetes live pods and their status, execute the following command:
```bash
kubectl get pods
```

This should install and deploy everything on the Goolge Cloud cluster.

### Step 10:

To access the Zeppelin notebook and Spark UI, you need to set SSH tunnels to link you local computer's ports to master, driver and zeppelin notebook residing in Google Cloud.

* To tunnel the Zeppelin port:
```bash
kubectl port-forward <ZEPPELIN_POD_ID> 8080:8080
```
Open any web browser and go to "localhost:8080" and run the example script [here](https://gist.githubusercontent.com/zmerlynn/875fed0f587d12b08ec9/raw/6eac83e99caf712482a4937800b17bbd2e7b33c4/movies.json) in to test the deployment.

* To tunnel throught the master Spark UI:
```bash
kubectl port-forward <MASTER_POD_ID> 8081:8080
```

* To tunnel through the Spark Driver UI:
```bash
kubectl port-forward <MASTER_POD_ID> 4040:4040
```

### Step 11:
Once you figured out all required steps, unless you continue to run a job on the cluster, you must delete the cluster. Otherwise it will cost $$, as you will be continuously charged for the cost, while the cluster is running. You can modify the following command to kill the cluster you created. 

```bash 
gcloud container clusters delete bigdebug-cluster --zone us-central1-f
```

