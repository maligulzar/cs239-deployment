# BigDebug/BigSift Google Cloud Deployment for CS239

### Step 1:
Create a Google cloud account [here](https://cloud.google.com/)

### Step 2:
Download Google Cloud SDK [here](https://cloud.google.com/sdk/). Follow the installation [instruction](https://cloud.google.com/sdk/docs/) to install the SDK. Make sure you have all the path variables/ environment variables set up in your terminals. (Use ~/.bash_rc or ~/.bash_profile). During the installation process, you will also be  asked to authorize your Google Cloud account. 

### Step 3:
Now install Docker in you local machine (Follow instructions [here](https://docs.docker.com/engine/installation/)). 

### Step 4:
Create a project in your Google Cloud account from your [Google Cloud Console](https://console.cloud.google.com) and note down the name of the project.  


### Step 5:
Now that you have installed Docker and created a Project, move in to the root directory of CS239 repository (where the "DockerFile" is located ) and run the following commond.
```bash
docker build -t gcr.io/<Your-Project-Name>/bigdebug:v1 .
```

This step will take several minutes to build the docker container from the reciepe. After this is done, you can test this image by running 

```bash
docker run -it gcr.io/<Your-Project-Name>/bigdebug:v1
```

Use CTRL+C or "exit" to dettach from the container. 

To list all the images along with their status. Run 
```bash
docker ps -a
```

Use the following to attach

```bash
docker attach <Container-ID>
```


### Step 6:
We have a running version of our docker container and now we'll push this container to our Google Cloud Contianer Registry. 
```bash
gcloud docker -- push gcr.io/deployment-test-183218/bigdebug
```

### Step 7:
Set up a Google Cloud Cluster with this command. (You also need to set the cluster size) 
```bash
gcloud container clusters create bigdebug-cluster
```
This may take several minutes as well. After it is finished, get the configurations to set the kubernetes conf file.

```bash
gcloud container clusters get-credentials bigdebug-cluster --project <Project-Name>
```

### Step 8 (Optional):
To check if kubernetes is configured propperly, run 
```bash
kubectl proxy
```
### Step 9:
Use kubernetes VM orchestration technology, to deploy our cluster. Kuberneter YAML files to deploy a master and several workers as well as Zeppelin.

```bash
kubectl create -f kubernetes/examples/spark
```

To check Kubernetes live pods and their status, executed the following command:
```bash
kubectl get pods
```

This should install and deploy every thing on the Goolge Cloud cluster.

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


