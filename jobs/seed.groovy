import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml

def fileContent = readFileFromWorkspace('config/repos.yaml')

DumperOptions options = new DumperOptions()
options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK)
def yaml = new Yaml(options)

def config = yaml.load(fileContent)
def repos = config.get('repos')

repos.each{ repo -> 
  def name = repo.get('name')
  def repoUrl = "https://github.com/sky-uk/${name}"
  def folderName = 'Builds'
  folder(folderName) {
  }

  multibranchPipelineJob("/${folderName}/${name}") {
    displayName "${name}"
    description "desc"
    branchSources {
      branchSource {
        source {
          github {
            id "${folderName}/${name}"
            repositoryUrl "${repoUrl}"
            repository "${name}"
            repoOwner "sky-uk"
            credentialsId "github-bot"
            configuredByUrl true
            traits {
              gitHubBranchDiscovery {
                strategyId(3)
              }
              gitHubPullRequestDiscovery {
                strategyId(1)
              }
            }
          }
        }
        strategy {
            defaultBranchPropertyStrategy {
                props {
                    noTriggerBranchProperty()
                }
            }
        }
      }
    }   
  }
}
