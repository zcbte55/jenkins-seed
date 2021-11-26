import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml

def fileContent = readFileFromWorkspace('config/repos.yaml')

DumperOptions options = new DumperOptions()
options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK)
def yaml = new Yaml(options)

def repos = yaml.load(fileContent)
print(repos['repos'])
print(repos['repos'][0])

repos.eachWithIndex{ repo, i -> 
  print(repo)
  print(repo['name'])
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
            }
          }
        }
      }
    }   
  }
}
