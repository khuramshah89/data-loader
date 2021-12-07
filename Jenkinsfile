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
  
        container('maven'){
         
                   echo 'testing application'
                sh ("mvn -B -DskipTests clean package")

           
        }
    }
    
  }
}
