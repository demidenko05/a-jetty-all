#!/bin/bash
# Deploy maven artefact in current directory into Maven central repository 
# using maven-release-plugin goals

read -p "Really deploy to maven cetral repository  (yes/no)? "

if ( [ "$REPLY" == "yes" ] ) then
  read -p "Enter SSH key-file name: " sshkeyfile
  ssh-add ~/.ssh/$sshkeyfile
  ssh-add -l
  read -p "Enter GPG keyname: " gpgkeynm
  read -p "Enter key alias sign JAR/APK: " keyalias
  read -s -p "Enter pass-phrase to sign JAR/APK: " passw
  mvn -Darguments="-Prelease -Dandroid.release=true -Dsignpass=$passw -Dsignalias=$keyalias -Dgpgkeyname=$gpgkeynm" release:clean release:prepare release:perform -B -e | tee maven-central-deploy.log
  ssh-add -D
else
  echo 'Exit without deploy'
fi

