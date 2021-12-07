pipeline {
  agent any 
  environment {
    PATH = "/opt/apache-maven-3.8.4/bin:$PATH"
  }
  stages {
    stage ("build"){
      steps{
   echo 'Building application'
      }
    }
    
    stage ("test"){
      steps{
   echo 'testing application'
      }
    }
    
     stage("Build with Maven") {
  
        steps{
         
                   echo 'testing application'

    sh "mvn -B -DskipTests clean package"
          
        }
    }
    
  }
}
