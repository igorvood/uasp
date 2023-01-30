import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.sshAgent
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.swabra
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2019_2.failureConditions.BuildFailureOnText
import jetbrains.buildServer.configs.kotlin.v2019_2.failureConditions.failOnText
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2021.1"

project {

    vcsRoot(MyVcsRoot)
    buildType(Build)
    buildType(TestIT)
    buildType(DeployToFlink)
    buildType(DeployToNexus)

}


object Build : BuildType({
    name = "Build"
    //buildNumberPattern="%build.counter%.%ARTIFACT_NAME%"
    artifactRules = """
        target/%ARTIFACT_NAME% => target/
        conf-deployment => conf-deployment
        *.xml
    """.trimIndent()
    maxRunningBuilds = 1
    publishArtifacts = PublishMode.SUCCESSFUL

    params {
        text("ARTIFACT_NAME", "", allowEmpty = true)
    }

    vcs {
        root(MyVcsRoot)

        checkoutMode = CheckoutMode.ON_AGENT
    }

    steps {
        script {
            name = "Check dir"
            scriptContent = """
                #!/usr/bin/env bash
                set -e -u
                
                ls -lha .
            """.trimIndent()
        }
        script {
            name = "Check project version"
            scriptContent = """
                #!/usr/bin/env bash
                PROJECT_VERSION=${'$'}(xmllint --xpath '/*[local-name()="project"]/*[local-name()="version"]/text()' pom.xml)
                SUB="SNAPSHOT"
                if grep -q "${'$'}SUB" <<< "${'$'}PROJECT_VERSION"; then
                  echo "It's a SNAPSHOT. Only release version is expected. Exit!"
                  exit 1
                fi
            """.trimIndent()
        }
        maven {
            name = "Maven build"
            goals = "clean package"
            runnerArgs = """
                -Ddrpim.nexus.repo.username=%DRPIM_NEXUS_USER%
                -Ddrpim.nexus.repo.password=%DRPIM_NEXUS_PWD%
                -Duasp.nexus.repo.username=%UASP_NEXUS_USER%
                -Duasp.nexus.repo.password=%UASP_NEXUS_PWD%
                -Dmaven.wagon.http.ssl.insecure=true
                -Dmaven.wagon.http.ssl.allowall=true
            """.trimIndent()
            mavenVersion = custom {
                path = "%teamcity.tool.maven.3.6.3%"
            }
            userSettingsSelection = "settings.xml"
            jdkHome = "%env.JDK_18%"
        }
        script {
            name = "Jar detecting and naming"
            scriptContent = """
                FIRST_JAR=${'$'}(ls target/ | grep -E ^uasp-streaming-* | awk 'NR==1 {print ${'$'}1}' | awk -F ".jar" '{print ${'$'}1}')
                
                echo ${'$'}FIRST_JAR
                
                ls -lh target/
                ARTIFACT_NAME=${'$'}{FIRST_JAR}.jar
                
                ls -lh target/
                echo ${'$'}ARTIFACT_NAME
                
                echo "##teamcity[setParameter name='ARTIFACT_NAME' value='${'$'}ARTIFACT_NAME']"
            """.trimIndent()
        }
    }



    features {
        swabra {
            enabled = false
        }
    }
})

object TestIT : BuildType({
    name = "TestIT"

    artifactRules = """
        target/%ARTIFACT_NAME% => target/
        conf-deployment => conf-deployment
        *.xml
    """.trimIndent()
    maxRunningBuilds = 1
    publishArtifacts = PublishMode.SUCCESSFUL

    vcs {
        root(MyVcsRoot)
        checkoutMode = CheckoutMode.ON_AGENT
    }

    steps {
        maven {
            name = "TetsIT exportAutoTests"
            goals = "test-it:exportAutoTests"
            runnerArgs = """
            -DmyCoolToken=%TESTIT_API_TOKEN%
            """.trimIndent()
            executionMode = BuildStep.ExecutionMode.ALWAYS
            mavenVersion = custom {
                path = "%teamcity.tool.maven.3.6.3%"
            }
            userSettingsSelection = "settings.xml"
            jdkHome = "%env.JDK_18%"
        }
        maven {
            name = "TestIT exportTestPlanResults"
            goals = "test-it:exportTestPlanResults"
            runnerArgs = """
            -DmyCoolToken=%TESTIT_API_TOKEN%
            """.trimIndent()
            mavenVersion = custom {
                path = "%teamcity.tool.maven.3.6.3%"
            }
            userSettingsSelection = "settings.xml"
            jdkHome = "%env.JDK_18%"
        }
    }

    failureConditions {
        testFailure = false
        nonZeroExitCode = false
    }

    features {
        swabra {
        }
        sshAgent {
            teamcitySshKey = "my_id_rsa"
        }
    }

    triggers {
        vcs {
            triggerRules = """
                -:conf-deployment
                -:.teamcity
            """.trimIndent()
        }
    }

    dependencies {
        dependency(Build) {
            snapshot {
            }
            artifacts {
                artifactRules = """
                    +:target => target
                    +:conf-deployment => conf-deployment
                    +:*.xml
                """.trimIndent()
            }
        }
    }

})

