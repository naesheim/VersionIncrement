## VersionIncrement


Automagically version increment

Available methods

./gradlew bumpMajor
  - increment x. sets y. and z. to 0  -> x+1.0.0


./gradlew bumpMinor
  - increment y. sets z. to 0  -> x.y+1.0


./gradlew bumpPatch
  - increment z.  -> x.y.z+1

./gradlew incrementVersion
  - query Artifactory/Nexus for highest version x.y then bumps z  -> x.y.+1 


Looks for artifact name and current version in build.properties in root folder

Looks for binary manager url, username and password in gradle.properties in ~/ folder
