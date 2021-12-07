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
          echo 'testing application'
        container('maven'){
         
                
                sh ("mvn -B -DskipTests clean package")

           
        }
    }
    
  }
}
