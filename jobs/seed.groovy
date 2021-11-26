def repos = [
  [
    'name': 'aieng-james-test'
  ]
]
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
