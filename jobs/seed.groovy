import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml

def fileContent = readFileFromWorkspace('config/repos.yaml')

DumperOptions options = new DumperOptions()
options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK)
def yaml = new Yaml(options)

def config = yaml.load(fileContent)
def repos = config.get('repos')

repos.each { repo ->
  def name = repo.get('name')
  def repoUrl  = repo.get('url')
  def urlParts = repoUrl.split('/')
  def repoOrg = urlParts[3]
  def repoName = urlParts[4]
  
  def folderName = 'Builds'
  folder(folderName) {}

  multibranchPipelineJob("/${folderName}/${name}") {
    displayName "${name}"
    branchSources {
      branchSource {
        source {
          github {
            id "${folderName}/${name}"
            repositoryUrl repoUrl
            repository repoName
            repoOwner repoOrg
            credentialsId "github-bot"
            configuredByUrl true
            traits {
              gitHubBranchDiscovery {
                strategyId(3)
              }
              gitHubPullRequestDiscovery {
                strategyId(2)
              }
              headWildcardFilter {
                includes(repo.getOrDefault('include-branches', 'main PR-*'))
                excludes(repo.getOrDefault('exclude-branches', ''))
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
    orphanedItemStrategy {
      discardOldItems {
        daysToKeep repo.getOrDefault('discard-builds-older-than-days', 7)
        numToKeep repo.getOrDefault('num-builds-to-keep', 10)
      }
    }
  }
}
