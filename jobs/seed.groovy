def projectName = 'aieng-conviva'
def repoUrl = "https://github.com/sky-uk/${projectName}"
def folderName = 'Builds'
folder(folderName) {
}

multibranchPipelineJob("/${folderName}/${projectName}") {
  displayName "${projectName}"
  description "desc"
  branchSources {
    branchSource {
      source {
        github {
          id "${folderName}/${projectName}"
          repositoryUrl "${repoUrl}"
          repository "${projectName}"
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