object DeployToFlink : BuildType({
    name = "Deploy to Flink"

    artifactRules = """
        target => target
        *.xml
    """.trimIndent()
    type = Type.DEPLOYMENT
    maxRunningBuilds = 1
    publishArtifacts = PublishMode.SUCCESSFUL

    params {
        select("STAGE", "",
            options = listOf("ift" to "ift", "ift-k3 - use UNP only" to "ift-k3", "nt" to "nt", "real" to "real", "real-k3 - use UNP only" to "real-k3"), allowMultiple = false,  display = ParameterDisplay.PROMPT)
    }

//    vcs {
//        root(MyVcsRoot)
//    }

    steps {
        script {
            name = "Clone repo, upload jar"
            scriptContent = """
                #!/usr/bin/env bash
                #set -e -u
                if [[ "%STAGE%" == "" ]]; then
                   echo "STAGE is not set. Exit!"
                   exit 1
                fi              
                git clone --single-branch --branch master ssh://git@%BB_HOST%:%BB_PORT%/%BB_REPO%/flink-deployment.git
                ls -lha .
                ls -lha target
                find flink-deployment/scripts/ -name "*.sh" -execdir chmod u+x {} +
                
                echo "Current stage: %STAGE%"
              
                cp target/*.jar flink-deployment/scripts/flink/
                ls -lha flink-deployment/scripts/flink
                cd flink-deployment/scripts               
                env | sort
                echo "Current stage: %STAGE%"
                
                if [[ "%STAGE%" == "real" ]] || [[ "%STAGE%" == "real-k3" ]]; then                   
                    TMP_DIR="/tmp/cicd/%teamcity.build.id%"
                    echo "${'$'}TMP_DIR"
                    ssh '%JUMP_USER%@%JUMP_HOST%' "mkdir -p ${'$'}TMP_DIR" 
                    cd ../..
                    rsync --delete -Pavr -e ssh . '%JUMP_USER%@%JUMP_HOST%':${'$'}TMP_DIR
                    ssh '%JUMP_USER%@%JUMP_HOST%' "chmod -R 777 ${'$'}TMP_DIR"
                    #ssh '%JUMP_USER%@%JUMP_HOST%' "ls -lha ${'$'}TMP_DIR/flink-deployment/scripts/flink/" 
                    ssh '%JUMP_USER%@%JUMP_HOST%' "export RR_KAFKA_SSL_TRUSTSTORE_PASSWORD=${'$'}RR_KAFKA_SSL_TRUSTSTORE_PASSWORD; export RR_KAFKA_SSL_KEYSTORE_PASSWORD=${'$'}RR_KAFKA_SSL_KEYSTORE_PASSWORD; export RR_KAFKA_SSL_KEY_PASSWORD=${'$'}RR_KAFKA_SSL_KEY_PASSWORD; cd ${'$'}TMP_DIR/flink-deployment/scripts; ./deploy2stage.sh %STAGE%"                              
                    ssh '%JUMP_USER%@%JUMP_HOST%' "rm -rf ${'$'}TMP_DIR"                 
                else
                    ./deploy2stage.sh %STAGE% 
                fi

            """.trimIndent()
        }
    }

    failureConditions {
        executionTimeoutMin = 2
        failOnText {
            conditionType = BuildFailureOnText.ConditionType.CONTAINS
            pattern = "See cluster's logs. Exit!"
            reverse = false
        }
    }



    features {
        swabra {
        }
        sshAgent {
            teamcitySshKey = "my_id_rsa"
        }
        sshAgent {
            teamcitySshKey = "id_rsa_bb"
        }
    }

    dependencies {
        artifacts(Build) {
            buildRule = lastSuccessful("+:*")
            cleanDestination = true
            artifactRules = """
                    +:target => target
                    +:conf-deployment => conf-deployment
                    +:*.xml
                """.trimIndent()
        }
//        dependency(Build) {
//            snapshot {
//            }
//
//            artifacts {
//                artifactRules = """
//                    +:target => target
//                    +:conf-deployment => conf-deployment
//                    +:*.xml
//                """.trimIndent()
//            }
//        }
    }
})

