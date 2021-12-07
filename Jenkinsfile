pipeline {
  agent any 
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

withMaven(maven: 'mvn') {
            sh "mvn clean package  -DskipTests"
        }
           
        }
    }
    
  }
}
