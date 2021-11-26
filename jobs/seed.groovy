import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml

def fileContent = readFileFromWorkspace('config/repos.yaml')

DumperOptions options = new DumperOptions()
options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK)
def yaml = new Yaml(options)

def repos = yaml.load(fileContent)

repos.each{ repo -> 
  def repoUrl = "https://github.com/sky-uk/${repo.name}"
  def folderName = 'Builds'
  folder(folderName) {
  }

  multibranchPipelineJob("/${folderName}/${repo.name}") {
    displayName "${repo.name}"
    description "desc"
    branchSources {
      branchSource {
        source {
          github {
            id "${folderName}/${repo.name}"
            repositoryUrl "${repoUrl}"
            repository "${repo.name}"
            repoOwner "sky-uk"
            credentialsId "github-bot"
            configuredByUrl true
            traits {
              gitHubBranchDiscovery {
                strategyId(3)
              }
            }
          }
        }
      }
    }   
  }
}