object DeployToNexus : BuildType({
    name = "Deploy to Nexus"

//    params {
//        text("NEXUS_VERSION", "0.0.1", description="example:0.0.1", display = ParameterDisplay.PROMPT, allowEmpty = false)
////        param("REPO_URL", "")
////        param("REPO_ID", "")
//
//    }

//    vcs {
//        root(MyVcsRoot)
//    }

    steps {
//        script {
//            name = "Pre-init vars"
//            scriptContent = """
//                ls -lha .
//                ls -lha target
//
//                FIRST_JAR=${'$'}(ls target/ | grep -E ^uasp-streaming-* | awk 'NR==1 {print ${'$'}1}' | awk -F ".jar" '{print ${'$'}1}')
//                echo ${'$'}FIRST_JAR
//                SUB="SNAPSHOT"
//                if grep -q "${'$'}SUB" <<< "${'$'}FIRST_JAR"; then
//                  echo "It's SNAPSHOT"
//                  REPO_ID="uasp-maven-snapshot"
//                  REPO_URL=%SNAPSHOT_REPO_URL%
//                else
//                  echo "It's RELEASE"
//                  REPO_ID="uasp-maven"
//                  REPO_URL=%RELEASE_REPO_URL%
//                fi
//
//                echo "REPO_ID: ${'$'}{REPO_ID}"
//                echo "REPO_URL: ${'$'}{REPO_URL}"
//
//                ARTFCT_NAME=${'$'}{FIRST_JAR}.jar
//                echo "ARTFCT_NAM: ${'$'}ARTFCT_NAME"
//
//                echo "##teamcity[setParameter name='ARTIFACT_NAME' value='${'$'}ARTFCT_NAME']"
//                echo "##teamcity[setParameter name='REPO_ID' value='${'$'}REPO_ID']"
//                echo "##teamcity[setParameter name='REPO_URL' value='${'$'}REPO_URL']"
//
//
//            """.trimIndent()
//        }
//        script {
//            name = "Create nexus tag"
//            scriptContent = """
//                    #!/usr/bin/env bash
//                    curl -kvL -u %UASP_NEXUS_USER%:'%UASP_NEXUS_PWD%' -X POST "%nexus_url%/service/rest/v1/script/getVersion/run" -H "accept: application/json" -H "Content-Type: text/plain" -d "{\"version\":\"%NEXUS_VERSION%\"}"
//                """.trimIndent()
//
//        }
//        script {
//            name = "Maven deploy artefact to cdp prod nexus"
//            scriptContent = """
//                    #!/usr/bin/env bash
//                    curl -kvL -u %UASP_NEXUS_USER%:'%UASP_NEXUS_PWD%' -X POST "%nexus_url%/service/rest/v1/components?repository=uasp-maven-test" \
//                       -F groupId=%maven.project.groupId% \
//                       -F artifactId=%maven.project.artifactId%  \
//                       -F version=%maven.project.version% \
//                       -F asset1=@target/%maven.project.artifactId%-%maven.project.version%.jar \
//                       -F asset1.extension=jar \
//                       -F tag=ver-uasp-%NEXUS_VERSION%
//                """.trimIndent()
//        }


//        maven {
//            name = "Maven deploy artefact to cdp prod nexus"
//            goals = "deploy:deploy-file"
//            pomLocation = ""
//            runnerArgs = "-DgroupId=%maven.project.groupId% -DartifactId=%maven.project.artifactId% -Dversion=%maven.project.version% -DgeneratePom=true -Dpackaging=jar -Dfile=target/%maven.project.artifactId%-%maven.project.version%.jar -DrepositoryId=uasp-maven-test -Ddrpim.nexus.repo.username=%DRPIM_NEXUS_USER% -Ddrpim.nexus.repo.password=%DRPIM_NEXUS_PWD% -Duasp.nexus.repo.username=%UASP_NEXUS_USER%-Duasp.nexus.repo.password=%UASP_NEXUS_PWD% -Durl=%CDP_TEST_REPO_URL%
//            mavenVersion = custom {
//                path = "%teamcity.tool.maven.3.6.3%"
//            }
//            userSettingsSelection = "settings.xml"
//        }



        maven {
            name = "Maven deploy artefact to Nexus"
            enabled = false
            goals = "deploy:deploy-file"
            pomLocation = ""
            runnerArgs = """
                -DgroupId=%maven.project.groupId% 
                -DartifactId=%maven.project.artifactId% 
                -Dversion=%maven.project.version% 
                -DgeneratePom=true 
                -Dpackaging=jar 
                -Dfile=target/%maven.project.artifactId%-%maven.project.version%.jar 
                -DrepositoryId=uasp-maven
                -Duasp.nexus.repo.username='%UASP_NEXUS_USER%' 
                -Duasp.nexus.repo.password='%UASP_NEXUS_PWD%' 
                -Ddrpim.nexus.repo.username='%DRPIM_NEXUS_USER%' 
                -Ddrpim.nexus.repo.password='%DRPIM_NEXUS_PWD%' 
                -Durl=%RELEASE_REPO_URL%
                -Dmaven.wagon.http.ssl.insecure=true
                -Dmaven.wagon.http.ssl.allowall=true    
            """.trimIndent()
            mavenVersion = custom {
                path = "%teamcity.tool.maven.3.6.3%"
            }
            userSettingsSelection = "settings.xml"
        }
        script {
            name = "Upload artefact to Nexus"
            scriptContent = """
            #!/usr/bin/env bash

            echo "Geting artifact parameters"
            SERVICE_ARTIFACT_ID=${'$'}(xmllint --xpath '/*[local-name()="project"]/*[local-name()="artifactId"]/text()' pom.xml)
            SERVICE_GROUP_ID=${'$'}(xmllint --xpath '/*[local-name()="project"]/*[local-name()="groupId"]/text()' pom.xml)
            SERVICE_VERSION=${'$'}(xmllint --xpath '/*[local-name()="project"]/*[local-name()="version"]/text()' pom.xml)
            printf 'GROUP_ID: %s, ARTIFACT_ID: %s, VERSION: %s\n' ${'$'}{SERVICE_GROUP_ID} ${'$'}{SERVICE_ARTIFACT_ID} ${'$'}{SERVICE_VERSION}
            FILE_NAME="target/${'$'}{SERVICE_ARTIFACT_ID}-${'$'}{SERVICE_VERSION}.jar"
            echo "FILE_NAME: ${'$'}FILE_NAME"
            
            echo "Check artifact exists"
            response=$(curl -kLs -H "Accept: application/json" -u %UASP_NEXUS_USER%:'%UASP_NEXUS_PWD%' "%nexus_url%/service/rest/v1/search?sort=version&direction=desc&repository=%nexus_api_repo%&format=maven2&group=${'$'}{SERVICE_GROUP_ID}&name=${'$'}{SERVICE_ARTIFACT_ID}&version=${'$'}{SERVICE_VERSION}")
            ITEMS_COUNT=$(echo "${'$'}response" | jq '.items | length')
            echo "ITEMS_COUNT: ${'$'}ITEMS_COUNT"
            if [[ "${'$'}ITEMS_COUNT" -ne 0 ]]; then
              echo "Artifact ${'$'}SERVICE_ARTIFACT_ID, groupId ${'$'}SERVICE_GROUP_ID, version ${'$'}SERVICE_VERSION already exist in the %nexus_api_repo%. Exit!"
              exit 1
            fi
            
            echo "Uploading to ci nexus"
            curl -kL -u %UASP_NEXUS_USER%:'%UASP_NEXUS_PWD%' -X POST "%nexus_url%/service/rest/v1/components?repository=uasp-maven-lib" \
               -F maven2.groupId="${'$'}SERVICE_GROUP_ID" \
               -F maven2.artifactId="${'$'}SERVICE_ARTIFACT_ID" \
               -F maven2.version="${'$'}SERVICE_VERSION" \
               -F maven2.asset1=@"${'$'}FILE_NAME" \
               -F maven2.asset1.extension=jar \
               -F maven2.generate-pom=true
    
            """.trimIndent()
        }


    }

    triggers {
        vcs {
            enabled = false
            triggerRules = "-:conf-deployment"
            watchChangesInDependencies = true
        }
    }

    features {
        swabra {
        }
    }

    dependencies {
        artifacts(DeployToFlink) {
            buildRule = lastSuccessful("+:*")
            cleanDestination = true
            artifactRules = """
                    +:target/*.jar => target/
                    +:*.xml
                """.trimIndent()

        }
//        dependency(DeployToFlink) {
//            snapshot {
//            }

//            artifacts {
//                artifactRules = """
//                    +:target/*.jar => target/
//                    +:*.xml
//                """.trimIndent()
//            }
//        }
    }
})

object MyVcsRoot : GitVcsRoot({
    name = DslContext.getParameter("vcsName")
    url = DslContext.getParameter("vcsUrl")
    branch = DslContext.getParameter("vcsBranch", "refs/heads/main")
    branchSpec = DslContext.getParameter("vcsBranchSpec","""
+:refs/heads/release/*
+:refs/heads/hotfixes/*
    """.trimMargin())
    authMethod = uploadedKey {
        uploadedKey = "id_rsa_bb"
    }
})
