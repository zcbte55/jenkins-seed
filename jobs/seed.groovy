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
  def repoUrl = repo.get('url')
  def scriptFilePath = repo.get('script-path', 'Jenkinsfile')
  def includeBranches = repo.getOrDefault('include-branches', 'main PR-*')
  def excludeBranches = repo.getOrDefault('exclude-branches', '')
  def buildsDaysToKeep = repo.getOrDefault('days-to-keep', 7)
  def buildsNumToKeep = repo.getOrDefault('num-builds-to-keep', 10)
  def includePullRequests = repo.getOrDefault('include-pull-requests', true)
  
  def urlParts = repoUrl.split('/')
  def repoOrg = urlParts[3]
  def repoName = urlParts[4]
  
  def folderName = 'Builds'
  folder(folderName) {}

  // https://jenkinsci.github.io/job-dsl-plugin/#path/multibranchPipelineJob
  multibranchPipelineJob("/${folderName}/${name}") {
    displayName name
    factory {
      workflowBranchProjectFactory {
        scriptPath(scriptFilePath)
      }
    }
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
              if (includePullRequests) {
                gitHubPullRequestDiscovery {
                  strategyId(2)
                }
              }
              headWildcardFilter {
                includes(includeBranches)
                excludes(excludeBranches)
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
        daysToKeep buildsDaysToKeep
        numToKeep buildsNumToKeep
      }
    }
    properties {
      customIcon('job.png')
    }
  }
}
