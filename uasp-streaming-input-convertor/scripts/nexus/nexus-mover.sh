#!/usr/bin/env bash
set -e -u
source ./ci.env

echo $CI_NEXUS_URL

#while IFS=' ' read -r GROUP_ID ARTIFACT_ID VERSION
while IFS= read -r REPO
do
  [[ $REPO =~ ^#.* ]] && continue
  echo "REPO: ${REPO}"
  chmod -R +w "./$REPO" || true
  rm -rf "./$REPO" || true
  git clone --single-branch --branch main "ssh://git@bitbucket.region.vtb.ru:7999/uasp/${REPO}.git"
  cd "./${REPO}"
  ARTIFACT_ID=$(xmllint --xpath '/*[local-name()="project"]/*[local-name()="artifactId"]/text()' pom.xml)
  GROUP_ID=$(xmllint --xpath '/*[local-name()="project"]/*[local-name()="groupId"]/text()' pom.xml)
  VERSION=$(xmllint --xpath '/*[local-name()="project"]/*[local-name()="version"]/text()' pom.xml)
  printf 'GROUP_ID: %s, ARTIFACT_ID: %s, VERSION: %s\n' ${GROUP_ID} ${ARTIFACT_ID} ${VERSION}
  mkdir target || true
  dir
  JAR_NEXUS_URL="${CI_NEXUS_URL}/service/rest/v1/search/assets/download?repository=drpim-maven-lib&maven.artifactId=${ARTIFACT_ID}&maven.baseVersion=${VERSION}&maven.extension=jar"
  echo "JAR_NEXUS_URL: $JAR_NEXUS_URL"
  FILE_NAME="target/${ARTIFACT_ID}-${VERSION}.jar"
  echo "FILE_NAME: ${FILE_NAME}"

  #rm -f ${FILE_NAME} || true
  if [ ! -f ./${FILE_NAME} ]; then
      echo "File not found!"
      if curl --fail -o ${FILE_NAME} -kvL -u $DRPIM_NEXUS_USER:$DRPIM_NEXUS_PWD $JAR_NEXUS_URL; then
          echo "Download success!"
        else
        echo "File $JAR_NEXUS_URL download failure. Exit!"
          cd ..
          exit 1
      fi
  fi

  mvn deploy:deploy-file \
    -Dmaven.wagon.http.ssl.insecure=true \
    -Dmaven.wagon.http.ssl.allowall=true \
    -DgroupId=${GROUP_ID} \
    -DartifactId=${ARTIFACT_ID} \
    -Dversion=${VERSION} \
    -DrepositoryId=uasp-maven \
    -DgeneratePom=true \
    -Dpackaging=jar \
    -Dfile=./${FILE_NAME} \
    -DrepositoryId=uasp-maven \
    -Dnexus.repo.username=${UASP_NEXUS_USER} \
    -Dnexus.repo.password=${UASP_NEXUS_PWD} \
    -Durl='https://nexus-ci.corp.dev.vtb/repository/uasp-maven-lib'

  cd ..
done < nexus.list

