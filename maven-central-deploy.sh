#!/bin/bash
# Deploy maven artefact in current directory into Maven central repository 
# using maven-release-plugin goals

read -p "Really deploy to maven cetral repository  (yes/no)? "

if ( [ "$REPLY" == "yes" ] ) then
  read -p "Enter paraphrase to sign APK: "
  ssh-add ~/.ssh/demidenko05git
  ssh-add -l
  mvn release:clean release:prepare release:perform -Dandroid.release=true -Dsignpass="$REPLY" -B -e | tee maven-central-deploy.log
  ssh-add -D
else
  echo 'Exit without deploy'
fi

