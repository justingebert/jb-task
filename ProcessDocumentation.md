# Process Documentation

Task: Prepare a Grafana dashboard for the cpu_usage metric

I will try to complete expected Result 1 and then move on to 4.

- 1 A JSON file with the Grafana dashboard created from grafana UI
- 4 A repository with an observability-as-code project (optionally using Grafana SDKs, pipelines, and documentation)

I will start with 1.

## Notes for 1

### Setup
- start by cloning the given repo 
- at first glance of the Readme, I heard of Grafana and Prometheus but haven't heard of Loki, need to look up what it does
- docker compose doesn't run right away, it wants a smtp.env, so I will create that based on the given example
- on second thought I probably won't need the SMTP service for this task → maybe remove it later
- local address works fine, explore grafana and prometheus
- grafana explore and prometheus are not returning anything like cpu_metrics, so I might have to generate it, so I will keep following the setup Readme
- need to install k6 first -> https://grafana.com/docs/k6/latest/set-up/install-k6/ -> this needs to be in the prequisites
- cpu_usage now shows up in the grafana explore tab, and in Prometheus, it's stable at 95
- now I will try to create a dashboard using the Grafana UI 

### Dashboard Setup in Grafana UI
- for cpu Usage it would make sense to plot a Graph to see current and maybe an average curve as well
- this might be a bit tricky to verify since cpu_usage is constant? is that correct? idk
  - when looking at the cpu_usage.js, it should generate random data between 80 and 100 
- start a dashboard and add cpu usage -> time series seems ok for this use case
- I see that cpu_usage comes with an instance: server1 so this could be used to filter for different instances of a service
- setting up indicators for bad metrics would be good -> threshold should be alright
- data doesn't seem right it generates straight lines, I will increase the iterations -> better now
- Unit to % and min to 0 and max to 100 so it's better to read than autoscaling
- do some styling -> title, graph, etc
- I also want to create a cpu_usage trend graph, this usually lives in one for better understanding I will do this in a separate panel
- I want to create a dropdown to select the instance this makes it more transparent -> add a variable to select instance label
- added a second instance to the k6 cpu_usage script to test my setup and the variable
- when a new server is added, the viral does not update automatically -> settings say it will update when dashboard is loaded _>
- dashboard looks good now with average over time for trend and current usage, now I want more readable legend names
- running average with a viewed interval might be confusing, but it is good to see trends over different times quickly
- panel will repeat for all selected isntaces up to 4 times, the way this is configured should be defined by the number of isntances if its a large nubmer a diffren approach would be better
- changed instance variable to refresh when the range is changed so new instances are picked up fast
- commit the exported json to the repo as the first version
- this can be imported in the grafana UI or placed in a grafana folder which mounts into the docker container

## Notes for 4

### observability as code approach

#### thoughts:

- a pipeline could mean running tests, exports, validation and deploying dashboard to grafana
    - but there is only local grafan give so push/deplopy where?
    - this could be done locally with a makefile or using gh/gl actions/runners CICD
- should I include the whole demo repo in my repo? I fell liek this is not very clean but lets start with it

### process:

- set up java module with java 21, need to find out if the sdk is made for 21
- also sdk should work with a Grafana version from the demo repo
- it seems like the Grafana foundation sdk is not published yet, so I might have to build it locally
    - documentation only uses go and TypeScript but from public registries
    - I found a GitHub repo using the foundation it uses the version: next-1759917807
    - I will use this for now and will come back to the version later
    - I looked online and found the grafana foundation sdk in the maven central repository with version
      tags: https://central.sonatype.com/artifact/com.grafana/grafana-foundation-sdk
- now I can start with the sdk for that I will scim through the documentation
- create a simple dashboard with cpu_usage metric using the sdk.
- I will keep it simple for now and focus on integration now

#### pipeline setup

- i will create a GitHub action to test and run this program to generate a json, this would then be pushed to prod
  grafana service
    - since it's only running locally, I will comment out the part where it gets deployed using grafanactl
- build and test, then generate a dashboard and deploy to grafana

### add demo repo to this one:

- add the demo repo as a git submodule to keep things clean
- although i also thought about adding it manually and removing everything that is not needed for this usecase
- I will keep it as a submodule for now but this means the setup with the smpt needs to be done for running it

### final touches:

- added comprehensive README with setup instructions, troubleshooting, and project structure
- added unit tests to verify generated dashboard JSON
- verified both dashboards import successfully into Grafana

## submission checklist:

- [x] JSON dashboard file (two versions: handcrafted + generated)
- [x] README with description and usage instructions
- [x] Public Git repository with demo-stand as submodule
- [x] Observability-as-code project using Grafana Foundation SDK (Java)
- [x] CI pipeline with build, test, deploy
- [x] Unit tests for dashboard generation
- [x] Complete documentation
