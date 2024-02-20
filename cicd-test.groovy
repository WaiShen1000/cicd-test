pipeline {
    // Define Agents & Pod Template
    agent {
        kubernetes {
            cloud 'kubernetes'
            inheritFrom 'default image-builder golang ci-test'
        }
    }

    // Define Parameters Input
    parameters {
        string(name: 'tag', defaultValue: '', description: 'Checkout Tag - Required', trim: true)
        hidden(name: 'commit', defaultValue: '', description: 'Hidden parameters')
        hidden(name: 'object_kind', defaultValue: 'manual_trigger', description: 'Hidden parameters')
        booleanParam(name: 'Skip_TestStage', defaultValue: true, description: '')
        hidden(name: 'checkout_sha', defaultValue: '', description: 'Hidden parameters')
    }

    // Define Options Setting
    options {
        ansiColor('xterm')
        skipDefaultCheckout(true)
    }

    // Define Environment Variables
    environment {
        SLACK_CHANNEL = "self-testing"
        PROJECT_REPO = "git@github.com:varadise-ltd/asset-monitoring-service.git"
        PROJECT_SSH_KEY = "GITHUB-SSH-KEY"

        ECR_REPO_URL = "510775875854.dkr.ecr.ap-southeast-1.amazonaws.com/asset-monitoring-service"

        GITLAB_TOKEN = credentials("GITLAB-API-Token-String")
        HELM_CHART_REPO_BRANCH = "dev"
        HELM_CHART_REPO = "git@gitlab.com:varadise-ltd/devops/helm-chart.git"
        HELM_CHART_PATH = "varadise/service/product/asset-monitoring-service/values.yaml"

        AWS_REGION = "ap-southeast-1"

        LINTER = ".golangci.toml" // .eslintrc.js/.pylintrc/.golangci.toml
    }

    // Define Trigger Parameters
    triggers {
        GenericTrigger(
            genericVariables:[
                    [key: 'project', value:"\$.repository.name", defaultValue:''],
                    [key: 'tag', value:"\$.ref", defaultValue:''],
                    [key: 'object_kind', value: '\$.ref_type', defaultValue:'']
                ],
                causeString: 'Triggered By Gitlab On $tag',
                genericRequestVariables: [],
                genericHeaderVariables: [],
                token: "abcdfed",
                tokenCredentialId: '',
                printContributedVariables: false,
                printPostContent: false,
                silentResponse: false,
                shouldNotFlattern: false,
                regexpFilterText: '$object_kind $tag',
                regexpFilterExpression: '(^tag\\s\\d+\\.\\d+\\.\\d+$)'
        )
    }
    
    stages {
        stage('Hello') {
            steps {
                echo 'Hello World'
            }
        }
    }
}
