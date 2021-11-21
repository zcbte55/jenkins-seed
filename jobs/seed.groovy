def projectName = 'aieng-james-test'
def repoUrl = 'https://github.com/sky-uk/aieng-james-test'
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
