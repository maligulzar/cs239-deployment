# BigDebug/BigSift Google Cloud Deployment for CS239

### Step 1:
Create a Google cloud account [here](https://cloud.google.com/)

### Step 2:
Download Google Cloud SDK [here](https://cloud.google.com/sdk/). Follow the installation [instruction](https://cloud.google.com/sdk/docs/) to install the SDK. Make sure you have all the path variables/ environment variables set up in your terminals. (Use ~/.bash_rc or ~/.bash_profile). For example, you must update your PATH environment in '~/.bashrc' so that 'gcloud' command can be used. During the installation process, you will also be asked to authorize your Google Cloud account. 

### Step 3:
Now install Docker in you local machine (Follow instructions [here](https://docs.docker.com/engine/installation/)). After the installation and once you launch the 'Docker' application, you will have a docker (whale icon) running. If this step is successful, you should be able to type 'docker' on your command line console.  

### Step 4:
Create a project in your Google Cloud account from your [Google Cloud Console](https://console.cloud.google.com) and note down the name of the project. For example, I created a project with the name 'Assignment0' and I can check that this project is assigned with Project ID, 'assignment0-184120'. You can see the projects listed under the option "Select projects and folders" and see the corresponding ID. 

### Step 5:
Now that you have installed Docker and created a project on GCP, check out the content of the assignment 0 from [this repository] (https://github.com/maligulzar/cs239-deployment).  After checking out the repository to your local hard disk, move to the root directory of './assignments/Assignment0'. You should be able to see the "DockerFile" under this directory. This command creates a docker image by running "Docker" file under the current directory ('.') and give the following name. 

```bash
docker build -t gcr.io/<Your-Project-Name>/bigdebug:v1 .
```
This step will take several minutes to build the docker container from the recipe. You should see the messages similar to the following on your screen. It will then pull the required packages, run each command, etc. This process will take a **very very long** time, as it downloads Spark, Scala, and other tools required to do your subsequent assignments. You need to ensure that your machine has enough hard disk space (several GBs) and memory to finish this step. 
 
```bash 
bash-3.2$ docker build -t gcr.io/assignment0-184120/bigdebug:v1 .
Sending build context to Docker daemon  56.32kB
Step 1/18 : FROM java:openjdk-8-jdk
openjdk-8-jdk: Pulling from library/java
5040bd298390: Downloading  4.194MB/51.36MB
fce5728aad85: Downloading  4.308MB/18.54MB
76610ec20bf5: Downloading  6.389MB/42.5MB
```
After this is done, you can test this image by running 

```bash
docker run -it gcr.io/<Your-Project-Name>/bigdebug:v1
```
After you run the above command, it automatically attaches to the container. Therefore you can use CTRL+C or "exit" to detach from the container. 

To list all the images along with their status. Run 
```bash
docker ps -a
```

Use the following to attach

```bash
docker attach <Container-ID>
```
This 'attach' command allows you to type commands in this container environment that you created based on a docker image. 

### Step 6:
We have a running version of our docker container and now we'll push this container to our Google Cloud Contianer Registry. 
```bash
gcloud docker -- push gcr.io/<project-name>/bigdebug
```

### Step 7:
Set up a Google Cloud Cluster with this command. (You also need to set the cluster size) 
```bash
gcloud container clusters create bigdebug-cluster
```
Note:If you see an error saying that resource could not be found. It is probably becuase of the unset project config. Use the following command to set the project config property. 
```bash
gcloud config set project <Project-Name>
```

This may take several minutes as well. After it is finished, get the configurations to set the kubernetes configuration file.

```bash
gcloud container clusters get-credentials bigdebug-cluster --project <Project-Name>
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

To access the Zeppelin notebook and Spark UI, you need to set SSH tunnels to link you computers' ports to master, driver and zeppelin notebook residing in Google Cloud.

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


