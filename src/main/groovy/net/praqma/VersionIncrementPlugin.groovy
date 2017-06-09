package net.praqma
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.apache.tools.ant.taskdefs.condition.Os

class VersionIncrementPlugin implements Plugin<Project> {

     void apply(Project project){
         project.task("bumpPatch") {
            group = "versioning"
            doLast {
                Properties props = new Properties()
                File propFile = new File('build.properties')
                propFile.withInputStream {
                    props.load(it)
                }
                def vers = props.version
                def list = vers.tokenize('.')
                def newPatch = list[2].toInteger() + 1

                props.setProperty('version', (list[0]+'.'+list[1]+'.'+newPatch).toString())
                props.store(propFile.newWriter(), null)
            }
        }

        project.task("bumpMinor") {
            group = "versioning"
            doLast {
                Properties props = new Properties()
                File propFile = new File('build.properties')
                propFile.withInputStream {
                    props.load(it)
                }
                def vers = props.version
                def list = vers.tokenize('.')
                def newMinor = list[1].toInteger() + 1

                props.setProperty('version', (list[0]+'.'+newMinor+'.'+'0').toString())
                props.store(propFile.newWriter(), null)
            }
        }

        project.task("bumpMajor") {
            group = "versioning"
            doLast {
                Properties props = new Properties()
                File propFile = new File('build.properties')
                propFile.withInputStream {
                    props.load(it)
                }
                def vers = props.version
                def list = vers.tokenize('.')
                def newMajor = list[0].toInteger() + 1

                props.setProperty('version', (newMajor+'.'+'0'+'.'+'0').toString())
                props.store(propFile.newWriter(), null)
            }
        }

        project.task("incrementVersion") {
            group = "versioning"
            doLast {
                Properties props = new Properties()
                File propFile = new File('build.properties')
                propFile.withInputStream {
                    props.load(it)
                }
                def vers = props.version
                def list = vers.tokenize('.')
                def group = props.group.replace(".","/")
                def url = new URL("${project.ext.properties.repositoryManagerUrl}/${props.publishRepo}/$group/${props.artifact}")
                def html = url.openConnection()
                if(html.getResponseCode().equals(200)){
                    BufferedReader reader = new BufferedReader(new InputStreamReader(html.getInputStream()))
                    def versionList = []
                    def search = list[0]+'.'+list[1]
                    def regx = /$search\.\d{1}/
                    reader.eachLine {
                            def finder = (it.toString() =~ /$regx/)
                            if (finder.size() != 0) {
                                versionList.add((finder.getAt(0)))
                            }
                    }
                    def high
                    if (versionList.size() != 0) {
                        high = versionList.max()
                        println("highest version in manager "+high)
                        def num = high.tokenize('.')
                        high = search + '.' + (num[2].toInteger() + 1)
                    }
                    else{
                        println("no match for current major.minor "+search)
                        high = search+'.0'
                    }
                    props.setProperty('version',high.toString())
                    props.store(propFile.newWriter(), null)
                    println("new version "+props.getProperty('version'))

                }
                else{
                    println("wrong connection")
                }
            }
        }
    }

}
